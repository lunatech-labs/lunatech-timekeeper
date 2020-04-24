package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import fr.lunatech.timekeeper.services.interfaces.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ClientServiceImpl implements ClientService {

    private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Override
    public Optional<ClientResponse> findClientById(Long id) {
        return Client.<Client>findByIdOptional(id).map(this::bind);
    }

    @Override
    public List<ClientResponse> listAllClients() {
        try (final Stream<Client> clients = Client.streamAll()) {
            return clients.map(this::bind).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createClient(ClientRequest request) {
        logger.info("Create a new client with " + request);
        final var client = unbind(request);
        Client.persist(client);
        return client.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateClient(Long id, ClientRequest request) {
        logger.info("Modify client for clientId=" + id + " with " + request);
        return Client.<Client>findByIdOptional(id).map(client -> unbind(client, request).id);
    }

    private ClientResponse bind(Client client) {
        return new ClientResponse(
                client.id,
                client.name,
                client.description,
                client.projects.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    private Client unbind(Client client, ClientRequest request) {
        client.name = request.getName();
        client.description = request.getDescription();
        return client;
    }

    private Client unbind(ClientRequest request) {
        return unbind(new Client(), request);
    }
}