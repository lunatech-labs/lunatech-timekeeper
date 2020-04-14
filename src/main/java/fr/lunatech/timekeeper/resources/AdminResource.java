package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.openapi.AdminResourceApi;
import io.quarkus.security.Authenticated;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/admin")
@Authenticated
public class AdminResource implements AdminResourceApi {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String admin() {
        return "granted";
    }
}
