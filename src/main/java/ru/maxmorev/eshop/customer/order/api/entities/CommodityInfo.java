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

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CommodityInfo {

    @Transient
    private @NotNull Long branchId;
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

}
