package fr.lunatech.timekeeper.members;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class MemberMutable {

    @NotNull
    private Long userId;
    @Column
    @Convert(converter = Role.Converter.class)
    @NotNull
    private Role role;

    public MemberMutable() {
    }
    public MemberMutable(Long userId, Role role) {
        this.userId = userId;
        this.role = role;
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
