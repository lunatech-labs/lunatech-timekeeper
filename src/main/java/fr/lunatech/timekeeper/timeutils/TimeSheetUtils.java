package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.time.TimeSheet;

import java.time.Duration;

public class TimeSheetUtils {

    // Compute what's left based on durationUnit type (DAY = 8hrs, HALFDAY = 4hrs)
    public static Long computeLeftOver(TimeSheet sheet) {
        long consumedHours = sheet.entries.stream()
                .mapToLong(timeEntry -> Duration.between(timeEntry.startDateTime, timeEntry.endDateTime).toHours())
                .sum();

        if (null != sheet.maxDuration && sheet.maxDuration > 0) {
            if (sheet.durationUnit != null) {
                switch (sheet.durationUnit) {
                    case DAY:
                        return ((sheet.maxDuration * 8) - consumedHours) / 8;
                    case HALFDAY:
                        return ((sheet.maxDuration * 4) - consumedHours) / 4;
                    case HOURLY:
                        return sheet.maxDuration - consumedHours;
                }
            } else {
                // if no durationUnit let's consider it's a day based duration
                return ((sheet.maxDuration * 8) - consumedHours) / 8;
            }
        }
        return null;
    }
}