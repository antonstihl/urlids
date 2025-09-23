package dev.antonstihl.urlids;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.UUID;

public final class IdMasker {

    /**
     * Convert UUID -> 16-byte array
     */
    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * Convert 16-byte array -> UUID
     */
    private static UUID bytesToUuid(byte[] bytes) {
        if (bytes.length != 16) throw new IllegalArgumentException("UUID must be 16 bytes");
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    /**
     * Derive a 16-byte AES key from a string
     */
    private static byte[] deriveKey(String keyStr) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(keyStr.getBytes());
        byte[] key = new byte[16]; // AES-128
        System.arraycopy(hash, 0, key, 0, 16);
        return key;
    }

    /**
     * Remove dashes from a UUID and make it a continuous string
     */
    private static String uuidToString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    /**
     * Add dashes back to a continuous UUID string
     */
    private static UUID stringToUuid(String str) {
        if (str.length() != 32) throw new IllegalArgumentException("UUID string must be 32 characters");
        String dashed = str.substring(0, 8) + "-" +
                        str.substring(8, 12) + "-" +
                        str.substring(12, 16) + "-" +
                        str.substring(16, 20) + "-" +
                        str.substring(20);
        return UUID.fromString(dashed);
    }

    /**
     * Deterministically encrypt a UUID into another UUID with a provided key string
     */
    public static String encrypt(UUID input, UUID principalCustomerId) {
        try {
            byte[] keyBytes = deriveKey(principalCustomerId.toString());
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));
            byte[] encrypted = cipher.doFinal(uuidToBytes(input));
            return uuidToString(bytesToUuid(encrypted));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deterministically decrypt back to original UUID with a provided key string
     */
    public static UUID decrypt(String masked, UUID principalCustomerId) {
        try {
            byte[] keyBytes = deriveKey(principalCustomerId.toString());
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));
            byte[] decrypted = cipher.doFinal(uuidToBytes(stringToUuid(masked)));
            return bytesToUuid(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
