package fr.lunatech.timekeeper.services.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class MembersUpdateRequest {

    @NotNull
    private final List<MemberRequest> members;

    @JsonbCreator
    public MembersUpdateRequest(@NotNull List<MemberRequest> members) {
        this.members = members;
    }

    public List<MemberRequest> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "MembersUpdateRequest{" +
                "members=" + members +
                '}';
    }
}
