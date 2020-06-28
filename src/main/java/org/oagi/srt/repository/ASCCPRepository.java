package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.ASCCP;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class ASCCPRepository implements SrtRepository<ASCCP> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record> getSelectOnConditionStep() {
        return dslContext.select(
                Tables.ASCCP_MANIFEST.ASCCP_MANIFEST_ID,
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM,
                Tables.ASCCP.DEN,
                Tables.ASCCP.DEFINITION,
                Tables.ASCCP.DEFINITION_SOURCE,
                Tables.ASCCP.ROLE_OF_ACC_ID,
                Tables.ASCCP.NAMESPACE_ID,
                Tables.ASCCP.CREATED_BY,
                Tables.ASCCP.OWNER_USER_ID,
                Tables.ASCCP.LAST_UPDATED_BY,
                Tables.ASCCP.CREATION_TIMESTAMP,
                Tables.ASCCP.LAST_UPDATE_TIMESTAMP,
                Tables.ASCCP.STATE,
                Tables.ASCCP_MANIFEST.RELEASE_ID,
                Tables.RELEASE.RELEASE_NUM,
                Tables.ASCCP_MANIFEST.REVISION_ID,
                Tables.REVISION.REVISION_NUM,
                Tables.REVISION.REVISION_TRACKING_NUM,
                Tables.ASCCP.REUSABLE_INDICATOR,
                Tables.ASCCP.IS_DEPRECATED.as("deprecated"),
                Tables.ASCCP.IS_NILLABLE.as("nillable"))
                .from(Tables.ASCCP)
                .join(Tables.ASCCP_MANIFEST)
                .on(Tables.ASCCP.ASCCP_ID.eq(Tables.ASCCP_MANIFEST.ASCCP_ID))
                .join(Tables.RELEASE)
                .on(Tables.ASCCP_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(Tables.REVISION)
                .on(Tables.ASCCP_MANIFEST.REVISION_ID.eq(Tables.REVISION.REVISION_ID));
    }

    @Override
    public List<ASCCP> findAll() {
        return getSelectOnConditionStep().fetchInto(ASCCP.class);
    }

    @Override
    public List<ASCCP> findAllByReleaseId(BigInteger releaseId) {
        return getSelectOnConditionStep()
                .where(Tables.ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchInto(ASCCP.class);
    }

    @Override
    public ASCCP findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return getSelectOnConditionStep()
                .where(Tables.ASCCP.ASCCP_ID.eq(ULong.valueOf(id)))
                .fetchOptionalInto(ASCCP.class).orElse(null);
    }
}
