package com.op1.iff.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Pascal-style string, a one byte count followed by text bytes.
 * The total number of bytes in this data type should be even.
 * A pad byte can be added at the end of the text to accomplish this. This pad byte is not reflected in the count.
 */
public class PString extends DataType {

    private static final Logger LOGGER = LoggerFactory.getLogger(PString.class);

    public PString(byte[] bytes) {
        super(bytes);
        LOGGER.info(String.format("new PString with %s bytes", bytes.length));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PString pString = (PString) o;

        return Arrays.equals(bytes, pString.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
