package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.CodeList;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class CodeListRepository implements ScoreRepository<CodeList, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<CodeList> findAll() {
        return dslContext.select(Tables.CODE_LIST.fields())
                .from(Tables.CODE_LIST).fetchInto(CodeList.class);
    }

    @Override
    public CodeList findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.CODE_LIST.fields())
                .from(Tables.CODE_LIST).where(Tables.CODE_LIST.CODE_LIST_ID.eq(id))
                .fetchOneInto(CodeList.class);
    }

}
