package com.auradev.erp.billing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BillLineResponse(
        UUID billItemId,
        UUID productId,
        String name,
        String sku,
        String unitLabel,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineDiscount,
        BigDecimal gstRate,
        BigDecimal lineTotal,
        BigDecimal returnedQuantity
) {}
