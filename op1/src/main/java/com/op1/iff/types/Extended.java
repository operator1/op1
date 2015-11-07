package com.op1.iff.types;

import java.util.Arrays;

/**
 * 80 bit IEEE Standard 754 floating point number (Standard Apple Numeric Environment [SANE] data type Extended).
 */
public class Extended extends DataType {

    public Extended(byte[] bytes) {
        super(bytes, 10);
    }

    @Override
    public String toString() {
        return String.valueOf(toDouble());
    }

    public double toDouble() {

        double f;
        int expon;
        long hiMant, loMant;
        long t1, t2;
        double HUGE = 3.40282346638528860e+38;

        expon = new UnsignedShort(Arrays.copyOfRange(bytes, 0, 2)).toInt();

        t1 = (long) new UnsignedShort(Arrays.copyOfRange(bytes, 2, 4)).toInt();
        t2 = (long) new UnsignedShort(Arrays.copyOfRange(bytes, 4, 6)).toInt();
        hiMant = t1 << 16 | t2;

        t1 = (long) new UnsignedShort(Arrays.copyOfRange(bytes, 6, 8)).toInt();
        t2 = (long) new UnsignedShort(Arrays.copyOfRange(bytes, 8, 10)).toInt();
        loMant = t1 << 16 | t2;

        if (expon == 0 && hiMant == 0 && loMant == 0) {
            f = 0;
        } else {
            if (expon == 0x7FFF)
                f = HUGE;
            else {
                expon -= 16383;
                expon -= 31;
                f = (hiMant * Math.pow(2, expon));
                expon -= 32;
                f += (loMant * Math.pow(2, expon));
            }
        }
        return f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Extended extended = (Extended) o;

        return Arrays.equals(bytes, extended.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
