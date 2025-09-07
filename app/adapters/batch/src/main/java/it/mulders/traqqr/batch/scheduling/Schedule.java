package it.mulders.traqqr.batch.scheduling;

/**
 * For the interpretation of each field, see {@link Scheduled}.
 */
public record Schedule(String dayOfMonth, String hour, String minute) {
    static Schedule fromScheduled(Scheduled scheduled) {
        return new Schedule(scheduled.dayOfMonth(), scheduled.hour(), scheduled.minute());
    }
}
