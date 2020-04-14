package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.MemberUpdateRequest;
import fr.lunatech.timekeeper.models.Role;
import fr.lunatech.timekeeper.openapi.MemberResourceApi;
import fr.lunatech.timekeeper.services.MemberService;

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
    public List<MemberResponse> getAllMembersOfActivity(Long activityId) {
        return memberService.listAllMembers(activityId);
    }

    @Override
    public Response addMemberToActivity(Long activityId, @Valid MemberRequest request, UriInfo uriInfo) {
        final Long memberId = memberService.createMember(activityId, request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(memberId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public MemberResponse getMember(Long activityId, Long id) {
        return memberService.findMemberById(activityId, id).orElseThrow(NotFoundException::new);
    }

    @Convert(converter = Role.Converter.class)
    @Override
    public Response updateMember(Long activityId, Long id, @Valid MemberUpdateRequest request) {
        memberService.updateMember(activityId, id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @Override
    public Response removeMemberToActivity(Long activityId, Long id) {
        memberService.deleteMember(activityId, id);
        return Response.noContent().build();
    }

    //TODO ajouter ou modifier la methode addMemberToActivity pour ajouter plusieurs membres d'un seul coup
    //TODO ajouter une methode pour modifier la liste des membres d'un seul coup
}
