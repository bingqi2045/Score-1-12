package org.oagi.srt.repository;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.ASCC;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ASCCRepository implements SrtRepository<ASCC> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record21<
            ULong, String, Integer, Integer, Integer,
            ULong, ULong, String, String, String,
            ULong, ULong, ULong, LocalDateTime, LocalDateTime,
            String, Integer, Integer, Byte, ULong,
            Byte>> getSelectJoinStep() {
        return dslContext.select(
                Tables.ASCC.ASCC_ID,
                Tables.ASCC.GUID,
                Tables.ASCC.CARDINALITY_MIN,
                Tables.ASCC.CARDINALITY_MAX,
                Tables.ASCC.SEQ_KEY,
                Tables.ASCC.FROM_ACC_ID,
                Tables.ASCC.TO_ASCCP_ID,
                Tables.ASCC.DEN,
                Tables.ASCC.DEFINITION,
                Tables.ASCC.DEFINITION_SOURCE,
                Tables.ASCC.CREATED_BY,
                Tables.ASCC.OWNER_USER_ID,
                Tables.ASCC.LAST_UPDATED_BY,
                Tables.ASCC.CREATION_TIMESTAMP,
                Tables.ASCC.LAST_UPDATE_TIMESTAMP,
                Tables.ASCC.STATE,
                Tables.ASCC.REVISION_NUM,
                Tables.ASCC.REVISION_TRACKING_NUM,
                Tables.ASCC.REVISION_ACTION,
                Tables.ASCC_MANIFEST.RELEASE_ID,
                Tables.ASCC.IS_DEPRECATED.as("deprecated"))
                .from(Tables.ASCC)
                .join(Tables.ASCC_MANIFEST)
                .on(Tables.ASCC.ASCC_ID.eq(Tables.ASCC_MANIFEST.ASCC_ID));
    }

    @Override
    public List<ASCC> findAll() {
        return getSelectJoinStep().fetchInto(ASCC.class);
    }

    @Override
    public ASCC findById(long id) {
        if (id <= 0L) {
            return null;
        }
        return getSelectJoinStep()
                .where(Tables.ASCC.ASCC_ID.eq(ULong.valueOf(id)))
                .fetchOptionalInto(ASCC.class).orElse(null);
    }
}
