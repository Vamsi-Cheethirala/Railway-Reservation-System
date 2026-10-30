package com.railway.model;

import com.railway.enums.BookingStatus;
import com.railway.enums.Gender;

/** Stores one traveller's details and status within a group booking. */
public class TravellerBooking {
    
    private String name;
    private int age;
    private Gender gender;
    private Seat seat;
    private BookingStatus bookingStatus;

    public TravellerBooking(String name, int age, Gender gender, Seat seat, BookingStatus bookingStatus){

        if (name == null || name.isBlank() || age < 1 || gender == null || bookingStatus == null) {
            throw new IllegalArgumentException("Invalid traveller details");
        }
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.seat = seat;
        this.bookingStatus = bookingStatus;
    }

    public String getName(){
        return this.name;
    }

    public int getAge(){
        return this.age;
    }

    public Gender getGender(){
        return this.gender;
    }

    public Seat getSeat(){
        return this.seat;
    }

    public BookingStatus getBookingStatus(){
        return this.bookingStatus;
    }

    public boolean cancelTicket(){
        if (this.bookingStatus == BookingStatus.CONFIRMED) {
            this.bookingStatus = BookingStatus.CANCELLED;
            return true;
        } else {
            return false;
        }        
    }
}
