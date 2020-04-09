package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.model.Member;
import fr.lunatech.timekeeper.model.UserTK;
import fr.lunatech.timekeeper.model.enums.Profile;
import fr.lunatech.timekeeper.model.enums.Role;
import fr.lunatech.timekeeper.services.dto.MemberDto;
import fr.lunatech.timekeeper.services.dto.UserTkDto;
import fr.lunatech.timekeeper.services.exception.IllegalEntityStateException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserTkServiceImpl implements UserTkService {

    @Transactional
    @Override
    public long addUserTk(UserTkDto userTkDto) {

        UserTK userTK = new UserTK();

        userTkDto.getId().map(v -> userTK.id = v);

        userTK.setEmail(userTkDto.getEmail());
        userTK.setFirstName(userTkDto.getFirstName());
        userTK.setLastname(userTkDto.getLastname());
        userTK.setProfile(Profile.valueOf(userTkDto.getProfile()));

        UserTK.persist(userTK);
        return userTK.id;

    }

    @Override
    public Optional<UserTkDto> getUserTkById(long id) {
        Optional<UserTK> userTK = UserTK.findByIdOptional(id);
        return userTK.map(u -> new UserTkDto(Optional.of(u.id), u.getFirstName(), u.getLastname(), u.getEmail(), u.getProfile().name()));
    }

    @Transactional
    @Override
    public long addMember(MemberDto memberDto) {
        Member member = new Member();

        memberDto.getId().map(v -> member.id = v);
        Optional<UserTK> userTK = UserTK.findByIdOptional(memberDto.getUserId());
        member.setUser(userTK.orElseThrow(() -> new IllegalEntityStateException("One User is required for member " + member.id + " activity name " + memberDto.getRole())));
        member.setRole(Role.valueOf(memberDto.getRole()));

        Member.persist(member);
        return member.id;
    }

    @Override
    public Optional<MemberDto> getMemberById(long id) {
        Optional<Member> member = Member.findByIdOptional(id);

        return member.map(m -> new MemberDto(Optional.of(m.id), m.getUser().id, m.getRole().name()));
    }
}
