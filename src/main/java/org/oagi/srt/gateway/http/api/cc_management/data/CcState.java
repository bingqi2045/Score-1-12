package org.oagi.srt.gateway.http.api.cc_management.data;

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
