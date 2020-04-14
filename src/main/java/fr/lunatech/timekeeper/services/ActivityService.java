package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;

import java.util.Optional;

public interface ActivityService {


    long addActivity(ActivityRequest request);

    Optional<ActivityResponse> getActivityById(long id);


}
