package org.oagi.score.gateway.http.api.cc_management.data;

public enum CcState {

    Deleted,
    WIP,
    Draft,
    QA,
    Candidate,
    Production,
    ReleaseDraft,
    Published;

    public boolean canMove(CcState to) {
        switch (this) {
            case Deleted:
                return (to == WIP);

            case WIP:
                return (to == Draft || to == QA);

            case Draft:
                return (to == WIP || to == Candidate);

            case QA:
                return (to == WIP || to == Production);

            case Candidate:
                return (to == WIP || to == Draft || to == ReleaseDraft);

            case ReleaseDraft:
                return (to == Candidate || to == Published);

            case Production:
            case Published:
            default:
                return false;
        }
    }

}