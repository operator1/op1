package com.op1.pack;

import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class ItemSizeComparatorTest {

    private static final Matcher<Integer> EQUALS_ZERO_MATCHER = equalTo(0);
    private static final Matcher<Integer> LESS_THAN_ZERO_MATCHER = lessThan(0);
    private static final Matcher<Integer> GREATER_THAN_ZERO_MATCHER = greaterThan(0);

    @Test
    public void returnsZeroWhenLeftSizeEqualsRightSize() {
        doComparisonTest(new Item("a", 3d), new Item("b", 3d), EQUALS_ZERO_MATCHER);
    }

    @Test
    public void returnsNegativeWhenLeftSizeLessThanRightSize() {
        doComparisonTest(new Item("a", 2.999d), new Item("b", 3d), LESS_THAN_ZERO_MATCHER);
    }

    @Test
    public void returnsPositiveWhenLeftSizeGreaterThanRightSize() {
        doComparisonTest(new Item("a", 3.0000001d), new Item("a", 3d), GREATER_THAN_ZERO_MATCHER);
    }

    private void doComparisonTest(Item left, Item right, Matcher<Integer> matcher) {

        // given
        final ItemSizeComparator comparator = new ItemSizeComparator();

        // when
        final int actual = comparator.compare(left, right);

        // then
        assertThat(actual, matcher);
    }
}
