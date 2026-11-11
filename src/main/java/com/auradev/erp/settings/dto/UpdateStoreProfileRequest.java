package com.auradev.erp.settings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateStoreProfileRequest(
        @NotBlank String name,
        String phone,
        String gstin,
        String stateCode,
        String address,
        @NotBlank String billNoPrefix,
        String billFooter,
        @Pattern(regexp = "QAR|INR", message = "Currency must be QAR or INR") String currency
) {}
