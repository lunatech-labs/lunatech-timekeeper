package fr.lunatech.timekeeper.resources.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.quarkus.jackson.ObjectMapperCustomizer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * TestUtils provides methods that ease
 */
@Singleton
public class TimeKeeperTestUtils {
    // Use the ObjectMapper customized
    @Inject
    public ObjectMapper mapper;

    public <T> String toJson(T o)  {
        try {
            return mapper.writeValueAsString(o);
        }catch (JsonProcessingException err){
            System.err.println(  "JSON Unmarshall error" + err);
            return null;
        }
    }

//    @SafeVarargs
    public <T> String listOfTasJson(T... elements) {
        List<T> ts = Arrays.<T>asList(elements);
//        System.out.println("To String : " + ts);
//        System.out.println("List as Json" + toJson(ts));
        return toJson(Arrays.<T>asList(elements));
    }

    /*public static <T> String toJson(T o)  {
        // https://www.baeldung.com/jackson-optional
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        //ObjectMapperCustomizer
        try {
            return mapper.writeValueAsString(o);
        }catch (JsonProcessingException err){
            System.err.println(  "JSON Unmarshall error" + err);
            return null;
        }
    }*/

    /*@SafeVarargs
    public static <T> String listOfTasJson(T... elements) {
        List<T> ts = Arrays.<T>asList(elements);
//        System.out.println("To String : " + ts);
//        System.out.println("List as Json" + toJson(ts));
        return toJson(Arrays.<T>asList(elements));
    }*/

    public <T> String listOfTasJson(List<T> elements) {
        return toJson(elements);
    }

    public <T> String listOfTasJson(io.vavr.collection.List<T> elements) {
        return toJson(elements.asJava());
    }
}
