package com.example.pizza.repository;

import com.example.pizza.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Custom query to find orders by customer name
    List<OrderItem> findByCustomer_Name(String customerName);

    // Custom query to find orders by pizza id
    List<OrderItem> findByPizzaId(int pizzaId);

    // ✅ Corrected custom query to find orders by customerId
    List<OrderItem> findByCustomer_CustomerId(int customerId);
}
