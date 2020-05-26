package fr.lunatech.timekeeper.timeutils;

/**
 * A TimeUnit is the default time subdivision for a TimeEntry and a specific project.
 * On a typical project you can bill hours, days or half-a-day.
 */
public enum TimeUnit {
    HOURLY,
    HALFDAY,
    DAY
}