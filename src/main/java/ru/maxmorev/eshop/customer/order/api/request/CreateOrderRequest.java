package ru.maxmorev.eshop.customer.order.api.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private Long customerId;
    private List<PurchaseInfoRequest> purchases;
}