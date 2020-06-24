/*
 * Copyright 2020 Lunatech Labs
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
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;

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

    public static OrganizationResponse create(OrganizationRequest organization, String token) {
        return InternalResourceUtils.createResource(organization, OrganizationDef.uri, OrganizationResponse.class, token);
    }

    public static Void create(Long timeSheetId, TimeEntryPerDayRequest timeEntryRequest, String token) {
        return InternalResourceUtils.createResource(timeEntryRequest, TimeEntryDef.uriWithArgs(timeSheetId, "day"), Void.class, token);
    }

    public static Void create(Long timeSheetId, TimeEntryPerHalfDayRequest timeEntryRequest, String token) {
        return InternalResourceUtils.createResource(timeEntryRequest, TimeEntryDef.uriWithArgs(timeSheetId, "half-a-day"), Void.class, token);
    }

    public static Void create(Long timeSheetId, TimeEntryPerHourRequest timeEntryRequest, String token) {
        return InternalResourceUtils.createResource(timeEntryRequest, TimeEntryDef.uriWithArgs(timeSheetId, "hour"), Void.class, token);
    }

    public static <P> void update(P request, String uri, String token) {
        InternalResourceUtils.updateResource(request, uri, token);
    }

    public static <P> void update(String uri, String token) {
        InternalResourceUtils.updateResource(uri, token);
    }
}