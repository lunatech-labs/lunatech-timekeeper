package fr.lunatech.timekeeper.services.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class ProjectResponse {

    @NotNull
    private final Long id;
    @NotBlank
    private final String name;
    @NotNull
    private final Boolean billable;
    @NotNull
    private final String description;
    private final String clientName;
    @NotNull
    private final List<MemberResponse> members;
    @NotNull
    private final Long origanisationId;

    public ProjectResponse(@NotNull Long id, @NotBlank String name, @NotNull Boolean billable, @NotNull String description, @NotNull String clientName, @NotNull List<MemberResponse> members, @NotNull Long organisationId) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.clientName = clientName;
        this.members = members;
        this.origanisationId = organisationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getBillable() {
        return billable;
    }

    public String getDescription() {
        return description;
    }

    public String getClientName() {
        return clientName;
    }

    public List<MemberResponse> getMembers() {
        return members;
    }

    public Long getOriganisationId() { return origanisationId; }

    @Override
    public String toString() {
        return "ProjectResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", billable=" + billable +
                ", description='" + description + '\'' +
                ", clientName='" + clientName + '\'' +
                ", members=" + members +
                ", origanisationId=" + origanisationId +
                '}';
    }
}
