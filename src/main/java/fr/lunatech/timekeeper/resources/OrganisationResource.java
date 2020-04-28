package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.OrganisationResourceApi;
import fr.lunatech.timekeeper.services.dtos.OrganisationRequest;
import fr.lunatech.timekeeper.services.interfaces.OrganisationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class OrganisationResource implements OrganisationResourceApi {

    @Inject
    OrganisationService organisationService;

    @RolesAllowed({"user", "admin"})
    @Override
    public Response createOrganisation(@Valid OrganisationRequest request, UriInfo uriInfo) {
        final long organisationId = organisationService.createOrganisation(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(organisationId)).build();
        return Response.created(uri).build();
    }
}
