package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityCreateRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.dtos.ActivityUpdateRequest;
import fr.lunatech.timekeeper.models.Activity;
import fr.lunatech.timekeeper.models.Customer;
import fr.lunatech.timekeeper.models.errors.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.interfaces.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ActivityServiceImpl implements ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Override
    public Optional<ActivityResponse> findActivityById(Long id) {
        return Activity.<Activity>findByIdOptional(id).map(this::response);
    }

    @Override
    public List<ActivityResponse> listAllActivities() {
        try (final Stream<Activity> activities = Activity.streamAll()) {
            return activities.map(this::response).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createActivity(ActivityCreateRequest request) {
        final var activity = new Activity();
        Activity.persist(bind(activity, request));
        return activity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateActivity(Long id, ActivityUpdateRequest request) {
        return Activity.<Activity>findByIdOptional(id).map(activity -> bind(activity, request).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteActivity(Long id) {
        return Activity.<Activity>findByIdOptional(id)
                .map(activity -> {
                    final Long oldId = activity.id;
                    if (activity.isPersistent()) {
                        activity.delete();
                    }
                    return oldId;
                });
    }

    public ActivityResponse response(Activity activity) {
        return new ActivityResponse(
                activity.id,
                activity.name,
                activity.billable,
                activity.description,
                activity.customer.id,
                activity.members.stream().map(m -> m.id).collect(Collectors.toList())
        );
    }

    public Activity bind(Activity activity, ActivityCreateRequest request)  {
        activity.name = request.getName();
        activity.billable = request.isBillable();
        activity.description = request.getDescription();
        activity.customer = getCustomerEntity(request.getCustomerId());
        return activity;
    }

    public Activity bind(Activity activity, ActivityUpdateRequest request)  {
        activity.name = request.getName();
        activity.billable = request.isBillable();
        activity.description = request.getDescription();
        activity.customer = getCustomerEntity(request.getCustomerId());
        return activity;
    }

    private static Customer getCustomerEntity(Long customerId) {
        return Customer.<Customer>findByIdOptional(customerId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Customer is required for an activity. customerId=%d", customerId)));
    }
}
