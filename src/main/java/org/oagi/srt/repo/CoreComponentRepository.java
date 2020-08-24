package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.gateway.http.api.info.data.SummaryCcExt;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.cc_arguments.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.max;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Acc.ACC;
import static org.oagi.srt.entity.jooq.tables.Ascc.ASCC;
import static org.oagi.srt.entity.jooq.tables.Bcc.BCC;
import static org.oagi.srt.entity.jooq.tables.Bccp.BCCP;
import static org.oagi.srt.entity.jooq.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class CoreComponentRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    public AccManifestRecord getAccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public List<AccManifestRecord> getAccManifestByBasedAccManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(manifestId))
                .fetch();
    }

    public AsccpManifestRecord getAsccpManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public List<AsccpManifestRecord> getAsccpManifestByRolOfAccManifestId(ULong roleOfAccManifestId) {
        if (roleOfAccManifestId == null || roleOfAccManifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(roleOfAccManifestId))
                .fetch();
    }

    public BccpManifestRecord getBccpManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public AsccManifestRecord getAsccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public List<AsccManifestRecord> getAsccManifestByFromAccManifestId(ULong accManifestId) {
        if (accManifestId == null || accManifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId))
                .fetch();
    }

    public List<AsccManifestRecord> getAsccManifestByToAsccpManifestId(ULong asccpManifestId) {
        if (asccpManifestId == null || asccpManifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(asccpManifestId))
                .fetch();
    }

    public Integer getNextSeqKey(ULong accManifestId) {
        if (accManifestId == null || accManifestId.longValue() <= 0L) {
            return null;
        }

        Integer asccMaxSeqKey = dslContext.select(max(ASCC.SEQ_KEY))
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId))
                .fetchOneInto(Integer.class);
        if (asccMaxSeqKey == null) {
            asccMaxSeqKey = 0;
        }

        Integer bccMaxSeqKey = dslContext.select(max(BCC.SEQ_KEY))
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId))
                .fetchOneInto(Integer.class);

        if (bccMaxSeqKey == null) {
            bccMaxSeqKey = 0;
        }

        return Math.max(asccMaxSeqKey, bccMaxSeqKey) + 1;
    }

    public BccManifestRecord getBccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public List<BccManifestRecord> getBccManifestByFromAccManifestId(ULong accManifestId) {
        if (accManifestId == null || accManifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(accManifestId))
                .fetch();
    }

    public List<BccManifestRecord> getBccManifestByToBccpManifestId(ULong bccpManifestId) {
        if (bccpManifestId == null || bccpManifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(bccpManifestId))
                .fetch();
    }

    public DtManifestRecord getBdtManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public DtManifestRecord getBdtManifestByBdtId(ULong bdtId, ULong releaseId) {
        if (bdtId == null || bdtId.longValue() <= 0L || releaseId == null || releaseId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(DT_MANIFEST)
                .where(and(DT_MANIFEST.DT_ID.eq(bdtId), DT_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetchOptional().orElse(null);
    }

    public AccRecord getAccById(ULong accId) {
        if (accId == null || accId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accId))
                .fetchOptional().orElse(null);
    }

    public AsccRecord getAsccById(ULong asccId) {
        if (asccId == null || asccId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.eq(asccId))
                .fetchOptional().orElse(null);
    }

    public BccRecord getBccById(ULong bccId) {
        if (bccId == null || bccId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(bccId))
                .fetchOptional().orElse(null);
    }

    public AsccpRecord getAsccpById(ULong asccpId) {
        if (asccpId == null || asccpId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpId))
                .fetchOptional().orElse(null);
    }

    public BccpRecord getBccpById(ULong bccpId) {
        if (bccpId == null || bccpId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpId))
                .fetchOptional().orElse(null);
    }

    public DtRecord getBdtById(ULong bdtId) {
        if (bdtId == null || bdtId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(bdtId))
                .fetchOptional().orElse(null);
    }

    public CcBccpNode getBccpNodeByBccpId(AuthenticatedPrincipal user, long bccpId) {
        return dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM.as("name"),
                BCCP.STATE,
                BCCP.BDT_ID,
                BCCP.OWNER_USER_ID,
                BCCP.PREV_BCCP_ID,
                BCCP.NEXT_BCCP_ID)
                .from(BCCP)
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(CcBccpNode.class);
    }

    public List<SummaryCcExt> getSummaryCcExtList() {
        List<ULong> uegAccIds =
                dslContext.select(max(ACC.ACC_ID).as("id"))
                        .from(ACC)
                        .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                        .where(and(
                                ACC.OAGIS_COMPONENT_TYPE.eq(OagisComponentType.UserExtensionGroup.getValue()),
                                ACC_MANIFEST.RELEASE_ID.greaterThan(ULong.valueOf(0))
                        ))
                        .groupBy(ACC.GUID)
                        .fetchInto(ULong.class);

        return dslContext.select(ACC.ACC_ID,
                ACC.OBJECT_CLASS_TERM,
                ACC.STATE,
                ACC.OWNER_USER_ID,
                APP_USER.LOGIN_ID)
                .from(ACC)
                .join(APP_USER).on(ACC.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .where(ACC.ACC_ID.in(uegAccIds))
                .fetchStream().map(e -> {
                    SummaryCcExt item = new SummaryCcExt();
                    item.setAccId(e.get(ACC.ACC_ID).toBigInteger());
                    item.setObjectClassTerm(e.get(ACC.OBJECT_CLASS_TERM));
                    item.setState(CcState.valueOf(e.get(ACC.STATE)));
                    item.setOwnerUsername(e.get(APP_USER.LOGIN_ID));
                    item.setOwnerUserId(e.get(ACC.OWNER_USER_ID).toBigInteger());
                    return item;
                }).collect(Collectors.toList());

    }

    public InsertAccArguments insertAccArguments() {
        return new InsertAccArguments(this);
    }

    public InsertAsccArguments insertAsccArguments() {
        return new InsertAsccArguments(this);
    }

    public InsertBccArguments insertBccArguments() {
        return new InsertBccArguments(this);
    }

    public InsertBccpArguments insertBccpArguments() {
        return new InsertBccpArguments(this);
    }

    public InsertAsccpArguments insertAsccpArguments() {
        return new InsertAsccpArguments(this);
    }

    public InsertAccManifestArguments insertAccManifestArguments() {
        return new InsertAccManifestArguments(this);
    }

    public InsertAsccManifestArguments insertAsccManifestArguments() {
        return new InsertAsccManifestArguments(this);
    }

    public InsertBccManifestArguments insertBccManifestArguments() {
        return new InsertBccManifestArguments(this);
    }

    public InsertBccpManifestArguments insertBccpManifest() {
        return new InsertBccpManifestArguments(this);
    }

    public InsertAsccpManifestArguments insertAsccpManifest() {
        return new InsertAsccpManifestArguments(this);
    }

    public UpdateAccArguments updateAccArguments(AccRecord acc) {
        return new UpdateAccArguments(this, acc);
    }

    public UpdateAsccArguments updateAsccArguments(AsccRecord ascc) {
        return new UpdateAsccArguments(this, ascc);
    }

    public UpdateBccArguments updateBccArguments(BccRecord bcc) {
        return new UpdateBccArguments(this, bcc);
    }

    public UpdateBccpArguments updateBccpArguments(BccpRecord bccp) {
        return new UpdateBccpArguments(this, bccp);
    }

    public UpdateAsccpArguments updateAsccpArguments(AsccpRecord asccp) {
        return new UpdateAsccpArguments(this, asccp);
    }

    public UpdateAccManifestArguments updateAccManifestArguments(AccManifestRecord accManifestRecord) {
        return new UpdateAccManifestArguments(this, accManifestRecord);
    }

    public UpdateAsccManifestArguments updateAsccManifestArguments(AsccManifestRecord asccManifest) {
        return new UpdateAsccManifestArguments(this, asccManifest);
    }

    public UpdateBccManifestArguments updateBccManifestArguments(BccManifestRecord bccManifest) {
        return new UpdateBccManifestArguments(this, bccManifest);
    }

    public UpdateBccpManifestArguments updateBccpManifestArguments(BccpManifestRecord bccpManifestRecord) {
        return new UpdateBccpManifestArguments(this, bccpManifestRecord);
    }

    public UpdateAsccpManifestArguments updateAsccpManifestArguments(AsccpManifestRecord asccpManifestRecord) {
        return new UpdateAsccpManifestArguments(this, asccpManifestRecord);
    }

    public ULong execute(InsertAccArguments arguments) {
        return dslContext.insertInto(ACC)
                .set(ACC.GUID, arguments.getGuid())
                .set(ACC.OBJECT_CLASS_TERM, arguments.getObjectClassTerm())
                .set(ACC.DEN, arguments.getDen())
                .set(ACC.DEFINITION, arguments.getDefinition())
                .set(ACC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ACC.OBJECT_CLASS_QUALIFIER, arguments.getObjectClassQualifier())
                .set(ACC.OAGIS_COMPONENT_TYPE, arguments.getOagisComponentType().getValue())
                .set(ACC.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ACC.CREATED_BY, arguments.getCreatedBy())
                .set(ACC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ACC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ACC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ACC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ACC.STATE, arguments.getState().name())
                .set(ACC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(ACC.IS_ABSTRACT, arguments.getAbstract() ? (byte) 1 : 0)
                .set(ACC.PREV_ACC_ID, arguments.getPrevAccId())
                .returning(ACC.ACC_ID).fetchOne().getAccId();
    }

    public ULong execute(InsertAsccArguments arguments) {
        return dslContext.insertInto(ASCC)
                .set(ASCC.ASCC_ID, arguments.getAsccId())
                .set(ASCC.GUID, arguments.getGuid())
                .set(ASCC.DEN, arguments.getDen())
                .set(ASCC.FROM_ACC_ID, arguments.getFromAccId())
                .set(ASCC.TO_ASCCP_ID, arguments.getToAsccpId())
                .set(ASCC.SEQ_KEY, arguments.getSeqKey())
                .set(ASCC.DEFINITION, arguments.getDefinition())
                .set(ASCC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ASCC.CARDINALITY_MIN, arguments.getCardinalityMin())
                .set(ASCC.CARDINALITY_MAX, arguments.getCardinalityMax())
                .set(ASCC.CREATED_BY, arguments.getCreatedBy())
                .set(ASCC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ASCC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ASCC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ASCC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ASCC.STATE, arguments.getState().name())
                .set(ASCC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(ASCC.PREV_ASCC_ID, arguments.getPrevAsccId())
                .set(ASCC.NEXT_ASCC_ID, arguments.getNextAsccId()).returning(ASCC.ASCC_ID).fetchOne().getAsccId();
    }

    public ULong execute(InsertBccArguments arguments) {
        return dslContext.insertInto(BCC)
                .set(BCC.BCC_ID, arguments.getBccId())
                .set(BCC.GUID, arguments.getGuid())
                .set(BCC.DEN, arguments.getDen())
                .set(BCC.SEQ_KEY, arguments.getSeqKey())
                .set(BCC.FROM_ACC_ID, arguments.getFromAccId())
                .set(BCC.TO_BCCP_ID, arguments.getToBccpId())
                .set(BCC.ENTITY_TYPE, arguments.getEntityType().getValue())
                .set(BCC.DEFINITION, arguments.getDefinition())
                .set(BCC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(BCC.CARDINALITY_MIN, arguments.getCardinalityMin())
                .set(BCC.CARDINALITY_MAX, arguments.getCardinalityMax())
                .set(BCC.CREATED_BY, arguments.getCreatedBy())
                .set(BCC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(BCC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(BCC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(BCC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(BCC.STATE, arguments.getState().name())
                .set(BCC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(BCC.IS_NILLABLE, arguments.getNillable() ? (byte) 1 : 0)
                .set(BCC.DEFAULT_VALUE, arguments.getDefaultValue())
                .set(BCC.FIXED_VALUE, arguments.getFixedValue())
                .set(BCC.PREV_BCC_ID, arguments.getPrevBccId())
                .set(BCC.NEXT_BCC_ID, arguments.getNextBccId()).returning(BCC.BCC_ID).fetchOne().getBccId();
    }


    public ULong execute(InsertAccManifestArguments arguments) {
        return dslContext.insertInto(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_ID, arguments.getAccId())
                .set(ACC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, arguments.getBasedAccManifestId())
                .returning(ACC_MANIFEST.ACC_MANIFEST_ID).fetchOne().getAccManifestId();
    }

    public ULong execute(InsertAsccManifestArguments arguments) {
        return dslContext.insertInto(ASCC_MANIFEST)
                .set(ASCC_MANIFEST.ASCC_ID, arguments.getAsccId())
                .set(ASCC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID, arguments.getFromAccManifestId())
                .set(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID, arguments.getToAsccpManifestId())
                .returning(ASCC_MANIFEST.ASCC_MANIFEST_ID).fetchOne().getAsccManifestId();
    }

    public ULong execute(InsertBccManifestArguments arguments) {
        return dslContext.insertInto(BCC_MANIFEST)
                .set(BCC_MANIFEST.BCC_ID, arguments.getBccId())
                .set(BCC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCC_MANIFEST.FROM_ACC_MANIFEST_ID, arguments.getFromAccManifestId())
                .set(BCC_MANIFEST.TO_BCCP_MANIFEST_ID, arguments.getToBccManifestId())
                .returning(BCC_MANIFEST.BCC_MANIFEST_ID).fetchOne().getBccManifestId();
    }

    public ULong execute(InsertBccpArguments arguments) {
        return dslContext.insertInto(BCCP)
                .set(BCCP.BCCP_ID, arguments.getBccpId())
                .set(BCCP.GUID, arguments.getGuid())
                .set(BCCP.PROPERTY_TERM, arguments.getPropertyTerm())
                .set(BCCP.REPRESENTATION_TERM, arguments.getRepresentationTerm())
                .set(BCCP.BDT_ID, arguments.getBdtId())
                .set(BCCP.DEN, arguments.getDen())
                .set(BCCP.DEFINITION, arguments.getDefinition())
                .set(BCCP.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(BCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(BCCP.CREATED_BY, arguments.getCreatedBy())
                .set(BCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(BCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(BCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(BCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(BCCP.STATE, arguments.getState().name())
                .set(BCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(BCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1 : 0)
                .set(BCCP.DEFAULT_VALUE, arguments.getDefaultValue())
                .set(BCCP.FIXED_VALUE, arguments.getFixedValue())
                .set(BCCP.PREV_BCCP_ID, arguments.getPrevBccpId())
                .set(BCCP.NEXT_BCCP_ID, arguments.getNextBccpId())
                .returning(BCCP.BCCP_ID).fetchOne().getBccpId();
    }

    public ULong execute(InsertBccpManifestArguments arguments) {
        return dslContext.insertInto(BCCP_MANIFEST)
                .set(BCCP_MANIFEST.BCCP_MANIFEST_ID, arguments.getBccpManifestId())
                .set(BCCP_MANIFEST.BCCP_ID, arguments.getBccpId())
                .set(BCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .returning(BCCP_MANIFEST.BCCP_MANIFEST_ID).fetchOne().getBccpManifestId();
    }

    public ULong execute(InsertAsccpArguments arguments) {
        return dslContext.insertInto(ASCCP)
                .set(ASCCP.GUID, arguments.getGuid())
                .set(ASCCP.PROPERTY_TERM, arguments.getPropertyTerm())
                .set(ASCCP.ROLE_OF_ACC_ID, arguments.getRoleOfAccId())
                .set(ASCCP.DEN, arguments.getDen())
                .set(ASCCP.DEFINITION, arguments.getDefinition())
                .set(ASCCP.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ASCCP.REUSABLE_INDICATOR, arguments.getReuseableIndicator() ? (byte) 1 : 0)
                .set(ASCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ASCCP.CREATED_BY, arguments.getCreatedBy())
                .set(ASCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ASCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ASCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ASCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ASCCP.STATE, arguments.getState().name())
                .set(ASCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(ASCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1 : 0)
                .set(ASCCP.PREV_ASCCP_ID, arguments.getPrevAsccpId())
                .set(ASCCP.NEXT_ASCCP_ID, arguments.getNextAsccpId())
                .returning(ASCCP.ASCCP_ID).fetchOne().getAsccpId();
    }

    public ULong execute(InsertAsccpManifestArguments arguments) {
        return dslContext.insertInto(ASCCP_MANIFEST)
                .set(ASCCP_MANIFEST.ASCCP_MANIFEST_ID, arguments.getAsccpManifestId())
                .set(ASCCP_MANIFEST.ASCCP_ID, arguments.getAsccpId())
                .set(ASCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, arguments.getRoleOfAccManifestId())
                .returning(ASCCP_MANIFEST.ASCCP_MANIFEST_ID).fetchOne().getAsccpManifestId();
    }

    public ULong execute(UpdateAccArguments arguments) {
        ULong nextAccId = dslContext.insertInto(ACC)
                .set(ACC.GUID, arguments.getGuid())
                .set(ACC.OBJECT_CLASS_TERM, arguments.getObjectClassTerm())
                .set(ACC.BASED_ACC_ID, arguments.getBasedAccId())
                .set(ACC.DEN, arguments.getDen())
                .set(ACC.DEFINITION, arguments.getDefinition())
                .set(ACC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ACC.OBJECT_CLASS_QUALIFIER, arguments.getObjectClassQualifier())
                .set(ACC.OAGIS_COMPONENT_TYPE, arguments.getOagisComponentType().getValue())
                .set(ACC.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ACC.CREATED_BY, arguments.getCreatedBy())
                .set(ACC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ACC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ACC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ACC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ACC.STATE, arguments.getState().name())
                .set(ACC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(ACC.IS_ABSTRACT, arguments.getAbstract() ? (byte) 1 : 0)
                .set(ACC.PREV_ACC_ID, arguments.getAccId())
                .returning(ACC.ACC_ID).fetchOne().getAccId();

        dslContext.update(ACC)
                .set(ACC.NEXT_ACC_ID, nextAccId)
                .where(ACC.ACC_ID.eq(arguments.getAccId()))
                .execute();

        return nextAccId;
    }

    public void execute(UpdateAccManifestArguments arguments) {
        dslContext.update(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_ID, arguments.getAccId())
                .set(ACC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, arguments.getBasedAccManifestId())
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(arguments.getAccManifestId()))
                .execute();
    }

    public ULong execute(UpdateAsccArguments arguments) {
        ULong nextAsccId = dslContext.insertInto(ASCC)
                .set(ASCC.GUID, arguments.getGuid())
                .set(ASCC.DEN, arguments.getDen())
                .set(ASCC.FROM_ACC_ID, arguments.getFromAccId())
                .set(ASCC.TO_ASCCP_ID, arguments.getToAsccpId())
                .set(ASCC.SEQ_KEY, arguments.getSeqKey())
                .set(ASCC.DEFINITION, arguments.getDefinition())
                .set(ASCC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ASCC.CARDINALITY_MIN, arguments.getCardinalityMin())
                .set(ASCC.CARDINALITY_MAX, arguments.getCardinalityMax())
                .set(ASCC.CREATED_BY, arguments.getCreatedBy())
                .set(ASCC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ASCC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ASCC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ASCC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ASCC.STATE, arguments.getState().name())
                .set(ASCC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(ASCC.PREV_ASCC_ID, arguments.getPrevAsccId())
                .set(ASCC.NEXT_ASCC_ID, arguments.getNextAsccId()).returning(ASCC.ASCC_ID).fetchOne().getAsccId();

        dslContext.update(ASCC)
                .set(ASCC.NEXT_ASCC_ID, nextAsccId)
                .where(ASCC.ASCC_ID.eq(arguments.getPrevAsccId()))
                .execute();
        return nextAsccId;
    }

    public void execute(UpdateAsccManifestArguments arguments) {
        dslContext.update(ASCC_MANIFEST)
                .set(ASCC_MANIFEST.ASCC_ID, arguments.getAsccId())
                .set(ASCC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID, arguments.getFromAccManifestId())
                .set(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID, arguments.getToAsccpManifestId())
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(arguments.getAsccManifestId()))
                .execute();
    }

    public ULong execute(UpdateBccArguments arguments) {
        ULong nextBccId = dslContext.insertInto(BCC)
                .set(BCC.GUID, arguments.getGuid())
                .set(BCC.DEN, arguments.getDen())
                .set(BCC.FROM_ACC_ID, arguments.getFromAccId())
                .set(BCC.TO_BCCP_ID, arguments.getToBccpId())
                .set(BCC.SEQ_KEY, arguments.getSeqKey())
                .set(BCC.ENTITY_TYPE, arguments.getEntitiyType())
                .set(BCC.DEFAULT_VALUE, arguments.getDefaultValue())
                .set(BCC.FIXED_VALUE, arguments.getFixedValue())
                .set(BCC.DEFINITION, arguments.getDefinition())
                .set(BCC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(BCC.CARDINALITY_MIN, arguments.getCardinalityMin())
                .set(BCC.CARDINALITY_MAX, arguments.getCardinalityMax())
                .set(BCC.CREATED_BY, arguments.getCreatedBy())
                .set(BCC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(BCC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(BCC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(BCC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(BCC.STATE, arguments.getState().name())
                .set(BCC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(BCC.PREV_BCC_ID, arguments.getPrevBccId())
                .set(BCC.NEXT_BCC_ID, arguments.getNextBccId()).returning(BCC.BCC_ID).fetchOne().getBccId();

        dslContext.update(BCC)
                .set(BCC.NEXT_BCC_ID, nextBccId)
                .where(BCC.BCC_ID.eq(arguments.getPrevBccId()))
                .execute();
        return nextBccId;
    }

    public void execute(UpdateBccManifestArguments arguments) {
        dslContext.update(BCC_MANIFEST)
                .set(BCC_MANIFEST.BCC_ID, arguments.getBccId())
                .set(BCC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCC_MANIFEST.FROM_ACC_MANIFEST_ID, arguments.getFromAccManifestId())
                .set(BCC_MANIFEST.TO_BCCP_MANIFEST_ID, arguments.getToBccpManifestId())
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(arguments.getBccManifestId()))
                .execute();
    }

    public ULong execute(UpdateBccpArguments arguments) {
        ULong nextBccpId = dslContext.insertInto(BCCP)
                .set(BCCP.GUID, arguments.getGuid())
                .set(BCCP.CREATED_BY, arguments.getCreatedBy())
                .set(BCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(BCCP.PROPERTY_TERM, arguments.getPropertyTerm())
                .set(BCCP.REPRESENTATION_TERM, arguments.getRepresentationTerm())
                .set(BCCP.BDT_ID, arguments.getBdtId())
                .set(BCCP.DEN, arguments.getPropertyTerm() + ". " + arguments.getRepresentationTerm())
                .set(BCCP.DEFINITION, arguments.getDefinition())
                .set(BCCP.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(BCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(BCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(BCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(BCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(BCCP.STATE, arguments.getState().name())
                .set(BCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(BCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1 : 0)
                .set(BCCP.DEFAULT_VALUE, arguments.getDefaultValue())
                .set(BCCP.FIXED_VALUE, arguments.getFixedValue())
                .set(BCCP.PREV_BCCP_ID, arguments.getPrevBccpId())
                .returning(BCCP.BCCP_ID).fetchOne().getBccpId();

        dslContext.update(BCCP)
                .set(BCCP.NEXT_BCCP_ID, nextBccpId)
                .where(BCCP.BCCP_ID.eq(arguments.getPrevBccpId()))
                .execute();

        return nextBccpId;
    }

    public ULong execute(UpdateAsccpArguments arguments) {
        ULong nextAsccpId = dslContext.insertInto(ASCCP)
                .set(ASCCP.GUID, arguments.getGuid())
                .set(ASCCP.PROPERTY_TERM, arguments.getPropertyTerm())
                .set(ASCCP.ROLE_OF_ACC_ID, arguments.getRoleOfAccId())
                .set(ASCCP.DEN, arguments.getDen())
                .set(ASCCP.DEFINITION, arguments.getDefinition())
                .set(ASCCP.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ASCCP.REUSABLE_INDICATOR, arguments.getReuseableIndicator() ? (byte) 1 : 0)
                .set(ASCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ASCCP.CREATED_BY, arguments.getCreatedBy())
                .set(ASCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ASCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ASCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ASCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ASCCP.STATE, arguments.getState().name())
                .set(ASCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1 : 0)
                .set(ASCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1 : 0)
                .set(ASCCP.PREV_ASCCP_ID, arguments.getPrevAsccpId())
                .set(ASCCP.NEXT_ASCCP_ID, arguments.getNextAsccpId())
                .returning(ASCCP.ASCCP_ID).fetchOne().getAsccpId();

        dslContext.update(ASCCP)
                .set(ASCCP.NEXT_ASCCP_ID, nextAsccpId)
                .where(ASCCP.ASCCP_ID.eq(arguments.getPrevAsccpId()))
                .execute();

        return nextAsccpId;
    }

    public void execute(UpdateBccpManifestArguments arguments) {
        dslContext.update(BCCP_MANIFEST)
                .set(BCCP_MANIFEST.BCCP_ID, arguments.getBccpId())
                .set(BCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(arguments.getBccpManifestId()))
                .execute();
    }

    public void execute(UpdateAsccpManifestArguments arguments) {
        dslContext.update(ASCCP_MANIFEST)
                .set(ASCCP_MANIFEST.ASCCP_ID, arguments.getAsccpId())
                .set(ASCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, arguments.getRoleOfAccManifestId())
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(arguments.getAsccpManifestId()))
                .execute();
    }

    public BigInteger getGlobalExtensionAccManifestId(BigInteger extensionAccManifestId) {
        ULong releaseId = dslContext.select(ACC_MANIFEST.RELEASE_ID)
                .from(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(extensionAccManifestId)))
                .fetchOneInto(ULong.class);

        return dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .join(ACC)
                .on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .where(and(
                        ACC_MANIFEST.RELEASE_ID.eq(releaseId),
                        ACC.OBJECT_CLASS_TERM.eq("All Extension")
                ))
                .fetchOneInto(BigInteger.class);
    }
}
