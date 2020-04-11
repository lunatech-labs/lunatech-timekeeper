package fr.lunatech.timekeeper.customers;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;

public class CustomerMutable {

    @NotEmpty
    private String name;
    private String description;

    public CustomerMutable() {
    }

    public CustomerMutable(String name, String description) {
        this.name = name;
        this.description = description;
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

}
