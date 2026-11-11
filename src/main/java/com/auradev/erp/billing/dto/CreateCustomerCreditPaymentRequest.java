package com.auradev.erp.billing.dto;

import com.auradev.erp.billing.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCustomerCreditPaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull PaymentMethod paymentMethod,
        String notes
) {}
