package com.railway.model;

import com.railway.enums.SeatStatus;
import com.railway.enums.TravelClass;

/** Represents one seat and tracks whether it is available or booked. */
public class Seat {
    
    private String seatNumber;
    private TravelClass travelClass;
    private SeatStatus seatStatus;

    public Seat(String seatNumber, TravelClass travelClass){
        this.seatNumber = seatNumber;
        this.travelClass = travelClass;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public String getSeatNumber() {
        return this.seatNumber;
    }

    public TravelClass getTravelClass() {
        return this.travelClass;
    }

    public SeatStatus getSeatStatus() {
        return this.seatStatus;
    }

    public boolean bookSeat() {
        if (this.seatStatus == SeatStatus.AVAILABLE) {
            this.seatStatus = SeatStatus.BOOKED;
            return true;
        }

        return false;
    }

    public boolean releaseSeat() {
        if (this.seatStatus == SeatStatus.BOOKED) {
            this.seatStatus = SeatStatus.AVAILABLE;
            return true;
        }

        return false;
    }
}
