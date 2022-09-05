package org.oagi.score.gateway.http.api.release_management.service;

import org.jooq.DSLContext;
import org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.service.common.data.CcState;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageCode.NAMESPACE;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageCode.*;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageLevel.Error;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageLevel.Warning;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

public class ReleaseValidator {

    private final DSLContext dslContext;

    private List<String> assignedAccComponentManifestIds = Collections.emptyList();
    private List<String> assignedAsccpComponentManifestIds = Collections.emptyList();
    private List<String> assignedBccpComponentManifestIds = Collections.emptyList();
    private List<String> assignedCodeListComponentManifestIds = Collections.emptyList();
    private List<String> assignedAgencyIdListComponentManifestIds = Collections.emptyList();
    private List<String> assignedDtComponentManifestIds = Collections.emptyList();

    private List<AccManifestRecord> accManifestRecords;
    private Map<String, AccManifestRecord> accManifestRecordMap;
    private List<AccRecord> accRecords;
    private Map<String, AccRecord> accRecordMap;

    private List<AsccManifestRecord> asccManifestRecords;
    private List<AsccRecord> asccRecords;
    private Map<String, AsccRecord> asccRecordMap;

    private List<BccManifestRecord> bccManifestRecords;
    private List<BccRecord> bccRecords;
    private Map<String, BccRecord> bccRecordMap;

    private List<AsccpManifestRecord> asccpManifestRecords;
    private Map<String, AsccpManifestRecord> asccpManifestRecordMap;
    private List<AsccpRecord> asccpRecords;
    private Map<String, AsccpRecord> asccpRecordMap;

    private List<BccpManifestRecord> bccpManifestRecords;
    private Map<String, BccpManifestRecord> bccpManifestRecordMap;
    private List<BccpRecord> bccpRecords;
    private Map<String, BccpRecord> bccpRecordMap;

    private List<CodeListManifestRecord> codeListManifestRecords;
    private Map<String, CodeListManifestRecord> codeListManifestRecordMap;
    private List<CodeListRecord> codeListRecords;
    private Map<String, CodeListRecord> codeListRecordMap;

    private List<AgencyIdListManifestRecord> agencyIdListManifestRecords;
    private Map<String, AgencyIdListManifestRecord> agencyIdListManifestRecordMap;
    private List<AgencyIdListRecord> agencyIdListRecords;
    private Map<String, AgencyIdListRecord> agencyIdListRecordMap;

    private List<DtManifestRecord> dtManifestRecords;
    private Map<String, DtManifestRecord> dtManifestRecordMap;
    private List<DtRecord> dtRecords;
    private Map<String, DtRecord> dtRecordMap;

    public ReleaseValidator(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void setAssignedAccComponentManifestIds(List<String> assignedAccComponentManifestIds) {
        this.assignedAccComponentManifestIds = assignedAccComponentManifestIds;
    }

    public void setAssignedAsccpComponentManifestIds(List<String> assignedAsccpComponentManifestIds) {
        this.assignedAsccpComponentManifestIds = assignedAsccpComponentManifestIds;
    }

    public void setAssignedBccpComponentManifestIds(List<String> assignedBccpComponentManifestIds) {
        this.assignedBccpComponentManifestIds = assignedBccpComponentManifestIds;
    }

    public void setAssignedCodeListComponentManifestIds(List<String> assignedCodeListComponentManifestIds) {
        this.assignedCodeListComponentManifestIds = assignedCodeListComponentManifestIds;
    }

    public void setAssignedDtComponentManifestIds(List<String> assignedDtComponentManifestIds) {
        this.assignedDtComponentManifestIds = assignedDtComponentManifestIds;
    }

    public ReleaseValidationResponse validate() {
        loadManifests();

        ReleaseValidationResponse response = new ReleaseValidationResponse();

        validateAcc(response);
        validateAsccp(response);
        validateBccp(response);
        validateCodeList(response);
        validateAgencyIdList(response);
        validateDt(response);

        return response;
    }

    private void loadManifests() {
        accManifestRecords = dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST)
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AccManifestRecord.class);
        accManifestRecordMap = accManifestRecords.stream()
                .collect(Collectors.toMap(AccManifestRecord::getAccManifestId, Function.identity()));

