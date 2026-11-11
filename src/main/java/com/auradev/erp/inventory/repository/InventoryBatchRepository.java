package com.auradev.erp.inventory.repository;

import com.auradev.erp.inventory.entity.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, UUID> {
    List<InventoryBatch> findByTenantIdAndProductIdOrderByExpiryDateAsc(UUID tenantId, UUID productId);
}
