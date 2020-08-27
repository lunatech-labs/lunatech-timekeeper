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
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
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
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(sam.getId());

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
                sam.getId(),
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
        final String samToken = getAdminAccessToken();
        var sam = create(samToken);

        EventTemplateRequest.UserEventRequest userEventRequest = new EventTemplateRequest.UserEventRequest(sam.getId());

        EventTemplateRequest eventTemplateRequest = new EventTemplateRequest(
                "request1",
                "description1",
                START_DAY,
                START_DAY.plusHours(8),
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
               sam.getId(),
                organization,
                Collections.emptyList()
        );

        // Insert a first event of 8 hours
        eventTemplateService.create(eventTemplateRequest, ctx);

        // Assert that we cannot add a second event of 10 because the limit of 8 hours is already reached
        Assertions.assertThrows(
                IllegalEntityStateException.class,
                () -> eventTemplateService.create(eventTemplateRequest, ctx)
        );

    }

}
