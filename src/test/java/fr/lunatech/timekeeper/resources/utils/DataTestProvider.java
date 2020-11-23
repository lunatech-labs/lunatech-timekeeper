package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.time.EventType;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import fr.lunatech.timekeeper.services.requests.UserEventRequest;
import fr.lunatech.timekeeper.services.responses.Attendee;
import fr.lunatech.timekeeper.services.responses.EventTemplateResponse;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.lang3.RandomStringUtils;

import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fr.lunatech.timekeeper.services.responses.UserEventResponse.bind;

@Singleton
public class DataTestProvider {

    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_8_AM = LocalDateTime.of(2020, 6, 24, 8, 0).withNano(0);
    public static final LocalDateTime THE_28_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020, 6, 28, 17, 0).withNano(0);
    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_2_PM = LocalDateTime.of(2020, 6, 28, 14, 0).withNano(0);
    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_9_AM = LocalDateTime.of(2020, 6, 24, 9, 0).withNano(0);
    public static final LocalDateTime THE_24_TH_JUNE_2020_AT_5_PM = LocalDateTime.of(2020, 6, 24, 17, 0).withNano(0);

    public static final LocalDateTime THE_18_TH_JULY_2020_AT_10_AM = LocalDateTime.of(2020, 7, 18, 10, 0).withNano(0);
    public static final LocalDateTime THE_18_TH_JULY_2020_AT_6_PM = LocalDateTime.of(2020, 7, 18, 18, 0).withNano(0);
    public static final String EVENT_DESCRIPTION = "It's a corporate event";
    public static final String EVENT_NAME = "The test event";

    public EventTemplateRequest generateEventTemplateRequest(String eventName, LocalDateTime start, LocalDateTime end, Long... usersId) {
        return new EventTemplateRequest(
                eventName,
                EVENT_DESCRIPTION,
                start,
                end,
                Arrays.stream(usersId)
                        .sorted(Comparator.comparingLong(value -> (long) value))
                        .map(value -> new UserEventRequest(value.longValue(), value.longValue()))
                        .collect(Collectors.toList())
        );
    }


    public Tuple2<EventTemplateRequest, EventTemplateResponse> generateEventTemplateTuple(String eventName, LocalDateTime start, LocalDateTime end, Long expectedTemplateId, Long... usersId) {
        final List<Attendee> attendees = generateAttendees(usersId);
        return Tuple.of(
                generateEventTemplateRequest(eventName, start, end, usersId),
                generateExpectedEventTemplateResponse(eventName, start, end, expectedTemplateId, attendees)
        );
    }

    public UserEventRequest generateUserEventRequest(String eventName, LocalDateTime start, LocalDateTime end, Long userId, Long creatorId) {
        return new UserEventRequest(
                userId,
                eventName,
                EVENT_DESCRIPTION,
                start,
                end,
                creatorId
        );
    }


    // the backend reverse order of the user ID and the generated userEvent id
    public UserEventResponse generateExpectedUserEventResponse(String eventName, LocalDateTime start, LocalDateTime end, Long expectedID, User user, User creator) {
        final var userEvent = new UserEvent(
                expectedID,
                start,
                end,
                eventName,
                EVENT_DESCRIPTION,
                EventType.PERSONAL,
                user,
                creator,
                creator.organization
        );
        return bind(userEvent);
    }

    public Tuple2<UserEventRequest, UserEventResponse> generateUserEventTuple(String eventName, LocalDateTime start, LocalDateTime end, Long expectedTemplateId, Long userId, Long creatorId) {
        final var user = generateUser(userId).stream().findFirst().orElseThrow(NotFoundException::new);
        final var creator = generateUser(creatorId).stream().findFirst().orElseThrow(NotFoundException::new);
        return Tuple.of(
                generateUserEventRequest(eventName, start, end, userId, creatorId),
                generateExpectedUserEventResponse(eventName, start, end, expectedTemplateId, user, creator)
        );
    }

    public String generateRandomEventName() {
        final int length = 5;
        final boolean useLetters = true;
        final boolean useNumbers = false;
        return EVENT_NAME + "-" + RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public Organization generateOrganization() {
        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();
        return organization;
    }


    private List<User> generateUser(Long... usersId) {
        return usersTest()
                .stream()
                .filter(user -> Arrays.asList(usersId).contains(user.getId()))
                .collect(Collectors.toList());
    }


    private List<Attendee> generateAttendees(Long... usersId) {
        return usersTest()
                .stream()
                .map(user -> new Attendee(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPicture()))
                .filter(attendee -> Arrays.asList(usersId).contains(attendee.getUserId()))
                .collect(Collectors.toList());
    }

    // the backend reverse order of the user ID and the generated userEvent id
    private EventTemplateResponse generateExpectedEventTemplateResponse(String eventName, LocalDateTime start, LocalDateTime end, Long expectedID, List<Attendee> attendees) {
        return new EventTemplateResponse(
                expectedID,
                eventName,
                EVENT_DESCRIPTION,
                start,
                end,
                attendees
        );
    }

    private List<User> usersTest() {
        return List.of(
                new User(1L, "Sam", "Uell", "sam@lunatech.fr", "sam.png"),
                new User(2L, "Jimmy", "James", "jimmy@lunatech.fr", "jimmy.png"),
                new User(3L, "Dutch First Name", "Dutch Last Name", "dutch@lunatech.nl", "dutch.png")
        );
    }

}
