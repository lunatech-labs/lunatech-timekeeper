package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.requests.*;
import fr.lunatech.timekeeper.services.responses.*;

import java.util.Collections;
import java.util.Map;

import static fr.lunatech.timekeeper.resources.utils.InternalResourceUtils.paramUrlResolver;
import static fr.lunatech.timekeeper.resources.utils.TypeDefinition.apply;

public enum ResourceDefinition {

    ClientDef("/api/clients", apply(ClientRequest.class, ClientResponse.class)),
    OrganizationDef("/api/organizations", apply(OrganizationRequest.class, OrganizationResponse.class)),
    UserDef("/api/users", apply(Void.class, UserResponse.class)),
    ProjectDef("/api/projects", apply(ProjectRequest.class, ProjectResponse.class)),
    TimeSheetPerProjectPerUserDef("/api/projects/%d/users/%d", apply(Void.class, TimeSheetResponse.class)),
    TimeEntryDef("/api/timeSheet/%d/timeEntry", apply(TimeEntryRequest.class, Void.class)),
    TimeSheetDef("/api/time-sheets", apply(TimeSheetRequest.class, TimeSheetResponse.class)),
    PersonalTimeSheetsDef("/api/my/%d?weekNumber=%d", apply(Void.class, WeekResponse.class)),
    EventDef("api/events", apply(EventTemplateRequest.class,EventTemplateResponse.class)),
    EventUsersDef("api/events/%d/users", apply(Void.class,UserResponse.class));

    final public String uri;
    final public TypeDefinition typeDef;

    <R, T> ResourceDefinition(final String uri, TypeDefinition typeDef) {
        this.uri = uri;
        this.typeDef = typeDef;
    }

    public String uriPlusId(Long id) {
        return uriPlusId(id, Collections.emptyMap());
    }

    public String uriPlusId(Long id, Map<String, String> params) {
        return String.format("%s/%s", this.uri, id) + paramUrlResolver(params);
    }

    public String uriWithArgs(Object... args) {
        return String.format(this.uri, args);
    }

    public String uriWithMultiId(Long... ids) {
        return String.format(this.uri, ids);
    }

    public String uriWithMultiInt(Integer... ids) {
        return String.format(this.uri, ids);
    }
}
