package fr.lunatech.timekeeper.timeutils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventDuration {

    /**
     * Compute the duration of the event
     * Assume that start >= end
     * @param start start date of the event
     * @param end end date of the event
     * @return the duration of the event up to midnight
     */
    static public long durationInHours(LocalDateTime start, LocalDateTime end) {
        if (start.toLocalDate().isEqual(end.toLocalDate())) {
            return Duration.between(start, end).toHours();
        } else {
            return Duration.between(start, LocalTime.MIDNIGHT).toHours();
        }
    }

}
