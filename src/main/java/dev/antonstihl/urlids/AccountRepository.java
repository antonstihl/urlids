package dev.antonstihl.urlids;

import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepository {
    private static final UUID UUID1 = UUID.fromString("097c174b-414f-463e-b07e-e2efab3d2b09"); // public
    private static final UUID UUID2 = UUID.fromString("df67f3ac-052a-49b1-8569-4808f7808f3c");
    private static final UUID UUID3 = UUID.fromString("a4f857bf-172a-41b4-b7ea-60f3ac2932aa");

    public static final Map<UUID, Account> ACCOUNTS = new LinkedHashMap<>();

    public AccountRepository() {
        ACCOUNTS.put(UUID1, new Account(UUID1, "AF"));
        ACCOUNTS.put(UUID2, new Account(UUID2, "KF"));
        ACCOUNTS.put(UUID3, new Account(UUID3, "ISK"));
    }

    public Optional<Account> getAccount(UUID id) {
        return Optional.ofNullable(ACCOUNTS.get(id));
    }

    public List<Account> getAccounts() {
        return ACCOUNTS.values().stream().toList();
    }
}
