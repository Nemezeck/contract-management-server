package com.cms.contract.client;

import com.cms.common.dto.ApiResponse;
import com.cms.contract.client.dto.AverageRatingResponse;
import com.cms.contract.client.dto.CollaboratorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "collaborator-service",
        url = "${collaborator.service.url:http://localhost:8081}",
        fallback = CollaboratorClientFallback.class
)
public interface CollaboratorClient {

    @GetMapping("/api/v1/collaborators/{id}")
    ApiResponse<CollaboratorResponse> getCollaboratorById(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/collaborators/{id}/exists")
    ApiResponse<Boolean> checkCollaboratorExists(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/performance-reviews/collaborator/{collaboratorId}/average-rating")
    ApiResponse<AverageRatingResponse> getAverageRating(@PathVariable("collaboratorId") UUID collaboratorId);

    @GetMapping("/api/v1/performance-reviews/collaborator/{collaboratorId}/renewal-eligibility")
    ApiResponse<Boolean> isEligibleForRenewal(@PathVariable("collaboratorId") UUID collaboratorId);
}
