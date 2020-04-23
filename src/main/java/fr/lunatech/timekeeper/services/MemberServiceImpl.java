package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.services.dtos.MemberRequest;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.dtos.MemberUpdateRequest;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.models.Project;
import fr.lunatech.timekeeper.models.Member;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.interfaces.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class MemberServiceImpl implements MemberService {

    private static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    public Optional<MemberResponse> findMemberById(Long projectId, Long id) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchProjectId(member, projectId))
                .map(this::from);
    }

    @Override
    public List<MemberResponse> listAllMembers() {
        try (final Stream<Member> members = Member.streamAll()) {
            return members
                    .map(this::from)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<MemberResponse> listAllMembers(Long projectId) {
        try (final Stream<Member> members = Member.streamAll()) {
            return members.filter(member -> matchProjectId(member, projectId))
                    .map(this::from)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createMember(Long projectId, MemberRequest request) {
        logger.debug("Create a new member for projectId="+projectId+" with memberRequest="+request);
        final var member = bind(projectId, request);
        Member.persist(member);
        return member.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateMember(Long projectId, Long id, MemberUpdateRequest request) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchProjectId(member, projectId))
                .map(member -> bind(member, projectId, request).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteMember(Long projectId, Long id) {
        return Member.<Member>findByIdOptional(id)
                .filter(member -> matchProjectId(member, projectId))
                .map(member -> {
                    final var oldId = member.id;
                    if (member.isPersistent()) {
                        member.delete();
                    }
                    return oldId;
                });
    }

    private MemberResponse from(Member member) {
        return new MemberResponse(member.id, member.user.id, member.role, member.project.id);
    }

    private Member bind(Long projectId, MemberRequest request) {
        final var member = new Member();
        member.project = getProject(projectId);
        member.user = getUser(request.getUserId());
        member.role = request.getRole();
        return member;
    }

    private Member bind(Member member, Long projectId, MemberUpdateRequest request) {
        member.project = getProject(projectId);
        member.role = request.getRole();
        return member;
    }

    private Project getProject(Long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One project is required for member. projectId=%s", projectId)));
    }

    private User getUser(Long userId) {
        return User.<User>findByIdOptional(userId)
                .orElseThrow(() -> new IllegalEntityStateException(String.format("One User is required for member. userId=%s", userId)));
    }

    private boolean matchProjectId(Member member, Long projectId) {
        return member.project != null && Objects.equals(member.project.id, projectId);
    }
}
