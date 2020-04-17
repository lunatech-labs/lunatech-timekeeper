package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class ScenarioRunner {

    public static UserResponse createUser(UserRequest userRequest) {
        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(userRequest)
                .post("/api/users").header("location");

        return null;

    }
}
