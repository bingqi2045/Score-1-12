package org.oagi.srt.repository;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.Xbt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.XBT;
import static org.oagi.srt.entity.jooq.Tables.XBT_MANIFEST;

@Repository
public class XbtRepository implements SrtRepository<Xbt> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record20<
            ULong, ULong, ULong, ULong, String,
            ULong, ULong, ULong, String, Timestamp,
            Byte, String, String, Timestamp, Byte,
            String, Integer, Integer, String, Integer>> getSelectJoinStep() {
        return dslContext.select(XBT.XBT_ID, XBT.CREATED_BY,
                XBT.LAST_UPDATED_BY, XBT_MANIFEST.MODULE_ID, XBT.NAME, XBT.OWNER_USER_ID,
                XBT_MANIFEST.RELEASE_ID, XBT.SUBTYPE_OF_XBT_ID, XBT.BUILTIN_TYPE,
                XBT.CREATION_TIMESTAMP, XBT.IS_DEPRECATED, XBT.JBT_DRAFT05_MAP, XBT.OPENAPI30_MAP,
                XBT.LAST_UPDATE_TIMESTAMP, XBT.REVISION_ACTION, XBT.REVISION_DOC,
                XBT.REVISION_NUM, XBT.REVISION_TRACKING_NUM, XBT.SCHEMA_DEFINITION,
                XBT.STATE)
                .from(XBT)
                .join(XBT_MANIFEST).on(XBT.XBT_ID.eq(XBT_MANIFEST.XBT_ID));
    }

    @Override
    public List<Xbt> findAll() {
        return getSelectJoinStep()
                .fetchInto(Xbt.class);
    }

    @Override
    public Xbt findById(long id) {
        return getSelectJoinStep()
                .where(XBT.XBT_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(Xbt.class);
    }

}
