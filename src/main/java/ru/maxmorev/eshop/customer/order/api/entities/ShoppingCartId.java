package ru.maxmorev.eshop.customer.order.api.entities;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class ShoppingCartId implements Serializable {

    @Column(name = "branch_id")
    Long branchId;
    @Column(name = "shopping_cart_id", updatable = false, insertable = false)
    Long shoppingCartId;

    protected ShoppingCartId() {
    }

    public ShoppingCartId(Long branchId, Long shoppingCartId) {
        if (branchId == null) throw new IllegalArgumentException("branchId cannot be null");
        if (shoppingCartId == null) throw new IllegalArgumentException("shoppingCartId cannot be null");
        this.branchId = branchId;
        this.shoppingCartId = shoppingCartId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingCartId)) return false;
        ShoppingCartId that = (ShoppingCartId) o;
        return branchId.equals(that.branchId) &&
                shoppingCartId.equals(that.shoppingCartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId, shoppingCartId);
    }
}
