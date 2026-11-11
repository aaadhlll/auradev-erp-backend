package com.auradev.erp.billing.repository;

import com.auradev.erp.billing.entity.SalesReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface SalesReturnItemRepository extends JpaRepository<SalesReturnItem, UUID> {
    @Query("select coalesce(sum(i.quantity), 0) from SalesReturnItem i where i.billItem.id = :billItemId")
    BigDecimal returnedQuantity(@Param("billItemId") UUID billItemId);

    @Query("""
            select i.billItem.id, coalesce(sum(i.quantity), 0)
            from SalesReturnItem i
            where i.salesReturn.bill.id = :billId
            group by i.billItem.id
            """)
    List<Object[]> returnedQuantitiesForBill(@Param("billId") UUID billId);
}
