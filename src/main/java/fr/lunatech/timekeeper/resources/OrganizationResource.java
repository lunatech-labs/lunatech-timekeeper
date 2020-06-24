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

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.OrganizationResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.OrganizationService;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class OrganizationResource implements OrganizationResourceApi {

    @Inject
    OrganizationService organizationService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"super_admin"})
    @Override
    public List<OrganizationResponse> getAllOrganizations() {
        final var ctx = authentication.context();
        return organizationService.listAllResponses(ctx);
    }

    @RolesAllowed({"super_admin"})
    @Override
    public Response createOrganization(@Valid OrganizationRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long organizationId = organizationService.create(request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(organizationId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"super_admin"})
    @Override
    public OrganizationResponse getOrganization(Long id) {
        final var ctx = authentication.context();
        return organizationService.findResponseById(id, ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"super_admin"})
    @Override
    public Response updateOrganization(Long id, @Valid OrganizationRequest request) {
        final var ctx = authentication.context();
        organizationService.update(id, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
