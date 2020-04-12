package fr.lunatech.timekeeper.models;

import fr.lunatech.timekeeper.entities.ActivityEntity;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class Activity {

    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Boolean billable;
    private String description;
    @NotNull
    private Long customerId;
    @NotNull
    private List<Long> membersId = emptyList();

    public Activity() {

    }

    public Activity(@NotNull Long id, @NotNull String name, @NotNull Boolean billable, String description, @NotNull Long customerId, @NotNull List<Long> membersId) {
        this.id = id;
        this.name = name;
        this.billable = billable;
        this.description = description;
        this.customerId = customerId;
        this.membersId = membersId;
    }

    public Activity(@NotNull ActivityEntity entity) {
        this(
                entity.id,
                entity.name,
                entity.billable,
                entity.description,
                entity.customer.id,
                entity.members.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    @JsonbTransient
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @JsonbDateFormat
    public List<Long> getMembersId() {
        return membersId;
    }

    @JsonbTransient
    public void setMembersId(List<Long> membersId) {
        this.membersId = membersId;
    }
}
