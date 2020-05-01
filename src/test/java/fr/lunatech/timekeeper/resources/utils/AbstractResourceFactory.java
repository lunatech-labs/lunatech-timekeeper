package fr.lunatech.timekeeper.resources.utils;

import com.google.common.collect.Iterables;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

import static fr.lunatech.timekeeper.resources.KeycloakTestResource.getAdminAccessToken;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

final public class AbstractResourceFactory {

    private final static String SLASH = "/";
    private final static String LOCATION = "location";
    private static Logger logger = LoggerFactory.getLogger(AbstractResourceFactory.class);

    static <R, P> R createResource(P request, String uri_root, Class<R> type) {
        return createResource(request, uri_root, Option.none(), type);
    }

    public static <R, P> R createResource(P request, String uri_root, Option<String> getUri, Class<R> type) {
        final String adminToken = getAdminAccessToken();

        logger.debug("Create : " + request.getClass() + " resource ");
        logger.debug("Uri    :" + uri_root);
        logger.debug("Verb   : GET");

        var reqSpec = given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post(uri_root);

        var location = reqSpec.header(LOCATION);
        var status = reqSpec.statusCode();
        logger.debug(String.format("Location of created resource   : %s", location));
        logger.debug(String.format("Status code   : %s", status));

        if (location == null || status < 200) {
            throw new IllegalStateException(String.format("Something went wrong during creation of resource : %s , Data : %s", request.getClass(), request.toString()));
        } else {
            final String id = Iterables.<String>getLast(Arrays.stream(reqSpec.header(LOCATION).split(SLASH)).collect(Collectors.toList()));

            return given()
                    .auth().preemptive().oauth2(adminToken)
                    .when()
                    .header(ACCEPT, APPLICATION_JSON)
                    .get(getUri.getOrElse(uri_root) + SLASH + id).body().as(type);
        }
    }

    public static <P> RType createResource(P request, String uri_root) {
        logger.debug("Create : " + request.getClass() + " resource [Without return value]");
        createResource(request, uri_root, Object.class);
        return RType.NoReturn;
    }

    static <R> R readResource(Long id, String uri_root, Class<R> type) {
        final String adminToken = getAdminAccessToken();
        return given()
                .auth().preemptive().oauth2(adminToken)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root + SLASH + id).body().as(type);
    }
}
