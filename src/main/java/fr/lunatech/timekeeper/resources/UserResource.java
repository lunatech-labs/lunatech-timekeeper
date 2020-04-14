package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.openapi.UserResourceApi;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This resource is used by the react front-end to retrive the current authenticated user.
 * React contacts Quarkus with a JWT token. Quarkus checks with Keycloak, extract the user from the JWT Token and
 * returns the User as a JSON object to React.
 *
 * @author Nicolas Martignole
 */
@Path("/api/users")
public class UserResource implements UserResourceApi {

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public JwtUser me() {
        return new JwtUser(identity);
    }

}
