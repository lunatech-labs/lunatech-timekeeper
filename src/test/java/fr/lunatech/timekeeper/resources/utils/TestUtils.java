package fr.lunatech.timekeeper.resources.utils;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Arrays;
import java.util.List;

/**
 * TestUtils provides methods that ease
 */
public class TestUtils {

    static Jsonb jsonb = JsonbBuilder.create();

    public static <T> String toJson(T o) {
        return jsonb.toJson(o);
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
