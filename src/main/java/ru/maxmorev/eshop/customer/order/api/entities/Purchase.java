package ru.maxmorev.eshop.customer.order.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "purchase")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Purchase implements Serializable {

    @EmbeddedId
    private PurchaseId id;

    @Embedded
    private CommodityInfo commodityInfo;

    @NotNull
    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PURCHASE_CUSTOMER_ORDER"))
    private CustomerOrder customerOrder;

    protected Purchase() {
    }

    public Purchase(Long branchId, CustomerOrder customerOrder, CommodityInfo commodityInfo) {
        this.id = new PurchaseId(branchId, customerOrder.getId());
        this.commodityInfo = commodityInfo;
        this.customerOrder = customerOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;
        Purchase purchase = (Purchase) o;
        return getId().equals(purchase.getId()) &&
                getCommodityInfo().equals(purchase.getCommodityInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCommodityInfo());
    }
}
