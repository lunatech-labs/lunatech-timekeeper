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

package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.requests.*;
import fr.lunatech.timekeeper.services.responses.*;
import io.restassured.response.ValidatableResponse;

import java.util.Map;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;

public class ResourceFactory {

    public static ProjectResponse create(ProjectRequest project, String token) {
        return InternalResourceUtils.createResource(project, ProjectDef.uri, ProjectResponse.class, token);
    }

    public static ProjectResponse read(Long id, Map<String, String> params, String token) {
        return InternalResourceUtils.readResource(id, params, ProjectDef.uri, ProjectResponse.class, token);
    }

    public static ClientResponse create(ClientRequest client, String token) {
        return InternalResourceUtils.createResource(client, ClientDef.uri, ClientResponse.class, token);
    }

    public static UserResponse create(String token) {
        return InternalResourceUtils.readResource("me", UserDef.uri, UserResponse.class, token);
    }

    public static EventTemplateResponse create(EventTemplateRequest eventTemplate, String token) {
        return InternalResourceUtils.createResource(eventTemplate, TemplateEventDef.uri, EventTemplateResponse.class, token);
    }

    public static UserEventResponse create(UserEventRequest userEventRequest, String token) {
        return InternalResourceUtils.createResource(userEventRequest, UserEventsDef.uri, UserEventResponse.class, token);
    }

    public static OrganizationResponse create(OrganizationRequest organization, String token) {
        return InternalResourceUtils.createResource(organization, OrganizationDef.uri, OrganizationResponse.class, token);
    }

    public static String create(Long timeSheetId, TimeEntryRequest timeEntryRequest, String token) {
        return InternalResourceUtils.createResource(timeEntryRequest, TimeEntryDef.uriWithArgs(timeSheetId), token);
    }

    public static <P> ValidatableResponse update(P request, String uri, String token) {
        return InternalResourceUtils.updateResource(request, uri, token);
    }

    public static <P> ValidatableResponse update(String uri, String token) {
       return InternalResourceUtils.updateResource(uri, token);
    }
}