package fr.lunatech.timekeeper.resources.utils;

import com.google.common.collect.Iterables;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class ScenarioRunner {

    private final static String SLASH = "/";
    private final static String LOCATION = "location";

    public static <R, P> R createResource(P request, String uri_root, Class<R> type) {
        return createResource(request, uri_root, Option.none(), type);
    }

    public static <R, P> R createResource(P request, String uri_root, Option<String> getUri, Class<R> type) {
        String location = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post(uri_root).header(LOCATION);

        final String id = Iterables.<String>getLast(Arrays.stream(location.split(SLASH)).collect(Collectors.toList()));

        System.out.println(uri_root + " : " +
                given().when()
                        .header(ACCEPT, APPLICATION_JSON)
                        .get(getUri.getOrElse(uri_root) + SLASH + id).statusCode());
        return given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(getUri.getOrElse(uri_root) + SLASH + id).body().as(type);
    }

    public static <P> RType createResource(P request, String uri_root) {
        String location = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(request)
                .post(uri_root).header(LOCATION);

        final String userId = Iterables.<String>getLast(Arrays.stream(location.split(SLASH)).collect(Collectors.toList()));

        given()
                .when()
                .header(ACCEPT, APPLICATION_JSON)
                .get(uri_root + SLASH + userId);
        return RType.NoReturn;
    }

    public static <A, B, C, D> D createLinkedResource3(A p, Function1<A, B> m, Function1<B, C> m2, Function1<C, D> m3) {
        return m3.compose(m2.compose(m)).apply(p);
    }

    public static <A, B, C> C createLinkedResource2(A p, Function1<A, B> m, Function1<B, C> m2) {
        return m2.compose(m).apply(p);
    }

    public static <A, B> B createLinkedResource1(A p, Function1<A, B> m) {
        return m.apply(p);
    }

    @SafeVarargs
    public static <A, B> Tuple2<A, List<B>> distribResource(A p, Function1<A, B>... distrib) {
        return Tuple.of(p, List.of(distrib).map(f -> f.apply(p)));
    }

    public static <NoReturn, A> RType createWithLink0(A p, Function1<A, NoReturn> m) {
        m.apply(p);
        return RType.NoReturn;
    }

}
