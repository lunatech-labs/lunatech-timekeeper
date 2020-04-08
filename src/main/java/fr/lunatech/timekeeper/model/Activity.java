package fr.lunatech.timekeeper.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Activity extends PanacheEntity {

    public Activity() {
    }

    private String name;
    private Boolean billale;
    private String description;

    @ManyToOne
    private Customer customer;

    @OneToMany
    private List<Member> members;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
}
