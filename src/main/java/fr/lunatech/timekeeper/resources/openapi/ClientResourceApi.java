package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/clients")
public interface ClientResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all clients",
            description = "Retrieve the list of clients.")
    @Tag(ref = "clients")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Clients retrieved"
            )
    })
    List<ClientResponse> getAllClients();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new client ",
            description = "Create a client.")
    @Tag(ref = "clients")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Client created",
                    headers = {
                            @Header(name = LOCATION, description = "New client url", schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createClient(@RequestBody ClientRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a client",
            description = "Retrieve client details.")
    @Tag(ref = "clients")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Client retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Client not found"
            )
    })
    ClientResponse getClient(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a client",
            description = "Update client details.")
    @Tag(ref = "clients")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Client updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Client not found"
            )
    })
    Response updateClient(@PathParam("id") Long id, @RequestBody ClientRequest request);

}
