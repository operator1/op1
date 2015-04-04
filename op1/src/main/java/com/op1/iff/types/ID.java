package com.op1.iff.types;

/**
 * 32 bits, the concatenation of four printable ASCII character in the range ' ' (SP, 0x20) through '~' (0x7E).
 * Spaces (0x20) cannot precede printing characters; trailing spaces are allowed.
 * Control characters are forbidden.
 */
public class ID extends DataType {

    private final String name;

    public ID(byte[] bytes) {
        super(bytes);
        this.name = new String(bytes);
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

        ID id = (ID) o;

        //noinspection RedundantIfStatement
        if (!name.equals(id.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
