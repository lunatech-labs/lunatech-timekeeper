package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.models.Activity;
import fr.lunatech.timekeeper.models.Customer;
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
        return Activity.<Activity>findByIdOptional(id).map(this::from);
    }

    @Override
    public List<ActivityResponse> listAllActivities() {
        try (final Stream<Activity> activities = Activity.streamAll()) {
            return activities.map(this::from).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createActivity(ActivityRequest request) {
        final var activity = bind(request);
        Activity.persist(request);
        return activity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateActivity(Long id, ActivityRequest request) {
        return Activity.<Activity>findByIdOptional(id).map(activity -> bind(activity, request).id);
    }

    private ActivityResponse from(Activity activity) {
        return new ActivityResponse(
                activity.id,
                activity.name,
                activity.billable,
                activity.description,
                activity.customer.id,
                activity.members.stream().map(m -> m.id).collect(Collectors.toList())
        );
    }

    private Activity bind(Activity activity, ActivityRequest request) {
        activity.name = request.getName();
        activity.billable = request.isBillable();
        activity.description = request.getDescription();
        activity.customer = getCustomer(request.getCustomerId());
        return activity;
    }

    private Activity bind(ActivityRequest request) {
        return bind(new Activity(), request);
    }

    private Customer getCustomer(Long customerId) {
        return Customer.<Customer>findByIdOptional(customerId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Customer is required for an activity. customerId=%d", customerId)));
    }
}
