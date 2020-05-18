package fr.lunatech.timekeeper.timeutils;

import java.time.LocalDate;

public class PublicHoliday {
    public LocalDate date;
    public String localName;
    public String name;
    public String countryCode;
    public Boolean fixedDay;

    public PublicHoliday(LocalDate date, String localName, String name, String countryCode, Boolean fixedDay) {
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.countryCode = countryCode;
        this.fixedDay = fixedDay;
    }
}

