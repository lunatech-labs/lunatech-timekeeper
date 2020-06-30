package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.time.TimeSheet;

import java.time.Duration;

public class TimeSheetUtils {

    private static final int NUMBER_OF_HOURS_IN_A_DAY = 8;
    private static final int NUMBER_OF_HOURS_IN_HALF_A_DAY = 4;

    public static Long computeLeftOver(TimeSheet sheet) {
        if (null != sheet.maxDuration && sheet.maxDuration > 0) {
            // Early exit if no entry : left = maxDuration
            if(sheet.entries == null || sheet.entries.isEmpty()){
                return sheet.maxDuration.longValue();
            }

            long consumedHours = sheet.entries.stream()
                            .mapToLong(timeEntry -> Duration.between(timeEntry.startDateTime, timeEntry.endDateTime).toHours())
                            .sum();

            // Compute what's left based on durationUnit type (see constant)
            if (sheet.durationUnit != null) {
                switch (sheet.durationUnit) {
                    case DAY:
                        return ((sheet.maxDuration * NUMBER_OF_HOURS_IN_A_DAY) - consumedHours) / NUMBER_OF_HOURS_IN_A_DAY;
                    case HALFDAY:
                        return ((sheet.maxDuration * NUMBER_OF_HOURS_IN_HALF_A_DAY) - consumedHours) / NUMBER_OF_HOURS_IN_A_DAY;
                    case HOURLY:
                        return (sheet.maxDuration - consumedHours) / NUMBER_OF_HOURS_IN_A_DAY;
                }
            } else {
                // if no durationUnit let's consider it's a day based duration
                return ((sheet.maxDuration * NUMBER_OF_HOURS_IN_A_DAY) - consumedHours) / NUMBER_OF_HOURS_IN_A_DAY;
            }
        }
        return null;
    }
}