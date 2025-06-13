package com.example.pizza.controller;

import com.example.pizza.entity.Customer;
import com.example.pizza.entity.OrderItem;
import com.example.pizza.enums.OrderStatus;
import com.example.pizza.repository.CustomerRepository;
import com.example.pizza.repository.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional; // Needed for Optional<Customer>

@Controller
public class OrderController {

    // Using constructor injection for dependencies (Spring's recommended practice)
    private final CustomerRepository customerRepo;
    private final OrderItemRepository orderRepo;

    // Static maps to store pizza names and prices (for simplicity)
    private static final Map<Integer, String> pizzaNames = new HashMap<>();
    private static final Map<Integer, Double> pizzaPrices = new HashMap<>();

    // Initialize static maps when the class is loaded
    static {
        pizzaNames.put(1, "Margherita");
        pizzaNames.put(2, "Pepperoni");
        pizzaNames.put(3, "Veggie Delight");

        pizzaPrices.put(1, 199.0);
        pizzaPrices.put(2, 249.0);
        pizzaPrices.put(3, 329.0);
    }

    // Constructor for dependency injection
    public OrderController(CustomerRepository customerRepo, OrderItemRepository orderRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }

    /**
     * Handles GET requests to the root URL ("/") and "/orderForm".
     * Displays the pizza order form.
     * @param model The Model object to pass data to the view.
     * @return The name of the Thymeleaf template ("form.html").
     */
    @GetMapping({"/", "/orderForm"}) // Maps both "/" and "/orderForm" to this method
    public String showForm(Model model) {
        // Add pizza names to the model if form.html needs to display them dynamically
        model.addAttribute("pizzaNames", pizzaNames);
        return "form"; // Renders src/main/resources/templates/form.html
    }

    /**
     * Handles POST requests for submitting a new pizza order.
     * Creates or finds a customer and saves a new order item.
     * @param customerName The name of the customer.
     * @param email The email of the customer.
     * @param phone The phone number of the customer.
     * @param pizzaId The ID of the selected pizza.
     * @param quantity The quantity of pizzas ordered.
     * @return A redirect to the success page.
     */
    @PostMapping("/submitOrder")
    public String placeOrder(
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam int pizzaId,
            @RequestParam int quantity
    ) {
        // --- Customer Handling: Find existing customer or create new ---
        // Attempt to find a customer by email. You MUST add findByEmail to CustomerRepository.
        Optional<Customer> existingCustomer = customerRepo.findByEmail(email);

        Customer customer;
        if (existingCustomer.isPresent()) {
            // If customer exists, use the existing customer entity
            customer = existingCustomer.get();
            // Optionally update name/phone if they changed, though not strictly necessary for this project's scope
            customer.setName(customerName);
            customer.setPhone(phone);
            customerRepo.save(customer); // Update existing customer if details changed
        } else {
            // If customer does not exist, create and save a new one
            customer = new Customer();
            customer.setName(customerName);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer = customerRepo.save(customer); // Save the new customer
        }

        // --- OrderItem Creation ---
        OrderItem order = new OrderItem();
        order.setCustomer(customer); // Link the order to the (existing or new) customer
        order.setPizzaId(pizzaId);
        order.setPizzaName(pizzaNames.get(pizzaId)); // Get pizza name from static map
        order.setQuantity(quantity);
        // Calculate total price using the static map, with a default if pizzaId is invalid
        order.setTotalPrice(quantity * pizzaPrices.getOrDefault(pizzaId, 0.0)); // Use 0.0 as default for safety
        order.setOrderStatus(OrderStatus.PENDING); // Default to PENDING status

        // For debugging: print details to console
        System.out.println("Placing Order: Customer: " + customerName + ", Pizza ID: " + pizzaId +
                           ", Quantity: " + quantity + ", Total Price: " + order.getTotalPrice());

        orderRepo.save(order); // Save the new order item

        return "redirect:/success"; // Redirect to the success page after submission
    }

    /**
     * Handles GET requests to "/orders".
     * Displays a paginated list of all orders.
     * @param page The current page number (default to 0).
     * @param size The number of items per page (default to 10).
     * @param model The Model object to pass data to the view.
     * @return The name of the Thymeleaf template ("orderList.html").
     */
    @GetMapping("/orders")
    public String viewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);
        // Retrieve a page of OrderItem entities from the repository
        Page<OrderItem> orderPage = orderRepo.findAll(pageable);

        // Add order data and pagination info to the model
        model.addAttribute("orders", orderPage.getContent()); // List of orders for the current page
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", orderPage.getTotalPages()); // Total number of pages
        model.addAttribute("totalItems", orderPage.getTotalElements()); // Total number of order items
        // Add flags for pagination links
        model.addAttribute("hasNextPage", orderPage.hasNext());
        model.addAttribute("hasPreviousPage", orderPage.hasPrevious());

        return "orderList"; // Renders src/main/resources/templates/orderList.html
    }

    /**
     * Handles GET requests to "/success".
     * Displays the order success page.
     * @return The name of the Thymeleaf template ("success.html").
     */
    @GetMapping("/success")
    public String successPage() {
        return "success"; // Renders src/main/resources/templates/success.html
    }

    /**
     * Handles GET requests to "/orderStatus/{id}".
     * Displays the status of a specific order by its ID.
     * @param id The ID of the order to check.
     * @param model The Model object to pass data to the view.
     * @return The name of the Thymeleaf template ("orderStatus.html").
     */
    @GetMapping("/orderStatus/{id}")
    public String checkOrderStatus(@PathVariable int id, Model model) {
        // Find the order by its ID, or return null if not found
        OrderItem order = orderRepo.findById(id).orElse(null);

        if (order == null) {
            // If order not found, add an error message to the model
            model.addAttribute("error", "Order not found with ID: " + id);
        } else {
            // If order found, add the order object to the model
            model.addAttribute("order", order);
        }

        return "orderStatus"; // Renders src/main/resources/templates/orderStatus.html
    }
}
