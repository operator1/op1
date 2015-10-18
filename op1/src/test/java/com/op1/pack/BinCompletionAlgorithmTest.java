package com.op1.pack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class BinCompletionAlgorithmTest {

    @Test
    public void findsSolution() {

        // given
        final int capacity = 100;

        final Item item1 = new Item("a", 83.0);
        final Item item2 = new Item("b", 42.0);
        final Item item3 = new Item("c", 41.0);
        final Item item4 = new Item("d", 40.0);
        final Item item5 = new Item("e", 12.0);
        final Item item6 = new Item("f", 11.0);
        final Item item7 = new Item("g", 5.0);

        final List<Item> items = new ArrayList<>(7);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);

        // when
        final List<Bin> bins = new BinCompletionAlgorithm(capacity, items, 100).packBins();

        // then
        final Bin expectedBin1 = new Bin(capacity, newArrayList(item1, item5, item7));
        final Bin expectedBin2 = new Bin(capacity, newArrayList(item2, item3, item6));
        final Bin expectedBin3 = new Bin(capacity, newArrayList(item4));
        assertThat(bins, hasItems(expectedBin1, expectedBin2, expectedBin3));
    }

    private List<Item> newArrayList(Item... items) {
        final List<Item> list = new ArrayList<>(items.length);
        Collections.addAll(list, items);
        return list;
    }
}
