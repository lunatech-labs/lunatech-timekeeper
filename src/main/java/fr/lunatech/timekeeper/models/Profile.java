package fr.lunatech.timekeeper.models;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public enum Profile {
    SuperAdmin ("super_admin"),
    Admin ("admin"),
    User ("user"),;

    String tokenValue;

    Profile(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public static Optional<Profile> getByName(String value) {
        return stream(Profile.values())
                .filter(profile -> value.equals(profile.tokenValue))
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
