package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.oagi.score.data.BBIE;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static org.jooq.impl.DSL.and;

@Repository
public class BBIERepository implements ScoreRepository<BBIE, String> {

    @Autowired
    private DSLContext dslContext;

    private SelectJoinStep<Record> getSelectOnConditionStep() {
        return dslContext.select(Tables.BBIE.BBIE_ID,
                Tables.BBIE.GUID,
                Tables.BBIE.BASED_BCC_MANIFEST_ID,
                Tables.BBIE.FROM_ABIE_ID,
                Tables.BBIE.TO_BBIEP_ID,
                Tables.BBIE.BDT_PRI_RESTRI_ID,
                Tables.BBIE.CODE_LIST_ID,
                Tables.BBIE.AGENCY_ID_LIST_ID,
                Tables.BBIE.CARDINALITY_MAX,
                Tables.BBIE.CARDINALITY_MIN,
                Tables.BBIE.FACET_MIN_LENGTH.as("min_length"),
                Tables.BBIE.FACET_MAX_LENGTH.as("max_length"),
                Tables.BBIE.FACET_PATTERN.as("pattern"),
                Tables.BBIE.DEFAULT_VALUE,
                Tables.BBIE.IS_NILLABLE.as("nillable"),
                Tables.BBIE.FIXED_VALUE,
                Tables.BBIE.IS_NULL.as("nill"),
                Tables.BBIE.DEFINITION,
                Tables.BBIE.REMARK,
                Tables.BBIE.EXAMPLE,
                Tables.BBIE.CREATED_BY,
                Tables.BBIE.CREATION_TIMESTAMP,
                Tables.BBIE.LAST_UPDATED_BY,
                Tables.BBIE.LAST_UPDATE_TIMESTAMP,
                Tables.BBIE.SEQ_KEY,
                Tables.BBIE.IS_USED.as("used"),
                Tables.BBIE.OWNER_TOP_LEVEL_ASBIEP_ID)
                .from(Tables.BBIE);
    }

    @Override
    public List<BBIE> findAll() {
        return getSelectOnConditionStep().fetchInto(BBIE.class);
    }

    @Override
    public BBIE findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return getSelectOnConditionStep()
                .where(Tables.BBIE.BBIE_ID.eq(id))
                .fetchOptionalInto(BBIE.class).orElse(null);
    }

    public List<BBIE> findByOwnerTopLevelAsbiepIdsAndUsed(Collection<String> ownerTopLevelAsbiepIds, boolean used) {
        return getSelectOnConditionStep()
                .where(and(
                        (ownerTopLevelAsbiepIds.size() == 1) ?
                                Tables.BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(
                                        ownerTopLevelAsbiepIds.iterator().next()) :
                                Tables.BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.in(
                                        ownerTopLevelAsbiepIds),
                        Tables.BBIE.IS_USED.eq((byte) (used ? 1 : 0))))
                .fetchInto(BBIE.class);
    }

}
