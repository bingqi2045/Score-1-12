package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.repo.cc_arguments.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
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
        return new InsertAccArguments();
    }

    public InsertBccpArguments insertBccp() {
        return new InsertBccpArguments(this);
    }

    public InsertAccManifestArguments insertAccManifestArguments() {
        return new InsertAccManifestArguments();
    }

    public InsertBccpManifestArguments insertBccpManifest() {
        return new InsertBccpManifestArguments(this);
    }

    public ULong insertBccpManifest(InsertBccpManifestArguments arguments) {
        return dslContext.insertInto(BCCP_MANIFEST)
                .set(BCCP_MANIFEST.BCCP_MANIFEST_ID, arguments.getBccpManifestId())
                .set(BCCP_MANIFEST.BCCP_ID, arguments.getBccpId())
                .set(BCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .returning(BCCP_MANIFEST.BCCP_MANIFEST_ID).fetchOne().getBccpManifestId();
    }

    public UpdateAccArguments updateAccArguments() {
        return new UpdateAccArguments();
    }

    public UpdateBccpArguments updateBccpArguments(BccpRecord bccp) {
        return new UpdateBccpArguments(this, bccp);
    }

    public UpdateAccManifestArguments updateAccManifestArguments() {
        return new UpdateAccManifestArguments();
    }

    public UpdateBccpManifestArguments updateBccpManifestArguments(BccpManifestRecord bccpManifestRecord) {
        return new UpdateBccpManifestArguments(this, bccpManifestRecord);
    }

    public ULong execute(InsertAccArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(InsertAccManifestArguments arguments) {
        return arguments.execute(dslContext);
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

    public ULong execute(UpdateAccArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(UpdateAccManifestArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(UpdateBccpArguments arguments) {
        ULong nextBccpId = dslContext.insertInto(BCCP)
                .set(BCCP.GUID, arguments.getGuid())
                .set(BCCP.CREATED_BY, arguments.getCreatedBy())
                .set(BCCP.CREATION_TIMESTAMP, arguments.getCreatedTimestamp())
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

    public void execute(UpdateBccpManifestArguments arguments) {
        dslContext.update(BCCP_MANIFEST)
                .set(BCCP_MANIFEST.BCCP_ID, arguments.getBccpId())
                .set(BCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(arguments.getBccpManifestId()))
                .execute();
    }
}
