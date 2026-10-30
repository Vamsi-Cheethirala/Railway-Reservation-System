package com.railway.exceptions;

/** Signals that a requested booking PNR could not be found. */
public class BookingNotFoundException extends Exception{
    public BookingNotFoundException(String message){
        super(message);
    }
}
