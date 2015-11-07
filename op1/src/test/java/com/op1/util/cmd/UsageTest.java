package com.op1.util.cmd;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class UsageTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsageTest.class);

    @Test
    public void toStringContainsExpectedText() throws Exception {

        // given
        final Usage usage = Usage.newBuilder()
                .programName("java -jar op1.jar")
                .mandatory("-source", "the root folder containing samples")
                .mandatory("-target", "the target folder to write drumkits to")
                .optional("-basename", "the base name of the drumkit (numbers will be appended to this name)")
                .optional("-maxLength", "the maximum length of a sample to include in a kit").build();

        // when
        final String usageString = usage.toString();

        // then
        assertThat(usageString, equalTo(System.lineSeparator() +
                "java -jar op1.jar -source <source> -target <target> [options]" + System.lineSeparator() +
                "    -source : the root folder containing samples" + System.lineSeparator() +
                "    -target : the target folder to write drumkits to" + System.lineSeparator() +
                "    -basename : the base name of the drumkit (numbers will be appended to this name)" + System.lineSeparator() +
                "    -maxLength : the maximum length of a sample to include in a kit" + System.lineSeparator() +"    "));
    }

    @Test
    public void canParseArgs() throws Exception {

        // given
        final Usage usage = Usage.newBuilder()
                .programName("java -jar op1.jar")
                .mandatory("-source", "the root folder containing samples")
                .mandatory("-target", "the target folder to write drumkits to")
                .optional("-basename", "the base name of the drumkit (numbers will be appended to this name)")
                .optional("-maxLength", "the maximum length of a sample to include in a kit").build();

        final Args args = new Args(new String[]{
                "-source", "C:/Temp/source",
                "-target", "C:/Temp/target",
                "-basename", "kit",
        });

        // when
        final Options options = usage.parse(args);

        // then
//        assertThat(options, Matchers.hasEntry("-source", "C:/Temp/source"));
//        assertThat(options, Matchers.hasEntry("-target", "C:/Temp/target"));
//        assertThat(options, Matchers.hasEntry("-basename", "kit"));
    }
}