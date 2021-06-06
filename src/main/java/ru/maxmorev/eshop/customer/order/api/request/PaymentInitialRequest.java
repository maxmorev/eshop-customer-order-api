package ru.maxmorev.eshop.customer.order.api.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentInitialRequest {
    private Long orderId;
    private String paymentID;
}
