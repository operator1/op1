package com.op1.util;

/**
 * Input argument validation methods, like guava's Preconditions, but just to remain dependency free.
 */
public class Check {

    public static <T> T notNull(T t, String message) {
        if (t == null) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }

    public static void that(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void state(boolean b, String message) {
        if (!b) {
            throw new IllegalStateException(message);
        }
    }
}
