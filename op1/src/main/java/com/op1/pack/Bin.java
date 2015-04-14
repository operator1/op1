package com.op1.pack;

import com.op1.util.Check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Bin represents a container with a capacity. You can fill the bin with Items. You can also check whether there is
 * space for an item before putting it in. A Bin can contain more items than its capacity. When this is the case, the
 * isOverflowing() method returns true.
 *
 * This class is not thread safe.
 */
public class Bin {

    private final double capacity;
    private List<Item> items = new ArrayList<Item>();

    public Bin(double capacity) {
        Check.that(capacity > 0, "Capacity must be non-zero and positive");
        this.capacity = capacity;
    }

    public Bin(double capacity, List<Item> items) {
        this(capacity);
        if (items != null) {
            this.items.addAll(items);
        }
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean canFit(Item item) {
        return (size() + item.getSize()) <= capacity;
    }

    public double size() {
        double size = 0;
        for (Item item : items) {
            size += item.getSize();
        }
        return size;
    }

    public boolean isOverflowing() {
        return size() > capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bin bin = (Bin) o;

        if (Double.compare(bin.capacity, capacity) != 0) return false;
        //noinspection RedundantIfStatement
        if (!items.equals(bin.items)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(capacity);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + items.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Bin{" +
                "capacity=" + capacity +
                ", size=" + size() +
                ", items=" + items +
                '}';
    }
}
