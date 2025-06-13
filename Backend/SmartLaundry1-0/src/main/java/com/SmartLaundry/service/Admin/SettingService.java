package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.ComplaintCategoryResponseDTO;
import com.SmartLaundry.dto.Admin.ComplaintCategorySetDTO;
import com.SmartLaundry.model.ComplaintCategory;
import com.SmartLaundry.repository.ComplaintCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

    @Autowired
    private ComplaintCategoryRepository complaintCategoryRepository;

    // Set complaint category
    public ComplaintCategoryResponseDTO setComplaintCategory(String userId, ComplaintCategorySetDTO complaintCategorySetDTO) {

        ComplaintCategory complaintCategory = ComplaintCategory.builder()
                .categoryName(complaintCategorySetDTO.getCategory())
                .build();

        complaintCategoryRepository.save(complaintCategory);

        return new ComplaintCategoryResponseDTO(complaintCategorySetDTO.getCategory(), "Category set successfully.");
    }
}
