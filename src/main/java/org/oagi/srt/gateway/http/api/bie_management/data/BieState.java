package org.oagi.srt.gateway.http.api.bie_management.data;

public enum BieState {

    Instantiating(1),
    Editing(2),
    Candidate(3),
    Published(4);

    private final int value;

    BieState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BieState valueOf(int value) {
        for (BieState state : BieState.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
    
}
