package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class OrganizationService {

    private static Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    public Optional<OrganizationResponse> findResponseById(Long id, AuthenticationContext ctx) {
        return findById(id, ctx).map(OrganizationResponse::bind);
    }

    @Transactional
    public List<OrganizationResponse> listAllResponses(AuthenticationContext ctx) {
        return streamAll(ctx, OrganizationResponse::bind, Collectors.toList());
    }

    @Transactional
    public Long create(OrganizationRequest request, AuthenticationContext ctx) {
        logger.info("Create a new organization with request={}, ctx={}", request, ctx);
        final Organization organization = request.unbind(ctx);
        organization.persist();
        return organization.id;
    }

    @Transactional
    public Optional<Long> update(Long id, OrganizationRequest request, AuthenticationContext ctx) {
        logger.info("Modify organization for id={} with request={}, ctx={}", id, request, ctx);
        return findById(id, ctx)
                .map(organization -> request.unbind(organization, ctx))
                .map(organization -> organization.id);
    }

    Optional<Organization> findById(Long id, AuthenticationContext ctx) {
        return Organization.<Organization>findByIdOptional(id)
                .filter(ctx::canAccess);
    }

    Optional<Organization> findByTokenName(String tokenName) {
        return Organization.<Organization>find("tokenName", tokenName)
                .firstResultOptional();
    }

    <R extends Collection<OrganizationResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<Organization, OrganizationResponse> bind,
            Collector<OrganizationResponse, ?, R> collector
    ) {
        try (final Stream<Organization> organizations = Organization.streamAll()) {
            return organizations
                    .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }
}
