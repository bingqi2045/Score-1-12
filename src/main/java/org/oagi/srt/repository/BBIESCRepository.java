package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BBIESC;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BBIESCRepository implements SrtRepository<BBIESC> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<BBIESC> findAll() {
        return dslContext.select(Tables.BBIE_SC.fields())
                .select(Tables.BBIE_SC.IS_USED.as("used"))
                .from(Tables.BBIE_SC).fetchInto(BBIESC.class);
    }

    @Override
    public BBIESC findById(long id) {
        return dslContext.select(Tables.BBIE_SC.fields())
                .select(Tables.BBIE_SC.IS_USED.as("used"))
                .from(Tables.BBIE_SC)
                .where(Tables.BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(BBIESC.class);
    }

    public List<BBIESC> findByOwnerTopLevelAbieIdAndUsed(long ownerTopLevelAbieId, boolean used) {
        return dslContext.select(Tables.BBIE_SC.fields()).select(Tables.BBIE_SC.IS_USED.as("used"))
                .from(Tables.BBIE_SC)
                .where(Tables.BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)),
                        Tables.BBIE_SC.IS_USED.eq((byte) (used ? 1 : 0)))
                .fetchInto(BBIESC.class);
    }

}
