package com.auradev.erp.billing.repository;

import com.auradev.erp.billing.entity.SalesReturn;
import com.auradev.erp.billing.dto.SalesReturnReportRow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface SalesReturnRepository extends JpaRepository<SalesReturn, UUID> {
    @EntityGraph(attributePaths = {"items", "items.billItem", "items.product"})
    List<SalesReturn> findByTenantIdAndBillIdOrderByCreatedAtDesc(UUID tenantId, UUID billId);

    @Query("""
            select new com.auradev.erp.billing.dto.SalesReturnReportRow(
                r.id, r.returnNo, b.id, b.billNo,
                (select c.name from Customer c where c.id = b.customerId),
                bi.productNameSnapshot, bi.skuSnapshot, item.quantity, item.refundAmount,
                r.refundMethod, r.reason,
                (select u.name from User u where u.id = r.createdBy), r.createdAt)
            from SalesReturn r
            join r.bill b
            join r.items item
            join item.billItem bi
            where r.tenantId = :tenantId
              and (:q is null or :q = ''
                   or lower(r.returnNo) like lower(concat('%', :q, '%'))
                   or lower(b.billNo) like lower(concat('%', :q, '%'))
                   or lower(bi.productNameSnapshot) like lower(concat('%', :q, '%')))
            order by r.createdAt desc, r.returnNo desc
            """)
    Page<SalesReturnReportRow> findReport(@Param("tenantId") UUID tenantId, @Param("q") String q, Pageable pageable);

    @Query("""
            select new com.auradev.erp.billing.dto.SalesReturnReportRow(
                r.id, r.returnNo, b.id, b.billNo,
                (select c.name from Customer c where c.id = b.customerId),
                bi.productNameSnapshot, bi.skuSnapshot, item.quantity, item.refundAmount,
                r.refundMethod, r.reason,
                (select u.name from User u where u.id = r.createdBy), r.createdAt)
            from SalesReturn r
            join r.bill b
            join r.items item
            join item.billItem bi
            where r.tenantId = :tenantId
              and (:q is null or :q = ''
                   or lower(r.returnNo) like lower(concat('%', :q, '%'))
                   or lower(b.billNo) like lower(concat('%', :q, '%'))
                   or lower(bi.productNameSnapshot) like lower(concat('%', :q, '%')))
            order by r.createdAt desc, r.returnNo desc
            """)
    List<SalesReturnReportRow> findReportForExport(@Param("tenantId") UUID tenantId, @Param("q") String q);
}
