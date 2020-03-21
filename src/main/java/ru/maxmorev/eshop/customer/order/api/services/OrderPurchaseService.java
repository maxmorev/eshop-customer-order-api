package ru.maxmorev.eshop.customer.order.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.maxmorev.eshop.customer.order.api.annotation.CustomerOrderStatus;
import ru.maxmorev.eshop.customer.order.api.annotation.PaymentProvider;
import ru.maxmorev.eshop.customer.order.api.entities.CommodityInfo;
import ru.maxmorev.eshop.customer.order.api.entities.CustomerOrder;
import ru.maxmorev.eshop.customer.order.api.response.CustomerOrderDto;
import ru.maxmorev.eshop.customer.order.api.response.OrderGrid;

import java.util.List;
import java.util.Optional;

public interface OrderPurchaseService {

    Optional<CustomerOrder> findOrder(Long id);

    Optional<CustomerOrder> findOrder(Long id, Long customerId);

    CustomerOrder createOrderFor(Long customerId, List<CommodityInfo> commodityInfoList);

    CustomerOrder confirmPaymentOrder(CustomerOrder order, PaymentProvider paymentProvider, String paymentID);

    void cancelOrderByCustomer(Long orderId);

    List<CustomerOrder> findCustomerOrders(Long customerId);

    List<CustomerOrder> findExpiredOrders();

    CustomerOrder setOrderStatus(Long id, CustomerOrderStatus status);

    List<CustomerOrder> findCustomerOrders(Long customerId, CustomerOrderStatus status);

    Page<CustomerOrder> findAllOrdersByPage(Pageable pageable);
    Page<CustomerOrder> findAllOrdersByPageAndStatus(Pageable pageable, CustomerOrderStatus status);
    Page<CustomerOrder> findAllOrdersByPageAndStatusNot(Pageable pageable, CustomerOrderStatus status);

    List<CustomerOrderDto> findOrderListForCustomer(Long customerId);

    OrderGrid getOrdersForAdmin(Integer page, Integer rows, String sortBy, String order);

}
