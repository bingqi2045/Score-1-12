package org.oagi.srt.gateway.http.bie_management;

public enum BieState {

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

}
