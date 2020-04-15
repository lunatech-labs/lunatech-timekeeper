package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.models.Activity;
import fr.lunatech.timekeeper.models.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityServiceImpl implements ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Override
    public Optional<ActivityResponse> findActivityById(Long id) {
        return Activity.<Activity>findByIdOptional(id).map(this::from);
    }

    @Transactional
    @Override
    public Long createActivity(ActivityRequest request) {
        final var activity = new Activity();
        Activity.persist(bind(activity, request));
        return activity.id;
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

    private Customer getCustomer(Long customerId) {
        return Customer.<Customer>findByIdOptional(customerId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One Customer is required for an activity. customerId=%d", customerId)));
    }
}
