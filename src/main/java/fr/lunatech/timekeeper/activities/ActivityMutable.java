package fr.lunatech.timekeeper.activities;

import javax.validation.constraints.NotNull;


public class ActivityMutable {

    @NotNull
    private String name;
    @NotNull
    private Boolean billable;
    private String description;
    @NotNull
    private Long customerId;

    public ActivityMutable() {
    }

    public ActivityMutable(String name, Boolean billable, String description, Long customerId) {
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.customerId = customerId;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
