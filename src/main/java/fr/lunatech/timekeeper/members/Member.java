package fr.lunatech.timekeeper.members;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class Member {

    private Long id;
    @NotNull
    private Long userId;
    @Column
    @Convert(converter = Role.Converter.class)
    @NotNull
    private Role role;

    public Member() {
    }
    public Member(Long userId, Role role) {
        this(null, userId, role);
    }
    public Member(Long id, Long userId, Role role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

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
