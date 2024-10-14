package it.mulders.traqqr.web.vehicles.model;

public class AuthorisationDTO {
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
}
