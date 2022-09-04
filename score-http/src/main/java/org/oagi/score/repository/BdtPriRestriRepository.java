package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.SelectJoinStep;
import org.jooq.types.ULong;
import org.oagi.score.data.BdtPriRestri;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class BdtPriRestriRepository implements ScoreRepository<BdtPriRestri, String> {

    @Autowired
    private DSLContext dslContext;

    private SelectJoinStep<Record6<String, String, String, String, String, Byte>> getSelectJoinStep() {
        return dslContext.select(
                Tables.BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID,
                Tables.BDT_PRI_RESTRI.BDT_ID,
                Tables.BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                Tables.BDT_PRI_RESTRI.CODE_LIST_ID,
                Tables.BDT_PRI_RESTRI.AGENCY_ID_LIST_ID,
                Tables.BDT_PRI_RESTRI.IS_DEFAULT.as("defaulted")
        ).from(Tables.BDT_PRI_RESTRI);
    }

    @Override
    public List<BdtPriRestri> findAll() {
        return getSelectJoinStep().fetchInto(BdtPriRestri.class);
    }

    @Override
    public BdtPriRestri findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return getSelectJoinStep()
                .where(Tables.BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID.eq(id))
                .fetchOptionalInto(BdtPriRestri.class).orElse(null);
    }
}
