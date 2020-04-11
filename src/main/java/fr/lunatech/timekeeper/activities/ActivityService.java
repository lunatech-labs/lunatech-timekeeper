package fr.lunatech.timekeeper.activities;

import fr.lunatech.timekeeper.application.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.customers.CustomerEntity;
import fr.lunatech.timekeeper.members.MemberEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@ApplicationScoped
public class ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityService.class);

    Optional<Activity> findActivityById(Long id) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id).map(this::fromEntity);
    }

    List<Activity> listAllActivities() {
        try (final Stream<ActivityEntity> entities = ActivityEntity.streamAll()) {
            return entities.map(this::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    Long insertActivity(Activity activity) {
        final ActivityEntity entity = toEntity(activity);
        ActivityEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    Optional<Long> updateActivity(Long id, Activity activity) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id)
                .map(entity -> fillEntity(entity, activity).id);
    }

    @Transactional
    Optional<Long> deleteActivity(Long id) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id)
                .map(entity -> {
                    final Long oldId = entity.id;
                    if(entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private ActivityEntity toEntity(Activity activity) {
        final ActivityEntity entity = new ActivityEntity();
        entity.id = activity.getId().orElse(null);
        entity.name = activity.getName();
        entity.billable = activity.isBillable();
        entity.description = activity.getDescription();
        entity.customer = customerLink(activity);
        entity.members = membersLink(activity);
        return entity;
    }

    private ActivityEntity fillEntity(ActivityEntity entity, Activity activity) {
        entity.name = activity.getName();
        entity.billable = activity.isBillable();
        entity.description = activity.getDescription();
        entity.customer = customerLink(activity);
        entity.members = membersLink(activity);
        return entity;
    }

    private Activity fromEntity(ActivityEntity entity) {
        return new Activity(
                entity.id,
                entity.name,
                entity.billable,
                entity.description,
                entity.customer.id,
                entity.members.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    private CustomerEntity customerLink(Activity activity) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(activity.getCustomerId())
                .orElseThrow(() -> new IllegalEntityStateException("One Customer is required for activity " + activity.getId() + " activity name " + activity.getName()));
    }

    private List<MemberEntity> membersLink(Activity activity) {
        if(isNotEmpty(activity.getMembers())) {
            return activity.getMembers()
                    .stream()
                    .map(memberId -> MemberEntity.<MemberEntity>findByIdOptional(memberId)
                            .orElseThrow(() -> new IllegalEntityStateException("MemberId : " + memberId + " doesn't corresponding to any member"))
                    )
                    .collect(Collectors.toList());
        } else {
            return emptyList();
        }
    }
}
