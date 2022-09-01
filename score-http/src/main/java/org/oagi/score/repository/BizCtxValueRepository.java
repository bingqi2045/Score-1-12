package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.BusinessContextValue;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Repository
public class BizCtxValueRepository implements ScoreRepository<BusinessContextValue, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<BusinessContextValue> findAll() {
        return dslContext.select(Tables.BIZ_CTX_VALUE.fields()).from(Tables.BIZ_CTX_VALUE)
                .fetchInto(BusinessContextValue.class);
    }

    @Override
    public BusinessContextValue findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.BIZ_CTX_VALUE.fields()).from(Tables.BIZ_CTX_VALUE)
                .where(Tables.BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID.eq(id))
                .fetchOneInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> findByBizCtxId(String bizCtxId) {
        if (!StringUtils.hasLength(bizCtxId)) {
            return Collections.emptyList();
        }
        return dslContext.select(Tables.BIZ_CTX_VALUE.fields()).from(Tables.BIZ_CTX_VALUE)
                .where(Tables.BIZ_CTX_VALUE.BIZ_CTX_ID.eq(bizCtxId))
                .fetchInto(BusinessContextValue.class);
    }

}
