package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.ActivityRequest;
import fr.lunatech.timekeeper.services.dtos.ActivityResponse;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    List<ActivityResponse> listAllActivities();
    Long createActivity(ActivityRequest request);
    Optional<ActivityResponse> findActivityById(Long id);
    Optional<Long> updateActivity(Long id, ActivityRequest activity);
}
