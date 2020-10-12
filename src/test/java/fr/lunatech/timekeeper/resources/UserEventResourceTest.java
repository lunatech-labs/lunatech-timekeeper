/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.DataTestProvider;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.requests.UserEventRequest;
import fr.lunatech.timekeeper.services.responses.UserEventResponse;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vavr.Tuple2;
import org.flywaydb.core.Flyway;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static fr.lunatech.timekeeper.resources.utils.DataTestProvider.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.PersonnalUserEventsDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.UserEventsDef;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getAdminAccessToken;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class UserEventResourceTest {

    @Inject
    TimeKeeperTestUtils timeKeeperTestUtils;

    @Inject
    DataTestProvider dataTestProvider;

    @Inject
    Flyway flyway;

    @AfterEach
    void cleanUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void shouldSuccessWhenCreateAnUserEvent() {
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var eventName = dataTestProvider.generateRandomEventName();
        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple1 =
                dataTestProvider.generateUserEventTuple(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, 1L, sam.getId(), sam.getId());
        final var response = create(userEventTuple1._1, adminToken);
        getValidation(
                UserEventsDef.uriPlusId(response.getId()), adminToken
        ).body(
                Matchers.is(timeKeeperTestUtils.toJson(userEventTuple1._2))
        ).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldListUserEvent() {
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var eventName = dataTestProvider.generateRandomEventName();
        final var anotherEventName = dataTestProvider.generateRandomEventName();

        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple1 =
                dataTestProvider.generateUserEventTuple(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, 1L, sam.getId(), sam.getId());
        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple2 =
                dataTestProvider.generateUserEventTuple(anotherEventName, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_6_PM, 2L, sam.getId(), sam.getId());
        create(userEventTuple1._1, adminToken);
        create(userEventTuple2._1, adminToken);
        getValidation(
                PersonnalUserEventsDef.uriWithMultiId(sam.getId()), adminToken
        ).body(
                Matchers.is(timeKeeperTestUtils.listOfTasJson(userEventTuple1._2, userEventTuple2._2))
        ).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldListUserEventCreatedByAnAdmin() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var jimmy = create(userToken);
        final var eventName = dataTestProvider.generateRandomEventName();
        final var anotherEventName = dataTestProvider.generateRandomEventName();
        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple1 =
                dataTestProvider.generateUserEventTuple(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, 1L, jimmy.getId(), sam.getId());
        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple2 =
                dataTestProvider.generateUserEventTuple(anotherEventName, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_6_PM, 2L, jimmy.getId(), sam.getId());
        create(userEventTuple1._1, adminToken);
        create(userEventTuple2._1, adminToken);
        getValidation(
                PersonnalUserEventsDef.uriWithMultiId(jimmy.getId()), userToken
        ).body(
                Matchers.is(timeKeeperTestUtils.listOfTasJson(userEventTuple1._2, userEventTuple2._2))
        ).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }

    @Test
    void shouldUpdateUserEventCreatorByAnAdmin() {
        final String adminToken = getAdminAccessToken();
        final String userToken = getAdminAccessToken();
        final var sam = create(adminToken);
        final var jimmy = create(userToken);
        final var eventName = dataTestProvider.generateRandomEventName();
        final var anotherEventName = dataTestProvider.generateRandomEventName();
        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple1 =
                dataTestProvider.generateUserEventTuple(eventName, THE_24_TH_JUNE_2020_AT_9_AM, THE_24_TH_JUNE_2020_AT_5_PM, 1L, jimmy.getId(), jimmy.getId());
        final var userEventRequest = dataTestProvider.generateUserEventRequest(anotherEventName, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_6_PM, jimmy.getId(), jimmy.getId());
        final Tuple2<UserEventRequest, UserEventResponse> userEventTuple2 =
                dataTestProvider.generateUserEventTuple(anotherEventName, THE_18_TH_JULY_2020_AT_10_AM, THE_18_TH_JULY_2020_AT_6_PM, 2L, jimmy.getId(), sam.getId());
        create(userEventTuple1._1, adminToken);
        create(userEventRequest, userToken);
        update(userEventTuple2._1, adminToken);
        getValidation(
                PersonnalUserEventsDef.uriWithMultiId(jimmy.getId()), userToken
        ).body(
                Matchers.is(timeKeeperTestUtils.listOfTasJson(userEventTuple1._2, userEventTuple2._2))
        ).statusCode(CoreMatchers.is(OK.getStatusCode()));
    }
}