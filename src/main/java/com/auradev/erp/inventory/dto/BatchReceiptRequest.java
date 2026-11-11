package com.auradev.erp.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BatchReceiptRequest(
        @NotNull @Positive BigDecimal quantity,
        @NotBlank String batchNumber,
        LocalDate expiryDate,
        String notes
) {}
