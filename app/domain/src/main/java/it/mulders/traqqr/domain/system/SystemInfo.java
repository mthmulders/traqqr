package it.mulders.traqqr.domain.system;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Named("systemInfo")
public class SystemInfo {
    private static final Logger log = LoggerFactory.getLogger(SystemInfo.class);

    private final Properties systemProperties = System.getProperties();
    private final Properties metadataProperties = new Properties();

    public SystemInfo() {
        this("/git.properties", "/application.properties");
    }

    protected SystemInfo(final String... classpathResourceNames) {
        Arrays.stream(classpathResourceNames).forEach(this::loadMetadataProperties);
    }

    public String getJavaVersion() {
        return (String) systemProperties.get("java.specification.version");
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
