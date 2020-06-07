package ru.maxmorev.eshop.customer.order.api.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maxmorev.eshop.customer.order.api.config.ShoppingCartConfig;
import ru.maxmorev.eshop.customer.order.api.entities.PurchaseInfo;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCart;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartId;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartSet;
import ru.maxmorev.eshop.customer.order.api.repository.ShoppingCartRepository;
import ru.maxmorev.eshop.customer.order.api.repository.ShoppingCartSetRepository;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service("shoppingCartService")
@Transactional
@AllArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartConfig config;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartSetRepository shoppingCartSetRepository;

    @Override
    public ShoppingCart createEmptyShoppingCart() {
        ShoppingCart newCart = new ShoppingCart();
        shoppingCartRepository.save(newCart);
        return newCart;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShoppingCart> findShoppingCartById(Long id) {
        return shoppingCartRepository.findById(id);
    }

    private void isValidShoppingCartSet(ShoppingCartSet shoppingCartSet) {
        if (Objects.isNull(shoppingCartSet)) {
            throw new IllegalArgumentException("Illegal argument: ShoppingCartSet is null");
        }
        if (Objects.isNull(shoppingCartSet.getAmount()) || shoppingCartSet.getAmount() <= 0) {
            throw new IllegalArgumentException("Illegal argument amount=" + shoppingCartSet.getAmount());
        }

        if (Objects.isNull(shoppingCartSet.getId().getBranchId())) {
            throw new IllegalArgumentException("Illegal argument branch=" + shoppingCartSet.getId().getBranchId());
        }

        if (Objects.isNull(shoppingCartSet.getShoppingCart())) {
            throw new IllegalArgumentException("Illegal argument shoppingCart=" + shoppingCartSet.getShoppingCart());
        }
    }

    //@Override
    protected ShoppingCart addToShoppingCartSet(ShoppingCartSet shoppingCartSet, Integer amount) {
        isValidShoppingCartSet(shoppingCartSet);
        shoppingCartSet.getPurchaseInfo().setAmount(shoppingCartSet.getPurchaseInfo().getAmount() + amount);
        var set = shoppingCartSetRepository.save(shoppingCartSet);
        return set.getShoppingCart();
    }

    @Override
    public ShoppingCart addBranchToShoppingCart(ShoppingCartId id, PurchaseInfo purchaseInfo) {
        log.info("---- addBranchToShoppingCart");
        if (id == null) throw new IllegalArgumentException("id can not be null");
        if (id.getBranchId() == null) throw new IllegalArgumentException("branchId can not be null");
        if (purchaseInfo == null) throw new IllegalArgumentException("commodityInfo can not be null");
        if (id.getShoppingCartId() == null) throw new IllegalArgumentException("shoppingCartId can not be null");
        if (purchaseInfo.getAmount() == null) throw new IllegalArgumentException("amount can not be null");
        ShoppingCart shoppingCart = this.findShoppingCartById(id.getShoppingCartId())
                .orElseThrow(() -> new IllegalArgumentException("Cant find shopping cart by id"));
        log.info("shoppingCart.getItemsAmount() {} purchaseInfo.getAmount() {}", shoppingCart.getItemsAmount(), purchaseInfo.getAmount());
        if (config.getMaxItemsAmount() == shoppingCart.getItemsAmount()
                || (shoppingCart.getItemsAmount() + purchaseInfo.getAmount()) > config.getMaxItemsAmount())
            return shoppingCart;
        var scs = shoppingCartSetRepository.findById(id);
        if (scs.isPresent()) {
            return addToShoppingCartSet(scs.get(), purchaseInfo.getAmount());
        } else {
            shoppingCart
                    .getShoppingSet()
                    .add(new ShoppingCartSet(id.getBranchId(), shoppingCart, purchaseInfo));
            return shoppingCartRepository.save(shoppingCart);
        }

    }

    @Override
    public ShoppingCart removeBranchFromShoppingCart(ShoppingCartId id, Integer amount) {
        if (id == null) throw new IllegalArgumentException("id can not be null");
        if (id.getBranchId() == null) throw new IllegalArgumentException("branchId can not be null");
        if (amount == null) throw new IllegalArgumentException("amount can not be null");
        if (id.getShoppingCartId() == null) throw new IllegalArgumentException("shoppingCartId can not be null");
        var scs = shoppingCartSetRepository.findById(id);
        if(scs.isPresent())
            if (scs.get().getAmount() - amount <= 0) {
                shoppingCartSetRepository.delete(scs.get());
            } else {
                 addToShoppingCartSet(scs.get(), -amount);
            }
        return shoppingCartRepository.findById(id.getShoppingCartId()).orElseThrow(()->new IllegalArgumentException("Shopping Cart not found"));
    }


    @Override
    public ShoppingCart update(ShoppingCart sc) {
        return shoppingCartRepository.save(sc);
    }

    @Override
    public ShoppingCart cleanShoppingCart(Long id) {
        ShoppingCart sc = shoppingCartRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));
        sc.getShoppingSet().clear();
        return shoppingCartRepository.save(sc);
    }

    @Override
    public void removeOrphan(Long id) {
        shoppingCartRepository.deleteById(id);
    }

    public ShoppingCart mergeFromTo(ShoppingCart from, ShoppingCart to) {
        log.info("mergeFromTo {}, {}", from, to);
        if (from != null && to != null && !Objects.equals(from, to)) {
            for (ShoppingCartSet set : from.getShoppingSet()) {

                this.addBranchToShoppingCart(
                        new ShoppingCartId(set.getId().getBranchId(), to.getId()),
                        set.getPurchaseInfo());
            }
            shoppingCartRepository.delete(from);
        }
        return shoppingCartRepository.findById(to.getId()).get();
    }

    @Override
    public ShoppingCart mergeCartFromTo(Long from, Long to) {
        return mergeFromTo(
                shoppingCartRepository
                        .findById(from)
                        .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found")),
                shoppingCartRepository
                        .findById(to)
                        .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"))
        );
    }

    //    @Override
//    public ShoppingCart mergeCartFromCookieWithCustomer(Long shoppingCartCookie, Long customerId)
//        if (
//                Objects.nonNull(customer.getShoppingCart())
//                        && !Objects.equals(customer.getShoppingCart(), sc)
//        ) {
//            //merge cart from cookie to customer cart
//            log.info("merging cart");
//            sc = mergeFromTo(sc, customer.getShoppingCart());
//            log.info("update cookie");
//
//        }
//        if (Objects.isNull(customer.getShoppingCartId())) {
//            customer.setShoppingCart(sc);
//            customerRepository.save(customer);
//        }
//        return checkAvailabilityByBranches(sc);
//    }
}
