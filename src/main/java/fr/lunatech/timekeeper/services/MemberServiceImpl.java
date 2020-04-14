package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.MemberRoleRequest;
import fr.lunatech.timekeeper.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.models.Activity;
import fr.lunatech.timekeeper.models.Member;
import fr.lunatech.timekeeper.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class MemberServiceImpl implements MemberService {

    private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    public Optional<MemberResponse> findMemberById(Long activityId, Long id) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchActivityId(member, activityId))
                .map(this::from);
    }

    @Override
    public List<MemberResponse> listAllMembers(Long activityId) {
        try (final Stream<Member> members = Member.streamAll()) {
            return members.filter(member -> matchActivityId(member, activityId))
                    .map(this::from)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createMember(Long activityId, MemberRequest request) {
        final var member = new Member();
        Member.persist(bind(member, activityId, request));
        return member.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateMember(Long activityId, Long id, MemberRoleRequest request) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchActivityId(member, activityId))
                .map(member -> bind(member, activityId, request).id);
    }

    private MemberResponse from(Member member) {
        return new MemberResponse(member.id, member.user.id, member.role);
    }

    private Member bind(Member member, Long activityId, MemberRequest request) {
        member.activity = getActivity(activityId);
        member.user = getUser(request.getUserId());
        member.role = request.getRole();
        return member;
    }

    private Member bind(Member member, Long activityId, MemberRoleRequest request) {
        member.activity = getActivity(activityId);
        member.role = request.getRole();
        return member;
    }

    private Activity getActivity(Long activityId) {
        return Activity.<Activity>findByIdOptional(activityId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Activity is required for member. activityId=%s", activityId)));
    }

    private User getUser(Long userId) {
        return User.<User>findByIdOptional(userId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId=%s", userId)));
    }

    private boolean matchActivityId(Member member, Long activityId) {
        return member.activity != null && Objects.equals(member.activity.id, activityId);
    }
}
