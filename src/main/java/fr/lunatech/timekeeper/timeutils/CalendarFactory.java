package fr.lunatech.timekeeper.timeutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarFactory {
    private static Logger logger = LoggerFactory.getLogger(CalendarFactory.class);

    public static Calendar instanceFor(final String locale, final Integer year) {
        if (locale.equalsIgnoreCase("FR") && year == 2020) {
            return CalendarFR2020.getInstance();
        }
        logger.error(String.format("Calendar not implemented for locale=%s and year=%d",locale,year));
        throw new IllegalStateException("Calendar not available for this locale and this year");
    }

}
