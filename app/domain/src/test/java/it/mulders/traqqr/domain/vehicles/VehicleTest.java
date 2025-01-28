package it.mulders.traqqr.domain.vehicles;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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
        var vehicle = new Vehicle("000001", "Vehicle 1", FAKE_OWNER_ID, authorisations, BigDecimal.valueOf(50.0));

        vehicle.regenerateKey();

        assertThat(existingAuthorisation.isValid()).isFalse();
        assertThat(existingAuthorisation.getInvalidatedAt()).isInThePast();
    }

    @Test
    void regenerate_should_add_new_authorisation() {
        var existingAuthorisation = Authorisation.generate();
        var authorisations = new HashSet<>(Set.of(existingAuthorisation));
        var vehicle = new Vehicle("000002", "Vehicle 2", FAKE_OWNER_ID, authorisations, BigDecimal.valueOf(50.0));

        vehicle.regenerateKey();

        var currentAuthorisations = new HashSet<>(vehicle.authorisations());
        assertThat(currentAuthorisations.size()).isEqualTo(2);

        // find the newly added authorisation by removing the one that was there before regeneration
        currentAuthorisations.remove(existingAuthorisation);

        assertThat(currentAuthorisations.size()).isEqualTo(1);
        assertThat(currentAuthorisations).allMatch(Authorisation::isValid);
    }

    @Test
    void should_be_able_to_verify_authorisation() {
        var existingAuthorisation = Authorisation.generate();
        var authorisations = new HashSet<>(Set.of(existingAuthorisation));
        var vehicle = new Vehicle("000003", "Vehicle 3", FAKE_OWNER_ID, authorisations, BigDecimal.valueOf(50.0));

        assertThat(vehicle.hasAuthorisationWithKey(existingAuthorisation.getRawKey()))
                .isTrue();
    }

    @Test
    void should_be_able_to_falsify_authorisation() {
        var existingAuthorisation = Authorisation.generate();
        var authorisations = new HashSet<>(Set.of(existingAuthorisation));
        var vehicle = new Vehicle("000004", "Vehicle 4", FAKE_OWNER_ID, authorisations, BigDecimal.valueOf(50.0));

        assertThat(vehicle.hasAuthorisationWithKey("whatever")).isFalse();
    }

    @Test
    void should_store_net_battery_capacity() {
        var vehicle = new Vehicle("000005", "Vehicle 5", FAKE_OWNER_ID, new HashSet<>(), BigDecimal.valueOf(75.5));

        assertThat(vehicle.netBatteryCapacity()).isEqualTo(BigDecimal.valueOf(75.5));
    }

    @Test
    void should_honor_equals_contract() {
        EqualsVerifier.forClass(Vehicle.class)
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}
