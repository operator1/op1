package com.op1.util.cmd;

import com.op1.util.Check;

/**
 * Represents the arguments passed in on the command line.
 */
public class Args {

    private final String[] args;
    private int index = 0;

    public Args(String[] args) {
        this.args = Check.notNull(args, "args cannot be null");
    }

    public String chomp() {
        if (args.length > index) {
            return args[index++];
        }
        throw new ArrayIndexOutOfBoundsException("No args left to chomp. Please use canChomp().");
    }

    public boolean canChomp() {
        return canChomp(1);
    }

    public boolean canChomp(int numArgs) {
        return index + numArgs <= args.length;
    }

    public String peek() {
        if (args.length > index) {
            return args[index];
        }
        throw new ArrayIndexOutOfBoundsException("No args left to peek. Please use canCPeek().");
    }

    public boolean canPeek() {
        return canChomp();
    }
}
