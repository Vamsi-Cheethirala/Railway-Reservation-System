
package com.railway.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.railway.enums.BookingStatus;
import com.railway.enums.SeatStatus;
import com.railway.model.GroupBooking;
import com.railway.model.Seat;
import com.railway.model.Train;
import com.railway.model.TrainJourney;
import com.railway.model.TravellerBooking;

/**
 * Validates generated demonstration data.
 *
 * This class only reads existing data.
 * It must never create or modify journey inventory.
 */
public class DemoDataValidator {

    private final RailwaySystem railwaySystem;
    private final DataInitializer dataInitializer;

    private int totalChecks;
    private int passedChecks;
    private int failedChecks;

    public DemoDataValidator(
            RailwaySystem railwaySystem,
            DataInitializer dataInitializer) {

        if (railwaySystem == null) {
            throw new IllegalArgumentException(
                    "RailwaySystem cannot be null");
        }

        if (dataInitializer == null) {
            throw new IllegalArgumentException(
                    "DataInitializer cannot be null");
        }

        this.railwaySystem = railwaySystem;
        this.dataInitializer = dataInitializer;
    }

    /**
     * Prints the complete demonstration data validation report.
     */
    public void printReport() {

        totalChecks = 0;
        passedChecks = 0;
        failedChecks = 0;

        System.out.printf("%n");
        System.out.printf("================================================%n");
        System.out.printf("          DEMO DATA VALIDATION REPORT%n");
        System.out.printf("================================================%n");

        checkRouteCoverage();
        checkTrainOccupancy();
        checkGroupBookings();
        checkDuplicateSeatAssignments();

        System.out.printf("================================================%n");
        System.out.printf("             VALIDATION SUMMARY%n");
        System.out.printf("================================================%n");

        System.out.printf("Total checks  : %d%n", totalChecks);
        System.out.printf("Passed        : %d%n", passedChecks);
        System.out.printf("Failed        : %d%n", failedChecks);

        if (failedChecks == 0) {
            System.out.println(
                    "[PASS] DEMO DATA VALIDATION COMPLETED SUCCESSFULLY");
        } else {
            System.out.println(
                    "[FAIL] DEMO DATA VALIDATION FOUND ISSUES");
        }

        System.out.printf("================================================%n");
    }

    /**
     * Records the result of an individual validation check.
     */
    private void recordResult(boolean passed) {

        totalChecks++;

        if (passed) {
            passedChecks++;
        } else {
            failedChecks++;
        }
    }

    /**
     * Checks whether every ordered pair of stations
     * has a direct train route.
     */
    private void checkRouteCoverage() {

        Set<String> stations = new HashSet<>();
        Set<String> routes = new HashSet<>();

        for (Train train : railwaySystem.getAllTrains()) {

            stations.add(normalize(train.getSource()));
            stations.add(normalize(train.getDestination()));

            routes.add(routeKey(
                    train.getSource(),
                    train.getDestination()));
        }

        int expectedRoutes =
                stations.size() * (stations.size() - 1);

        int missingRoutes = 0;

        for (String source : stations) {

            for (String destination : stations) {

                if (!source.equals(destination)
                        && !routes.contains(
                                routeKey(source, destination))) {

                    missingRoutes++;

                    System.out.printf(
                            "[MISSING ROUTE] %s -> %s%n",
                            source,
                            destination);
                }
            }
        }

        boolean passed = missingRoutes == 0;

        recordResult(passed);

        System.out.printf(
                "[%s] Direct route coverage: %d/%d routes; missing: %d%n",
                passed ? "PASS" : "FAIL",
                expectedRoutes - missingRoutes,
                expectedRoutes,
                missingRoutes);
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase();
    }

    private String routeKey(String source, String destination) {

        return normalize(source) + "->" + normalize(destination);
    }

    /**
     * Checks occupancy of journeys recorded by DataInitializer.
     *
     * Uses findExistingJourney(), so missing journeys
     * are not created during validation.
     */
    private void checkTrainOccupancy() {

        Map<Integer, LocalDate> demoJourneyDates =
                dataInitializer.getDemoJourneyDates();

        int populatedTrains = 0;
        int occupancyFailures = 0;

        for (Map.Entry<Integer, LocalDate> entry
                : demoJourneyDates.entrySet()) {

            int trainNumber = entry.getKey();
            LocalDate journeyDate = entry.getValue();

            try {

                TrainJourney journey =
                        railwaySystem.findExistingJourney(
                                trainNumber,
                                journeyDate);

                if (journey == null) {

                    occupancyFailures++;

                    System.out.printf(
                            "[FAIL] Train %d on %s has no existing journey.%n",
                            trainNumber,
                            journeyDate);

                    continue;
                }

                int totalCapacity = journey.getSeats().size();
                int bookedSeats = 0;

                for (Seat seat : journey.getSeats()) {

                    if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                        bookedSeats++;
                    }
                }

                if (totalCapacity == 0) {

                    occupancyFailures++;

                    System.out.printf(
                            "[FAIL] Train %d on %s has no seats.%n",
                            trainNumber,
                            journeyDate);

                    continue;
                }

                double occupancy =
                        100.0 * bookedSeats / totalCapacity;

                populatedTrains++;

                if (occupancy < 40.0 || occupancy > 60.0) {

                    occupancyFailures++;

                    System.out.printf(
                            "[FAIL] Train %d | Date: %s | "
                            + "Booked: %d/%d | Occupancy: %.1f%%%n",
                            trainNumber,
                            journeyDate,
                            bookedSeats,
                            totalCapacity,
                            occupancy);
                }

            } catch (Exception e) {

                occupancyFailures++;

                System.out.printf(
                        "[ERROR] Occupancy check for train %d: %s%n",
                        trainNumber,
                        e.getMessage());
            }
        }

