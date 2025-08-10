package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.ChartPointDTO;
import com.SmartLaundry.model.FeedbackAgents;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintsRepository extends JpaRepository<Ticket, Long> {

    // Return count of tickets

    // Return count of total tickets which is raised between date range
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.submittedAt BETWEEN :start AND :end")
    Long findTotalComplaintsCountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Return count of tickets which is raised between date range and based on role
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.user.role = :role AND t.submittedAt BETWEEN :start AND :end")
    Long findComplaintsCountByDateRangeAndRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") UserRole role);

    // Return count of tickets which is raised and responded between date range
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.respondedAt IS NOT NULL AND t.submittedAt BETWEEN :start AND :end")
    Long findRespondedComplaintsCountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Return count of tickets which is raised and not responded between date range
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.respondedAt IS NULL AND t.submittedAt BETWEEN :start AND :end")
    Long findNotRespondedComplaintsCountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Return count of total tickets which is raised overall
    @Query("SELECT COUNT(t) FROM Ticket t")
    Long findTotalComplaintsCount();

    // Return count of total tickets which is raised based on role overall
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.user.role = :role")
    Long findComplaintsCount(UserRole role);

    // Return count of tickets which is raised and responded overall
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.respondedAt IS NOT NULL")
    Long findRespondedComplaintsCount();

    // Return count of tickets which is raised and not responded overall
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.respondedAt IS NULL")
    Long findNotRespondedComplaintsCount();

    // Return list of tickets

    // Return list of total tickets which is raised between date range
    @Query("SELECT t FROM Ticket t WHERE t.submittedAt BETWEEN :start AND :end")
    List<Ticket> findTotalComplaintsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Return list of tickets which is raised based on role and between date range
    @Query("SELECT t FROM Ticket t WHERE t.user.role = :role AND t.submittedAt BETWEEN :start AND :end")
    List<Ticket> findComplaintsByDateRangeAndRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") UserRole role);

    // Return list of tickets which is raised and responded between date range
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NOT NULL AND t.submittedAt BETWEEN :start AND :end")
    List<Ticket> findRespondedComplaintsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Return list of tickets which is raised and responded between date range and for particular user
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NOT NULL AND t.submittedAt BETWEEN :start AND :end AND t.user.role = :role")
    List<Ticket> findRespondedComplaintsByDateRangeAndRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") UserRole role);

    // Return count of tickets which is raised and not responded between date range
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NULL AND t.submittedAt BETWEEN :start AND :end")
    List<Ticket> findNotRespondedComplaintsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Return count of tickets which is raised and not responded between date range by user role
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NULL AND t.submittedAt BETWEEN :start AND :end AND t.user.role = :role")
    List<Ticket> findNotRespondedComplaintsByDateRangeAndRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") UserRole role);

    // Return list of tickets which is raised overall
    @Query("SELECT t FROM Ticket t")
    List<Ticket> findTotalComplaints();

    // Return list of tickets which is raised based on user role and overall
    @Query("SELECT t FROM Ticket t WHERE t.user.role = :role")
    List<Ticket> findComplaintsByRole(@Param("role") UserRole role);

    // Return list of tickets which is raised and responded overall
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NOT NULL")
    List<Ticket> findRespondedComplaints();

    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NOT NULL AND t.user.role = :role")
    List<Ticket> findRespondedComplaintsByRole(@Param("role") UserRole role);

    // Return list of tickets which is raised and not responded overall
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NULL")
    List<Ticket> findNotRespondedComplaints();

    // Return list of tickets which is raised and not responded overall by user role
    @Query("SELECT t FROM Ticket t WHERE t.respondedAt IS NULL AND t.user.role = :role")
    List<Ticket> findNotRespondedComplaintsByRole(@Param("role") UserRole role);

    // Return count of complaints register for each day between date range for role
    @Query(value = """
    SELECT TO_CHAR(t.submitted_at, 'YYYY-MM-DD') AS label, COUNT(*) AS value
    FROM ticket t
    JOIN users u ON t.user_id = u.user_id
    WHERE t.submitted_at BETWEEN :start AND :end
      AND (:role IS NULL OR u.role = :role)
    GROUP BY TO_CHAR(t.submitted_at, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(t.submitted_at, 'YYYY-MM-DD')""", nativeQuery = true)
    List<Object[]> getComplaintCountByDateAndRole(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("role") String role);

    // Return count of complaints register for each day between date range for responded complaints
    @Query(value = """
    SELECT TO_CHAR(t.submitted_at, 'YYYY-MM-DD') AS label, COUNT(*) AS value
    FROM ticket t
    JOIN users u ON t.user_id = u.user_id
    WHERE t.submitted_at BETWEEN :start AND :end
      AND t.responded_at IS NOT NULL
      AND (:role IS NULL OR u.role = :role)
    GROUP BY TO_CHAR(t.submitted_at, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(t.submitted_at, 'YYYY-MM-DD')""", nativeQuery = true)
    List<Object[]> getComplaintCountByDateAndRoleResponded(@Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end,
                                                           @Param("role") String role);

    // Return count of complaints register for each day between date range for non responded complaints
    @Query(value = """
    SELECT TO_CHAR(t.submitted_at, 'YYYY-MM-DD') AS label, COUNT(*) AS value
    FROM ticket t
    JOIN users u ON t.user_id = u.user_id
    WHERE t.submitted_at BETWEEN :start AND :end
      AND t.responded_at IS NULL
      AND (:role IS NULL OR u.role = :role)
    GROUP BY TO_CHAR(t.submitted_at, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(t.submitted_at, 'YYYY-MM-DD')""", nativeQuery = true)
    List<Object[]> getComplaintCountByDateAndRoleNotResponded(@Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end,
                                                              @Param("role") String role);

    // Return chart based data based on category complaints
    @Query(value = """
    SELECT t.category AS label, COUNT(*) AS value
    FROM ticket t
    JOIN users u ON t.user_id = u.user_id
    WHERE (:role IS NULL OR u.role = :role)
      AND (:responseType = 'all'
           OR (:responseType = 'responded' AND t.responded_at IS NOT NULL)
           OR (:responseType = 'not_responded' AND t.responded_at IS NULL))
      AND t.submitted_at BETWEEN :startDate AND :endDate
    GROUP BY t.category
    ORDER BY value DESC""", nativeQuery = true)
    List<Object[]> getComplaintCountByCategory(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("role") String role,
                                               @Param("responseType") String responseType);



}
