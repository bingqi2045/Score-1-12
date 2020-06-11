package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.Record11;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.DTSC;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class DTSCRepository implements SrtRepository<DTSC> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record11<
            ULong, ULong, String, String, String,
            String, String, ULong, Integer, Integer,
            ULong>> getSelectJoinStep() {
        return dslContext.select(
                Tables.DT_SC_MANIFEST.DT_SC_MANIFEST_ID,
                Tables.DT_SC.DT_SC_ID,
                Tables.DT_SC.GUID,
                Tables.DT_SC.PROPERTY_TERM,
                Tables.DT_SC.REPRESENTATION_TERM,
                Tables.DT_SC.DEFINITION,
                Tables.DT_SC.DEFINITION_SOURCE,
                Tables.DT_SC.OWNER_DT_ID,
                Tables.DT_SC.CARDINALITY_MIN,
                Tables.DT_SC.CARDINALITY_MAX,
                Tables.DT_SC.BASED_DT_SC_ID)
                .from(Tables.DT_SC)
                .join(Tables.DT_SC_MANIFEST).on(Tables.DT_SC.DT_SC_ID.eq(Tables.DT_SC_MANIFEST.DT_SC_ID));
    }

    @Override
    public List<DTSC> findAll() {
        return getSelectJoinStep().fetchInto(DTSC.class);
    }

    @Override
    public List<DTSC> findAllByReleaseId(BigInteger releaseId) {
        return getSelectJoinStep()
                .where(Tables.DT_SC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchInto(DTSC.class);
    }

    @Override
    public DTSC findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return getSelectJoinStep()
                .where(Tables.DT_SC.DT_SC_ID.eq(ULong.valueOf(id)))
                .fetchOptionalInto(DTSC.class).orElse(null);
    }
}
