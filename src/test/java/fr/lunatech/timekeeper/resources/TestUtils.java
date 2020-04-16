package fr.lunatech.timekeeper.resources;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class TestUtils {

    static Jsonb jsonb = JsonbBuilder.create();

    public  static <T> String toJson(T o){
        return jsonb.toJson(o);
    }
}
