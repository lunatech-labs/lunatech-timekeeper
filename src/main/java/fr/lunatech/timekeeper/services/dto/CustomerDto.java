package fr.lunatech.timekeeper.services.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDto {

    private Optional<Long> id = Optional.empty();
    private String name;
    private String description;
    private List<Long> activitiesId = new ArrayList<>();

    public CustomerDto() {
    }

    public CustomerDto(Optional<Long> id, String name, String description, List<Long> activitiesId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activitiesId = activitiesId;
    }

    public Optional<Long> getId() {
        return id;
    }

    public void setId(Optional<Long> id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerDto that = (CustomerDto) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return activitiesId != null ? activitiesId.equals(that.activitiesId) : that.activitiesId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (activitiesId != null ? activitiesId.hashCode() : 0);
        return result;
    }
}
