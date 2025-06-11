package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Customer.RaiseTicketRequestDto;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.model.TicketStatus;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserUserId(String userId);

    List<Ticket> findByUser(Users user);

    List<Ticket> findByTitleOrCategoryOrStatus(String title, String category, TicketStatus status);
}

