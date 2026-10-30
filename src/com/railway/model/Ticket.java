package com.railway.model;

import java.time.LocalDate;

import com.railway.enums.BookingStatus;
import com.railway.enums.TravelClass;

/** Represents one passenger's reservation for one train journey and seat. */
public class Ticket {

    private String pnr;
    private Passenger passenger;
    private Train train;
    private LocalDate travelDate;
    private Seat seat;
    private TravelClass travelClass;
    private BookingStatus bookingStatus;
    private TrainJourney journey;

    public Ticket(String pnr, Passenger passenger, Train train, LocalDate travelDate, Seat seat, TravelClass travelClass, TrainJourney journey){
        this.pnr = pnr;
        this.passenger = passenger;
        this.train = train;
        this.travelDate = travelDate;
        this.seat = seat;
        this.travelClass = travelClass;
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.journey = journey;
    }

    public String getPnr() {
        return pnr;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Train getTrain() {
        return train;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public Seat getSeat() {
        return seat;
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public TrainJourney getJourney(){
        return journey;
    }

    /** Prints the ticket details in a consistent aligned layout. */
    public void displayTicket(){
        System.out.printf("%n+--------------------------------------+%n");
        System.out.printf("| %-36s |%n", "RAILWAY TICKET");
        System.out.printf("+--------------------------------------+%n");
        System.out.printf("%-15s : %s%n", "PNR", this.pnr);
        System.out.printf("%-15s : %s%n", "Passenger", this.passenger.getName());
        System.out.printf("%-15s : %d%n", "Train Number", this.train.getTrainNumber());
        System.out.printf("%-15s : %s%n", "Train Name", this.train.getTrainName());
        System.out.printf("%-15s : %s%n", "From", this.train.getSource());
        System.out.printf("%-15s : %s%n", "To", this.train.getDestination());
        System.out.printf("%-15s : %s%n", "Journey Date", this.travelDate);
        System.out.printf("%-15s : %s%n", "Seat Number", this.seat.getSeatNumber());
        System.out.printf("%-15s : %s%n", "Class", this.travelClass);
        System.out.printf("%-15s : %s%n", "Booking Status", this.bookingStatus);
        System.out.printf("+--------------------------------------+%n");
    }

    /** Changes a confirmed ticket to cancelled; does not manage seat inventory. */
    public void cancelTicket(){
        if (this.bookingStatus == BookingStatus.CONFIRMED) {
            this.bookingStatus = BookingStatus.CANCELLED;
        } else {
            System.out.printf("Ticket is already cancelled%n");
        }
    }
}
