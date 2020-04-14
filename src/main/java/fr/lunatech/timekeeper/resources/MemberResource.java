package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.openapi.MemberResourceApi;
import fr.lunatech.timekeeper.services.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class MemberResource implements MemberResourceApi {

    @Inject
    UserService userService;

    @Override
    public Response addMemberToActivity(Long activityId, @Valid MemberRequest request, UriInfo uriInfo) {
        final Long memberId = userService.addMember(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(memberId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public MemberResponse getMember(Long activityId, Long id) {
        return userService.getMemberById(id).orElseThrow(NotFoundException::new);
    }

}
