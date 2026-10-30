
package com.railway.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.railway.enums.BookingStatus;
import com.railway.enums.Gender;
import com.railway.enums.TravelClass;
import com.railway.model.GroupBooking;
import com.railway.model.Passenger;
import com.railway.model.Train;
import com.railway.model.TravellerBooking;
import com.railway.service.RailwaySystem;

public class BookingIntegrationTest {

    private static int passed = 0;
    private static int failed = 0;

    private static final int TRAIN_NUMBER = 99999;

    public static void main(String[] args) {

        System.out.println("\n========== GROUP BOOKING INTEGRATION TESTS ==========\n");

        runTest("01. Successful booking with 2 travellers",
                BookingIntegrationTest::testTwoTravellerBooking);

        runTest("02. Successful booking with 6 travellers",
                BookingIntegrationTest::testSixTravellerBooking);

        runTest("03. Reject booking with 1 traveller",
                BookingIntegrationTest::testOneTravellerRejected);

        runTest("04. Reject booking with 7 travellers",
                BookingIntegrationTest::testSevenTravellersRejected);

        runTest("05. Reject empty traveller list",
                BookingIntegrationTest::testEmptyListRejected);

        runTest("06. Reject null traveller list",
                BookingIntegrationTest::testNullListRejected);

        runTest("07. Reject null traveller entry",
                BookingIntegrationTest::testNullTravellerRejected);

        runTest("08. Reject blank traveller name",
                BookingIntegrationTest::testBlankNameRejected);

        runTest("09. Reject invalid traveller age",
                BookingIntegrationTest::testInvalidAgeRejected);

        runTest("10. Reject null traveller gender",
                BookingIntegrationTest::testNullGenderRejected);

        runTest("11. Reject insufficient seats without changing inventory",
                BookingIntegrationTest::testInsufficientSeats);

        runTest("12. Cancel one traveller successfully",
                BookingIntegrationTest::testCancelOneTraveller);

        runTest("13. Reject repeated traveller cancellation",
                BookingIntegrationTest::testRepeatedCancellation);

        runTest("14. Cancel all travellers and update group status",
                BookingIntegrationTest::testCancelAllTravellers);

        runTest("15. Reject cancellation by non-owner",
                BookingIntegrationTest::testNonOwnerCancellation);

        runTest("16. Roll back seats after simulated booking failure",
                BookingIntegrationTest::testBookingRollback);

        printSummary();
    }

    // -------------------------------------------------
    // TEST FIXTURE
    // -------------------------------------------------

    private static class Fixture {

        RailwaySystem system;
        Passenger owner;
        Train train;
        LocalDate journeyDate;

        Fixture(RailwaySystem system, int acSeats) throws Exception {

            this.system = system;

            owner = system.registerPassenger(
                    "Test Owner",
                    25,
                    Gender.MALE,
                    "9876543210",
                    "owner@test.com"
            );

            train = new Train(
                    TRAIN_NUMBER,
                    "Integration Test Express",
                    "Hyderabad",
                    "Chennai",
                    LocalTime.of(18, 30),
                    LocalTime.of(6, 45),
                    acSeats,
                    6,
                    6
            );

            system.addTrain(train);

            journeyDate = LocalDate.now().plusDays(10);
        }
    }

    private static Fixture createFixture() throws Exception {
        return new Fixture(new RailwaySystem(), 10);
    }

    private static List<TravellerBooking> createTravellers(int count) {

        List<TravellerBooking> travellers = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            travellers.add(new TravellerBooking(
                    "Test Traveller " + i,
                    25 + i,
                    i % 2 == 0 ? Gender.FEMALE : Gender.MALE,
                    null,
                    BookingStatus.CONFIRMED
            ));
        }

