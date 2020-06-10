package fr.lunatech.timekeeper.timeutils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    public String getDate() {
        if(date==null) return null;
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }

    public String getLocalName() {
        return localName;
    }

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Boolean getFixedDay() {
        return fixedDay;
    }
}

