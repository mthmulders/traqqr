package it.mulders.traqqr.domain.vehicles;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authorisation {
    private static final Logger log = LoggerFactory.getLogger(Authorisation.class);

    private final OffsetDateTime generatedAt;
    private OffsetDateTime invalidatedAt;
    private final String hashedKey;
    private final String rawKey;

    public Authorisation(final OffsetDateTime generatedAt, final OffsetDateTime invalidatedAt, final String hashedKey) {
        this.generatedAt = generatedAt;
        this.invalidatedAt = invalidatedAt;
        this.hashedKey = hashedKey;
        this.rawKey = null;
    }

    private Authorisation(final String rawKey, final String hashedKey) {
        this.generatedAt = OffsetDateTime.now();
        this.hashedKey = hashedKey;
        this.rawKey = rawKey;
    }

    public static Authorisation generate() {
        var rawKey = RandomStringUtils.generateRandomIdentifier(30);
        return new Authorisation(rawKey, calculateHash(rawKey));
    }

    public static Authorisation fromInput(final String input) {
        return new Authorisation(input, calculateHash(input));
    }

    private static String calculateHash(final String input) {
        // Use SHA-256 to calculate a hash; no need (yet) to use a salt for this.
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException nsae) {
            log.error("Can't generate hash", nsae);
            throw new RuntimeException("Can't calculate SHA-256 hash", nsae);
        }
    }

    public String getRawKey() {
        return rawKey;
    }

    public String getHashedKey() {
        return hashedKey;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public OffsetDateTime getInvalidatedAt() {
        return invalidatedAt;
    }

    public void invalidate() {
        this.invalidatedAt = OffsetDateTime.now();
    }

    public boolean isValid() {
        return invalidatedAt == null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Authorisation that)) return false;
        return Objects.equals(generatedAt, that.generatedAt) && Objects.equals(hashedKey, that.hashedKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generatedAt, hashedKey);
    }
}
