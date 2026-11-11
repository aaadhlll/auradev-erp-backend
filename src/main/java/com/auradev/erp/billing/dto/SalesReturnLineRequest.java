package com.auradev.erp.billing.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
public record SalesReturnLineRequest(@NotNull UUID billItemId, @NotNull @DecimalMin("0.001") BigDecimal quantity) {}
