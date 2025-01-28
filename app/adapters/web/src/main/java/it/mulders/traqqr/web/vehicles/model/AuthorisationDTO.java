package it.mulders.traqqr.web.vehicles.model;

import java.util.Objects;

public final class AuthorisationDTO {
    private final String hashedKey;
    private final String rawKey;

    public AuthorisationDTO(String hashedKey, String rawKey) {
        this.hashedKey = hashedKey;
        this.rawKey = rawKey;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public String getRawKey() {
        return rawKey;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthorisationDTO that)) return false;
        return Objects.equals(hashedKey, that.hashedKey) && Objects.equals(rawKey, that.rawKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedKey, rawKey);
    }
}
