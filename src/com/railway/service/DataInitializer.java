package com.railway.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.railway.enums.BookingStatus;
import com.railway.enums.Gender;
import com.railway.enums.TravelClass;
import com.railway.exceptions.InvalidPassengerException;
import com.railway.exceptions.SeatNotAvailableException;
import com.railway.exceptions.TrainNotFoundException;
import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.model.TravellerBooking;

/** Seeds demonstration trains, passengers, journey inventory, and sample tickets. */
public class DataInitializer {

    private List<Passenger> passengers;
    private final Map<Integer, LocalDate> demoJourneyDates = new HashMap<>();

    
    public DataInitializer(RailwaySystem railwaySystem) throws TrainNotFoundException, InvalidPassengerException{
        
        Train train1 = new Train(12728, "Godavari SF Express", "Hyderabad", "Visakhapatnam", LocalTime.of(17, 05), LocalTime.of(05, 55), 30, 45, 35);

        Train train2 = new Train(12760, "Charminar SF Express", "Hyderabad", "Chennai", LocalTime.of(18, 0), LocalTime.of(8, 0), 22, 95, 20);

        Train train3 = new Train(12734, "Narayanadri SF Express", "Lingampalli", "Tirupati", LocalTime.of(17, 30), LocalTime.of(5, 55), 24, 140, 10);

        Train train4 = new Train(12714, "Satavahana SF Express", "Secunderabad Jn", "Vijayawada Jn", LocalTime.of(16, 25), LocalTime.of(22, 20), 18, 75, 45);

        Train train5 = new Train(17202, "Golconda Express", "Secunderabad Jn", "Guntur Jn", LocalTime.of(12, 30), LocalTime.of(21, 15), 19, 210, 60);

        Train train6 = new Train(20701, "Tirupati Vande Bharat", "Secunderabad Jn", "Tirupati", LocalTime.of(6, 10), LocalTime.of(14, 30), 8, 45, 5);

        Train train7 = new Train(12723, "Telangana Express", "Hyderabad", "New Delhi", LocalTime.of(6, 0), LocalTime.of(9, 5), 22, 110, 25);

        Train train8 = new Train(12702, "Hussain Sagar Express", "Hyderabad", "Mumbai CSMT", LocalTime.of(14, 50), LocalTime.of(4, 55), 20, 85, 30);

        Train train9 = new Train(17058, "Devagiri Express", "Secunderabad Jn", "Mumbai CSMT", LocalTime.of(13, 25), LocalTime.of(7, 10), 22, 130, 40);

        Train train10 = new Train(12710, "Simhapuri SF Express", "Secunderabad Jn", "Gudur Jn", LocalTime.of(23, 5), LocalTime.of(8, 20), 24, 160, 15);

        Train train11 = new Train(12764, "Padmavathi SF Express", "Secunderabad Jn", "Tirupati", LocalTime.of(18, 40), LocalTime.of(6, 55), 24, 90, 12);

        Train train12 = new Train(17406, "Krishna Express", "Adilabad", "Tirupati", LocalTime.of(20, 45), LocalTime.of(21, 40), 21, 240, 50);

        Train train13 = new Train(12740, "Garibrath Express", "Secunderabad Jn", "Visakhapatnam", LocalTime.of(20, 30), LocalTime.of(7, 40), 18, 180, 0);

        Train train14 = new Train(17064, "Ajanta Express", "Secunderabad Jn", "Manmad Jn", LocalTime.of(18, 50), LocalTime.of(7, 10), 21, 105, 35);

        Train train15 = new Train(12797, "Venkatadri SF Express", "Hyderabad", "Chittoor", LocalTime.of(20, 5), LocalTime.of(8, 55), 24, 115, 18);

        Train train16 = new Train(12727, "Godavari SF Express", "Visakhapatnam", "Hyderabad", LocalTime.of(17, 20), LocalTime.of(6, 15), 24, 120, 15);

        Train train17 = new Train(12759, "Charminar SF Express", "Chennai", "Hyderabad", LocalTime.of(17, 30), LocalTime.of(8, 10), 22, 95, 20);

        Train train18 = new Train(12733, "Narayanadri SF Express", "Tirupati", "Lingampalli", LocalTime.of(18, 20), LocalTime.of(6, 55), 24, 140, 10);

        Train train19 = new Train(12713, "Satavahana SF Express", "Vijayawada Jn", "Secunderabad Jn", LocalTime.of(6, 25), LocalTime.of(12, 40), 18, 75, 45);

        Train train20 = new Train(17201, "Golconda Express", "Guntur Jn", "Secunderabad Jn", LocalTime.of(6, 0), LocalTime.of(14, 5), 19, 210, 60);

        Train train21 = new Train(20702, "Secunderabad Vande Bharat", "Tirupati", "Secunderabad Jn", LocalTime.of(15, 15), LocalTime.of(23, 45), 8, 45, 5);

        Train train22 = new Train(12724, "Telangana Express", "New Delhi", "Hyderabad", LocalTime.of(16, 0), LocalTime.of(18, 40), 22, 110, 25);

        Train train23 = new Train(12701, "Hussain Sagar Express", "Mumbai CSMT", "Hyderabad", LocalTime.of(21, 50), LocalTime.of(12, 10), 20, 85, 30);

        Train train24 = new Train(17057, "Devagiri Express", "Mumbai CSMT", "Secunderabad Jn", LocalTime.of(21, 30), LocalTime.of(14, 45), 22, 130, 40);

        Train train25 = new Train(12709, "Simhapuri SF Express", "Gudur Jn", "Secunderabad Jn", LocalTime.of(18, 40), LocalTime.of(4, 35), 24, 160, 15);

        Train train26 = new Train(12763, "Padmavathi SF Express", "Tirupati", "Secunderabad Jn", LocalTime.of(16, 55), LocalTime.of(5, 45), 24, 90, 12);

        Train train27 = new Train(17405, "Krishna Express", "Tirupati", "Adilabad", LocalTime.of(5, 45), LocalTime.of(6, 15), 21, 240, 50);

        Train train28 = new Train(12739, "Garibrath Express", "Visakhapatnam", "Secunderabad Jn", LocalTime.of(20, 40), LocalTime.of(8, 10), 18, 180, 0);

        Train train29 = new Train(17063, "Ajanta Express", "Manmad Jn", "Secunderabad Jn", LocalTime.of(20, 5), LocalTime.of(8, 50), 21, 105, 35);

        Train train30 = new Train(12798, "Venkatadri SF Express", "Chittoor", "Hyderabad", LocalTime.of(18, 30), LocalTime.of(6, 20), 24, 115, 18);

        railwaySystem.addTrain(train1);
        railwaySystem.addTrain(train2);
        railwaySystem.addTrain(train3);
        railwaySystem.addTrain(train4);
        railwaySystem.addTrain(train5);
        railwaySystem.addTrain(train6);
        railwaySystem.addTrain(train7);
        railwaySystem.addTrain(train8);
        railwaySystem.addTrain(train9);
        railwaySystem.addTrain(train10);
        railwaySystem.addTrain(train11);
        railwaySystem.addTrain(train12);
        railwaySystem.addTrain(train13);
        railwaySystem.addTrain(train14);
        railwaySystem.addTrain(train15);
        railwaySystem.addTrain(train16);
        railwaySystem.addTrain(train17);
        railwaySystem.addTrain(train18);
        railwaySystem.addTrain(train19);
        railwaySystem.addTrain(train20);
        railwaySystem.addTrain(train21);
        railwaySystem.addTrain(train22);
        railwaySystem.addTrain(train23);
        railwaySystem.addTrain(train24);
        railwaySystem.addTrain(train25);
        railwaySystem.addTrain(train26);
        railwaySystem.addTrain(train27);
        railwaySystem.addTrain(train28);
        railwaySystem.addTrain(train29);
        railwaySystem.addTrain(train30);
        
                // Complete direct route coverage between every pair
        // of distinct stations in the existing train network.
        Set<String> stations = new HashSet<>();

        for (Train train : railwaySystem.getAllTrains()) {
            stations.add(train.getSource());
            stations.add(train.getDestination());
        }

        Set<String> existingRoutes = new HashSet<>();

        for (Train train : railwaySystem.getAllTrains()) {
            existingRoutes.add(
                    routeKey(train.getSource(), train.getDestination()));
        }

        Random routeRandom = new Random();
        int nextTrainNumber = 30001;
        int addedRouteTrains = 0;

        for (String source : stations) {
            for (String destination : stations) {

                if (source.equalsIgnoreCase(destination)) {
                    continue;
                }

                String key = routeKey(source, destination);

                if (existingRoutes.contains(key)) {
                    continue;
                }

                // Generate a unique train number.
                while (trainNumberExists(
                        railwaySystem, nextTrainNumber)) {
                    nextTrainNumber++;
                }

                int departureHour = routeRandom.nextInt(24);
                LocalTime departure =
                        LocalTime.of(departureHour, 0);
                LocalTime arrival =
                        LocalTime.of((departureHour + 8) % 24, 0);

                Train generatedTrain = new Train(
                        nextTrainNumber,
                        "Regional Express " + nextTrainNumber,
                        source,
                        destination,
                        departure,
                        arrival,
                        30, 60, 30);

                railwaySystem.addTrain(generatedTrain);
                existingRoutes.add(key);

                nextTrainNumber++;
                addedRouteTrains++;
            }
        }

        System.out.printf("Additional route trains created: %d%n", addedRouteTrains);

        LocalDate today = LocalDate.now();
        for (Train train : railwaySystem.getAllTrains()) {
            for (LocalDate i = today.minusDays(7); !i.isAfter(today.plusDays(30)); i = i.plusDays(1)) {
                railwaySystem.getOrCreateJourney(train.getTrainNumber(), i);
            }
        }

        passengers = new ArrayList<>();
        passengers.add(railwaySystem.registerPassenger("vamsi", 23, Gender.MALE, "9542204142", "vamsi@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("vivek", 24, Gender.MALE, "9542204143", "vivek@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("Adi", 26, Gender.MALE, "7659096936", "adi@gmail.in"));
        passengers.add(railwaySystem.registerPassenger("vardhan", 25, Gender.MALE, "7659096935", "vardhan@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("lakshmi", 48, Gender.FEMALE, "7396229272", "lakshmi@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("naga", 47, Gender.FEMALE, "7396227292", "naga@yahoo.in"));
        passengers.add(railwaySystem.registerPassenger("raja", 59, Gender.MALE, "9491512535", "raja@film.in"));
        passengers.add(railwaySystem.registerPassenger("rao", 58, Gender.MALE, "9491512536", "rao@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("deepu", 31, Gender.MALE, "9876543210", "deep@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("mani", 32, Gender.MALE, "0123456789", "mani@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("teja", 29, Gender.FEMALE, "1234567890", "teja@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("john", 36, Gender.OTHER, "0987654321", "john@chat.in"));
        passengers.add(railwaySystem.registerPassenger("emma", 19, Gender.FEMALE, "2134567890", "emma@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("emily", 75, Gender.FEMALE, "3214567890", "emily@gmail.com"));
        passengers.add(railwaySystem.registerPassenger("mohan", 25, Gender.MALE, "9807654321", "mohan@gmail.com"));

        createSampleBookings(railwaySystem);
    }

    public List<Passenger> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    public Map<Integer, LocalDate> getDemoJourneyDates() {
        return Collections.unmodifiableMap(demoJourneyDates);
    }

    private void createSampleBookings(RailwaySystem railwaySystem) {

        List<Train> trains = new ArrayList<>(railwaySystem.getAllTrains());

        Random random = new Random();

        // Randomize which trains receive demo bookings.
        Collections.shuffle(trains, random);

        int trainsToPopulate = (int) Math.ceil(trains.size() * 0.80);

        LocalDate today = LocalDate.now();
        int passengerIndex = 0;
        int totalIndividualBookings = 0;
        int totalGroupBookings = 0;

        for (int i = 0; i < trainsToPopulate; i++) {

            Train train = trains.get(i);

            // Each populated train uses a random future journey date.
            LocalDate journeyDate = today.plusDays(1 + random.nextInt(30));

            // Store the exact date used for this train's demo bookings.
            demoJourneyDates.put(train.getTrainNumber(), journeyDate);
            
            boolean groupCreatedForTrain = false;

            for (TravelClass travelClass : TravelClass.values()) {

                int capacity = getClassCapacity(train, travelClass);

                if (capacity <= 0) {
                    continue;
                }

                // Random target between 40% and 60% for this class.
                double occupancy = 0.40 + random.nextDouble() * 0.20;

                int targetBookings = (int) Math.round(capacity * occupancy);

                int remainingBookings = targetBookings;

                // Add a few group bookings without exceeding
                // the target number of booked seats.
                if (!groupCreatedForTrain && totalGroupBookings < 12 && remainingBookings >= 2 && random.nextDouble() < 0.12) {
                    int maxGroupSize = Math.min(6, remainingBookings);

                    int groupSize = 2 + random.nextInt(maxGroupSize - 1);

                    Passenger owner = passengers.get(random.nextInt(passengers.size()));

                    List<TravellerBooking> travellers = new ArrayList<>();

                    for (int j = 0; j < groupSize; j++) {
                        Gender gender = Gender.values()[random.nextInt(Gender.values().length)];

                        String travellerName = "Group Traveller " + (char) ('A' + j);

                        travellers.add(new TravellerBooking(travellerName, 20 + random.nextInt(40), gender, null, BookingStatus.CONFIRMED));
                    }

                    try {
                        railwaySystem.bookGroupTicket(owner.getPassengerId(), train.getTrainNumber(), journeyDate, travelClass, travellers);

                        remainingBookings -= groupSize;
                        totalGroupBookings++;
                        groupCreatedForTrain = true;

                    } catch (TrainNotFoundException | SeatNotAvailableException | InvalidPassengerException e) {

                        throw new IllegalStateException("Failed to create sample group booking", e);
                    }
                }

                // Fill the remaining target seats with individual tickets.
                for (int j = 0; j < remainingBookings; j++) {

                    Passenger passenger = passengers.get(passengerIndex % passengers.size());

                    try {
                        Ticket ticket = railwaySystem.bookTicket(train.getTrainNumber(), passenger, journeyDate, travelClass);

                        if (ticket != null) {
                            totalIndividualBookings++;
                        }

                        passengerIndex++;

                    } catch (TrainNotFoundException | SeatNotAvailableException | InvalidPassengerException e) {

                        throw new IllegalStateException("Failed to initialize bookings for train " + train.getTrainNumber() + ", date " + journeyDate + ", class " + travelClass, e);
                    }
                }
            }
        }

        System.out.printf("%n========== DEMO DATA SUMMARY ==========%n");
        System.out.printf("Total trains                 : %d%n", railwaySystem.getAllTrains().size());
        System.out.printf("Trains populated with bookings: %d%n", trainsToPopulate);
        System.out.printf("Individual tickets created   : %d%n", totalIndividualBookings);
        System.out.printf("Group bookings created       : %d%n", totalGroupBookings);
        System.out.printf("=======================================%n");
    }


    private int getClassCapacity(Train train, TravelClass travelClass) {

        switch (travelClass) {
            case AC:
                return train.getAcSeats();
            case SLEEPER:
                return train.getSleeperSeats();
            case GENERAL:
                return train.getGeneralSeats();
            default:
                throw new IllegalArgumentException("Unsupported travel class: " + travelClass);
        }
    }

    
    private String routeKey(String source, String destination) {
        return source.trim().toLowerCase()
                + "->"
                + destination.trim().toLowerCase();
    }

    private boolean trainNumberExists(
            RailwaySystem railwaySystem, int trainNumber) {

        for (Train train : railwaySystem.getAllTrains()) {
            if (train.getTrainNumber() == trainNumber) {
                return true;
            }
        }

        return false;
    }

}
