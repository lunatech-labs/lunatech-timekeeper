package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.time.TimeSheet;
import org.junit.platform.commons.util.CollectionUtils;
import org.keycloak.common.util.CollectionUtil;

import java.time.Duration;
import java.util.Collections;

public class TimeSheetUtils {


    public static Long computeLeftOver(TimeSheet sheet) {
        if (null != sheet.maxDuration && sheet.maxDuration > 0) {
            // Early exit if no entry : left = maxDuration
            if(sheet.entries == null || sheet.entries.isEmpty()){
                return sheet.maxDuration.longValue();
            }

            long consumedHours = sheet.entries.stream()
                            .mapToLong(timeEntry -> Duration.between(timeEntry.startDateTime, timeEntry.endDateTime).toHours())
                            .sum();

            // Compute what's left based on durationUnit type (DAY = 8hrs, HALFDAY = 4hrs)
            if (sheet.durationUnit != null) {
                switch (sheet.durationUnit) {
                    case DAY:
                        return ((sheet.maxDuration * 8) - consumedHours) / 8;
                    case HALFDAY:
                        return ((sheet.maxDuration * 4) - consumedHours) / 8;
                    case HOURLY:
                        return (sheet.maxDuration - consumedHours) / 8;
                }
            } else {
                // if no durationUnit let's consider it's a day based duration
                return ((sheet.maxDuration * 8) - consumedHours) / 8;
            }
        }
        return null;
    }
}