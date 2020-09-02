package org.oagi.score.gateway.http.api.common.data;

import org.oagi.score.data.AppUser;
import org.oagi.score.data.BieState;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

public enum AccessPrivilege {

    CanEdit,
    CanView,
    CanMove,
    Prohibited,
    Unprepared;

    public static AccessPrivilege toAccessPrivilege(AppUser requester, AppUser owner, CcState ccState) {
        AccessPrivilege accessPrivilege = Prohibited;
        switch (ccState) {
            case Deleted:
                if (owner.isDeveloper() == requester.isDeveloper()) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;
            case WIP:
                if (requester.getAppUserId().equals(owner.getAppUserId())) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;
            case Draft:
            case QA:
            case Candidate:
            case ReleaseDraft:
                if (requester.getAppUserId().equals(owner.getAppUserId())) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = CanView;
                }
                break;
            case Production:
            case Published:
                if (requester.isDeveloper() == owner.isDeveloper()) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = CanView;
                }
                break;
        }
        return accessPrivilege;
    }

    public static AccessPrivilege toAccessPrivilege(AppUser requester, BigInteger bieOwnerId, BieState bieState) {
        AccessPrivilege accessPrivilege = Prohibited;
        switch (bieState) {
            case Initiating:
                accessPrivilege = Unprepared;
                break;
            case WIP:
                if (requester.getAppUserId().equals(bieOwnerId)) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;
            case QA:
                if (requester.getAppUserId().equals(bieOwnerId)) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = CanView;
                }
                break;
            case Production:
                accessPrivilege = CanView;
                break;
        }
        return accessPrivilege;
    }
}
