package com.auradev.erp.billing.dto;

import java.math.BigDecimal;
import java.util.List;

public record CustomerCreditLedgerResponse(BigDecimal currentBalance, List<CustomerCreditLedgerEntry> entries) {}
