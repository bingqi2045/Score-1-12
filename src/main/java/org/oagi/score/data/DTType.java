package org.oagi.score.data;

public enum DTType {

    CDT(0),
    BDT(1);

    private final int value;

    DTType(int value) {
        this.value = value;
    }

    public static DTType valueOf(int value) {
        for (DTType state : DTType.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public int getValue() {
        return value;
    }
}
