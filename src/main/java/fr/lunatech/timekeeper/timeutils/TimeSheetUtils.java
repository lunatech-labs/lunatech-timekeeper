/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.timeutils;

import fr.lunatech.timekeeper.models.time.TimeSheet;

public class TimeSheetUtils {

    private static final int NUMBER_OF_HOURS_IN_A_DAY = 8;
    private static final int NUMBER_OF_HOURS_IN_HALF_A_DAY = 4;

    private TimeSheetUtils() {
    }

    public static Long computeLeftOver(TimeSheet sheet) {
        if (null != sheet.maxDuration && sheet.maxDuration > 0) {
            // Early exit if no entry : left = maxDuration
            if (sheet.entries == null || sheet.entries.isEmpty()) {
                return sheet.maxDuration.longValue();
            }

            long consumedHours = sheet.entries.stream()
                    .mapToLong(timeEntry -> timeEntry.numberOfHours)
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
                    default:
                        throw new IllegalStateException("Invalid duration unit for sheet=" + sheet);
                }
            } else {
                // if no durationUnit let's consider it's a day based duration
                return ((sheet.maxDuration * NUMBER_OF_HOURS_IN_A_DAY) - consumedHours) / NUMBER_OF_HOURS_IN_A_DAY;
            }
        }
        return null;
    }
}