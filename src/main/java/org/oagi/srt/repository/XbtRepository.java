package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.Xbt;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class XbtRepository implements SrtRepository<Xbt> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<Xbt> findAll() {
        return dslContext.select(Tables.XBT.XBT_ID, Tables.XBT.CREATED_BY, Tables.XBT.CURRENT_XBT_ID,
                    Tables.XBT.LAST_UPDATED_BY, Tables.XBT.MODULE_ID, Tables.XBT.NAME, Tables.XBT.OWNER_USER_ID,
                    Tables.XBT.RELEASE_ID, Tables.XBT.SUBTYPE_OF_XBT_ID, Tables.XBT.BUILTIN_TYPE,
                    Tables.XBT.CREATION_TIMESTAMP, Tables.XBT.IS_DEPRECATED, Tables.XBT.JBT_DRAFT05_MAP,
                    Tables.XBT.LAST_UPDATE_TIMESTAMP, Tables.XBT.REVISION_ACTION, Tables.XBT.REVISION_DOC,
                    Tables.XBT.REVISION_NUM, Tables.XBT.REVISION_TRACKING_NUM, Tables.XBT.SCHEMA_DEFINITION,
                    Tables.XBT.STATE)
                .from(Tables.XBT)
                .fetchInto(Xbt.class);
    }

    @Override
    public Xbt findById(long id) {
        return dslContext.select(Tables.XBT.XBT_ID, Tables.XBT.CREATED_BY, Tables.XBT.CURRENT_XBT_ID,
                Tables.XBT.LAST_UPDATED_BY, Tables.XBT.MODULE_ID, Tables.XBT.NAME, Tables.XBT.OWNER_USER_ID,
                Tables.XBT.RELEASE_ID, Tables.XBT.SUBTYPE_OF_XBT_ID, Tables.XBT.BUILTIN_TYPE,
                Tables.XBT.CREATION_TIMESTAMP, Tables.XBT.IS_DEPRECATED, Tables.XBT.JBT_DRAFT05_MAP,
                Tables.XBT.LAST_UPDATE_TIMESTAMP, Tables.XBT.REVISION_ACTION, Tables.XBT.REVISION_DOC,
                Tables.XBT.REVISION_NUM, Tables.XBT.REVISION_TRACKING_NUM, Tables.XBT.SCHEMA_DEFINITION,
                Tables.XBT.STATE)
                .from(Tables.XBT)
                .where(Tables.XBT.XBT_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(Xbt.class);
    }

}
