package it.mulders.traqqr.domain.system;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("systemInfo")
public class SystemInfo {
    private static final Logger log = LoggerFactory.getLogger(SystemInfo.class);

    private final Properties systemProperties = System.getProperties();
    private final Properties metadataProperties = new Properties();

    private final DataSource datasource;

    @Inject
    public SystemInfo(DataSource datasource) {
        this(datasource, "/git.properties", "/application.properties");
    }

    protected SystemInfo(DataSource datasource, String... classpathResourceNames) {
        this.datasource = datasource;
        Arrays.stream(classpathResourceNames).forEach(this::loadMetadataProperties);
    }

    public String getJavaVersion() {
        return systemProperties.getProperty("java.specification.version");
    }

    public String getJavaRuntime() {
        return "%s %s".formatted(systemProperties.get("java.vm.vendor"), systemProperties.get("java.runtime.version"));
    }

    public String getApplicationVersion() {
        return metadataProperties.getProperty("application.version");
    }

    public String getGitVersion() {
        return metadataProperties.getProperty("git.commit.id.abbrev");
    }

    public String getOsInfo() {
        return "%s %s".formatted(systemProperties.get("os.name"), systemProperties.get("os.version"));
    }

    public String getDatabaseInfo() {
        try (var connection = datasource.getConnection()) {
            var metaData = connection.getMetaData();
            return "%s %s".formatted(metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
        } catch (Exception e) {
            log.error("Could not retrieve database metadata", e);
            return "Unknown";
        }
    }

    private void loadMetadataProperties(String classpathResourceName) {
        if (classpathResourceName == null) {
            var msg = "No metadata resource supplied";
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }

        try (var input = getClass().getResourceAsStream(classpathResourceName)) {
            if (input == null) {
                var msg = "Metadata resource %s does not exist".formatted(classpathResourceName);
                log.warn(msg);
                throw new IllegalArgumentException(msg);
            }

            var sizeBefore = metadataProperties.size();
            metadataProperties.load(input);
            var sizeAfter = metadataProperties.size();
            log.info("Metadata loaded; num_entries={}, source={}", sizeAfter - sizeBefore, classpathResourceName);
        } catch (IOException e) {
            var msg = "Could not read metadata from %s".formatted(classpathResourceName);
            log.error(msg, e);
            throw new IllegalArgumentException(msg);
        }
    }
}
