package org.oagi.srt.gateway.http.api.common.data;

import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

public enum AccessPrivilege {

    CanEdit,
    CanView,
    CanMove,
    Prohibited,
    Unprepared;

    public static AccessPrivilege toAccessPrivilege(AppUser requester, BigInteger ccOwnerId, CcState ccState) {
        AccessPrivilege accessPrivilege = Prohibited;
        switch (ccState) {
            case Deleted:
                if (requester.getAppUserId().equals(ccOwnerId)) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;
            case WIP:
                if (requester.getAppUserId().equals(ccOwnerId)) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;
            case Draft:
            case QA:
            case Candidate:
            case ReleaseDraft:
                if (requester.getAppUserId().equals(ccOwnerId)) {
                    accessPrivilege = CanMove;
                } else {
                    accessPrivilege = CanView;
                }
                break;
            case Production:
            case Published:
                accessPrivilege = CanView;
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
