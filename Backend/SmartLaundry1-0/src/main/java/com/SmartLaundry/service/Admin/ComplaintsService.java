package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.ChartPointDTO;
import com.SmartLaundry.dto.Admin.ComplaintResponseDTO;
import com.SmartLaundry.dto.Admin.ComplaintsListResponseDTO;
import com.SmartLaundry.dto.Admin.ComplaintsSummaryResponseDTO;
import com.SmartLaundry.model.Ticket;
import com.SmartLaundry.model.TicketStatus;
import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.ComplaintsRepository;
import com.SmartLaundry.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintsService {

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    private ComplaintsRepository complaintsRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Return summary count of complaints based filter
    public ComplaintsSummaryResponseDTO getSummary(String userId, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        Long totalComplaints, customerComplaints, serviceProviderComplaints, deliveryAgentComplaints, respondedComplaints,
        nonRespondedComplaints;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                totalComplaints = complaintsRepository.findTotalComplaintsCountByDateRange(start, end);
                customerComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                deliveryAgentComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                serviceProviderComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                respondedComplaints = complaintsRepository.findRespondedComplaintsCountByDateRange(start, end);
                nonRespondedComplaints = complaintsRepository.findNotRespondedComplaintsCountByDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                totalComplaints = complaintsRepository.findTotalComplaintsCountByDateRange(start, end);
                customerComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                deliveryAgentComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                serviceProviderComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                respondedComplaints = complaintsRepository.findRespondedComplaintsCountByDateRange(start, end);
                nonRespondedComplaints = complaintsRepository.findNotRespondedComplaintsCountByDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                totalComplaints = complaintsRepository.findTotalComplaintsCountByDateRange(start, end);
                customerComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                deliveryAgentComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                serviceProviderComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                respondedComplaints = complaintsRepository.findRespondedComplaintsCountByDateRange(start, end);
                nonRespondedComplaints = complaintsRepository.findNotRespondedComplaintsCountByDateRange(start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                totalComplaints = complaintsRepository.findTotalComplaintsCountByDateRange(start, end);
                customerComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                deliveryAgentComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                serviceProviderComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                respondedComplaints = complaintsRepository.findRespondedComplaintsCountByDateRange(start, end);
                nonRespondedComplaints = complaintsRepository.findNotRespondedComplaintsCountByDateRange(start, end);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                totalComplaints = complaintsRepository.findTotalComplaintsCountByDateRange(start, end);
                customerComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                deliveryAgentComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                serviceProviderComplaints = complaintsRepository.findComplaintsCountByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                respondedComplaints = complaintsRepository.findRespondedComplaintsCountByDateRange(start, end);
                nonRespondedComplaints = complaintsRepository.findNotRespondedComplaintsCountByDateRange(start, end);
        }

        ComplaintsSummaryResponseDTO complaintsSummaryResponseDTO = ComplaintsSummaryResponseDTO.builder()
                .totalComplaints(totalComplaints)
                .customerComplaints(customerComplaints)
                .deliveryAgentComplaints(deliveryAgentComplaints)
                .serviceProviderComplaints(serviceProviderComplaints)
                .respondedComplaints(respondedComplaints)
                .nonRespondedComplaints(nonRespondedComplaints)
                .build();

        return complaintsSummaryResponseDTO;
    }

    // Return list of total complaints based on filter
    public List<ComplaintsListResponseDTO> getTotal(String userId, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Ticket> ticketList = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findTotalComplaintsByDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findTotalComplaintsByDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findTotalComplaintsByDateRange(start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                ticketList = complaintsRepository.findTotalComplaintsByDateRange(start, end);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findTotalComplaintsByDateRange(start, end);
        }

        List<ComplaintsListResponseDTO> dto = new ArrayList<>();

        for(Ticket ticket : ticketList){
            ComplaintsListResponseDTO complaintsListResponseDTO = ComplaintsListResponseDTO.builder()
                    .complaintId(ticket.getTicketId())
                    .userName(ticket.getUser().getFirstName() + ticket.getUser().getLastName())
                    .userType(ticket.getUser().getRole())
                    .submittedAt(ticket.getSubmittedAt())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                            ? ("/complaints/image/" + ticket.getTicketId())
                            : null)
                    .category(ticket.getCategory())
                    .response(ticket.getResponse())
                    .status(ticket.getStatus())
                    .respondedAt(ticket.getRespondedAt())
                    .response(ticket.getResponse())
                    .build();

            dto.add(complaintsListResponseDTO);
            System.out.println(Boolean.parseBoolean("Photo : " + ticket.getPhoto()) ? ("/complaints/image/" + ticket.getTicketId()) : null);

        }

        return dto;
    }

    // Return list of customer complaints based on filter
    public List<ComplaintsListResponseDTO> getCustomer(String userId, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Ticket> ticketList = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.CUSTOMER);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.CUSTOMER);
        }

        List<ComplaintsListResponseDTO> dto = new ArrayList<>();

        for(Ticket ticket : ticketList){
            ComplaintsListResponseDTO complaintsListResponseDTO = ComplaintsListResponseDTO.builder()
                    .complaintId(ticket.getTicketId())
                    .userName(ticket.getUser().getFirstName() + ticket.getUser().getLastName())
                    .submittedAt(ticket.getSubmittedAt())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                                    ? ("/complaints/image/" + ticket.getTicketId())
                                    : null)
                    .category(ticket.getCategory())
                    .response(ticket.getResponse())
                    .status(ticket.getStatus())
                    .respondedAt(ticket.getRespondedAt())
                    .response(ticket.getResponse())
                    .build();

            dto.add(complaintsListResponseDTO);
        }

        return dto;
    }

    // Return list of delivery agent complaints based on filter
    public List<ComplaintsListResponseDTO> getDeliveryAgent(String userId, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Ticket> ticketList = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.DELIVERY_AGENT);
        }

        List<ComplaintsListResponseDTO> dto = new ArrayList<>();

        for(Ticket ticket : ticketList){
            ComplaintsListResponseDTO complaintsListResponseDTO = ComplaintsListResponseDTO.builder()
                    .complaintId(ticket.getTicketId())
                    .userName(ticket.getUser().getFirstName() + ticket.getUser().getLastName())
                    .submittedAt(ticket.getSubmittedAt())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                                    ? ("/complaints/image/" + ticket.getTicketId())
                                    : null)
                    .category(ticket.getCategory())
                    .response(ticket.getResponse())
                    .status(ticket.getStatus())
                    .respondedAt(ticket.getRespondedAt())
                    .response(ticket.getResponse())
                    .build();

            dto.add(complaintsListResponseDTO);
        }

        return dto;

    }

    // Return list of service provider complaints based on filter
    public List<ComplaintsListResponseDTO> getServiceProvider(String userId, String filter, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Ticket> ticketList = new ArrayList<>();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                ticketList = complaintsRepository.findComplaintsByDateRangeAndRole(start, end, UserRole.SERVICE_PROVIDER);
        }

        List<ComplaintsListResponseDTO> dto = new ArrayList<>();

        for(Ticket ticket : ticketList){
            ComplaintsListResponseDTO complaintsListResponseDTO = ComplaintsListResponseDTO.builder()
                    .complaintId(ticket.getTicketId())
                    .userName(ticket.getUser().getFirstName() + ticket.getUser().getLastName())
                    .submittedAt(ticket.getSubmittedAt())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                                    ? ("/complaints/image/" + ticket.getTicketId())
                                    : null)
                    .category(ticket.getCategory())
                    .response(ticket.getResponse())
                    .status(ticket.getStatus())
                    .respondedAt(ticket.getRespondedAt())
                    .response(ticket.getResponse())
                    .build();

            dto.add(complaintsListResponseDTO);
        }

        return dto;

    }

    // Return list of responded complaints based on filter
    public List<ComplaintsListResponseDTO> getResponded(String userId, String filter, UserRole role, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Ticket> ticketList = new ArrayList<>();

        boolean filterByUserRole = role != null;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findRespondedComplaintsByDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findRespondedComplaintsByDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findRespondedComplaintsByDateRange(start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                ticketList = filterByUserRole
                        ? complaintsRepository.findRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findRespondedComplaintsByDateRange(start, end);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findRespondedComplaintsByDateRange(start, end);
        }

        List<ComplaintsListResponseDTO> dto = new ArrayList<>();

        for(Ticket ticket : ticketList){
            ComplaintsListResponseDTO complaintsListResponseDTO = ComplaintsListResponseDTO.builder()
                    .complaintId(ticket.getTicketId())
                    .userName(ticket.getUser().getFirstName() + ticket.getUser().getLastName())
                    .userType(ticket.getUser().getRole())
                    .submittedAt(ticket.getSubmittedAt())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                            ? ("/complaints/image/" + ticket.getTicketId())
                            : null)
                    .category(ticket.getCategory())
                    .respondedAt(ticket.getRespondedAt())
                    .response(ticket.getResponse())
                    .build();

            dto.add(complaintsListResponseDTO);
        }

        return dto;
    }

    // Return list of non-responded complaints based on filter
    public List<ComplaintsListResponseDTO> getNonResponded(String userId, String filter, UserRole role, LocalDate startDate, LocalDate endDate) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime start, end;

        List<Ticket> ticketList = new ArrayList<>();

        boolean filterByUserRole = role != null;

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findNotRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findNotRespondedComplaintsByDateRange(start, end);
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findNotRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findNotRespondedComplaintsByDateRange(start, end);
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findNotRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findNotRespondedComplaintsByDateRange(start, end);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                ticketList = filterByUserRole
                        ? complaintsRepository.findNotRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findNotRespondedComplaintsByDateRange(start, end);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = today;
                ticketList = filterByUserRole
                        ? complaintsRepository.findNotRespondedComplaintsByDateRangeAndRole(start, end, role)
                        : complaintsRepository.findNotRespondedComplaintsByDateRange(start, end);
        }

        List<ComplaintsListResponseDTO> dto = new ArrayList<>();

        for(Ticket ticket : ticketList){
            ComplaintsListResponseDTO complaintsListResponseDTO = ComplaintsListResponseDTO.builder()
                    .complaintId(ticket.getTicketId())
                    .userName(ticket.getUser().getFirstName() + ticket.getUser().getLastName())
                    .userType(ticket.getUser().getRole())
                    .submittedAt(ticket.getSubmittedAt())
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .photo(ticket.getPhoto() != null && !ticket.getPhoto().isBlank()
                            ? ("/complaints/image/" + ticket.getTicketId())
                            : null)
                    .category(ticket.getCategory())
                    .response(ticket.getResponse())
                    .build();

            dto.add(complaintsListResponseDTO);
        }

        return dto;

    }

    public String giveResponse(String userId, Long id, ComplaintResponseDTO complaintResponseDTO) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found."));

        ticket.setResponse(complaintResponseDTO.getResponse());
        ticket.setStatus(TicketStatus.RESPONDED);
        ticket.setRespondedAt(LocalDateTime.now());

        ticketRepository.save(ticket);

        return "Complaint responded successfully.";

    }

    // Return chart based data based on complaints
    public List<ChartPointDTO> getComplaintsChartDataFiltered(String userId, String filter, LocalDate startDate, LocalDate endDate, UserRole role, String responseType) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (filter.toLowerCase()) {
            case "today":
                start = LocalDate.now().atStartOfDay();
                break;
            case "this week":
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                break;
            case "this month":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end dates are required for custom filter.");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
                break;
            case "overall":
            default:
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                break;
        }

        List<Object[]> rawData;

        // Choose correct repository method
        if ("responded".equalsIgnoreCase(responseType)) {
            rawData = complaintsRepository.getComplaintCountByDateAndRoleResponded(start, end, String.valueOf(role));
        } else if ("not_responded".equalsIgnoreCase(responseType)) {
            rawData = complaintsRepository.getComplaintCountByDateAndRoleNotResponded(start, end, String.valueOf(role));
        } else {
            rawData = complaintsRepository.getComplaintCountByDateAndRole(start, end, String.valueOf(role));
        }

        return rawData.stream()
                .map(obj -> new ChartPointDTO(
                        (String) obj[0],                // label = date
                        ((Number) obj[1]).longValue()   // value = count
                ))
                .collect(Collectors.toList());
    }

    // Return chart based data based on category complaints
    public List<ChartPointDTO> getComplaintCategoryAnalytics(String userId, String filter, LocalDate startDate, LocalDate endDate, UserRole role, String responseType) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);

        LocalDateTime start, end;
        LocalDateTime now = LocalDateTime.now();

        switch (filter.toLowerCase()) {
            case "today" :
                start = LocalDate.now().atStartOfDay();
                end = now;
            break;
            case "this week" :
                start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
                end = now;
            break;
            case "this month" :
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                end = now;
            break;
            case "custom" :
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("Start and end date required for custom filter");
                }
                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
            break;
            case "overall" :
            default :
                start = LocalDate.now().withDayOfYear(1).atStartOfDay();
                end = now;
        }

        String roleStr = (role != null) ? role.name() : null;

        List<Object[]> rawData = complaintsRepository.getComplaintCountByCategory(start, end, roleStr, responseType);

        return rawData.stream()
                .map(obj -> new ChartPointDTO((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }


}
