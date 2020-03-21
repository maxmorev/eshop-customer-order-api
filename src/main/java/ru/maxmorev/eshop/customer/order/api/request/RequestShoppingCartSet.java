package ru.maxmorev.eshop.customer.order.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.maxmorev.eshop.customer.order.api.entities.CommodityInfo;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartSet;
import ru.maxmorev.eshop.customer.order.api.validation.CheckShoppingCartId;

import javax.validation.constraints.NotNull;

/**
 * The POJO class to build from the JSON request with id's for persistence level class ShoppingCartSet
 * @see ShoppingCartSet
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestShoppingCartSet {

    @NotNull(message = "{validation.shopping.cart.id.NotNull}")
    @CheckShoppingCartId(message = "{validation.shopping.cart.id.valid}")
    private Long shoppingCartId;
    @NotNull(message = "{validation.shopping.cart.branch.id.NotNull}")
    private Long branchId;
    @NotNull
    private CommodityInfo commodityInfo;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
