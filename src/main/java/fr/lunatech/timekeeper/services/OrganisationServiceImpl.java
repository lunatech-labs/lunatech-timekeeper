package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Organisation;
import fr.lunatech.timekeeper.services.dtos.OrganisationRequest;
import fr.lunatech.timekeeper.services.dtos.OrganisationResponse;
import fr.lunatech.timekeeper.services.interfaces.OrganisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class OrganisationServiceImpl implements OrganisationService {

    private static Logger logger = LoggerFactory.getLogger(OrganisationServiceImpl.class);


    @Override
    public Optional<OrganisationResponse> findOrganisationById(Long id) {
        return Organisation.<Organisation>findByIdOptional(id).map(this::bind);
    }

    @Override
    public Optional<OrganisationResponse> findOrganisationByTokenName(String tokenName) {
        return Organisation.<Organisation>find("tokenName", tokenName).firstResultOptional().map(this::bind);
    }

    @Transactional
    @Override
    public List<OrganisationResponse> listAllOrganisations() {
        try (final Stream<Organisation> organisations = Organisation.streamAll()) {
            return organisations.map(this::bind).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createOrganisation(OrganisationRequest request) {
        logger.info("Create a new organisation with " + request);
        Organisation organisation = unbind(request);

        organisation.persist();

        return organisation.id;
    }

    private OrganisationResponse bind(Organisation organisation) {
        return new OrganisationResponse(
                organisation.id,
                organisation.name,
                organisation.tokenname,
                organisation.projects.stream().map(a -> a.id).collect(Collectors.toList()),
                organisation.users.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    private Organisation unbind(Organisation organisation, OrganisationRequest request) {
        organisation.name = request.getName();
        organisation.tokenname = request.getTokenName();
        return organisation;
    }

    private Organisation unbind(OrganisationRequest request) {
        return unbind(new Organisation(), request);
    }

}
