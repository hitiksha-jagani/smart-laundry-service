package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ChartPointDTO;
import com.SmartLaundry.dto.Admin.ComplaintResponseDTO;
import com.SmartLaundry.dto.Admin.ComplaintsListResponseDTO;
import com.SmartLaundry.dto.Admin.ComplaintsSummaryResponseDTO;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.TicketRepository;
import com.SmartLaundry.repository.UserAddressRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Admin.ComplaintsService;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/complaints")
public class ComplaintsController {

    @Autowired
    private ComplaintsService complaintsService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    private TicketRepository ticketRepository;

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/summary
    // Return summary count of total complaints, customer complaints, service provider complaints, delivery agent
    // complaints, responded complaints, non-responded complaints
    @GetMapping("/summary")
    public ResponseEntity<ComplaintsSummaryResponseDTO> getComplaintsSummary(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException{
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getSummary(userId, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/total
    // Return list of total tickets raised based on filter
    @GetMapping("/total")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getTotalComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getTotal(userId, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/customer
    // Return list of tickets raised by customers based on filter
    @GetMapping("/customer")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getCustomerComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getCustomer(userId, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/customer/responded
    // Return list of responded tickets raised by customers based on filter
    @GetMapping("/customer/responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getCustomerRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getResponded(userId, filter, UserRole.CUSTOMER, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/customers/non-responded
    // Return list of tickets raised by customers and non-responded based on filter
    @GetMapping("/customer/non-responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getCustomerNonRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getNonResponded(userId, filter, UserRole.CUSTOMER, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/delivery-agent
    // Return list of tickets raised by delivery agent based on filter
    @GetMapping("/delivery-agent")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getDeliveryAgentComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getDeliveryAgent(userId, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/delivery-agent/responded
    // Return list of responded tickets raised by delivery agent based on filter
    @GetMapping("/delivery-agent/responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getDeliveryAgentRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getResponded(userId, filter, UserRole.DELIVERY_AGENT, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/delivery-agent/non-responded
    // Return list of tickets raised by delivery agent and non-responded based on filter
    @GetMapping("/delivery-agent/non-responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getDeliveryAgentNonRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getNonResponded(userId, filter, UserRole.DELIVERY_AGENT, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/service-provider
    // Return list of tickets raised by service provider based on filter
    @GetMapping("/service-provider")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getServiceProviderComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getServiceProvider(userId, filter, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/service-provider/responded
    // Return list of responded tickets raised by service provider based on filter
    @GetMapping("/service-provider/responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getServiceProviderRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getResponded(userId, filter, UserRole.SERVICE_PROVIDER, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/service-provider/non-responded
    // Return list of tickets raised by service provider and non-responded based on filter
    @GetMapping("/service-provider/non-responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getServiceProviderNonRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getNonResponded(userId, filter, UserRole.SERVICE_PROVIDER, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/responded
    // Return list of tickets raised and responded based on filter
    @GetMapping("/responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getResponded(userId, filter, role, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/non-responded
    // Return list of tickets raised and non-responded based on filter
    @GetMapping("/non-responded")
    public ResponseEntity<List<ComplaintsListResponseDTO>> getNonRespondedComplaints(
            HttpServletRequest request,
            @RequestParam(defaultValue = "Overall") String filter,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.getNonResponded(userId, filter, role, startDate, endDate));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/response/{id}
    // Return a form to respond ticket
    @PostMapping("/response/{id}")
    public ResponseEntity<String> giveResponse(HttpServletRequest request, @PathVariable Long id,
                                               @RequestBody ComplaintResponseDTO complaintResponseDTO) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(complaintsService.giveResponse(userId, id, complaintResponseDTO));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/image/{ticketId}
    // Display image
    @GetMapping("/image/{ticketId}")
    public ResponseEntity<byte[]> fetchTicketImage(
            @PathVariable Long ticketId) throws IOException {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not exist."));

        String imageData = ticket.getPhoto();

        File imageFile = new File(imageData);
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imagePath  = Files.readAllBytes(imageFile.toPath());

        // Detect file type
        String mimeType = Files.probeContentType(imageFile.toPath());
        MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imagePath);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/analytics
    // Return chart based data based on complaints
    @GetMapping("/analytics")
    public ResponseEntity<List<ChartPointDTO>> getComplaintAnalytics(
            HttpServletRequest request,
            @RequestParam String filter, // "today", "this week", "this month", "overall", "custom"
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UserRole role, // Customer, Delivery Agent, Service provider
            @RequestParam(defaultValue = "all") String responseType // "all", "responded", "not_responded"
    ) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        List<ChartPointDTO> chartData = complaintsService.getComplaintsChartDataFiltered( userId, filter, startDate, endDate, role, responseType
        );

        return ResponseEntity.ok(chartData);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complaints/analytics
    // Return chart based data based on category complaints
    @GetMapping("/category-chart")
    public ResponseEntity<List<ChartPointDTO>> getComplaintsByCategory(
            HttpServletRequest request,
            @RequestParam String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "all") String responseType
    ) throws AccessDeniedException {

        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        List<ChartPointDTO> data = complaintsService.getComplaintCategoryAnalytics(
                userId, filter, startDate, endDate, role, responseType
        );

        return ResponseEntity.ok(data);
    }



}
