package com.op1.pack;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class BinTest {

    @Test(expected = IllegalArgumentException.class)
    public void cannotConstuctWithZeroCapacity() {
        new Bin(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotConstuctWithNegativeCapacity() {
        new Bin(-1);
    }

    @Test
    public void canConstructWithNullList() {

        // given
        final Bin bin = new Bin(1, null);

        // when
        final List<Item> items = bin.getItems();

        // then
        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(0));
    }

    @Test
    public void itemsGetterReturnsItems() {

        // given
        final Item item1 = new Item("a", 1d);
        final Item item2 = new Item("b", 2d);
        final Item item3 = new Item("c", 3d);
        final Bin bin = new Bin(10);
        bin.addItem(item1);
        bin.addItem(item2);
        bin.addItem(item3);

        // when
        final List<Item> items = bin.getItems();

        // then
        assertThat(items, notNullValue());
        assertThat(items, Matchers.hasItems(item1, item2, item3));
    }

    @Test
    public void binsWithSameItemsInOrderAreEqual() {

        // given
        final List<Item> list = new ArrayList<Item>(3);
        final Item item1 = new Item("a", 1d);
        final Item item2 = new Item("b", 2d);
        final Item item3 = new Item("c", 3d);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        final Bin bin1 = new Bin(10, list);
        final Bin bin2 = new Bin(10, list);

        // when
        final boolean binsAreEqual = bin1.equals(bin2);

        // then
        assertThat(binsAreEqual, is(true));
    }

    @Test
    public void binsWithSameItemsOutOfOrderAreNotEqual() {

        // given
        final Item item1 = new Item("a", 1d);
        final Item item2 = new Item("b", 2d);
        final Item item3 = new Item("c", 3d);
        final List<Item> list1 = new ArrayList<Item>(3);
        final List<Item> list2 = new ArrayList<Item>(3);
        list1.add(item1);
        list1.add(item2);
        list1.add(item3);
        list2.add(item2);
        list2.add(item3);
        list2.add(item1);
        final Bin bin1 = new Bin(10, list1);
        final Bin bin2 = new Bin(10, list2);

        // when
        final boolean binsAreEqual = bin1.equals(bin2);

        // then
        assertThat(binsAreEqual, is(false));
    }

    @Test
    public void binsWithDifferentItemsAreNotEqual() {

        // given
        final Item item1 = new Item("a", 1d);
        final Item item2 = new Item("b", 2d);
        final Item item3 = new Item("c", 3d);
        final List<Item> list1 = new ArrayList<Item>(2);
        final List<Item> list2 = new ArrayList<Item>(1);
        list1.add(item1);
        list1.add(item2);
        list2.add(item3);
        final Bin bin1 = new Bin(10, list1);
        final Bin bin2 = new Bin(10, list2);

        // when
        final boolean binsAreEqual = bin1.equals(bin2);

        // then
        assertThat(binsAreEqual, is(false));
    }

    @Test
    public void isOverflowingReturnsTrueWhenBinSizeGreaterThanCapacity() {

        // given
        final Bin bin = new Bin(10);
        bin.addItem(new Item("a", 5d));
        bin.addItem(new Item("b", 7d));

        // when
        final boolean overflowing = bin.isOverflowing();

        // then
        assertThat(overflowing, is(true));
    }

    @Test
    public void isOverflowingReturnsFalseWhenBinSizeLessThanCapacity() {

        // given
        final Bin bin = new Bin(10);
        bin.addItem(new Item("a", 5d));
        bin.addItem(new Item("b", 3d));

        // when
        final boolean overflowing = bin.isOverflowing();

        // then
        assertThat(overflowing, is(false));
    }

    @Test
    public void isOverflowingReturnsFalseWhenBinSizeEqualsCapacity() {

        // given
        final Bin bin = new Bin(10);
        bin.addItem(new Item("a", 5d));
        bin.addItem(new Item("b", 5d));

        // when
        final boolean overflowing = bin.isOverflowing();

        // then
        assertThat(overflowing, is(false));
    }

}
