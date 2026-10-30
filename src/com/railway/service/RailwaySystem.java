package com.railway.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import com.railway.enums.BookingStatus;
import com.railway.enums.Gender;
import com.railway.enums.TravelClass;
import com.railway.exceptions.BookingNotFoundException;
import com.railway.exceptions.InvalidPassengerException;
import com.railway.exceptions.SeatNotAvailableException;
import com.railway.exceptions.TrainNotFoundException;
import com.railway.model.GroupBooking;
import com.railway.model.Passenger;
import com.railway.model.Seat;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.util.InputValidator;
import com.railway.model.TrainJourney;
import com.railway.util.PassengerIDGenerator;
import com.railway.util.PNRGenerator;
import com.railway.model.TravellerBooking;

/**
 * In-memory service layer for trains, passengers, journeys, and reservations.
 * This class contains the application's core business rules and admin operations.
 */
public class RailwaySystem {

    private List<Train> trains;
    private List<Passenger> passengers;
    private List<Ticket> tickets;
    private Map<String, TrainJourney> journeys = new HashMap<>();
    private final Set<String> removedJourneys = new HashSet<>();
    private List<GroupBooking> groupBookings;
    private static final String ADMIN_USERNAME = "system";
    private static final String ADMIN_PASSWORD = "manager";

    // ==================== INITIALIZATION ====================

    // ==================== INITIALIZATION ====================

    public RailwaySystem(){
        
        this.trains = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.tickets = new ArrayList<>();
        this.groupBookings = new ArrayList<>();

    }


    // ==================== TRAIN MANAGEMENT ====================

