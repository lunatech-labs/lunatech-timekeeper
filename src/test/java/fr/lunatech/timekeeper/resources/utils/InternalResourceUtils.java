package fr.lunatech.timekeeper.resources.utils;

import com.google.common.collect.Iterables;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

/**
 * InternalResourceUtils provided abstract implementations of resource interaction
 */
final class InternalResourceUtils {

    private final static String SLASH = "/";
    private final static String LOCATION = "location";
    private final static Logger logger = LoggerFactory.getLogger(InternalResourceUtils.class);

    static <R, P> R createResource(P request, String uri_root, Class<R> type, String token) {
        return createResource(request, uri_root, Option.none(), type, token);
    }

    public static <P> RType updateResource(P request, String uri, String token) {
        given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .put(uri)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        return RType.NoReturn;
    }

    public static <R, P> R createResource(P request, String uri_root, Option<String> getUri, Class<R> type, String token) {

        logger.debug("Create : " + request.getClass() + " resource ");
        logger.debug("Uri    :" + uri_root);
        logger.debug("Verb   : GET");

        var reqSpec = given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post(uri_root);

        var location = reqSpec.header(LOCATION);
        var status = reqSpec.statusCode();
        logger.debug(String.format("Location of created resource   : %s", location));
        logger.debug(String.format("Status code   : %s", status));

        if (location == null || status < 200) {
            throw new HttpTestRuntimeException(status, reqSpec.getBody().print() , reqSpec.getContentType());
        } else {
            final String id = Iterables.<String>getLast(Arrays.stream(reqSpec.header(LOCATION).split(SLASH)).collect(Collectors.toList()));

            return given()
                    .auth().preemptive().oauth2(token)
                    .when()
                    .header(ACCEPT, APPLICATION_JSON)
                    .get(getUri.getOrElse(uri_root) + SLASH + id).body().as(type);
        }
    }

    public static <P> RType createResource(P request, String uri_root, String token) {
        logger.debug("Create : " + request.getClass() + " resource [Without return value]");
        createResource(request, uri_root, Object.class, token);
        return RType.NoReturn;
    }

    static <R> R readResource(Long id, String uri_root, Class<R> type, String token) {
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root + SLASH + id).body().as(type);
    }

    static <R> R readResource(String id, String uri_root, Class<R> type, String token) {
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root + SLASH + id)
                .body()
                .as(type);
    }

    static ValidatableResponse readResourceValidation(Long id, String uri_root, String token) {
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root + SLASH + id)
                .then();
    }

    static ValidatableResponse readResourceValidation(String uri_root, String token) {
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root)
                .then();
    }

    /**
     * Allows the caller to specify as parameter the statusCode and the body
     *
     * @param m      HttpMethod
     * @param uri    uri
     * @param token  security token
     * @param status status Code
     * @return
     */
    static <T> ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token, T body, javax.ws.rs.core.Response.Status status) {
        return resourceValidation(m, uri, token, Option.some(body), Option.some(status));
    }

    /**
     * Allows the caller to specify as parameter the statusCode
     *
     * @param m      HttpMethod
     * @param uri    uri
     * @param token  security token
     * @param status status Code
     * @return
     */
    static ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token, javax.ws.rs.core.Response.Status status) {
        return resourceValidation(m, uri, token, Option.none(), Option.some(status));
    }

    /**
     * Delegate to the caller to put the assertion on response
     *
     * @param m     HttpMethod
     * @param uri   Uri
     * @param token security token
     * @return ValidatableResponse
     */
    static ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token) {
        return resourceValidation(m, uri, token, Option.none(), Option.none());
    }

    private static <T> ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token, Option<T> body, Option<javax.ws.rs.core.Response.Status> status) {

        var root = given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON);

        body.map(b -> root.body(b));

        var resp = computeVerb(m, uri, root);
        var then = resp.then();
        return status.map(s -> then.statusCode(s.getStatusCode())).getOrElse(then);
    }

    private static Response computeVerb(VerbDefinition m, String uri, RequestSpecification root) {
        switch (m.verb) {
            case HttpMethod.GET:
                return root.get(uri);
            case HttpMethod.PUT:
                return root.put(uri);
            case HttpMethod.POST:
                return root.post(uri);
            case HttpMethod.DELETE:
                return root.delete(uri);
            default:
                throw new UnsupportedOperationException("Operation not supported " + m.verb);
        }
    }
}
