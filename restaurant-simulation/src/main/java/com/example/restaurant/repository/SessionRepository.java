package com.example.restaurant.repository;

import com.example.restaurant.model.RestaurantSession;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SessionRepository {

    private final Map<String, RestaurantSession> sessions = new HashMap<>();

    public void saveSession(String sessionId, RestaurantSession session) {
        sessions.put(sessionId, session);
    }

    public RestaurantSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public Map<String, RestaurantSession> getAllSessions() {
        return sessions;
    }
}
