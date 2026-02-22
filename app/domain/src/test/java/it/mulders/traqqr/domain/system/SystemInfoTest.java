package it.mulders.traqqr.domain.system;

import com.mockrunner.mock.jdbc.MockDataSource;
import javax.sql.DataSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SystemInfoTest implements WithAssertions {
    private final DataSource datasource = new MockDataSource();
    private final SystemInfo systemInfo = new SystemInfo(datasource);

    @Test
    void should_expose_application_version() {
        assertThat(systemInfo.getApplicationVersion()).isNotNull().isEqualTo("42");
    }

    @Test
    void should_expose_git_commit_hash() {
        assertThat(systemInfo.getGitVersion()).isNotNull().isEqualTo("cafebabe");
    }

    @Test
    void should_expose_java_runtime() {
        assertThat(systemInfo.getJavaRuntime()).isNotNull().isNotEmpty();
    }

    @Test
    void should_expose_java_version() {
        assertThat(systemInfo.getJavaVersion()).isNotNull().isNotEmpty();
    }

    @Test
    void should_expose_os_info() {
        assertThat(systemInfo.getOsInfo()).isNotNull().isNotEmpty();
    }

    @Test
    void should_expose_database_info() {
        assertThat(systemInfo.getDatabaseInfo()).isNotNull().isNotEmpty();
    }

    @Test
    void should_fail_to_load_invalid_property_resource() {
        assertThatThrownBy(() -> new SystemInfo(datasource, new String[] {"/non-existing.properties"}))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SystemInfo(datasource, new String[] {null}))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
