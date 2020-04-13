package fr.lunatech.timekeeper.dtos;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class ActivityCreateRequest {

    @NotBlank
    private final String name;
    @NotNull
    private final Boolean billable;
    @NotNull
    private final String description;
    @NotNull
    private final Long customerId;

    @JsonbCreator
    public ActivityCreateRequest(@NotBlank String name, @NotNull Boolean billable, @NotNull String description, @NotNull Long customerId) {
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.customerId = customerId;
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

    public Long getCustomerId() {
        return customerId;
    }
}
