package dev.antonstihl.urlids;

import java.util.UUID;

public record PublicAccount(
        UUID publicId,
        String accountType
) {
}
