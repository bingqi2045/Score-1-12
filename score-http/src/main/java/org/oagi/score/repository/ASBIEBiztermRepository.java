package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.AsbieBizTerm;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static org.jooq.impl.DSL.inline;

@Repository
public class ASBIEBiztermRepository implements ScoreRepository<AsbieBizTerm, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<AsbieBizTerm> findAll() {
        return dslContext.select(
                Tables.ASBIE_BIZTERM.ASBIE_BIZTERM_ID,
                Tables.ASBIE_BIZTERM.ASCC_BIZTERM_ID,
                Tables.ASBIE_BIZTERM.ASBIE_ID,
                inline("ASBIE").as("bieType"),
                Tables.ASBIE_BIZTERM.PRIMARY_INDICATOR,
                Tables.ASBIE_BIZTERM.TYPE_CODE,
                Tables.ASCC_BIZTERM.BUSINESS_TERM_ID,
                Tables.BUSINESS_TERM.BUSINESS_TERM_,
                Tables.BUSINESS_TERM.GUID,
                Tables.BUSINESS_TERM.EXTERNAL_REF_URI,
                Tables.ASBIE_BIZTERM.CREATED_BY,
                Tables.ASBIE_BIZTERM.LAST_UPDATED_BY,
                Tables.ASBIE_BIZTERM.CREATION_TIMESTAMP,
                Tables.ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP
                ).from(Tables.ASBIE_BIZTERM)
                .innerJoin(Tables.ASCC_BIZTERM)
                .on(Tables.ASBIE_BIZTERM.ASCC_BIZTERM_ID.eq(Tables.ASCC_BIZTERM.ASCC_BIZTERM_ID))
                .innerJoin(Tables.BUSINESS_TERM)
                .on(Tables.ASCC_BIZTERM.BUSINESS_TERM_ID.eq(Tables.BUSINESS_TERM.BUSINESS_TERM_ID))
                .fetchInto(AsbieBizTerm.class);
    }

    @Override
    public AsbieBizTerm findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(
                Tables.ASBIE_BIZTERM.ASBIE_BIZTERM_ID,
                Tables.ASBIE_BIZTERM.ASCC_BIZTERM_ID,
                Tables.ASBIE_BIZTERM.ASBIE_ID,
                Tables.ASBIE_BIZTERM.PRIMARY_INDICATOR,
                Tables.ASBIE_BIZTERM.TYPE_CODE,
                Tables.ASCC_BIZTERM.BUSINESS_TERM_ID,
                Tables.BUSINESS_TERM.BUSINESS_TERM_,
                Tables.BUSINESS_TERM.GUID,
                Tables.BUSINESS_TERM.EXTERNAL_REF_URI,
                Tables.ASBIE_BIZTERM.CREATED_BY,
                Tables.ASBIE_BIZTERM.LAST_UPDATED_BY,
                Tables.ASBIE_BIZTERM.CREATION_TIMESTAMP,
                Tables.ASBIE.LAST_UPDATE_TIMESTAMP
        ).from(Tables.ASBIE_BIZTERM)
        .innerJoin(Tables.ASCC_BIZTERM)
        .on(Tables.ASBIE_BIZTERM.ASCC_BIZTERM_ID.eq(Tables.ASCC_BIZTERM.ASCC_BIZTERM_ID))
        .innerJoin(Tables.BUSINESS_TERM)
        .on(Tables.ASCC_BIZTERM.BUSINESS_TERM_ID.eq(Tables.BUSINESS_TERM.BUSINESS_TERM_ID))
        .where(Tables.ASBIE_BIZTERM.ASBIE_BIZTERM_ID.eq(id))
        .fetchOneInto(AsbieBizTerm.class);
    }
}
