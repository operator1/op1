package com.op1.iff.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
