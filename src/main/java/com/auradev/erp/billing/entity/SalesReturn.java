package com.auradev.erp.billing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Entity @Table(name = "sales_returns")
public class SalesReturn {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "tenant_id", nullable = false) private UUID tenantId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "bill_id") private Bill bill;
    @Column(name = "return_no", nullable = false) private String returnNo;
    @Column(name = "reason", nullable = false, columnDefinition = "text") private String reason;
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Enumerated(EnumType.STRING)
    @Column(name = "refund_method", nullable = false, columnDefinition = "payment_method") private PaymentMethod refundMethod;
    @Column(name = "refund_total", nullable = false, precision = 12, scale = 2) private BigDecimal refundTotal;
    @Column(name = "created_by", nullable = false) private UUID createdBy;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @OneToMany(mappedBy = "salesReturn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesReturnItem> items = new ArrayList<>();
}
