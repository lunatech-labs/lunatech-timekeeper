package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.requests.TimeEntryPerDayRequest;
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
    TimeSheetDef("/api/my/timeSheets", apply(Void.class, TimeSheetResponse.class)),
    TimeEntryDayDef("/api/timeSheet/%d/timeEntry/%s", apply(TimeEntryPerDayRequest.class, Void.class));

    final public String uri;
    final public TypeDefinition typeDef;

    <R, T> ResourceDefinition(final String uri, TypeDefinition typeDef) {
        this.uri = uri;
        this.typeDef = typeDef;
    }

    public String uriWithid(Long id) {
        return uriWithid(id, Collections.emptyMap());
    }

    public String uriWithid(Long id, Map<String, String> params) {
        return String.format("%s/%s", this.uri, id) + paramUrlResolver(params);
    }

    public String uriWithArgs(Object... args) {
        return String.format(this.uri, args);
    }

    public String uriWithMultiId(Long... ids) {
        return String.format(this.uri,ids);
    }

}
