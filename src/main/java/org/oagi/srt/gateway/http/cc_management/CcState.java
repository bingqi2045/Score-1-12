package org.oagi.srt.gateway.http.cc_management;

public enum CcState {

    Editing(1),
    Candidate(2),
    Published(3);

    private final int value;

    CcState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
