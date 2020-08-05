package fr.lunatech.timekeeper.gauges;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeEntriesNumberPerHoursGaugeTest {
    @Test
    public void shouldIncrement() {
        final TimeEntriesNumberPerHoursGauge timeEntriesNumberPerHoursGauge = new TimeEntriesNumberPerHoursGauge();
        timeEntriesNumberPerHoursGauge.incrementGauges(4);
        timeEntriesNumberPerHoursGauge.incrementGauges(4);
        timeEntriesNumberPerHoursGauge.incrementGauges(1);
        timeEntriesNumberPerHoursGauge.incrementGauges(2);
        timeEntriesNumberPerHoursGauge.incrementGauges(8);
        assertEquals(2L, timeEntriesNumberPerHoursGauge.numberOfHalfDayEntries().longValue());
        assertEquals(2L, timeEntriesNumberPerHoursGauge.numberOfOtherHoursEntries().longValue());
        assertEquals(1L, timeEntriesNumberPerHoursGauge.numberOfDayEntries().longValue());
    }

    @Test
    public void shouldDecrement() {
        final TimeEntriesNumberPerHoursGauge timeEntriesNumberPerHoursGauge = new TimeEntriesNumberPerHoursGauge();
        //Increment
        timeEntriesNumberPerHoursGauge.incrementGauges(4);
        timeEntriesNumberPerHoursGauge.incrementGauges(4);
        timeEntriesNumberPerHoursGauge.incrementGauges(1);
        timeEntriesNumberPerHoursGauge.incrementGauges(2);
        timeEntriesNumberPerHoursGauge.incrementGauges(8);
        timeEntriesNumberPerHoursGauge.incrementGauges(8);

        //Decrement
        timeEntriesNumberPerHoursGauge.decrementGauges(4);
        timeEntriesNumberPerHoursGauge.decrementGauges(1);
        timeEntriesNumberPerHoursGauge.decrementGauges(8);

        assertEquals(1L, timeEntriesNumberPerHoursGauge.numberOfHalfDayEntries().longValue());
        assertEquals(1L, timeEntriesNumberPerHoursGauge.numberOfOtherHoursEntries().longValue());
        assertEquals(1L, timeEntriesNumberPerHoursGauge.numberOfDayEntries().longValue());
    }

    @Test
    public void shouldNotDecrement() {
        final TimeEntriesNumberPerHoursGauge timeEntriesNumberPerHoursGauge = new TimeEntriesNumberPerHoursGauge();
        timeEntriesNumberPerHoursGauge.decrementGauges(8);
        assertEquals(0L, timeEntriesNumberPerHoursGauge.numberOfDayEntries().longValue());
    }
}
