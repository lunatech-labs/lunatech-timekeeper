package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.model.Activity;
import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.model.Member;
import fr.lunatech.timekeeper.services.dto.ActivityDto;
import fr.lunatech.timekeeper.services.exception.IllegalEntityStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ActivityServiceImpl implements ActivityService {

    private static Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Inject
    CustomerService customerService;

    @Transactional
    @Override
    public long addActivity(ActivityDto activityDto) {
        Activity act = from(activityDto);
        Activity.persist(act);
        return act.id;
    }

    @Override
    public Optional<ActivityDto> getActivityById(long id) {

        Optional<Activity> activity = Activity.findByIdOptional(id);

        return activity.map(this::from);
    }

    private ActivityDto from(Activity activity) {
        return new ActivityDto(
                Optional.of(activity.id), activity.getName(), activity.getBillale(), activity.getDescription(), activity.getCustomer().id, activity.getMembers().stream().map(m -> m.id).collect(Collectors.toList()));
    }

    private Activity from(ActivityDto activityDto) {
        final Activity activity = new Activity();

        activityDto.getId().map(v -> activity.id = v);
        activity.setName(activityDto.getName());
        activity.setBillale(activityDto.getBillale());

        Customer c = customerService.getCustomerById(activityDto.getCustomerId())
                .orElseThrow(() -> new IllegalEntityStateException("One Customer is required for activity " + activity.id + " activity name " + activityDto.getName()));

        activity.setCustomer(c);

        List<Member> members = activityDto.getMembers().stream().map(memberId -> {
                    Optional<Member> m = Member.findByIdOptional(memberId);
                    return m.orElseThrow(() -> new IllegalEntityStateException("MemberId : " + memberId + " doesn't corresponding to any member"));
                }
        ).collect(Collectors.toList());

        activity.setMembers(members);

        return activity;
    }
}
