package fr.lunatech.timekeeper.models;

import fr.lunatech.timekeeper.entities.CustomerEntity;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class Customer {

    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    private String description;
    @NotNull
    private List<Long> activitiesId = emptyList();

    public Customer() {

    }

    public Customer(@NotNull Long id, @NotEmpty String name, String description, @NotNull List<Long> activitiesId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activitiesId = activitiesId;
    }

    public Customer(@NotNull CustomerEntity entity) {
        this(
                entity.id,
                entity.name,
                entity.description,
                entity.activities.stream().map(a -> a.id).collect(Collectors.toList())
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getActivitiesId() {
        return activitiesId;
    }

    @JsonbTransient
    public void setActivitiesId(List<Long> activitiesId) {
        this.activitiesId = emptyList();
    }

}
