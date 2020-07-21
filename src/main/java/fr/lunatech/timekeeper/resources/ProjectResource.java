/*
 * Copyright 2020 Lunatech S.A.S
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

package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.ProjectService;
import fr.lunatech.timekeeper.services.TimeSheetService;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class ProjectResource implements ProjectResourceApi {

    @Inject
    ProjectService projectService;

    @Inject
    TimeSheetService timeSheetService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetAllProjects", description = "Counts how many times the user load the project list on method 'getAllProjects'")
    @Timed(name = "timeGetAllProjects", description = "Times how long it takes the user load the project list on method 'getAllProjects'", unit = MetricUnits.MILLISECONDS)
    public List<ProjectResponse> getAllProjects() {
        final var ctx = authentication.context();
        return projectService.listAllResponses(ctx);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countCreateProject", description = "Counts how many times the user create an project on method 'createProject'")
    @Timed(name = "timeCreateProject", description = "Times how long it takes the user create an project on method 'createProject'", unit = MetricUnits.MILLISECONDS)
    public Response createProject(@Valid ProjectRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long projectId = projectService.create(request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(projectId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetProject", description = "Counts how many times the user to get the client on method 'getProject'")
    @Timed(name = "timeGetProject", description = "Times how long it takes the user to get the client on method 'getProject'", unit = MetricUnits.MILLISECONDS)
    public ProjectResponse getProject(Long id, Optional<Boolean> optimized) {
        final var ctx = authentication.context();
        return projectService.findResponseById(id, optimized, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countUpdateProject", description = "Counts how many times the user to update the project on method 'updateProject'")
    @Timed(name = "timeUpdateProject", description = "Times how long it takes the user to update the project on method 'updateProject'", unit = MetricUnits.MILLISECONDS)
    public Response updateProject(Long id, @Valid ProjectRequest request) {
        final var ctx = authentication.context();
        return projectService.update(id, request, ctx)
                .map( it -> Response.noContent().build())
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));
    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countJoinPublicProject", description = "Counts how many times the user to join a public project on method 'joinPublicProject'")
    @Timed(name = "timeJoinPublicProject", description = "Times how long it takes the user to join a public project on method 'joinPublicProject'", unit = MetricUnits.MILLISECONDS)
    public Response joinPublicProject(Long projectId) {
        final var ctx = authentication.context();
        return projectService.joinProject(projectId, ctx)
                .map( it -> Response.noContent().build())
                .orElseThrow(() -> new NotFoundException(String.format("Project or user not found for project id=%d and user id=%d", projectId, ctx.getUserId())));

    }

    @RolesAllowed({"user", "admin"})
    @Override
    @Counted(name = "countGetLastActiveTimeSheetForUser", description = "Counts how many times the user to get the last timesheet of a project on method 'getLastActiveTimeSheetForUser'")
    @Timed(name = "timeGetLastActiveTimeSheetForUser", description = "Times how long it takes the user to get the last timesheet of a project on method 'getLastActiveTimeSheetForUser'", unit = MetricUnits.MILLISECONDS)
    public TimeSheetResponse getLastActiveTimeSheetForUser(long idProject, long idUser) {
        Optional<TimeSheetResponse> maybeResponse = timeSheetService.findFirstForProjectForUser(idProject, idUser);
        return maybeResponse.orElseThrow(() -> new NotFoundException(String.format("No timesheet found for project_id=%d, and user_id=%d", idProject, idUser)));
    }
}
