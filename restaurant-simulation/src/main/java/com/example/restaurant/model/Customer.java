package com.example.restaurant.model;

import java.util.Random;

public class Customer {
    private String id;
    private int waitingTolerance;  // Time the customer will wait in queue (15-60 seconds)
    private int eatingSpeed;       // Time the customer takes to eat (10-20 seconds)

    public Customer() {
        this.id = java.util.UUID.randomUUID().toString();
        Random random = new Random();
        this.waitingTolerance = 15 + random.nextInt(46);  // Random between 15 and 60
        this.eatingSpeed = 10 + random.nextInt(11);       // Random between 10 and 20
    }


    // Getters and setters
    public String getId() { return id; }
    public int getWaitingTolerance() { return waitingTolerance; }
    public int getEatingSpeed() { return eatingSpeed; }
}
