package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ClientResourceApi;
import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import fr.lunatech.timekeeper.services.interfaces.ClientService;

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

    @RolesAllowed({"user", "admin"})
    @Override
    public List<ClientResponse> getAllClients() {
        return clientService.listAllClients();
    }

    @RolesAllowed({"admin"})
    @Override
    public Response createClient(@Valid ClientRequest request, UriInfo uriInfo) {
        final long clientId = clientService.createClient(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(clientId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public ClientResponse getClient(Long id) {
        return clientService.findClientById(id).orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"admin"})
    @Override
    public Response updateClient(Long id, @Valid ClientRequest request) {
        clientService.updateClient(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
