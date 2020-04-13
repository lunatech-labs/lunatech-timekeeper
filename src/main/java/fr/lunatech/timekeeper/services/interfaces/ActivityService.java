package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.dtos.ActivityCreateRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.dtos.ActivityUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ActivityService {
    Optional<ActivityResponse> findActivityById(Long id);
    List<ActivityResponse> listAllActivities();
    Long createActivity(ActivityCreateRequest activity);
    Optional<Long> updateActivity(Long id, ActivityUpdateRequest activity);
    Optional<Long> deleteActivity(Long id);
}
