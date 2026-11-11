package com.auradev.erp.billing.dto;
import com.auradev.erp.billing.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
public record CreateSalesReturnRequest(@NotBlank String reason, @NotNull PaymentMethod refundMethod,
                                       @NotEmpty List<@Valid SalesReturnLineRequest> items) {}
