package fr.lunatech.timekeeper.services.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class ProjectRequest {

    @NotBlank
    private final String name;
    @NotNull
    private final Boolean billable;
    @NotNull
    private final String description;
    @NotNull
    private final Long clientId;

    @JsonbCreator
    public ProjectRequest(@NotBlank String name, @NotNull Boolean billable, @NotNull String description, @NotNull Long clientId) {
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public Boolean isBillable() {
        return billable;
    }

    public String getDescription() {
        return description;
    }

    public Long getClientId() {
        return clientId;
    }
}
