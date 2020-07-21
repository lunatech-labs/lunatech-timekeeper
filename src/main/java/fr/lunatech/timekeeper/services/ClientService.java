/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Client;
import fr.lunatech.timekeeper.resources.exceptions.CreateResourceException;
import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.PersistenceException;
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
        try {
            client.persistAndFlush();
        } catch (PersistenceException pe) {
            throw new CreateResourceException("Client was not created due to constraint violation");
        }
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