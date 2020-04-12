package fr.lunatech.timekeeper.models;

import fr.lunatech.timekeeper.entities.MemberEntity;

import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotNull;

public class Member {

    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private Role role;

    public Member() {

    }

    public Member(@NotNull Long id, @NotNull Long userId, @NotNull Role role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    public Member(@NotNull MemberEntity entity) {
        this(entity.id, entity.user.id, entity.role);
    }

    public Long getId() {
        return id;
    }

    @JsonbTransient
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
