
package com.railway.main;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.railway.enums.TravelClass;

public class MainInputTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("           MAIN INPUT HELPER TESTS");
        System.out.println("==============================================");

        testValidInteger();
        testInvalidInteger();
        testNegativeInteger();
        testNonNegativeInteger();
        testValidText();
        testBlankText();
        testValidDate();
        testInvalidDate();
        testValidTime();
        testInvalidTime();
        testValidEnum();
        testInvalidEnum();
        testEndOfInput();

        printSummary();
    }

    private static void testValidInteger() {

        try (Scanner scanner = new Scanner("25\n")) {

            int result = Main.readInt(scanner, "Number: ");

            assertEquals(25, result, "Valid integer accepted");
        }
    }

    private static void testInvalidInteger() {

        try (Scanner scanner = new Scanner("abc\n45\n")) {

            int result = Main.readInt(scanner, "Number: ");

            assertEquals(45, result,
                    "Invalid integer rejected and valid input accepted");
        }
    }

    private static void testNegativeInteger() {

        try (Scanner scanner = new Scanner("-10\n")) {

            int result = Main.readInt(scanner, "Number: ");

            assertEquals(-10, result,
                    "Negative integer accepted by readInt");
        }
    }

    private static void testNonNegativeInteger() {

        try (Scanner scanner = new Scanner("-5\n0\n")) {

            int result = Main.readNonNegativeInt(
                    scanner, "Capacity: ");

            assertEquals(0, result,
                    "Negative capacity rejected and zero accepted");
        }
    }

    private static void testValidText() {

        try (Scanner scanner = new Scanner("Vamsi\n")) {

            String result = Main.readText(scanner, "Name: ");

            assertEquals("Vamsi", result,
                    "Valid text accepted");
        }
    }

    private static void testBlankText() {

        try (Scanner scanner = new Scanner("   \nJava\n")) {

            String result = Main.readText(scanner, "Name: ");

            assertEquals("Java", result,
                    "Blank text rejected and valid text accepted");
        }
    }

    private static void testValidDate() {

        try (Scanner scanner =
                new Scanner("2026-02-30\n2026-11-15\n")) {

            LocalDate result = Main.readDate(
                    scanner, "Journey date: ");

            assertEquals(
                    LocalDate.of(2026, 11, 15),
                    result,
                    "Invalid calendar date rejected; valid date accepted");
        }
    }

    private static void testInvalidDate() {

        try (Scanner scanner = new Scanner("15-11-2026\n")) {

            boolean rejected = false;

            try {
                Main.readDate(scanner, "Journey date: ");
            } catch (NoSuchElementException e) {
                rejected = true;
            }

            assertTrue(rejected,
                    "Incorrect date format rejected");
        }
    }

    private static void testValidTime() {

        try (Scanner scanner = new Scanner("18:45\n")) {

            LocalTime result = Main.readTime(
                    scanner, "Departure time: ");

            assertEquals(
                    LocalTime.of(18, 45),
                    result,
                    "Valid time accepted");
        }
    }

    private static void testInvalidTime() {

        try (Scanner scanner =
                new Scanner("25:70\n09:30\n")) {

            LocalTime result = Main.readTime(
                    scanner, "Departure time: ");

            assertEquals(
                    LocalTime.of(9, 30),
                    result,
                    "Invalid time rejected; valid time accepted");
        }
    }

    private static void testValidEnum() {

        try (Scanner scanner =
                new Scanner("ac\n")) {

            TravelClass result = Main.readEnum(
                    scanner,
                    "Travel class: ",
                    TravelClass.class);

            assertEquals(
                    TravelClass.AC,
                    result,
                    "Case-insensitive enum input accepted");
        }
    }

    private static void testInvalidEnum() {

        try (Scanner scanner =
                new Scanner("FIRST\nSLEEPER\n")) {

            TravelClass result = Main.readEnum(
                    scanner,
                    "Travel class: ",
                    TravelClass.class);

            assertEquals(
                    TravelClass.SLEEPER,
                    result,
                    "Invalid enum rejected; valid enum accepted");
        }
    }

    private static void testEndOfInput() {

        try (Scanner scanner = new Scanner("")) {

            boolean handled = false;

            try {
                Main.readInt(scanner, "Number: ");
            } catch (NoSuchElementException e) {
                handled = true;
            }

            assertTrue(
                    handled,
                    "EOF is detected instead of causing an infinite loop");
        }
    }

    private static void assertEquals(
            Object expected,
            Object actual,
            String message) {

        if (expected == null
                ? actual == null
                : expected.equals(actual)) {

            pass(message);

        } else {

            fail(message + " | Expected: "
                    + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(
            boolean condition,
            String message) {

        if (condition) {
            pass(message);
        } else {
            fail(message);
        }
    }

    private static void pass(String message) {

        passed++;
        System.out.println("[PASS] " + message);
    }

    private static void fail(String message) {

        failed++;
        System.out.println("[FAIL] " + message);
    }

    private static void printSummary() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("                TEST SUMMARY");
        System.out.println("==============================================");

        System.out.println("Passed : " + passed);
        System.out.println("Failed : " + failed);
        System.out.println("Total  : " + (passed + failed));

        if (failed == 0) {
            System.out.println("ALL MAIN INPUT TESTS PASSED!");
        } else {
            System.out.println("SOME MAIN INPUT TESTS FAILED!");
        }
    }
}
