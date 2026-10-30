package com.railway.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import com.railway.enums.BookingStatus;
import com.railway.enums.Gender;
import com.railway.enums.TravelClass;
import com.railway.exceptions.BookingNotFoundException;
import com.railway.exceptions.InvalidPassengerException;
import com.railway.exceptions.SeatNotAvailableException;
import com.railway.exceptions.TrainNotFoundException;
import com.railway.model.GroupBooking;
import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.model.TravellerBooking;
import com.railway.service.DataInitializer;
import com.railway.service.DemoDataValidator;
import com.railway.service.RailwaySystem;

/**
 * Main console application for the Railway Reservation System.
 * Menus collect user input and delegate all reservation logic to RailwaySystem.
 */
public class Main {

    private static final String ADMIN_USERNAME = "system";
    private static final String ADMIN_PASSWORD = "manager";
    private static final String LINE = "======================================================================";

    private Main() {
        // Utility class: do not instantiate.
    }

    /** Initializes demo data and starts the main menu. */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            RailwaySystem railwaySystem = new RailwaySystem();
            DataInitializer dataInitializer = new DataInitializer(railwaySystem);

            DemoDataValidator validator = new DemoDataValidator(railwaySystem, dataInitializer);

            try {
                validator.printReport();
            } catch (Exception e) {
                System.out.printf("Demo data validation failed: %s%n", e.getMessage());
            }
            
            printHeader("RAILWAY RESERVATION SYSTEM");
            System.out.printf("%nDemo data loaded: %d trains, %d passengers, %d individual bookings.%n",
                    railwaySystem.getAllTrains().size(),
                    railwaySystem.getAllPassengers().size(),
                    railwaySystem.getAllBookings().size());

