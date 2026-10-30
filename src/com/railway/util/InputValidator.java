
package com.railway.util;

import com.railway.model.Passenger;

/**
 * Contains validation rules used by passenger registration and booking.
 */
public class InputValidator {

    public static boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    public static boolean isValidAge(int age) {
        return age >= 1 && age <= 120;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && phoneNumber.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email) {
        return email != null
                && email.matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                );
    }

    public static boolean isValidPassword(String password) {
        return password != null
                && password.matches(
                    "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])"
                    + "(?=.*[@$%#!^&*]).{8,20}$"
                );
    }

    public static boolean isValidPassenger(Passenger passenger) {
        return passenger != null
                && isValidName(passenger.getName())
                && isValidAge(passenger.getAge())
                && passenger.getGender() != null
                && isValidPhoneNumber(passenger.getPhoneNumber())
                && isValidEmail(passenger.getEmail());
    }
}
