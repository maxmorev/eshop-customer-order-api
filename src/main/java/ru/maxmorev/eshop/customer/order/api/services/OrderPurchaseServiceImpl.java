package ru.maxmorev.eshop.customer.order.api.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maxmorev.eshop.customer.order.api.annotation.CustomerOrderStatus;
import ru.maxmorev.eshop.customer.order.api.annotation.PaymentProvider;
import ru.maxmorev.eshop.customer.order.api.config.OrderConfiguration;
import ru.maxmorev.eshop.customer.order.api.entities.CustomerOrder;
import ru.maxmorev.eshop.customer.order.api.entities.Purchase;
import ru.maxmorev.eshop.customer.order.api.repository.CustomerOrderRepository;
import ru.maxmorev.eshop.customer.order.api.repository.ShoppingCartRepository;
import ru.maxmorev.eshop.customer.order.api.request.PaymentInitialRequest;
import ru.maxmorev.eshop.customer.order.api.request.PurchaseInfoRequest;
import ru.maxmorev.eshop.customer.order.api.response.OrderGrid;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("orderPurchaseService")
@Transactional
@AllArgsConstructor
public class OrderPurchaseServiceImpl implements OrderPurchaseService {

    private final CustomerOrderRepository customerOrderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderConfiguration orderConfiguration;


    @Override
    public CustomerOrder createOrderFor(Long customerId, List<PurchaseInfoRequest> purchaseInfoList) {
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setCustomerId(customerId);
        customerOrder.setStatus(CustomerOrderStatus.AWAITING_PAYMENT);
        final CustomerOrder newOrder = customerOrderRepository.save(customerOrder);
        purchaseInfoList.forEach(commodityInfo -> {
            Purchase purchase = new Purchase(commodityInfo.getBranchId(), newOrder, commodityInfo.toEntity());
            newOrder.getPurchases().add(purchase);
        });
        return customerOrderRepository.save(newOrder);
    }


    @Override
    public CustomerOrder confirmPaymentOrder(Long orderId, PaymentProvider paymentProvider, String paymentID) {
        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!CustomerOrderStatus.AWAITING_PAYMENT.equals(order.getStatus()))
            throw new IllegalArgumentException("Invalid order status");
        order.setStatus(CustomerOrderStatus.PAYMENT_APPROVED);
        order.setPaymentProvider(paymentProvider);
        order.setPaymentID(paymentID);
        //clean shopping cart
        return customerOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerOrder> findCustomerOrders(Long customerId) {
        return customerOrderRepository.findByCustomerIdOrderByDateOfCreationDesc(customerId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerOrder> findCustomerOrders(Long customerId, CustomerOrderStatus status) {
        return customerOrderRepository.findByCustomerIdAndStatusOrderByDateOfCreationDesc(customerId, status);
    }

    @Override
    public Optional<CustomerOrder> findOrder(Long id) {
        return customerOrderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerOrder> findExpiredOrders() {
        //DO NOTHING
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -orderConfiguration.getExpiredMinutes());
        Date teenMinutesFromNow = now.getTime();
        return customerOrderRepository.findExpiredOrdersByStatus(CustomerOrderStatus.AWAITING_PAYMENT, teenMinutesFromNow);
    }

    @Override
    public void removeExpiredOrder(Long id) {
        CustomerOrder order = customerOrderRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!CustomerOrderStatus.AWAITING_PAYMENT.equals(order.getStatus())) {
            throw new RuntimeException("Illegal Order Status. Can not remove");
        }

        customerOrderRepository.delete(order);
    }

    @Override
    public CustomerOrder setOrderStatus(Long id, CustomerOrderStatus status) {
        CustomerOrder order = findOrder(id).orElseThrow(() -> new IllegalArgumentException("Invalid order id"));
        order.setStatus(status);
        return customerOrderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerOrder> findAllOrdersByPageAndStatusNot(Pageable pageable, CustomerOrderStatus status) {
        return customerOrderRepository.findByStatusNot(pageable, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerOrder> findOrder(Long id, Long customerId) {
        return customerOrderRepository.findByIdAndCustomerId(id, customerId);
    }

    @Override
    public void cancelOrderByCustomer(Long customerId, Long orderId) {
        //move elements back to branch
        CustomerOrder order = customerOrderRepository
                .findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        //set order status to canceled
        if (CustomerOrderStatus.AWAITING_PAYMENT.equals(order.getStatus())) {
            customerOrderRepository.delete(order);
            return;
        }
        if (CustomerOrderStatus.PAYMENT_APPROVED.equals(order.getStatus())) {
            order.setStatus(CustomerOrderStatus.CANCELED_BY_CUSTOMER);
            customerOrderRepository.save(order);
            return;
        }
        throw new RuntimeException("Implement logic with other OrderStatus");
    }

    @Override
    public List<CustomerOrder> findOrderListForCustomer(Long customerId) {
        return customerOrderRepository
                .findByCustomerIdAndStatusNotOrderByDateOfCreationDesc(
                        customerId,
                        CustomerOrderStatus.AWAITING_PAYMENT);
    }

    private PageRequest getPageRequeset(Integer page,
                                        Integer rows,
                                        String sortBy,
                                        String order) {
        Sort sort = null;
        String orderBy = sortBy;
        if (orderBy != null && orderBy.equals("dateOfCreation")) {
            orderBy = "dateOfCreation";
        } else {
            orderBy = "id";
        }
        if (Objects.isNull(order)) {
            order = "desc";
        }
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(rows)) {
            rows = 10;
        }

        if (orderBy != null && order != null) {
            if (order.equals("desc")) {
                sort = Sort.by(Sort.Direction.DESC, orderBy);
            } else
                sort = Sort.by(Sort.Direction.ASC, orderBy);
        }
        // Constructs page request for current page
        // Note: page number for Spring Data JPA starts with 0, while jqGrid starts with 1
        PageRequest pageRequest = null;
        if (sort != null) {
            pageRequest = PageRequest.of(page - 1, rows, sort);
        } else {
            pageRequest = PageRequest.of(page - 1, rows);
        }
        return pageRequest;
    }

    @Override
    public OrderGrid getOrdersForAdmin(Integer page, Integer rows, String sortBy, String order) {
        return new OrderGrid(findAllOrdersByPageAndStatusNot(
                getPageRequeset(page,
                        rows,
                        sortBy,
                        order),
                CustomerOrderStatus.AWAITING_PAYMENT));
    }

    @Override
    public Optional<CustomerOrder> paymentInitial(PaymentInitialRequest paymentRequest) {
        return customerOrderRepository.findById(paymentRequest.getOrderId())
                .map(customerOrder -> {
                    customerOrder.setPaymentID(paymentRequest.getPaymentID());
                    customerOrder.setStatus(CustomerOrderStatus.AWAITING_PAYMENT);
                    return customerOrderRepository.save(customerOrder);
                });
    }
}
