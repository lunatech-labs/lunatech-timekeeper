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

import fr.lunatech.timekeeper.services.exceptions.CalendarNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarFactory {
    private static Logger logger = LoggerFactory.getLogger(CalendarFactory.class);

    private CalendarFactory() {
        throw new IllegalStateException("Utility class, not meant to be instanciated");
    }

    public static Calendar instanceFor(final String locale, final Integer year) {
        if (locale.equalsIgnoreCase("FR") && year == 2020) {
            return CalendarFR2020.getInstance();
        }
        if(locale.equalsIgnoreCase("FR") && year == 2021){
            return CalendarFR2021.getInstance();
        }
        if(logger.isErrorEnabled()) {
            logger.error(String.format("Calendar not implemented for locale=%s and year=%d", locale, year));
        }
        throw new CalendarNotFoundException("Calendar not available for this locale and this year");
    }

}
