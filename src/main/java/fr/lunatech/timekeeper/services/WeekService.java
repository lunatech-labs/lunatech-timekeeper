package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.services.responses.WeekResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class WeekService {
    private static Logger logger = LoggerFactory.getLogger(WeekService.class);

    public Optional<WeekResponse> getCurrentWeek(Long userId) {

        // TODO
        logger.warn("TODO getCurrentWeek");
        //return Week.find("users", userId);


        return Optional.empty();

    }
}
