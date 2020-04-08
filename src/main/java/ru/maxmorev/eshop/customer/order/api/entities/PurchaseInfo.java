package ru.maxmorev.eshop.customer.order.api.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInfo {

    @Column(name = "amount", nullable = false)
    private @NotNull Integer amount;
    @Column(name = "price", nullable = false)
    private @NotNull Float price;
    @NotBlank
    @Column(name = "commodity_name", nullable = false)
    private String commodityName;
    @NotBlank
    @Column(name = "commodity_image_uri", nullable = false)
    private String commodityImageUri;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseInfo)) return false;
        PurchaseInfo that = (PurchaseInfo) o;
        return getAmount().equals(that.getAmount()) &&
                getPrice().equals(that.getPrice()) &&
                getCommodityName().equals(that.getCommodityName()) &&
                getCommodityImageUri().equals(that.getCommodityImageUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount(), getPrice(), getCommodityName(), getCommodityImageUri());
    }
}
