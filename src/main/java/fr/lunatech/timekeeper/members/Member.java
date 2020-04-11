package fr.lunatech.timekeeper.members;

import javax.validation.constraints.NotNull;

public class Member extends MemberMutable {

    @NotNull
    private Long id;

    public Member() {
        super();
    }
    public Member(Long id, Long userId, Role role) {
        super(userId, role);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
