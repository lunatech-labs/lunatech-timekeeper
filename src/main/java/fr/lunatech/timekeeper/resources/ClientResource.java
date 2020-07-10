package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ClientResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.ClientService;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;

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
    public List<ClientResponse> getAllClients() {
        final var ctx = authentication.context();
        return clientService.listAllResponses(ctx);
    }

    @RolesAllowed({"admin"})
    @Override
    public Response createClient(@Valid ClientRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long clientId = clientService.create(request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(clientId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public ClientResponse getClient(Long id) {
        final var ctx = authentication.context();
        return clientService.findResponseById(id, ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"admin"})
    @Override
    public Response updateClient(Long id, @Valid ClientRequest request) {
        final var ctx = authentication.context();
        return clientService.update(id, request, ctx)
                .map( it -> Response.noContent().build())
                .orElseThrow(NotFoundException::new);
    }
}
