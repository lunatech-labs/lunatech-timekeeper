package fr.lunatech.timekeeper.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class ExampleResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Logger logger = LoggerFactory.getLogger(ExampleResource.class);
        logger.info("Hello World");

        return "hello";
    }
}