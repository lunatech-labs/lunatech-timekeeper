package fr.lunatech.timekeeper.gauges;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class TimeEntriesNumberOfHoursGauge {

    private long fourHours;
    private long eightHours;
    private long otherHours;

    public void incrementGauges(Integer numberHours) {
        switch (numberHours) {
            case 4:
                fourHours++;
                break;
            case 8:
                eightHours++;
                break;
            default:
                otherHours++;
                break;
        }
    }

    public void decrementGauges(Integer numberHours) {
        switch (numberHours) {
            case 4:
                if (fourHours - 1 >= 0) {
                    fourHours--;
                }
                break;
            case 8:
                if (eightHours - 1 >= 0) {
                    eightHours--;
                }
                break;
            default:
                if (otherHours - 1 >= 0) {
                    otherHours--;
                }
                break;
        }
    }

    @Gauge(name = "numberOfHalfDayEntries", unit = MetricUnits.NONE, description = "Number of entries created for a half day")
    public Long numberOfHalfDayEntries() {
        return fourHours;
    }


    @Gauge(name = "numberOfFullDayEntries", unit = MetricUnits.NONE, description = "Number of entries created for a full day")
    public Long numberOfDayEntries() {
        return eightHours;
    }

    @Gauge(name = "numberOfOtherHoursEntries", unit = MetricUnits.NONE, description = "Number of entries created with an amount of hours different than 4 and 8 hours")
    public Long numberOfOtherHoursEntries() {
        return otherHours;
    }

    void init(@Observes StartupEvent event) {
    }
}