            runMainMenu(scanner, railwaySystem);
        } catch (TrainNotFoundException | InvalidPassengerException e) {
            System.out.printf("Application initialization failed: %s%n", e.getMessage());
        }
    }

    // ============================== MAIN MENU ==============================

    static void runMainMenu(Scanner scanner, RailwaySystem system) {
        boolean running = true;
        while (running) {
            printHeader("MAIN MENU");
            System.out.printf("1. Admin Login%n2. Passenger Menu%n3. Exit%n");
            int choice = readInt(scanner, "Choose an option: ");

            switch (choice) {
                case 1 -> adminLogin(scanner, system);
                case 2 -> passengerAccessMenu(scanner, system);
                case 3 -> {
                    System.out.printf("%nThank you for using Railway Reservation System.%n");
                    running = false;
                }
                default -> showError("Please choose 1, 2, or 3.");
            }
        }
    }

    // ============================== ADMIN MENU =============================

    private static void adminLogin(Scanner scanner, RailwaySystem system) {
        printHeader("ADMIN LOGIN");
        String username = readText(scanner, "Username: ");
        String password = readText(scanner, "Password: ");

        if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
            showError("Login failed. Invalid username or password.");
            return;
        }

        System.out.printf("%nWelcome, administrator.%n");
        runAdminMenu(scanner, system, username, password);
    }

    private static void runAdminMenu(Scanner scanner, RailwaySystem system,
                                     String username, String password) {
        boolean loggedIn = true;
        while (loggedIn) {
            printHeader("ADMIN DASHBOARD");
            System.out.printf("1. Add Train%n2. Remove Train%n3. View All Trains%n"
                    + "4. View All Passengers%n5. View Individual Bookings%n"
                    + "6. View Group Bookings%n7. Adjust Journey Seat Capacity%n"
                    + "8. View Journey Seat Availability%n9. Logout%n");

            int choice = readInt(scanner, "Choose an option: ");
            switch (choice) {
                case 1 -> addTrain(scanner, system, username, password);
                case 2 -> removeTrain(scanner, system, username, password);
                case 3 -> viewTrains(scanner, system);
                case 4 -> viewPassengers(system, username, password);
                case 5 -> viewIndividualBookings(scanner, system, username, password);
                case 6 -> viewGroupBookings(system, username, password);
                case 7 -> adjustSeatCapacity(scanner, system, username, password);
                case 8 -> viewJourneyAvailability(scanner, system);
                case 9 -> {
                    System.out.printf("Admin logged out.%n");
                    loggedIn = false;
                }
                default -> showError("Invalid menu choice.");
            }
        }
    }

    private static void addTrain(Scanner scanner, RailwaySystem system,
                                 String username, String password) {
        printHeader("ADD TRAIN");
        try {
            int number = readInt(scanner, "Train number: ");
            String name = readText(scanner, "Train name: ");
            String source = readText(scanner, "Source station: ");
            String destination = readText(scanner, "Destination station: ");
            LocalTime departure = readTime(scanner, "Departure time (HH:mm): ");
            LocalTime arrival = readTime(scanner, "Arrival time (HH:mm): ");
            int ac = readNonNegativeInt(scanner, "AC seat capacity: ");
            int sleeper = readNonNegativeInt(scanner, "Sleeper seat capacity: ");
            int general = readNonNegativeInt(scanner, "General seat capacity: ");

            Train train = new Train(number, name, source, destination,
                    departure, arrival, ac, sleeper, general);
            system.adminAddTrain(username, password, train);
            showSuccess("Train added and journey inventory initialized.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (TrainNotFoundException e) {
            showError(e.getMessage());
        }
    }

    private static void removeTrain(Scanner scanner, RailwaySystem system, String username, String password) {
        System.out.println("\n========== REMOVE TRAIN ==========");
        System.out.println("1. Remove train for ALL dates");
        System.out.println("2. Remove train for a SPECIFIC date");
        System.out.println("0. Cancel");

        int choice = readInt(scanner, "Choose an option: ");

        if (choice == 0) {
            System.out.println("[INFO] Train removal cancelled.");
            return;
        }

        if (choice != 1 && choice != 2) {
            System.out.println("[ERROR] Invalid option.");
            return;
        }

        int trainNumber = readInt(scanner, "Enter train number: ");

        try {
            if (choice == 1) {
            // No date should be requested for all-dates removal.
            boolean removed = system.adminRemoveTrain(
                    username, password, trainNumber);

            if (removed) {
                System.out.println(
                        "[SUCCESS] Train removed for all dates.");
            } else {
                System.out.println(
                        "[ERROR] Train has confirmed bookings and cannot be removed.");
            }

        } else if (choice == 2) {
            LocalDate journeyDate = readDate(
                    scanner, "Enter journey date (yyyy-MM-dd): ");

            boolean removed = system.adminRemoveTrainJourney(
                    username, password, trainNumber, journeyDate);

            if (removed) {
                System.out.printf(
                        "[SUCCESS] Train %d removed for %s only.%n",
                        trainNumber, journeyDate);
            } else {
                System.out.println(
                        "[ERROR] Journey has confirmed bookings or was already removed.");
            }
        }

        } catch (TrainNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void viewTrains(Scanner scanner, RailwaySystem system) {
        printHeader("REGISTERED TRAINS");
        List<Train> trains = system.getAllTrains();

        if (trains.isEmpty()) {
            System.out.printf("No trains are currently registered.%n");
            return;
        }

        System.out.printf("%-5s %-12s %-28s %-22s %-22s%n",
                "No.", "TRAIN NO.", "TRAIN NAME", "SOURCE", "DESTINATION");
        System.out.printf("%s%n", "-".repeat(94));

        for (int i = 0; i < trains.size(); i++) {
            Train train = trains.get(i);
            System.out.printf("%-5d %-12d %-28.28s %-22.22s %-22.22s%n",
                    i + 1, train.getTrainNumber(), train.getTrainName(),
                    train.getSource(), train.getDestination());
        }

        int selected = readInt(scanner,
                "Enter train list number to view details (0 to return): ");
        if (selected == 0) {
            return;
        }
        if (selected < 1 || selected > trains.size()) {
            showError("Invalid train list number.");
            return;
        }

        trains.get(selected - 1).displayTrainDetails();
    }

    private static void viewPassengers(RailwaySystem system, String username, String password) {
        printHeader("REGISTERED PASSENGERS");
        try {
            List<Passenger> passengers = system.adminViewPassengers(username, password);
            if (passengers.isEmpty()) {
                System.out.printf("No registered passengers.%n");
                return;
            }
            System.out.printf("%-5s %-14s %-22s %-5s %-10s %-28s %-15s%n",
                    "No.", "PASSENGER ID", "NAME", "AGE", "GENDER", "EMAIL", "PHONE");
            System.out.printf("%s%n", "-".repeat(106));
            for (int i = 0; i < passengers.size(); i++) {
                Passenger passenger = passengers.get(i);
                System.out.printf("%-5d %-14s %-22.22s %-5d %-10s %-28.28s %-15s%n",
                        i + 1, passenger.getPassengerId(), passenger.getName(), passenger.getAge(),
                        passenger.getGender(), passenger.getEmail(), maskPhone(passenger.getPhoneNumber()));
            }
        } catch (SecurityException e) {
            showError(e.getMessage());
        }
    }

    private static void viewIndividualBookings(Scanner scanner, RailwaySystem system,
                                               String username, String password) {
        printHeader("INDIVIDUAL BOOKINGS BY PASSENGER");
        try {
            List<Passenger> passengers = system.adminViewPassengers(username, password);
            List<Ticket> allTickets = system.adminViewIndividualBookings(username, password);

            if (passengers.isEmpty()) {
                System.out.printf("No registered passengers.%n");
                return;
            }

            System.out.printf("%-5s %-14s %-24s %-16s%n",
                    "No.", "PASSENGER ID", "PASSENGER NAME", "BOOKING COUNT");
            System.out.printf("%s%n", "-".repeat(64));

            for (int i = 0; i < passengers.size(); i++) {
                Passenger passenger = passengers.get(i);
                long count = allTickets.stream()
                        .filter(ticket -> ticket.getPassenger().getPassengerId()
                                .equals(passenger.getPassengerId()))
                        .count();
                System.out.printf("%-5d %-14s %-24.24s %-16d%n",
                        i + 1, passenger.getPassengerId(), passenger.getName(), count);
            }

            int selectedPassenger = readInt(scanner,
                    "Select passenger number to view bookings (0 to return): ");
            if (selectedPassenger == 0) {
                return;
            }
            if (selectedPassenger < 1 || selectedPassenger > passengers.size()) {
                showError("Invalid passenger number.");
                return;
            }

            Passenger selected = passengers.get(selectedPassenger - 1);
            List<Ticket> passengerTickets = allTickets.stream()
                    .filter(ticket -> ticket.getPassenger().getPassengerId()
                            .equals(selected.getPassengerId()))
                    .toList();

            printHeader("BOOKINGS FOR " + selected.getName().toUpperCase()
                    + " (" + selected.getPassengerId() + ")");

            if (passengerTickets.isEmpty()) {
                System.out.printf("This passenger has no individual bookings.%n");
                return;
            }

            Map<LocalDate, List<Ticket>> ticketsByDate = new LinkedHashMap<>();
            passengerTickets.stream()
                    .sorted((a, b) -> a.getTravelDate().compareTo(b.getTravelDate()))
                    .forEach(ticket -> ticketsByDate
                            .computeIfAbsent(ticket.getTravelDate(), key -> new ArrayList<>())
                            .add(ticket));

            System.out.printf("Total individual bookings: %d%n", passengerTickets.size());
            System.out.printf("%n%-5s %-15s %-16s%n", "No.", "JOURNEY DATE", "BOOKINGS");
            System.out.printf("%s%n", "-".repeat(40));

            List<LocalDate> dates = new ArrayList<>(ticketsByDate.keySet());
            for (int i = 0; i < dates.size(); i++) {
                LocalDate date = dates.get(i);
                System.out.printf("%-5d %-15s %-16d%n",
                        i + 1, date, ticketsByDate.get(date).size());
            }

            int selectedDate = readInt(scanner, "Select journey date number (0 to return): ");
            if (selectedDate == 0) {
                return;
            }
            if (selectedDate < 1 || selectedDate > dates.size()) {
                showError("Invalid journey date number.");
                return;
            }

            LocalDate date = dates.get(selectedDate - 1);
            List<Ticket> selectedDateTickets = ticketsByDate.get(date);
            printHeader("BOOKINGS ON " + date);
            for (Ticket ticket : selectedDateTickets) {
                ticket.displayTicket();
            }
        } catch (SecurityException e) {
            showError(e.getMessage());
        }
    }

    private static void viewGroupBookings(RailwaySystem system,
                                          String username, String password) {
        printHeader("GROUP BOOKINGS");
        try {
            List<GroupBooking> bookings = system.adminViewGroupBookings(username, password);
            if (bookings.isEmpty()) {
                System.out.printf("No group bookings found.%n");
                return;
            }
            for (GroupBooking booking : bookings) {
                displayGroupBooking(booking);
            }
        } catch (SecurityException e) {
            showError(e.getMessage());
        }
    }

    private static void adjustSeatCapacity(Scanner scanner, RailwaySystem system,
                                           String username, String password) {
        printHeader("ADJUST JOURNEY SEAT CAPACITY");
        LocalDate date = readDate(scanner, "Journey date (yyyy-MM-dd): ");
        List<Train> trains = system.getAllTrains();

        if (trains.isEmpty()) {
            System.out.printf("No trains are currently registered.%n");
            return;
        }

        System.out.printf("%-5s %-12s %-25s %-18s %-18s %8s %8s %8s%n",
                "No.", "TRAIN NO.", "TRAIN NAME", "SOURCE", "DESTINATION",
                "AC", "SLEEPER", "GENERAL");
        System.out.printf("%s%n", "-".repeat(112));

        try {
            for (int i = 0; i < trains.size(); i++) {
                Train train = trains.get(i);
                System.out.printf("%-5d %-12d %-25.25s %-18.18s %-18.18s %8d %8d %8d%n",
                        i + 1, train.getTrainNumber(), train.getTrainName(),
                        train.getSource(), train.getDestination(),
                        system.checkAvailability(train.getTrainNumber(), date, TravelClass.AC),
                        system.checkAvailability(train.getTrainNumber(), date, TravelClass.SLEEPER),
                        system.checkAvailability(train.getTrainNumber(), date, TravelClass.GENERAL));
            }

            int selected = readInt(scanner, "Select train number from list: ");
            if (selected < 1 || selected > trains.size()) {
                showError("Invalid train list number.");
                return;
            }

            Train train = trains.get(selected - 1);
            TravelClass travelClass = readTravelClass(scanner);
            int change = readInt(scanner, "Seat capacity change (e.g. 5 or -2): ");

            boolean changed = system.adminAdjustSeatCapacityJourney(
                    username, password, train.getTrainNumber(), date, travelClass, change);
            if (changed) {
                showSuccess("Seat capacity updated.");
                System.out.printf("Updated availability for train %d on %s:%n",
                        train.getTrainNumber(), date);
                for (TravelClass type : TravelClass.values()) {
                    System.out.printf("%-12s %d%n", type,
                            system.checkAvailability(train.getTrainNumber(), date, type));
                }
            } else {
                showError("Capacity change rejected. Check the requested change and booked seats.");
            }
        } catch (TrainNotFoundException | IllegalArgumentException | SecurityException e) {
            showError(e.getMessage());
        }
    }

    private static void viewJourneyAvailability(Scanner scanner, RailwaySystem system) {
        int trainNumber = readInt(scanner, "Train number: ");
        LocalDate date = readDate(scanner, "Journey date (yyyy-MM-dd): ");
        try {
            System.out.printf("%n%-12s %12s%n", "CLASS", "AVAILABLE");
            System.out.printf("%s%n", "-".repeat(26));
            for (TravelClass travelClass : TravelClass.values()) {
                System.out.printf("%-12s %12d%n", travelClass,
                        system.checkAvailability(trainNumber, date, travelClass));
            }
        } catch (TrainNotFoundException | IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ============================ PASSENGER ACCESS ==========================

    private static void passengerAccessMenu(Scanner scanner, RailwaySystem system) {
        boolean back = false;
        while (!back) {
            printHeader("PASSENGER ACCESS");
            System.out.printf("1. Existing Passenger Login%n2. Register New Passenger%n3. Back%n");
            int choice = readInt(scanner, "Choose an option: ");
            switch (choice) {
                case 1 -> {
                    Passenger passenger = findExistingPassenger(scanner, system);
                    if (passenger != null) {
                        runPassengerMenu(scanner, system, passenger);
                    }
                }
                case 2 -> {
                    Passenger passenger = registerPassenger(scanner, system);
                    if (passenger != null) {
                        showSuccess("Registration complete. Your Passenger ID is "
                                + passenger.getPassengerId());
                        runPassengerMenu(scanner, system, passenger);
                    }
                }
                case 3 -> back = true;
                default -> showError("Invalid menu choice.");
            }
        }
    }

    private static Passenger findExistingPassenger(Scanner scanner, RailwaySystem system) {
        printHeader("FIND EXISTING PASSENGER");
        System.out.printf("1. Passenger ID%n2. Phone number%n3. Email%n");
        int choice = readInt(scanner, "Search by: ");
        String value = readText(scanner, "Enter search value: ");

        Passenger passenger = switch (choice) {
            case 1 -> system.findPassengerById(value);
            case 2 -> system.findPassengerBYPhoneNumber(value);
            case 3 -> system.findPassengerByEmail(value);
            default -> null;
        };

        if (passenger == null) {
            showError("Passenger not found. Check the details or register first.");
        } else {
            showSuccess("Passenger found: " + passenger.getName()
                    + " (" + passenger.getPassengerId() + ")");
        }
        return passenger;
    }

    private static Passenger registerPassenger(Scanner scanner, RailwaySystem system) {
        printHeader("PASSENGER REGISTRATION");
        try {
            String name = readText(scanner, "Full name: ");
            int age = readInt(scanner, "Age: ");
            Gender gender = readEnum(scanner, "Gender (MALE, FEMALE, OTHER): ", Gender.class);
            String phone = readText(scanner, "Phone number: ");
            String email = readText(scanner, "Email: ");
            return system.registerPassenger(name, age, gender, phone, email);
        } catch (InvalidPassengerException | IllegalArgumentException e) {
            showError(e.getMessage());
            return null;
        }
    }

    // ============================ PASSENGER MENU ===========================

    private static void runPassengerMenu(Scanner scanner, RailwaySystem system,
                                         Passenger passenger) {
        boolean active = true;
        while (active) {
            printHeader("PASSENGER MENU");
            System.out.printf("Signed in: %s (%s)%n%n", passenger.getName(), passenger.getPassengerId());
            System.out.printf("1. Search Trains%n2. Check Journey Availability%n"
                    + "3. Book Individual Ticket%n4. View/Cancel Individual Ticket%n"
                    + "5. Book Group Ticket%n6. View My Group Bookings%n"
                    + "7. Cancel Group Traveller%n8. Back to Main Menu%n");
            int choice = readInt(scanner, "Choose an option: ");
            switch (choice) {
                case 1 -> searchTrains(scanner, system);
                case 2 -> checkAvailability(scanner, system);
                case 3 -> bookIndividualTicket(scanner, system, passenger);
                case 4 -> manageIndividualTicket(scanner, system, passenger);
                case 5 -> bookGroupTicket(scanner, system, passenger);
                case 6 -> viewMyGroupBookings(system, passenger);
                case 7 -> cancelGroupTraveller(scanner, system, passenger);
                case 8 -> active = false;
                default -> showError("Invalid menu choice.");
            }
        }
    }

    private static void searchTrains(Scanner scanner, RailwaySystem system) {
        
        List<Train> trains = system.getAllTrains();
        
        if (trains.isEmpty()) {
            System.out.printf("No trains available.%n");
            return;
        }
        
        //Collect unique sources and destinations.
        Set<String> sources = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> destinations = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (Train train : trains) {
            sources.add(train.getSource());
            destinations.add(train.getDestination());
        }

        printHeader("AVAILABLE ROUTES");

        System.out.printf("%-5s %-25s %n", "No.", "Source");
        System.out.printf("%-5s %-25s %n", "---", "-------------------------");

        int index = 1;

        for (String source : sources) {
            System.out.printf("%-5d %-25s%n", index++, source);
        }

        System.out.println();
        System.out.printf("%-5s %-25s %n", "No.", "Destination");
        System.out.printf("%-5s %-25s %n", "---", "-------------------------");

        index = 1;

        for (String destination : destinations) {
            System.out.printf("%-5d %s%n", index++, destination);
        }

        System.out.printf("%nEnter source: ");
        String source = scanner.nextLine().trim();

        System.out.printf("Enter destination: ");
        String destination = scanner.nextLine().trim();

        if (source.isBlank() || destination.isBlank()) {
            System.out.printf("[ERROR] Source and destination are required.%n");
            return;
        }

        List<Train> matchingTrains = system.searchTrain(source, destination);

        if (matchingTrains.isEmpty()) {
            System.out.printf("%nNo trains found from %s to %s.%n", source, destination);
            return;
        }

        printHeader("TRAINS FROM " + source + " TO " + destination);

        for (Train train : matchingTrains) {
            train.displayTrainDetails();
        }

        System.out.printf("%nTotal trains found: %d%n", matchingTrains.size());

    }

    private static void checkAvailability(Scanner scanner, RailwaySystem system) {
        int trainNumber = readInt(scanner, "Train number: ");
        LocalDate date = readDate(scanner, "Journey date (yyyy-MM-dd): ");
        try {
            System.out.printf("%n%-12s %12s%n", "CLASS", "AVAILABLE SEATS");
            System.out.printf("%s%n", "-".repeat(28));
            for (TravelClass travelClass : TravelClass.values()) {
                System.out.printf("%-12s %12d%n", travelClass,
                        system.checkAvailability(trainNumber, date, travelClass));
            }
        } catch (TrainNotFoundException | IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private static void bookIndividualTicket(Scanner scanner, RailwaySystem system,
                                             Passenger passenger) {
        printHeader("BOOK INDIVIDUAL TICKET");
        int trainNumber = readInt(scanner, "Train number: ");
        LocalDate date = readDate(scanner, "Journey date (yyyy-MM-dd): ");
        TravelClass travelClass = readTravelClass(scanner);
        try {
            Ticket ticket = system.bookTicket(trainNumber, passenger, date, travelClass);
            showSuccess("Ticket booked successfully.");
            ticket.displayTicket();
        } catch (TrainNotFoundException | SeatNotAvailableException | InvalidPassengerException e) {
            showError(e.getMessage());
        }
    }

    private static void manageIndividualTicket(Scanner scanner, RailwaySystem system,
                                               Passenger passenger) {
        String pnr = readText(scanner, "Individual ticket PNR: ");
        try {
            Ticket ticket = system.findTicket(pnr);
            if (!ticket.getPassenger().getPassengerId().equals(passenger.getPassengerId())) {
                showError("No individual booking found for this passenger.");
                return;
            }
            ticket.displayTicket();
            System.out.printf("1. Cancel ticket%n2. Keep ticket%n");
            if (readInt(scanner, "Choose an option: ") == 1) {
                if (system.cancelTicket(pnr)) {
                    showSuccess("Ticket cancelled and seat released.");
                } else {
                    showError("Ticket was already cancelled.");
                }
            }
        } catch (BookingNotFoundException e) {
            showError(e.getMessage());
        }
    }

    private static void bookGroupTicket(Scanner scanner, RailwaySystem system,
                                        Passenger owner) {
        printHeader("GROUP BOOKING");
        int trainNumber = readInt(scanner, "Train number: ");
        LocalDate date = readDate(scanner, "Journey date (yyyy-MM-dd): ");
        TravelClass travelClass = readTravelClass(scanner);
        int count = readInt(scanner, "Number of travellers (2-6): ");
        if (count < 2 || count > 6) {
            showError("A group booking must contain 2 to 6 travellers.");
            return;
        }

        List<TravellerBooking> travellers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            System.out.printf("%nTraveller %d of %d%n", i, count);
            String name = readText(scanner, "Full name: ");
            int age = readInt(scanner, "Age: ");
            Gender gender = readEnum(scanner, "Gender (MALE, FEMALE, OTHER): ", Gender.class);
            travellers.add(new TravellerBooking(name, age, gender, null, BookingStatus.CONFIRMED));
        }

        try {
            GroupBooking booking = system.bookGroupTicket(
                    owner.getPassengerId(), trainNumber, date, travelClass, travellers);
            showSuccess("Group booking confirmed. Keep this PNR for cancellations.");
            displayGroupBooking(booking);
        } catch (InvalidPassengerException | TrainNotFoundException
                 | SeatNotAvailableException | IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private static void viewMyGroupBookings(RailwaySystem system, Passenger passenger) {
        printHeader("MY GROUP BOOKINGS");
        boolean found = false;
        for (GroupBooking booking : system.getAllGroupBookings()) {
            if (booking.getBookingOwner().getPassengerId().equals(passenger.getPassengerId())) {
                displayGroupBooking(booking);
                found = true;
            }
        }
        if (!found) {
            System.out.printf("No group bookings found for your account.%n");
        }
    }

    private static void cancelGroupTraveller(Scanner scanner, RailwaySystem system,
                                             Passenger passenger) {
        printHeader("CANCEL GROUP TRAVELLER");
        String pnr = readText(scanner, "Group PNR: ");
        GroupBooking booking = null;
        for (GroupBooking candidate : system.getAllGroupBookings()) {
            if (candidate.getPnr().equals(pnr)) {
                booking = candidate;
                break;
            }
        }
        if (booking == null || !booking.getBookingOwner().getPassengerId()
                .equals(passenger.getPassengerId())) {
            showError("Group booking not found for this passenger.");
            return;
        }

        displayGroupBooking(booking);
        int travellerNumber = readInt(scanner, "Traveller number to cancel (1-based): ");
        boolean cancelled = system.cancelGroupTraveller(
                passenger.getPassengerId(), pnr, travellerNumber - 1);
        if (cancelled) {
            showSuccess("Traveller cancelled; only that traveller's seat was released.");
        } else {
            showError("Cancellation was rejected. Check the traveller number and status.");
        }
        displayGroupBooking(booking);
    }

    // ================================ DISPLAY ==============================

    private static void displayGroupBooking(GroupBooking booking) {
        System.out.printf("%n%s%n", "-".repeat(68));
        System.out.printf("GROUP PNR       : %s%n", booking.getPnr());
        System.out.printf("OWNER           : %s (%s)%n",
                booking.getBookingOwner().getName(), booking.getBookingOwner().getPassengerId());
        System.out.printf("TRAIN           : %d - %s%n",
                booking.getTrain().getTrainNumber(), booking.getTrain().getTrainName());
        System.out.printf("ROUTE           : %s -> %s%n",
                booking.getTrain().getSource(), booking.getTrain().getDestination());
        System.out.printf("JOURNEY DATE    : %s%n", booking.getTravelDate());
        System.out.printf("CLASS           : %s%n", booking.getTravelClass());
        System.out.printf("GROUP STATUS    : %s%n", booking.getBookingStatus());
        System.out.printf("%n%-5s %-24s %-6s %-10s %-12s%n",
                "NO.", "TRAVELLER", "AGE", "SEAT", "STATUS");
        System.out.printf("%s%n", "-".repeat(62));
        for (int i = 0; i < booking.getTravellers().size(); i++) {
            TravellerBooking traveller = booking.getTravellers().get(i);
            String seatNumber = traveller.getSeat() == null
                    ? "-" : traveller.getSeat().getSeatNumber();
            System.out.printf("%-5d %-24.24s %-6d %-10s %-12s%n",
                    i + 1, traveller.getName(), traveller.getAge(),
                    seatNumber, traveller.getBookingStatus());
        }
        System.out.printf("%s%n", "-".repeat(68));
    }

    // ============================== INPUT HELPERS ==========================

    static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                showError("Enter a valid whole number.");
            }
        }
    }

    static int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value >= 0) {
                return value;
            }
            showError("Value cannot be negative.");
        }
    }

    static String readText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            showError("Input cannot be blank.");
        }
    }

    static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            String value = readText(scanner, prompt);
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                showError("Use the date format yyyy-MM-dd, for example 2026-11-15.");
            }
        }
    }

    static LocalTime readTime(Scanner scanner, String prompt) {
        while (true) {
            String value = readText(scanner, prompt);
            try {
                return LocalTime.parse(value);
            } catch (DateTimeParseException e) {
                showError("Use 24-hour time in HH:mm format, for example 18:45.");
            }
        }
    }

    private static TravelClass readTravelClass(Scanner scanner) {
        return readEnum(scanner, "Travel class (AC, SLEEPER, GENERAL): ", TravelClass.class);
    }

    static <T extends Enum<T>> T readEnum(
            Scanner scanner, String prompt, Class<T> enumType) {
        while (true) {
            String value = readText(scanner, prompt).toUpperCase();
            try {
                return Enum.valueOf(enumType, value);
            } catch (IllegalArgumentException e) {
                showError("Invalid choice. Valid options: "
                        + String.join(", ", enumConstants(enumType)));
            }
        }
    }

    private static <T extends Enum<T>> List<String> enumConstants(Class<T> enumType) {
        List<String> values = new ArrayList<>();
        for (T value : enumType.getEnumConstants()) {
            values.add(value.name());
        }
        return values;
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() <= 4) {
            return "****";
        }
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }

    private static void printHeader(String title) {
        System.out.printf("%n%s%n  %s%n%s%n", LINE, title, LINE);
    }

    private static void showSuccess(String message) {
        System.out.printf("[SUCCESS] %s%n", message);
    }

    private static void showError(String message) {
        System.out.printf("[ERROR] %s%n", message == null ? "Operation failed." : message);
    }
}
