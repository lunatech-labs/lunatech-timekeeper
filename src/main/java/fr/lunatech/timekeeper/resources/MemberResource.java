package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.MemberRequest;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.dtos.MemberUpdateRequest;
import fr.lunatech.timekeeper.models.Role;
import fr.lunatech.timekeeper.resources.openapi.MemberResourceApi;
import fr.lunatech.timekeeper.services.interfaces.MemberService;

import javax.inject.Inject;
import javax.persistence.Convert;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class MemberResource implements MemberResourceApi {

    @Inject
    MemberService memberService;

    @Override
    public List<MemberResponse> getAllMembersOfProject(Long projectId) {
        return memberService.listAllMembers(projectId);
    }

    @Override
    public Response addMemberToProject(Long projectId, @Valid MemberRequest request, UriInfo uriInfo) {
        final Long memberId = memberService.createMember(projectId, request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(memberId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public MemberResponse getMember(Long projectId, Long id) {
        return memberService.findMemberById(projectId, id).orElseThrow(NotFoundException::new);
    }

    @Convert(converter = Role.Converter.class)
    @Override
    public Response updateMember(Long projectId, Long id, @Valid MemberUpdateRequest request) {
        memberService.updateMember(projectId, id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @Override
    public Response removeMemberToProject(Long projectId, Long id) {
        memberService.deleteMember(projectId, id);
        return Response.noContent().build();
    }

    //TODO add or modify addMemberToproject method to add multiple users at the same time
    //TODO add a method to update all project members at the same time
}
