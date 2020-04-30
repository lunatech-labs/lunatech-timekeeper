package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    Optional<ClientResponse> findClientById(Long id);

    List<ClientResponse> listAllClients();

    Long createClient(ClientRequest request);

    Optional<Long> updateClient(Long id, ClientRequest request);
}