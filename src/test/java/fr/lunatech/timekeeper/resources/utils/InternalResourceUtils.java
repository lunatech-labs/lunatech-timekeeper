/*
 * Copyright 2020 Lunatech Labs
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
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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

    public static <P> ValidatableResponse updateResource(P request, String uri, String token) {
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .put(uri)
                .then();
    }

    public static <P> ValidatableResponse updateResource(String uri, String token) {
       return given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .put(uri)
                .then();
    }

    public static <R, P> R createResource(P request, String uri_root, Option<String> getUri, Class<R> type, String token) {
        var location = createResource(request, uri_root, token);
        final String id = Iterables.getLast(Arrays.stream(location.split(SLASH)).collect(Collectors.toList()));
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(getUri.getOrElse(uri_root) + SLASH + id).body().as(type);
    }

    public static <P> String createResource(P request, String uri_root, String token) {
        logger.debug("Create : " + request.getClass() + " resource ");
        logger.debug("Uri    :" + uri_root);
        logger.debug("Verb   : POST");

        Response reqSpec = given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post(uri_root);

        var location = reqSpec.header(LOCATION);
        var status = reqSpec.statusCode();
        logger.debug(String.format("Location of created resource   : %s", location));
        logger.debug(String.format("Status code   : %s", status));

        if (location == null || status > 201) {
            throw new HttpTestRuntimeException(status, reqSpec.getBody().print(), reqSpec.getContentType());
        }
        return location;
    }

    static <R> R readResource(Long id, String uri_root, Class<R> type, String token) {
        return readResource(id, Collections.emptyMap(), uri_root, type, token);
    }

    static <R> R readResource(String id, String uri_root, Class<R> type, String token) {
        return readResource(id, Collections.emptyMap(), uri_root, type, token);
    }

    static String paramUrlResolver(Map<String, String> params) {
        final StringBuilder builder = new StringBuilder();
        params.forEach((key, value) -> {
            builder.append("&").append(key).append("=").append(value);
        });
        return params.entrySet().size() > 0 ? "?" + builder.replace(0, 1, "").toString() : "";
    }

    static <R> R readResource(Long id, Map<String, String> params, String uri_root, Class<R> type, String token) {
        return readResource(id.toString(), params, uri_root, type, token);
    }

    static <R> R readResource(String id, Map<String, String> params, String uri_root, Class<R> type, String token) {
        final var queryParams = paramUrlResolver(params);
        return given()
                .auth().preemptive().oauth2(token)
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root + SLASH + id + queryParams)
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
     * @return
     */
    static <T> ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token, T body) {
        return resourceValidation(m, uri, token, Option.some(body));
    }

    /**
     * Allows the caller to specify as parameter the statusCode
     *
     * @param m      HttpMethod
     * @param uri    uri
     * @param token  security token
     * @return
     */
    static ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token) {
        return resourceValidation(m, uri, token, Option.none());
    }


    private static <T> ValidatableResponse resourceValidation(VerbDefinition m, String uri, String token, Option<T> body) {

        var root = given()
                .auth().preemptive().oauth2(token)
                .when()
                .contentType(APPLICATION_JSON);

        body.map(b -> root.body(b));

        var resp = computeVerb(m, uri, root);
        return resp.then();
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
