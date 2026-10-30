
package com.railway.main;

import com.railway.enums.Gender;
import com.railway.service.RailwaySystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MainMenuIntegrationTest {

    private static int passed = 0;
    private static int failed = 0;

    private static final String TEST_PASSENGER_NAME = "Vamsi Test";
    private static final String TEST_PHONE = "9876543210";
    private static final String TEST_EMAIL = "vamsi.test@example.com";

    public static void main(String[] args) {

        printHeader("MAIN MENU INTEGRATION TESTS");

        testMainMenuExit();
        testInvalidMainMenuChoice();
        testAdminLoginFailure();
        testAdminLoginSuccessAndLogout();
        testPassengerMenuBackNavigation();
        testPassengerRegistration();
        testExistingPassengerLogin();
        testInvalidPassengerLogin();

        printSummary();

        if (failed > 0) {
            throw new AssertionError(
                    "Some Main Menu integration tests failed."
            );
        }
    }

    // =========================================================
    // MAIN MENU TESTS
    // =========================================================

    private static void testMainMenuExit() {

        runTest("Main menu exit", () -> {

            String output = runMenu("3\n");

            assertContains(
                    output,
                    "Thank you for using Railway Reservation System."
            );
        });
    }

    private static void testInvalidMainMenuChoice() {

        runTest("Invalid main menu choice", () -> {

            String output = runMenu("9\n3\n");

            assertContains(output, "Please choose 1, 2, or 3");
            assertContains(output, "Thank you");
        });
    }

    // =========================================================
    // ADMIN LOGIN TESTS
    // =========================================================

    private static void testAdminLoginFailure() {

        runTest("Admin login failure", () -> {

            String input =
                    "1\n" +
                    "wrong\n" +
                    "wrong\n" +
                    "3\n";

            String output = runMenu(input);

            assertContains(output, "Login failed");
            assertContains(output, "Thank you");

            assertNotContains(
                    output,
                    "Welcome, administrator."
            );
        });
    }

    private static void testAdminLoginSuccessAndLogout() {

        runTest("Admin login success and logout", () -> {

            String input =
                    "1\n" +
                    "system\n" +
                    "manager\n" +
                    "9\n" +
                    "3\n";

            String output = runMenu(input);

            assertContains(output, "Welcome, administrator.");
            assertContains(output, "ADMIN DASHBOARD");
            assertContains(output, "Admin logged out.");
            assertContains(output, "Thank you");
        });
    }

    // =========================================================
    // PASSENGER MENU TESTS
    // =========================================================

    private static void testPassengerMenuBackNavigation() {

        runTest("Passenger menu back navigation", () -> {

            String input =
                    "2\n" +
                    "3\n" +
                    "3\n";

            String output = runMenu(input);

            assertContains(output, "PASSENGER ACCESS");
            assertContains(output, "Thank you");
        });
    }

    private static void testPassengerRegistration() {

        runTest("Passenger registration", () -> {

            String input =
                    "2\n" +                       // Main: Passenger Menu
                    "2\n" +                       // Register passenger
                    TEST_PASSENGER_NAME + "\n" +
                    "25\n" +
                    "MALE\n" +
                    TEST_PHONE + "\n" +
                    TEST_EMAIL + "\n" +
                    "8\n" +                       // Back from passenger menu
                    "3\n" +                       // Back from passenger access
                    "3\n";                        // Exit application

            String output = runMenu(input);

            assertContains(output, "Registration complete");
            assertContains(output, "PASSENGER MENU");
            assertContains(output, TEST_PASSENGER_NAME);
            assertContains(output, "Thank you");
        });
    }

    private static void testExistingPassengerLogin() {

        runTest("Existing passenger login", () -> {

            String input =
                    "2\n" +                       // Main: Passenger Menu
                    "1\n" +                       // Existing passenger login
                    "2\n" +                       // Search by phone
                    TEST_PHONE + "\n" +
                    "8\n" +                       // Back from passenger menu
                    "3\n" +                       // Back from passenger access
                    "3\n";                        // Exit application

            String output = runMenu(input, true);

            assertContains(output, "Passenger found");
            assertContains(output, TEST_PASSENGER_NAME);
            assertContains(output, "PASSENGER MENU");
            assertContains(output, "Thank you");
        });
    }

    private static void testInvalidPassengerLogin() {

        runTest("Invalid passenger login", () -> {

            String input =
                    "2\n" +
                    "1\n" +
                    "2\n" +
                    "9000000000\n" +
                    "3\n" +
                    "3\n";

            String output = runMenu(input);

            assertContains(output, "Passenger not found");
            assertContains(output, "Thank you");

            assertNotContains(
                    output,
                    "Signed in"
            );
        });
    }

    // =========================================================
    // TEST EXECUTION
    // =========================================================

    private static String runMenu(String input) throws Exception {

        return runMenu(input, false);
    }

    private static String runMenu(
            String input,
            boolean seedPassenger) throws Exception {

        PrintStream originalOut = System.out;

        ByteArrayOutputStream outputBuffer =
                new ByteArrayOutputStream();

        try (
                PrintStream capturedOut = new PrintStream(
                        outputBuffer,
                        true,
                        StandardCharsets.UTF_8
                )
        ) {

            System.setOut(capturedOut);

            RailwaySystem railwaySystem = new RailwaySystem();

            if (seedPassenger) {
                railwaySystem.registerPassenger(
                        TEST_PASSENGER_NAME,
                        25,
                        Gender.MALE,
                        TEST_PHONE,
                        TEST_EMAIL
                );
            }

            try (Scanner scanner = new Scanner(input)) {

                Main.runMainMenu(scanner, railwaySystem);
            }

            return outputBuffer.toString(StandardCharsets.UTF_8);

        } finally {

            System.setOut(originalOut);
        }
    }

    // =========================================================
    // ASSERTION HELPERS
    // =========================================================

    private static void assertContains(
            String output,
            String expected) {

        if (output == null ||
                !output.toLowerCase().contains(
                        expected.toLowerCase())) {

            throw new AssertionError(
                    "Expected output to contain: " + expected
            );
        }
    }

    private static void assertNotContains(
            String output,
            String unexpected) {

        if (output != null &&
                output.toLowerCase().contains(
                        unexpected.toLowerCase())) {

            throw new AssertionError(
                    "Output should not contain: " + unexpected
            );
        }
    }

    // =========================================================
    // TEST REPORTING
    // =========================================================

    private static void runTest(
            String testName,
            TestCase testCase) {

        try {

            testCase.run();
            passed++;

            System.out.println("[PASS] " + testName);

        } catch (Throwable e) {

            failed++;

            System.out.println("[FAIL] " + testName);
            System.out.println("       Reason: " + e.getMessage());
        }
    }

    private static void printHeader(String title) {

        System.out.println("\n========================================");
        System.out.println("      " + title);
        System.out.println("========================================\n");
    }

    private static void printSummary() {

        System.out.println("\n========================================");
        System.out.println("              TEST SUMMARY");
        System.out.println("========================================");

        System.out.println("Passed : " + passed);
        System.out.println("Failed : " + failed);
        System.out.println("Total  : " + (passed + failed));

        if (passed + failed > 0) {

            double successRate =
                    (passed * 100.0) / (passed + failed);

            System.out.printf(
                    "Success Rate: %.2f%%%n",
                    successRate
            );
        }

        System.out.println("========================================");
    }

    @FunctionalInterface
    interface TestCase {

        void run() throws Exception;
    }
}
