package ru.maxmorev.eshop.customer.order.api.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

//Readonly
@Embeddable
@Getter
@Setter
public class PurchaseId implements Serializable {

    @Column(name = "branch_id")
    Long branchId;
    @Column(name = "order_id")
    Long orderId;

    protected PurchaseId() {
    }

    public PurchaseId(Long branchId, Long orderId) {
        if (branchId == null) throw new IllegalArgumentException("branchId cannot be null");
        if (orderId == null) throw new IllegalArgumentException("orderId cannot be null");
        this.branchId = branchId;
        this.orderId = orderId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public Long getOrderId() {
        return orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseId)) return false;
        PurchaseId that = (PurchaseId) o;
        return getBranchId().equals(that.getBranchId()) &&
                getOrderId().equals(that.getOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBranchId(), getOrderId());
    }
}

