package fr.lunatech.timekeeper.activities;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    Optional<Activity> findActivityById(Long id);
    List<Activity> listAllActivities();
    Long insertActivity(ActivityMutable activity);
    Optional<Long> updateActivity(Long id, ActivityMutable activity);
    Optional<Long> deleteActivity(Long id);
}
