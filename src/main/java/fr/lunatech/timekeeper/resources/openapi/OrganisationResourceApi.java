package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.OrganisationRequest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/organisations")
public interface OrganisationResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new organisation ",
            description = "Create a organisation.")
    @Tag(ref = "organisations")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Organisation created",
                    headers = {
                            @Header(name = LOCATION, description = "New organisation url", schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createOrganisation(@RequestBody OrganisationRequest request, @Context UriInfo uriInfo);
}
