package fr.lunatech.timekeeper.timeutils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.DayOfWeek.*;

/**
 * Design note : this is a sample class created manually (10mn of work).
 */
public class CalendarFR2020 implements Calendar {

    final LocalDate startDate = LocalDate.of(2020, 1, 1);
    final LocalDate endDate = LocalDate.of(2021, 01, 01);

    // List of public holidays here : https://date.nager.at/
    // For further version there is an API on this web service we could use
    // but it's not mandatory for the MVP
    final List<PublicHoliday> publicHolidays = List.of(
            new PublicHoliday(LocalDate.of(2020, 1, 1), "Jour de l'an", "New Year's Day", "FR",true)
            , new PublicHoliday(LocalDate.of(2020, 4, 13), "Lundi de Pâques", "Easter Monday", "FR", false)
            , new PublicHoliday(LocalDate.of(2020, 5, 1), "Fête du premier mai", "Labour Day", "FR", true)
            , new PublicHoliday(LocalDate.of(2020, 5, 8), "Fête de la Victoire", "Victory in Europe Day", "FR", true)
            , new PublicHoliday(LocalDate.of(2020, 5, 21), "Jour de l'Ascension", "Ascension Day", "FR", false)
            , new PublicHoliday(LocalDate.of(2020, 6, 1), "Lundi de Pentecôte", "Whit Monday", "FR", false)
            , new PublicHoliday(LocalDate.of(2020, 7, 14), "Fête nationale", "Bastille Day", "FR", true)
            , new PublicHoliday(LocalDate.of(2020, 8, 15), "L'Assomption de Marie", "Assumption Day", "FR", true)
            , new PublicHoliday(LocalDate.of(2020, 11, 1), "La Toussaint", "All Saints' Day", "FR", true)
            , new PublicHoliday(LocalDate.of(2020, 11, 11), "Armistice de 1918", "Armistice Day", "FR", true)
            , new PublicHoliday(LocalDate.of(2020, 12, 25), "Noël", "Christmas Day", "FR", true)
    );

    final List<LocalDate> holidays = publicHolidays.stream().map(publicHoliday -> publicHoliday.date).collect(Collectors.toList());

    final List<LocalDate> allDates =
            // Java 9 provides a method to return a stream with dates from the
            // startdate to the given end date. Note that the end date itself is
            // NOT included.
            startDate.datesUntil(endDate)

                    // Retain all business days. Use static imports from
                    // java.time.DayOfWeek.*
                    .filter(t -> Stream.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
                            .anyMatch(t.getDayOfWeek()::equals))

                    // Retain only dates not present in our holidays list
                    .filter(t -> !holidays.contains(t))

                    // Collect them into a List. If you only need to know the number of
                    // dates, you can also use .count()
                    .collect(Collectors.toList());

    @Override
    public List<LocalDate> getBusinessDays() {
        return Collections.unmodifiableList(allDates);
    }

    @Override
    public List<PublicHoliday> getPublicHolidays() {
        return Collections.unmodifiableList(publicHolidays);
    }

}
