package ru.maxmorev.eshop.customer.order.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.maxmorev.eshop.customer.order.api.annotation.CustomerOrderStatus;
import ru.maxmorev.eshop.customer.order.api.annotation.PaymentProvider;
import ru.maxmorev.eshop.customer.order.api.request.OrderPaymentConfirmation;
import ru.maxmorev.eshop.customer.order.api.request.PaymentInitialRequest;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 4555)
@RunWith(SpringRunner.class)
@DisplayName("Integration controller (OrderPurchaseController) test")
@SpringBootTest
public class OrderPurchaseControllerTest {

    private static final Long APPROVED_ORDER_ID = 25L;
    private static final Long AWAITING_ORDER_ID = 16L;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should confirm order")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void confirmOrderTest() throws Exception {

        OrderPaymentConfirmation opc = OrderPaymentConfirmation
                .builder()
                .paymentId("3HW05364355651909")
                .orderId(16L)
                .paymentProvider(PaymentProvider.Paypal.name())
                .build();
        log.info("Request: {}", opc.toString());
        mockMvc.perform(put("/order/confirmation/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(opc.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(16)))
                .andExpect(jsonPath("$.paymentID", is("3HW05364355651909")))
                .andExpect(jsonPath("$.purchases[0].id.branchId", is(5)))
                .andExpect(jsonPath("$.status", is(CustomerOrderStatus.PAYMENT_APPROVED.name())));

    }

    @Test
    @DisplayName("Should except validation errors: Wrond orderId")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void confirmOrderWrongOrderIdErrorTest() throws Exception {
        OrderPaymentConfirmation opc = OrderPaymentConfirmation
                .builder()
                .paymentId("3HW05364355651909")
                .orderId(166L)
                .paymentProvider(PaymentProvider.Paypal.name())
                .build();
        mockMvc.perform(put("/order/confirmation/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(opc.toString()))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Order not found")));

    }

    @Test
    @DisplayName("Should except validation errors: Invalid order status")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void confirmOrderWrongOrderStatusErrorTest() throws Exception {
        OrderPaymentConfirmation opc = OrderPaymentConfirmation
                .builder()
                .paymentId("3HW05364355651909")
                .orderId(25L)
                .paymentProvider(PaymentProvider.Paypal.name())
                .build();
        mockMvc.perform(put("/order/confirmation/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(opc.toString()))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Invalid order status")));
    }

    @Test
    @DisplayName("Should return customer's order list")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void customerOrderListTest() throws Exception {
        mockMvc.perform(get("/order/list/customer/10"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId", is(10)))
                .andExpect(jsonPath("$[0].status", is("PAYMENT_APPROVED")));
    }

    @Test
    @DisplayName("Should return order by id and customer.id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void customerGetOrderTest() throws Exception {
        mockMvc.perform(get("/order/25/customer/10/"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(25)))
                .andExpect(jsonPath("$.customerId", is(10)))
                .andExpect(jsonPath("$.status", is("PAYMENT_APPROVED")));
    }

    @Test
    @DisplayName("Should return error by id and customer.id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void customerOrderErrorTest() throws Exception {
        mockMvc.perform(get("/order/25/customer/15/"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("No such order")));
    }


//    @Test
//    @DisplayName("Should expect error with wrong Authorities")
//    @SqlGroup({
//            @Sql(value = "classpath:db/purchase/test-data.sql",
//                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
//                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
//            @Sql(value = "classpath:db/purchase/clean-up.sql",
//                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
//                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
//    })
//    public void prepareToShipException() throws Exception {
//        mockMvc.perform(put("/order/PREPARING_TO_SHIP/" + APPROVED_ORDER_ID)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is(403));
//    }

    @Test
    @DisplayName("Should change order status")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void prepareToShipOk() throws Exception {
        mockMvc.perform(put("/order/" + APPROVED_ORDER_ID + "/PREPARING_TO_SHIP"))
                .andDo(print())
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Should expect error with invalid order id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void prepareToShipInvalidIdTest() throws Exception {

        mockMvc.perform(put("/order/" + 2929 + "/PREPARING_TO_SHIP/"))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Invalid order id")));
    }

    @Test
    @DisplayName("Should expect error with invalid Status")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void prepareToShipInvalidStatusTest() throws Exception {
        mockMvc.perform(put("/order/" + APPROVED_ORDER_ID + "/PREPARING_TO_ROCK"))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Invalid status")));
    }

    @Test
    @DisplayName("Should remove expired order by id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void removeExpiredOrderTest() throws Exception {
        mockMvc.perform(delete("/order/expired/" + AWAITING_ORDER_ID))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.message", is("Success")));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should set paymentID")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void paymentInitial() {
        PaymentInitialRequest paymentInitial = new PaymentInitialRequest()
                .setPaymentID("INITIAL-PAYMENT-ID")
                .setPaymentProvider("Yoomoney")
                .setOrderId(16L);
        mockMvc.perform(put("/order/payment/initial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(paymentInitial)))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.id", is(16)))
                .andExpect(jsonPath("$.data.status", is("AWAITING_PAYMENT")))
                .andExpect(jsonPath("$.data.paymentID", is("INITIAL-PAYMENT-ID")))
                .andExpect(jsonPath("$.data.paymentProvider", is("Yoomoney")))
        ;
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return RestResponse with fail status")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void paymentInitialNotFoundOrder() {
        PaymentInitialRequest paymentInitial = new PaymentInitialRequest()
                .setPaymentID("INITIAL-PAYMENT-ID")
                .setOrderId(166L);
        mockMvc.perform(put("/order/payment/initial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(paymentInitial)))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.errorMessage", is("Order not found")))
        ;
    }

    @Test
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void adminAllCustomerOrderList() throws Exception {
        mockMvc.perform(get("/order/list/all"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.orderData").isArray())
                ;
    }

}
