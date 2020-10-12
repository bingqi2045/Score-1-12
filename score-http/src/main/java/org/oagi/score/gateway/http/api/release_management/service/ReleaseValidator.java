package org.oagi.score.gateway.http.api.release_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageCode.NAMESPACE;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageCode.ACC_Association;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageCode.ACC_BasedACC;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageCode.ASCCP_RoleOfAcc;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageLevel.Error;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageLevel.Warn;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

public class ReleaseValidator {

    private final DSLContext dslContext;

    private List<BigInteger> assignedAccComponentManifestIds = Collections.emptyList();
    private List<BigInteger> assignedAsccpComponentManifestIds = Collections.emptyList();
    private List<BigInteger> assignedBccpComponentManifestIds = Collections.emptyList();
    private List<BigInteger> assignedCodeListComponentManifestIds = Collections.emptyList();

    private List<AccManifestRecord> accManifestRecords;
    private Map<ULong, AccManifestRecord> accManifestRecordMap;
    private List<AccRecord> accRecords;
    private Map<ULong, AccRecord> accRecordMap;

    private List<AsccManifestRecord> asccManifestRecords;
    private List<AsccRecord> asccRecords;
    private Map<ULong, AsccRecord> asccRecordMap;

    private List<BccManifestRecord> bccManifestRecords;
    private List<BccRecord> bccRecords;
    private Map<ULong, BccRecord> bccRecordMap;

    private List<AsccpManifestRecord> asccpManifestRecords;
    private Map<ULong, AsccpManifestRecord> asccpManifestRecordMap;
    private List<AsccpRecord> asccpRecords;
    private Map<ULong, AsccpRecord> asccpRecordMap;

    private List<BccpManifestRecord> bccpManifestRecords;
    private Map<ULong, BccpManifestRecord> bccpManifestRecordMap;
    private List<BccpRecord> bccpRecords;
    private Map<ULong, BccpRecord> bccpRecordMap;

    private List<CodeListManifestRecord> codeListManifestRecords;
    private Map<ULong, CodeListManifestRecord> codeListManifestRecordMap;
    private List<CodeListRecord> codeListRecords;
    private Map<ULong, CodeListRecord> codeListRecordMap;

