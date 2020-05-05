package fr.lunatech.timekeeper.models;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public enum Profile {
    SuperAdmin, Admin, User, Guest;

    public static Optional<Profile> getByName(String value) {
        // To manage the value super-admin without to define a String Enum
        return stream(Profile.values())
                .filter(profile -> profile.name().equalsIgnoreCase(value.replace("-", "")))
                .findFirst();
    }

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
