package com.legalnow.api.consultation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.common.SecurityUtils;
import com.legalnow.api.common.exception.BadRequestException;
import com.legalnow.api.common.exception.ConflictException;
import com.legalnow.api.common.exception.ForbiddenException;
import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.consultation.dto.ConsultationResponse;
import com.legalnow.api.consultation.dto.CreateConsultationRequest;
import com.legalnow.api.consultation.dto.StatusTransitionRequest;
import com.legalnow.api.consultation.dto.UpdateConsultationRequest;
import com.legalnow.api.user.Role;
import com.legalnow.api.user.User;
import com.legalnow.api.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;

    public ConsultationService(
        ConsultationRepository consultationRepository,
        UserRepository userRepository
    ) {
        this.consultationRepository = consultationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ConsultationResponse create(CreateConsultationRequest req) {
        UUID callerId = SecurityUtils.currentUserId();
        if (req.lawyerId().equals(callerId)) {
            throw new BadRequestException("lawyer_id must differ from the caller");
        }
        User lawyer = userRepository.findById(req.lawyerId())
            .orElseThrow(() -> new BadRequestException("Lawyer not found"));
        if (lawyer.getRole() != Role.LAWYER) {
            throw new BadRequestException("Target user is not a lawyer");
        }

        Consultation c = new Consultation();
        c.setClientId(callerId);
        c.setLawyerId(req.lawyerId());
        c.setStatus(ConsultationStatus.PENDING);
        c.setTitle(req.title());
        c.setDescription(req.description());
        c.setScheduledAt(req.scheduledAt());
        Consultation saved = consultationRepository.save(c);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConsultationResponse getById(UUID id) {
        Consultation c = loadOrThrow(id);
        authorizeView(c);
        return toResponse(c);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponse> listMine(ConsultationStatus statusOrNull, Role asRoleOrNull, Pageable pageable) {
        UUID callerId = SecurityUtils.currentUserId();
        Role asRole = asRoleOrNull != null ? asRoleOrNull : inferCallerRole(callerId);

        log.info("CALLER_ID: " + callerId);
        log.info("ROLE: " + asRole);

        Page<Consultation> page;
        if (asRole == Role.LAWYER) {
            page = statusOrNull == null
                ? consultationRepository.findByLawyerId(callerId, pageable)
                : consultationRepository.findByLawyerIdAndStatus(callerId, statusOrNull, pageable);
        } else {
            // CLIENT or ADMIN listing-as-client; admin would normally filter elsewhere
            page = statusOrNull == null
                ? consultationRepository.findByClientId(callerId, pageable)
                : consultationRepository.findByClientIdAndStatus(callerId, statusOrNull, pageable);
        }

        log.info("PAGE: " + page);

        // Avoid N+1: collect all distinct user ids on the page, fetch in one go.
        Set<UUID> userIds = new HashSet<>();
        for (Consultation c : page.getContent()) {
            userIds.add(c.getClientId());
            userIds.add(c.getLawyerId());
        }

        log.info("USERS: " + userIds);

        Map<UUID, User> usersById = loadUsers(userIds);
        return page.map(c -> ConsultationResponse.from(c, usersById.get(c.getClientId()), usersById.get(c.getLawyerId())));
    }

    @Transactional
    public ConsultationResponse update(UUID id, UpdateConsultationRequest req) {
        Consultation c = loadOrThrow(id);
        UUID callerId = SecurityUtils.currentUserId();
        boolean admin = SecurityUtils.isAdmin();
        boolean isClient = c.getClientId().equals(callerId);
        boolean isLawyer = c.getLawyerId().equals(callerId);

        if (c.getStatus().isTerminal() && !admin) {
            throw new ConflictException("Consultation is in a terminal state");
        }

        if (!admin) {
            if (isClient) {
                // client can edit only before ACCEPTED (i.e., only while PENDING)
                if (c.getStatus() != ConsultationStatus.PENDING) {
                    throw new ForbiddenException("Client may only edit pending consultations");
                }
            } else if (isLawyer) {
                // lawyer can only touch scheduledAt on non-terminal states
                if (req.title() != null || req.description() != null) {
                    throw new ForbiddenException("Lawyer may only update scheduled_at");
                }
            } else {
                throw new ForbiddenException("Not a participant of this consultation");
            }
        }

        if (req.title() != null) {
            c.setTitle(req.title());
        }
        if (req.description() != null) {
            c.setDescription(req.description());
        }
        if (req.scheduledAt() != null) {
            c.setScheduledAt(req.scheduledAt());
        }
        return toResponse(consultationRepository.save(c));
    }

    @Transactional
    public ConsultationResponse transition(UUID id, StatusTransitionRequest req) {
        Consultation c = loadOrThrow(id);
        UUID callerId = SecurityUtils.currentUserId();
        boolean admin = SecurityUtils.isAdmin();
        boolean isClient = c.getClientId().equals(callerId);
        boolean isLawyer = c.getLawyerId().equals(callerId);

        if (!admin && !isClient && !isLawyer) {
            throw new ForbiddenException("Not a participant of this consultation");
        }

        ConsultationStatus from = c.getStatus();
        ConsultationStatus to = req.status();
        if (!from.canTransitionTo(to)) {
            throw new ConflictException("Invalid status transition: " + from.toDb() + " -> " + (to == null ? "null" : to.toDb()));
        }

        if (!admin) {
            authorizeTransition(from, to, isClient, isLawyer);
        }

        c.setStatus(to);
        return toResponse(consultationRepository.save(c));
    }

    @Transactional
    public ConsultationResponse cancel(UUID id) {
        return transition(id, new StatusTransitionRequest(ConsultationStatus.CANCELLED, null));
    }

    private Consultation loadOrThrow(UUID id) {
        return consultationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Consultation not found"));
    }

    private void authorizeView(Consultation c) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        UUID callerId = SecurityUtils.currentUserId();
        if (!c.getClientId().equals(callerId) && !c.getLawyerId().equals(callerId)) {
            throw new ForbiddenException("Not a participant of this consultation");
        }
    }

    // Caller-role guard per transition; admin path skips this method.
    private void authorizeTransition(ConsultationStatus from, ConsultationStatus to, boolean isClient, boolean isLawyer) {
        switch (from) {
            case PENDING -> {
                if (to == ConsultationStatus.ACCEPTED || to == ConsultationStatus.REJECTED) {
                    if (!isLawyer) throw new ForbiddenException("Only the lawyer may accept or reject");
                } else if (to == ConsultationStatus.CANCELLED) {
                    if (!isClient) throw new ForbiddenException("Only the client may cancel a pending consultation");
                }
            }
            case ACCEPTED -> {
                if (to == ConsultationStatus.SCHEDULED) {
                    if (!isLawyer) throw new ForbiddenException("Only the lawyer may schedule");
                }
                // CANCELLED: either participant
            }
            case SCHEDULED -> {
                if (to == ConsultationStatus.IN_PROGRESS) {
                    if (!isLawyer) throw new ForbiddenException("Only the lawyer may start the session");
                }
                // CANCELLED: either participant
            }
            case IN_PROGRESS -> {
                if (to == ConsultationStatus.COMPLETED) {
                    if (!isLawyer) throw new ForbiddenException("Only the lawyer may complete the session");
                }
                // CANCELLED: either participant
            }
            default -> throw new ConflictException("No transitions allowed from " + from.toDb());
        }
    }

    private Role inferCallerRole(UUID callerId) {
        return userRepository.findById(callerId)
            .map(User::getRole)
            .orElseThrow(() -> new ForbiddenException("User not found"));
    }

    // Single-consultation enrichment: load both participants in one query.
    private ConsultationResponse toResponse(Consultation c) {
        Map<UUID, User> usersById = loadUsers(Set.of(c.getClientId(), c.getLawyerId()));
        return ConsultationResponse.from(c, usersById.get(c.getClientId()), usersById.get(c.getLawyerId()));
    }

    private Map<UUID, User> loadUsers(Set<UUID> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<User> found = userRepository.findAllById(ids);
        Map<UUID, User> byId = new HashMap<>(found.size());
        for (User u : found) {
            byId.put(u.getId(), u);
        }
        return byId;
    }
}
