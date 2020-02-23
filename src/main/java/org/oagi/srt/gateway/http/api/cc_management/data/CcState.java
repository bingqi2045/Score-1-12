package org.oagi.srt.gateway.http.api.cc_management.data;

public enum CcState {

    WIP(1),
    Draft(2),
    QA(3),
    Candidate(4),
    Production(5),
    ReleaseDraft(6),
    Published(7);

    private final int value;

    CcState(int value) {
        this.value = value;
    }

    public static CcState valueOf(int value) {
        for (CcState state : CcState.values()) {
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
