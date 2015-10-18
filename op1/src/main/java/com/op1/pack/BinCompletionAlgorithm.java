package com.op1.pack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BinCompletionAlgorithm {

    private final double capacity;
    private final int maxItemsPerBin;
    private final List<Item> items = new ArrayList<>();

    private static final Comparator<Item> DECREASING_SIZE_ORDER = Collections.reverseOrder(new ItemSizeComparator());

    public BinCompletionAlgorithm(double capacity, List<Item> items, int maxItemsPerBin) {
        this.capacity = capacity;
        this.items.addAll(items);
        Collections.sort(this.items, DECREASING_SIZE_ORDER);
        this.maxItemsPerBin = maxItemsPerBin;
    }

    public List<Bin> packBins() {

        final ArrayList<Bin> maximalFeasibleAssignments = new ArrayList<>();
        pack(maximalFeasibleAssignments, new ArrayList<>(items));
        return maximalFeasibleAssignments;
    }

    private void pack(List<Bin> maximalFeasibleAssignments, List<Item> items) {

        List<Item> remainingItems = new ArrayList<>(items.size());

        final Bin bin = new Bin(capacity);
        for (Item item : items) {

            // TODO: I added a second part to the if statement: && bin.size() < maxItemsPerBin
            // Would be good to have a think about the impact that this has on the algorithm.

            if (bin.canFit(item) && bin.getItems().size() < maxItemsPerBin) {
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
