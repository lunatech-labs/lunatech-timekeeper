package fr.lunatech.timekeeper.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "roleInProject", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "project_id"})})
public class RoleInProject extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    public User user;
    @NotNull
    public Role role;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    public Project project;

    public final Boolean hasRoleInProject(Long projectId) {
        return Objects.equals(project.id, projectId);
    }

    public Boolean isSameUser(Long userId) {
        return Objects.equals(user.id, userId);
    }
}
