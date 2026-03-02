package com.cms.contract.client;

import com.cms.common.dto.ApiResponse;
import com.cms.contract.client.dto.AverageRatingResponse;
import com.cms.contract.client.dto.CollaboratorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CollaboratorClientFallback implements CollaboratorClient {

    @Override
    public ApiResponse<CollaboratorResponse> getCollaboratorById(UUID id) {
        log.warn("Fallback: Unable to get collaborator with ID: {}", id);
        return ApiResponse.error("Collaborator service is unavailable");
    }

    @Override
    public ApiResponse<Boolean> checkCollaboratorExists(UUID id) {
        log.warn("Fallback: Unable to check if collaborator exists with ID: {}", id);
        return ApiResponse.error("Collaborator service is unavailable");
    }

    @Override
    public ApiResponse<AverageRatingResponse> getAverageRating(UUID collaboratorId) {
        log.warn("Fallback: Unable to get average rating for collaborator: {}", collaboratorId);
        return ApiResponse.error("Collaborator service is unavailable");
    }

    @Override
    public ApiResponse<Boolean> isEligibleForRenewal(UUID collaboratorId) {
        log.warn("Fallback: Unable to check renewal eligibility for collaborator: {}", collaboratorId);
        return ApiResponse.error("Collaborator service is unavailable");
    }
}
