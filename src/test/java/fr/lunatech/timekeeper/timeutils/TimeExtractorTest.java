package fr.lunatech.timekeeper.timeutils;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

// Duration ISO https://en.wikipedia.org/wiki/ISO_8601#Durations
class TimeExtractorTest {

    @Test
    void shouldExtractDuration() {
        assertEquals(   Duration.parse("PT4H") , TimeExtractor.extractDuration("4h"));
        assertEquals(   Duration.parse("PT8H") , TimeExtractor.extractDuration("8h"));
        assertEquals(   Duration.parse("PT8H") , TimeExtractor.extractDuration("1d"));
        assertEquals(   Duration.parse("PT1H") , TimeExtractor.extractDuration("1h"));
        assertEquals(   Duration.parse("PT1H30M") , TimeExtractor.extractDuration("1h30mn"));
    }

    @Test
    void extractStartTime() {
    }

    @Test
    void extractEndTime() {
    }
}