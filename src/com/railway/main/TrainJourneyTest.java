
package com.railway.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.railway.enums.SeatStatus;
import com.railway.enums.TravelClass;
import com.railway.model.Seat;
import com.railway.model.Train;
import com.railway.model.TrainJourney;

public class TrainJourneyTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }

    private static Train createTestTrain() {
        return new Train(
            99001,
            "Test Express",
            "Hyderabad",
            "Chennai",
            LocalTime.of(10, 0),
            LocalTime.of(18, 0),
            3,  // AC seats
            4,  // Sleeper seats
            2   // General seats
        );
    }

    public static void main(String[] args) {

        System.out.println("\n=== TRAIN JOURNEY TESTS ===");

        Train train = createTestTrain();
        LocalDate journeyDate = LocalDate.now().plusDays(5);
        TrainJourney journey = new TrainJourney(train, journeyDate);

        // 1. Initial inventory
        check("Journey stores the correct train",
                journey.getTrain() == train);

        check("Journey stores the correct date",
                journey.getJourneyDate().equals(journeyDate));

        check("Initial total seat count is 9",
                journey.getSeats().size() == 9);

        check("Initial AC availability is 3",
                journey.getAvailableSeats(TravelClass.AC) == 3);

        check("Initial Sleeper availability is 4",
                journey.getAvailableSeats(TravelClass.SLEEPER) == 4);

        check("Initial General availability is 2",
                journey.getAvailableSeats(TravelClass.GENERAL) == 2);

        check("Initial total available seats is 9",
                journey.getAvailableSeats() == 9);

        // 2. Random seat selection
        Seat selected = journey.getRandomAvailableSeat(TravelClass.AC);

        check("Random selection returns an AC seat",
                selected != null
                && selected.getTravelClass() == TravelClass.AC
                && selected.getSeatStatus() == SeatStatus.AVAILABLE);

        check("Null travel class returns no seat",
                journey.getRandomAvailableSeat(null) == null);

        // 3. Book a seat and ensure it is not counted as available
        selected.bookSeat();

        check("Booking changes seat status to BOOKED",
                selected.getSeatStatus() == SeatStatus.BOOKED);

        check("Booking reduces available AC seats to 2",
                journey.getAvailableSeats(TravelClass.AC) == 2);

        check("Booking reduces total available seats to 8",
                journey.getAvailableSeats() == 8);

        // 4. Increase capacity
        boolean increased =
                journey.adjustSeatCapacity(TravelClass.AC, 2);

        check("AC capacity increase succeeds", increased);

        check("AC availability increases by 2",
                journey.getAvailableSeats(TravelClass.AC) == 4);

        check("Total inventory increases to 11",
                journey.getSeats().size() == 11);

        // 5. Decrease capacity
        boolean decreased =
                journey.adjustSeatCapacity(TravelClass.SLEEPER, -2);

        check("Sleeper capacity decrease succeeds", decreased);

        check("Sleeper availability decreases by 2",
                journey.getAvailableSeats(TravelClass.SLEEPER) == 2);

        check("Total inventory decreases to 9",
                journey.getSeats().size() == 9);

        // 6. Invalid decrease must not change inventory
        int beforeAvailable =
                journey.getAvailableSeats(TravelClass.GENERAL);
        int beforeTotal = journey.getSeats().size();

        boolean invalidDecrease =
                journey.adjustSeatCapacity(TravelClass.GENERAL, -3);

        check("Insufficient available seats reject capacity decrease",
                !invalidDecrease);

        check("Rejected decrease preserves available seats",
                journey.getAvailableSeats(TravelClass.GENERAL)
                        == beforeAvailable);

        check("Rejected decrease preserves total inventory",
                journey.getSeats().size() == beforeTotal);

        // 7. Zero adjustment
        int beforeZero = journey.getSeats().size();

        check("Zero adjustment succeeds",
                journey.adjustSeatCapacity(TravelClass.AC, 0));

        check("Zero adjustment changes nothing",
                journey.getSeats().size() == beforeZero);

        // 8. Overflow protection
        int beforeMinValue = journey.getSeats().size();

        check("Integer.MIN_VALUE adjustment is rejected",
                !journey.adjustSeatCapacity(
                    TravelClass.AC, Integer.MIN_VALUE));

        check("MIN_VALUE rejection preserves inventory",
                journey.getSeats().size() == beforeMinValue);

        // 9. Null class validation
        check("Null class capacity adjustment is rejected",
                !journey.adjustSeatCapacity(null, 1));

        // 10. Returned seat list must not expose internal list
        List<Seat> copiedSeats = journey.getSeats();
        int beforeExternalRemoval = journey.getSeats().size();

        copiedSeats.clear();

        check("Clearing returned list does not clear inventory",
                journey.getSeats().size() == beforeExternalRemoval);

        // 11. Constructor validation
        boolean nullTrainRejected = false;

        try {
            new TrainJourney(null, journeyDate);
        } catch (IllegalArgumentException e) {
            nullTrainRejected = true;
        }

        check("Null train is rejected", nullTrainRejected);

        boolean nullDateRejected = false;

        try {
            new TrainJourney(train, null);
        } catch (IllegalArgumentException e) {
            nullDateRejected = true;
        }

        check("Null journey date is rejected", nullDateRejected);

        // 12. Booked seats must never be removed
        boolean bookedSeatStillExists = false;

        for (Seat seat : journey.getSeats()) {
            if (seat.getSeatNumber().equals(selected.getSeatNumber())
                    && seat.getSeatStatus() == SeatStatus.BOOKED) {
                bookedSeatStillExists = true;
                break;
            }
        }

        check("Booked seat remains in journey inventory",
                bookedSeatStillExists);

        // Summary
        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total : " + (passed + failed));

        if (failed == 0) {
            System.out.println("All TrainJourney tests passed!");
        } else {
            System.out.println("Some TrainJourney tests failed.");
        }
    }
}
