package com.op1.pack;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ItemTest {

    @Test(expected = IllegalArgumentException.class)
    public void cannotConstructWithEmptyName() {
        new Item(" ", (double) 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotConstructWithNullName() {
        new Item(null, (double) 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotConstructWithZeroSize() {
        new Item(null, (double) 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotConstructWithNegativeSize() {
        new Item(null, (double) -1);
    }

    @Test
    public void nameGetterReturnsName() {

        // given
        String expected = "a";
        Item item = new Item(expected, (double) 1);

        // when
        String actual = item.getName();

        // then
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void sizeGetterReturnsSize() {

        // given
        double expected = 1d;
        Item item = new Item("name", expected);

        // when
        double actual = item.getSize();

        // then
        assertThat(actual, equalTo(expected));
    }
}
