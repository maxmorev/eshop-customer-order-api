package ru.maxmorev.eshop.customer.order.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.maxmorev.eshop.customer.order.api.entities.Purchase;
import ru.maxmorev.eshop.customer.order.api.entities.PurchaseId;

@Repository
public interface PurchaseRepository extends CrudRepository<Purchase, PurchaseId> {
}
