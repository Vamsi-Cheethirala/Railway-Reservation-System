
package com.railway.main;

import com.railway.util.InputValidator;

public class InputValidatorTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.printf("[PASS] %s%n", name);
        } else {
            failed++;
            System.out.printf("[FAIL] %s%n", name);
        }
    }

    public static void main(String[] args) {

        // Name validation
        check("Valid name accepted",
                InputValidator.isValidName("Ravi Kumar"));

        check("Blank name rejected",
                !InputValidator.isValidName("   "));

        check("Null name rejected",
                !InputValidator.isValidName(null));

        // Age validation
        check("Age 1 accepted",
                InputValidator.isValidAge(1));

        check("Age 120 accepted",
                InputValidator.isValidAge(120));

        check("Age 0 rejected",
                !InputValidator.isValidAge(0));

        check("Age 121 rejected",
                !InputValidator.isValidAge(121));

        // Phone validation
        check("Ten-digit phone accepted",
                InputValidator.isValidPhoneNumber("9876543210"));

        check("Nine-digit phone rejected",
                !InputValidator.isValidPhoneNumber("987654321"));

        check("Alphabetic phone rejected",
                !InputValidator.isValidPhoneNumber("abcdefghij"));

        check("Null phone rejected",
                !InputValidator.isValidPhoneNumber(null));

        // Email validation
        check("Valid .com email accepted",
                InputValidator.isValidEmail("ravi@example.com"));

        check("Valid .in email accepted",
                InputValidator.isValidEmail("ravi@example.in"));

        check("Valid .org email accepted",
                InputValidator.isValidEmail("ravi@example.org"));

        check("Email without @ rejected",
                !InputValidator.isValidEmail("raviexample.com"));

        check("Email without domain suffix rejected",
                !InputValidator.isValidEmail("ravi@example"));

        check("Null email rejected",
                !InputValidator.isValidEmail(null));

        // Password validation
        check("Strong password accepted",
                InputValidator.isValidPassword("Ravi@123"));

        check("Password without uppercase rejected",
                !InputValidator.isValidPassword("ravi@1234"));

        check("Password without digit rejected",
                !InputValidator.isValidPassword("Ravi@abcd"));

        check("Password without special character rejected",
                !InputValidator.isValidPassword("Ravi1234"));

        check("Password below minimum length rejected",
                !InputValidator.isValidPassword("Ra@1"));

        check("Password above maximum length rejected",
                !InputValidator.isValidPassword("Ravi@1234567890123456"));

        System.out.printf("%n========== SUMMARY ==========%n");
        System.out.printf("Passed: %d%n", passed);
        System.out.printf("Failed: %d%n", failed);
        System.out.printf("Total : %d%n", passed + failed);
    }
}
