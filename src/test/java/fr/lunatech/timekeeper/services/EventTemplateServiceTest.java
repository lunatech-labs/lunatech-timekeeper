package fr.lunatech.timekeeper.services;

import com.google.common.collect.Lists;
import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.time.UserEvent;
import fr.lunatech.timekeeper.resources.KeycloakTestResource;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.requests.EventTemplateRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")
@Disabled // Test is disabled because H2 does not support `date()` function. Re-enable once event will store date only (not date and time).
class EventTemplateServiceTest {

    @Inject
    Flyway flyway;

    @Inject
    EventTemplateService eventTemplateService;

    @AfterEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }

    private static final LocalDateTime START_DAY = LocalDateTime.of(2020, 1, 1, 8, 0);

    @Test
    void create_should_create_an_user_event() {

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(1L);

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "request1",
                "description1",
                START_DAY,
                START_DAY.plusHours(6),
                Lists.newArrayList(userEventRequest)
        );

        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();

        AuthenticationContext ctx = new AuthenticationContext(
                userEventRequest.getUserId(),
                organization,
                Collections.emptyList()
        );

        Stream<UserEvent> eventsBefore = eventTemplateService.findUserEventsByUserId(userEventRequest.getUserId());
        eventTemplateService.create(eventTemplateRequest, ctx);
        Stream<UserEvent> eventsAfter = eventTemplateService.findUserEventsByUserId(userEventRequest.getUserId());

        Assertions.assertEquals(Optional.empty(), eventsBefore.findFirst());
        Assertions.assertEquals(1, (int) eventsAfter.count());
    }

    @Test
    void create_should_throw_an_exception_if_one_attendee_cannot_join() {

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(1L);

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "request1",
                "description1",
                START_DAY,
                START_DAY.plusHours(10),
                Lists.newArrayList(userEventRequest)
        );

        Organization organization = new Organization();
        organization.id = 1L;
        organization.name = "name";
        organization.tokenName = "tokenName";
        organization.users = Collections.emptyList();
        organization.projects = Collections.emptyList();
        organization.clients = Collections.emptyList();

        AuthenticationContext ctx = new AuthenticationContext(
                userEventRequest.getUserId(),
                organization,
                Collections.emptyList()
        );

        // Insert a first event of 10 hours
        eventTemplateService.create(eventTemplateRequest, ctx);

        // Assert that we cannot add a second event of 10 because the limit of 8 hours is already reached
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> eventTemplateService.create(eventTemplateRequest, ctx)
        );

    }

}
