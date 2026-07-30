package it.mulders.traqqr.jpa.vehicles;

import static it.mulders.traqqr.domain.fakes.OwnerFaker.createOwner;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.vehicles.Authorisation;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import it.mulders.traqqr.jpa.AbstractJpaRepositoryTest;
import jakarta.persistence.RollbackException;
import java.util.function.Function;
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

    @BeforeEach
    void prepare() {
        prepare(em -> new JpaVehicleRepository(em, vehicleMapper));
    }

    @Test
    void should_find_vehicles_by_owner() {
        var myOwnerId = "my-owner-id";
        persist(vehicleMapper.vehicleToVehicleEntity(createVehicle("000003", myOwnerId)));
        persist(vehicleMapper.vehicleToVehicleEntity(createVehicle("000004", myOwnerId)));
        persist(vehicleMapper.vehicleToVehicleEntity(createVehicle("000005", "somebody-else")));

        var result = repository.findByOwner(createOwner(myOwnerId));

        assertThat(result).isNotEmpty().hasSize(2).allSatisfy(found -> {
            assertThat(found.ownerId()).isEqualTo(myOwnerId);
        });
    }

    @Test
    void should_find_vehicle_by_code() {
        var vehicle = createVehicle();
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
        var vehicle = createVehicle();
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));

        runTransactional(() -> repository.removeVehicle(vehicle));

        assertThat(repository.findByCode(vehicle.code())).isEmpty();
    }

    @Test
    void should_save_vehicle() {
        var vehicle = createVehicle();

        runTransactional(() -> repository.save(vehicle));

        var result = entityManager
                .createQuery("select v from Vehicle v where v.code = :code", VehicleEntity.class)
                .setParameter("code", vehicle.code())
                .getSingleResult();
        assertThat(vehicleMapper.vehicleEntityToVehicle(result)).isEqualTo(vehicle);
    }

    @Test
    void should_reject_vehicle_with_duplicate_code() {
        var vehicle1 = createVehicle("000008");
        var vehicle2 = createVehicle("000008");
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
        var original = createVehicle();
        persist(vehicleMapper.vehicleToVehicleEntity(original));
        var updated = createVehicle(
                original.code(), original.ownerId(), "Updated Vehicle Description", original.netBatteryCapacity());

        runTransactional(() -> repository.update(updated));

        assertThat(repository.findByCode(original.code())).hasValue(updated);
    }

    @Test
    void should_update_vehicle_with_one_authorisation() {
        var vehicle = createVehicle();
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
    void should_update_vehicle_with_new_and_one_existing_authorisation() {
        var vehicle = createVehicle();
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

    @Test
    void update_vehicle_should_not_create_superfluous_authorisations() {
        var vehicle = createVehicle();
        persist(vehicleMapper.vehicleToVehicleEntity(vehicle));

        var regenerateKey = (Function<Vehicle, Authorisation>) (Vehicle v) -> {
            var a = v.regenerateKey();
            repository.update(v);
            return a;
        };

        var authorisation1 = runTransactional(
                () -> repository.findByCode(vehicle.code()).map(regenerateKey).get());

        var authorisation2 = runTransactional(
                () -> repository.findByCode(vehicle.code()).map(regenerateKey).get());

        var authorisation3 = runTransactional(
                () -> repository.findByCode(vehicle.code()).map(regenerateKey).get());

        var rawRows = this.entityManager
                .createNativeQuery("""
                        select hashed_key
                        from authorisation
                        where vehicle_id in (select id from vehicle where code = ?)
                        ;
                        """)
                .setParameter(1, vehicle.code())
                .getResultList();

        assertThat(rawRows)
                .hasSize(3)
                .containsOnly(
                        authorisation1.getHashedKey(), authorisation2.getHashedKey(), authorisation3.getHashedKey());
    }
}
