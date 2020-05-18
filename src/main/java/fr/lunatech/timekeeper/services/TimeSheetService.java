package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.time.TimeSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TimeSheetService {

    Optional<TimeSheet> findById(Long id) {
        return TimeSheet.findByIdOptional(id);
    }

}
