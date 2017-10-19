package com.aroraaman.fitbitchallenge.model;

public class Row {
    public final String commandTye;               // "Absolute" or "Relative"
    public final int cRValue, cGValue, cBValue;   // command RGB value
    public final int fRValue, fGValue, fBValue;   // final RGB value after command


    public Row(String commandTye, int cRValue, int cGValue, int cBValue, int fRValue, int fGValue, int fBValue) {
        this.commandTye = commandTye;
        this.cRValue = cRValue;
        this.cGValue = cGValue;
        this.cBValue = cBValue;
        this.fRValue = fRValue;
        this.fGValue = fGValue;
        this.fBValue = fBValue;
    }

    @Override
    public String toString() {
        return commandTye + "(" + cRValue + ", " +
                                + cGValue + ", " +
                                + cBValue + ")"  +
                "\nFinal Color RGB = " +
                    "(" + fRValue + ", " +
                        + fGValue + ", " +
                        + fBValue + ")";
    }
}
