/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
