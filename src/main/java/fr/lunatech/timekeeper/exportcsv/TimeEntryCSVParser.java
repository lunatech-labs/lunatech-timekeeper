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

package fr.lunatech.timekeeper.exportcsv;

import fr.lunatech.timekeeper.timeutils.TimeKeeperDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Singleton
public class TimeEntryCSVParser {

    private static Logger logger = LoggerFactory.getLogger(TimeEntryCSVParser.class);

    /**
     * Check if the parameters from URI can be parse into LocalDate and parse them
     *
     * @param start as a String
     * @param end   as a String
     * @return a List of LocalDate
     */
    public List<LocalDate> checkStringParametersAndParseThemIntoLocalDate(String start, String end) {
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = TimeKeeperDateUtils.formatToLocalDate(start);
            endDate = TimeKeeperDateUtils.formatToLocalDate(end);
            return Arrays.asList(startDate, endDate);
        } catch (DateTimeParseException e) {
            logger.warn("StartDate or EndDate is not valid to be parse into a LocalDate");
        }
        throw new IllegalArgumentException("Start Date or EndDate is not valid to be parsed");
    }

}
