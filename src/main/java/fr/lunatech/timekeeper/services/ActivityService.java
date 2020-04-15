package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;

import java.util.Optional;

public interface ActivityService {
    Long createActivity(ActivityRequest request);
    Optional<ActivityResponse> findActivityById(Long id);
    Optional<Long> updateActivity(Long id, ActivityRequest activity);
}
