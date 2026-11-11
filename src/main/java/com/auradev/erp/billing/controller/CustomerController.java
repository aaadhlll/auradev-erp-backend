package com.auradev.erp.billing.controller;

import com.auradev.erp.billing.dto.CreateCustomerRequest;
import com.auradev.erp.billing.dto.CustomerResponse;
import com.auradev.erp.billing.dto.CreateCustomerCreditPaymentRequest;
import com.auradev.erp.billing.dto.CustomerCreditPaymentResponse;
import com.auradev.erp.billing.dto.CustomerCreditLedgerResponse;
import com.auradev.erp.billing.service.BillingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer directory for billing")
@RequiredArgsConstructor
public class CustomerController {

    private final BillingService billingService;

    @GetMapping
    @PreAuthorize("@authz.can(authentication, 'CUSTOMER_VIEW')")
    public ResponseEntity<List<CustomerResponse>> list() {
        return ResponseEntity.ok(billingService.listCustomers());
    }

    @PostMapping
    @PreAuthorize("@authz.can(authentication, 'BILL_CREATE')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createCustomer(req));
    }

    @GetMapping("/{id}/credit-payments")
    @PreAuthorize("@authz.can(authentication, 'CUSTOMER_CREDIT_SETTLE')")
    public ResponseEntity<List<CustomerCreditPaymentResponse>> creditPaymentHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.listCustomerCreditPayments(id));
    }

    @GetMapping("/{id}/credit-ledger")
    @PreAuthorize("@authz.can(authentication, 'CUSTOMER_CREDIT_SETTLE')")
    public ResponseEntity<CustomerCreditLedgerResponse> creditLedger(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.customerCreditLedger(id));
    }

    @PostMapping("/{id}/credit-payments")
    @PreAuthorize("@authz.can(authentication, 'CUSTOMER_CREDIT_SETTLE')")
    public ResponseEntity<CustomerCreditPaymentResponse> settleCredit(
            @PathVariable UUID id, @Valid @RequestBody CreateCustomerCreditPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.settleCustomerCredit(id, request));
    }
}
