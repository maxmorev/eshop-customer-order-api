package ru.maxmorev.eshop.customer.order.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.maxmorev.eshop.customer.order.api.entities.PurchaseInfo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseInfoRequest {
    @NotNull
    private Long branchId;
    @Min(value = 0, message = "{validation.commodity.amount.min.message}")
    @NotNull(message = "{validation.commodity.amount.NotNull.message}")
    private Integer amount;
    @Min(value = 0, message = "{validation.commodity.price.gt.zero}")
    @NotNull(message = "{validation.commodity.price.NotNull.message}")
    private Float price;
    @NotBlank(message = "{validation.commodity.name.NotBlank.message}")
    private String commodityName;
    @NotBlank
    private String commodityImageUri;

    public PurchaseInfo toEntity() {
        return new PurchaseInfo(amount, price, commodityName, commodityImageUri);
    }

}
