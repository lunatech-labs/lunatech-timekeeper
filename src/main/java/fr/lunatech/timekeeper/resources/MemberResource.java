package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.MemberCreateRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.MemberUpdateRequest;
import fr.lunatech.timekeeper.models.Role;
import fr.lunatech.timekeeper.resources.apis.MemberResourceApi;
import fr.lunatech.timekeeper.services.interfaces.MemberService;

import javax.inject.Inject;
import javax.persistence.Convert;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

public class MemberResource implements MemberResourceApi {

    @Inject
    MemberService memberService;

    @Override
    public List<MemberResponse> getAllMembersOfActivity(Long activityId) {
        return memberService.listAllMembers(activityId);
    }

    @Override
    public Response addMemberToActivity(Long activityId, MemberCreateRequest request) {
        return Response.ok(memberService.createMember(activityId, request)).build();
    }

    @Override
    public MemberResponse getMember(Long activityId, Long id) {
        return memberService.findMemberById(activityId, id).orElseThrow(NotFoundException::new);
    }

    @Convert(converter = Role.Converter.class)
    @Override
    public Response updateMember(Long activityId, Long id, MemberUpdateRequest request) {
        return Response.ok(memberService.updateMember(activityId, id, request).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response removeMemberToActivity(Long activityId, Long id) {
        return Response.ok(memberService.deleteMember(activityId, id).orElse(id)).build();
    }

    //TODO ajouter ou modifier la methode addMemberToActivity pour ajouter plusieurs membres d'un seul coup
    //TODO ajouter une methode pour modifier la liste des membres d'un seul coup
}
