package com.railway.exceptions;

/** Signals invalid passenger details or registration state. */
public class InvalidPassengerException extends Exception{
    public InvalidPassengerException(String message){
        super(message);
    }
}
