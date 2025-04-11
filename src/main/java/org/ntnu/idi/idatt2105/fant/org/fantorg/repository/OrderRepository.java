package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Order} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations for {@link Order} entities.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {}
