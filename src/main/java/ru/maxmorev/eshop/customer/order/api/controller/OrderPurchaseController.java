package ru.maxmorev.eshop.customer.order.api.controller;


import com.google.common.base.Enums;
import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.maxmorev.eshop.customer.order.api.annotation.CustomerOrderStatus;
import ru.maxmorev.eshop.customer.order.api.annotation.PaymentProvider;
import ru.maxmorev.eshop.customer.order.api.entities.CustomerOrder;
import ru.maxmorev.eshop.customer.order.api.request.OrderIdRequest;
import ru.maxmorev.eshop.customer.order.api.request.OrderPaymentConfirmation;
import ru.maxmorev.eshop.customer.order.api.response.CustomerOrderDto;
import ru.maxmorev.eshop.customer.order.api.response.Message;
import ru.maxmorev.eshop.customer.order.api.response.OrderGrid;
import ru.maxmorev.eshop.customer.order.api.services.OrderPurchaseService;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class OrderPurchaseController {

    private final MessageSource messageSource;
    private final OrderPurchaseService orderPurchaseService;

    @RequestMapping(path = "/order/confirm/", method = RequestMethod.PUT)
    @ResponseBody
    Message confirmOrder(@RequestBody
                         @Valid OrderPaymentConfirmation orderPaymentConfirmation,
                         Locale locale) {

        orderPurchaseService.findOrder(orderPaymentConfirmation.getOrderId()).ifPresent(order -> {
            orderPurchaseService.confirmPaymentOrder(
                    order,
                    PaymentProvider.valueOf(orderPaymentConfirmation.getPaymentProvider()),
                    orderPaymentConfirmation.getPaymentId()
            );
        });

        return new Message(Message.SUCCES, messageSource.getMessage("message_success", new Object[]{}, locale));
    }

    @RequestMapping(path = "/order/{id}/customer/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    CustomerOrder customerOrder(
            @PathVariable(name = "customerId") Long customerId,
            @PathVariable(name = "id") Long orderId,
            Locale locale) {
        //TODO check auth customer.id with id in PathVariable
        return orderPurchaseService
                .findOrder(orderId, customerId)
                .orElseThrow(() -> new NoSuchElementException("No such order"));
    }

    @RequestMapping(path = "/order/list/customer/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    List<CustomerOrderDto> customerOrderList(@PathVariable(name = "customerId", required = true) Long customerId, Locale locale) {
        //TODO check auth customer.id with id in PathVariable
        return orderPurchaseService.findOrderListForCustomer(customerId);
    }

    @RequestMapping(path = "/order/cancel/", method = RequestMethod.PUT)
    @ResponseBody
    Message customerOrderCancel(
            @Valid @RequestBody OrderIdRequest order,
            Locale locale) {
        //TODO check auth customer.id with id in PathVariable
        orderPurchaseService.cancelOrderByCustomer(order.getOrderId());
        return new Message(Message.SUCCES, messageSource.getMessage("message_success", new Object[]{}, locale));
    }


    @RequestMapping(path = "/order/{status}/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Message setOrderStatus(
            @PathVariable(name = "status", required = true) String status,
            @PathVariable(name = "id", required = true) Long id,
            Locale locale) {
        Optional<CustomerOrderStatus> orderStatus = Enums.getIfPresent(CustomerOrderStatus.class, status);
        if (!orderStatus.isPresent())
            throw new IllegalArgumentException("Invalid status");
        orderPurchaseService.setOrderStatus(id, orderStatus.get());
        return new Message(Message.SUCCES, messageSource.getMessage("message_success", new Object[]{}, locale));
    }

    @RequestMapping(path = "/order/list/all", method = RequestMethod.GET)
    @ResponseBody
    OrderGrid adminAllCustomerOrderList(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows,
            @RequestParam(value = "sort", required = false) String sortBy,
            @RequestParam(value = "order", required = false) String order,
            Locale locale) {

        return orderPurchaseService
                .getOrdersForAdmin(page,
                        rows,
                        sortBy,
                        order);
    }

}
