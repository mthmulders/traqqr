package it.mulders.traqqr.batch.scheduling;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NextInvocationTimeCalculatorTest implements WithAssertions {
    private final NextInvocationTimeCalculator calculator = new NextInvocationTimeCalculator();

    private static final Clock base = Clock.fixed(Instant.parse("2025-08-09T11:39:16+01:00"), ZoneId.of("UTC"));
    private static final OffsetDateTime after = OffsetDateTime.now(base);

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(
                        "each minute",
                        "advance to the next minute",
                        new Schedule("9", "10", "*"),
                        after.plusMinutes(1)),
                Arguments.of(
                        "every 5 minutes",
                        "advance to 5 minutes later",
                        new Schedule("9", "10", "*/5"),
                        after.plusMinutes(5)),
                Arguments.of(
                        "fixed moment in current hour",
                        "advance to 52nd minute of current hour",
                        new Schedule("9", "10", "52"),
                        after.plusMinutes(52 - 39)),
                Arguments.of(
                        "every hour at fixed minute",
                        "advance to next hour at given minute",
                        new Schedule("9", "*", "22"),
                        after.plusHours(1).withMinute(22)),
                Arguments.of(
                        "every hour at fixed minute",
                        "stay within current hour if possible",
                        new Schedule("9", "*", "39"),
                        after.plusHours(1)),
                Arguments.of(
                        "every three hours at fixed minute",
                        "advance three hours",
                        new Schedule("9", "*/3", "39"),
                        after.plusHours(3)),
                Arguments.of(
                        "fixed moment every month",
                        "resolve on the correct day if that is today",
                        new Schedule("9", "14", "39"),
                        after.plusHours(4)),
                Arguments.of(
                        "fixed moment every day",
                        "resolve to that moment next day if it already happened today",
                        new Schedule("*", "9", "39"),
                        after.plusDays(1).withHour(9)),
                Arguments.of(
                        "fixed moment every day",
                        "resolve to that moment if it has just passed",
                        new Schedule("*", "10", "39"),
                        after.plusDays(1)),
                Arguments.of(
                        "every seven days",
                        "resolve to next week if it has already passed",
                        new Schedule("*/7", "10", "39"),
                        after.plusDays(7)),
                Arguments.of(
                        "fixed moment each month",
                        "resolve to this month if it hasn't passed yet",
                        new Schedule("15", "10", "39"),
                        after.plusDays(6)),
                Arguments.of(
                        "fixed moment each month",
                        "resolve to next month if it has already passed",
                        new Schedule("3", "10", "39"),
                        after.plusMonths(1).withDayOfMonth(3)));
    }

    @MethodSource("args")
    @ParameterizedTest(name = "Schedule \"{0}\" should {1}")
    void verify_calculation_of_next_invocation_time(
            String name, String behaviour, Schedule schedule, OffsetDateTime expected) {
        var result = calculator.calculateNextInvocationTime(after, schedule);
        assertThat(result).isEqualTo(expected);
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
