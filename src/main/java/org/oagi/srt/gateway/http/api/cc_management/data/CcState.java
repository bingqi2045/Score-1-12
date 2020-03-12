package org.oagi.srt.gateway.http.api.cc_management.data;

public enum CcState implements Comparable<CcState> {

    Deleted,
    WIP,
    Draft,
    QA,
    Candidate,
    Production,
    ReleaseDraft,
    Published;

}
