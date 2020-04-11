package fr.lunatech.timekeeper.members;

import fr.lunatech.timekeeper.activities.ActivityEntity;
import fr.lunatech.timekeeper.application.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.users.UserEntity;
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
public class MemberService {

    private static Logger logger = LoggerFactory.getLogger(MemberService.class);

    Optional<Member> findMemberById(Long activityId, Long id) {
        return MemberEntity.<MemberEntity>findByIdOptional(id)
                .filter(entity -> matchActivityId(entity, activityId))
                .map(this::fromEntity);
    }

    List<Member> listAllMembers(Long activityId) {
        try (final Stream<MemberEntity> entities = MemberEntity.streamAll()) {
            return entities.filter(entity -> matchActivityId(entity, activityId))
                    .map(this::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    long insertMember(Long activityId, Member member) {
        final MemberEntity entity = toEntity(activityId, member);
        MemberEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    Optional<Long> updateMember(Long activityId, Long id, Member member) {
        return MemberEntity.<MemberEntity>findByIdOptional(id)
                .filter(entity -> matchActivityId(entity, activityId))
                .map(entity -> fillEntity(activityId, entity, member).id);
    }

    @Transactional
    Optional<Long> deleteMember(Long activityId, Long id) {
        return MemberEntity.<MemberEntity>findByIdOptional(id)
                .filter(entity -> matchActivityId(entity, activityId))
                .map(entity -> {
                    final Long oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private MemberEntity toEntity(Long activityId, Member member) {
        final MemberEntity entity = new MemberEntity();
        entity.id = member.getId().orElse(null);
        entity.user = userLink(member);
        entity.activity = activityLink(activityId, member);
        entity.role = member.getRole();
        return entity;
    }

    private MemberEntity fillEntity(Long activityId, MemberEntity entity, Member member) {
        entity.user = userLink(member);
        entity.activity = activityLink(activityId, member);
        entity.role = member.getRole();
        return entity;
    }

    private Member fromEntity(MemberEntity entity) {
        return new Member(
                entity.id,
                entity.user.id,
                entity.role
        );
    }

    private ActivityEntity activityLink(Long activityId, Member member) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(activityId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Activity is required for member %s with activityId %s", member.getId(), activityId)));
    }

    private UserEntity userLink(Member member) {
        return UserEntity.<UserEntity>findByIdOptional(member.getUserId())
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member %s with userId %s", member.getId(), member.getUserId())));
    }

    private boolean matchActivityId(MemberEntity entity, Long activityId) {
        return entity.activity != null && Objects.equals(entity.activity.id, activityId);
    }
}
