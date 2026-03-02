package com.cms.collaborator.repository;

import com.cms.collaborator.entity.PerformanceReview;
import com.cms.collaborator.entity.enums.PerformanceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, UUID> {

    List<PerformanceReview> findByCollaboratorIdOrderByCreatedAtDesc(UUID collaboratorId);

    Page<PerformanceReview> findByCollaboratorId(UUID collaboratorId, Pageable pageable);

    Optional<PerformanceReview> findTopByCollaboratorIdOrderByReviewPeriodEndDesc(UUID collaboratorId);

    List<PerformanceReview> findByCollaboratorIdAndReviewPeriodEndAfter(UUID collaboratorId, LocalDate date);

    @Query("SELECT AVG(pr.rating) FROM PerformanceReview pr WHERE pr.collaborator.id = :collaboratorId")
    Optional<BigDecimal> calculateAverageRatingByCollaboratorId(@Param("collaboratorId") UUID collaboratorId);

    @Query("SELECT COUNT(pr) FROM PerformanceReview pr WHERE pr.collaborator.id = :collaboratorId")
    long countByCollaboratorId(@Param("collaboratorId") UUID collaboratorId);

    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.collaborator.id = :collaboratorId " +
            "AND pr.isEligibleRenewal = true ORDER BY pr.reviewPeriodEnd DESC")
    List<PerformanceReview> findEligibleReviewsByCollaboratorId(@Param("collaboratorId") UUID collaboratorId);

    @Query("SELECT CASE WHEN COUNT(pr) > 0 THEN true ELSE false END " +
            "FROM PerformanceReview pr WHERE pr.collaborator.id = :collaboratorId " +
            "AND pr.isEligibleRenewal = true " +
            "AND pr.reviewPeriodEnd >= :sinceDate")
    boolean hasRecentEligibleReview(
            @Param("collaboratorId") UUID collaboratorId,
            @Param("sinceDate") LocalDate sinceDate
    );

    List<PerformanceReview> findByPerformanceCategory(PerformanceCategory category);

    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.rating >= :minRating")
    List<PerformanceReview> findByRatingGreaterThanOrEqual(@Param("minRating") BigDecimal minRating);

    @Query("SELECT pr FROM PerformanceReview pr WHERE " +
            "pr.reviewPeriodStart >= :startDate AND pr.reviewPeriodEnd <= :endDate")
    List<PerformanceReview> findByReviewPeriodBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT pr.collaborator.id, AVG(pr.rating) as avgRating " +
            "FROM PerformanceReview pr " +
            "GROUP BY pr.collaborator.id " +
            "HAVING AVG(pr.rating) >= :minAvgRating")
    List<Object[]> findCollaboratorsWithMinAverageRating(@Param("minAvgRating") BigDecimal minAvgRating);

    boolean existsByCollaboratorIdAndReviewPeriodStartAndReviewPeriodEnd(
            UUID collaboratorId,
            LocalDate reviewPeriodStart,
            LocalDate reviewPeriodEnd
    );
}
