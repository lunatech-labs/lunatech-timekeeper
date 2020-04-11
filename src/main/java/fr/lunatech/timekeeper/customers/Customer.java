package fr.lunatech.timekeeper.customers;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

public class Customer {

    private Long id;
    @NotEmpty
    private String name;
    private String description;
    private List<Long> activitiesId;

    public Customer() {
    }

    public Customer(Long id, String name, String description, List<Long> activitiesId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activitiesId = activitiesId;
    }

    public Customer(String name, String description, List<Long> activitiesId) {
        this(null, name, description, activitiesId);
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(List<Long> activitiesId) {
        this.activitiesId = activitiesId;
    }

}
