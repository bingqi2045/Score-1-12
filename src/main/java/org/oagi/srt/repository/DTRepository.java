package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.DT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class DTRepository implements SrtRepository<DT> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record> getSelectOnConditionStep() {
        return dslContext.select(
                DT.DT_ID,
                DT.GUID,
                DT.TYPE,
                DT.VERSION_NUM,
                DT.PREVIOUS_VERSION_DT_ID,
                DT.DATA_TYPE_TERM,
                DT.QUALIFIER,
                DT.BASED_DT_ID,
                DT.DEN,
                DT.CONTENT_COMPONENT_DEN,
                DT.DEFINITION,
                DT.DEFINITION_SOURCE,
                DT.CONTENT_COMPONENT_DEFINITION,
                DT.REVISION_DOC,
                DT.STATE,
                DT_MANIFEST.MODULE_ID,
                DT.CREATED_BY,
                DT.LAST_UPDATED_BY,
                DT.OWNER_USER_ID,
                DT.CREATION_TIMESTAMP,
                DT.LAST_UPDATE_TIMESTAMP,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                DT_MANIFEST.RELEASE_ID,
                DT.IS_DEPRECATED.as("deprecated"),
                MODULE.MODULE_.as("module"))
                .from(DT)
                .join(DT_MANIFEST)
                .on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .join(REVISION)
                .on(DT.REVISION_ID.eq(REVISION.REVISION_ID))
                .leftJoin(MODULE).on(DT_MANIFEST.MODULE_ID.eq(MODULE.MODULE_ID));
    }

    @Override
    public List<DT> findAll() {
        return getSelectOnConditionStep().fetchInto(DT.class);
    }

    @Override
    public DT findById(long id) {
        if (id <= 0L) {
            return null;
        }
        return getSelectOnConditionStep()
                .where(DT.DT_ID.eq(ULong.valueOf(id)))
                .fetchOptionalInto(DT.class).orElse(null);
    }
}
