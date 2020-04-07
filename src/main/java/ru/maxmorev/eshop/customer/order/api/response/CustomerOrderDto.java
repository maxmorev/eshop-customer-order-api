package ru.maxmorev.eshop.customer.order.api.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import ru.maxmorev.eshop.customer.order.api.annotation.CustomerOrderStatus;
import ru.maxmorev.eshop.customer.order.api.annotation.PaymentProvider;
import ru.maxmorev.eshop.customer.order.api.entities.CustomerOrder;
import ru.maxmorev.eshop.customer.order.api.entities.Purchase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
public class CustomerOrderDto {
    private Long id;
    private Long customerId;
    private Date dateOfCreation;
    private CustomerOrderStatus status;
    private PaymentProvider paymentProvider;
    private String paymentID;
    private List<Purchase> purchases;
    private List<Action> actions;
    private Double totalPrice;

    private static CustomerOrderDto.CustomerOrderDtoBuilder getCommonBuilder(CustomerOrder co) {
        return CustomerOrderDto.builder()
                .id(co.getId())
                .customerId(co.getCustomerId())
                .dateOfCreation(co.getDateOfCreation())
                .status(co.getStatus())
                .paymentProvider(co.getPaymentProvider())
                .paymentID(co.getPaymentID())
                .purchases(co.getPurchases());
    }

    public static CustomerOrderDto forCusrtomer(CustomerOrder co){
        return getCommonBuilder(co)
                .actions(getCustomerAvailableActions(co.getStatus()))
                .build();
    }

    public static CustomerOrderDto of(CustomerOrder co) {
        return getCommonBuilder(co)
                .actions(getAvailableActions(co.getStatus()))
                .build();
    }

    public static List<Action> getAvailableActions(CustomerOrderStatus status) {
        List<Action> actions = new ArrayList<>();
        switch (status) {
            case PAYMENT_APPROVED:
                actions.add(Action.builder()
                        .action(CustomerOrderStatus.PREPARING_TO_SHIP.name())
                        .id(2)
                        .build());
                break;
            case PREPARING_TO_SHIP:
                actions.add(Action.builder()
                        .action(CustomerOrderStatus.DISPATCHED.name())
                        .id(3)
                        .build());
                break;
        }
        return actions;
    }

    public static List<Action> getCustomerAvailableActions(CustomerOrderStatus status) {
        List<Action> actions = new ArrayList<>();
        switch (status) {
            case PAYMENT_APPROVED:
                actions.add(Action.builder()
                        .action(CustomerOrderStatus.CANCELED_BY_CUSTOMER.name())
                        .id(2)
                        .build());
                break;

        }
        return actions;
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

    public Double getTotalPrice() {
        if (Objects.isNull(totalPrice)) {
            totalPrice = purchases.stream().mapToDouble(p -> p.getPurchaseInfo().getAmount() * p.getPurchaseInfo().getPrice()).sum();
        }
        return totalPrice;
    }


}
