package fr.lunatech.timekeeper.activities;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.util.List;

public class Activity extends ActivityMutable {

    @NotNull
    private Long id;
    @JsonbDateFormat
    @NotNull
    private List<Long> membersId;

    public Activity() {
        super();
    }

    public Activity(Long id, String name, Boolean billable, String description, Long customerId, List<Long> membersId) {
        super(name, billable, description, customerId);
        this.id = id;
        this.membersId = membersId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getMembersId() {
        return membersId;
    }

    public void setMembersId(List<Long> membersId) {
        this.membersId = membersId;
    }
}
