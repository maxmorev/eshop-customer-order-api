package ru.maxmorev.eshop.customer.order.api.controllers;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.maxmorev.eshop.customer.order.api.request.PurchaseInfoRequest;
import ru.maxmorev.eshop.customer.order.api.request.RemoveFromCartRequest;
import ru.maxmorev.eshop.customer.order.api.request.ShoppingCartSetRequest;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DisplayName("Integration controller (ShoppingCartController) test")
@SpringBootTest
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should get shopping cart by id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void getShoppingCartTest() throws Exception {
        mockMvc.perform(get("/shoppingCart/id/11"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.shoppingSet").isArray())
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.shoppingSet[0].amount", is(2)));
    }

    ShoppingCartSetRequest getShoppingCartRequest(){
        PurchaseInfoRequest purchaseInfo = new PurchaseInfoRequest(
                5L,
                1,
                45f,
                "T-SHIRT",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png");

        return ShoppingCartSetRequest
                .builder()
                .purchaseInfo(purchaseInfo)
                .branchId(5L)
                .shoppingCartId(11L)
                .build();
    }

    @Test
    @DisplayName("Should increment amount of branch in shopping cart")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addToShoppingCartSetTest() throws Exception {
        mockMvc.perform(post("/shoppingCart/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getShoppingCartRequest().toString()))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.shoppingSet").isArray())
                .andExpect(jsonPath("$.shoppingSet[0].purchaseInfo.amount", is(3)))
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("Should increment amount of branch in shopping cart by remove method")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addToShoppingCartSetByRemoveTest() throws Exception {
        mockMvc.perform(delete("/shoppingCart/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new RemoveFromCartRequest(11L, 5L,-1).toString()))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.shoppingSet").isArray())
                .andExpect(jsonPath("$.shoppingSet[0].amount", is(3)))
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("Should expect no operation with cart")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addToShoppingCartSetByRemoveNoBranchIdTest() throws Exception {
        mockMvc.perform(delete("/shoppingCart/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new RemoveFromCartRequest(11L, 25L,-1).toString()))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.shoppingSet").isArray())
                .andExpect(jsonPath("$.shoppingSet[0].amount", is(2)))
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("Should decrement amount of branch in shopping cart")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void removeFromShoppingCartSetTest() throws Exception {
        mockMvc.perform(delete("/shoppingCart/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new RemoveFromCartRequest(11L, 5L,1).toString()))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.shoppingSet").isArray())
                .andExpect(jsonPath("$.shoppingSet[0].amount", is(1)))
                .andExpect(jsonPath("$.id", is(11)));
    }

    @Test
    @DisplayName("Should expect error because cardId is invalid")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void removeFromShoppingCartSetErrorTest() throws Exception {
        mockMvc.perform(delete("/shoppingCart/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new RemoveFromCartRequest(111L, 5L,1).toString()))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Validation error")))
                .andExpect(jsonPath("$.errors[0].message", is("Wrong Shopping Cart id")))
                .andExpect(jsonPath("$.errors[0].field", is("shoppingCartId")));
    }

    @Test
    @DisplayName("Should expect validation error in shopping cart id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addToSCSShoppingCartIdValidationTest() throws Exception {
        ShoppingCartSetRequest rscs = getShoppingCartRequest();
        rscs.setShoppingCartId(23L);
        mockMvc.perform(post("/shoppingCart/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rscs.toString()))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Validation error")))
                .andExpect(jsonPath("$.errors[0].field", is("shoppingCartId")))
                .andExpect(jsonPath("$.errors[0].message", is("Wrong Shopping Cart id")));
    }

    @Test
    @DisplayName("Should expect merged cart from cart to")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void mergeCartFromToTest() throws Exception {

        mockMvc.perform(post("/shoppingCart/merge/from/900/to/11")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.itemsAmount", is(3)))
                .andExpect(jsonPath("$.shoppingSet[0].id.branchId", is(5)))
                .andExpect(jsonPath("$.shoppingSet[1].id.branchId", is(6)));
    }
}
