package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.OrganisationRequest;
import fr.lunatech.timekeeper.services.dtos.OrganisationResponse;

import java.util.List;
import java.util.Optional;

public interface OrganisationService {

    Optional<OrganisationResponse> findOrganisationById(Long id);

    Optional<OrganisationResponse> findOrganisationByTokenName(String tokenName);

    List<OrganisationResponse> listAllOrganisations();

    Long createOrganisation(OrganisationRequest request);

}
