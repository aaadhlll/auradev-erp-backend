package com.auradev.erp.billing.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record SalesReturnLineResponse(UUID billItemId, UUID productId, String name, BigDecimal quantity, BigDecimal refundAmount) {}
