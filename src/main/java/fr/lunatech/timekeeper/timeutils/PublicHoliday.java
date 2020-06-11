package fr.lunatech.timekeeper.timeutils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PublicHoliday {
    protected LocalDate date;
    private String localName;
    private String name;
    private String countryCode;

    public PublicHoliday(LocalDate date, String localName, String name, String countryCode) {
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.countryCode = countryCode;
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

}

