
package com.railway.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.railway.enums.BookingStatus;
import com.railway.enums.Gender;
import com.railway.enums.SeatStatus;
import com.railway.enums.TravelClass;
import com.railway.exceptions.TrainNotFoundException;
import com.railway.model.GroupBooking;
import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.model.TravellerBooking;
import com.railway.service.DataInitializer;
import com.railway.service.RailwaySystem;
import com.railway.util.PNRGenerator;
import com.railway.exceptions.BookingNotFoundException;

public class TestMain {

    private static int passed = 0;
    private static int failed = 0;

    private static final String ADMIN = "system";
    private static final String PASSWORD = "manager";
    private static final int TEST_TRAIN_NUMBER = 99999;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.printf("%s%n", "[PASS] " + name);
        } else {
            failed++;
            System.out.printf("%s%n", "[FAIL] " + name);
        }
    }

    private static boolean throwsSecurityException(Runnable operation) {
        try {
            operation.run();
            return false;
        } catch (SecurityException e) {
            return true;
        }
    }

    private static List<TravellerBooking> createTravellers() {
        List<TravellerBooking> travellers = new ArrayList<>();

        travellers.add(new TravellerBooking(
                "Admin Test Traveller One",
                25,
                Gender.MALE,
                null,
                BookingStatus.CONFIRMED));

        travellers.add(new TravellerBooking(
                "Admin Test Traveller Two",
                27,
                Gender.FEMALE,
                null,
                BookingStatus.CONFIRMED));

        return travellers;
    }

    public static void main(String[] args) throws Exception {

        RailwaySystem railwaySystem = new RailwaySystem();
        DataInitializer initializer = new DataInitializer(railwaySystem);

        Passenger owner = initializer.getPassengers().get(0);

        testPNRUniqueness();
        
        System.out.printf("\n=== ADMIN MANAGEMENT TESTS ===%n");

        // -----------------------------------------------
        // TEST 1: Admin authentication
        // -----------------------------------------------

        check("Correct credentials allow passenger viewing",
                railwaySystem.adminViewPassengers(ADMIN, PASSWORD).size()
                        == railwaySystem.getAllPassengers().size());

        check("Incorrect credentials reject passenger viewing",
                throwsSecurityException(() ->
                        railwaySystem.adminViewPassengers(
                                "wrong", "wrong")));

        check("Incorrect credentials reject individual booking viewing",
                throwsSecurityException(() ->
                        railwaySystem.adminViewIndividualBookings(
                                ADMIN, "wrong")));

        check("Incorrect credentials reject group booking viewing",
                throwsSecurityException(() ->
                        railwaySystem.adminViewGroupBookings(
                                "wrong", PASSWORD)));

        // -----------------------------------------------
        // TEST 2: Admin adds a train
        // -----------------------------------------------

        Train testTrain = new Train(
                TEST_TRAIN_NUMBER,
                "Admin Test Express",
                "Hyderabad",
                "Chennai",
                LocalTime.of(18, 0),
                LocalTime.of(6, 0),
                3, 4, 5);

        check("Admin can add a train",
                railwaySystem.adminAddTrain(
                        ADMIN, PASSWORD, testTrain));

        check("Added train can be found",
                railwaySystem.findTrain(TEST_TRAIN_NUMBER)
                        .getTrainName().equals("Admin Test Express"));

        check("Incorrect credentials cannot add a train",
                throwsSecurityException(() -> {
                    try {
                        railwaySystem.adminAddTrain(
                                "wrong", "wrong",
                                new Train(
                                        99998,
                                        "Unauthorized Train",
                                        "Hyderabad",
                                        "Chennai",
                                        LocalTime.of(10, 0),
                                        LocalTime.of(20, 0),
                                        2, 2, 2));
                    } catch (TrainNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }));

        boolean unauthorizedTrainAbsent;

        try {
            railwaySystem.findTrain(99998);
            unauthorizedTrainAbsent = false;
        } catch (TrainNotFoundException e) {
            unauthorizedTrainAbsent = true;
        }

        check("Unauthorized train was not added",
                unauthorizedTrainAbsent);

        check("Adding a duplicate train number is rejected",
                duplicateTrainRejected(railwaySystem, testTrain));

        // -----------------------------------------------
        // TEST 3: Admin views passengers and bookings
        // -----------------------------------------------

        check("Admin can view all registered passengers",
                railwaySystem.adminViewPassengers(
                        ADMIN, PASSWORD).size() == 15);

        check("Admin can view individual bookings",
                railwaySystem.adminViewIndividualBookings(
                        ADMIN, PASSWORD).size()
                        == railwaySystem.getAllBookings().size());

        check("Admin can view initially seeded group bookings", !railwaySystem.adminViewGroupBookings(ADMIN, PASSWORD).isEmpty());

        check("Admin passenger view is read-only",
                readOnlyPassengerView(railwaySystem));

        // -----------------------------------------------
        // TEST 4: Admin adjusts journey seat capacity
        // -----------------------------------------------

        LocalDate capacityDate = LocalDate.now().plusDays(12);

        int capacityBefore = railwaySystem.checkAvailability(
                TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC);

        boolean capacityIncreased =
                railwaySystem.adminAdjustSeatCapacityJourney(
                        ADMIN, PASSWORD,
                        TEST_TRAIN_NUMBER, capacityDate,
                        TravelClass.AC, 2);

        check("Admin can increase journey seat capacity",
                capacityIncreased);

        check("Capacity increase adds two available seats",
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC)
                        == capacityBefore + 2);

        boolean capacityDecreased =
                railwaySystem.adminAdjustSeatCapacityJourney(
                        ADMIN, PASSWORD,
                        TEST_TRAIN_NUMBER, capacityDate,
                        TravelClass.AC, -1);

        check("Admin can decrease journey seat capacity",
                capacityDecreased);

        check("Capacity decrease removes one available seat",
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC)
                        == capacityBefore + 1);

        int beforeUnauthorizedAdjustment =
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC);

        check("Incorrect credentials reject capacity adjustment",
                throwsSecurityException(() -> {
                    try {
                        railwaySystem.adminAdjustSeatCapacityJourney(
                                "wrong", PASSWORD,
                                TEST_TRAIN_NUMBER, capacityDate,
                                TravelClass.AC, 5);
                    } catch (TrainNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }));

        check("Unauthorized adjustment changes no seats",
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC)
                        == beforeUnauthorizedAdjustment);

        // -----------------------------------------------
        // TEST 5: Invalid seat-capacity adjustments
        // -----------------------------------------------

        int capacityBeforeInvalidChange = railwaySystem.checkAvailability(TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC);

        // Attempt to decrease capacity by an invalid amount.
        boolean invalidDecrease = railwaySystem.adminAdjustSeatCapacityJourney(ADMIN, PASSWORD, TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC, -100);

        check("Invalid capacity decrease is rejected", !invalidDecrease);

        check("Invalid decrease leaves seat availability unchanged", railwaySystem.checkAvailability(TEST_TRAIN_NUMBER, capacityDate, TravelClass.AC) == capacityBeforeInvalidChange);

        // -----------------------------------------------
        // TEST 6: Train with confirmed individual booking
        // cannot be removed.
        // -----------------------------------------------

        LocalDate individualDate = LocalDate.now().plusDays(13);

        Ticket individualTicket = railwaySystem.bookTicket(
                TEST_TRAIN_NUMBER,
                owner,
                individualDate,
                TravelClass.AC);

        check("Individual booking on the new train succeeds", individualTicket != null && individualTicket.getBookingStatus() == BookingStatus.CONFIRMED);
        
        check("Specific-date removal is blocked by a confirmed individual booking", !railwaySystem.adminRemoveTrainJourney( ADMIN, PASSWORD, TEST_TRAIN_NUMBER, individualDate));

        check("Train with confirmed individual booking cannot be removed",
                !railwaySystem.adminRemoveTrain(
                        ADMIN, PASSWORD, TEST_TRAIN_NUMBER));

        check("Incorrect credentials cannot remove a train",
                throwsSecurityException(() -> {
                    try {
                        railwaySystem.adminRemoveTrain(
                                "wrong", "wrong", TEST_TRAIN_NUMBER);
                    } catch (TrainNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }));

        int seatsBeforeCancellation = individualTicket.getJourney().getAvailableSeats(individualTicket.getTravelClass());

        check("Individual ticket can be cancelled", railwaySystem.cancelTicket(individualTicket.getPnr()));

        check("Cancellation changes ticket status to CANCELLED", individualTicket.getBookingStatus() == BookingStatus.CANCELLED);

        check("Cancellation releases exactly one seat", individualTicket.getJourney().getAvailableSeats(individualTicket.getTravelClass()) == seatsBeforeCancellation + 1);

        Ticket secondTicket = railwaySystem.bookTicket(TEST_TRAIN_NUMBER, owner, individualDate, TravelClass.AC);

        check("Second individual booking succeeds", secondTicket != null);

        check("Second ticket has a different PNR", !secondTicket.getPnr().equals(individualTicket.getPnr()));

        int seatsBeforeFirstCancellation = secondTicket.getJourney().getAvailableSeats(secondTicket.getTravelClass());

        boolean firstTicketCancelledAgain = railwaySystem.cancelTicket(individualTicket.getPnr());

        check("Repeated cancellation returns false", !firstTicketCancelledAgain);

        check("Repeated cancellation preserves availability", secondTicket.getJourney().getAvailableSeats(secondTicket.getTravelClass()) == seatsBeforeFirstCancellation);

        check("Second ticket remains confirmed", secondTicket.getBookingStatus() == BookingStatus.CONFIRMED);

        check("Second ticket seat remains booked", secondTicket.getSeat().getSeatStatus() == SeatStatus.BOOKED);

        // Clean up the second ticket so later train-removal tests still work.
        check("Second ticket can be cancelled", railwaySystem.cancelTicket(secondTicket.getPnr()));

        // -----------------------------------------------
        // TEST 7: Admin views group bookings and removal
        // protection for confirmed group bookings.
        // -----------------------------------------------

        LocalDate groupDate = LocalDate.now().plusDays(5);

        GroupBooking group = railwaySystem.bookGroupTicket(
                owner.getPassengerId(),
                TEST_TRAIN_NUMBER,
                groupDate,
                TravelClass.AC,
                createTravellers());

        check("Group booking can be created on the new train",
                group != null);

        check("Specific-date removal is blocked by a confirmed group booking", !railwaySystem.adminRemoveTrainJourney(ADMIN, PASSWORD, TEST_TRAIN_NUMBER, groupDate));

        check("Admin can view the created group booking",
                railwaySystem.adminViewGroupBookings(
                        ADMIN, PASSWORD).stream()
                        .anyMatch(b -> b.getPnr().equals(group.getPnr())));

        check("Train with a confirmed group booking cannot be removed",
                !railwaySystem.adminRemoveTrain(
                        ADMIN, PASSWORD, TEST_TRAIN_NUMBER));

        check("Owner can cancel the first group traveller",
                railwaySystem.cancelGroupTraveller(
                        owner.getPassengerId(), group.getPnr(), 0));

        
        // Repeated cancellation of the same group traveller
        int seatsAfterFirstCancellation =
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER,
                        groupDate,
                        TravelClass.AC);

        boolean secondTravellerCancellation = railwaySystem.cancelGroupTraveller(owner.getPassengerId(), group.getPnr(), 0);

        check("Repeated group traveller cancellation is rejected",
                !secondTravellerCancellation);

        check("Repeated cancellation does not change seat availability", railwaySystem.checkAvailability(TEST_TRAIN_NUMBER, groupDate, TravelClass.AC) == seatsAfterFirstCancellation);

        check("Group remains confirmed while one traveller remains",
                group.getBookingStatus() == BookingStatus.CONFIRMED);

        check("Owner can cancel the second group traveller",
                railwaySystem.cancelGroupTraveller(
                        owner.getPassengerId(), group.getPnr(), 1));

        check("Group status becomes cancelled",
                group.getBookingStatus() == BookingStatus.CANCELLED);

        
        // Verify repeated cancellation of a fully cancelled group.
        int seatsAfterGroupCancellation =
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER,
                        groupDate,
                        TravelClass.AC);

        boolean repeatedGroupCancellation =
                railwaySystem.cancelGroupTraveller(
                        owner.getPassengerId(),
                        group.getPnr(),
                        1);

        check("Repeated cancellation of a cancelled group traveller is rejected",
                !repeatedGroupCancellation);

        check("Repeated group cancellation does not restore seats twice",
                railwaySystem.checkAvailability(
                        TEST_TRAIN_NUMBER,
                        groupDate,
                        TravelClass.AC)
                        == seatsAfterGroupCancellation);

        // -----------------------------------------------
        // TEST 8: Remove one unbooked journey date
        // -----------------------------------------------

        LocalDate removableDate = LocalDate.now().plusDays(20);
        LocalDate remainingDate = LocalDate.now().plusDays(21);

        boolean journeyRemoved = railwaySystem.adminRemoveTrainJourney(ADMIN, PASSWORD, TEST_TRAIN_NUMBER, removableDate);

        check("Admin can remove an unbooked journey date", journeyRemoved);

        // The removed journey must not be recreated automatically.
        boolean removedJourneyBlocked;

        try {
            railwaySystem.getOrCreateJourney(TEST_TRAIN_NUMBER, removableDate);
            removedJourneyBlocked = false;
        } catch (TrainNotFoundException e) {
                removedJourneyBlocked = true;
        }

        check("Removed journey cannot be recreated", removedJourneyBlocked);

        // The train must remain available for other dates.
        boolean otherDateAvailable;

        try {
            railwaySystem.getOrCreateJourney(TEST_TRAIN_NUMBER, remainingDate);
            otherDateAvailable = true;
        } catch (TrainNotFoundException e) {
            otherDateAvailable = false;
        }

        check("Other journey dates remain available", otherDateAvailable);

        // -----------------------------------------------
        // TEST 9: Journey inventory after removing a train
        // -----------------------------------------------

        LocalDate consistencyDate = LocalDate.now().plusDays(22);

        // Create journey inventory before removing the train.
        boolean journeyCreated;

        try {
            railwaySystem.getOrCreateJourney(TEST_TRAIN_NUMBER, consistencyDate);

            journeyCreated = true;
        } catch (TrainNotFoundException e) {
            journeyCreated = false;
        }

        check("Journey inventory can be created before train removal", journeyCreated);

        int journeysBeforeRemoval = railwaySystem.getJourneyCountForTesting();
        
        // Remove the train.
        boolean removed = railwaySystem.adminRemoveTrain(ADMIN, PASSWORD, TEST_TRAIN_NUMBER);

        check("Train removal succeeds after confirmed bookings are cancelled", removed);

        check("Whole-train removal cleans up journey inventory", railwaySystem.getJourneyCountForTesting() < journeysBeforeRemoval);

        // Attempt to access journey inventory after removal.
        boolean bookingBlocked;

        try {
            railwaySystem.getOrCreateJourney(TEST_TRAIN_NUMBER, consistencyDate);

            bookingBlocked = false;
        } catch (TrainNotFoundException e) {
            bookingBlocked = true;
        }

        check("Removed train cannot recreate journey inventory", bookingBlocked);

        
        // -----------------------------------------------
        // TEST 10: Remove train after booking cancellation
        // -----------------------------------------------

        int cancelledBookingTrainNumber = 99997;

        Train cancelledBookingTrain = new Train(
                cancelledBookingTrainNumber,
                "Cancellation Test Express",
                "Hyderabad",
                "Bengaluru",
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                3, 4, 5);

        boolean trainAdded = railwaySystem.adminAddTrain(
                ADMIN, PASSWORD, cancelledBookingTrain);

        check("Train for cancellation test can be added",
                trainAdded);

        LocalDate cancelledBookingDate =
                LocalDate.now().plusDays(30);

        Ticket cancellationTestTicket = railwaySystem.bookTicket(
                cancelledBookingTrainNumber,
                owner,
                cancelledBookingDate,
                TravelClass.AC);

        check("Booking on cancellation test train succeeds",
                cancellationTestTicket != null
                        && cancellationTestTicket.getBookingStatus()
                                == BookingStatus.CONFIRMED);

        boolean ticketCancelled = railwaySystem.cancelTicket(
                cancellationTestTicket.getPnr());

        check("Booking is cancelled before train removal",
                ticketCancelled
                        && cancellationTestTicket.getBookingStatus()
                                == BookingStatus.CANCELLED);

        boolean cancelledTrainRemoved;

        try {
            cancelledTrainRemoved = railwaySystem.adminRemoveTrain(
                    ADMIN,
                    PASSWORD,
                    cancelledBookingTrainNumber);
        } catch (TrainNotFoundException e) {
            cancelledTrainRemoved = false;
        }

        check("Train can be removed after its booking is cancelled",
                cancelledTrainRemoved);

        boolean removedTrainNotFound;

        try {
            railwaySystem.findTrain(cancelledBookingTrainNumber);
            removedTrainNotFound = false;
        } catch (TrainNotFoundException e) {
            removedTrainNotFound = true;
        }

        check("Cancelled-booking train no longer exists",
                removedTrainNotFound);

        
        // -----------------------------------------------
        // TEST 11: Repeated individual ticket cancellation
        // -----------------------------------------------

        boolean secondCancellation =
                railwaySystem.cancelTicket(
                        individualTicket.getPnr());

        check("Repeated ticket cancellation is rejected",
                !secondCancellation);

        
        
        // -----------------------------------------------
        // EDGE-CASE TEST A: Invalid PNR cancellation
        // -----------------------------------------------

        boolean invalidPnrRejected;

        try {
            railwaySystem.cancelTicket("INVALID_PNR_12345");
            invalidPnrRejected = false;

        } catch (BookingNotFoundException e) {
            invalidPnrRejected = true;
        }

        check("Cancellation with an invalid PNR is rejected",
                invalidPnrRejected);



        // -----------------------------------------------
        // EDGE-CASE TEST B: Removing a non-existent train
        // -----------------------------------------------

        boolean missingTrainRejected;

        try {
            railwaySystem.adminRemoveTrain(
                    ADMIN, PASSWORD, 88888);

            missingTrainRejected = false;

        } catch (TrainNotFoundException e) {
            missingTrainRejected = true;
        }

        check("Removing a non-existent train is rejected",
                missingTrainRejected);


        // -----------------------------------------------
        // EDGE-CASE TEST C: Invalid credentials cannot
        // remove a non-existent train
        // -----------------------------------------------

        boolean invalidCredentialsRejected =
                throwsSecurityException(() -> {
                    try {
                        railwaySystem.adminRemoveTrain(
                                "wrong",
                                "wrong",
                                88888);

                    } catch (TrainNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        check("Invalid admin credentials are rejected before train lookup",
                invalidCredentialsRejected);

        // -----------------------------------------------
        // SUMMARY
        // -----------------------------------------------

        System.out.printf("\n========== SUMMARY ==========%n");
        System.out.printf("%s%n", "Passed: " + passed);
        System.out.printf("%s%n", "Failed: " + failed);
        System.out.printf("%s%n", "Total : " + (passed + failed));

        if (failed == 0) {
            System.out.printf("All admin management tests passed!%n");
        } else {
            System.out.printf("Some tests failed. Review the output.%n");
        }
    }

    private static boolean duplicateTrainRejected(
            RailwaySystem railwaySystem, Train train) {

        try {
            railwaySystem.adminAddTrain(
                    ADMIN, PASSWORD, train);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        } catch (TrainNotFoundException e) {
            return false;
        }
    }

    private static boolean readOnlyPassengerView(
            RailwaySystem railwaySystem) {

        try {
            railwaySystem.adminViewPassengers(
                    ADMIN, PASSWORD).clear();
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    private static void testPNRUniqueness() {

        Set<String> pnrs = new HashSet<>();

        for (int i = 0; i < 100; i++) {
                pnrs.add(PNRGenerator.generatePNR());
        }

        System.out.println(pnrs.size() == 100 ? "PASS: All PNRs are unique" : "FAIL: Duplicate PNR detected");
     }
}
