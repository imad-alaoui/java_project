package com.example.restaurant.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import com.example.restaurant.controller.RestaurantSessionHandler;

public class RestaurantSession {
    private String id;
    private int seats;
    private int availableSeats;
    private int openingHours;
    private int servingRate;
    private int customersWaiting;
    private int customersEating;
    private int customersLeft;

    private Queue<Customer> customerQueue;
    private RestaurantSessionHandler webSocketHandler;  // WebSocket handler

    public RestaurantSession(int seats, int openingHours, int servingRate, RestaurantSessionHandler webSocketHandler) {
        this.id = UUID.randomUUID().toString();
        this.seats = seats;
        this.availableSeats = seats;
        this.openingHours = openingHours;
        this.servingRate = servingRate;
        this.customersWaiting = 0;
        this.customersEating = 0;
        this.customersLeft = 0;
        this.customerQueue = new LinkedList<>();
        this.webSocketHandler = webSocketHandler;  // Initialize WebSocket handler
    }

    public void addCustomerToQueue(Customer customer) {
        customerQueue.add(customer);
        customersWaiting++;
        
        System.out.println("Customer added to queue: " + customer.getId());  // Add log

        // Broadcast the updated status
        webSocketHandler.broadcastSessionUpdate(this, getCustomers());
    }
        // When a customer starts eating (add this method)
public void startEating() {
    if (customersWaiting > 0 && availableSeats > 0) {
        // customersWaiting--;
        // customersEating++;
        // availableSeats--;

        System.out.println("Customer started eating. Available seats: " + availableSeats + ", Eating customers: " + customersEating);
        
        // Broadcast the updated status when customer starts eating
        webSocketHandler.broadcastSessionUpdate(this, getCustomers());

        // Simulate serving more customers after the current one finishes eating
        new Thread(() -> {
            try {
                Thread.sleep(servingRate * 1000);  // Simulate the serving rate in seconds
                serveCustomers();  // Serve more customers after the current finishes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}


// Serve customers when seats are available
public void serveCustomers() {
    while (availableSeats > 0 && !customerQueue.isEmpty()) {
        Customer customer = customerQueue.poll();
        if (customer != null) {
            System.out.println("Serving customer: " + customer.getId() + " with eating speed: " + customer.getEatingSpeed() + " seconds.");
            customersWaiting--;
            customersEating++;
            availableSeats--;

            // Broadcast the updated status when customer starts eating
            webSocketHandler.broadcastSessionUpdate(this, getCustomers());

            // Simulate eating based on the customer's eating speed
            new Thread(() -> {
                try {
                    Thread.sleep(customer.getEatingSpeed() * 1000);  // Use the customer's eating speed
                    System.out.println("Customer finished eating: " + customer.getId());
                    finishEating(customer);  // When done eating, customer leaves
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}


// When a customer finishes eating
public void finishEating(Customer customer) {
    if (customersEating > 0) {  // Ensure customersEating is valid
        customersEating--;
        customersLeft++;
        availableSeats++;

        // Broadcast the updated status when customer finishes eating
        webSocketHandler.broadcastSessionUpdate(this, getCustomers());

        // Continue serving the next customers
        serveCustomers();
    }
}


    // Getters and setters
    public String getId() { return id; }
    public int getSeats() { return seats; }
    public int getAvailableSeats() { return availableSeats; }
    public int getCustomersWaiting() { return customersWaiting; }
    public int getCustomersEating() { return customersEating; }
    public int getCustomersLeft() { return customersLeft; }

    // Get customers in queue and eating
    public List<Customer> getCustomers() {
        List<Customer> allCustomers = new LinkedList<>(customerQueue);
        return allCustomers;
    }
}
