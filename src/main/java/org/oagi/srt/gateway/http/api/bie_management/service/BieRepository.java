package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.*;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.info.data.SummaryBie;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BieRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    public List<SummaryBie> getSummaryBieList() {
        return dslContext.select(
                TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP,
                TOP_LEVEL_ABIE.STATE,
                TOP_LEVEL_ABIE.OWNER_USER_ID,
                APP_USER.LOGIN_ID,
                ASCCP.PROPERTY_TERM)
                .from(TOP_LEVEL_ABIE)
                .join(APP_USER).on(TOP_LEVEL_ABIE.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .join(ABIE).on(TOP_LEVEL_ABIE.ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASBIEP).on(ABIE.ABIE_ID.eq(ASBIEP.ROLE_OF_ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .fetchStream().map(e -> {
            SummaryBie item = new SummaryBie();
            item.setTopLevelAbieId(e.get(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID).toBigInteger());
            item.setLastUpdateTimestamp(e.get(TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP));
            item.setState(BieState.valueOf(e.get(TOP_LEVEL_ABIE.STATE)));
            item.setOwnerUserId(e.get(TOP_LEVEL_ABIE.OWNER_USER_ID).toBigInteger());
            item.setOwnerUsername(e.get(APP_USER.LOGIN_ID));
            item.setPropertyTerm(e.get(ASCCP.PROPERTY_TERM));
            return item;
        }).collect(Collectors.toList());
    }

    public BigInteger getAccIdByTopLevelAbieId(BigInteger topLevelAbieId, BigInteger releaseId) {
        return dslContext.select(ACC_MANIFEST.ACC_ID)
                .from(ABIE)
                .join(TOP_LEVEL_ABIE)
                .on(ABIE.ABIE_ID.eq(TOP_LEVEL_ABIE.ABIE_ID))
                .join(ACC_MANIFEST)
                .on(ABIE.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId))
                        .and(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchOneInto(BigInteger.class);
    }

    public BieEditAcc getAccByAccId(BigInteger accId, BigInteger releaseId) {
        // BIE only can see the ACCs whose state is in Published.
        return dslContext.select(
                ACC.ACC_ID,
                ACC_MANIFEST.as("base").ACC_ID.as("based_acc_id"),
                ACC.OAGIS_COMPONENT_TYPE,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .join(RELEASE)
                .on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .leftJoin(ACC_MANIFEST.as("base"))
                .on(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.as("base").ACC_MANIFEST_ID))
                .where(and(
                        ACC.STATE.eq(CcState.Published.name()),
                        ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchOneInto(BieEditAcc.class);
    }

    public BieEditAcc getAccByAccManifestId(BigInteger accManifestId) {
        // BIE only can see the ACCs whose state is in Published.
        return dslContext.select(
                ACC.ACC_ID,
                ACC_MANIFEST.as("base").ACC_ID.as("based_acc_id"),
                ACC.OAGIS_COMPONENT_TYPE,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .join(RELEASE)
                .on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .leftJoin(ACC_MANIFEST.as("base"))
                .on(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.as("base").ACC_MANIFEST_ID))
                .where(and(
                        ACC.STATE.eq(CcState.Published.name()),
                        ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId))
                ))
                .fetchOneInto(BieEditAcc.class);
    }

    public BieEditAcc getAcc(BigInteger accId, BigInteger releaseId) {
        return dslContext.select(
                ACC.ACC_ID,
                ACC_MANIFEST.as("base").ACC_ID.as("based_acc_id"),
                ACC.OAGIS_COMPONENT_TYPE,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .join(RELEASE)
                .on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .leftJoin(ACC_MANIFEST.as("base"))
                .on(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.as("base").ACC_MANIFEST_ID))
                .where(ACC.ACC_ID.eq(ULong.valueOf(accId))
                        .and(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchOptionalInto(BieEditAcc.class).orElse(null);
    }

    public BieEditBbiep getBbiep(BigInteger bbiepId, BigInteger topLevelAbieId) {
        return dslContext.select(
                BBIEP.BBIEP_ID,
                BCCP_MANIFEST.BCCP_ID.as("based_bccp_id"))
                .from(BBIEP)
                .join(BCCP_MANIFEST).on(BBIEP.BASED_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(and(
                        BBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        BBIEP.BBIEP_ID.eq(ULong.valueOf(bbiepId))))
                .fetchOptionalInto(BieEditBbiep.class).orElse(null);
    }

    public BccForBie getBcc(BigInteger bccId) {
        return dslContext.select(
                BCC.BCC_ID,
                BCC.GUID,
                BCC.CARDINALITY_MIN,
                BCC.CARDINALITY_MAX,
                BCC.DEN,
                BCC.DEFINITION,
                ACC_MANIFEST.ACC_ID.as("from_acc_id"),
                BCCP_MANIFEST.BCCP_ID.as("to_bccp_id"),
                BCC.ENTITY_TYPE,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .join(RELEASE)
                .on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ACC_MANIFEST)
                .on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(BCC.BCC_ID.eq(ULong.valueOf(bccId)))
                .fetchOptionalInto(BccForBie.class).orElse(null);
    }

    public BieEditBccp getBccp(BigInteger bccpId, BigInteger releaseId) {
        return dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.BDT_ID,
                BCCP.PROPERTY_TERM,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .join(RELEASE)
                .on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(REVISION)
                .on(BCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)).and(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchOptionalInto(BieEditBccp.class).orElse(null);
    }

    public int getCountDtScByOwnerDtId(BigInteger ownerDtManifestId) {
        return dslContext.selectCount()
                .from(DT_SC)
                .join(DT)
                .on(DT_SC.OWNER_DT_ID.eq(DT.DT_ID))
                .join(DT_MANIFEST)
                .on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .where(and(
                        DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(ownerDtManifestId)),
                        DT_SC.CARDINALITY_MAX.ne(0)
                )).fetchOptionalInto(Integer.class).orElse(0);
    }

    public int getCountBbieScByBbieIdAndIsUsedAndOwnerTopLevelAbieId(BigInteger bbieId,
                                                                     boolean used, BigInteger ownerTopLevelAbieId) {
        if (bbieId == null || bbieId.longValue() == 0L) {
            return 0;
        }

        return dslContext.selectCount()
                .from(BBIE_SC)
                .where(and(BBIE_SC.BBIE_ID.eq(ULong.valueOf(bbieId)),
                        BBIE_SC.IS_USED.eq((byte) ((used) ? 1 : 0)),
                        BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId))))
                .fetchOptionalInto(Integer.class).orElse(0);
    }

    public List<BieEditBdtSc> getBdtScListByOwnerDtId(BigInteger ownerDtManifestId) {
        return dslContext.select(
                DT_SC.DT_SC_ID,
                DT_SC.GUID,
                DT_SC.PROPERTY_TERM,
                DT_SC.REPRESENTATION_TERM,
                DT_SC.OWNER_DT_ID)
                .from(DT_SC)
                .join(DT)
                .on(DT_SC.OWNER_DT_ID.eq(DT.DT_ID))
                .where(and(
                        DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID.eq(ULong.valueOf(ownerDtManifestId)),
                        DT_SC.CARDINALITY_MAX.ne(0)
                )).fetchInto(BieEditBdtSc.class);
    }

    public BieEditBbieSc getBbieScIdByBbieIdAndDtScId(BigInteger bbieId, BigInteger dtScManifestId, BigInteger topLevelAbieId) {
        return dslContext.select(
                BBIE_SC.BBIE_SC_ID,
                BBIE_SC.BBIE_ID,
                DT_SC_MANIFEST.DT_SC_ID,
                BBIE_SC.IS_USED.as("used"))
                .from(BBIE_SC)
                .join(DT_SC_MANIFEST).on(BBIE_SC.BASED_DT_SC_MANIFEST_ID.eq(DT_SC_MANIFEST.DT_SC_MANIFEST_ID))
                .where(and(
                        BBIE_SC.BBIE_ID.eq(ULong.valueOf(bbieId)),
                        DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(dtScManifestId)),
                        BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId))
                )).fetchOptionalInto(BieEditBbieSc.class).orElse(null);
    }


    public String getAsccpPropertyTermByAsbiepId(BigInteger asbiepId) {
        return dslContext.select(
                ASCCP.PROPERTY_TERM)
                .from(ASBIEP)
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(ASBIEP.ASBIEP_ID.eq(ULong.valueOf(asbiepId)))
                .fetchOptionalInto(String.class).orElse(null);
    }

    public String getBccpPropertyTermByBbiepId(BigInteger bbiepId) {
        return dslContext.select(
                BCCP.PROPERTY_TERM)
                .from(BBIEP)
                .join(BCCP_MANIFEST).on(BBIEP.BASED_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .where(BBIEP.BBIEP_ID.eq(ULong.valueOf(bbiepId)))
                .fetchOptionalInto(String.class).orElse(null);
    }

    public BieEditAsccp getAsccpByAsccpManifestId(BigInteger asccpManifestId) {
        return dslContext.select(
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM,
                ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(RELEASE)
                .on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(REVISION)
                .on(ASCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                .fetchOneInto(BieEditAsccp.class);
    }

    public BieEditBccp getBccpByBccpManifestId(BigInteger bccpManifestId) {
        return dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM,
                BCCP_MANIFEST.BDT_MANIFEST_ID,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .join(RELEASE)
                .on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(REVISION)
                .on(BCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchOneInto(BieEditBccp.class);
    }

    public BieEditAbie getAbieByAsbiepId(BigInteger asbiepId) {
        return dslContext.select(
                ABIE.ABIE_ID,
                ACC_MANIFEST.ACC_ID.as("based_acc_id"))
                .from(ABIE)
                .join(ACC_MANIFEST).on(ABIE.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(ASBIEP).on(ABIE.ABIE_ID.eq(ASBIEP.ROLE_OF_ABIE_ID))
                .where(ASBIEP.ASBIEP_ID.eq(ULong.valueOf(asbiepId)))
                .fetchOptionalInto(BieEditAbie.class).orElse(null);
    }

    public List<BieEditAsbie> getAsbieListByFromAbieId(BigInteger fromAbieId, BieEditNode node) {
        return dslContext.select(
                ASBIE.ASBIE_ID,
                ASBIE.FROM_ABIE_ID,
                ASBIE.TO_ASBIEP_ID,
                ASCC_MANIFEST.ASCC_ID.as("based_ascc_id"),
                ASBIE.IS_USED.as("used"),
                ASBIE.CARDINALITY_MIN,
                ASBIE.CARDINALITY_MAX)
                .from(ASBIE)
                .join(ASCC_MANIFEST).on(ASBIE.BASED_ASCC_MANIFEST_ID.eq(ASCC_MANIFEST.ASCC_MANIFEST_ID))
                .where(and(
                        ASBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(node.getTopLevelAbieId())),
                        ASBIE.FROM_ABIE_ID.eq(ULong.valueOf(fromAbieId))
                ))
                .fetchInto(BieEditAsbie.class);
    }

    public List<BieEditBbie> getBbieListByFromAbieId(BigInteger fromAbieId, BieEditNode node) {
        return dslContext.select(
                BBIE.BBIE_ID,
                BBIE.FROM_ABIE_ID,
                BBIE.TO_BBIEP_ID,
                BCC_MANIFEST.BCC_ID.as("based_bcc_id"),
                BBIE.IS_USED.as("used"),
                BBIE.CARDINALITY_MIN,
                BBIE.CARDINALITY_MAX)
                .from(BBIE)
                .join(BCC_MANIFEST).on(BBIE.BASED_BCC_MANIFEST_ID.eq(BCC_MANIFEST.BCC_MANIFEST_ID))
                .where(and(
                        BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(node.getTopLevelAbieId())),
                        BBIE.FROM_ABIE_ID.eq(ULong.valueOf(fromAbieId))
                ))
                .fetchInto(BieEditBbie.class);
    }

    public BigInteger getRoleOfAccIdByAsbiepId(BigInteger asbiepId) {
        return dslContext.select(ACC_MANIFEST.ACC_ID)
                .from(ASBIEP)
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ACC_MANIFEST).on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ASBIEP.ASBIEP_ID.eq(ULong.valueOf(asbiepId)))
                .fetchOptionalInto(BigInteger.class).orElse(BigInteger.ZERO);
    }

    public BigInteger getRoleOfAccIdByAsccpId(BigInteger asccpManifestId) {
        return dslContext.select(ACC_MANIFEST.ACC_ID)
                .from(ACC_MANIFEST)
                .join(ASCCP_MANIFEST)
                .on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                .fetchOptionalInto(BigInteger.class).orElse(BigInteger.ZERO);
    }

    public List<BieEditAscc> getAsccListByFromAccId(BigInteger fromAccId, BigInteger releaseId) {
        return getAsccListByFromAccId(fromAccId, releaseId, false);
    }

    public List<BieEditAscc> getAsccListByFromAccId(BigInteger fromAccId, BigInteger releaseId, boolean isPublished) {
        List<Condition> conditions = new ArrayList(Arrays.asList(
                ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(fromAccId)),
                ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
        ));

        if (isPublished) {
            conditions.add(
                    ASCC.STATE.eq(CcState.Published.name())
            );
        }

        return dslContext.select(
                ASCC.ASCC_ID,
                ASCC.GUID,
                ASCC_MANIFEST.ASCC_MANIFEST_ID,
                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                ASCC.SEQ_KEY,
                ASCC.CARDINALITY_MIN,
                ASCC.CARDINALITY_MAX,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                .join(ACC_MANIFEST)
                .on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(conditions))
                .fetchInto(BieEditAscc.class);
    }

    public List<BieEditBcc> getBccListByFromAccId(BigInteger fromAccId, BigInteger releaseId) {
        return getBccListByFromAccId(fromAccId, releaseId, false);
    }

    public List<BieEditBcc> getBccListByFromAccId(BigInteger fromAccId, BigInteger releaseId, boolean isPublished) {
        List<Condition> conditions = new ArrayList(Arrays.asList(
                ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(fromAccId)),
                BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
        ));

        if (isPublished) {
            conditions.add(
                    BCC.STATE.eq(CcState.Published.name())
            );
        }

        return dslContext.select(
                BCC.BCC_ID,
                BCC.GUID,
                BCC_MANIFEST.BCC_MANIFEST_ID,
                BCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                BCC_MANIFEST.TO_BCCP_MANIFEST_ID,
                BCC.SEQ_KEY,
                BCC.ENTITY_TYPE,
                BCC.CARDINALITY_MIN,
                BCC.CARDINALITY_MAX,
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                REVISION.REVISION_ID,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM)
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .join(ACC_MANIFEST)
                .on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(conditions))
                .fetchInto(BieEditBcc.class);
    }

    public List<Long> getBizCtxIdByTopLevelAbieId(BigInteger topLevelAbieId) {
        return dslContext.select(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID)
                .from(BIZ_CTX_ASSIGNMENT)
                .join(TOP_LEVEL_ABIE).on(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchInto(Long.class);
    }

    public AsbiepRecord createAsbiep(User user, BigInteger asccpManifestId, BigInteger abieId, BigInteger topLevelAbieId) {
        BigInteger userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        return dslContext.insertInto(ASBIEP)
                .set(ASBIEP.GUID, SrtGuid.randomGuid())
                .set(ASBIEP.BASED_ASCCP_MANIFEST_ID, ULong.valueOf(asccpManifestId))
                .set(ASBIEP.ROLE_OF_ABIE_ID, ULong.valueOf(abieId))
                .set(ASBIEP.CREATED_BY, ULong.valueOf(userId))
                .set(ASBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(ASBIEP.CREATION_TIMESTAMP, timestamp)
                .set(ASBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(ASBIEP.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                .returning().fetchOne();
    }

    public AsbieRecord createAsbie(User user, BigInteger fromAbieId, BigInteger toAsbiepId,
                                   BigInteger basedAsccManifestId,
                                   int seqKey, BigInteger topLevelAbieId) {

        BigInteger userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        Record2<Integer, Integer> cardinality = dslContext.select(
                ASCC.CARDINALITY_MIN,
                ASCC.CARDINALITY_MAX)
                .from(ASCC)
                .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(basedAsccManifestId)))
                .fetchOne();

        Byte AsccpNillable = dslContext.select(ASCCP.IS_NILLABLE)
                .from(ASCC_MANIFEST)
                .join(ASCCP_MANIFEST).on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(basedAsccManifestId)))
                .fetchOne().getValue(ASCCP.IS_NILLABLE);

        return dslContext.insertInto(ASBIE)
                .set(ASBIE.GUID, SrtGuid.randomGuid())
                .set(ASBIE.FROM_ABIE_ID, ULong.valueOf(fromAbieId))
                .set(ASBIE.TO_ASBIEP_ID, ULong.valueOf(toAsbiepId))
                .set(ASBIE.BASED_ASCC_MANIFEST_ID, ULong.valueOf(basedAsccManifestId))
                .set(ASBIE.CARDINALITY_MIN, cardinality.get(ASCC.CARDINALITY_MIN))
                .set(ASBIE.CARDINALITY_MAX, cardinality.get(ASCC.CARDINALITY_MAX))
                .set(ASBIE.IS_NILLABLE, AsccpNillable)
                .set(ASBIE.CREATED_BY, ULong.valueOf(userId))
                .set(ASBIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(ASBIE.CREATION_TIMESTAMP, timestamp)
                .set(ASBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(ASBIE.SEQ_KEY, BigDecimal.valueOf(seqKey))
                .set(ASBIE.IS_USED, (byte) (cardinality.get(ASCC.CARDINALITY_MIN) > 0 ? 1 : 0))
                .set(ASBIE.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                .returning().fetchOne();

    }

    public BbiepRecord createBbiep(User user, BigInteger basedBccpManifestId, BigInteger topLevelAbieId) {
        BigInteger userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        return dslContext.insertInto(BBIEP)
                .set(BBIEP.GUID, SrtGuid.randomGuid())
                .set(BBIEP.BASED_BCCP_MANIFEST_ID, ULong.valueOf(basedBccpManifestId))
                .set(BBIEP.CREATED_BY, ULong.valueOf(userId))
                .set(BBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(BBIEP.CREATION_TIMESTAMP, timestamp)
                .set(BBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(BBIEP.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                .returning().fetchOne();
    }

    public BbieRecord createBbie(User user, BigInteger fromAbieId, BigInteger toBbiepId,
                                 BigInteger basedBccManifestId,
                                 BigInteger bdtManifestId,
                                 int seqKey, BigInteger topLevelAbieId) {

        BigInteger userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        BccRecord bccRecord = dslContext.select(
                BCC.TO_BCCP_ID,
                BCC.CARDINALITY_MIN,
                BCC.CARDINALITY_MAX,
                BCC.DEFAULT_VALUE,
                BCC.FIXED_VALUE,
                BCC.IS_NILLABLE)
                .from(BCC)
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(basedBccManifestId)))
                .fetchOneInto(BccRecord.class);

        BccpRecord bccpRecord = dslContext.select(
                BCCP.DEFAULT_VALUE,
                BCCP.FIXED_VALUE)
                .from(BCCP)
                .where(BCCP.BCCP_ID.eq(bccRecord.getToBccpId()))
                .fetchOneInto(BccpRecord.class);

        return dslContext.insertInto(BBIE)
                .set(BBIE.GUID, SrtGuid.randomGuid())
                .set(BBIE.FROM_ABIE_ID, ULong.valueOf(fromAbieId))
                .set(BBIE.TO_BBIEP_ID, ULong.valueOf(toBbiepId))
                .set(BBIE.BASED_BCC_MANIFEST_ID, ULong.valueOf(basedBccManifestId))
                .set(BBIE.BDT_PRI_RESTRI_ID, ULong.valueOf(getDefaultBdtPriRestriIdByBdtId(bdtManifestId)))
                .set(BBIE.CARDINALITY_MIN, bccRecord.getCardinalityMin())
                .set(BBIE.CARDINALITY_MAX, bccRecord.getCardinalityMax())
                .set(BBIE.IS_NILLABLE, bccRecord.getIsNillable())
                .set(BBIE.IS_NULL, (byte) ((0)))
                .set(BBIE.CREATED_BY, ULong.valueOf(userId))
                .set(BBIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(BBIE.CREATION_TIMESTAMP, timestamp)
                .set(BBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(BBIE.SEQ_KEY, BigDecimal.valueOf(seqKey))
                .set(BBIE.IS_USED, (byte) (bccRecord.getCardinalityMin() > 0 ? 1 : 0))
                .set(BBIE.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                .set(BBIE.DEFAULT_VALUE, StringUtils.defaultIfEmpty(bccRecord.getDefaultValue(), bccpRecord.getDefaultValue()))
                .set(BBIE.FIXED_VALUE, StringUtils.defaultIfEmpty(bccRecord.getFixedValue(), bccpRecord.getFixedValue()))
                .returning().fetchOne();
    }

    public BigInteger getDefaultBdtPriRestriIdByBdtId(BigInteger bdtManifestId) {
        ULong dtManifestId = ULong.valueOf(bdtManifestId);
        String bdtDataTypeTerm = dslContext.select(DT.DATA_TYPE_TERM)
                .from(DT)
                .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(dtManifestId))
                .fetchOneInto(String.class);

        /*
         * Issue #808
         */
        List<Condition> conds = new ArrayList();
        conds.add(DT_MANIFEST.DT_MANIFEST_ID.eq(dtManifestId));
        if ("Date Time".equals(bdtDataTypeTerm)) {
            conds.add(XBT.NAME.eq("date time"));
        } else if ("Date".equals(bdtDataTypeTerm)) {
            conds.add(XBT.NAME.eq("date"));
        } else if ("Time".equals(bdtDataTypeTerm)) {
            conds.add(XBT.NAME.eq("time"));
        } else {
            conds.add(BDT_PRI_RESTRI.IS_DEFAULT.eq((byte) 1));
        }

        SelectOnConditionStep<Record1<ULong>> step = dslContext.select(
                BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID)
                .from(BDT_PRI_RESTRI)
                .join(DT).on(BDT_PRI_RESTRI.BDT_ID.eq(DT.DT_ID))
                .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .join(CDT_AWD_PRI_XPS_TYPE_MAP).on(BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID));
        return step.where(conds)
                .fetchOptionalInto(BigInteger.class).orElse(BigInteger.ZERO);
    }

    public BigInteger createBbieSc(User user, BigInteger bbieId, BigInteger dtScManifestId,
                                   BigInteger topLevelAbieId) {

        DtScRecord dtScRecord = dslContext.select(DT_SC.fields())
                .from(DT_SC)
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(dtScManifestId)))
                .fetchOneInto(DtScRecord.class);

        return dslContext.insertInto(BBIE_SC)
                .set(BBIE_SC.GUID, SrtGuid.randomGuid())
                .set(BBIE_SC.BBIE_ID, ULong.valueOf(bbieId))
                .set(BBIE_SC.BASED_DT_SC_MANIFEST_ID, ULong.valueOf(dtScManifestId))
                .set(BBIE_SC.DT_SC_PRI_RESTRI_ID, ULong.valueOf(getDefaultDtScPriRestriIdByDtScId(dtScRecord.getDtScId().toBigInteger())))
                .set(BBIE_SC.CARDINALITY_MIN, dtScRecord.getCardinalityMin())
                .set(BBIE_SC.CARDINALITY_MAX, dtScRecord.getCardinalityMax())
                .set(BBIE_SC.IS_USED, (byte) (dtScRecord.getCardinalityMin() > 0 ? 1 : 0))
                .set(BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                .set(BBIE_SC.DEFAULT_VALUE, dtScRecord.getDefaultValue())
                .set(BBIE_SC.FIXED_VALUE, dtScRecord.getFixedValue())
                .returning().fetchOne().getBbieScId().toBigInteger();
    }

    public BigInteger getDefaultDtScPriRestriIdByDtScId(BigInteger dtScManifestId) {
        ULong bdtScManifestId = ULong.valueOf(dtScManifestId);
        String bdtScRepresentationTerm = dslContext.select(DT_SC.REPRESENTATION_TERM)
                .from(DT_SC)
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(bdtScManifestId))
                .fetchOneInto(String.class);

        /*
         * Issue #808
         */
        List<Condition> conds = new ArrayList();
        conds.add(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(bdtScManifestId));
        if ("Date Time".equals(bdtScRepresentationTerm)) {
            conds.add(XBT.NAME.eq("date time"));
        } else if ("Date".equals(bdtScRepresentationTerm)) {
            conds.add(XBT.NAME.eq("date"));
        } else if ("Time".equals(bdtScRepresentationTerm)) {
            conds.add(XBT.NAME.eq("time"));
        } else {
            conds.add(BDT_SC_PRI_RESTRI.IS_DEFAULT.eq((byte) 1));
        }

        SelectOnConditionStep<Record1<ULong>> step = dslContext.select(
                BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID)
                .from(BDT_SC_PRI_RESTRI)
                .join(DT_SC).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC.DT_SC_ID))
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(CDT_SC_AWD_PRI_XPS_TYPE_MAP).on(BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_SC_AWD_PRI_XPS_TYPE_MAP.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_SC_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID));
        return step.where(conds)
                .fetchOptionalInto(BigInteger.class).orElse(BigInteger.ZERO);
    }

    public void updateState(BigInteger topLevelAbieId, BieState state) {
        dslContext.update(TOP_LEVEL_ABIE)
                .set(TOP_LEVEL_ABIE.STATE, state.getValue())
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .execute();

        dslContext.update(ABIE)
                .set(ABIE.STATE, state.getValue())
                .where(ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .execute();
    }

    public OagisComponentType getOagisComponentTypeOfAccByAsccpId(BigInteger asccpManifestId, BigInteger releaseId) {
        int oagisComponentType = dslContext.select(ACC.OAGIS_COMPONENT_TYPE)
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .join(ASCCP_MANIFEST).on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId))
                        .and(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetchOneInto(Integer.class);
        return OagisComponentType.valueOf(oagisComponentType);
    }

}
