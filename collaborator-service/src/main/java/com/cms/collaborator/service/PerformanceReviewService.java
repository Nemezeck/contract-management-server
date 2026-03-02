package com.cms.collaborator.service;

import com.cms.collaborator.dto.request.PerformanceReviewRequest;
import com.cms.collaborator.dto.response.AverageRatingResponse;
import com.cms.collaborator.dto.response.PerformanceReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PerformanceReviewService {

    PerformanceReviewResponse submitReview(PerformanceReviewRequest request);

    PerformanceReviewResponse getReviewById(UUID id);

    Page<PerformanceReviewResponse> getReviewsByCollaborator(UUID collaboratorId, Pageable pageable);

    List<PerformanceReviewResponse> getAllReviewsByCollaborator(UUID collaboratorId);

    PerformanceReviewResponse getLatestReview(UUID collaboratorId);

    AverageRatingResponse calculateAverageRating(UUID collaboratorId);

    boolean isEligibleForRenewal(UUID collaboratorId);
}
