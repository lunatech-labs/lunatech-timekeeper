package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.services.dto.*;


import java.util.Optional;

public interface ActivityService {


    long addActivity(ActivityDto activity);

    Optional<ActivityDto> getActivityById(long id);


}
