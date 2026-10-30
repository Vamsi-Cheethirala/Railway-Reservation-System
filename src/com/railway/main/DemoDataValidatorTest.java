
package com.railway.main;

import com.railway.service.RailwaySystem;
import com.railway.service.DataInitializer;
import com.railway.service.DemoDataValidator;

public class DemoDataValidatorTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("       DEMO DATA VALIDATOR TESTS");
        System.out.println("==============================================");

        testNullRailwaySystem();
        testNullDataInitializer();
        testValidatorDoesNotCreateJourneys();

        System.out.println();
        System.out.println("==============================================");
        System.out.println("              TEST SUMMARY");
        System.out.println("==============================================");

        System.out.println("Passed : " + passed);
        System.out.println("Failed : " + failed);
        System.out.println("Total  : " + (passed + failed));

        if (failed == 0) {
            System.out.println("ALL DEMO DATA VALIDATOR TESTS PASSED!");
        } else {
            System.out.println("SOME TESTS FAILED!");
        }
    }

    private static void testNullRailwaySystem() {

        try {
            new DemoDataValidator(null, null);
            fail("Null RailwaySystem is rejected");

        } catch (IllegalArgumentException e) {
            pass("Null RailwaySystem is rejected");
        }
    }

    private static void testNullDataInitializer() {

        try {
            new DemoDataValidator(new RailwaySystem(), null);
            fail("Null DataInitializer is rejected");

        } catch (IllegalArgumentException e) {
            pass("Null DataInitializer is rejected");
        }
    }

    private static void testValidatorDoesNotCreateJourneys() {

        try {
            RailwaySystem railwaySystem = new RailwaySystem();

            DataInitializer dataInitializer =
                    new DataInitializer(railwaySystem);

            int before =
                    railwaySystem.getJourneyCountForTesting();

            DemoDataValidator validator =
                    new DemoDataValidator(
                            railwaySystem,
                            dataInitializer);

            validator.printReport();

            int after =
                    railwaySystem.getJourneyCountForTesting();

            if (before == after) {
                pass("DemoDataValidator does not create journeys");
            } else {
                fail("Validator changed journey count: before="
                        + before + ", after=" + after);
            }

        } catch (Exception e) {
            fail("Read-only validation test: " + e.getMessage());
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
}
