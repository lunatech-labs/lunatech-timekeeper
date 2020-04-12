package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.application.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.entities.ActivityEntity;
import fr.lunatech.timekeeper.entities.MemberEntity;
import fr.lunatech.timekeeper.entities.UserEntity;
import fr.lunatech.timekeeper.models.Member;
import fr.lunatech.timekeeper.models.Role;
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
                .map(Member::new);
    }

    @Override
    public List<Member> listAllMembers(Long activityId) {
        try (final Stream<MemberEntity> entities = MemberEntity.streamAll()) {
            return entities.filter(entity -> matchActivityId(entity, activityId))
                    .map(Member::new)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertMember(Long activityId, Member member) {
        final var entity = new MemberEntity();
        MemberEntity.persist(bind(entity, activityId, member));
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

    private static MemberEntity bind(MemberEntity entity, Long activityId, Member member)  {
        entity.user = getUserEntity(member);
        entity.activity = getActivityEntity(activityId, member);
        entity.role = member.getRole();
        return entity;
    }


    private static ActivityEntity getActivityEntity(Long activityId, Member member) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(activityId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Activity is required for member. activityId=%s - userId = %s", activityId, member.getUserId())));
    }

    private static UserEntity getUserEntity(Member member) {
        return UserEntity.<UserEntity>findByIdOptional(member.getUserId())
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId %s", member.getUserId())));
    }

    private boolean matchActivityId(MemberEntity entity, Long activityId) {
        return entity.activity != null && Objects.equals(entity.activity.id, activityId);
    }
}
