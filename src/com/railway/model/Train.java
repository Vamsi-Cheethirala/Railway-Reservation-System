package com.railway.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.railway.enums.SeatStatus;
import com.railway.enums.TravelClass;

/** Represents a train, its route, timetable, and configured seat capacities. */
public class Train {
    
    private int trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private List<Seat> seats;
    private int acSeats;
    private int sleeperSeats;
    private int generalSeats;

    public Train(int trainNumber, String trainName, String source, String destination, LocalTime departureTime, LocalTime arrivalTime, int acSeats, int sleeperSeats, int generalSeats){
        if (trainNumber <= 0) {
            throw new IllegalArgumentException("Train number must be positive");
        }

        if (acSeats < 0 || sleeperSeats < 0 || generalSeats < 0) {
            throw new IllegalArgumentException("Seat capacities cannot be negative");
        }
        
        if (trainName == null || source == null || destination == null || trainName.isBlank() || source.isBlank() || destination.isBlank()) {
            throw new IllegalArgumentException("Train name, source, and destination cannot be null or blank");
        }
        
        this.departureTime = Objects.requireNonNull(departureTime, "Departure time cannot be null");
        this.arrivalTime = Objects.requireNonNull(arrivalTime, "Arrival time cannot be null");
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.seats = new ArrayList<>();
        this.acSeats = acSeats;
        this.sleeperSeats = sleeperSeats;
        this.generalSeats = generalSeats;
        generateSeats(acSeats,TravelClass.AC);
        generateSeats(sleeperSeats, TravelClass.SLEEPER);
        generateSeats(generalSeats, TravelClass.GENERAL);
    }

    public int getTrainNumber(){
        return this.trainNumber;
    }

    public String getTrainName(){
        return this.trainName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    private void generateSeats(int noOfSeats, TravelClass travelClass){
        for (int i = 1; i <= noOfSeats; i++) {
            String seatNumber = String.format(travelClass.getPrefix() + "-%02d", i);
    
            this.seats.add(new Seat(seatNumber, travelClass));
        }
    }

    public int getAcSeats(){
        return this.acSeats;
    }

    public int getSleeperSeats(){
        return this.sleeperSeats;
    }

    public int getGeneralSeats(){
        return this.generalSeats;
    }
    
    public int getAvailableSeats(){
        int count = 0;
        for (Seat seat : this.seats) {
            if (seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                count++;
            }
        }
        return count;
    }

    public int getTotalSeats(){
        return this.seats.size();
    }

    public List<Seat> getSeats(){
        return Collections.unmodifiableList(this.seats);
    }
    
    public Seat getSeat(String seatNumber){
        
        for (Seat seat : this.seats) {
            if (seatNumber.equalsIgnoreCase(seat.getSeatNumber()) && seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                return seat;
            }
        }
        
        return null;
    }

    //Over Loading a method(getAvailableSeats)
    public int getAvailableSeats(TravelClass travelClass){
        int count = 0;
        for (Seat seat : this.seats) {
            if (seat.getTravelClass() == travelClass && seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                count++;
            }
        }
        return count;
    }

    public Seat getRandomAvailableSeat(TravelClass travelClass){
        
        List<Seat> availableSeats = new ArrayList<>();

        for (Seat seat : this.seats) {
            if (seat.getTravelClass() == travelClass && seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                availableSeats.add(seat);
            }
        }
        
        if (!availableSeats.isEmpty()) {
            return availableSeats.get(new Random().nextInt(availableSeats.size()));
        }
        
        return null;
    }

    /** Prints train information in a compact, aligned format. */
    public void displayTrainDetails() {
        System.out.printf("%n+------------------------------------------------------------+%n");
        System.out.printf("| %-58s |%n", "TRAIN DETAILS");
        System.out.printf("+------------------------------------------------------------+%n");
        System.out.printf("| %-18s : %-37s |%n", "Train Number", this.trainNumber);
        System.out.printf("| %-18s : %-37s |%n", "Train Name", this.trainName);
        System.out.printf("| %-18s : %-37s |%n", "Source", this.source);
        System.out.printf("| %-18s : %-37s |%n", "Destination", this.destination);
        System.out.printf("| %-18s : %-37s |%n", "Departure", this.departureTime);
        System.out.printf("| %-18s : %-37s |%n", "Arrival", this.arrivalTime);
        System.out.printf("+------------------------------------------------------------+%n");
    }
}
