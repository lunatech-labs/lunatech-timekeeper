package fr.lunatech.timekeeper.dtos;

import java.util.List;

public final class CustomerResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final List<Long> activitiesId;

    public CustomerResponse(Long id, String name, String description, List<Long> activitiesId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activitiesId = activitiesId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getActivitiesId() {
        return activitiesId;
    }
}
