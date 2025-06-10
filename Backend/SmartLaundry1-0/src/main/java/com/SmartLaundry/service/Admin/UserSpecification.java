package com.SmartLaundry.service.Admin;

import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

    public static Specification<Users> searchByEmailOrPhone(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction(); // Always true
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNo")), likePattern)
            );
        };
    }

    public static Specification<Users> joinDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction(); // Always true
            } else if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("createdAt"), startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay());
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate.plusDays(1).atStartOfDay());
            }
        };
    }

    public static Specification<Users> hasRole(UserRole role) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), role);
    }
}

