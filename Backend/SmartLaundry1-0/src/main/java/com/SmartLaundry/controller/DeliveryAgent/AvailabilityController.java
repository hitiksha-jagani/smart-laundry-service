package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.AvailabilityDTO;
import com.SmartLaundry.service.DeliveryAgent.AvailabilityService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AvailabilityService availabilityService;

    //@author Hitiksha Jagani
    // http://localhost:8080/availability/manage
    // Render a form to set availability for current week.
    @PostMapping("/manage")
    public ResponseEntity<String> saveAvailability(HttpServletRequest request,
                                              @RequestBody List<AvailabilityDTO> dto) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(availabilityService.saveAvailability(userId, dto));
    }

    //@author Hitiksha Jagani
    // http://localhost:8080/availability/saved
    // Return all the availability list of the week with edit option.
    @GetMapping("/saved")
    public ResponseEntity<?> getAvailability(HttpServletRequest request) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(availabilityService.getThisWeeksAvailability(userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/availability/manage/delete/{id}
    // Delete selected availability
    @DeleteMapping("/manage/delete/{id}")
    public ResponseEntity<?> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.ok("Deleted");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/availability/manage/edit/{id}
    // Edit selected availability
    @PutMapping("/manage/edit/{id}")
    public ResponseEntity<?> updateAvailability(@PathVariable Long id,
                                                @RequestBody AvailabilityDTO dto) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, dto));
    }

}
