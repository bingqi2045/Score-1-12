package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.ContextSchemeValue;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CtxSchemeValueRepository implements ScoreRepository<ContextSchemeValue, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<ContextSchemeValue> findAll() {
        return dslContext.select(Tables.CTX_SCHEME_VALUE.fields()).from(Tables.CTX_SCHEME_VALUE)
                .fetchInto(ContextSchemeValue.class);
    }

    @Override
    public ContextSchemeValue findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.CTX_SCHEME_VALUE.fields()).from(Tables.CTX_SCHEME_VALUE)
                .where(Tables.CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID.eq(id))
                .fetchOneInto(ContextSchemeValue.class);
    }
}
