package fr.lunatech.timekeeper.services.dto;


import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class ActivityDto {


    public ActivityDto() {
    }

    public ActivityDto(Optional<Long> id, String name, Boolean billale, String description, long customer, List<Long> members) {
        this.id = id;
        this.name = name;
        this.billale = billale;
        this.description = description;
        this.customerId = customer;
        this.members = members;
    }

    private Optional<Long> id = Optional.empty();
    private String name;
    private Boolean billale;
    private String description;
    private long customerId;
    private List<Long> members;

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

    public Boolean getBillale() {
        return billale;
    }

    public void setBillale(Boolean billale) {
        this.billale = billale;
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

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityDto that = (ActivityDto) o;
        return customerId == that.customerId &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(billale, that.billale) &&
                Objects.equals(description, that.description) &&
                Objects.equals(members, that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, billale, description, customerId, members);
    }
}
