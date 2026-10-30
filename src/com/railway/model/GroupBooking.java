package com.railway.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.railway.enums.BookingStatus;
import com.railway.enums.TravelClass;

/** Represents a group reservation with one PNR and per-traveller seat status. */
public class GroupBooking {
    
    private String pnr;
    private Passenger bookingOwner;
    private Train train;
    private LocalDate travelDate;
    private TravelClass travelClass;
    private TrainJourney journey;
    private List<TravellerBooking> travellers;
    private BookingStatus bookingStatus;

    public GroupBooking(String pnr, Passenger bookingOwner, Train train, LocalDate travelDate, TravelClass travelClass, TrainJourney journey, BookingStatus bookingStatus){
        
        if (pnr == null || pnr.isBlank() || bookingOwner == null || train == null || travelDate == null || travelClass == null || journey == null || bookingStatus == null) {
            throw new IllegalArgumentException("Group booking details cannot be null or invalid");
        }
        
        this.pnr = pnr;
        this.bookingOwner = bookingOwner;
        this.train = train;
        this.travelDate = travelDate;
        this.travelClass = travelClass;
        this.journey = journey;
        this.travellers = new ArrayList<>();
        this.bookingStatus = bookingStatus;
    }

    public String getPnr(){
        return this.pnr;
    }

    public Passenger getBookingOwner(){
        return bookingOwner;
    }

    public  Train getTrain(){
        return this.train;
    }

    public LocalDate getTravelDate(){
        return this.travelDate;
    }

    public TravelClass getTravelClass(){
        return this.travelClass;
    }

    public TrainJourney getJourney(){
        return this.journey;
    }

    public List<TravellerBooking> getTravellers(){
        return Collections.unmodifiableList(this.travellers);
    }

    public BookingStatus getBookingStatus(){
        return this.bookingStatus;
    }

    /** Adds a traveller, enforcing the six-traveller group limit. */
    public void addTraveller(TravellerBooking traveller) {
        if (bookingStatus != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot add travellers to a cancelled booking");
        }
        if (traveller == null) {
            throw new IllegalArgumentException("Traveller cannot be null");
        }

        if (travellers.size() >= 6) {
            throw new IllegalStateException("A group booking can have at most 6 travellers");
        }

        travellers.add(traveller);
    }

    /** Attempts to cancel the traveller at the supplied zero-based index. */
    public boolean cancelTraveller(int index){
        if (bookingStatus == BookingStatus.CANCELLED) {
            return false;
        }
        if (index < 0 || index >= travellers.size()) {
            return false;
        }

        TravellerBooking traveller = this.travellers.get(index);
        return traveller.cancelTicket();
    }

    public void updateBookingStatus(BookingStatus status){
        if (status == null) {
            throw new IllegalArgumentException("Booking status cannot be null");
        }

        this.bookingStatus = status;
    }
}
