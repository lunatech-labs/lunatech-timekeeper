package fr.lunatech.timekeeper.resources;

import fr.lunatech.utils.VersionInfo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/about")
public class VersionResource {
    @Inject
    VersionInfo versionInfo;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return String.format("%s %s", versionInfo.getGitHash(), versionInfo.getInstanceName());
    }
}
