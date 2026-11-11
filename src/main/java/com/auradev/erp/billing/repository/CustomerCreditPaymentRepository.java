package com.auradev.erp.billing.repository;

import com.auradev.erp.billing.entity.CustomerCreditPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerCreditPaymentRepository extends JpaRepository<CustomerCreditPayment, UUID> {
    List<CustomerCreditPayment> findTop50ByTenantIdAndCustomerIdOrderByCreatedAtDesc(UUID tenantId, UUID customerId);
}
