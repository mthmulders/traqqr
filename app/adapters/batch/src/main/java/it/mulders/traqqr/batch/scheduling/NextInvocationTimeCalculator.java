package it.mulders.traqqr.batch.scheduling;

import static java.lang.Integer.parseInt;

import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NextInvocationTimeCalculator {
    private static final Logger log = LoggerFactory.getLogger(NextInvocationTimeCalculator.class);

    private static final String WILDCARD_CHAR = "*";

    public OffsetDateTime calculateNextInvocationTime(final OffsetDateTime after, final Schedule schedule) {
        log.debug("Calculating next invocation time; after={}, schedule={}", after, schedule);
        var intermediate = after;

        // 1. Calculate the next minute.
        intermediate = processMinuteSpec(after, intermediate, schedule);
        // 2. Calculate the next hour.
        intermediate = processHourSpec(after, intermediate, schedule);
        // 3. Calculate the next day.
        intermediate = processDayOfMonthSpec(after, intermediate, schedule);

        return intermediate;
    }

    private OffsetDateTime processMinuteSpec(
            final OffsetDateTime after, OffsetDateTime intermediate, final Schedule schedule) {
        var minuteSpec = schedule.minute();
        if (WILDCARD_CHAR.equals(minuteSpec)) {
            intermediate = intermediate.plusMinutes(1);
        } else if (minuteSpec.contains(WILDCARD_CHAR)) {
            var minutes = extractInterval(minuteSpec);
            intermediate = intermediate.plusMinutes(minutes);
        } else {
            var minute = extractSingleValue(minuteSpec);
            intermediate = intermediate.withMinute(minute);
            if (minute < after.getMinute()) {
                intermediate = intermediate.plusHours(1);
            }
        }
        return intermediate;
    }

    private OffsetDateTime processHourSpec(
            final OffsetDateTime after, OffsetDateTime intermediate, final Schedule schedule) {
        var hourSpec = schedule.hour();
        if (WILDCARD_CHAR.equals(hourSpec)) {
            if (intermediate.equals(after)) {
                intermediate = intermediate.plusHours(1);
            }
        } else if (hourSpec.contains(WILDCARD_CHAR)) {
            var hours = extractInterval(hourSpec);
            intermediate = intermediate.plusHours(hours);
        } else {
            var hour = extractSingleValue(hourSpec);
            intermediate = intermediate.withHour(hour);
            if (hour < after.getHour()) {
                intermediate = intermediate.plusDays(1);
            }
        }
        return intermediate;
    }

    private OffsetDateTime processDayOfMonthSpec(
            final OffsetDateTime after, OffsetDateTime intermediate, final Schedule schedule) {
        var dayOfMonthSpec = schedule.dayOfMonth();
        if (WILDCARD_CHAR.equals(dayOfMonthSpec)) {
            if (intermediate.equals(after)) {
                intermediate = intermediate.plusDays(1);
            }
        } else if (dayOfMonthSpec.contains(WILDCARD_CHAR)) {
            var days = extractInterval(dayOfMonthSpec);
            intermediate = intermediate.plusDays(days);
        } else {
            var day = extractSingleValue(dayOfMonthSpec);
            intermediate = intermediate.withDayOfMonth(day);
            if (day < after.getDayOfMonth()) {
                intermediate = intermediate.plusMonths(1);
            }
        }
        return intermediate;
    }

    private int extractInterval(final String spec) {
        return parseInt(spec.substring(spec.indexOf(WILDCARD_CHAR) + 2), 10);
    }

    private int extractSingleValue(final String spec) {
        return parseInt(spec, 10);
    }
}
