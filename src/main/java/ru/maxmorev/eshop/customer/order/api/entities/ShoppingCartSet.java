package ru.maxmorev.eshop.customer.order.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "shopping_cart_set")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingCartSet {

    @EmbeddedId
    private ShoppingCartId id;

    @Embedded
    private PurchaseInfo purchaseInfo;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "shopping_cart_id", insertable = false, updatable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_SHOPPING_CART_SET_CART"))
    private ShoppingCart shoppingCart;

    public ShoppingCartSet(Long branchId, ShoppingCart shoppingCart, PurchaseInfo purchaseInfo) {
        this.id = new ShoppingCartId(branchId, shoppingCart.getId());
        this.shoppingCart = shoppingCart;
        this.purchaseInfo = purchaseInfo;
    }

    public int getAmount() {
        return this.getPurchaseInfo().getAmount();
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingCartSet)) return false;
        ShoppingCartSet that = (ShoppingCartSet) o;
        return getId().equals(that.getId()) &&
                getPurchaseInfo().equals(that.getPurchaseInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPurchaseInfo());
    }
}
