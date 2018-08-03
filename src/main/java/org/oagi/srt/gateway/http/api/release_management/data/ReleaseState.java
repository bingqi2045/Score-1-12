package org.oagi.srt.gateway.http.api.release_management.data;

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
