package com.cms.collaborator.service;

import com.cms.collaborator.dto.request.CollaboratorRequest;
import com.cms.collaborator.dto.response.AverageRatingResponse;
import com.cms.collaborator.dto.response.CollaboratorDetailResponse;
import com.cms.collaborator.dto.response.CollaboratorResponse;
import com.cms.collaborator.dto.response.PerformanceReviewResponse;
import com.cms.collaborator.entity.Collaborator;
import com.cms.collaborator.entity.PerformanceReview;
import com.cms.collaborator.entity.enums.CollaboratorStatus;
import com.cms.collaborator.exception.CollaboratorNotFoundException;
import com.cms.collaborator.mapper.CollaboratorMapper;
import com.cms.collaborator.mapper.PerformanceReviewMapper;
import com.cms.collaborator.repository.CollaboratorRepository;
import com.cms.collaborator.repository.PerformanceReviewRepository;
import com.cms.common.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollaboratorServiceImpl implements CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final CollaboratorMapper collaboratorMapper;
    private final PerformanceReviewMapper performanceReviewMapper;

    @Override
    @Transactional
    public CollaboratorResponse createCollaborator(CollaboratorRequest request) {
        log.info("Creating new collaborator with email: {}", request.getEmail());

        validateUniqueConstraints(request, null);

        Collaborator collaborator = collaboratorMapper.toEntity(request);
        if (collaborator.getStatus() == null) {
            collaborator.setStatus(CollaboratorStatus.ACTIVE);
        }

        Collaborator savedCollaborator = collaboratorRepository.save(collaborator);
        log.info("Created collaborator with ID: {}", savedCollaborator.getId());

        return collaboratorMapper.toResponse(savedCollaborator);
    }

    @Override
    public CollaboratorResponse getCollaboratorById(UUID id) {
        log.debug("Fetching collaborator with ID: {}", id);
        Collaborator collaborator = findCollaboratorById(id);
        return collaboratorMapper.toResponse(collaborator);
    }

    @Override
    public CollaboratorDetailResponse getCollaboratorDetailById(UUID id) {
        log.debug("Fetching collaborator details with ID: {}", id);
        Collaborator collaborator = findCollaboratorById(id);

        CollaboratorDetailResponse response = collaboratorMapper.toDetailResponse(collaborator);

        // Enrich with performance data
        enrichWithPerformanceData(response, id);

        return response;
    }

    @Override
    public Page<CollaboratorResponse> getAllCollaborators(Pageable pageable) {
        log.debug("Fetching all collaborators with pagination");
        return collaboratorRepository.findAll(pageable)
                .map(collaboratorMapper::toResponse);
    }

    @Override
    public Page<CollaboratorResponse> getCollaboratorsWithFilters(
            CollaboratorStatus status,
            String department,
            String searchTerm,
            Pageable pageable) {
        log.debug("Fetching collaborators with filters - status: {}, department: {}, search: {}",
                status, department, searchTerm);
        return collaboratorRepository.findWithFilters(status, department, searchTerm, pageable)
                .map(collaboratorMapper::toResponse);
    }

    @Override
    @Transactional
    public CollaboratorResponse updateCollaborator(UUID id, CollaboratorRequest request) {
        log.info("Updating collaborator with ID: {}", id);

        Collaborator collaborator = findCollaboratorById(id);
        validateUniqueConstraints(request, id);

        collaboratorMapper.updateEntity(collaborator, request);
        Collaborator updatedCollaborator = collaboratorRepository.save(collaborator);

        log.info("Updated collaborator with ID: {}", id);
        return collaboratorMapper.toResponse(updatedCollaborator);
    }

    @Override
    @Transactional
    public void deleteCollaborator(UUID id) {
        log.info("Deleting collaborator with ID: {}", id);

        if (!collaboratorRepository.existsById(id)) {
            throw new CollaboratorNotFoundException(id);
        }

        collaboratorRepository.deleteById(id);
        log.info("Deleted collaborator with ID: {}", id);
    }

    @Override
    public boolean existsById(UUID id) {
        return collaboratorRepository.existsById(id);
    }

    private Collaborator findCollaboratorById(UUID id) {
        return collaboratorRepository.findById(id)
                .orElseThrow(() -> new CollaboratorNotFoundException(id));
    }

    private void validateUniqueConstraints(CollaboratorRequest request, UUID excludeId) {
        if (excludeId == null) {
            if (collaboratorRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Collaborator", "email", request.getEmail());
            }
            if (collaboratorRepository.existsByEmployeeCode(request.getEmployeeCode())) {
                throw new DuplicateResourceException("Collaborator", "employeeCode", request.getEmployeeCode());
            }
        } else {
            if (collaboratorRepository.existsByEmailAndIdNot(request.getEmail(), excludeId)) {
                throw new DuplicateResourceException("Collaborator", "email", request.getEmail());
            }
            if (collaboratorRepository.existsByEmployeeCodeAndIdNot(request.getEmployeeCode(), excludeId)) {
                throw new DuplicateResourceException("Collaborator", "employeeCode", request.getEmployeeCode());
            }
        }
    }

    private void enrichWithPerformanceData(CollaboratorDetailResponse response, UUID collaboratorId) {
        // Get average rating
        Optional<BigDecimal> avgRating = performanceReviewRepository
                .calculateAverageRatingByCollaboratorId(collaboratorId);
        response.setAverageRating(avgRating.orElse(null));

        // Get total reviews count
        long totalReviews = performanceReviewRepository.countByCollaboratorId(collaboratorId);
        response.setTotalReviews((int) totalReviews);

        // Determine renewal eligibility
        boolean isEligible = avgRating.isPresent() &&
                avgRating.get().compareTo(new BigDecimal("3.00")) >= 0;
        response.setIsEligibleForRenewal(isEligible);

        // Get latest review
        Optional<PerformanceReview> latestReview = performanceReviewRepository
                .findTopByCollaboratorIdOrderByReviewPeriodEndDesc(collaboratorId);
        latestReview.ifPresent(review ->
                response.setLatestReview(performanceReviewMapper.toResponse(review)));
    }
}
