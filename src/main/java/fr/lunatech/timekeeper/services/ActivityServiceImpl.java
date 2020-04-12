package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.application.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.entities.ActivityEntity;
import fr.lunatech.timekeeper.entities.CustomerEntity;
import fr.lunatech.timekeeper.models.Activity;
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
public class ActivityServiceImpl implements ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Override
    public Optional<Activity> findActivityById(Long id) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id).map(Activity::new);
    }

    @Override
    public List<Activity> listAllActivities() {
        try (final Stream<ActivityEntity> entities = ActivityEntity.streamAll()) {
            return entities.map(Activity::new).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertActivity(Activity activity) {
        final var entity = new ActivityEntity();
        ActivityEntity.persist(bind(entity, activity));
        return entity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateActivity(Long id, Activity activity) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id).map(entity -> bind(entity, activity).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteActivity(Long id) {
        return ActivityEntity.<ActivityEntity>findByIdOptional(id)
                .map(entity -> {
                    final Long oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private static ActivityEntity bind(ActivityEntity entity, Activity activity)  {
        entity.name = activity.getName();
        entity.billable = activity.isBillable();
        entity.description = activity.getDescription();
        entity.customer = getCustomerEntity(activity);
        entity.members = emptyList();
        return entity;
    }

    private static CustomerEntity getCustomerEntity(Activity activity) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(activity.getCustomerId())
                .orElseThrow(() -> new IllegalEntityStateException("One Customer is required for an activity. CustomerId=" + activity.getCustomerId() + " - activityName=" + activity.getName()));
    }
}
