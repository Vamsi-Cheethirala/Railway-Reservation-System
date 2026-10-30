package com.railway.enums;

/** Describes available travel classes used by the reservation system. */
public enum TravelClass {
    AC("AC"),
    SLEEPER("SL"),
    GENERAL("GN");

    private String prefix;

    TravelClass(String prefix){
        this.prefix = prefix;
    }

    public String getPrefix(){
        return prefix;
    }
}
