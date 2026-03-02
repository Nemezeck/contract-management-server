package com.cms.collaborator.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AverageRatingResponse {

    private UUID collaboratorId;
    private String collaboratorName;
    private BigDecimal averageRating;
    private Long totalReviews;
    private Boolean isEligibleForRenewal;
}
