package fr.lunatech.timekeeper.timeutils;

import java.time.Duration;
import java.time.LocalDateTime;

public class EventDuration {

    private EventDuration() {
    }

    /**
     * Compute the duration of the event
     * Assume that start >= end
     *
     * @param start start date of the event
     * @param end   end date of the event
     * @return the duration of the event up to midnight
     */
    public static long durationInHours(LocalDateTime start, LocalDateTime end) {
        if (start.toLocalDate().isAfter(end.toLocalDate())) {
            return 0L;
        }

        if (start.toLocalDate().isEqual(end.toLocalDate())) {
            return Duration.between(start, end).toHours();
        }

        // since end is after the start and NOT on the same day, we adjust 'start' to the next day value... to midnight
        // this is how we compute the duration for this event.
        // For an event that starts on 9:00 on the 27th of august, and has an end date set to the 29th at 9:00 => it should return 15 hours
        return Duration.between(start, start.plusDays(1).withHour(0).withMinute(0).withSecond(0)).toHours();
    }

}
