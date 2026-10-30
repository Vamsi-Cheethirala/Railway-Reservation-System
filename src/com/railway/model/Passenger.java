package com.railway.model;

import com.railway.enums.Gender;

/** Stores the registration details of a railway passenger. */
public class Passenger {

    private String passengerId;
    private String name;
    private int age;
    private Gender gender;
    private String phoneNumber;
    private String email;

    public Passenger(String passengerId, String name, int age, Gender gender, String phoneNumber, String email){
        this.passengerId = passengerId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getPassengerId(){
        return this.passengerId;
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

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public String getEmail(){
        return email;
    }
    
    /** Displays passenger details while masking most of the phone number. */
    public void displayPassengerDetails() {
        String maskedPhone = phoneNumber.length() <= 4
                ? "*".repeat(phoneNumber.length())
                : "*".repeat(phoneNumber.length() - 4)
                        + phoneNumber.substring(phoneNumber.length() - 4);

        System.out.printf("%n+----------------------------------------+%n");
        System.out.printf("| %-38s |%n", "PASSENGER DETAILS");
        System.out.printf("+----------------------------------------+%n");
        System.out.printf("| %-14s : %-21s |%n", "Passenger ID", this.passengerId);
        System.out.printf("| %-14s : %-21s |%n", "Name", this.name);
        System.out.printf("| %-14s : %-21d |%n", "Age", this.age);
        System.out.printf("| %-14s : %-21s |%n", "Gender", this.gender);
        System.out.printf("| %-14s : %-21s |%n", "Phone", maskedPhone);
        System.out.printf("| %-14s : %-21s |%n", "Email", this.email);
        System.out.printf("+----------------------------------------+%n");
    }
}
