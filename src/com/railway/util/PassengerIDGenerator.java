package com.railway.util;

/** Generates passenger IDs for newly registered passengers. */
public class PassengerIDGenerator {
    private static int counter = 1000;

    private PassengerIDGenerator() {

    }
    
    public static String generatePassengerID() {
        counter++;
        return "P" + counter;
    }
}
