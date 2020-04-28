package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.OrganizationResourceApi;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import fr.lunatech.timekeeper.services.dtos.OrganizationRequest;
import fr.lunatech.timekeeper.services.dtos.OrganizationResponse;
import fr.lunatech.timekeeper.services.interfaces.OrganizationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class OrganizationResource implements OrganizationResourceApi {

    @Inject
    OrganizationService organizationService;

    @RolesAllowed({"user", "admin"})
    @Override
    public List<OrganizationResponse> getAllOrganizations() {
        return organizationService.listAllOrganizations();
    }

    @RolesAllowed({"admin"})
    @Override
    public Response createOrganization(@Valid OrganizationRequest request, UriInfo uriInfo) {
        final long organizationId = organizationService.createOrganization(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(organizationId)).build();
        return Response.created(uri).build();
    }
}
