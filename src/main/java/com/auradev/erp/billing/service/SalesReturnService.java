package com.auradev.erp.billing.service;

import com.auradev.erp.audit.service.AuditService;
import com.auradev.erp.auth.security.UserPrincipal;
import com.auradev.erp.billing.dto.*;
import com.auradev.erp.billing.entity.*;
import com.auradev.erp.billing.repository.*;
import com.auradev.erp.common.error.BusinessException;
import com.auradev.erp.common.error.EntityNotFoundException;
import com.auradev.erp.inventory.entity.MovementType;
import com.auradev.erp.inventory.entity.RefType;
import com.auradev.erp.inventory.service.InventoryService;
import com.auradev.erp.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Pageable;
import com.auradev.erp.common.pagination.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service @Transactional @RequiredArgsConstructor
public class SalesReturnService {
    private final BillRepository billRepository;
    private final SalesReturnRepository returnRepository;
    private final SalesReturnItemRepository returnItemRepository;
    private final InventoryService inventoryService;
    private final AuditService auditService;

    public SalesReturnResponse create(UUID billId, CreateSalesReturnRequest request) {
        UUID tenantId = TenantContext.require();
        UserPrincipal actor = principal();
        Bill bill = billRepository.findForReturn(billId, tenantId, BillStatus.COMPLETED)
                .orElseThrow(() -> new EntityNotFoundException("CompletedBill", billId));
        if (request.refundMethod() == PaymentMethod.SPLIT_COMPONENT) {
            throw new BusinessException("INVALID_REFUND_METHOD", "Choose Cash, UPI, Card, or Credit");
        }

        Map<UUID, BillItem> sold = new HashMap<>();
        bill.getItems().forEach(item -> sold.put(item.getId(), item));
        BigDecimal linesTotal = bill.getItems().stream().map(BillItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal billFactor = linesTotal.signum() == 0 ? BigDecimal.ONE
                : bill.getGrandTotal().divide(linesTotal, 8, RoundingMode.HALF_UP);
        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setTenantId(tenantId);
        salesReturn.setBill(bill);
        salesReturn.setReturnNo("RET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        salesReturn.setReason(request.reason().trim());
        salesReturn.setRefundMethod(request.refundMethod());
        salesReturn.setCreatedBy(actor.getId());
        salesReturn.setCreatedAt(Instant.now());

        BigDecimal total = BigDecimal.ZERO;
        Set<UUID> requestedItems = new HashSet<>();
        for (SalesReturnLineRequest line : request.items()) {
            if (!requestedItems.add(line.billItemId())) throw new BusinessException("DUPLICATE_RETURN_LINE", "Duplicate return item");
            BillItem item = sold.get(line.billItemId());
            if (item == null) throw new BusinessException("INVALID_RETURN_ITEM", "Item is not part of this bill");
            BigDecimal alreadyReturned = returnItemRepository.returnedQuantity(item.getId());
            BigDecimal remaining = item.getQuantity().subtract(alreadyReturned);
            if (line.quantity().compareTo(remaining) > 0) {
                throw new BusinessException("RETURN_QUANTITY_EXCEEDED", "Return quantity exceeds remaining quantity for " + item.getProductNameSnapshot());
            }
            BigDecimal refund = item.getLineTotal().multiply(line.quantity())
                    .divide(item.getQuantity(), 8, RoundingMode.HALF_UP)
                    .multiply(billFactor).setScale(2, RoundingMode.HALF_UP);
            SalesReturnItem returned = new SalesReturnItem();
            returned.setSalesReturn(salesReturn);
            returned.setBillItem(item);
            returned.setProduct(item.getProduct());
            returned.setQuantity(line.quantity());
            returned.setRefundAmount(refund);
            salesReturn.getItems().add(returned);
            total = total.add(refund);
        }
        salesReturn.setRefundTotal(total.setScale(2, RoundingMode.HALF_UP));
        SalesReturn saved = returnRepository.save(salesReturn);
        for (SalesReturnItem item : saved.getItems()) {
            inventoryService.recordMovement(item.getProduct().getId(), MovementType.customer_return, item.getQuantity(),
                    RefType.bill, saved.getId(), saved.getReturnNo() + " · " + saved.getReason());
        }
        auditService.log("SALE_RETURN_CREATED", "sales_return", saved.getId(), Map.of(
                "billNo", bill.getBillNo(), "returnNo", saved.getReturnNo(), "refundTotal", saved.getRefundTotal()));
        return response(saved);
    }

    @Transactional(readOnly = true)
    public List<SalesReturnResponse> list(UUID billId) {
        return returnRepository.findByTenantIdAndBillIdOrderByCreatedAtDesc(TenantContext.require(), billId).stream()
                .map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<SalesReturnReportRow> report(String q, Pageable pageable) {
        return PageResponse.of(returnRepository.findReport(TenantContext.require(), q, pageable));
    }

    @Transactional(readOnly = true)
    public List<SalesReturnReportRow> reportForExport(String q) {
        return returnRepository.findReportForExport(TenantContext.require(), q);
    }

    private SalesReturnResponse response(SalesReturn value) {
        return new SalesReturnResponse(value.getId(), value.getReturnNo(), value.getBill().getId(), value.getReason(),
                value.getRefundMethod(), value.getRefundTotal(), value.getCreatedAt(), value.getItems().stream()
                .map(item -> new SalesReturnLineResponse(item.getBillItem().getId(), item.getProduct().getId(),
                        item.getBillItem().getProductNameSnapshot(), item.getQuantity(), item.getRefundAmount())).toList());
    }

    private static UserPrincipal principal() {
        Object value = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (value instanceof UserPrincipal principal) return principal;
        throw new IllegalStateException("Authenticated user required");
    }
}
