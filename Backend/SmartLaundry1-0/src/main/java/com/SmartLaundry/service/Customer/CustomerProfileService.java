package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.UserUpdateDto;
import com.SmartLaundry.dto.LanguageUpdateDto;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerProfileService {

    private final UserRepository usersRepository;

    public void updateLanguage(LanguageUpdateDto dto) {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPreferredLanguage(dto.getLanguage());
        usersRepository.save(user);
    }

    public Users updateUserProfile(UserUpdateDto dto) {
        Users existingUser = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setPhoneNo(dto.getPhoneNo());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPreferredLanguage(dto.getPreferredLanguage());

        return usersRepository.save(existingUser);
    }

}
