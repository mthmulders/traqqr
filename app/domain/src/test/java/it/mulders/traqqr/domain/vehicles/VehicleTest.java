package it.mulders.traqqr.domain.vehicles;

import java.util.HashSet;
import java.util.Set;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleTest implements WithAssertions {
    private static final String FAKE_OWNER_ID = "its-a-me";

    @Test
    void regenerate_should_invalidate_previous_authorisations() {
        var existingAuthorisation = Authorisation.generate();
        var authorisations = new HashSet<>(Set.of(existingAuthorisation));
        var vehicle = new Vehicle("000001", "Vehicle 1", FAKE_OWNER_ID, authorisations);

        vehicle.regenerateKey();

        assertThat(existingAuthorisation.isValid()).isFalse();
        assertThat(existingAuthorisation.getInvalidatedAt()).isInThePast();
    }

    @Test
    void regenerate_should_add_new_authorisation() {
        var existingAuthorisation = Authorisation.generate();
        var authorisations = new HashSet<>(Set.of(existingAuthorisation));
        var vehicle = new Vehicle("000002", "Vehicle 2", FAKE_OWNER_ID, authorisations);

        vehicle.regenerateKey();

        var currentAuthorisations = new HashSet<>(vehicle.authorisations());
        assertThat(currentAuthorisations.size()).isEqualTo(2);

        // find the newly added authorisation by removing the one that was there before regeneration
        currentAuthorisations.remove(existingAuthorisation);

        assertThat(currentAuthorisations.size()).isEqualTo(1);
        assertThat(currentAuthorisations).allMatch(Authorisation::isValid);
    }

    @Test
    void should_expose_raw_key_after_creation() {
        var authorisation = Authorisation.generate();
        assertThat(authorisation.getRawKey()).isNotNull();
    }

    @Test
    void should_allow_verification() {
        var originalAuthorisation = Authorisation.generate();

        var verification = Authorisation.fromInput(originalAuthorisation.getRawKey());

        assertThat(verification.getHashedKey()).isEqualTo(originalAuthorisation.getHashedKey());
    }
}
