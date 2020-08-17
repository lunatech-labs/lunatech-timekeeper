package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.time.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserEventService {

    private static final Logger logger = LoggerFactory.getLogger(EventTemplateService.class);

    public List<UserEvent> getEventsByUser(Long ownerId){
        logger.debug("getEventsForUser {}", ownerId);
        return UserEvent.<UserEvent>stream("owner_id=?1", ownerId)
                .collect(Collectors.toList());
    }
}
