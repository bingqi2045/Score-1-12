package org.oagi.srt.gateway.http.release_management;

public enum ReleaseState {

    Draft(1),
    Final(2);

    private final int value;

    ReleaseState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
