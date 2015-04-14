package com.op1.pack;

import java.util.Comparator;

public class ItemSizeComparator implements Comparator<Item> {

    @Override
    public int compare(Item left, Item right) {

        if (left == null && right == null) {
            return 0;
        } else if (left == null) {
            return -1;
        } else if (right == null) {
            return 1;
        }

        return left.getSize().compareTo(right.getSize());
    }
}
