package com.example.restaurant.controller;

import com.example.restaurant.model.RestaurantSession;
import com.example.restaurant.model.Customer;
import com.example.restaurant.repository.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestaurantSessionHandler extends TextWebSocketHandler {

    @Autowired
    private SessionRepository sessionRepository;  // Injected session repository

    private Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket connection established with session: " + session.getId());

        // Get the session ID from the WebSocket URL
        String uri = session.getUri().toString();
        String sessionId = uri.substring(uri.lastIndexOf("/") + 1);

        // Get the restaurant session from the repository
        RestaurantSession restaurantSession = sessionRepository.getSession(sessionId);

        if (restaurantSession != null) {
            // Send the initial data to the client
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("session", restaurantSession);
            sessionData.put("customers", restaurantSession.getCustomers());  // Send the current customers

            String message = new ObjectMapper().writeValueAsString(sessionData);
            session.sendMessage(new TextMessage(message));  // Send the data to the client
            System.out.println("Sent initial restaurant data to client.");
        } else {
            System.out.println("Restaurant session not found for sessionId: " + sessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("WebSocket connection closed for session: " + session.getId());
    }

@Override
public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    Map<String, Object> receivedData = new ObjectMapper().readValue(payload, HashMap.class);

    if ("updateCustomerStatus".equals(receivedData.get("action"))) {
        String sessionId = (String) receivedData.get("sessionId");
        String customerStatus = (String) receivedData.get("status");

        System.out.println("Received status update: " + customerStatus + " for session: " + sessionId);

        // Retrieve the session from your repository or session store
        RestaurantSession restaurantSession = sessionRepository.getSession(sessionId);
        if (restaurantSession != null) {
            
            
            if ("Eating".equals(customerStatus)) {
                restaurantSession.startEating();
            } else if ("Finished eating".equals(customerStatus)) {
                // We need to pass the customer object to finishEating()
                // Assuming you can identify which customer finished, you should pass the correct customer object.
                // For simplicity, here we are just calling finishEating on the first customer.
                Customer customer = restaurantSession.getCustomers().get(0); // Update this as needed
                restaurantSession.finishEating(customer);
            }

            // Broadcast the updated session state to all connected clients
            // broadcastSessionUpdate(restaurantSession, restaurantSession.getCustomers());
        }
    }
}


public void broadcastSessionUpdate(RestaurantSession restaurantSession, List<Customer> customers) {
    System.out.println("Broadcasting session update: " + restaurantSession.getId());  // Add log here
    
    for (WebSocketSession webSocketSession : sessions.values()) {
        try {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("session", restaurantSession);
            sessionData.put("customers", customers);

            String message = new ObjectMapper().writeValueAsString(sessionData);
            webSocketSession.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}
