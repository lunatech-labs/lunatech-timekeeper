package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import fr.lunatech.timekeeper.services.interfaces.ClientService;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ClientServiceImpl implements ClientService {

    @Override
    public Optional<ClientResponse> findClientById(Long id) {
        return Client.<Client>findByIdOptional(id).map(this::response);
    }

    @Override
    public List<ClientResponse> listAllClients() {
        try (final Stream<Client> clients = Client.streamAll()) {
            return clients.map(this::response).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createClient(ClientRequest request) {
        final var client = bind(request);
        Client.persist(client);
        return client.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateClient(Long id, ClientRequest request) {
        return Client.<Client>findByIdOptional(id).map(client -> bind(client, request).id);
    }

    private ClientResponse response(Client client) {
        return new ClientResponse(
                client.id,
                client.name,
                client.description,
                client.projects.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    private Client bind(Client client, ClientRequest request) {
        client.name = request.getName();
        client.description = request.getDescription();
        return client;
    }

    private Client bind(ClientRequest request) {
        return bind(new Client(), request);
    }
}