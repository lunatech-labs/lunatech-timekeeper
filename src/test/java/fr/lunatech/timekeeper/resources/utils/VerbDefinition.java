package fr.lunatech.timekeeper.resources.utils;

import javax.ws.rs.HttpMethod;

public enum VerbDefinition {

    GET(HttpMethod.GET), POST(HttpMethod.POST), PUT(HttpMethod.PUT), DELETE(HttpMethod.DELETE);

    final public String verb;

    private VerbDefinition(String verb) {
        this.verb = verb;
    }

}
