package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.MemberBisResourceApi;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.interfaces.MemberService;

import javax.inject.Inject;
import java.util.List;

//TODO FIX TEMPORAIRE
public class MemberBisResource implements MemberBisResourceApi {

    @Inject
    MemberService memberService;

    @Override
    public List<MemberResponse> getAllMembers() {
        return memberService.listAllMembers();
    }
}
