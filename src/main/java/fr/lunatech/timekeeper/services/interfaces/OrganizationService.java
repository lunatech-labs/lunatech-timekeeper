package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.OrganizationRequest;
import fr.lunatech.timekeeper.services.dtos.OrganizationResponse;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {

    Optional<OrganizationResponse> findOrganizationById(Long id);

    Optional<OrganizationResponse> findOrganizationByTokenName(String tokenName);

    List<OrganizationResponse> listAllOrganizations();

    Long createOrganization(OrganizationRequest request);

    Optional<Long> updateOrganization(Long id, OrganizationRequest request);
}
