package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ABIE;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ABIERepository implements SrtRepository<ABIE> {
    
    @Autowired
    private DSLContext dslContext;

    @Override
    public List<ABIE> findAll() {
        return dslContext.select(Tables.ABIE.ABIE_ID,
                Tables.ABIE.BASED_ACC_ID,
                Tables.ABIE.BIZ_CTX_ID,
                Tables.ABIE.OWNER_TOP_LEVEL_ABIE_ID,
                Tables.ABIE.LAST_UPDATE_TIMESTAMP,
                Tables.ABIE.STATE,
                Tables.ABIE.LAST_UPDATED_BY,
                Tables.ABIE.STATUS,
                Tables.ABIE.VERSION,
                Tables.ABIE.BIZ_TERM,
                Tables.ABIE.CLIENT_ID,
                Tables.ABIE.CREATED_BY,
                Tables.ABIE.DEFINITION,
                Tables.ABIE.GUID,
                Tables.ABIE.REMARK,
                Tables.ABIE.CREATION_TIMESTAMP)
                .from(Tables.ABIE).fetchInto(ABIE.class);
    }

    @Override
    public ABIE findById(long id) {
        if (id <= 0L) {
            return null;
        }
        return dslContext.select(Tables.ABIE.ABIE_ID,
                Tables.ABIE.BASED_ACC_ID,
                Tables.ABIE.BIZ_CTX_ID,
                Tables.ABIE.OWNER_TOP_LEVEL_ABIE_ID,
                Tables.ABIE.LAST_UPDATE_TIMESTAMP,
                Tables.ABIE.STATE,
                Tables.ABIE.LAST_UPDATED_BY,
                Tables.ABIE.STATUS,
                Tables.ABIE.VERSION,
                Tables.ABIE.BIZ_TERM,
                Tables.ABIE.CLIENT_ID,
                Tables.ABIE.CREATED_BY,
                Tables.ABIE.DEFINITION,
                Tables.ABIE.GUID,
                Tables.ABIE.REMARK,
                Tables.ABIE.CREATION_TIMESTAMP)
                .from(Tables.ABIE).where(Tables.ABIE.ABIE_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ABIE.class);
    }

    public List<ABIE> findByOwnerTopLevelAbieId(long ownerTopLevelAbieId) {
        if (ownerTopLevelAbieId <= 0L) {
            return Collections.emptyList();
        }
        return dslContext.select(Tables.ABIE.ABIE_ID,
                Tables.ABIE.BASED_ACC_ID,
                Tables.ABIE.BIZ_CTX_ID,
                Tables.ABIE.OWNER_TOP_LEVEL_ABIE_ID,
                Tables.ABIE.LAST_UPDATE_TIMESTAMP,
                Tables.ABIE.STATE,
                Tables.ABIE.LAST_UPDATED_BY,
                Tables.ABIE.STATUS,
                Tables.ABIE.VERSION,
                Tables.ABIE.BIZ_TERM,
                Tables.ABIE.CLIENT_ID,
                Tables.ABIE.CREATED_BY,
                Tables.ABIE.DEFINITION,
                Tables.ABIE.GUID,
                Tables.ABIE.REMARK,
                Tables.ABIE.CREATION_TIMESTAMP)
                .from(Tables.ABIE).where(Tables.ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(ABIE.class);
    }

}
