package fr.lunatech.timekeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.quarkus.jackson.ObjectMapperCustomizer;

import javax.inject.Singleton;
import java.util.TimeZone;

@Singleton
public class JacksonObjectMapperConfig implements ObjectMapperCustomizer {
    @Override
    public void customize(ObjectMapper objectMapper) {
        // https://www.baeldung.com/jackson-optional
        objectMapper.registerModule(new Jdk8Module());
    }
}
