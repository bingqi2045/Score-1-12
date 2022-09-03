package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.Record17;
import org.jooq.Record20;
import org.jooq.SelectJoinStep;
import org.jooq.types.ULong;
import org.oagi.score.data.BBIESC;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;

@Repository
public class BBIESCRepository implements ScoreRepository<BBIESC, String> {

    @Autowired
    private DSLContext dslContext;

    private SelectJoinStep<Record20<String, String, String, ULong, ULong,
            String, String, Integer, Integer, ULong,
            ULong, String, String, String, String,
            String, String, String, Byte, String>> getSelectJoinStep() {
        return dslContext.select(
                Tables.BBIE_SC.BBIE_SC_ID,
                Tables.BBIE_SC.GUID,
                Tables.BBIE_SC.BBIE_ID,
                Tables.BBIE_SC.BASED_DT_SC_MANIFEST_ID,
                Tables.BBIE_SC.DT_SC_PRI_RESTRI_ID,
                Tables.BBIE_SC.CODE_LIST_ID,
                Tables.BBIE_SC.AGENCY_ID_LIST_ID,
                Tables.BBIE_SC.CARDINALITY_MIN,
                Tables.BBIE_SC.CARDINALITY_MAX,
                Tables.BBIE_SC.FACET_MIN_LENGTH.as("min_length"),
                Tables.BBIE_SC.FACET_MAX_LENGTH.as("max_length"),
                Tables.BBIE_SC.FACET_PATTERN.as("pattern"),
                Tables.BBIE_SC.DEFAULT_VALUE,
                Tables.BBIE_SC.FIXED_VALUE,
                Tables.BBIE_SC.DEFINITION,
                Tables.BBIE_SC.REMARK,
                Tables.BBIE_SC.BIZ_TERM,
                Tables.BBIE_SC.EXAMPLE,
                Tables.BBIE_SC.IS_USED.as("used"),
                Tables.BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID)
                .from(Tables.BBIE_SC);
    }

    @Override
    public List<BBIESC> findAll() {
        return getSelectJoinStep().fetchInto(BBIESC.class);
    }

    @Override
    public BBIESC findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return getSelectJoinStep()
                .where(Tables.BBIE_SC.BBIE_SC_ID.eq(id))
                .fetchOptionalInto(BBIESC.class).orElse(null);
    }

    public List<BBIESC> findByOwnerTopLevelAsbiepIdsAndUsed(Collection<String> ownerTopLevelAsbiepIds, boolean used) {
        return getSelectJoinStep()
                .where(and(
                        (ownerTopLevelAsbiepIds.size() == 1) ?
                                Tables.BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.eq(
                                        ownerTopLevelAsbiepIds.iterator().next()) :
                                Tables.BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.in(
                                        ownerTopLevelAsbiepIds),
                        Tables.BBIE_SC.IS_USED.eq((byte) (used ? 1 : 0))))
                .fetchInto(BBIESC.class);
    }

}