    public ReleaseValidator(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void setAssignedAccComponentManifestIds(List<BigInteger> assignedAccComponentManifestIds) {
        this.assignedAccComponentManifestIds = assignedAccComponentManifestIds;
    }

    public void setAssignedAsccpComponentManifestIds(List<BigInteger> assignedAsccpComponentManifestIds) {
        this.assignedAsccpComponentManifestIds = assignedAsccpComponentManifestIds;
    }

    public void setAssignedBccpComponentManifestIds(List<BigInteger> assignedBccpComponentManifestIds) {
        this.assignedBccpComponentManifestIds = assignedBccpComponentManifestIds;
    }

    public void setAssignedCodeListComponentManifestIds(List<BigInteger> assignedCodeListComponentManifestIds) {
        this.assignedCodeListComponentManifestIds = assignedCodeListComponentManifestIds;
    }

    public ReleaseValidationResponse validate() {
        loadManifests();

        ReleaseValidationResponse response = new ReleaseValidationResponse();

        validateAcc(response);
        validateAsccp(response);
        validateBccp(response);
        validateCodeList(response);

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
    }

    private void validateAcc(ReleaseValidationResponse response) {
        for (AccManifestRecord accManifestRecord : accManifestRecords) {
            ULong accId = accManifestRecord.getAccId();
            AccRecord accRecord = accRecordMap.get(accId);
            CcState state = CcState.valueOf(accRecord.getState());
            if (state != CcState.Published && !assignedAccComponentManifestIds.contains(
                    accManifestRecord.getAccManifestId().toBigInteger())) {
                continue;
            }
            if (accRecord.getNamespaceId() == null) {
                response.addMessageForAcc(accManifestRecord.getAccManifestId().toBigInteger(),
                        Error, "Namespace is required.", NAMESPACE);
            }

            // check ACCs whose `basedACC` is this acc.
            accManifestRecords.stream().filter(e -> e.getAccManifestId().equals(accManifestRecord.getBasedAccManifestId()))
                    .forEach(basedAccManifestRecord -> {
                        AccRecord basedAcc = accRecordMap.get(basedAccManifestRecord.getAccId());
                        CcState basedAccstate = CcState.valueOf(basedAcc.getState());
                        if (basedAccstate != CcState.Published) {
                            if (assignedAccComponentManifestIds.contains(basedAccManifestRecord.getAccManifestId().toBigInteger())) {
                                if (basedAccstate != CcState.Candidate) {
                                    response.addMessageForAcc(basedAccManifestRecord.getAccManifestId().toBigInteger(),
                                            Error, "'" + basedAcc.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ACC_BasedACC);
                                }
                            } else {
                                if (basedAccManifestRecord.getPrevAccManifestId() == null) {
                                    response.addMessageForAcc(basedAccManifestRecord.getAccManifestId().toBigInteger(),
                                            Error, "'" + basedAcc.getDen() + "' is needed in the release assignment due to '" + accRecord.getDen() + "'.",
                                            ACC_BasedACC);
                                } else {
                                    response.addMessageForAcc(basedAccManifestRecord.getAccManifestId().toBigInteger(),
                                            Warn, "'" + basedAcc.getDen() + "' has been revised but not included in the release assignment.",
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
                            if (assignedAsccpComponentManifestIds.contains(asccpManifestRecord.getAsccpManifestId().toBigInteger())) {
                                if (asccpState != CcState.Candidate) {
                                    response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId().toBigInteger(),
                                            Error, "'" + asccpRecord.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ACC_Association);
                                }
                            } else {
                                if (asccpManifestRecord.getPrevAsccpManifestId() == null) {
                                    response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId().toBigInteger(),
                                            Error, "'" + asccpRecord.getDen() + "' is needed in the release assignment due to '" + accRecord.getDen() + "'.",
                                            ACC_Association);
                                } else {
                                    response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId().toBigInteger(),
                                            Warn, "'" + asccpRecord.getDen() + "' has been revised but not included in the release assignment.",
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
                            if (assignedBccpComponentManifestIds.contains(bccpManifestRecord.getBccpManifestId().toBigInteger())) {
                                if (bccpState != CcState.Candidate) {
                                    response.addMessageForBccp(bccpManifestRecord.getBccpManifestId().toBigInteger(),
                                            Error, "'" + bccpRecord.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ACC_Association);
                                }
                            } else {
                                if (bccpManifestRecord.getPrevBccpManifestId() == null) {
                                    response.addMessageForBccp(bccpManifestRecord.getBccpManifestId().toBigInteger(),
                                            Error, "'" + bccpRecord.getDen() + "' is needed in the release assignment due to '" + accRecord.getDen() + "'.",
                                            ACC_Association);
                                } else {
                                    response.addMessageForBccp(bccpManifestRecord.getBccpManifestId().toBigInteger(),
                                            Warn, "'" + bccpRecord.getDen() + "' has been revised but not included in the release assignment.",
                                            ACC_Association);
                                }
                            }
                        }
                    });
        }
    }

    private void validateAsccp(ReleaseValidationResponse response) {
        for (AsccpManifestRecord asccpManifestRecord : asccpManifestRecords) {
            ULong asccpId = asccpManifestRecord.getAsccpId();
            AsccpRecord asccpRecord = asccpRecordMap.get(asccpId);
            CcState state = CcState.valueOf(asccpRecord.getState());
            if (state != CcState.Published && !assignedAsccpComponentManifestIds.contains(
                    asccpManifestRecord.getAsccpManifestId().toBigInteger())) {
                continue;
            }
            if (asccpRecord.getNamespaceId() == null) {
                response.addMessageForAsccp(asccpManifestRecord.getAsccpManifestId().toBigInteger(),
                        Error, "Namespace is required.", NAMESPACE);
            }

            // check ACCs whose `roleOfAcc` is this acc.
            accManifestRecords.stream().filter(e -> e.getAccManifestId().equals(asccpManifestRecord.getRoleOfAccManifestId()))
                    .forEach(accManifestRecord -> {
                        AccRecord accRecord = accRecordMap.get(accManifestRecord.getAccId());
                        CcState accState = CcState.valueOf(accRecord.getState());
                        if (accState != CcState.Published) {
                            if (assignedAccComponentManifestIds.contains(accManifestRecord.getAccManifestId().toBigInteger())) {
                                if (accState != CcState.Candidate) {
                                    response.addMessageForAcc(accManifestRecord.getAccManifestId().toBigInteger(),
                                            Error, "'" + accRecord.getDen() + "' should be in '" + CcState.Candidate + "'.",
                                            ASCCP_RoleOfAcc);
                                }
                            } else {
                                if (accManifestRecord.getPrevAccManifestId() == null) {
                                    response.addMessageForAcc(accManifestRecord.getAccManifestId().toBigInteger(),
                                            Error, "'" + accRecord.getDen() + "' is needed in the release assignment due to '" + asccpRecord.getDen() + "'.",
                                            ASCCP_RoleOfAcc);
                                } else {
                                    response.addMessageForAcc(accManifestRecord.getAccManifestId().toBigInteger(),
                                            Warn, "'" + accRecord.getDen() + "' has been revised but not included in the release assignment.",
                                            ASCCP_RoleOfAcc);
                                }
                            }
                        }
                    });
        }
    }

    private void validateBccp(ReleaseValidationResponse response) {
        for (BccpManifestRecord bccpManifestRecord : bccpManifestRecords) {
            ULong bccpId = bccpManifestRecord.getBccpId();
            BccpRecord bccpRecord = bccpRecordMap.get(bccpId);
            CcState state = CcState.valueOf(bccpRecord.getState());
            if (state != CcState.Published && !assignedBccpComponentManifestIds.contains(
                    bccpManifestRecord.getBccpManifestId().toBigInteger())) {
                continue;
            }
            if (bccpRecord.getNamespaceId() == null) {
                response.addMessageForBccp(bccpManifestRecord.getBccpManifestId().toBigInteger(),
                        Error, "Namespace is required.", NAMESPACE);
            }
        }
    }

    private void validateCodeList(ReleaseValidationResponse response) {
    }
}
