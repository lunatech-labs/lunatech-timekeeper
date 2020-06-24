/*
 * Copyright 2020 Lunatech Labs
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.DayOfWeek.*;

public class CalendarFR2020 implements Calendar {
    private static final CalendarFR2020 instance = new CalendarFR2020();

    private CalendarFR2020() {
    }

    public static CalendarFR2020 getInstance() {
        return instance;
    }

    final LocalDate startDate = LocalDate.of(2020, 1, 1);
    final LocalDate endDate = LocalDate.of(2021, 01, 01);

    // List of public holidays here : https://date.nager.at/
    // For further version there is an API on this web service we could use
    // but it's not mandatory for the MVP
    final List<PublicHoliday> publicHolidays = List.of(
            new PublicHoliday(LocalDate.of(2020, 1, 1), "Jour de l'an", "New Year's Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 4, 13), "Lundi de Pâques", "Easter Monday", "FR")
            , new PublicHoliday(LocalDate.of(2020, 5, 1), "Fête du premier mai", "Labour Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 5, 8), "Fête de la Victoire", "Victory in Europe Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 5, 21), "Jour de l'Ascension", "Ascension Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 6, 1), "Lundi de Pentecôte", "Whit Monday", "FR")
            , new PublicHoliday(LocalDate.of(2020, 7, 14), "Fête nationale", "Bastille Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 8, 15), "L'Assomption de Marie", "Assumption Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 11, 1), "La Toussaint", "All Saints' Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 11, 11), "Armistice de 1918", "Armistice Day", "FR")
            , new PublicHoliday(LocalDate.of(2020, 12, 25), "Noël", "Christmas Day", "FR")
    );

    final List<LocalDate> holidays = publicHolidays.stream().map(publicHoliday -> publicHoliday.date).collect(Collectors.toList());

    final List<LocalDate> allDates =
            // Java 9 provides a method to return a stream with dates from the
            // startdate to the given end date.
            // Note that the end date itself is NOT included.
            startDate.datesUntil(endDate)
                    // Retain all business days. Use static imports from
                    // java.time.DayOfWeek.*
                    .filter(t -> Stream.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
                            .anyMatch(t.getDayOfWeek()::equals))
                    // Retain only dates not present in our holidays list
                    .filter(t -> !holidays.contains(t))
                    .collect(Collectors.toList());

    @Override
    public List<LocalDate> getBusinessDays() {
        return Collections.unmodifiableList(allDates);
    }

    @Override
    public List<PublicHoliday> getPublicHolidays() {
        return Collections.unmodifiableList(publicHolidays);
    }

    public List<PublicHoliday> getPublicHolidaysForWeekNumber(final Integer weekNumber) {
        // In 2020 there are 53 weeks in the IsoWeek calendar.
        if (weekNumber < 1 || weekNumber > 53) throw new IllegalStateException("Invalid weekNumber value");
        return publicHolidays.stream()
                .filter(p -> TimeKeeperDateUtils.getWeekNumberFromDate(p.date).equals(weekNumber))
                .collect(Collectors.toList());

    }
}
