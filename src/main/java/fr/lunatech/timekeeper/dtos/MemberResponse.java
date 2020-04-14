package fr.lunatech.timekeeper.dtos;

import fr.lunatech.timekeeper.models.Role;

public final class MemberResponse {

    private final Long id;
    private final Long userId;
    private final Role role;

    public MemberResponse(Long id, Long userId, Role role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }
}
