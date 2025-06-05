package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.exception.ExceptionMsg;
import com.SmartLaundry.exception.FormatException;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.util.UsernameUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DeliveryAgentProfileService {

    @Autowired
    private UsernameUtil usernameUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

//    public DeliveryAgentCompleteProfileResponseDTO registerProfileDetails(
//            @Valid DeliveryAgentCompleteProfileRequestDTO request, String username) {
//
//        // Validation
//        if(request.getVehicleNumber().matches(null) || request.getVehicleNumber().isBlank()){
//            throw new ExceptionMsg("Vehicle number is required.");
//        }
//
//        if(!request.getAccountHolderName().matches("^[A-Za-z\\s]+$")){
//            throw new FormatException("Account holder name contains invalid characters.");
//        }
//
//        if(!request.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")){
//            throw new FormatException("Invalid IFSC Code format.");
//        }
//
//        Optional<Users> userDetail;
//
//        if(usernameUtil.isEmail(username)){
//            userDetail = userRepository.findByEmail(username);
//        } else {
//            userDetail = userRepository.findByPhoneNo(username);
//        }
//
//        Users user = userDetail.orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Set null if user does not provide pancard
//        if (request.getPanCardPhoto() != null && (request.getPanCardPhoto().length == 0)) {
//            request.setPanCardPhoto(null);
//        }
//
//        // Store delivery agent data.
//        DeliveryAgent deliveryAgent = new DeliveryAgent();
//        deliveryAgent.setProfilePhoto(request.getProfilePhoto());
//        deliveryAgent.setGender(request.getGender());
//        deliveryAgent.setUsers(user);
//        deliveryAgent.setDateOfBirth(request.getDateOfBirth());
//        deliveryAgent.setVehicleNumber(request.getVehicleNumber());
//        deliveryAgent.setAadharCardPhoto(request.getAadharCardPhoto());
//        deliveryAgent.setDrivingLicensePhoto(request.getDrivingLicensePhoto());
//        deliveryAgent.setPanCardPhoto(request.getPanCardPhoto());
//        deliveryAgent.setBankName(request.getBankName());
//        deliveryAgent.setAccountHolderName(request.getAccountHolderName());
//        deliveryAgent.setBankAccountNumber(request.getBankAccountNumber());
//        deliveryAgent.setIfscCode(request.getIfscCode());
//
//        deliveryAgentRepository.save(deliveryAgent);
//
//        return new DeliveryAgentCompleteProfileResponseDTO(user.getFirstName() + user.getLastName(), "Your request is send successfully. Wait for a response. ");
//    }
}
