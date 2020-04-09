package fr.lunatech.timekeeper.services.dto;

import fr.lunatech.timekeeper.model.enums.Role;

import java.util.Objects;
import java.util.Optional;

public class MemberDto {

    public MemberDto() {
    }

    public MemberDto(Optional<Long> id, Long userId, String role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    private Optional<Long> id = Optional.empty();
    private Long userId;
    private String role;


    public Optional<Long> getId() {
        return id;
    }

    public void setId(Optional<Long> id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDto that = (MemberDto) o;
        return Objects.equals(userId, that.userId) &&
                role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role);
    }
}
