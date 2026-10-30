
package com.railway.main;

import java.time.LocalDate;
import java.time.LocalTime;

import com.railway.enums.BookingStatus;
import com.railway.enums.SeatStatus;
import com.railway.enums.TravelClass;
import com.railway.model.Passenger;
import com.railway.model.Seat;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.model.TrainJourney;

public class SeatTicketTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    public static void main(String[] args) {

        System.out.println("\n=== SEAT AND TICKET TESTS ===");

        // Seat setup
        Seat seat = new Seat("AC-01", TravelClass.AC);

        check("New seat is available",
                seat.getSeatStatus() == SeatStatus.AVAILABLE);

        check("First booking succeeds",
                seat.bookSeat());

        check("Booked seat has BOOKED status",
                seat.getSeatStatus() == SeatStatus.BOOKED);

        check("Booking an already-booked seat is rejected",
                !seat.bookSeat());

        check("Rejected booking preserves BOOKED status",
                seat.getSeatStatus() == SeatStatus.BOOKED);

        check("Releasing a booked seat succeeds",
                seat.releaseSeat());

        check("Released seat becomes AVAILABLE",
                seat.getSeatStatus() == SeatStatus.AVAILABLE);

        check("Releasing an available seat is rejected",
                !seat.releaseSeat());

        // Ticket setup
        Train train = new Train(
                99002,
                "Ticket Test Express",
                "Hyderabad",
                "Chennai",
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                2, 2, 2
        );

        LocalDate date = LocalDate.now().plusDays(5);
        TrainJourney journey = new TrainJourney(train, date);

        Seat ticketSeat =
                journey.getRandomAvailableSeat(TravelClass.AC);

        ticketSeat.bookSeat();

        Passenger passenger = new Passenger(
                "TEST1001",
                "Test Passenger",
                25,
                com.railway.enums.Gender.MALE,
                "9876543210",
                "test.passenger@example.com"
        );

        Ticket ticket = new Ticket(
                "RLYTEST1001",
                passenger,
                train,
                date,
                ticketSeat,
                TravelClass.AC,
                journey
        );

        check("New ticket is confirmed",
                ticket.getBookingStatus() == BookingStatus.CONFIRMED);

        check("Ticket references the correct seat",
                ticket.getSeat() == ticketSeat);

        check("Ticket references the correct journey",
                ticket.getJourney() == journey);

        ticket.cancelTicket();

        check("Cancelling ticket changes status",
                ticket.getBookingStatus() == BookingStatus.CANCELLED);

        check("Ticket cancellation does not directly release seat",
                ticketSeat.getSeatStatus() == SeatStatus.BOOKED);

        ticket.cancelTicket();

        check("Repeated ticket cancellation preserves CANCELLED status",
                ticket.getBookingStatus() == BookingStatus.CANCELLED);

        // Simulate the seat release performed by RailwaySystem.
        check("Seat can be released after ticket cancellation",
                ticketSeat.releaseSeat());

        check("Released ticket seat becomes available",
                ticketSeat.getSeatStatus() == SeatStatus.AVAILABLE);

        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total : " + (passed + failed));

        if (failed == 0) {
            System.out.println("All Seat and Ticket tests passed!");
        } else {
            System.out.println("Some Seat and Ticket tests failed.");
        }
    }
}
