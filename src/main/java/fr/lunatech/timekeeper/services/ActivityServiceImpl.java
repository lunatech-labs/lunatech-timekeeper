package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.models.Activity;
import fr.lunatech.timekeeper.models.Customer;
import fr.lunatech.timekeeper.services.exception.IllegalEntityStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityServiceImpl implements ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Inject
    CustomerService customerService;

    @Transactional
    @Override
    public long addActivity(ActivityRequest request) {
        Activity act = from(request);
        Activity.persist(act);
        return act.id;
    }

    @Override
    public Optional<ActivityResponse> getActivityById(long id) {

        Optional<Activity> activity = Activity.findByIdOptional(id);

        return activity.map(this::from);
    }

    private ActivityResponse from(Activity activity) {
        return new ActivityResponse(
                activity.id, activity.name, activity.billable, activity.description, activity.customer.id, activity.members.stream().map(m -> m.id).collect(Collectors.toList()));
    }

    private Activity from(ActivityRequest request) {
        final Activity activity = new Activity();

        activity.name = request.getName();
        activity.description = request.getDescription();
        activity.billable = request.isBillable();

        Optional<Customer> c = Customer.findByIdOptional(request.getCustomerId());
        activity.customer = c.orElseThrow(() -> new IllegalEntityStateException("One Customer is required for activity " + activity.id + " activity name " + activity.name));

        return activity;
    }
}
