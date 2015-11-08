package com.op1.iff.types;

import java.util.Arrays;

/**
 * 32 bits, the concatenation of four printable ASCII character in the range ' ' (SP, 0x20) through '~' (0x7E).
 * Spaces (0x20) cannot precede printing characters; trailing spaces are allowed.
 * Control characters are forbidden.
 */
public class ID extends DataType {

    private final String name;

    public ID(byte[] bytes) {
        // TODO: ahem, validation?
        super(bytes, 4);
        this.name = new String(bytes);
    }

    public static ID valueOf(String s) {
        return new ID(s.getBytes());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ID id = (ID) o;

        return Arrays.equals(bytes, id.bytes);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
