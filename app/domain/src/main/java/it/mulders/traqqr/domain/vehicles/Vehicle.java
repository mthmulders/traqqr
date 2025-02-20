package it.mulders.traqqr.domain.vehicles;

import java.math.BigDecimal;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Vehicle(
        String code,
        String description,
        String ownerId,
        Collection<Authorisation> authorisations,
        BigDecimal netBatteryCapacity) {
    private static final Logger log = LoggerFactory.getLogger(Vehicle.class);

    public Authorisation regenerateKey() {
        log.info("Regenerating API key; vehicle={}", code);

        // Invalidate all previous API keys, if they weren't invalidated yet.
        authorisations.stream().filter(Authorisation::isValid).forEach(Authorisation::invalidate);

        var authorisation = Authorisation.generate();
        authorisations.add(authorisation);
        return authorisation;
    }

    public boolean hasAuthorisationWithKey(String rawKey) {
        var hashedKey = Authorisation.fromInput(rawKey).getHashedKey();

        return authorisations != null
                && authorisations.stream()
                        .anyMatch(existing -> existing.getHashedKey().equals(hashedKey));
    }
}
