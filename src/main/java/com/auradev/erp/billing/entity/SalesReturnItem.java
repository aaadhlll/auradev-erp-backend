package com.auradev.erp.billing.entity;

import com.auradev.erp.catalog.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Entity @Table(name = "sales_return_items")
public class SalesReturnItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "return_id") private SalesReturn salesReturn;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "bill_item_id") private BillItem billItem;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "product_id") private Product product;
    @Column(name = "quantity", nullable = false, precision = 12, scale = 3) private BigDecimal quantity;
    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2) private BigDecimal refundAmount;
}
