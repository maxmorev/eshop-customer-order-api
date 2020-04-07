package ru.maxmorev.eshop.customer.order.api.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.maxmorev.eshop.customer.order.api.validation.CheckShoppingCartId;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveFromCartRequest {
    @NotNull(message = "{validation.shopping.cart.id.NotNull}")
    @CheckShoppingCartId(message = "{validation.shopping.cart.id.valid}")
    private Long shoppingCartId;
    @NotNull(message = "{validation.shopping.cart.branch.id.NotNull}")
    private Long branchId;
    @NotNull
    private Integer amount;

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
