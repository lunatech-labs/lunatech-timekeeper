package fr.lunatech.timekeeper.timeutils;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class TimeExtractor {

    /**
     * Parse an ISO 8601 duration, returns null if the duration is null.
     * https://en.wikipedia.org/wiki/ISO_8601#Durations
     *
     * @param duration
     * @return the Parsed duration or null if no duration was specified
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed to a duration
     */
    public static Duration extractDuration(String duration) {
        if (StringUtils.trimToNull(duration) == null) {
            return null;
        }
        return Duration.parse(StringUtils.trimToNull(duration));
    }

    /**
     * Obtains an instance of {@code LocalDateTime} from a text string such as {@code 2020-05-18T10:15:30}.
     * <p>
     * The string must represent a valid date-time and is parsed using
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param startTime the text to parse such as "2007-12-03T10:15:30" or null
     * @param isMorning
     * @return the parsed local date-time or null if the param was not null
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static LocalDateTime extractStartTime(String startTime, Boolean isMorning) {
        if (StringUtils.trimToNull(startTime) == null) {
            return null;
        }
        return LocalDateTime.parse(startTime);
    }

    public static LocalDateTime extractEndTime(String endTime, Boolean isMorning) {
        if (StringUtils.trimToNull(endTime) == null) {
            return null;
        }
        return LocalDateTime.parse(endTime);
    }

    public static LocalDateTime extractStartTimeForDay(String startTime) {
        if (StringUtils.trimToNull(startTime) == null) {
            return null;
        }
        return LocalDateTime.parse(startTime);
    }

    public static LocalDateTime extractEndTimeForDay(String endTime) {
        if (StringUtils.trimToNull(endTime) == null) {
            return null;
        }
        return LocalDateTime.parse(endTime);
    }

    public static LocalDateTime extractStartTime(String startTime) {
        if (StringUtils.trimToNull(startTime) == null) {
            return null;
        }
        return LocalDateTime.parse(startTime);
    }

    public static LocalDateTime extractEndTime(String endTime) {
        if (StringUtils.trimToNull(endTime) == null) {
            return null;
        }
        return LocalDateTime.parse(endTime);
    }
}
