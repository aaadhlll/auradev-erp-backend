package com.auradev.erp.billing.repository;

import com.auradev.erp.billing.entity.Customer;
import com.auradev.erp.billing.entity.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    List<Customer> findByTenantIdOrderByNameAsc(UUID tenantId);

    Optional<Customer> findByTenantIdAndType(UUID tenantId, CustomerType type);

    Optional<Customer> findByIdAndTenantId(UUID id, UUID tenantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Customer> findForCreditPaymentByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByTenantIdAndPhoneAndTypeNot(UUID tenantId, String phone, CustomerType type);
}
