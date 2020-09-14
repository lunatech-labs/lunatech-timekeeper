package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import org.apache.commons.lang3.RandomStringUtils;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class DataTestProvider {

    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_8_AM = LocalDateTime.of(2020, 6, 24, 8, 0);
    public static final LocalDateTime THE_28_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020, 6, 28, 17, 0);
    public static final LocalDateTime THE_28_TH_JUNE_2020_AT_2_PM = LocalDateTime.of(2020, 6, 28, 14, 0);
    public static final String EVENT_DESCRIPTION = "It's a corporate event";
    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_9_AM = LocalDateTime.of(2020, 6, 24, 9, 0);
    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020, 6, 24, 17, 0);
    private static final String EVENT_NAME = "The test event";

    public EventTemplateRequest generateTestEventRequest(String eventName, Long... usersId) {
        return new EventTemplateRequest(
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                Arrays.stream(usersId)
                        .sorted(Comparator.comparingLong(value -> (long) value))
                        .map(EventTemplateRequest.UserEventRequest::new)
                        .collect(Collectors.toList())
        );
    }

    // the backend reverse order of the user ID and the generated userEvent id
    public EventTemplateResponse generateExpectedEventTemplateResponse(String eventName, Long expectedID, List<EventTemplateResponse.Attendee> attendees) {
        return new EventTemplateResponse(
                expectedID,
                eventName,
                EVENT_DESCRIPTION,
                THE_24_TH_JUNE_2020_AT_9_AM,
                THE_24_TH_JUNE_2020_AT_5_PM,
                attendees
        );
    }

    public String generateRandomEventName() {
        final int length = 5;
        final boolean useLetters = true;
        final boolean useNumbers = false;
        return EVENT_NAME + "-" + RandomStringUtils.random(length, useLetters, useNumbers);
    }
}
