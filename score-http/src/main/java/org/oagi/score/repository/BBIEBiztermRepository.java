package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.BbieBizterm;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class BBIEBiztermRepository implements ScoreRepository<BbieBizterm, BigInteger> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<BbieBizterm> findAll() {
        return dslContext.select(
                Tables.BBIE_BIZTERM.BBIE_BIZTERM_ID,
                Tables.BBIE_BIZTERM.BCC_BIZTERM_ID,
                Tables.BBIE_BIZTERM.BBIE_ID,
                Tables.BBIE_BIZTERM.PRIMARY_INDICATOR,
                Tables.BBIE_BIZTERM.TYPE_CODE,
                Tables.BCC_BIZTERM.BUSINESS_TERM_ID,
                Tables.BUSINESS_TERM.BUSINESS_TERM_,
                Tables.BUSINESS_TERM.GUID,
                Tables.BUSINESS_TERM.EXTERNAL_REF_URI,
                Tables.BBIE_BIZTERM.CREATED_BY,
                Tables.BBIE_BIZTERM.LAST_UPDATED_BY,
                Tables.BBIE_BIZTERM.CREATION_TIMESTAMP,
                Tables.BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP
                ).from(Tables.BBIE_BIZTERM)
                .innerJoin(Tables.BCC_BIZTERM)
                .on(Tables.BBIE_BIZTERM.BCC_BIZTERM_ID.eq(Tables.BCC_BIZTERM.BCC_BIZTERM_ID))
                .innerJoin(Tables.BUSINESS_TERM)
                .on(Tables.BCC_BIZTERM.BUSINESS_TERM_ID.eq(Tables.BUSINESS_TERM.BUSINESS_TERM_ID))
                .fetchInto(BbieBizterm.class);
    }

    @Override
    public BbieBizterm findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(
                Tables.BBIE_BIZTERM.BBIE_BIZTERM_ID,
                Tables.BBIE_BIZTERM.BCC_BIZTERM_ID,
                Tables.BBIE_BIZTERM.BBIE_ID,
                Tables.BBIE_BIZTERM.PRIMARY_INDICATOR,
                Tables.BBIE_BIZTERM.TYPE_CODE,
                Tables.BCC_BIZTERM.BUSINESS_TERM_ID,
                Tables.BUSINESS_TERM.BUSINESS_TERM_,
                Tables.BUSINESS_TERM.GUID,
                Tables.BUSINESS_TERM.EXTERNAL_REF_URI,
                Tables.BBIE_BIZTERM.CREATED_BY,
                Tables.BBIE_BIZTERM.LAST_UPDATED_BY,
                Tables.BBIE_BIZTERM.CREATION_TIMESTAMP,
                Tables.BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP
        ).from(Tables.BBIE_BIZTERM)
        .innerJoin(Tables.BCC_BIZTERM)
        .on(Tables.BBIE_BIZTERM.BCC_BIZTERM_ID.eq(Tables.BCC_BIZTERM.BCC_BIZTERM_ID))
        .innerJoin(Tables.BUSINESS_TERM)
        .on(Tables.BCC_BIZTERM.BUSINESS_TERM_ID.eq(Tables.BUSINESS_TERM.BUSINESS_TERM_ID))
        .where(Tables.BBIE_BIZTERM.BBIE_BIZTERM_ID.eq(ULong.valueOf(id)))
        .fetchOneInto(BbieBizterm.class);
    }
}
