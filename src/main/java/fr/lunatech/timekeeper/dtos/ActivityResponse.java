package fr.lunatech.timekeeper.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class ActivityResponse {

    @NotNull
    private final Long id;
    @NotBlank
    private final String name;
    @NotNull
    private final Boolean billable;
    @NotNull
    private final String description;
    @NotNull
    private final Long customerId;
    @NotNull
    private final List<Long> membersId;

    public ActivityResponse(@NotNull Long id, @NotBlank String name, @NotNull Boolean billable, @NotNull String description, @NotNull Long customerId, @NotNull List<Long> membersId) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.customerId = customerId;
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

    public Long getCustomerId() {
        return customerId;
    }

    public List<Long> getMembersId() {
        return membersId;
    }
}
