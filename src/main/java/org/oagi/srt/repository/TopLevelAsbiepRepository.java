package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.TopLevelAsbiep;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TopLevelAsbiepRepository implements SrtRepository<TopLevelAsbiep> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<TopLevelAsbiep> findAll() {
        return dslContext.select(Tables.TOP_LEVEL_ASBIEP.fields()).from(Tables.TOP_LEVEL_ASBIEP)
                .fetchInto(TopLevelAsbiep.class);
    }

    @Override
    public TopLevelAsbiep findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(Tables.TOP_LEVEL_ASBIEP.fields()).from(Tables.TOP_LEVEL_ASBIEP)
                .where(Tables.TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(TopLevelAsbiep.class);
    }

    public List<TopLevelAsbiep> findByIdIn(List<BigInteger> topLevelAbieIds) {
        return dslContext.select(Tables.TOP_LEVEL_ASBIEP.fields()).from(Tables.TOP_LEVEL_ASBIEP)
                .where(Tables.TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.in(topLevelAbieIds))
                .fetchInto(TopLevelAsbiep.class);
    }

    public void updateTopLevelAbieLastUpdated(BigInteger userId, BigInteger topLevelAbieId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        dslContext.update(Tables.TOP_LEVEL_ASBIEP)
                .set(Tables.TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                .set(Tables.TOP_LEVEL_ASBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                .where(Tables.TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)))
                .execute();
    }
}
