package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.MemberResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

//TODO FIX TEMPORAIRE
@Path("/api/members")
public interface MemberBisResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MemberResponse> getAllMembers();
}
