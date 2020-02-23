package org.oagi.srt.data;

public enum BieState {

    Initiating(1),
    WIP(2),
    QA(3),
    Production(4);

    private final int value;

    BieState(int value) {
        this.value = value;
    }

    public static BieState valueOf(int value) {
        for (BieState state : BieState.values()) {
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