    /** Adds a train after rejecting null values and duplicate train numbers. */
    public void addTrain(Train train){
        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null");
        }
        for (Train trains : this.trains) {
            if (trains.getTrainNumber() == train.getTrainNumber()) {
                throw new IllegalArgumentException("This train number already exists !!!");
            }
        }
        this.trains.add(train);
    }


    public Train findTrain(int trainNumber) throws TrainNotFoundException{
        for (Train train : this.trains) {
            if (train.getTrainNumber() == trainNumber) {
                return train;
            } 
        }
        throw new TrainNotFoundException("Train Number " + trainNumber + " does not exist");
    }


    public List<Train> searchTrain(String source, String destination){
        List<Train> al = new ArrayList<>();
        for (Train train : this.trains) {
            if (train.getSource().equalsIgnoreCase(source) && train.getDestination().equalsIgnoreCase(destination)) {
                al.add(train);
            }
        }
        return al;
    }


    public List<Train> getAllTrains(){
        return Collections.unmodifiableList(this.trains);
    }


    /** Removes a train only when it has no confirmed individual or group bookings. */
    public boolean removeTrain(int trainNumber) throws TrainNotFoundException {
        Train train = findTrain(trainNumber);
        for (Ticket ticket : this.tickets) {
            if (ticket.getTrain() == train && ticket.getBookingStatus() == BookingStatus.CONFIRMED) {
                return false;
            }
        }

        for (GroupBooking booking : groupBookings) {
            if (booking.getTrain() == train && booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                return false;
            }
        }

        this.trains.remove(train);

        Iterator<String> iterator = journeys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();

            if (key.startsWith(trainNumber + "_")) {
                iterator.remove();
            }
        }

        return true;
    }


    // ==================== PASSENGER MANAGEMENT ====================

    /** Validates and registers a passenger, enforcing unique phone and email values. */
    public Passenger registerPassenger(String name, int age, Gender gender, String phoneNumber, String email) throws InvalidPassengerException{
        
        Passenger candidate = new Passenger("TEMP", name, age, gender, phoneNumber, email);
        
        if (!InputValidator.isValidPassenger(candidate)) {
            throw new InvalidPassengerException("Invalid passenger details. Check name, age, gender, phone and email.");
        }
        
        if (findPassengerBYPhoneNumber(phoneNumber) != null) {
            throw new InvalidPassengerException("Phone number already registered. Select Existing Passenger.");
        }

        if (findPassengerByEmail(email) != null) {
            throw new InvalidPassengerException("Email already registered. Select Existing Passenger.");
        }

        String passengerId = PassengerIDGenerator.generatePassengerID();

        Passenger passenger = new Passenger(passengerId, name, age, gender, phoneNumber, email);

        this.passengers.add(passenger);
        return passenger;
    }


    public Passenger findPassengerById(String passengerId){
        for (Passenger passenger : passengers) {
            if (passenger.getPassengerId().equals(passengerId)) {
                return passenger;
            }
        }
        return null;
    }


    public Passenger findPassengerBYPhoneNumber(String phoneNumber){
        for (Passenger passenger : this.passengers) {
            if (passenger.getPhoneNumber().equals(phoneNumber)) {
                return passenger;
            }
        }
        return null;
    }


    public Passenger findPassengerByEmail(String email){
        for (Passenger passenger : this.passengers) {
            if (passenger.getEmail().equalsIgnoreCase(email)) {
                return passenger;
            }
        }
        return null;
    }


    public List<Passenger> getAllPassengers(){
        return Collections.unmodifiableList(this.passengers);
    }


    public List<Passenger> adminViewPassengers(String username, String password) {
        requireAdmin(username, password);
        return getAllPassengers();
    }


    // ==================== INDIVIDUAL BOOKINGS ====================

    /** Books one available seat for a registered passenger and creates a PNR ticket. */
    public Ticket bookTicket(int trainNumber, Passenger passenger, LocalDate travelDate, TravelClass travelClass) throws TrainNotFoundException, SeatNotAvailableException ,InvalidPassengerException {

        if (!InputValidator.isValidPassenger(passenger)) {
            throw new InvalidPassengerException("Invalid passenger details. Please check name, age, and phone number.");
        }
        
        
        if (travelDate == null || travelClass == null) {
            throw new InvalidPassengerException("Travel date and class are required.");
        }

        if (travelDate.isBefore(LocalDate.now())) {
            throw new InvalidPassengerException("Journey date cannot be in the past.");
        }


        Passenger registeredPassenger = findPassengerById(passenger.getPassengerId());

        if (registeredPassenger == null || !registeredPassenger.getPhoneNumber().equals(passenger.getPhoneNumber()) || !registeredPassenger.getEmail().equals(passenger.getEmail())) {
            throw new InvalidPassengerException("Passenger must be registered before booking");
        }

        Train train = findTrain(trainNumber);
        TrainJourney trainJourney = getOrCreateJourney(trainNumber, travelDate);

        Seat seat = trainJourney.getRandomAvailableSeat(travelClass);
       
        if (seat == null) {
            throw new SeatNotAvailableException("No seats available in " + travelClass + " class for train " + trainNumber);
        }

        seat.bookSeat();

        String pnr = PNRGenerator.generatePNR();

        Ticket ticket = new Ticket(pnr, passenger, train, travelDate, seat, travelClass, trainJourney);
 
        this.tickets.add(ticket);
        return ticket;
    }


    public Ticket findTicket(String pnr) throws BookingNotFoundException{
        for (Ticket ticket : this.tickets) {
            if (ticket.getPnr().equals(pnr)) {
                return ticket;
            }
        }
        throw new BookingNotFoundException("Booking not found for PNR " + pnr);
    }


    public List<Ticket> getAllBookings(){
        return Collections.unmodifiableList(this.tickets);
    }


    public List<Ticket> getBookingsByPassenger(String passengerId){
        List<Ticket> tickets = new ArrayList<>();
        for (Ticket ticket : this.tickets) {
            if (ticket.getPassenger().getPassengerId().equals(passengerId)) {
                tickets.add(ticket);
            }
        }
        return Collections.unmodifiableList(tickets);
    }


    /** Cancels an active individual ticket and releases its seat. */
    public boolean cancelTicket(String pnr) throws BookingNotFoundException{
        Ticket ticket = findTicket(pnr);

        if (ticket.getBookingStatus() == BookingStatus.CANCELLED) {
            return false;
        }

        ticket.cancelTicket();
        
        Seat seat = ticket.getSeat();

        seat.releaseSeat();

        return true;
    }


    public List<Ticket> adminViewIndividualBookings(String username, String password) {
        requireAdmin(username, password);
        return getAllBookings();
    }


    // ==================== GROUP BOOKINGS ====================

    /** Books a group atomically: if allocation fails, every allocated seat is released. */
    public GroupBooking bookGroupTicket(String passengerId, int trainNumber, LocalDate travelDate, TravelClass travelClass, List<TravellerBooking> travellers) throws InvalidPassengerException, TrainNotFoundException, SeatNotAvailableException {

    // 1. Validate booking details.
        if (travelDate == null || travelClass == null) {
            throw new IllegalArgumentException("Travel date and class cannot be null");
        }

        if (travelDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Journey date cannot be in the past");
        }

        if (travellers == null || travellers.size() < 2 || travellers.size() > 6) {
            throw new IllegalArgumentException("Group booking requires 2 to 6 travellers");
        }

    // 2. Resolve the registered booking owner.
        Passenger owner = findPassengerById(passengerId);

        if (owner == null) {
            throw new InvalidPassengerException("Booking owner is not a registered passenger");
        }

    // 3. Resolve the train and its date-specific journey.
        Train train = findTrain(trainNumber);
        TrainJourney journey = getOrCreateJourney(trainNumber, travelDate);

    // 4. Validate every traveller BEFORE reserving seats.
        for (TravellerBooking traveller : travellers) {
            if (traveller == null || !InputValidator.isValidName(traveller.getName()) || !InputValidator.isValidAge(traveller.getAge()) || traveller.getGender() == null) {
                throw new IllegalArgumentException("Every traveller must have a valid name, age and gender");
            }
        }

    // 5. Ensure sufficient seats exist.
        if (journey.getAvailableSeats(travelClass) < travellers.size()) {
            throw new SeatNotAvailableException("Insufficient seats available for the entire group");
        }

        List<Seat> allocatedSeats = new ArrayList<>();
        boolean bookingSaved = false;

        try {
        // 6. Create the group booking.
            GroupBooking groupBooking = new GroupBooking(PNRGenerator.generatePNR(), owner, train, travelDate, travelClass, journey, BookingStatus.CONFIRMED);

        // 7. Allocate a separate seat to every traveller.
            for (TravellerBooking traveller : travellers) {
                Seat seat = journey.getRandomAvailableSeat(travelClass);

                if (seat == null) {
                    throw new SeatNotAvailableException("Not enough seats available for the group");
                }

                if (!seat.bookSeat()) {
                    throw new SeatNotAvailableException("Selected seat is no longer available.");
                }

                allocatedSeats.add(seat);

                TravellerBooking confirmedTraveller = new TravellerBooking(traveller.getName(), traveller.getAge(), traveller.getGender(), seat, BookingStatus.CONFIRMED);

                groupBooking.addTraveller(confirmedTraveller);

                afterGroupSeatAllocated(allocatedSeats.size());
            }

        // 8. Save only after every traveller has a seat.
            groupBookings.add(groupBooking);
            bookingSaved = true;

            return groupBooking;

        } finally {
        // 9. If anything fails before saving, release every
        // seat reserved by this attempt.
            if (!bookingSaved) {
                for (Seat seat : allocatedSeats) {
                    seat.releaseSeat();
                }
            }
        }
    }


    public List<GroupBooking> getAllGroupBookings(){
        return Collections.unmodifiableList(groupBookings);
    }


    /** Allows the registered booking owner to cancel one traveller in a group. */
    public boolean cancelGroupTraveller(String requesterPassengerId, String pnr, int travellerIndex){
        if (requesterPassengerId == null || pnr == null) {
            return false;
        }

        GroupBooking booking = null;

        for (GroupBooking group : groupBookings) {
            if (pnr.equals(group.getPnr())) {
                booking = group;
                break;
            }
        }

        if (booking == null) {
            return false;
        }

        if (!requesterPassengerId.equals(booking.getBookingOwner().getPassengerId())) {
            return false;
        }

        return cancelGroupTravellerInternal(booking, travellerIndex);
    }


    public boolean adminCancelGroupTraveller(String adminUsername, String adminPassword, String pnr, int travellerIndex){
        if (!ADMIN_USERNAME.equals(adminUsername) || !ADMIN_PASSWORD.equals(adminPassword)) {
            return false;
        }

        if (pnr == null || pnr.isBlank()) {
            return false;
        }

        GroupBooking booking = null;

        for (GroupBooking group : groupBookings) {
            if (pnr.equals(group.getPnr())) {
                booking = group;
                break;
            }
        }

        if (booking == null) {
            return false;
        }

        return cancelGroupTravellerInternal(booking, travellerIndex);
    }


    private boolean cancelGroupTravellerInternal(GroupBooking booking, int travellerIndex) {

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            return false;
        }

        List<TravellerBooking> travellers = booking.getTravellers();

        if (travellerIndex < 0 || travellerIndex >= travellers.size()) {
            return false;
        }

        TravellerBooking traveller = travellers.get(travellerIndex);

        if (traveller.getBookingStatus() == BookingStatus.CANCELLED) {
            return false;
        }

        if (!traveller.cancelTicket()) {
            return false;
        }

        traveller.getSeat().releaseSeat();

        boolean anyoneConfirmed = travellers.stream().anyMatch(t -> t.getBookingStatus() == BookingStatus.CONFIRMED);

        if (!anyoneConfirmed) {
            booking.updateBookingStatus(BookingStatus.CANCELLED);
        }

        return true;
    }


    public List<GroupBooking> adminViewGroupBookings(String username, String password) {
        requireAdmin(username, password);
        return getAllGroupBookings();
    }


    // ==================== JOURNEY AND SEAT INVENTORY ====================

    /** Returns the journey inventory for a train/date, creating it when absent. */
    public TrainJourney getOrCreateJourney(int trainNumber, LocalDate journeyDate) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        if (train == null) {
            return null;
        }

        String key = journeyKey(trainNumber, journeyDate);

        if (removedJourneys.contains(key)) {
            throw new TrainNotFoundException(
                    "Journey for train " + trainNumber + " on " + journeyDate + " has been removed");
        }

        if (!journeys.containsKey(key)) {
            journeys.put(key, new TrainJourney(train, journeyDate));
        }

        return journeys.get(key);
    }


    /**
 * Retrieves an existing journey without creating a new one.
 * Returns null if the journey inventory does not exist.
 */
    public TrainJourney findExistingJourney(int trainNumber,LocalDate journeyDate) throws TrainNotFoundException {

        if (journeyDate == null) {
            throw new IllegalArgumentException("Journey date cannot be null");
        }

        // Verify that the train exists.
        findTrain(trainNumber);

        // Retrieve existing inventory without modifying the map.
        String key = journeyKey(trainNumber, journeyDate);

        return journeys.get(key);
    }

    /** Adjusts capacity for one specific journey and travel class. */
    public boolean adjustSeatCapacityJourney(int trainNumber, LocalDate journeyDate, TravelClass travelClass, int change) throws TrainNotFoundException{
        TrainJourney trainJourney = getOrCreateJourney(trainNumber, journeyDate);
        return trainJourney.adjustSeatCapacity(travelClass, change);
    }


    public int checkAvailability(int trainNumber) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        return train.getAvailableSeats();
    }


    public int checkAvailability(int trainNumber, TravelClass travelClass) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        return train.getAvailableSeats(travelClass);
    }


    public int checkAvailability(int trainNumber, LocalDate travelDate, TravelClass travelClass) throws TrainNotFoundException{
        TrainJourney trainJourney = getOrCreateJourney(trainNumber, travelDate);
        return trainJourney.getAvailableSeats(travelClass);
    }


    public int acSeats(int trainNumber) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        return train.getAcSeats();
    }


    public int sleeperSeats(int trainNumber) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        return train.getSleeperSeats();
    }


    public int generalSeats(int trainNumber) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        return train.getGeneralSeats();
    }


    public int totalSeats(int trainNumber) throws TrainNotFoundException{
        Train train = findTrain(trainNumber);
        return train.getTotalSeats();
    }


    // ==================== ADMIN OPERATIONS ====================

    /** Authenticates an admin, adds a train, and initializes its rolling journey window. */
    public boolean adminAddTrain(String username, String password, Train train) throws TrainNotFoundException {
        requireAdmin(username, password);

        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null");
        }

        addTrain(train);

        LocalDate today = LocalDate.now();

        for(int offset = -7; offset <= 30; offset++) {
            getOrCreateJourney(train.getTrainNumber(), today.plusDays(offset));
        }

        return true;
    }



    /**
     * Authenticates an admin before removing a train.
     * Removal is rejected if any individual or group booking is confirmed.
     * Removes all journey inventory belonging to the train after successful removal.
     */
    public boolean adminRemoveTrain(String username, String password, int trainNumber) throws TrainNotFoundException {

        requireAdmin(username, password);

        Train train = findTrain(trainNumber);

        // Check individual bookings across every date.
        for (Ticket ticket : tickets) {
            if (ticket.getTrain() == train && ticket.getBookingStatus() == BookingStatus.CONFIRMED) {
                return false;
            }
        }

        // Check group bookings across every date.
        for (GroupBooking booking : groupBookings) {
            if (booking.getTrain() == train && booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                return false;
            }
        }

        // Remove the train from the registered train list.
        trains.remove(train);

        // Remove all journey inventory belonging to this train.
        String prefix = trainNumber + "_";
        journeys.keySet().removeIf(key -> key.startsWith(prefix));

        // Remove journey-removal markers for this train.
        // The train itself no longer exists, so these markers are unnecessary.
        removedJourneys.removeIf(key -> key.startsWith(prefix));

        return true;
    }

    /**
     * Removes a train's journey for one date only. The train remains registered
     * for other dates, and a removed journey cannot be recreated accidentally.
     */
    public boolean adminRemoveTrainJourney(String username, String password, int trainNumber, LocalDate journeyDate) throws TrainNotFoundException {
        requireAdmin(username, password);

        if (journeyDate == null) {
            throw new IllegalArgumentException("Journey date is required");
        }

        Train train = findTrain(trainNumber);
        String key = journeyKey(trainNumber, journeyDate);

        if (removedJourneys.contains(key)) {
            return false;
        }

        for (Ticket ticket : tickets) {
            if (ticket.getTrain() == train && ticket.getTravelDate().equals(journeyDate) && ticket.getBookingStatus() == BookingStatus.CONFIRMED) {
                return false;
            }
        }

        for (GroupBooking booking : groupBookings) {
            if (booking.getTrain() == train && booking.getTravelDate().equals(journeyDate) && booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                return false;
            }
        }

        journeys.remove(key);
        removedJourneys.add(key);
        return true;
    }


    /** Authenticates an admin before changing journey seat capacity. */
    public boolean adminAdjustSeatCapacityJourney(String username, String password, int trainNumber, LocalDate journeyDate, TravelClass travelClass, int change) throws TrainNotFoundException {
        requireAdmin(username, password);

        if (journeyDate == null || travelClass == null) {
            throw new IllegalArgumentException("Journey date and travel class are required");
        }

        return adjustSeatCapacityJourney(trainNumber, journeyDate, travelClass, change);
    }


    // ==================== PRIVATE ADMIN HELPER ====================

    private String journeyKey(int trainNumber, LocalDate journeyDate) {
        return trainNumber + "_" + journeyDate;
    }

    private void requireAdmin(String username, String password) {
        if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
            throw new SecurityException("Admin authentication failed");
        }
    }


    // ==================== TEST HOOK ====================

    protected void afterGroupSeatAllocated(int allocatedCount) {
        // Test seam: no action during normal booking.
    }

    // Package-private accessor for automated tests.
    public int getJourneyCountForTesting() {
        return journeys.size();
    }

}
