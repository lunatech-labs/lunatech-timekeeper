package fr.lunatech.timekeeper.activities;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


public class Activity {

    private Long id;
    @NotNull
    private String name;
    private Boolean billable;
    private String description;
    @NotNull
    private Long customerId;
    private List<Long> members;
    public Activity() {
    }
    public Activity(Long id, String name, Boolean billable, String description, long customer, List<Long> members) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.customerId = customer;
        this.members = members;
    }
    public Activity(String name, Boolean billable, String description, long customer, List<Long> members) {
        this(null, name, billable, description, customer, members);
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

    public Boolean isBillable() {
        return billable;
    }

    public void setBillable(Boolean billable) {
        this.billable = billable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
}
