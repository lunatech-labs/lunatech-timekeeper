package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.services.dtos.UserRequest;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

    static Jsonb jsonb = JsonbBuilder.create();

    public static <T> String toJson(T o) {
        return jsonb.toJson(o);
    }

    public static UserRequest createUserRequest(String firstName, String lastName, String email, String picture, Profile... profiles) {
        final List<Profile> p = Arrays.asList(profiles);
        return new UserRequest(firstName, lastName, email, picture, p);
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return Arrays.<T>asList(elements);

    }



    @SafeVarargs
    public static <T> String listOfTasJson(T... elements) {
        return toJson(Arrays.<T>asList(elements));


    }


    public static <T> String listOfTasJson(List<T> elements) {
        return toJson(elements);

    }
    public static <T> String listOfTasJson(io.vavr.collection.List<T> elements) {
        return toJson(elements.asJava());

    }
}