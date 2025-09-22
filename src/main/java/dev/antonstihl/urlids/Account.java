package dev.antonstihl.urlids;

import java.util.UUID;

public record Account(
        UUID id,
        String accountType
) {
}
