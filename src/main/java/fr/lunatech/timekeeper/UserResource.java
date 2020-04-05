package fr.lunatech.timekeeper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.lunatech.timekeeper.model.User;
import org.jboss.resteasy.annotations.cache.NoCache;

import io.quarkus.security.identity.SecurityIdentity;

import java.util.HashMap;
import java.util.Map;

@Path("/api/users")
public class UserResource {

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public User me() {
        return new User(identity);
    }

    @GET
    @Path("/decode")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public Map decode() {
        Map map = new HashMap();
        map.put("token" , new User(identity));
        map.put("expired", false);
        return map;
    }
}
