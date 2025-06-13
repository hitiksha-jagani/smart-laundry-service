package com.SmartLaundry.repository;

import com.SmartLaundry.model.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintCategoryRepository extends JpaRepository<ComplaintCategory, Long> {
}
