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

import fr.lunatech.timekeeper.resources.openapi.ClientResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.ClientService;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class ClientResource implements ClientResourceApi {

    @Inject
    ClientService clientService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetAllClients", description = "Counts how many times the user load the client list on method 'getAllClients'")
    @Timed(name = "timeGetAllClients", description = "Times how long it takes the user load the client list on method 'getAllClients'", unit = MetricUnits.MILLISECONDS)
    public List<ClientResponse> getAllClients() {
        final var ctx = authentication.context();
        return clientService.listAllResponses(ctx);
    }

    @RolesAllowed({"admin"})
    @Override
    @Counted(name = "countCreateClient", description = "Counts how many times the user to create the client on method 'createClient'")
    @Timed(name = "timeCreateClient", description = "Times how long it takes the user to create the client on method 'createClient'", unit = MetricUnits.MILLISECONDS)
    public Response createClient(@Valid ClientRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long clientId = clientService.create(request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(clientId)).build();
        return Response.created(uri).build();
    }

    @Counted(name = "countGetClient", description = "Counts how many times the user to get the client on method 'getClient'")
    @Timed(name = "timeGetClient", description = "Times how long it takes the user to get the client on method 'getClient'", unit = MetricUnits.MILLISECONDS)
    @RolesAllowed({"user", "admin"})
    @Override
    public ClientResponse getClient(Long id) {
        final var ctx = authentication.context();
        return clientService.findResponseById(id, ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"admin"})
    @Override
    @Counted(name = "countUpdateClient", description = "Counts how many times the user to update the client on method 'updateClient'")
    @Timed(name = "timeUpdateClient", description = "Times how long it takes the user to update the client on method 'updateClient'", unit = MetricUnits.MILLISECONDS)
    public Response updateClient(Long id, @Valid ClientRequest request) {
        final var ctx = authentication.context();
        return clientService.update(id, request, ctx)
                .map( it -> Response.noContent().build())
                .orElseThrow(NotFoundException::new);
    }
}
