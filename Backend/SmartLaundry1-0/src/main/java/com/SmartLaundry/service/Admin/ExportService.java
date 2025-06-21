package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.ExportRequestDTO;

public interface ExportService {
    byte[] export(ExportRequestDTO request);
}
