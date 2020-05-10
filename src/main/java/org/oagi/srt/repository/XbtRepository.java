package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.Record19;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.Xbt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class XbtRepository implements SrtRepository<Xbt> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record19<ULong, ULong, ULong, ULong, String, ULong, ULong, ULong, String, LocalDateTime, Byte, String, String, LocalDateTime, String, UInteger, UInteger, String, Integer>> getSelectJoinStep() {
        return dslContext.select(XBT.XBT_ID, XBT.CREATED_BY,
                XBT.LAST_UPDATED_BY, XBT_MANIFEST.MODULE_ID, XBT.NAME, XBT.OWNER_USER_ID,
                XBT_MANIFEST.RELEASE_ID, XBT.SUBTYPE_OF_XBT_ID, XBT.BUILTIN_TYPE,
                XBT.CREATION_TIMESTAMP, XBT.IS_DEPRECATED, XBT.JBT_DRAFT05_MAP, XBT.OPENAPI30_MAP,
                XBT.LAST_UPDATE_TIMESTAMP, XBT.REVISION_DOC,
                REVISION.REVISION_NUM, REVISION.REVISION_TRACKING_NUM, XBT.SCHEMA_DEFINITION,
                XBT.STATE)
                .from(XBT)
                .join(XBT_MANIFEST).on(XBT.XBT_ID.eq(XBT_MANIFEST.XBT_ID))
                .join(REVISION).on(XBT_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID));
    }

    @Override
    public List<Xbt> findAll() {
        return getSelectJoinStep()
                .fetchInto(Xbt.class);
    }

    @Override
    public Xbt findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return getSelectJoinStep()
                .where(XBT.XBT_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(Xbt.class);
    }

}
