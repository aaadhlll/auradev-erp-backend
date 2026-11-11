package com.auradev.erp.billing.dto;

import com.auradev.erp.billing.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** One returned product line, retained for return reporting and CSV export. */
public record SalesReturnReportRow(
        UUID id,
        String returnNo,
        UUID billId,
        String billNo,
        String customerName,
        String productName,
        String sku,
        BigDecimal quantity,
        BigDecimal refundAmount,
        PaymentMethod refundMethod,
        String reason,
        String processedBy,
        Instant createdAt
) {}
