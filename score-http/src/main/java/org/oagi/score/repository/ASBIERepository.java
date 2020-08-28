package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.ASBIE;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ASBIERepository implements SrtRepository<ASBIE> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<ASBIE> findAll() {
        return dslContext.select(Tables.ASBIE.ASBIE_ID,
                Tables.ASBIE.GUID,
                Tables.ASBIE.FROM_ABIE_ID,
                Tables.ASBIE.TO_ASBIEP_ID,
                Tables.ASBIE.BASED_ASCC_MANIFEST_ID,
                Tables.ASBIE.DEFINITION,
                Tables.ASBIE.CARDINALITY_MAX,
                Tables.ASBIE.CARDINALITY_MIN,
                Tables.ASBIE.IS_NILLABLE.as("nillable"),
                Tables.ASBIE.IS_USED.as("used"),
                Tables.ASBIE.REMARK,
                Tables.ASBIE.CREATED_BY,
                Tables.ASBIE.CREATION_TIMESTAMP,
                Tables.ASBIE.LAST_UPDATED_BY,
                Tables.ASBIE.LAST_UPDATE_TIMESTAMP,
                Tables.ASBIE.SEQ_KEY,
                Tables.ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID).from(Tables.ASBIE).fetchInto(ASBIE.class);
    }

    @Override
    public ASBIE findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(Tables.ASBIE.ASBIE_ID,
                Tables.ASBIE.GUID,
                Tables.ASBIE.FROM_ABIE_ID,
                Tables.ASBIE.TO_ASBIEP_ID,
                Tables.ASBIE.BASED_ASCC_MANIFEST_ID,
                Tables.ASBIE.DEFINITION,
                Tables.ASBIE.CARDINALITY_MAX,
                Tables.ASBIE.CARDINALITY_MIN,
                Tables.ASBIE.IS_NILLABLE.as("nillable"),
                Tables.ASBIE.IS_USED.as("used"),
                Tables.ASBIE.REMARK,
                Tables.ASBIE.CREATED_BY,
                Tables.ASBIE.CREATION_TIMESTAMP,
                Tables.ASBIE.LAST_UPDATED_BY,
                Tables.ASBIE.LAST_UPDATE_TIMESTAMP,
                Tables.ASBIE.SEQ_KEY,
                Tables.ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID).from(Tables.ASBIE)
                .where(Tables.ASBIE.ASBIE_ID.eq(ULong.valueOf(id))).fetchOneInto(ASBIE.class);
    }

    public List<ASBIE> findByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        if (ownerTopLevelAbieId.longValue() <= 0L) {
            return Collections.emptyList();
        }
        return findByOwnerTopLevelAbieIds(Arrays.asList(ownerTopLevelAbieId));
    }

    public List<ASBIE> findByOwnerTopLevelAbieIds(Collection<BigInteger> ownerTopLevelAbieIds) {
        if (ownerTopLevelAbieIds == null || ownerTopLevelAbieIds.isEmpty()) {
            return Collections.emptyList();
        }
        return dslContext.select(Tables.ASBIE.ASBIE_ID,
                Tables.ASBIE.GUID,
                Tables.ASBIE.FROM_ABIE_ID,
                Tables.ASBIE.TO_ASBIEP_ID,
                Tables.ASBIE.BASED_ASCC_MANIFEST_ID,
                Tables.ASBIE.DEFINITION,
                Tables.ASBIE.CARDINALITY_MAX,
                Tables.ASBIE.CARDINALITY_MIN,
                Tables.ASBIE.IS_NILLABLE.as("nillable"),
                Tables.ASBIE.IS_USED.as("used"),
                Tables.ASBIE.REMARK,
                Tables.ASBIE.CREATED_BY,
                Tables.ASBIE.CREATION_TIMESTAMP,
                Tables.ASBIE.LAST_UPDATED_BY,
                Tables.ASBIE.LAST_UPDATE_TIMESTAMP,
                Tables.ASBIE.SEQ_KEY,
                Tables.ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID).from(Tables.ASBIE)
                .where(
                        (ownerTopLevelAbieIds.size() == 1) ?
                                Tables.ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(
                                        ULong.valueOf(ownerTopLevelAbieIds.iterator().next())) :
                                Tables.ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.in(
                                        ownerTopLevelAbieIds.stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                )
                .fetchInto(ASBIE.class);

    }

}
