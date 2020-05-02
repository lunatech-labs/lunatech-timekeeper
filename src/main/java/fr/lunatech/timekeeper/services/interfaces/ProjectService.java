package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    Optional<ProjectResponse> findProjectById(Long organizationId, Long id);

    List<ProjectResponse> listAllProjects(Long organizationId);

    Long createProject(Long organizationId, ProjectRequest request);

    Optional<Long> updateProject(Long organizationId, Long id, ProjectRequest project);
}
