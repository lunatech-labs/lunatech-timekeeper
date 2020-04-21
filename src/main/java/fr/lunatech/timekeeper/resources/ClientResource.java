package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.MemberServiceImpl;
import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import fr.lunatech.timekeeper.resources.openapi.ClientResourceApi;
import fr.lunatech.timekeeper.services.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class ClientResource implements ClientResourceApi {

    private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Inject
    ClientService clientService;

    @Override
    public List<ClientResponse> getAllClients() {
        return clientService.listAllClients();
    }

    @Override
    public Response createClient(@Valid ClientRequest request, UriInfo uriInfo) {
        logger.debug("Create a new client with request="+request);
        final long clientId = clientService.createClient(request);
        logger.debug("Created client with clientId="+clientId);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(clientId)).build();
        return Response.created(uri).build();
    }

    @Override
    public ClientResponse getClient(Long id) {
        return clientService.findClientById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateClient(Long id, @Valid ClientRequest request) {
        clientService.updateClient(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
