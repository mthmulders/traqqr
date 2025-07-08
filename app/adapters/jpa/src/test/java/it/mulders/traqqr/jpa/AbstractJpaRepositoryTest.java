package it.mulders.traqqr.jpa;

import it.mulders.traqqr.jpa.batch.BatchJobItemMapper;
import it.mulders.traqqr.jpa.measurements.MeasurementMapper;
import it.mulders.traqqr.jpa.vehicles.VehicleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.RollbackException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.assertj.core.api.WithAssertions;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.Location;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @param <Int> repository type
 * @param <Impl> JPA-based repository type
 */
@Testcontainers
public abstract class AbstractJpaRepositoryTest<Int, Impl extends Int> implements WithAssertions {
    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:16.6-alpine");

    protected final BatchJobItemMapper batchJobItemMapper = MapStructHelper.getMapper(BatchJobItemMapper.class);
    protected final MeasurementMapper measurementMapper = MapStructHelper.getMapper(MeasurementMapper.class);
    protected final VehicleMapper vehicleMapper = MapStructHelper.getMapper(VehicleMapper.class);

    protected EntityManager entityManager;
    protected Int repository;

    protected void prepare(Function<EntityManager, Impl> entityManagerCreator) {
        var username = POSTGRESQL_CONTAINER.getUsername();
        var url = POSTGRESQL_CONTAINER.getJdbcUrl();
        var driver = POSTGRESQL_CONTAINER.getDriverClassName();
        var password = POSTGRESQL_CONTAINER.getPassword();

        var props = Map.of(
                "jakarta.persistence.jdbc.user", username,
                "jakarta.persistence.jdbc.url", url,
                "jakarta.persistence.jdbc.driver", driver,
                "jakarta.persistence.jdbc.password", password);

        var flyway = Flyway.configure()
                .locations(
                        new Location("filesystem:src/main/resources/db/migration"),
                        new Location("filesystem:src/main/resources/db/migration-postgresql"))
                .dataSource(url, username, password)
                .load();
        try {
            flyway.migrate();
        } catch (final FlywayException fe) {
            fail("Failed to migrate database schema", fe);
        }

        var emf = Persistence.createEntityManagerFactory("test", props);
        this.entityManager = emf.createEntityManager(props);
        this.repository = entityManagerCreator.apply(this.entityManager);
    }

    protected <T> T persist(T entity) {
        return runTransactional(() -> {
            entityManager.persist(entity);
            return entity;
        });
    }

    protected void runTransactional(Runnable action) {
        runTransactional(runnableToVoidSupplier(action));
    }

    private Supplier<Void> runnableToVoidSupplier(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    protected <T> T runTransactional(Supplier<T> supplier) {
        var transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            var result = supplier.get();
            transaction.commit();
            return result;
        } catch (RollbackException re) {
            var cause = re.getCause();
            fail("Could not run transactional action", cause);
            return (T) null;
        } catch (Throwable t) {
            transaction.rollback();
            fail("Could not run transactional action", t);
            return (T) null;
        }
    }
}
