package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.model.TicketStatus;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByUserUserId(String userId);

    List<Ticket> findByUser(Users user);

    @Query("SELECT t FROM Ticket t WHERE t.category = :category AND t.user.userId = :userId")
    List<Ticket> findTicketsByCategoryAndUser(@Param("category") String category,@Param("userId") String userId);

    @Query("SELECT t FROM Ticket t WHERE t.status = :status AND t.user.userId = :userId")
    List<Ticket> findTicketByStatusAndUser(@Param("status") TicketStatus status, @Param("userId") String userId);
}

