package ru.maxmorev.eshop.customer.order.api.controller;


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
import ru.maxmorev.eshop.customer.order.api.request.CreateOrderRequest;
import ru.maxmorev.eshop.customer.order.api.request.OrderIdRequest;
import ru.maxmorev.eshop.customer.order.api.request.OrderPaymentConfirmation;
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

    @RequestMapping(path = "/order/confirmation/", method = RequestMethod.PUT)
    @ResponseBody
    public CustomerOrder confirmOrder(@RequestBody
                                      @Valid OrderPaymentConfirmation orderPaymentConfirmation,
                                      Locale locale) {
        return orderPurchaseService.confirmPaymentOrder(orderPaymentConfirmation.getOrderId(),
                PaymentProvider.valueOf(orderPaymentConfirmation.getPaymentProvider()),
                orderPaymentConfirmation.getPaymentId()
        );
    }

    @RequestMapping(path = "/order/", method = RequestMethod.POST)
    @ResponseBody
    public CustomerOrder createOrder(@RequestBody
                                     @Valid CreateOrderRequest createOrderRequest,
                                     Locale locale) {
        return orderPurchaseService
                .createOrderFor(createOrderRequest.getCustomerId(),
                        createOrderRequest.getPurchases());
    }

    @RequestMapping(path = "/order/expired/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Message removeExpiredOrder(@PathVariable(name = "id") Long id, Locale locale) {
        orderPurchaseService.removeExpiredOrder(id);
        return new Message(Message.SUCCES, messageSource.getMessage("message_success", new Object[]{}, locale));
    }

    @RequestMapping(path = "/order/{id}/customer/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerOrder customerOrder(
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
    public List<CustomerOrder> customerOrderList(@PathVariable(name = "customerId", required = true) Long customerId, Locale locale) {
        //TODO check auth customer.id with id in PathVariable
        return orderPurchaseService.findOrderListForCustomer(customerId);
    }

    @RequestMapping(path = "/order/list/customer/{customerId}/status/{status}", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerOrder> customerOrderListByStatus(@PathVariable(name = "customerId") Long customerId,
                                                         @PathVariable(name = "status") String statusName,
                                                         Locale locale) {
        //TODO check auth customer.id with id in PathVariable
        return orderPurchaseService
                .findCustomerOrders(customerId, CustomerOrderStatus.valueOf(statusName));
    }

    @RequestMapping(path = "/order/by/customer/cancellation/", method = RequestMethod.PUT)
    @ResponseBody
    public Message customerOrderCancel(
            @Valid @RequestBody OrderIdRequest order,
            Locale locale) {
        //TODO check auth customer.id with id in PathVariable
        orderPurchaseService.cancelOrderByCustomer(order.getCustomerId(), order.getOrderId());
        return new Message(Message.SUCCES, messageSource.getMessage("message_success", new Object[]{}, locale));
    }


    @RequestMapping(path = "/order/{id}/{status}", method = RequestMethod.PUT)
    @ResponseBody
    public CustomerOrder setOrderStatus(
            @PathVariable(name = "id", required = true) Long id,
            @PathVariable(name = "status", required = true) String status,
            Locale locale) {
        CustomerOrderStatus statusValue;
        try {
            statusValue = CustomerOrderStatus.valueOf(status);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid status");
        }
        return orderPurchaseService.setOrderStatus(id, statusValue);
    }

    @RequestMapping(path = "/order/list/all", method = RequestMethod.GET)
    @ResponseBody
    public OrderGrid adminAllCustomerOrderList(
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

    @RequestMapping(path = "/order/list/expired", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerOrder> findExpiredOrders() {
        return orderPurchaseService.findExpiredOrders();
    }

}
