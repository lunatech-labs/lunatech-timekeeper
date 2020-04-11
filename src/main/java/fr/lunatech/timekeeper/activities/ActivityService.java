package fr.lunatech.timekeeper.activities;

import fr.lunatech.timekeeper.application.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.customers.CustomerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

@ApplicationScoped
public class ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityService.class);

    Optional<Activity> findActivityById(Long id) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id).map(this::fromEntity);
    }

    List<Activity> listAllActivities() {
        try (final Stream<ActivityEntity> entities = ActivityEntity.streamAll()) {
            return entities.map(this::fromEntity).collect(Collectors.toList());
        }
    }

    @Transactional
    Long insertActivity(ActivityMutable activity) {
        final var entity = toEntity(activity);
        ActivityEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    Optional<Long> updateActivity(Long id, ActivityMutable activity) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id).map(entity -> fillEntity(entity, activity).id);
    }

    @Transactional
    Optional<Long> deleteActivity(Long id) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id)
                .map(entity -> {
                    final Long oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private ActivityEntity toEntity(ActivityMutable activity) {
        final var entity = new ActivityEntity();
        entity.name = activity.getName();
        entity.billable = activity.isBillable();
        entity.description = activity.getDescription();
        entity.customer = customerLink(activity);
        entity.members = emptyList();
        return entity;
    }

    private ActivityEntity fillEntity(ActivityEntity entity, ActivityMutable activity) {
        entity.name = activity.getName();
        entity.billable = activity.isBillable();
        entity.description = activity.getDescription();
        entity.customer = customerLink(activity);
        entity.members = emptyList();
        return entity;
    }

    private Activity fromEntity(ActivityEntity entity) {
        return new Activity(
                entity.id,
                entity.name,
                entity.billable,
                entity.description,
                entity.customer.id,
                entity.members != null ? entity.members.stream().map(a -> a.id).collect(Collectors.toList()) : emptyList()
        );
    }

    private CustomerEntity customerLink(ActivityMutable activity) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(activity.getCustomerId())
                .orElseThrow(() -> new IllegalEntityStateException("One Customer is required for an activity. CustomerId=" + activity.getCustomerId() + " - activityName=" + activity.getName()));
    }

    //TODO remove si pas utile (ce qui doit etre le cas)
    /*private List<MemberEntity> membersLink(ActivityMutable activity) {
        if (isNotEmpty(activity.getMembersId())) {
            return activity.getMembersId()
                    .stream()
                    .map(memberId -> MemberEntity.<MemberEntity>findByIdOptional(memberId)
                            .orElseThrow(() -> new IllegalEntityStateException("MemberId : " + memberId + " doesn't corresponding to any member"))
                    )
                    .collect(Collectors.toList());
        } else {
            return emptyList();
        }
    }*/
}
