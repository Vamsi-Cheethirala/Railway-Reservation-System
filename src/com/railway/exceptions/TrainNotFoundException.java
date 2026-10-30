package com.railway.exceptions;

/** Signals that a requested train number does not exist. */
public class TrainNotFoundException extends Exception{
    public TrainNotFoundException(String message){
        super(message);
    }
}
