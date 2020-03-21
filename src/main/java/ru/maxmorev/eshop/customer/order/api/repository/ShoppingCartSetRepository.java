package ru.maxmorev.eshop.customer.order.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartId;
import ru.maxmorev.eshop.customer.order.api.entities.ShoppingCartSet;

import java.util.Optional;

@Repository
public interface ShoppingCartSetRepository extends CrudRepository<ShoppingCartSet, ShoppingCartId> {

    Optional<ShoppingCartSet> findById(ShoppingCartId id);
}