        int totalTrains = railwaySystem.getAllTrains().size();

        double percentage = totalTrains == 0
                ? 0.0
                : 100.0 * populatedTrains / totalTrains;

        System.out.printf(
                "[INFO] Trains with demo bookings: %d/%d (%.1f%%)%n",
                populatedTrains,
                totalTrains,
                percentage);

        boolean passed = occupancyFailures == 0;

        recordResult(passed);

        System.out.printf(
                "[%s] Demo occupancy validation; failures: %d%n",
                passed ? "PASS" : "FAIL",
                occupancyFailures);
    }

    /**
     * Checks group booking size, seat allocation,
     * booking status and duplicate seats within each group.
     */
    private void checkGroupBookings() {

        int groups = railwaySystem.getAllGroupBookings().size();
        int invalidGroups = 0;

        for (GroupBooking group
                : railwaySystem.getAllGroupBookings()) {

            if (group == null
                    || group.getTravellers() == null
                    || group.getTravellers().size() < 2
                    || group.getTravellers().size() > 6) {

                invalidGroups++;
                continue;
            }

            Set<String> seats = new HashSet<>();

            boolean invalidGroup = false;

            for (TravellerBooking traveller : group.getTravellers()) {

                if (traveller == null
                        || traveller.getSeat() == null
                        || traveller.getBookingStatus() == null) {

                    invalidGroup = true;
                    break;
                }

                if (traveller.getBookingStatus()
                        == BookingStatus.CONFIRMED) {

                    if (traveller.getSeat().getSeatStatus()
                            != SeatStatus.BOOKED) {

                        invalidGroup = true;
                        break;
                    }

                    if (!seats.add(
                            traveller.getSeat().getSeatNumber())) {

                        invalidGroup = true;
                        break;
                    }
                }
            }

            if (invalidGroup) {
                invalidGroups++;
            }
        }

        boolean passed = invalidGroups == 0;

        recordResult(passed);

        System.out.printf(
                "[%s] Group bookings: %d; invalid groups: %d%n",
                passed ? "PASS" : "FAIL",
                groups,
                invalidGroups);
    }

    /**
     * Checks duplicate booked seat numbers in existing
     * journey inventories only.
     *
     * Does not create journeys.
     */
    private void checkDuplicateSeatAssignments() {

        int duplicates = 0;
        int checkedJourneys = 0;
        int journeyErrors = 0;

        LocalDate today = LocalDate.now();

        for (Train train : railwaySystem.getAllTrains()) {

            for (int offset = -7; offset <= 30; offset++) {

                LocalDate date = today.plusDays(offset);

                try {

                    TrainJourney journey =
                            railwaySystem.findExistingJourney(
                                    train.getTrainNumber(),
                                    date);

                    // Missing journeys are skipped.
                    if (journey == null) {
                        continue;
                    }

                    checkedJourneys++;

                    Set<String> bookedSeats = new HashSet<>();

                    for (Seat seat : journey.getSeats()) {

                        if (seat.getSeatStatus() != SeatStatus.BOOKED) {
                            continue;
                        }

                        if (!bookedSeats.add(seat.getSeatNumber())) {

                            duplicates++;

                            System.out.printf(
                                    "[DUPLICATE SEAT] Train: %d | "
                                    + "Date: %s | Seat: %s%n",
                                    train.getTrainNumber(),
                                    date,
                                    seat.getSeatNumber());
                        }
                    }

                } catch (Exception e) {

                    journeyErrors++;

                    System.out.printf(
                            "[ERROR] Checking train %d on %s: %s%n",
                            train.getTrainNumber(),
                            date,
                            e.getMessage());
                }
            }
        }

        boolean passed =
                duplicates == 0 && journeyErrors == 0;

        recordResult(passed);

        System.out.printf(
                "[INFO] Existing journeys checked: %d%n",
                checkedJourneys);

        System.out.printf(
                "[%s] Duplicate booked seat numbers: %d; "
                + "journey errors: %d%n",
                passed ? "PASS" : "FAIL",
                duplicates,
                journeyErrors);
    }
}
