package fr.lunatech.timekeeper.customers;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Customer extends CustomerMutable {

    @NotNull
    private Long id;
    private List<Long> activitiesId;

    public Customer() {
    }

    public Customer(Long id, String name, String description, List<Long> activitiesId) {
        super(name, description);
        this.id = id;
        this.activitiesId = activitiesId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(List<Long> activitiesId) {
        this.activitiesId = activitiesId;
    }
}
