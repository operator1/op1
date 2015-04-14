package com.op1.pack;

import com.op1.util.Check;

/**
 * An immutable holder of a name and a size. Size must be non-zero and positive.
 */
public class Item {

    private final String name;
    private final Double size;

    public Item(String name, Double size) {
        Check.that(name != null && name.trim().length() > 0, "Must have a name");
        Check.that(size != null && size > 0, "Size must be non-zero and positive");
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public Double getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!name.equals(item.name)) return false;
        //noinspection RedundantIfStatement
        if (!size.equals(item.size)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
