package dev.antonstihl.urlids;

import java.util.UUID;

public abstract class AccountConverter {
    public static PublicAccount convert(Account account, UUID principalCustomerId) {
        try {
            return new PublicAccount(IdMasker.encryptToString(account.id(), principalCustomerId), account.accountType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
