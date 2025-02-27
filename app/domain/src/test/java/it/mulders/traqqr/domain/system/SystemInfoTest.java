package it.mulders.traqqr.domain.system;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SystemInfoTest implements WithAssertions {
    private final SystemInfo systemInfo = new SystemInfo();

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
    void should_fail_to_load_invalid_property_resource() {
        assertThatThrownBy(() -> new SystemInfo(new String[]{"/non-existing.properties"})).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SystemInfo(new String[]{null})).isInstanceOf(IllegalArgumentException.class);
    }
}