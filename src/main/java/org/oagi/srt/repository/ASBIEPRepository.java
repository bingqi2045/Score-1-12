package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ASBIEP;
import org.oagi.srt.entity.jooq.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ASBIEPRepository implements SrtRepository<ASBIEP> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<ASBIEP> findAll() {
        return dslContext.select(Tables.ASBIEP.BASED_ASCCP_MANIFEST_ID,
                Tables.ASBIEP.ASBIEP_ID,
                Tables.ASBIEP.ROLE_OF_ABIE_ID,
                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID,
                Tables.ASBIEP.LAST_UPDATED_BY,
                Tables.ASBIEP.BIZ_TERM,
                Tables.ASBIEP.CREATED_BY,
                Tables.ASBIEP.REMARK,
                Tables.ASBIEP.GUID,
                Tables.ASBIEP.LAST_UPDATE_TIMESTAMP,
                Tables.ASBIEP.CREATION_TIMESTAMP,
                Tables.ASBIEP.DEFINITION)
                .from(Tables.ASBIEP)
                .fetchInto(ASBIEP.class);
    }

    @Override
    public ASBIEP findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(Tables.ASBIEP.BASED_ASCCP_MANIFEST_ID,
                Tables.ASBIEP.ASBIEP_ID,
                Tables.ASBIEP.ROLE_OF_ABIE_ID,
                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID,
                Tables.ASBIEP.LAST_UPDATED_BY,
                Tables.ASBIEP.BIZ_TERM,
                Tables.ASBIEP.CREATED_BY,
                Tables.ASBIEP.REMARK,
                Tables.ASBIEP.GUID,
                Tables.ASBIEP.LAST_UPDATE_TIMESTAMP,
                Tables.ASBIEP.CREATION_TIMESTAMP,
                Tables.ASBIEP.DEFINITION)
                .from(Tables.ASBIEP)
                .where(Tables.ASBIEP.ASBIEP_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ASBIEP.class);
    }

    public List<ASBIEP> findByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        if (ownerTopLevelAbieId.longValue() <= 0L) {
            return Collections.emptyList();
        }
        return findByOwnerTopLevelAbieIds(Arrays.asList(ownerTopLevelAbieId));
    }

    public List<ASBIEP> findByOwnerTopLevelAbieIds(Collection<BigInteger> ownerTopLevelAbieIds) {
        if (ownerTopLevelAbieIds == null || ownerTopLevelAbieIds.isEmpty()) {
            return Collections.emptyList();
        }
        return dslContext.select(Tables.ASBIEP.BASED_ASCCP_MANIFEST_ID,
                Tables.ASBIEP.ASBIEP_ID,
                Tables.ASBIEP.ROLE_OF_ABIE_ID,
                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID,
                Tables.ASBIEP.LAST_UPDATED_BY,
                Tables.ASBIEP.BIZ_TERM,
                Tables.ASBIEP.CREATED_BY,
                Tables.ASBIEP.REMARK,
                Tables.ASBIEP.GUID,
                Tables.ASBIEP.LAST_UPDATE_TIMESTAMP,
                Tables.ASBIEP.CREATION_TIMESTAMP,
                Tables.ASBIEP.DEFINITION)
                .from(Tables.ASBIEP)
                .where(
                        (ownerTopLevelAbieIds.size() == 1) ?
                                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(
                                        ULong.valueOf(ownerTopLevelAbieIds.iterator().next())) :
                                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.in(
                                        ownerTopLevelAbieIds.stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))

                )
                .fetchInto(ASBIEP.class);
    }

}
