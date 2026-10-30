
package com.railway.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates in-process PNR values for individual and group reservations.
 */
public final class PNRGenerator {

    private static final AtomicLong counter = new AtomicLong(10000);

    private PNRGenerator() {
        // Prevent object creation.
    }

    public static String generatePNR() {
        return "RLY" + counter.incrementAndGet();
    }
}
