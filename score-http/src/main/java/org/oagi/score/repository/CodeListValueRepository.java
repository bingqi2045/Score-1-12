package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.CodeListValue;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class CodeListValueRepository implements ScoreRepository<CodeListValue, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<CodeListValue> findAll() {
        return dslContext.select(Tables.CODE_LIST_VALUE.CODE_LIST_ID, Tables.CODE_LIST_VALUE.CODE_LIST_VALUE_ID,
                Tables.CODE_LIST_VALUE.DEFINITION, Tables.CODE_LIST_VALUE.DEFINITION_SOURCE,
                Tables.CODE_LIST_VALUE.MEANING, Tables.CODE_LIST_VALUE.VALUE)
                .from(Tables.CODE_LIST_VALUE).fetchInto(CodeListValue.class);
    }

    @Override
    public CodeListValue findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.CODE_LIST_VALUE.CODE_LIST_ID, Tables.CODE_LIST_VALUE.CODE_LIST_VALUE_ID,
                Tables.CODE_LIST_VALUE.DEFINITION, Tables.CODE_LIST_VALUE.DEFINITION_SOURCE,
                Tables.CODE_LIST_VALUE.MEANING, Tables.CODE_LIST_VALUE.VALUE)
                .from(Tables.CODE_LIST_VALUE).where(Tables.CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(id))
                .fetchOneInto(CodeListValue.class);
    }

}
