package dev.antonstihl.urlids;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;
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
     * Deterministically encrypt a UUID into a Base64 string with a provided key string
     */
    public static String encryptToString(UUID input, UUID principalCustomerId) {
        try {
            byte[] keyBytes = deriveKey(principalCustomerId.toString());
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));
            byte[] encrypted = cipher.doFinal(uuidToBytes(input));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deterministically decrypt a Base64 string back to original UUID with a provided key string
     */
    public static UUID decryptFromString(String masked, UUID principalCustomerId) {
        try {
            byte[] keyBytes = deriveKey(principalCustomerId.toString());
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"));
            byte[] encrypted = Base64.getUrlDecoder().decode(masked);
            byte[] decrypted = cipher.doFinal(encrypted);
            return bytesToUuid(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
