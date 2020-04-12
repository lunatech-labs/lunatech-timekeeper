package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Activity;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    Optional<Activity> findActivityById(Long id);
    List<Activity> listAllActivities();
    Long insertActivity(Activity activity);
    Optional<Long> updateActivity(Long id, Activity activity);
    Optional<Long> deleteActivity(Long id);
}
