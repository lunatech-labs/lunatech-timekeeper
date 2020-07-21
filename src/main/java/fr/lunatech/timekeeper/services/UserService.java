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

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.requests.AuthenticationRequest;
import fr.lunatech.timekeeper.services.responses.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Inject
    OrganizationService organizationService;

    public Optional<UserResponse> findResponseById(Long id, AuthenticationContext ctx) {
        return findById(id, ctx).map(UserResponse::bind);
    }

    public List<UserResponse> listAllResponses(AuthenticationContext ctx) {
        return streamAll(ctx, UserResponse::bind, Collectors.toList());
    }

    @Transactional
    public AuthenticationContext authenticate(AuthenticationRequest request) {
        final User authenticatedUser = findByEmail(request.getEmail())
                .map(user -> {
                    if (request.isEquals(user)) {
                        logger.debug("Modify user for email={} with {}", user.email, request);
                        return request.unbind(user, organizationService::findByTokenName);
                    } else {
                        return user;
                    }
                })
                .orElseGet(() -> {
                    logger.debug("Create a new user for email={} with {}", request.getEmail(), request);
                    final User user = request.unbind(organizationService::findByTokenName);
                    User.persist(user);
                    return user;
                });

        return AuthenticationContext.bind(authenticatedUser);
    }

    public Long countAll() {
        return User.count();
    }

    Optional<User> findById(Long id, AuthenticationContext ctx) {
        return User.<User>findByIdOptional(id)
                .filter(ctx::canAccess);
    }

    private Optional<User> findByEmail(String email) {
        return User.<User>find("email", email)
                .firstResultOptional();
    }

    <R extends Collection<UserResponse>> R streamAll(
            AuthenticationContext ctx,
            Function<User, UserResponse> bind,
            Collector<UserResponse, ?, R> collector
    ) {
        try (final Stream<User> users = User.streamAll()) {
            return users
                    .filter(ctx::canAccess)
                    .map(bind)
                    .collect(collector);
        }
    }
}
