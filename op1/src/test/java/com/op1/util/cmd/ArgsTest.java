package com.op1.util.cmd;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ArgsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void cannot_construct_with_null_array() throws Exception {

        // given
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("args cannot be null");

        // when
        new Args(null);

        // then boom!
    }

    @Test
    public void canChompExpectedNumberOfTimes() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        args.chomp();
        args.chomp();
        args.chomp();

        // then
        assertThat(args.canChomp(), is(false));
    }

    @Test
    public void cannotChompMoreThanExpectedNumberOfTimes() throws Exception {

        // given
        expectedException.expect(ArrayIndexOutOfBoundsException.class);
        expectedException.expectMessage("No args left to chomp. Please use canChomp().");
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        args.chomp();
        args.chomp();
        args.chomp();
        args.chomp();

        // then boom!
    }

    @Test
    public void canChomp() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        final String arg = args.chomp();

        // then
        assertThat(arg, equalTo("1"));
    }

    @Test
    public void canPeekAndThenChomp() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        final String peekedArg = args.peek();
        final String chompedArg = args.chomp();

        // then
        assertThat(peekedArg, equalTo("1"));
        assertThat(peekedArg, equalTo(chompedArg));
    }

    @Test
    public void canChompReturnsFalseWhenThereAreNoArgsLeft() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        args.chomp();
        args.chomp();
        args.chomp();
        final boolean canChomp = args.canChomp();

        // then
        assertThat(canChomp, equalTo(false));
    }

    @Test
    public void canChompReturnsTrueWhenThereAreArgsLeft() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        args.chomp();
        args.chomp();
        final boolean canChomp = args.canChomp();

        // then
        assertThat(canChomp, equalTo(true));
    }

    @Test
    public void canChompNumReturnsTrueWhenThereAreNumArgsLeft() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        final boolean canChomp = args.canChomp(3);

        // then
        assertThat(canChomp, equalTo(true));
    }

    @Test
    public void canChompNumReturnsFalseWhenThereAreNotNumArgsLeft() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        final boolean canChomp = args.canChomp(4);

        // then
        assertThat(canChomp, equalTo(false));
    }

    @Test
    public void canPeekReturnsFalseWhenThereAreNoArgsLeft() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        args.chomp();
        args.chomp();
        args.chomp();
        final boolean canPeek = args.canPeek();

        // then
        assertThat(canPeek, equalTo(false));
    }

    @Test
    public void canPeekReturnsTrueWhenThereAreArgsLeft() throws Exception {

        // given
        final Args args = new Args(new String[]{"1", "2", "3"});

        // when
        args.chomp();
        args.chomp();
        final boolean canPeek = args.canPeek();

        // then
        assertThat(canPeek, equalTo(true));
    }
}