package com.op1.util;

import java.util.List;

public class AiffNotCompliantException extends Exception {

    public AiffNotCompliantException(List<String> problems) {
        super(concatenate(problems));
    }

    private static String concatenate(List<String> problems) {
        StringBuilder builder = new StringBuilder();
        for (String problem : problems) {
            builder.append(problem).append(System.lineSeparator());
        }
        return builder.toString();
    }
}
