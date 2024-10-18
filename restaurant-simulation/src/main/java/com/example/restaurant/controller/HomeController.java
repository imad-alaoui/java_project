package com.example.restaurant.controller;

import com.example.restaurant.model.RestaurantSession;
import com.example.restaurant.model.RestaurantParams;
import com.example.restaurant.model.Customer;
import com.example.restaurant.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private SessionRepository sessionRepository;  // Injected session repository

    @Autowired
    private RestaurantSessionHandler webSocketHandler;  // Injected WebSocket handler

    @PostMapping("/api/create-restaurant")
    public RestaurantSession createRestaurant(@RequestBody RestaurantParams params) {
        // Pass the webSocketHandler to the RestaurantSession constructor
        RestaurantSession session = new RestaurantSession(params.getSeats(), params.getOpeningHours(), params.getServingRate(), webSocketHandler);
        sessionRepository.saveSession(session.getId(), session);  // Save the session in the repository

        // Broadcast the session creation to WebSocket clients
        webSocketHandler.broadcastSessionUpdate(session, new ArrayList<>());  // Send initial update with empty customers list

        return session;
    }

    @GetMapping("/api/sessions")
    public Map<String, RestaurantSession> listSessions() {
        return sessionRepository.getAllSessions();  // Get all sessions from the repository
    }

    @PostMapping("/api/sessions/{sessionId}/join")
    public Customer joinSession(@PathVariable String sessionId) {
        RestaurantSession session = sessionRepository.getSession(sessionId);
        if (session == null) {
            throw new RuntimeException("Session not found");
        }

        Customer customer = new Customer();  // Create customer with random values
        session.addCustomerToQueue(customer);

        webSocketHandler.broadcastSessionUpdate(session, session.getCustomers());  // Broadcast session update to WebSocket clients

        return customer;
    }
}
