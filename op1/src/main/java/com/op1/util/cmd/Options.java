package com.op1.util.cmd;

import java.util.HashMap;
import java.util.Map;

public class Options {

    private final Map<String, String> options = new HashMap<>();

    Options(Map<String, String> options) {
        this.options.putAll(options);
    }

    public <T> T getValue(String optionName, Validator<T> validator) {
        try {
            return validator.validate(options.get(optionName));
        } catch (ValidationException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public <T> T getValue(String optionName, Validator<T> validator, T defaultValue) {
        if (!hasOption(optionName)) {
            return defaultValue;
        } else {
            return getValue(optionName, validator);
        }
    }

    public boolean hasOption(String optionName) {
        return options.containsKey(optionName);
    }
}
