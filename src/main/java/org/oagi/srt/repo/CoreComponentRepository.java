package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.repo.cc_arguments.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
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

    public AccManifestRecord getAccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public AsccpManifestRecord getAsccpManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public ULong getRoleOfAccManifestIdFromAsccpManifestByManifestId(long manifestId) {
        if (manifestId <= 0L) {
            return null;
        }
        return getRoleOfAccManifestIdFromAsccpManifestByManifestId(ULong.valueOf(manifestId));
    }

    public ULong getRoleOfAccManifestIdFromAsccpManifestByManifestId(ULong manifestId) {
        return dslContext.select(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID)
                .from(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptionalInto(ULong.class).orElse(null);
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

    public BccManifestRecord getBccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
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

    public CcBccpNode getBccpNodeByBccpId(User user, long bccpId) {
        return dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM.as("name"),
                BCCP.STATE,
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM,
                BCCP.BDT_ID,
                BCCP.OWNER_USER_ID,
                BCCP.PREV_BCCP_ID,
                BCCP.NEXT_BCCP_ID)
                .from(BCCP)
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(CcBccpNode.class);
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

    public InsertAsccManifestArguments insertAsccManifestArguments() { return new InsertAsccManifestArguments(this); }

    public InsertBccManifestArguments insertBccManifestArguments() { return new InsertBccManifestArguments(this); }

    public InsertBccpManifestArguments insertBccpManifest() {
        return new InsertBccpManifestArguments(this);
    }

    public InsertAsccpManifestArguments insertAsccpManifest() {
        return new InsertAsccpManifestArguments(this);
    }

    public UpdateAccArguments updateAccArguments(AccRecord acc) {
        return new UpdateAccArguments(this, acc);
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
                .set(ACC.REVISION_NUM, arguments.getRevisionNum())
                .set(ACC.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ACC.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(ACC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(ACC.IS_ABSTRACT, arguments.getAbstract() ? (byte) 1: 0)
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
                .set(ASCC.REVISION_NUM, arguments.getRevisionNum())
                .set(ASCC.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ASCC.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(ASCC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
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
                .set(BCC.REVISION_NUM, arguments.getRevisionNum())
                .set(BCC.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(BCC.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(BCC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(BCC.IS_NILLABLE, arguments.getNillable() ? (byte) 1: 0)
                .set(BCC.DEFAULT_VALUE, arguments.getDefaultValue())
                .set(BCC.FIXED_VALUE, arguments.getFixedValue())
                .set(BCC.PREV_BCC_ID, arguments.getPrevBccId())
                .set(BCC.NEXT_BCC_ID, arguments.getNextBccId()).returning(BCC.BCC_ID).fetchOne().getBccId();
    }


    public ULong execute(InsertAccManifestArguments arguments) {
        return dslContext.insertInto(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_ID, arguments.getAccId())
                .set(ACC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ACC_MANIFEST.MODULE_ID, arguments.getModuleId())
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
                .set(BCCP.REVISION_NUM, arguments.getRevisionNum())
                .set(BCCP.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(BCCP.REVISION_ACTION, arguments.getRevisionAction().getValue())
                .set(BCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(BCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1: 0)
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
                .set(BCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
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
                .set(ASCCP.REUSABLE_INDICATOR, arguments.getReuseableIndicator()? (byte) 1: 0)
                .set(ASCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ASCCP.CREATED_BY, arguments.getCreatedBy())
                .set(ASCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ASCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ASCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ASCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ASCCP.STATE, arguments.getState().name())
                .set(ASCCP.REVISION_NUM, arguments.getRevisionNum())
                .set(ASCCP.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ASCCP.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(ASCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(ASCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1: 0)
                .set(ASCCP.PREV_ASCCP_ID, arguments.getPrevAsccpId())
                .set(ASCCP.NEXT_ASCCP_ID, arguments.getNextAsccpId())
                .returning(ASCCP.ASCCP_ID).fetchOne().getAsccpId();
    }

    public ULong execute(InsertAsccpManifestArguments arguments) {
        return dslContext.insertInto(ASCCP_MANIFEST)
                .set(ASCCP_MANIFEST.ASCCP_MANIFEST_ID, arguments.getAsccpManifestId())
                .set(ASCCP_MANIFEST.ASCCP_ID, arguments.getAsccpId())
                .set(ASCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ASCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, arguments.getRoleOfAccManifestId())
                .returning(ASCCP_MANIFEST.ASCCP_MANIFEST_ID).fetchOne().getAsccpManifestId();
    }

    public ULong execute(UpdateAccArguments arguments) {
        ULong nextAccId = dslContext.insertInto(ACC)
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
                .set(ACC.REVISION_NUM, arguments.getRevisionNum())
                .set(ACC.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ACC.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(ACC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(ACC.IS_ABSTRACT, arguments.getAbstract() ? (byte) 1: 0)
                .set(ACC.PREV_ACC_ID, arguments.getPrevAccId())
                .returning(ACC.ACC_ID).fetchOne().getAccId();

        dslContext.update(ACC)
                .set(ACC.NEXT_ACC_ID, nextAccId)
                .where(ACC.ACC_ID.eq(arguments.getPrevAccId()))
                .execute();
        return nextAccId;
    }

    public void execute(UpdateAccManifestArguments arguments) {
        dslContext.update(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_ID, arguments.getAccId())
                .set(ACC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ACC_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, arguments.getBasedAccManifestId())
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(arguments.getAccManifestId()))
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
                .set(BCCP.REVISION_NUM, arguments.getRevisionNum())
                .set(BCCP.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(BCCP.REVISION_ACTION, arguments.getRevisionAction().getValue())
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
                .set(ASCCP.REUSABLE_INDICATOR, arguments.getReuseableIndicator()? (byte) 1: 0)
                .set(ASCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ASCCP.CREATED_BY, arguments.getCreatedBy())
                .set(ASCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ASCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ASCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ASCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ASCCP.STATE, arguments.getState().name())
                .set(ASCCP.REVISION_NUM, arguments.getRevisionNum())
                .set(ASCCP.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ASCCP.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(ASCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(ASCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1: 0)
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
                .set(BCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(arguments.getBccpManifestId()))
                .execute();
    }

    public void execute(UpdateAsccpManifestArguments arguments) {
        dslContext.update(ASCCP_MANIFEST)
                .set(ASCCP_MANIFEST.ASCCP_ID, arguments.getAsccpId())
                .set(ASCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ASCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, arguments.getRoleOfAccManifestId())
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(arguments.getAsccpManifestId()))
                .execute();
    }
}
