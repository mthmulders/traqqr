package it.mulders.traqqr.batch.scheduling;

import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.inject.spi.DeploymentException;
import org.assertj.core.api.WithAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SchedulingExtensionIT implements WithAssertions {

    @Test
    void should_execute_scheduled_method_invocation() {
        // Arrange
        try (var container = SeContainerInitializer.newInstance()
                .disableDiscovery()
                .addExtensions(SchedulingExtension.class)
                .addBeanClasses(DummyBean.class, ClockProducer.class, TestScheduler.class)
                .initialize()) {
            // Act
            var dummyBean = CDI.current().select(DummyBean.class).get();

            // Assert
            Awaitility.await()
                    .pollInterval(Duration.ofSeconds(15))
                    .atMost(2, TimeUnit.MINUTES)
                    .untilAsserted(() -> {
                        assertThat(dummyBean.getInvocationCount()).isEqualTo(1);
                    });
        }
    }

    @Test
    void should_fail_when_no_scheduler_found() {
        assertThatThrownBy(() -> {
            try (var container = SeContainerInitializer.newInstance()
                    .disableDiscovery()
                    .addExtensions(SchedulingExtension.class)
                    .addBeanClasses(DummyBean.class, ClockProducer.class)
                    .initialize()) {
            }
        }).isInstanceOf(DeploymentException.class);
    }
}
