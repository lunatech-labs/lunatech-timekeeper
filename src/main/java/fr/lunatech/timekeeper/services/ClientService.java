package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
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
public class ClientService {

    private static Logger logger = LoggerFactory.getLogger(ClientService.class);

    public Optional<ClientResponse> findResponseById(Long id, AuthenticationContext ctx) {
        return findById(id, ctx).map(ClientResponse::bind);
    }

    public List<ClientResponse> listAllResponses(AuthenticationContext ctx) {
        return streamAll(ctx, ClientResponse::bind, Collectors.toList());
    }

    @Transactional
    public Long create(ClientRequest request, AuthenticationContext ctx) {
        logger.debug("Create a new client with {}, {}", request, ctx);
        final Client client = request.unbind(ctx);
        Client.persist(client);
        return client.id;
    }

    @Transactional
    public Optional<Long> update(Long id, ClientRequest request, AuthenticationContext ctx) {
        logger.debug("Modify client for id={} with {}, {}", id, request, ctx);
        return findById(id, ctx)
                .map(client -> request.unbind(client, ctx))
                .map(client -> client.id);
    }

    Optional<Client> findById(Long id, AuthenticationContext ctx) {
        return Client.<Client>findByIdOptional(id)
                .filter(ctx::canAccess);
    }

    <R extends Collection<ClientResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<Client, ClientResponse> bind,
            Collector<ClientResponse, ?, R> collector
    ) {
        try (final Stream<Client> clients = Client.streamAll()) {
            return clients
                    .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }
}