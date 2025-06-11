package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.FeedbackResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.FeedbackSummaryResponseDTO;
import com.SmartLaundry.service.DeliveryAgent.FeedbackService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

// @author Hitiksha Jagani
@RestController
@RequestMapping("")
public class FeedbackController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private FeedbackService feedbackService;

    // http://localhost:8080/feedback-summary
    // Return summary of feedbacks for time filter.
    @GetMapping("/feedback-summary")
    public ResponseEntity<FeedbackSummaryResponseDTO> getFeedbackSummary(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(feedbackService.getSummary(userId, filter, startDate, endDate));
    }

    // http://localhoct:8080/feedbacks/
    // Return list of feedback for time filter.
    @GetMapping("/feedbacks")
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbacks(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException{
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(feedbackService.getFeedbacks(userId, filter, startDate, endDate));
    }


}
