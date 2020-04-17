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
    @NotNull
    private final Long clientId;
    @NotNull
    private final List<Long> membersId;

    public ProjectResponse(@NotNull Long id, @NotBlank String name, @NotNull Boolean billable, @NotNull String description, @NotNull Long clientId, @NotNull List<Long> membersId) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.clientId = clientId;
        this.membersId = membersId;
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

    public Long getClientId() {
        return clientId;
    }

    public List<Long> getMembersId() {
        return membersId;
    }
}
