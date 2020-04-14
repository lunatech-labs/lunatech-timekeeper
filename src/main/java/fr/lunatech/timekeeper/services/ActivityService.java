package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    List<ActivityResponse> listAllActivities();
    Long createActivity(ActivityRequest request);
    Optional<ActivityResponse> findActivityById(Long id);
    Optional<Long> updateActivity(Long id, ActivityRequest activity);
}
