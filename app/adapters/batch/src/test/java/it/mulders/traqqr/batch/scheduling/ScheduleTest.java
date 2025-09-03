package it.mulders.traqqr.batch.scheduling;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScheduleTest implements WithAssertions {
    @Test
    void should_convert_from_Scheduled_annotation() throws NoSuchMethodException {
        // Arrange
        var scheduled = ScheduleTest.class.getMethod("dummy").getAnnotation(Scheduled.class);

        // Act
        var schedule = Schedule.fromScheduled(scheduled);

        // Assert
        assertThat(schedule.dayOfMonth()).isEqualTo(scheduled.dayOfMonth());
        assertThat(schedule.hour()).isEqualTo(scheduled.hour());
        assertThat(schedule.minute()).isEqualTo(scheduled.minute());
    }

    @Scheduled(dayOfMonth = "1", hour = "2", minute = "3")
    public void dummy() {
    }
}