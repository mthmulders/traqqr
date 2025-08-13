package it.mulders.traqqr.batch.scheduling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a schedule by which a method should be invoked. Each field can be
 *
 * <ul>
 *     <li>A <strong>single value</strong>, constraining the invocation to only one value for that time unit.</li>
 *     <li>A <strong>wild card</strong>, allowing the invocation to happen at all possible values for that time unit.</li>
 *     <li>An <strong>increment</strong>, allowing the invocation to happen every <code>N</code> possible values for that time unit.</li>
 * </ul>
 *
 * The design is inspired by Jakarta EE's <code>@Schedule</code>, but simplified.
 *
 * Without any element specified, the default is to run at midnight every day.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Scheduled {
    /** At which hour of the day invocations should take place. Allowed values: [1-31]. */
    String dayOfMonth() default "*";
    /** At which hour of the day invocations should take place. Allowed values: [0,23]. */
    String hour() default "0";
    /** At which minute of the hour invocations should take place. Allowed values: [0,59]. */
    String minute() default "0";
}
