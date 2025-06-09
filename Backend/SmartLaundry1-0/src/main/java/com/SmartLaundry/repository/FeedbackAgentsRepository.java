package com.SmartLaundry.repository;

import com.SmartLaundry.model.FeedbackProviders;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.FeedbackAgents;

import java.util.List;

public interface FeedbackAgentsRepository extends JpaRepository<FeedbackAgents, Long> {

}

