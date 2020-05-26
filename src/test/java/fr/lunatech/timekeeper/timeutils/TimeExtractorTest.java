package fr.lunatech.timekeeper.timeutils;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

// Duration ISO https://en.wikipedia.org/wiki/ISO_8601#Durations
class TimeExtractorTest {

    @Test
    void shouldExtractDuration() {
        assertEquals(   Duration.parse("PT4H") , TimeExtractor.extractDuration("PT4H"));
        assertEquals(   Duration.parse("PT8H") , TimeExtractor.extractDuration("PT8H"));
        assertEquals(   Duration.parse("PT8H") , TimeExtractor.extractDuration("PT8H"));
        assertEquals(   Duration.parse("PT1H") , TimeExtractor.extractDuration("PT1H"));
        assertEquals(   Duration.parse("PT1H30M") , TimeExtractor.extractDuration("PT1H30M"));
    }

}