package ru.maxmorev.eshop.customer.order.api.services;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.maxmorev.eshop.customer.order.api.annotation.CustomerOrderStatus;
import ru.maxmorev.eshop.customer.order.api.annotation.PaymentProvider;
import ru.maxmorev.eshop.customer.order.api.entities.CommodityInfo;
import ru.maxmorev.eshop.customer.order.api.entities.CustomerOrder;
import ru.maxmorev.eshop.customer.order.api.response.CustomerOrderDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Integration Purchase Service Test")
public class OrderPurchaseServiceTest {

    private static final Long APPROVED_ORDER_ID = 25L;
    private static final Long AWAITING_PAYMENT_ORDER_ID = 16L;
    @Autowired
    private OrderPurchaseService orderPurchaseService;
    @PersistenceContext
    private EntityManager em;

    private CommodityInfo commodityInfo = new CommodityInfo(
            5L,
            1,
            45f,
            "T-SHIRT",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png");

    @Value
    public static class ShoppingCartInfo {
        long branchId;
        int branchAmount;
        int shoppingCartAmount;

        public ShoppingCartInfo(long branchId, int branchAmount, int shoppingCartAmount) {
            this.branchId = branchId;
            this.branchAmount = branchAmount;
            this.shoppingCartAmount = shoppingCartAmount;
        }
    }

    @Test
    @Transactional
    @DisplayName("should create order")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void createOrderForTest() {

        CustomerOrder newOrder = orderPurchaseService.createOrderFor(10L, List.of(commodityInfo));
        em.flush();
        Optional<CustomerOrder> order = orderPurchaseService.findOrder(newOrder.getId());
        assertTrue(order.isPresent());
    }

    @Test
    @Transactional
    @DisplayName("should check and confirm order")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void confirmPaymentOrderTest() {


        Optional<CustomerOrder> order = orderPurchaseService.findOrder(AWAITING_PAYMENT_ORDER_ID);
        assertTrue(order.isPresent());
        order.ifPresent(o -> {

            Assertions.assertEquals(CustomerOrderStatus.AWAITING_PAYMENT, o.getStatus());

            orderPurchaseService.confirmPaymentOrder(o, PaymentProvider.Paypal, "3HW05364355651909");
            em.flush();

            Optional<CustomerOrder> orderUpdate = orderPurchaseService.findOrder(AWAITING_PAYMENT_ORDER_ID);
            assertNotNull(orderUpdate.get().getPaymentID());
            /* checks status change for expected */
            Assertions.assertEquals(
                    CustomerOrderStatus.PAYMENT_APPROVED,
                    orderUpdate.get().getStatus()
            );

        });

    }

    @Test
    @Transactional
    @DisplayName("should find customer order by order ID")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void findOrderTest() {
        Optional<CustomerOrder> order = orderPurchaseService.findOrder(AWAITING_PAYMENT_ORDER_ID);
        assertTrue(order.isPresent());
    }

    @Test
    @Transactional
    @DisplayName("should find customer orders")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void findCustomerOrdersTest() {
        List<CustomerOrder> orders = orderPurchaseService.findCustomerOrders(10L);
        assertFalse(orders.isEmpty());
        assertEquals(2, orders.size());
    }

    @Test
    @Transactional
    @DisplayName("should find expired orders")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void findExpiredOrdersTest() {
        assertEquals(1, orderPurchaseService.findExpiredOrders().size());
    }

    @Test
    @Transactional
    @DisplayName("should change order status to PREPARING_TO_SHIP")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void confirmOrderPreparingToShipTest() {
        Optional<CustomerOrder> order = orderPurchaseService.findOrder(APPROVED_ORDER_ID);
        assertTrue(order.isPresent());
        Assertions.assertEquals(CustomerOrderStatus.PAYMENT_APPROVED, order.get().getStatus());
        CustomerOrder co = orderPurchaseService.setOrderStatus(APPROVED_ORDER_ID, CustomerOrderStatus.PREPARING_TO_SHIP);
        Assertions.assertEquals(CustomerOrderStatus.PREPARING_TO_SHIP, co.getStatus());
    }

    @Test
    @Transactional
    @DisplayName("should cancel order with status AWAITING_PAYMENT")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void cancelOrderAndDeleteTest() {
        orderPurchaseService.cancelOrderByCustomer(AWAITING_PAYMENT_ORDER_ID);
        em.flush();
        Optional<CustomerOrder> order = orderPurchaseService.findOrder(AWAITING_PAYMENT_ORDER_ID);
        assertTrue(order.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("should except change order status by customer")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void cancelOrderExceptionTest() {

        orderPurchaseService.cancelOrderByCustomer(APPROVED_ORDER_ID);

        orderPurchaseService.findOrder(APPROVED_ORDER_ID).ifPresent(o -> {
            assertEquals(CustomerOrderStatus.CANCELED_BY_CUSTOMER, o.getStatus());
        });

    }

    @Test
    @Transactional
    @DisplayName("should except correct list of orders dto for customer no orders with status=AWAITING_PAYMENT")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void getCorrectOrderListDtoForCustomerWithActionTest() {

        List<CustomerOrderDto> orders = orderPurchaseService.findOrderListForCustomer(10L);
        assertEquals(1, orders.size());
        assertEquals(25L, orders.get(0).getId().longValue());
        assertEquals(1, orders.get(0).getActions().size());
        assertEquals(CustomerOrderStatus.CANCELED_BY_CUSTOMER.name(),
                orders.get(0).getActions().get(0).getAction());

    }

    @Test
    @Transactional
    @DisplayName("should except correct Page for admin")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void getCorrectOrderPageForAdminTest() {

        var page = orderPurchaseService.getOrdersForAdmin(null, null, null, null);
        assertEquals(1, page.getTotalRecords());
        assertEquals(CustomerOrderStatus.PREPARING_TO_SHIP.name(),
                page.getOrderData()
                        .get(0)
                        .getActions()
                        .get(0)
                        .getAction());

    }


}
