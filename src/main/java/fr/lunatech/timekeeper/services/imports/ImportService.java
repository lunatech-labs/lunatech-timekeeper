package fr.lunatech.timekeeper.services.imports;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;

@Named("ImportServiceBean")
public class ImportService {

    private static Logger logger = LoggerFactory.getLogger(ImportService.class);

    @Transactional
    public void persistClients(List<Client> clients){
        clients.forEach(PanacheEntityBase::persistAndFlush);
    }

    @Transactional
    public void persistProjects(List<Project> projects){
        projects.forEach(PanacheEntityBase::persistAndFlush);
    }

    @Transactional
    public void persistUsers(List<User> users){
        users.forEach(PanacheEntityBase::persistAndFlush);
    }

}
