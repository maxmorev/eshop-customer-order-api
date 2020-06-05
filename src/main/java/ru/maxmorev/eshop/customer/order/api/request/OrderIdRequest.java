package ru.maxmorev.eshop.customer.order.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.maxmorev.eshop.customer.order.api.validation.CheckCustomerOrderId;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@CheckCustomerOrderId(message = "{validation.order.payment.confirmation.invalid.orderId}")
public class OrderIdRequest {
    @NotNull
    private Long orderId;
    @NotNull
    private Long customerId;

}
