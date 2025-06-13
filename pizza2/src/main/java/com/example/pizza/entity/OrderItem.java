package com.example.pizza.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.pizza.enums.OrderStatus;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private int pizzaId;

    // ✅ NEW FIELD: Pizza name (used for displaying in order list)
    private String pizzaName;

    private int quantity;

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    private double totalPrice;

    // Getters and Setters

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(int pizzaId) {
        this.pizzaId = pizzaId;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "OrderItem [orderItemId=" + orderItemId +
                ", customer=" + customer.getName() +
                ", pizzaId=" + pizzaId +
                ", pizzaName=" + pizzaName +
                ", quantity=" + quantity +
                ", orderDate=" + orderDate +
                ", orderStatus=" + orderStatus +
                ", totalPrice=" + totalPrice + "]";
    }

    // Optional: Override equals and hashCode for entity comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return orderItemId == orderItem.orderItemId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(orderItemId);
    }

    // Optional: Automatically calculate total price before persist
//    @PrePersist
//    @PreUpdate
//    public void calculateTotalPrice() {
//        // You may need to calculate totalPrice based on pizzaId and quantity
//        // For now, I'm using a basic formula (you can modify it based on your logic)
//        if (pizzaId > 0 && quantity > 0) {
//            // Assuming a method to get pizza price by pizzaId (you need to implement it)
//            double pizzaPrice = 10.0; // Placeholder
//            this.totalPrice = pizzaPrice * quantity;
//        }
//    }
}
