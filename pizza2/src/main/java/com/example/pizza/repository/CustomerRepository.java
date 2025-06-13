// src/main/java/com/example/pizza/repository/CustomerRepository.java
package com.example.pizza.repository;

import com.example.pizza.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Don't forget to import Optional

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Add this method to find a customer by email
    Optional<Customer> findByEmail(String email);
    // You could also add findByPhone(String phone) or findByNameAndEmail(String name, String email)
}