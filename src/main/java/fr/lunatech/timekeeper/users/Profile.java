package fr.lunatech.timekeeper.users;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public enum Profile {
    Admin, SimpleUSer;

    @Converter
    public static class ListConverter implements AttributeConverter<List<Profile>, String> {

        @Override
        public String convertToDatabaseColumn(List<Profile> list) {
            return list.stream().map(Enum::name).collect(Collectors.joining(","));
        }

        @Override
        public List<Profile> convertToEntityAttribute(String joined) {
            return stream(joined.split(",")).map(Profile::valueOf).collect(Collectors.toList());
        }

    }
}
