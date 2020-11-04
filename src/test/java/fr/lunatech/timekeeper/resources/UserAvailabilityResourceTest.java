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
import fr.lunatech.timekeeper.resources.utils.DateUtilsTestResourceProvider;
import fr.lunatech.timekeeper.resources.utils.TimeKeeperTestUtils;
import fr.lunatech.timekeeper.services.responses.AvailabilityResponse;
import fr.lunatech.timekeeper.testcontainers.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Collections;
import java.util.List;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.create;
import static fr.lunatech.timekeeper.resources.utils.ResourceFactory.update;
import static fr.lunatech.timekeeper.resources.utils.ResourceValidation.getValidation;
import static fr.lunatech.timekeeper.testcontainers.KeycloakTestResource.getAdminAccessToken;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
@Tag("integration")
class UserAvailabilityResourceTest {

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
    void withNoUserEventsShouldListAllUsersAsAvailable() {
        //TODO resolve user creation. Sam, Jimmy and Geoff UserResponses all contain the User Sam
        //Create Sam, an admin
        final String adminToken = getAdminAccessToken();
        final var sam = create(adminToken);
        //Create Jimmy, a user
        final String userToken = getAdminAccessToken();
        final var jimmy = create(userToken);
        //Create another user Geoff
        final String geoffToken = getAdminAccessToken();
        final var geoff = create(geoffToken);

        final var expectedResponse = new AvailabilityResponse(
                DateUtilsTestResourceProvider.THE_6_TH_OCTOBER_2020_AT_9_AM
                , DateUtilsTestResourceProvider.THE_6_TH_OCTOBER_2020_AT_17_PM
                , List.of(sam, jimmy, geoff)
                , Collections.emptyList()
        );

        //Call endpoint to get available users
        getValidation(UserAvailabilityDef.uriWithLocalDateTimes(
                DateUtilsTestResourceProvider.THE_6_TH_OCTOBER_2020_AT_9_AM
        , DateUtilsTestResourceProvider.THE_6_TH_OCTOBER_2020_AT_17_PM), adminToken)
                .body(is(timeKeeperTestUtils.toJson(expectedResponse)))
                .statusCode(is(OK.getStatusCode()));
    }

//    @Test
//    void shouldListNoUsersAvailable() {
//        assertTrue((1 == 2));
//    }
//
//    @Test
//    void shouldListOnlyOneUserAvailable() {
//        assertTrue((1 == 2));
//    }
}