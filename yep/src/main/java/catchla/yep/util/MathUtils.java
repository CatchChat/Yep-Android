/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

public class MathUtils {
    public static float clamp(final float num, final float bound1, final float bound2) {
        final float max = Math.max(bound1, bound2), min = Math.min(bound1, bound2);
        return Math.max(Math.min(num, max), min);
    }

    public static int clamp(final int num, final int bound1, final int bound2) {
        final int max = Math.max(bound1, bound2), min = Math.min(bound1, bound2);
        return Math.max(Math.min(num, max), min);
    }

    // Returns the next power of two.
    // Returns the input if it is already power of 2.
    // Throws IllegalArgumentException if the input is <= 0 or
    // the answer overflows.
    public static int nextPowerOf2(int n) {
        if (n <= 0 || n > 1 << 30) throw new IllegalArgumentException("n is invalid: " + n);
        n -= 1;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        return n + 1;
    }

    // Returns the previous power of two.
    // Returns the input if it is already power of 2.
    // Throws IllegalArgumentException if the input is <= 0
    public static int prevPowerOf2(final int n) {
        if (n <= 0) throw new IllegalArgumentException();
        return Integer.highestOneBit(n);
    }

    public static float avg(final float[] array, final int start, final int end) {
        float avg = array[start];
        for (int i = start + 1; i < end; i++) {
            avg = (avg + array[i]) / 2;
        }
        return avg;
    }
}
