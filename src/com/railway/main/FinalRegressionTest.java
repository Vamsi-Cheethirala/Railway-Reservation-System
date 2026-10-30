
package com.railway.main;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FinalRegressionTest {

    private static int totalPassed = 0;
    private static int totalFailed = 0;
    private static int suiteErrors = 0;

    private static final String[] TEST_CLASSES = {
            "com.railway.main.TestMain",
            "com.railway.main.InputValidatorTest",
            "com.railway.main.TrainJourneyTest",
            "com.railway.main.SeatTicketTest",
            "com.railway.main.BookingIntegrationTest",
            "com.railway.main.DemoDataValidatorTest",
            "com.railway.main.MainInputTest",
            "com.railway.main.MainMenuIntegrationTest"
    };

    public static void main(String[] args) {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("          RAILWAY RESERVATION SYSTEM");
        System.out.println("             FINAL REGRESSION TEST");
        System.out.println("====================================================");

        for (String className : TEST_CLASSES) {
            runTestSuite(className);
        }

        printFinalSummary();
    }

    private static void runTestSuite(String className) {

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        ByteArrayOutputStream outputBuffer =
                new ByteArrayOutputStream();

        int passed = 0;
        int failed = 0;
        boolean executionError = false;
        String errorMessage = null;

        try (PrintStream capturedOutput = new PrintStream(
                outputBuffer, true, StandardCharsets.UTF_8)) {

            System.setOut(capturedOutput);
            System.setErr(capturedOutput);

            Class<?> testClass = Class.forName(className);

            Method mainMethod =
                    testClass.getMethod("main", String[].class);

            mainMethod.invoke(null, (Object) new String[0]);

        } catch (InvocationTargetException e) {

            executionError = true;

            Throwable cause = e.getCause();

            errorMessage = cause == null
                    ? e.toString()
                    : cause.toString();

        } catch (Exception e) {

            executionError = true;
            errorMessage = e.toString();

        } finally {

            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        String output =
                outputBuffer.toString(StandardCharsets.UTF_8);

        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("TEST SUITE: " + simpleClassName(className));
        System.out.println("----------------------------------------------------");

        System.out.println(output);

        Integer reportedPassed = extractCount(output, "Passed");
        Integer reportedFailed = extractCount(output, "Failed");

        if (reportedPassed == null || reportedFailed == null) {

            executionError = true;

            System.out.println(
                    "[ERROR] Could not extract the test summary."
            );

        } else {

            passed = reportedPassed;
            failed = reportedFailed;

            totalPassed += passed;
            totalFailed += failed;
        }

        if (executionError) {

            suiteErrors++;

            System.out.println("[SUITE ERROR] " + errorMessage);

            // Count an unexpected execution error if the suite
            // did not already report failed test cases.
            if (reportedFailed == null || reportedFailed == 0) {
                totalFailed++;
            }
        }

        if (!executionError && failed == 0) {
            System.out.println("[SUITE RESULT] PASSED");
        } else {
            System.out.println("[SUITE RESULT] FAILED");
        }
    }

    private static Integer extractCount(
            String output,
            String label) {

        Pattern pattern = Pattern.compile(
                "(?im)^\\s*" + label + "\\s*:\\s*(\\d+)\\s*$"
        );

        Matcher matcher = pattern.matcher(output);

        Integer result = null;

        while (matcher.find()) {
            result = Integer.parseInt(matcher.group(1));
        }

        return result;
    }

    private static String simpleClassName(String className) {

        return className.substring(
                className.lastIndexOf('.') + 1
        );
    }

    private static void printFinalSummary() {

        System.out.println();
        System.out.println("====================================================");
        System.out.println("              FINAL TEST SUMMARY");
        System.out.println("====================================================");

        System.out.println("Test suites executed : " + TEST_CLASSES.length);
        System.out.println("Passed test cases    : " + totalPassed);
        System.out.println("Failed test cases    : " + totalFailed);
        System.out.println("Suite execution errors: " + suiteErrors);

        System.out.println("====================================================");

        if (totalFailed == 0 && suiteErrors == 0) {

            System.out.println(
                    "ALL REGRESSION TEST SUITES PASSED!"
            );

        } else {

            System.out.println(
                    "REGRESSION TESTS FAILED. REVIEW THE OUTPUT."
            );
        }

        System.out.println("====================================================");
    }
}
