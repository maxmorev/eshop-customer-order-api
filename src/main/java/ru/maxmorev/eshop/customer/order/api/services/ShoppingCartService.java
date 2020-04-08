package ru.maxmorev.eshop.customer.order.api.services;

import ru.maxmorev.eshop.customer.order.api.entities.PurchaseInfo;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCart;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartId;

import java.util.Optional;

public interface ShoppingCartService {

    ShoppingCart createEmptyShoppingCart();

    Optional<ShoppingCart> findShoppingCartById(Long id);

    ShoppingCart removeBranchFromShoppingCart(ShoppingCartId id, Integer amount);

    ShoppingCart update(ShoppingCart sc);

    ShoppingCart addBranchToShoppingCart(ShoppingCartId id, PurchaseInfo purchaseInfo);

    ShoppingCart cleanShoppingCart(Long id);

    void removeOrphan(Long id);

    ShoppingCart mergeCartFromTo(Long from, Long to);

}
