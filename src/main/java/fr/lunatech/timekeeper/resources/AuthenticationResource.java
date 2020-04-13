package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.authentication.JwtUser;
import fr.lunatech.timekeeper.dtos.UserCreateRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.resources.apis.AuthenticationResourceApi;
import fr.lunatech.timekeeper.services.interfaces.UserService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

import static java.util.Collections.emptyList;

/**
 * This resource is used by the react front-end to retrive the current authenticated user.
 * React contacts Quarkus with a JWT token. Quarkus checks with Keycloak, extract the user from the JWT Token and
 * returns the User as a JSON object to React.
 *
 * @author Nicolas Martignole
 */
@Path("/api/me")
public class AuthenticationResource implements AuthenticationResourceApi {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationResource.class);

    @Inject
    SecurityIdentity identity;

    @Inject
    UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    @Override
    public UserResponse me() {

        logger.debug("create user from " + identity.getPrincipal());
        logger.debug(identity.getPrincipal().getClass().toString());

        if (identity.getPrincipal() instanceof io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) {
            OidcJwtCallerPrincipal jwtCallerPrincipal = (io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) identity.getPrincipal();

            // logger.debug("Claims" + jwtCallerPrincipal.getClaims());
            final String name = identity.getPrincipal().getName();
            final String email = jwtCallerPrincipal.getClaims().getClaimValueAsString("email");
            final String firstName = jwtCallerPrincipal.getClaims().getClaimValueAsString("given_name");
            final String lastname = jwtCallerPrincipal.getClaims().getClaimValueAsString("family_name");

            //TODO manage the register process
            return new UserResponse(0L, firstName, lastname, email, emptyList());
            //return userService.findUserByEmail(email).orElseThrow(ForbiddenException::new);
        } else {
            throw new ForbiddenException();
        }
    }

}
