package com.auradev.erp.billing.dto;

import com.auradev.erp.billing.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** One dated credit charge or settlement in a customer's khata ledger. */
public record CustomerCreditLedgerEntry(
        UUID id,
        String type,
        String reference,
        String description,
        BigDecimal debit,
        BigDecimal credit,
        BigDecimal balanceAfter,
        PaymentMethod paymentMethod,
        String notes,
        String processedBy,
        Instant createdAt
) {}