        return travellers;
    }

    private static GroupBooking bookGroup(
            Fixture fixture,
            List<TravellerBooking> travellers) throws Exception {

        return fixture.system.bookGroupTicket(
                fixture.owner.getPassengerId(),
                TRAIN_NUMBER,
                fixture.journeyDate,
                TravelClass.AC,
                travellers
        );
    }

    // -------------------------------------------------
    // TEST 01: TWO TRAVELLERS
    // -------------------------------------------------

    private static void testTwoTravellerBooking() throws Exception {

        Fixture f = createFixture();

        int before = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        int groupCountBefore = f.system.getAllGroupBookings().size();

        GroupBooking group = bookGroup(f, createTravellers(2));

        assertTrue(group != null, "Group booking should be created");

        assertTrue(group.getTravellers().size() == 2,
                "Group should contain 2 travellers");

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC)
                        == before - 2,
                "Exactly 2 seats should be booked"
        );

        assertTrue(
                f.system.getAllGroupBookings().size() == groupCountBefore + 1,
                "Group booking should be saved"
        );

        assertTrue(group.getBookingStatus() == BookingStatus.CONFIRMED,
                "Group status should be CONFIRMED");
    }

    // -------------------------------------------------
    // TEST 02: SIX TRAVELLERS
    // -------------------------------------------------

    private static void testSixTravellerBooking() throws Exception {

        Fixture f = createFixture();

        GroupBooking group = bookGroup(f, createTravellers(6));

        assertTrue(group.getTravellers().size() == 6,
                "All 6 travellers should be booked");

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC) == 4,
                "6 seats should be consumed from 10"
        );
    }

    // -------------------------------------------------
    // TEST 03: ONE TRAVELLER
    // -------------------------------------------------

    private static void testOneTravellerRejected() throws Exception {

        Fixture f = createFixture();

        int before = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        expectFailure(() -> bookGroup(f, createTravellers(1)));

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC) == before,
                "No seats should be consumed"
        );
    }

    // -------------------------------------------------
    // TEST 04: SEVEN TRAVELLERS
    // -------------------------------------------------

    private static void testSevenTravellersRejected() throws Exception {

        Fixture f = createFixture();

        int before = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        expectFailure(() -> bookGroup(f, createTravellers(7)));

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC) == before,
                "No seats should be consumed"
        );
    }

    // -------------------------------------------------
    // TEST 05: EMPTY LIST
    // -------------------------------------------------

    private static void testEmptyListRejected() throws Exception {

        Fixture f = createFixture();

        expectFailure(() -> bookGroup(f, new ArrayList<>()));

        assertTrue(f.system.getAllGroupBookings().isEmpty(),
                "No group booking should be saved");
    }

    // -------------------------------------------------
    // TEST 06: NULL LIST
    // -------------------------------------------------

    private static void testNullListRejected() throws Exception {

        Fixture f = createFixture();

        expectFailure(() -> bookGroup(f, null));

        assertTrue(f.system.getAllGroupBookings().isEmpty(),
                "No group booking should be saved");
    }

    // -------------------------------------------------
    // TEST 07: NULL TRAVELLER
    // -------------------------------------------------

    private static void testNullTravellerRejected() throws Exception {

        Fixture f = createFixture();

        List<TravellerBooking> travellers = createTravellers(2);
        travellers.set(1, null);

        expectFailure(() -> bookGroup(f, travellers));

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC) == 10,
                "No seats should be consumed"
        );
    }

    // -------------------------------------------------
    // TEST 08: BLANK NAME
    // -------------------------------------------------

    private static void testBlankNameRejected() throws Exception {

        expectFailure(() -> new TravellerBooking(
                "   ",
                25,
                Gender.MALE,
                null,
                BookingStatus.CONFIRMED
        ));
    }

    // -------------------------------------------------
    // TEST 09: INVALID AGE
    // -------------------------------------------------

    private static void testInvalidAgeRejected() throws Exception {

        Fixture f = createFixture();

        List<TravellerBooking> travellers = createTravellers(2);

        travellers.set(1, new TravellerBooking(
                "Invalid Age Traveller",
                121,
                Gender.MALE,
                null,
                BookingStatus.CONFIRMED
        ));

        expectFailure(() -> bookGroup(f, travellers));

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC) == 10,
                "No seats should be consumed"
        );
    }

    // -------------------------------------------------
    // TEST 10: NULL GENDER
    // -------------------------------------------------

    private static void testNullGenderRejected() throws Exception {

        expectFailure(() -> new TravellerBooking(
                "Null Gender Traveller",
                25,
                null,
                null,
                BookingStatus.CONFIRMED
        ));
    }

    // -------------------------------------------------
    // TEST 11: INSUFFICIENT SEATS
    // -------------------------------------------------

    private static void testInsufficientSeats() throws Exception {

        Fixture f = new Fixture(new RailwaySystem(), 1);

        int before = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        expectFailure(() -> bookGroup(f, createTravellers(2)));

        int after = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        assertTrue(before == 1, "Initial AC availability should be 1");

        assertTrue(after == before,
                "Failed booking must not consume any seats");

        assertTrue(f.system.getAllGroupBookings().isEmpty(),
                "Failed group booking must not be saved");
    }

    // -------------------------------------------------
    // TEST 12: CANCEL ONE TRAVELLER
    // -------------------------------------------------

    private static void testCancelOneTraveller() throws Exception {

        Fixture f = createFixture();

        GroupBooking group = bookGroup(f, createTravellers(2));

        int before = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        boolean cancelled = f.system.cancelGroupTraveller(
                f.owner.getPassengerId(),
                group.getPnr(),
                0
        );

        int after = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        assertTrue(cancelled, "Cancellation should succeed");

        assertTrue(after == before + 1,
                "Exactly one seat should be released");

        assertTrue(
                group.getTravellers().get(0).getBookingStatus()
                        == BookingStatus.CANCELLED,
                "Traveller status should become CANCELLED"
        );

        assertTrue(group.getBookingStatus() == BookingStatus.CONFIRMED,
                "Group should remain CONFIRMED");
    }

    // -------------------------------------------------
    // TEST 13: REPEATED CANCELLATION
    // -------------------------------------------------

    private static void testRepeatedCancellation() throws Exception {

        Fixture f = createFixture();

        GroupBooking group = bookGroup(f, createTravellers(2));

        String ownerId = f.owner.getPassengerId();
        String pnr = group.getPnr();

        boolean first = f.system.cancelGroupTraveller(ownerId, pnr, 0);

        int afterFirst = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        boolean second = f.system.cancelGroupTraveller(ownerId, pnr, 0);

        int afterSecond = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        assertTrue(first, "First cancellation should succeed");

        assertTrue(!second, "Repeated cancellation should be rejected");

        assertTrue(afterSecond == afterFirst,
                "Repeated cancellation must not release another seat");
    }

    // -------------------------------------------------
    // TEST 14: CANCEL ALL TRAVELLERS
    // -------------------------------------------------

    private static void testCancelAllTravellers() throws Exception {

        Fixture f = createFixture();

        GroupBooking group = bookGroup(f, createTravellers(2));

        String ownerId = f.owner.getPassengerId();
        String pnr = group.getPnr();

        boolean first = f.system.cancelGroupTraveller(ownerId, pnr, 0);
        boolean second = f.system.cancelGroupTraveller(ownerId, pnr, 1);

        assertTrue(first && second,
                "Both traveller cancellations should succeed");

        assertTrue(group.getBookingStatus() == BookingStatus.CANCELLED,
                "Group status should become CANCELLED");

        assertTrue(
                f.system.checkAvailability(
                        TRAIN_NUMBER, f.journeyDate, TravelClass.AC) == 10,
                "All seats should be released"
        );
    }

    // -------------------------------------------------
    // TEST 15: NON-OWNER CANCELLATION
    // -------------------------------------------------

    private static void testNonOwnerCancellation() throws Exception {

        Fixture f = createFixture();

        Passenger anotherPassenger = f.system.registerPassenger(
                "Another Passenger",
                26,
                Gender.FEMALE,
                "9876543211",
                "another@test.com"
        );

        GroupBooking group = bookGroup(f, createTravellers(2));

        int before = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        boolean cancelled = f.system.cancelGroupTraveller(
                anotherPassenger.getPassengerId(),
                group.getPnr(),
                0
        );

        int after = f.system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        assertTrue(!cancelled,
                "Non-owner cancellation should be rejected");

        assertTrue(before == after,
                "No seats should be released");

        assertTrue(
                group.getTravellers().get(0).getBookingStatus()
                        == BookingStatus.CONFIRMED,
                "Traveller should remain CONFIRMED"
        );
    }

    // -------------------------------------------------
    // TEST 16: ROLLBACK AFTER SIMULATED FAILURE
    // -------------------------------------------------

    private static class FailingRailwaySystem extends RailwaySystem {

        @Override
        protected void afterGroupSeatAllocated(int allocatedCount) {

            if (allocatedCount == 1) {
                throw new RuntimeException(
                        "Simulated failure after first seat allocation"
                );
            }
        }
    }

    private static void testBookingRollback() throws Exception {

        FailingRailwaySystem system = new FailingRailwaySystem();

        Fixture f = new Fixture(system, 10);

        int seatsBefore = system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        int groupsBefore = system.getAllGroupBookings().size();

        expectFailure(() -> bookGroup(f, createTravellers(2)));

        int seatsAfter = system.checkAvailability(
                TRAIN_NUMBER, f.journeyDate, TravelClass.AC);

        int groupsAfter = system.getAllGroupBookings().size();

        assertTrue(seatsAfter == seatsBefore,
                "Allocated seat should be released during rollback");

        assertTrue(groupsAfter == groupsBefore,
                "Failed group booking should not be saved");
    }

    // -------------------------------------------------
    // TEST HELPERS
    // -------------------------------------------------

    @FunctionalInterface
    private interface TestOperation {
        void execute() throws Exception;
    }

    private static void expectFailure(TestOperation operation)
            throws Exception {

        boolean exceptionThrown = false;

        try {
            operation.execute();
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown,
                "Expected an exception, but operation succeeded");
    }

    private static void assertTrue(boolean condition, String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void runTest(String testName, TestOperation operation) {

        try {
            operation.execute();

            passed++;

            System.out.printf("[PASS] %s%n", testName);

        } catch (Throwable e) {

            failed++;

            System.out.printf("[FAIL] %s%n", testName);
            System.out.printf("       Reason: %s%n", e.getMessage());
        }
    }

    private static void printSummary() {

        System.out.println("\n==================================================");
        System.out.println("          GROUP BOOKING TEST SUMMARY");
        System.out.println("==================================================");

        System.out.printf("Total scenarios : %d%n", passed + failed);
        System.out.printf("Passed          : %d%n", passed);
        System.out.printf("Failed          : %d%n", failed);

        System.out.println("==================================================");

        if (failed == 0) {
            System.out.println("ALL GROUP BOOKING TESTS PASSED!");
        } else {
            System.out.println("SOME TESTS FAILED. REVIEW THE OUTPUT.");
        }
    }
}
