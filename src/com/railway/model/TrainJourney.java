package com.railway.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.railway.enums.SeatStatus;
import com.railway.enums.TravelClass;

/** Maintains date-specific seat inventory for a particular train. */
public class TrainJourney {
    private Train train;
    private LocalDate journeyDate;
    private List<Seat> seats;

    
    public TrainJourney(Train train, LocalDate journeyDate) {

        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null.");
        }

        if (journeyDate == null) {
            throw new IllegalArgumentException("Journey date cannot be null.");
        }

        this.train = train;
        this.journeyDate = journeyDate;
        this.seats = new ArrayList<>();

        generateSeats(train.getAcSeats(), TravelClass.AC);
        generateSeats(train.getSleeperSeats(), TravelClass.SLEEPER);
        generateSeats(train.getGeneralSeats(), TravelClass.GENERAL);
    }


    public Train getTrain(){
        return this.train;
    }

    public LocalDate getJourneyDate(){
        return this.journeyDate;
    }

    public List<Seat> getSeats(){
        return new ArrayList<>(this.seats);
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

    public int getAvailableSeats(TravelClass travelClass){
        int count = 0;

        for (Seat seat : this.seats) {
            if (seat.getTravelClass() == travelClass && seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                count++;
            }
        }

        return count;
    }

    private void generateSeats(int noOfSeats, TravelClass travelClass){
        for (int i = 1; i <= noOfSeats; i++){
            String seatNumber = String.format(travelClass.getPrefix() + "-%02d", i);
            this.seats.add(new Seat(seatNumber, travelClass));
        }
    }

    /** Returns a randomly selected available seat, or null when none remain. */
    public Seat getRandomAvailableSeat(TravelClass travelClass){
        
        if (travelClass == null) {
            return null;
        }
        
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

    /** Adds seats or removes only available seats, never booked seats. */
    public boolean adjustSeatCapacity(TravelClass travelClass, int change) {

        if (travelClass == null) {
            return false;
        }

        if (change == 0) {
            return true;
        }

        if (change == Integer.MIN_VALUE) {
            return false;
        }

        if (change > 0) {

            int maxSeatNumber = 0;

            for (Seat seat : this.seats) {
                if (seat.getTravelClass() == travelClass) {
                    String[] parts = seat.getSeatNumber().split("-");
                    int seatNumber = Integer.parseInt(parts[1]);

                    if (seatNumber > maxSeatNumber) {
                        maxSeatNumber = seatNumber;
                    }
                }
            }

            for (int i = 1 ; i <= change; i++) {
                int nextSeatNumber = maxSeatNumber + i;
                String seatNumber = String.format(travelClass.getPrefix() + "-%02d", nextSeatNumber);

                this.seats.add(new Seat(seatNumber, travelClass));
            }

            return true;
        }

        int seatsToRemove = -change;
        List<Seat> availableSeats = new ArrayList<>();

        for (Seat seat : this.seats) {
            if (seat.getTravelClass() == travelClass && seat.getSeatStatus() == SeatStatus.AVAILABLE) {
                availableSeats.add(seat);
            }
        }

        if (availableSeats.size() < seatsToRemove) {
            return false;
        }

        // Remove exactly the requested number of available seats
        for (int i = 0; i < seatsToRemove; i++) {
            this.seats.remove(availableSeats.get(i));
        }

        return true;
    }

}
