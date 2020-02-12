package org.oagi.srt.data;

public enum ReleaseState {

    Draft(1),
    Published(2);

    private final int value;

    ReleaseState(int value) {
        this.value = value;
    }

    public static ReleaseState valueOf(int value) {
        for (ReleaseState state : ReleaseState.values()) {
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
