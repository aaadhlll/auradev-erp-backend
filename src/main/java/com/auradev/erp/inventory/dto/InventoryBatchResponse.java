package com.auradev.erp.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InventoryBatchResponse(
        UUID id,
        String batchNumber,
        LocalDate expiryDate,
        LocalDate receivedDate,
        BigDecimal quantityReceived,
        BigDecimal quantityOnHand,
        String notes
) {}