        accRecords = dslContext.select(ACC.fields())
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AccRecord.class);
        accRecordMap = accRecords.stream()
                .collect(Collectors.toMap(AccRecord::getAccId, Function.identity()));

        asccManifestRecords = dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AsccManifestRecord.class);

        asccRecords = dslContext.select(ASCC.fields())
                .from(ASCC)
                .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AsccRecord.class);

        bccManifestRecords = dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(BccManifestRecord.class);

        bccRecords = dslContext.select(BCC.fields())
                .from(BCC)
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(BccRecord.class);

        asccpManifestRecords = dslContext.select(ASCCP_MANIFEST.fields())
                .from(ASCCP_MANIFEST)
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AsccpManifestRecord.class);
        asccpManifestRecordMap = asccpManifestRecords.stream()
                .collect(Collectors.toMap(AsccpManifestRecord::getAsccpManifestId, Function.identity()));

        asccpRecords = dslContext.select(ASCCP.fields())
                .from(ASCCP)
                .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AsccpRecord.class);
        asccpRecordMap = asccpRecords.stream()
                .collect(Collectors.toMap(AsccpRecord::getAsccpId, Function.identity()));

        bccpManifestRecords = dslContext.select(BCCP_MANIFEST.fields())
                .from(BCCP_MANIFEST)
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(BccpManifestRecord.class);
        bccpManifestRecordMap = bccpManifestRecords.stream()
                .collect(Collectors.toMap(BccpManifestRecord::getBccpManifestId, Function.identity()));

        bccpRecords = dslContext.select(BCCP.fields())
                .from(BCCP)
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(BccpRecord.class);
        bccpRecordMap = bccpRecords.stream().collect(Collectors.toMap(BccpRecord::getBccpId, Function.identity()));

        codeListManifestRecords = dslContext.select(CODE_LIST_MANIFEST.fields())
                .from(CODE_LIST_MANIFEST)
                .join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(CodeListManifestRecord.class);
        codeListManifestRecordMap = codeListManifestRecords.stream()
                .collect(Collectors.toMap(CodeListManifestRecord::getCodeListManifestId, Function.identity()));

        codeListRecords = dslContext.select(CODE_LIST.fields())
                .from(CODE_LIST)
                .join(CODE_LIST_MANIFEST).on(CODE_LIST.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID))
                .join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(CodeListRecord.class);
        codeListRecordMap = codeListRecords.stream().collect(Collectors.toMap(CodeListRecord::getCodeListId, Function.identity()));

        agencyIdListManifestRecords = dslContext.select(AGENCY_ID_LIST_MANIFEST.fields())
                .from(AGENCY_ID_LIST_MANIFEST)
                .join(RELEASE).on(AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AgencyIdListManifestRecord.class);
        agencyIdListManifestRecordMap = agencyIdListManifestRecords.stream()
                .collect(Collectors.toMap(AgencyIdListManifestRecord::getAgencyIdListManifestId, Function.identity()));

        agencyIdListRecords = dslContext.select(AGENCY_ID_LIST.fields())
                .from(AGENCY_ID_LIST)
                .join(AGENCY_ID_LIST_MANIFEST).on(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID))
                .join(RELEASE).on(AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(AgencyIdListRecord.class);
        agencyIdListRecordMap = agencyIdListRecords.stream().collect(Collectors.toMap(AgencyIdListRecord::getAgencyIdListId, Function.identity()));

        dtManifestRecords = dslContext.select(DT_MANIFEST.fields())
                .from(DT_MANIFEST)
                .join(RELEASE).on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(DtManifestRecord.class);
        dtManifestRecordMap = dtManifestRecords.stream()
                .collect(Collectors.toMap(DtManifestRecord::getDtManifestId, Function.identity()));

        dtRecords = dslContext.select(DT.fields())
                .from(DT)
                .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .join(RELEASE).on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchInto(DtRecord.class);
        dtRecordMap = dtRecords.stream().collect(Collectors.toMap(DtRecord::getDtId, Function.identity()));
    }

    private void validateAcc(ReleaseValidationResponse response) {
        for (AccManifestRecord accManifestRecord : accManifestRecords) {
            String accId = accManifestRecord.getAccId();
            AccRecord accRecord = accRecordMap.get(accId);
            CcState state = CcState.valueOf(accRecord.getState());
            if (state != CcState.Published && !assignedAccComponentManifestIds.contains(
                    accManifestRecord.getAccManifestId())) {
                continue;
            }
            if (accRecord.getNamespaceId() == null) {
                response.addMessageForAcc(accManifestRecord.getAccManifestId(),
                        Error, "Namespace is required.", NAMESPACE);
            }

            // check ACCs whose `basedACC` is this acc.
            accManifestRecords.stream().filter(e -> e.getAccManifestId().equals(accManifestRecord.getBasedAccManifestId()))
                    .forEach(basedAccManifestRecord -> {
                        AccRecord basedAcc = accRecordMap.get(basedAccManifestRecord.getAccId());
                        CcState basedAccstate = CcState.valueOf(basedAcc.getState());
                        if (basedAccstate != CcState.Published) {
                            if (assignedAccComponentManifestIds.contains(basedAccManifestRecord.getAccManifestId())) {
                                if (basedAccstate != CcState.Candidate) {
                                    response.addMessageForAcc(basedAccManifestRecord.getAccManifestId(),
                                            Error, "'" + basedAcc.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ACC_BasedACC);
                                }
                            } else {
                                if (basedAccManifestRecord.getPrevAccManifestId() == null) {
                                    response.addMessageForAcc(basedAccManifestRecord.getAccManifestId(),
                                            Error, "'" + basedAcc.getDen() + "' is needed in the release assignment due to '" + accRecord.getDen() + "'.",
                                            ACC_BasedACC);
                                } else {
                                    response.addMessageForAcc(basedAccManifestRecord.getAccManifestId(),
                                            Warning, "'" + basedAcc.getDen() + "' has been revised but not included in the release assignment.",
                                            ACC_BasedACC);
                                }
                            }
                        }
                    });

            // check ASCCs
            asccManifestRecords.stream().filter(e -> e.getFromAccManifestId().equals(accManifestRecord.getAccManifestId()))
                    .forEach(asccManifestRecord -> {
                        AsccpManifestRecord asccpManifestRecord = asccpManifestRecordMap.get(asccManifestRecord.getToAsccpManifestId());
                        AsccpRecord asccpRecord = asccpRecordMap.get(asccpManifestRecord.getAsccpId());
                        CcState asccpState = CcState.valueOf(asccpRecord.getState());
                        if (asccpState != CcState.Published) {
                            if (assignedAsccpComponentManifestIds.contains(asccpManifestRecord.getAsccpManifestId())) {
                                if (asccpState != CcState.Candidate) {
                                    response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId(),
                                            Error, "'" + asccpRecord.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ACC_Association);
                                }
                            } else {
                                if (asccpManifestRecord.getPrevAsccpManifestId() == null) {
                                    response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId(),
                                            Error, "'" + asccpRecord.getDen() + "' is needed in the release assignment due to '" + accRecord.getDen() + "'.",
                                            ACC_Association);
                                } else {
                                    response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId(),
                                            Warning, "'" + asccpRecord.getDen() + "' has been revised but not included in the release assignment.",
                                            ACC_Association);
                                }
                            }
                        }
                    });

            // check BCCs
            bccManifestRecords.stream().filter(e -> e.getFromAccManifestId().equals(accManifestRecord.getAccManifestId()))
                    .forEach(bccManifestRecord -> {
                        BccpManifestRecord bccpManifestRecord = bccpManifestRecordMap.get(bccManifestRecord.getToBccpManifestId());
                        BccpRecord bccpRecord = bccpRecordMap.get(bccpManifestRecord.getBccpId());
                        CcState bccpState = CcState.valueOf(bccpRecord.getState());
                        if (bccpState != CcState.Published) {
                            if (assignedBccpComponentManifestIds.contains(bccpManifestRecord.getBccpManifestId())) {
                                if (bccpState != CcState.Candidate) {
                                    response.addMessageForBccp(bccpManifestRecord.getBccpManifestId(),
                                            Error, "'" + bccpRecord.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ACC_Association);
                                }
                            } else {
                                if (bccpManifestRecord.getPrevBccpManifestId() == null) {
                                    response.addMessageForBccp(bccpManifestRecord.getBccpManifestId(),
                                            Error, "'" + bccpRecord.getDen() + "' is needed in the release assignment due to '" + accRecord.getDen() + "'.",
                                            ACC_Association);
                                } else {
                                    response.addMessageForBccp(bccpManifestRecord.getBccpManifestId(),
                                            Warning, "'" + bccpRecord.getDen() + "' has been revised but not included in the release assignment.",
                                            ACC_Association);
                                }
                            }
                        }
                    });
        }
    }

    private void validateAsccp(ReleaseValidationResponse response) {
        for (AsccpManifestRecord asccpManifestRecord : asccpManifestRecords) {
            String asccpId = asccpManifestRecord.getAsccpId();
            AsccpRecord asccpRecord = asccpRecordMap.get(asccpId);
            CcState state = CcState.valueOf(asccpRecord.getState());
            if (state != CcState.Published && !assignedAsccpComponentManifestIds.contains(
                    asccpManifestRecord.getAsccpManifestId())) {
                continue;
            }
            if (asccpRecord.getNamespaceId() == null) {
                response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId(),
                        Error, "Namespace is required.", NAMESPACE);
            }

            // check ACCs whose `roleOfAcc` is this acc.
            accManifestRecords.stream().filter(e -> e.getAccManifestId().equals(asccpManifestRecord.getRoleOfAccManifestId()))
                    .forEach(accManifestRecord -> {
                        AccRecord accRecord = accRecordMap.get(accManifestRecord.getAccId());
                        CcState accState = CcState.valueOf(accRecord.getState());
                        if (accState != CcState.Published) {
                            if (assignedAccComponentManifestIds.contains(accManifestRecord.getAccManifestId())) {
                                if (accState != CcState.Candidate) {
                                    response.addMessageForAcc(accManifestRecord.getAccManifestId(),
                                            Error, "'" + accRecord.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ASCCP_RoleOfAcc);
                                }
                            } else {
                                if (accManifestRecord.getPrevAccManifestId() == null) {
                                    response.addMessageForAcc(accManifestRecord.getAccManifestId(),
                                            Error, "'" + accRecord.getDen() + "' is needed in the release assignment due to '" + asccpRecord.getDen() + "'.",
                                            ASCCP_RoleOfAcc);
                                } else {
                                    response.addMessageForAcc(accManifestRecord.getAccManifestId(),
                                            Warning, "'" + accRecord.getDen() + "' has been revised but not included in the release assignment.",
                                            ASCCP_RoleOfAcc);
                                }
                            }
                        }
                    });
        }
    }

    private void validateBccp(ReleaseValidationResponse response) {
        for (BccpManifestRecord bccpManifestRecord : bccpManifestRecords) {
            String bccpId = bccpManifestRecord.getBccpId();
            BccpRecord bccpRecord = bccpRecordMap.get(bccpId);
            CcState state = CcState.valueOf(bccpRecord.getState());
            if (state != CcState.Published && !assignedBccpComponentManifestIds.contains(
                    bccpManifestRecord.getBccpManifestId())) {
                continue;
            }
            if (bccpRecord.getNamespaceId() == null) {
                response.addMessageForBccp(bccpManifestRecord.getBccpManifestId(),
                        Error, "Namespace is required.", NAMESPACE);
            }
        }
    }

    private void validateCodeList(ReleaseValidationResponse response) {
        for (CodeListManifestRecord codeListManifestRecord : codeListManifestRecords) {
            String codeListId = codeListManifestRecord.getCodeListId();
            CodeListRecord codeListRecord = codeListRecordMap.get(codeListId);
            CcState state = CcState.valueOf(codeListRecord.getState());
            if (state != CcState.Published && !assignedCodeListComponentManifestIds.contains(
                    codeListManifestRecord.getCodeListManifestId())) {
                continue;
            }
            if (codeListRecord.getNamespaceId() == null) {
                response.addMessageForCodeList(codeListManifestRecord.getCodeListManifestId(),
                        Error, "Namespace is required.", NAMESPACE);
            }
        }
    }

    private void validateAgencyIdList(ReleaseValidationResponse response) {
        for (AgencyIdListManifestRecord agencyIdListManifestRecord : agencyIdListManifestRecords) {
            String agencyIdListId = agencyIdListManifestRecord.getAgencyIdListId();
            AgencyIdListRecord agencyIdListRecord = agencyIdListRecordMap.get(agencyIdListId);
            CcState state = CcState.valueOf(agencyIdListRecord.getState());
            if (state != CcState.Published && !assignedAgencyIdListComponentManifestIds.contains(
                    agencyIdListManifestRecord.getAgencyIdListManifestId())) {
                continue;
            }
            if (agencyIdListRecord.getNamespaceId() == null) {
                response.addMessageForAgencyIdList(agencyIdListManifestRecord.getAgencyIdListManifestId(),
                        Error, "Namespace is required.", NAMESPACE);
            }
        }
    }

    private void validateDt(ReleaseValidationResponse response) {
        for (DtManifestRecord dtManifestRecord : dtManifestRecords) {
            String dtId = dtManifestRecord.getDtId();
            DtRecord dtRecord = dtRecordMap.get(dtId);
            CcState state = CcState.valueOf(dtRecord.getState());
            if (state != CcState.Published && !assignedDtComponentManifestIds.contains(
                    dtManifestRecord.getDtManifestId())) {
                continue;
            }
            if (dtRecord.getNamespaceId() == null) {
                response.addMessageForDt(dtManifestRecord.getDtManifestId(),
                        Error, "Namespace is required.", NAMESPACE);
            }
        }
    }
}
