package com.SmartLaundry.repository;

import com.SmartLaundry.model.FeedbackProviders;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.FeedbackAgents;

import java.util.List;

public interface FeedbackAgentsRepository extends JpaRepository<FeedbackAgents, Long> {
    List<FeedbackProviders> findByOrder(Order order);
    List<FeedbackProviders> findByUserAndOrder(Users user, Order order);


}

