package fr.lunatech.timekeeper.timeutils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * @author created by N.Martignole, Lunatech, on 2020-06-10.
 */
public class TimeKeeperDateUtils {
    /**
     * Returns the LocalDate from a weekNumber, default is set to the Monday
     *
     * @param weekNumber is a value between 1 to 52
     * @return the Monday of this week number in the year
     */
    public static LocalDate retriveDateFromWeekNumber(final Integer weekNumber) {
        return LocalDate.now()
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Adjust a date to the first day of week, according to the current Locale.
     * For US Calendar, the week starts on Sunday. For European calendar, it is the Monday.
     *
     * @param inputDate is any valid date
     * @return an adjusted LocalDate
     */
    public static LocalDate adjustToFirstDayOfWeek(LocalDate inputDate) {
        // We can use either the User Locale or the Organization Locale.
        // But for the time being I set it to FR so the first day of week is a Monday
        // This code will need to be updated later, to adjust user preferences and maybe relies on the AuthenticationContext
        TemporalField fieldISO = WeekFields.of(Locale.FRANCE).dayOfWeek();
        LocalDate firstDayOfWeek = inputDate.with(fieldISO, 1);
        return firstDayOfWeek;
    }
}
