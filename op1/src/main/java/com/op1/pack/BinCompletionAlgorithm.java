package com.op1.pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BinCompletionAlgorithm {

    private final double capacity;
    private final List<Item> items = new ArrayList<Item>();

    private static final Comparator<Item> DECREASING_SIZE_ORDER = Collections.reverseOrder(new ItemSizeComparator());

    public BinCompletionAlgorithm(double capacity, List<Item> items) {
        this.capacity = capacity;
        this.items.addAll(items);
        Collections.sort(this.items, DECREASING_SIZE_ORDER);
    }

    public List<Bin> packBins() {

        final ArrayList<Bin> maximalFeasibleAssignments = new ArrayList<Bin>();
        pack(maximalFeasibleAssignments, new ArrayList<Item>(items));
        return maximalFeasibleAssignments;
    }

    private void pack(List<Bin> maximalFeasibleAssignments, List<Item> items) {

        List<Item> remainingItems = new ArrayList<Item>(items.size());

        final Bin bin = new Bin(capacity);
        for (Item item : items) {
            if (bin.canFit(item)) {
                bin.addItem(item);
            } else {
                remainingItems.add(item);
            }
        }
        maximalFeasibleAssignments.add(bin);

        if (!remainingItems.isEmpty()) {
            pack(maximalFeasibleAssignments, remainingItems);
        }
    }

}
