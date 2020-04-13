package fr.lunatech.timekeeper.dtos;

import java.util.List;

public final class ActivityResponse {

    private final Long id;
    private final String name;
    private final Boolean billable;
    private final String description;
    private final Long customerId;
    private final List<Long> membersId;

    public ActivityResponse(Long id, String name, Boolean billable, String description, Long customerId, List<Long> membersId) {
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
