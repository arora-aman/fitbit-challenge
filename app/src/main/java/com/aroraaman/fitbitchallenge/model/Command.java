package com.aroraaman.fitbitchallenge.model;

public enum Command {
    RELATIVE(1, 3, 16),
    ABSOLUTE(2, 3, 8);

    public final int INT_VALUE;
    public final int ARG_COUNT;
    public final int ARG_BITS;

    Command(int value, int argCount, int argBits) {
        INT_VALUE = value;
        ARG_COUNT = argCount;
        ARG_BITS = argBits;
    }
}