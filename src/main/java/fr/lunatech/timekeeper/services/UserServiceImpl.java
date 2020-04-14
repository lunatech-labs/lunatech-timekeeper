package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.models.Member;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.exception.IllegalEntityStateException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Transactional
    @Override
    public long addUser(UserRequest request) {

        User user = new User();
        user.email = request.getEmail();
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.profiles = request.getProfiles();

        User.persist(user);
        return user.id;

    }

    @Override
    public Optional<UserResponse> getUserById(long id) {
        Optional<User> userTK = User.findByIdOptional(id);
        return userTK.map(u -> new UserResponse(u.id, u.firstName, u.lastName, u.email, u.profiles));
    }

    @Transactional
    @Override
    public long addMember(MemberRequest request) {
        Member member = new Member();
        Optional<User> user = User.findByIdOptional(request.getUserId());
        member.user = user.orElseThrow(() -> new IllegalEntityStateException("One User is required for member " + member.id + " activity name " + request.getRole()));
        member.role = request.getRole();

        Member.persist(member);
        return member.id;
    }

    @Override
    public Optional<MemberResponse> getMemberById(long id) {
        Optional<Member> member = Member.findByIdOptional(id);
        return member.map(m -> new MemberResponse(m.id, m.user.id, m.role));
    }
}
