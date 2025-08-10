package com.SmartLaundry.repository;

import com.SmartLaundry.model.FeedbackProviders;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.FeedbackAgents;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackAgentsRepository extends JpaRepository<FeedbackAgents, Long> {

    @Query("SELECT f FROM FeedbackAgents f WHERE f.createdAt BETWEEN :start AND :end AND f.deliveryAgent.users.userId = :userId")
    List<FeedbackAgents> findByDateAndId(@Param("userId") String userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT f FROM FeedbackAgents f WHERE f.deliveryAgent.users.userId = :userId")
    List<FeedbackAgents> findById(@Param("userId") String userId);

    @Query("SELECT COUNT(*) FROM FeedbackAgents f WHERE f.deliveryAgent.deliveryAgentId = :agentId AND f.createdAt BETWEEN :start AND :end")
    Long findReviewCountByAgentIdAndDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("agentId") String agentId);

    @Query("SELECT COUNT(*) FROM FeedbackAgents f WHERE f.deliveryAgent.deliveryAgentId = :agentId")
    Long findReviewCountByAgentId(@Param("agentId") String agentId);

    @Query("SELECT AVG(f.rating) FROM FeedbackAgents f WHERE f.deliveryAgent.deliveryAgentId = :agentId AND f.createdAt BETWEEN :start AND :end")
    Double findAverageRatingByAgentIdAndDateRange(
            @Param("agentId") String agentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT AVG(f.rating) FROM FeedbackAgents f WHERE f.deliveryAgent.deliveryAgentId = :agentId")
    Double findAverageRatingByAgentId(@Param("agentId") String agentId);
}

