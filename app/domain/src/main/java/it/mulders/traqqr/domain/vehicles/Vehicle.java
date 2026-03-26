package it.mulders.traqqr.domain.vehicles;

import static it.mulders.traqqr.domain.shared.Mutations.overwrite;

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

    public Vehicle updateWith(Vehicle updated) {
        // Never copy the authorisations, as they are managed separately
        // through regenerateKey.
        return new Vehicle(
                overwrite(code).with(updated.code),
                overwrite(description).with(updated.description),
                overwrite(ownerId).with(updated.ownerId),
                authorisations,
                overwrite(netBatteryCapacity).with(updated.netBatteryCapacity));
    }
}
