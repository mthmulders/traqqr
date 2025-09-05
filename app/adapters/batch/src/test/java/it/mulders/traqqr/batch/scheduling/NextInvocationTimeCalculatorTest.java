package it.mulders.traqqr.batch.scheduling;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NextInvocationTimeCalculatorTest implements WithAssertions {
    private final NextInvocationTimeCalculator calculator = new NextInvocationTimeCalculator();

    @DisplayName("Single field schedules")
    @Nested
    class SingleFieldSchedules {
        private final Clock base = Clock.fixed(Instant.parse("2025-08-09T11:39:16+01:00"), ZoneId.of("UTC"));
        private final OffsetDateTime after = OffsetDateTime.now(base);

        @Test
        void should_calculate_next_minute_every_minute() {
            // Arrange
            var schedule = new Schedule("9", "10", "*");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusMinutes(1));
        }

        @Test
        void should_calculate_next_minute_every_five_minutes() {
            // Arrange
            var schedule = new Schedule("9", "10", "*/5");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusMinutes(5));
        }

        @Test
        void should_calculate_next_minute_single_value_this_hour() {
            // Arrange
            var schedule = new Schedule("9", "10", "52");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusMinutes(52 - 39));
        }

        @Test
        void should_calculate_next_minute_single_value_next_hour() {
            // Arrange
            var schedule = new Schedule("9", "*", "22");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusHours(1).withMinute(22));
        }

        @Test
        void should_calculate_next_hour_every_hour() {
            // Arrange
            var schedule = new Schedule("9", "*", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusHours(1));
        }

        @Test
        void should_calculate_next_hour_every_two_hours() {
            // Arrange
            var schedule = new Schedule("9", "*/3", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusHours(3));
        }

        @Test
        void should_calculate_next_hour_single_value_this_day() {
            // Arrange
            var schedule = new Schedule("9", "14", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusHours(4));
        }

        @Test
        void should_calculate_next_hour_single_value_next_day() {
            // Arrange
            var schedule = new Schedule("*", "9", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusDays(1).withHour(9));
        }

        @Test
        void should_calculate_next_day_every_day() {
            // Arrange
            var schedule = new Schedule("*", "10", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusDays(1));
        }

        @Test
        void should_calculate_next_day_every_seven_days() {
            // Arrange
            var schedule = new Schedule("*/7", "10", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusDays(7));
        }

        @Test
        void should_calculate_next_day_single_value_this_month() {
            // Arrange
            var schedule = new Schedule("15", "10", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusDays(6));
        }

        @Test
        void should_calculate_next_day_single_value_next_month() {
            // Arrange
            var schedule = new Schedule("3", "10", "39");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusMonths(1).withDayOfMonth(3));
        }
    }

    @DisplayName("Multi-field schedules")
    @Nested
    class MultiFieldSchedules {
        private final Clock base = Clock.fixed(Instant.parse("2025-08-09T23:49:16+01:00"), ZoneId.of("UTC"));
        private final OffsetDateTime after = OffsetDateTime.now(base);

        @Test
        void should_calculate_next_every_minute_every_other_hour() {
            // Arrange
            var schedule = new Schedule("*", "*/2", "*/15");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusDays(1).withHour(1).withMinute(4));
        }

        @Test
        void should_calculate_next_every_minute_every_other_hour_every_week() {
            // Arrange
            var schedule = new Schedule("*/7", "*/2", "*/15");

            // Act
            var result = calculator.calculateNextInvocationTime(after, schedule);

            // Assert
            assertThat(result).isEqualTo(after.plusDays(8).withHour(1).withMinute(4));
        }
    }

    @DisplayName("Observed misbehaviour during development")
    @Nested
    class Bugfixes {
        @Test
        void should_not_increment_higher_fields_when_lower_field_already_led_to_new_value() {
            // Arrange
            var now = OffsetDateTime.parse("2025-08-31T14:03:29.445223+02:00");
            var schedule = new Schedule("*", "*", "*/5");

            // Act
            var result = calculator.calculateNextInvocationTime(now, schedule);

            // Assert
            var expected = OffsetDateTime.parse("2025-08-31T14:08:29.445223+02:00");
            assertThat(result).isEqualTo(expected);
        }
    }
}
