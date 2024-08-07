package it.mulders.traqqr.db;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SchemaUpdater {
    private static final Logger logger = LoggerFactory.getLogger(SchemaUpdater.class.getName());

    @Resource(name = "jdbc/traqqr-ds")
    private DataSource dataSource;

    @SuppressWarnings("java:S1172") // "Unused method parameters should be removed"
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        logger.info("Starting Flyway");
        final Flyway flyway = initFlyway().dataSource(dataSource).load();
        try {
            logger.info("Migrating database schema");
            var result = flyway.migrate();
            logger.info("Database schema migration done; migrations_executed={}", result.migrationsExecuted);
        } catch (final FlywayException fe) {
            logger.error("Failed to migrate database schema", fe);
        }
    }

    protected FluentConfiguration initFlyway() {
        return Flyway.configure();
    }
}
