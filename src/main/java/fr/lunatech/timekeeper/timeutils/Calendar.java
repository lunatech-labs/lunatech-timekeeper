package fr.lunatech.timekeeper.timeutils;

import java.time.LocalDate;
import java.util.List;

public interface Calendar {
    List<LocalDate> getBusinessDays();

    List<PublicHoliday> getPublicHolidays();

}
