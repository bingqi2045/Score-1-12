package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.ContextScheme;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CtxSchemeRepository implements ScoreRepository<ContextScheme, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<ContextScheme> findAll() {
        return dslContext.select(Tables.CTX_SCHEME.fields()).from(Tables.CTX_SCHEME).fetchInto(ContextScheme.class);
    }

    @Override
    public ContextScheme findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.CTX_SCHEME.fields())
                .from(Tables.CTX_SCHEME).where(Tables.CTX_SCHEME.CTX_SCHEME_ID.eq(id))
                .fetchOneInto(ContextScheme.class);
    }

}
