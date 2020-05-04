package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.OrganizationResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.OrganizationService;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;

import javax.annotation.security.PermitAll;
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

    @RolesAllowed({"user", "admin"})
    @Override
    public List<OrganizationResponse> getAllOrganizations() {
        final var ctx = authentication.context();
        return organizationService.listAllResponses(ctx);
    }

   // @RolesAllowed({"admin"})
    @PermitAll
    @Override
    public Response createOrganization(@Valid OrganizationRequest request, UriInfo uriInfo) {
        //final var ctx = authentication.context();//TODO super admin

        final long organizationId = organizationService.create(request, null);//TODO super admin
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(organizationId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public OrganizationResponse getOrganization(Long id) {
        final var ctx = authentication.context();
        return organizationService.findResponseById(id, ctx)
                .orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"admin"})
    @Override
    public Response updateOrganization(Long id, @Valid OrganizationRequest request) {
        final var ctx = authentication.context();
        organizationService.update(id, request, ctx)
                .orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
