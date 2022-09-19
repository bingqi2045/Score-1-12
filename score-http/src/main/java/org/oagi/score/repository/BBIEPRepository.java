package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.BBIEP;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class BBIEPRepository implements ScoreRepository<BBIEP, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<BBIEP> findAll() {
        return dslContext.select(Tables.BBIEP.BBIEP_ID,
                Tables.BBIEP.GUID,
                Tables.BBIEP.BASED_BCCP_MANIFEST_ID,
                Tables.BBIEP.DEFINITION,
                Tables.BBIEP.REMARK,
                Tables.BBIEP.BIZ_TERM,
                Tables.BBIEP.CREATED_BY,
                Tables.BBIEP.CREATION_TIMESTAMP,
                Tables.BBIEP.LAST_UPDATED_BY,
                Tables.BBIEP.LAST_UPDATE_TIMESTAMP,
                Tables.BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID)
                .from(Tables.BBIEP)
                .fetchInto(BBIEP.class);
    }

    @Override
    public BBIEP findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.BBIEP.BBIEP_ID,
                Tables.BBIEP.GUID,
                Tables.BBIEP.BASED_BCCP_MANIFEST_ID,
                Tables.BBIEP.DEFINITION,
                Tables.BBIEP.REMARK,
                Tables.BBIEP.BIZ_TERM,
                Tables.BBIEP.CREATED_BY,
                Tables.BBIEP.CREATION_TIMESTAMP,
                Tables.BBIEP.LAST_UPDATED_BY,
                Tables.BBIEP.LAST_UPDATE_TIMESTAMP,
                Tables.BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID)
                .from(Tables.BBIEP)
                .where(Tables.BBIEP.BBIEP_ID.eq(id))
                .fetchOneInto(BBIEP.class);
    }

    public List<BBIEP> findByOwnerTopLevelAsbiepIds(Collection<String> ownerTopLevelAsbiepIds) {
        return dslContext.select(Tables.BBIEP.BBIEP_ID,
                Tables.BBIEP.GUID,
                Tables.BBIEP.BASED_BCCP_MANIFEST_ID,
                Tables.BBIEP.DEFINITION,
                Tables.BBIEP.REMARK,
                Tables.BBIEP.BIZ_TERM,
                Tables.BBIEP.CREATED_BY,
                Tables.BBIEP.CREATION_TIMESTAMP,
                Tables.BBIEP.LAST_UPDATED_BY,
                Tables.BBIEP.LAST_UPDATE_TIMESTAMP,
                Tables.BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID)
                .from(Tables.BBIEP)
                .where(
                        (ownerTopLevelAsbiepIds.size() == 1) ?
                                Tables.BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(
                                        ownerTopLevelAsbiepIds.iterator().next()) :
                                Tables.BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.in(
                                        ownerTopLevelAsbiepIds)
                )
                .fetchInto(BBIEP.class);
    }

}
