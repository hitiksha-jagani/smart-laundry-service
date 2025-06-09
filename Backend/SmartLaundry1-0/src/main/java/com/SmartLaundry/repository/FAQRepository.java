package com.SmartLaundry.repository;
import com.SmartLaundry.model.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {

    // Find all visible FAQs
    List<FAQ> findByVisibilityStatusTrue();

    // Find all FAQs by ticket
    List<FAQ> findByTicket_TicketId(Long ticketId);
}

