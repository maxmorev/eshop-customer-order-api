package ru.maxmorev.eshop.customer.order.api.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.maxmorev.eshop.customer.order.api.config.ShoppingCartConfig;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCart;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartId;
import ru.maxmorev.eshop.customer.order.api.request.PurchaseInfoRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DisplayName("Integration Shopping Cart Service Test")
@SpringBootTest
public class ShoppingCartServiceTest {

    @Autowired
    private ShoppingCartConfig shoppingCartConfig;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PersistenceContext
    private EntityManager em;

    private PurchaseInfoRequest purchaseInfo = new PurchaseInfoRequest(
            5L,
            1,
            45f,
            "T-SHIRT",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png");


    @Test
    @Transactional
    @DisplayName("should create empty shopping cart")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void createEmptyShoppingCart() {
        ShoppingCart sc = shoppingCartService.createEmptyShoppingCart();
        em.flush();
        assertNotNull(sc.getId());
    }

    @Test
    @Transactional
    @DisplayName("should find shopping cart by id")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void findShoppingCartByIdTest() {
        assertTrue(shoppingCartService.findShoppingCartById(11L).isPresent());
    }

    @Test
    @Transactional
    @DisplayName("should decrement amount from set for branch")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void decrementBranchFromShoppingCartSetTest() {

        shoppingCartService
                .findShoppingCartById(11L).ifPresent(shoppingCart -> {
            shoppingCart.getShoppingSet().forEach(shoppingCartSet -> {
                assertEquals(2, shoppingCartSet.getAmount());
                ShoppingCart res = shoppingCartService
                        .removeBranchFromShoppingCart(
                                new ShoppingCartId(shoppingCartSet.getId().getBranchId(),
                                        shoppingCart.getId()),
                                1);
                em.flush();
                res.getShoppingSet()
                        .stream()
                        .filter(
                                s -> s
                                        .getId().getBranchId()
                                        .equals(shoppingCartSet.getId().getBranchId()))
                        .findFirst()
                        .ifPresent(scs -> {
                            assertEquals(1, scs.getAmount());
                        });

            });
        });
    }

    @Test
    @Transactional
    @DisplayName("should remove all set from shopping cart while decrement total amount for branch in set")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void removeSetFromShoppingCartTest() {
        shoppingCartService.removeBranchFromShoppingCart(new ShoppingCartId(5L, 11L), 2);
        em.flush();
        assertTrue(shoppingCartService
                .findShoppingCartById(11L)
                .get()
                .getShoppingSet()
                .isEmpty());

    }

    @Test
    @Transactional
    @DisplayName("should update shopping cart")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void updateTest() {
        ShoppingCart sc = shoppingCartService
                .findShoppingCartById(11L).get();
        assertEquals(2, sc.getItemsAmount());
        sc.getShoppingSet().clear();
        shoppingCartService.update(sc);
        em.flush();
        assertTrue(shoppingCartService.findShoppingCartById(11L).map(ShoppingCart::getShoppingSet).get().isEmpty());
    }


    @Test
    @Transactional
    @DisplayName("should add amount for branch to shopping cart set")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addBranchToShoppingCartTest() {
       var sc = shoppingCartService.findShoppingCartById(11L).get();
        assertEquals(2, sc.getItemsAmount());
       var res = shoppingCartService
                .addBranchToShoppingCart(
                        new ShoppingCartId(5L, 11L),
                        purchaseInfo.toEntity());
       em.flush();
       sc = shoppingCartService.findShoppingCartById(11L).get();
       assertEquals(3, sc.getItemsAmount());
    }

    @Test
    @Transactional
    @DisplayName("should not add amount for branch to shopping cart set")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addBranchToShoppingCartMaximumCartItemsTest() {
        shoppingCartService
                .findShoppingCartById(11L).ifPresent(shoppingCart -> {
            shoppingCart.getShoppingSet().forEach(shoppingCartSet -> {
                assertEquals(2, shoppingCartSet.getAmount());
                ShoppingCart res = shoppingCartService
                        .addBranchToShoppingCart(new ShoppingCartId(
                                        shoppingCartSet.getId().getBranchId(),
                                        shoppingCart.getId()),
                                purchaseInfo.toEntity());
                assertEquals(shoppingCartConfig.getMaxItemsAmount(), res.getItemsAmount());
                em.flush();
                res = shoppingCartService
                        .addBranchToShoppingCart(
                                new ShoppingCartId(shoppingCartSet.getId().getBranchId(),
                                        shoppingCart.getId()),
                                purchaseInfo.toEntity());
                assertEquals(shoppingCartConfig.getMaxItemsAmount(), res.getItemsAmount());
            });
        });
    }

    /**
     * should ignore adding amount for branch to shopping cart set
     * because sum(amount + added amount) > branch total amount )
     */
    @Test
    @Transactional
    @DisplayName("should ignore adding amount for branch to shopping cart set")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void addIngnoreBranchToShoppingCartTest() {
        PurchaseInfoRequest pir = new PurchaseInfoRequest(
                5L,
                4,
                45f,
                "T-SHIRT",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/SSL_Deep_Inspection_Explanation.svg/1050px-SSL_Deep_Inspection_Explanation.svg.png");
        shoppingCartService
                .findShoppingCartById(11L).ifPresent(shoppingCart -> {
            shoppingCart.getShoppingSet().forEach(shoppingCartSet -> {
                assertEquals(2, shoppingCartSet.getAmount());
                ShoppingCart res = shoppingCartService
                        .addBranchToShoppingCart(new ShoppingCartId(shoppingCartSet
                                        .getId().getBranchId(),
                                        shoppingCart.getId()),
                                pir.toEntity());
                em.flush();
                assertEquals(2, res.getItemsAmount());
            });
        });
    }

    @Test
    @Transactional
    @DisplayName("should merge cart from to by adding to shopping cart set")
    @SqlGroup({
            @Sql(value = "classpath:db/purchase/test-data.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "classpath:db/purchase/clean-up.sql",
                    config = @SqlConfig(encoding = "utf-8", separator = ";", commentPrefix = "--"),
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
    })
    public void mergeCartFromToTest() {
        ShoppingCart to = shoppingCartService.findShoppingCartById(11L).get();
        assertEquals(1, to.getShoppingSet().size());
        assertEquals(2, to.getItemsAmount());
        ShoppingCart from = shoppingCartService.findShoppingCartById(900L).get();
        assertEquals(1, from.getShoppingSet().size());
        assertEquals(1,from.getItemsAmount());
        ShoppingCart toMerged = shoppingCartService.mergeCartFromTo(900L, 11L);
        assertEquals(11L, toMerged.getId());
        assertEquals(3, toMerged.getItemsAmount());
        assertEquals(2, toMerged.getShoppingSet().size());
        assertTrue(toMerged.getShoppingSet().stream().anyMatch(sc->sc.getPurchaseInfo().getCommodityName().equals("T-SHIRT 01")));
        assertTrue(toMerged.getShoppingSet().stream().anyMatch(sc->sc.getPurchaseInfo().getCommodityName().equals("T-SHIRT 02")));
    }


}
