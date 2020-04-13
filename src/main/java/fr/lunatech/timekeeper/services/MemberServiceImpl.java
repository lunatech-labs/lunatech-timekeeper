package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.MemberCreateRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.MemberUpdateRequest;
import fr.lunatech.timekeeper.models.Activity;
import fr.lunatech.timekeeper.models.Member;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.models.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.interfaces.MemberService;
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
                .map(this::response);
    }

    @Override
    public List<MemberResponse> listAllMembers(Long activityId) {
        try (final Stream<Member> members = Member.streamAll()) {
            return members.filter(member -> matchActivityId(member, activityId))
                    .map(this::response)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createMember(Long activityId, MemberCreateRequest request) {
        final var member = new Member();
        Member.persist(bind(member, activityId, request));
        return member.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateMember(Long activityId, Long id, MemberUpdateRequest request) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchActivityId(member, activityId))
                .map(member -> bind(member, activityId, request).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteMember(Long activityId, Long id) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchActivityId(member, activityId))
                .map(member -> {
                    final var oldId = member.id;
                    if (member.isPersistent()) {
                        member.delete();
                    }
                    return oldId;
                });
    }

    public MemberResponse response(Member member) {
        return new MemberResponse(member.id, member.user.id, member.role);
    }

    public Member bind(Member member, Long activityId, MemberCreateRequest request) {
        member.activity = getActivityEntity(activityId);
        member.user = getUserEntity(request.getUserId());
        member.role = request.getRole();
        return member;
    }

    public Member bind(Member member, Long activityId, MemberUpdateRequest request) {
        member.activity = getActivityEntity(activityId);
        member.role = request.getRole();
        return member;
    }

    private static Activity getActivityEntity(Long activityId) {
        return Activity.<Activity>findByIdOptional(activityId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Activity is required for member. activityId=%s", activityId)));
    }

    private static User getUserEntity(Long userId) {
        return User.<User>findByIdOptional(userId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId=%s", userId)));
    }

    private boolean matchActivityId(Member member, Long activityId) {
        return member.activity != null && Objects.equals(member.activity.id, activityId);
    }
}
