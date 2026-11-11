package com.auradev.erp.billing.dto;
import com.auradev.erp.billing.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
public record SalesReturnResponse(UUID id, String returnNo, UUID billId, String reason, PaymentMethod refundMethod,
                                  BigDecimal refundTotal, Instant createdAt, List<SalesReturnLineResponse> items) {}
