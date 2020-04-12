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
public class MemberServiceImpl implements MemberService {

    private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    public Optional<Member> findMemberById(Long activityId, Long id) {
        return MemberEntity.<MemberEntity>findByIdOptional(id)
                .filter(entity -> matchActivityId(entity, activityId))
                .map(this::fromEntity);
    }

    @Override
    public List<Member> listAllMembers(Long activityId) {
        try (final Stream<MemberEntity> entities = MemberEntity.streamAll()) {
            return entities.filter(entity -> matchActivityId(entity, activityId))
                    .map(this::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertMember(Long activityId, MemberMutable member) {
        final var entity = toEntity(activityId, member);
        MemberEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    @Override
    public Optional<Long> changeRole(Long activityId, Long id, Role role) {
        return MemberEntity.<MemberEntity>findByIdOptional(id)
                .filter(entity -> matchActivityId(entity, activityId))
                .map(entity -> {
                    entity.role = role;
                    return entity.id;
                });
    }

    @Transactional
    @Override
    public Optional<Long> deleteMember(Long activityId, Long id) {
        return MemberEntity.<MemberEntity>findByIdOptional(id)
                .filter(entity -> matchActivityId(entity, activityId))
                .map(entity -> {
                    final var oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private MemberEntity toEntity(Long activityId, MemberMutable member) {
        final var entity = new MemberEntity();
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

    private ActivityEntity activityLink(Long activityId, MemberMutable member) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(activityId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Activity is required for member. activityId=%s - userId = %s", activityId, member.getUserId())));
    }

    private UserEntity userLink(MemberMutable member) {
        return UserEntity.<UserEntity>findByIdOptional(member.getUserId())
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId %s", member.getUserId())));
    }

    private boolean matchActivityId(MemberEntity entity, Long activityId) {
        return entity.activity != null && Objects.equals(entity.activity.id, activityId);
    }
}
