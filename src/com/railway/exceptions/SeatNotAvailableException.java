package com.railway.exceptions;

/** Signals that the requested seat inventory is insufficient. */
public class SeatNotAvailableException extends Exception{
    public SeatNotAvailableException(String message){
        super(message);
    }
}
