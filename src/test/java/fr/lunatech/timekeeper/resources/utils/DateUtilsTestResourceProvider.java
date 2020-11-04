package fr.lunatech.timekeeper.resources.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class DateUtilsTestResourceProvider {
    public static final LocalDate THE_6_TH_OCTOBER_2020 = LocalDate.of(2020, 10, 6);
    public static final LocalDate THE_8_TH_OCTOBER_2020 = LocalDate.of(2020, 10, 8);
    public static final LocalDate THE_11_TH_JUNE_2020 = LocalDate.of(2020, 6, 11);

    public static final LocalDateTime THE_29_TH_DECEMBER_2019_AT_16_PM = LocalDate.of(2019, 12, 29).atTime(16, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_3_RD_JANUARY_2020_AT_10_AM = LocalDate.of(2020, 1, 3).atTime(10, 0).truncatedTo(ChronoUnit.HOURS);

    public static final LocalDateTime THE_15_TH_JUNE_2020_AT_9_AM = LocalDate.of(2020, 6, 15).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_15_TH_JUNE_2020_AT_15_PM = LocalDate.of(2020, 6, 15).atTime(15, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_16_TH_JUNE_2020_AT_9_AM = LocalDate.of(2020, 6, 16).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JUNE_2020_AT_9_AM = LocalDate.of(2020, 6, 17).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JUNE_2020_AT_8_AM = LocalDate.of(2020, 6, 17).atTime(8, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JUNE_2020_AT_NOON = LocalDate.of(2020, 6, 17).atTime(12, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JUNE_2020_AT_17_PM = LocalDate.of(2020, 6, 17).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JULY_2020_AT_16_PM = LocalDate.of(2020, 7, 17).atTime(16, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JUNE_2020_AT_18_PM = LocalDate.of(2020, 6, 17).atTime(18, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JUNE_2020_AT_19_PM = LocalDate.of(2020, 6, 17).atTime(19, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_18_TH_JUNE_2020_AT_8_AM = LocalDate.of(2020, 6, 18).atTime(8, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_18_TH_JUNE_2020_AT_NOON = LocalDate.of(2020, 6, 18).atTime(12, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_18_TH_JUNE_2020_AT_13_PM = LocalDate.of(2020, 6, 18).atTime(13, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_18_TH_JUNE_2020_AT_14_PM = LocalDate.of(2020, 6, 18).atTime(14, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_18_TH_JUNE_2020_AT_17_PM = LocalDate.of(2020, 6, 18).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_18_TH_JUNE_2020_AT_18_PM = LocalDate.of(2020, 6, 18).atTime(18, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_19_TH_JUNE_2020_AT_17_PM = LocalDate.of(2020, 6, 19).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_23_RD_JUNE_2020_AT_17_PM = LocalDate.of(2020, 6, 23).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_23_RD_JUNE_2020_AT_18_PM = LocalDate.of(2020, 6, 23).atTime(18, 0).truncatedTo(ChronoUnit.HOURS);

    public static final LocalDateTime THE_10_TH_JULY_2020_AT_9_AM = LocalDate.of(2020, 7, 10).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_13_TH_JULY_2020_AT_9_AM = LocalDate.of(2020, 7, 13).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_14_TH_JULY_2020_AT_9_AM = LocalDate.of(2020, 7, 14).atTime(9, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_14_TH_JULY_2020_AT_17_PM = LocalDate.of(2020, 7, 14).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_15_TH_JULY_2020_AT_17_PM = LocalDate.of(2020, 7, 15).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_17_TH_JULY_2020_AT_17_PM = LocalDate.of(2020, 7, 17).atTime(17, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_20_TH_JULY_2020_AT_10_AM = LocalDate.of(2020, 7, 20).atTime(10, 0).truncatedTo(ChronoUnit.HOURS);

    public static final LocalDateTime THE_6_TH_OCTOBER_2020_AT_9_AM = LocalDateTime.of(2020, 10, 6, 9, 0);
    public static final LocalDateTime THE_6_TH_OCTOBER_2020_AT_10_AM = LocalDateTime.of(2020, 10, 6, 10, 0);
    public static final LocalDateTime THE_6_TH_OCTOBER_2020_AT_17_PM = LocalDateTime.of(2020, 10, 6, 17, 0);

    public static final LocalDateTime NEW_YEARS_EVE_2020_AT_16_PM = LocalDate.of(2020, 12, 31).atTime(16, 0).truncatedTo(ChronoUnit.HOURS);
    public static final LocalDateTime THE_4_TH_JANUARY_2021_AT_10_AM = LocalDate.of(2021, 1, 4).atTime(10, 0).truncatedTo(ChronoUnit.HOURS);

    public static final LocalTime MORNING_AT_9_AM = LocalTime.parse("09:00:00", DateTimeFormatter.ISO_TIME);
    public static final LocalTime NOON = LocalTime.parse("12:00:00", DateTimeFormatter.ISO_TIME);
    public static final LocalTime AFTERNOON_AT_14_PM = LocalTime.parse("14:00:00", DateTimeFormatter.ISO_TIME);
    public static final LocalTime AFTERNOON_AT_18_PM = LocalTime.parse("18:00:00", DateTimeFormatter.ISO_TIME);
}
