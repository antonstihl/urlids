package dev.antonstihl.urlids;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
public class AccountController {

    private final AccountRepository accountRepository;

    private static final UUID PRINCIPAL_CUSTOMER_ID = UUID.fromString("8cdac769-997b-49d7-bfe2-b42c68918d06");

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/accounts")
    public List<PublicAccount> getAccounts() {
        return accountRepository.getAccounts().stream()
                .map(a -> AccountConverter.convert(a, PRINCIPAL_CUSTOMER_ID))
                .toList();
    }

    @GetMapping("/accounts/{id}")
    public PublicAccount getAccount(@PathVariable UUID id) {
        UUID accountId = IdMasker.decrypt(id, PRINCIPAL_CUSTOMER_ID);
        return accountRepository.getAccount(accountId)
                .map(a -> AccountConverter.convert(a, PRINCIPAL_CUSTOMER_ID))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }


}
