package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BizCtx;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BizCtxRepository implements SrtRepository<BizCtx> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<BizCtx> findAll() {
        return dslContext.select(Tables.BIZ_CTX.fields()).from(Tables.BIZ_CTX).fetchInto(BizCtx.class);
    }

    @Override
    public BizCtx findById(long id) {
        if (id <= 0L) {
            return null;
        }
        return dslContext.select(Tables.BIZ_CTX.fields()).from(Tables.BIZ_CTX)
                .where(Tables.BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(id))).fetchOneInto(BizCtx.class);

    }

}
