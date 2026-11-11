package com.auradev.erp.billing.dto;

import com.auradev.erp.billing.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CustomerCreditPaymentResponse(
        UUID id,
        String paymentNo,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String notes,
        String receivedBy,
        Instant createdAt,
        BigDecimal balanceAfter
) {}
