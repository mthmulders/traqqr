package it.mulders.traqqr.jpa.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.jpa.AbstractJpaRepositoryTest;
import it.mulders.traqqr.jpa.MapStructHelper;
import jakarta.persistence.RollbackException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import org.assertj.core.api.WithAssertions;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JpaVehicleRepositoryIT extends AbstractJpaRepositoryTest<VehicleRepository, JpaVehicleRepository>
        implements WithAssertions {
    private static final String FAKE_OWNER_ID = "its-a-me";

    private final VehicleMapper vehicleMapper = MapStructHelper.getMapper(VehicleMapper.class);

    @BeforeEach
    void prepare() {
        prepare(em -> new JpaVehicleRepository(em, vehicleMapper));
    }

    @Test
    void should_find_vehicles_by_owner() {
        var myOwnerId = "my-owner-id";
        var vehicle1 = new Vehicle(
                "000003",
                "should_find_vehicles_by_owner_1",
                myOwnerId,
                Collections.emptySet(),
                BigDecimal.valueOf(50.0));
        var vehicle2 = new Vehicle(
                "000004",
                "should_find_vehicles_by_owner_2",
                myOwnerId,
                Collections.emptySet(),
                BigDecimal.valueOf(50.0));
        var vehicle3 = new Vehicle(
                "000005",
                "should_find_vehicles_by_owner_3",
                "somebody-else",
                Collections.emptySet(),
                BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle1));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle2));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle3));

        var result = repository.findByOwner(createOwner(myOwnerId));

        assertThat(result).isNotEmpty().hasSize(2).allSatisfy(found -> {
            assertThat(found.ownerId()).isEqualTo(myOwnerId);
        });
    }

    @Test
    void should_find_vehicle_by_code() {
        var vehicle = new Vehicle(
                "000001",
                "should_find_vehicle_by_code",
                FAKE_OWNER_ID,
                Collections.emptySet(),
                BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));

        var result = repository.findByCode(vehicle.code());

        assertThat(result).isPresent().hasValueSatisfying(found -> {
            assertThat(found.code()).isEqualTo(vehicle.code());
            assertThat(found.description()).isEqualTo(vehicle.description());
            assertThat(found.netBatteryCapacity()).isEqualTo(vehicle.netBatteryCapacity());
            assertThat(found.ownerId()).isEqualTo(vehicle.ownerId());
        });
    }

    @Test
    void should_remove_vehicle() {
        var vehicle = new Vehicle(
                "000002", "should_remove_vehicle", FAKE_OWNER_ID, Collections.emptySet(), BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));

        runTransactional(() -> repository.removeVehicle(vehicle));

        assertThat(repository.findByCode(vehicle.code())).isEmpty();
    }

    @Test
    void should_save_vehicle() {
        var vehicle = new Vehicle(
                "000006", "should_remove_vehicle", FAKE_OWNER_ID, new ArrayList<>(), BigDecimal.valueOf(50.0));

        runTransactional(() -> repository.save(vehicle));

        assertThat(repository.findByCode(vehicle.code())).hasValue(vehicle);
    }

    @Test
    void should_reject_vehicle_with_duplicate_code() {
        var vehicle1 = new Vehicle(
                "000008", "should_remove_vehicle", FAKE_OWNER_ID, new ArrayList<>(), BigDecimal.valueOf(50.0));
        var vehicle2 = new Vehicle(
                "000008", "should_remove_vehicle", FAKE_OWNER_ID, new ArrayList<>(), BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle1));

        assertThatThrownBy(() -> {
                    var transaction = entityManager.getTransaction();
                    transaction.begin();
                    repository.save(vehicle2);
                    transaction.commit();
                })
                .isInstanceOf(RollbackException.class)
                .hasCauseInstanceOf(DatabaseException.class)
                .hasMessageContaining("duplicate key")
                .hasMessageContaining("vehicle_owner_code");
    }

    @Test
    void should_update_vehicle() {
        var original = new Vehicle(
                "000009", "should_update_vehicle", FAKE_OWNER_ID, new ArrayList<>(), BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(original));
        var updated = new Vehicle(
                "000009", "should_update_vehicle_v2", FAKE_OWNER_ID, new ArrayList<>(), BigDecimal.valueOf(50.0));

        runTransactional(() -> repository.update(updated));

        assertThat(repository.findByCode(original.code())).hasValue(updated);
    }

    @Test
    void should_update_vehicle_with_authorisation() {
        var vehicle = new Vehicle(
                "000010",
                "should_update_vehicle_with_authorisation",
                FAKE_OWNER_ID,
                new ArrayList<>(),
                BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));

        var authorisation = vehicle.regenerateKey();

        runTransactional(() -> repository.update(vehicle));

        assertThat(repository.findByCode(vehicle.code()).map(Vehicle::authorisations))
                .isPresent()
                .hasValueSatisfying(authorisations -> {
                    assertThat(authorisations).hasSize(1).containsOnly(authorisation);
                });
    }

    @Test
    void should_update_vehicle_with_new_and_updated_authorisation() {
        var vehicle = new Vehicle(
                "000011",
                "should_update_vehicle_with_new_and_updated_authorisation",
                FAKE_OWNER_ID,
                new ArrayList<>(),
                BigDecimal.valueOf(50.0));
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));

        var authorisation1 = vehicle.regenerateKey();

        runTransactional(() -> repository.update(vehicle));

        var authorisation2 = vehicle.regenerateKey();
        runTransactional(() -> repository.update(vehicle));

        assertThat(repository.findByCode(vehicle.code()).map(Vehicle::authorisations))
                .isPresent()
                .hasValueSatisfying(authorisations -> {
                    assertThat(authorisations).hasSize(2);

                    assertThat(authorisations).anySatisfy(authorisation -> {
                        assertThat(authorisation.getHashedKey()).isEqualTo(authorisation1.getHashedKey());
                        assertThat(authorisation.getInvalidatedAt()).isNotNull();
                    });

                    assertThat(authorisations).anySatisfy(authorisation -> {
                        assertThat(authorisation.getHashedKey()).isEqualTo(authorisation2.getHashedKey());
                        assertThat(authorisation.getInvalidatedAt()).isNull();
                    });
                });
    }

    private Owner createOwner(String ownerId) {
        return new Owner() {

            @Override
            public String code() {
                return ownerId;
            }

            @Override
            public String displayName() {
                return "Fake Owner";
            }

            @Override
            public String profilePictureUrl() {
                return "n/a";
            }
        };
    }
}
