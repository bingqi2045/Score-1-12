package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TopLevelAbieRepository implements SrtRepository<TopLevelAbie> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<TopLevelAbie> findAll() {
        return dslContext.select(Tables.TOP_LEVEL_ABIE.fields()).from(Tables.TOP_LEVEL_ABIE)
                .fetchInto(TopLevelAbie.class);
    }

    @Override
    public TopLevelAbie findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(Tables.TOP_LEVEL_ABIE.fields()).from(Tables.TOP_LEVEL_ABIE)
                .where(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(TopLevelAbie.class);
    }

    public List<TopLevelAbie> findByIdIn(List<BigInteger> topLevelAbieIds) {
        return dslContext.select(Tables.TOP_LEVEL_ABIE.fields()).from(Tables.TOP_LEVEL_ABIE)
                .where(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.in(topLevelAbieIds))
                .fetchInto(TopLevelAbie.class);
    }

    public void updateTopLevelAbieLastUpdated(BigInteger userId, BigInteger topLevelAbieId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        dslContext.update(Tables.TOP_LEVEL_ABIE)
                .set(Tables.TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                .set(Tables.TOP_LEVEL_ABIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .where(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .execute();
    }
}
