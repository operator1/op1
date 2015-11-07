package com.op1.util.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Usage {

    private String programName;
    private List<Option> mandatoryDefs = new ArrayList<>();
    private List<Option> optionalDefs = new ArrayList<>();

    private static final String TAB = "    ";

    private Usage() {
    }

    public Usage(Usage usage) {
        this.mandatoryDefs.addAll(usage.mandatoryDefs);
        this.optionalDefs.addAll(usage.optionalDefs);
        this.programName = usage.programName;
    }

    public Options parse(Args args) {

        final Map<String, String> options = new HashMap<>();

        while (args.canChomp(2)) {
            final String optionName = args.chomp();
            final String optionValue = args.chomp();
            options.put(optionName, optionValue);
        }

        checkMandatoryOptions(options);
        checkNoUndefinedOptions(options);

        return new Options(options);
    }

    private void checkNoUndefinedOptions(Map<String, String> options) {
        final Predicate<String> filter = optionName -> !isMandatoryOptionName(optionName) && !isOptionalOptionName(optionName);
        final Consumer<String> action = optionName -> {
            System.err.println("Do not recognize option: " + optionName);
            printUsageAndExit();
        };
        options.keySet().stream().filter(filter).forEach(action);
    }

    private boolean isMandatoryOptionName(String optionName) {
        return containsOptionName(mandatoryDefs, optionName);
    }

    private boolean isOptionalOptionName(String optionName) {
        return containsOptionName(optionalDefs, optionName);
    }

    private boolean containsOptionName(List<Option> options, String optionName) {
        for (Option def : options) {
            if (def.getName().equals(optionName)) {
                return true;
            }
        }
        return false;
    }

    private void checkMandatoryOptions(Map<String, String> options) {
        mandatoryDefs.stream().filter(def -> !options.containsKey(def.getName())).forEach(def -> {
            System.err.println("Missing mandatory argument: " + def.getName());
            printUsageAndExit();
        });
    }

    private void printUsageAndExit() {
        System.err.println(this.toString());
        System.exit(1);
    }

    @Override
    public String toString() {

        final StringBuilder buf = new StringBuilder(System.lineSeparator());
        buf.append(programName).append(" ");

        for (Option def : mandatoryDefs) {
            buf.append(def.getName()).append(" ").append(angularBracketsName(def.getName())).append(" ");
        }

        buf.append("[options]").append(System.lineSeparator()).append(TAB);

        for (Option def : mandatoryDefs) {
            buf.append(def.getName()).append(" : ").append(def.getHelp());
            buf.append(System.lineSeparator()).append(TAB);
        }
        for (Option def : optionalDefs) {
            buf.append(def.getName()).append(" : ").append(def.getHelp());
            buf.append(System.lineSeparator()).append(TAB);
        }

        return buf.toString();
    }

    private static String angularBracketsName(String name) {
        if (name.startsWith("-")) {
            return "<" + name.substring(1, name.length()) + ">";
        } else {
            return "<" + name.substring(0, name.length()) + ">";
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private final Usage instance = new Usage();

        private Builder() {}

        public Builder programName(String programName) {
            instance.programName = programName;
            return this;
        }

        public Builder mandatory(String name, String help) {
            instance.mandatoryDefs.add(new Option(name, help));
            return this;
        }

        public Builder optional(String name, String help) {
            instance.optionalDefs.add(new Option(name, help));
            return this;
        }

        public Usage build() {
            return new Usage(instance);
        }
    }

    /**
     * The definition of an option.
     */
    public static class Option {

        private final String name;
        private final String help;

        public Option(String name, String help) {
            this.name = name;
            this.help = help;
        }

        public String getName() {
            return name;
        }

        public String getHelp() {
            return help;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Option option = (Option) o;

            return !(name != null ? !name.equals(option.name) : option.name != null);

        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }
}
