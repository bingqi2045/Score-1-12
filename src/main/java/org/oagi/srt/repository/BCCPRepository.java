package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCP;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BCCPRepository implements SrtRepository<BCCP> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record> getSelectOnConditionStep() {
        return dslContext.select(
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.PROPERTY_TERM,
                BCCP.REPRESENTATION_TERM,
                BCCP.DEFAULT_VALUE,
                BCCP.BDT_ID,
                BCCP.DEN,
                BCCP.DEFINITION,
                BCCP.DEFINITION_SOURCE,
                BCCP_MANIFEST.MODULE_ID,
                BCCP.NAMESPACE_ID,
                BCCP.CREATED_BY,
                BCCP.OWNER_USER_ID,
                BCCP.LAST_UPDATED_BY,
                BCCP.CREATION_TIMESTAMP,
                BCCP.LAST_UPDATE_TIMESTAMP,
                BCCP.STATE,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                REVISION.REVISION_ACTION,
                BCCP_MANIFEST.RELEASE_ID,
                BCCP.IS_DEPRECATED.as("deprecated"),
                BCCP.IS_NILLABLE.as("nillable"),
                Tables.MODULE.MODULE_.as("module"))
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .join(REVISION)
                .on(BCCP.REVISION_ID.eq(REVISION.REVISION_ID))
                .leftJoin(Tables.MODULE).on(BCCP_MANIFEST.MODULE_ID.eq(Tables.MODULE.MODULE_ID));
    }

    @Override
    public List<BCCP> findAll() {
        return getSelectOnConditionStep().fetchInto(BCCP.class);
    }

    @Override
    public BCCP findById(long id) {
        if (id <= 0L) {
            return null;
        }
        return getSelectOnConditionStep()
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(id)))
                .fetchOptionalInto(BCCP.class).orElse(null);
    }
}
