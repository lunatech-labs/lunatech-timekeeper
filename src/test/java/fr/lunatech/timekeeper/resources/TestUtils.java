package fr.lunatech.timekeeper.resources;

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

    static UserRequest createUserRequest(String firstName, String lastName, String email, String picture, Profile... profiles) {
        final List<Profile> p = Arrays.asList(profiles);
        return new UserRequest(firstName, lastName, email, picture, p);
    }

    static <T> List<T> listOf(T... elements) {
        return Arrays.asList(elements);
    }

    static <T> String listOfTasJson(T... elements) {
        return toJson(Arrays.asList(elements));

    }
}
