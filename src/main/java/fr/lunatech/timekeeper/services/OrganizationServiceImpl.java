package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.services.dtos.OrganizationRequest;
import fr.lunatech.timekeeper.services.dtos.OrganizationResponse;
import fr.lunatech.timekeeper.services.interfaces.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class OrganizationServiceImpl implements OrganizationService {

    private static Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);


    @Override
    public Optional<OrganizationResponse> findOrganizationById(Long id) {
        return Organization.<Organization>findByIdOptional(id).map(this::bind);
    }

    @Override
    public Optional<OrganizationResponse> findOrganizationByTokenName(String tokenName) {
        return Organization.<Organization>find("tokenName", tokenName).firstResultOptional().map(this::bind);
    }

    @Transactional
    @Override
    public List<OrganizationResponse> listAllOrganizations() {
        try (final Stream<Organization> organizations = Organization.streamAll()) {
            return organizations.map(this::bind).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createOrganization(OrganizationRequest request) {
        logger.info("Create a new organization with " + request);
        Organization organization = unbind(request);

        organization.persist();

        return organization.id;
    }

    private OrganizationResponse bind(Organization organization) {
        return new OrganizationResponse(
                organization.id,
                organization.name,
                organization.tokenname,
                organization.projects.stream().map(a -> a.id).collect(Collectors.toList()),
                organization.users.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    private Organization unbind(Organization organization, OrganizationRequest request) {
        organization.name = request.getName();
        organization.tokenname = request.getTokenName();
        return organization;
    }

    private Organization unbind(OrganizationRequest request) {
        return unbind(new Organization(), request);
    }

}
