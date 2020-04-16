package fr.lunatech.timekeeper.models;

import javax.persistence.AttributeConverter;

public enum Role {

    TeamLeader, Developer;

    @javax.persistence.Converter
    public static class Converter implements AttributeConverter<Role, String> {

        @Override
        public String convertToDatabaseColumn(Role role) {
            return role.name();
        }

        @Override
        public Role convertToEntityAttribute(String joined) {
            return Role.valueOf(joined);
        }

    }
}
