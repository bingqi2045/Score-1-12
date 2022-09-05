package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.ASBIEP;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ASBIEPRepository implements ScoreRepository<ASBIEP, String> {

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
    public ASBIEP findById(String id) {
        if (!StringUtils.hasLength(id)) {
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
                .where(Tables.ASBIEP.ASBIEP_ID.eq(id))
                .fetchOneInto(ASBIEP.class);
    }

    public List<ASBIEP> findByOwnerTopLevelAsbiepIds(Collection<String> ownerTopLevelAsbiepIds) {
        if (ownerTopLevelAsbiepIds == null || ownerTopLevelAsbiepIds.isEmpty()) {
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
                        (ownerTopLevelAsbiepIds.size() == 1) ?
                                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(
                                        ownerTopLevelAsbiepIds.iterator().next()) :
                                Tables.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.in(
                                        ownerTopLevelAsbiepIds)

                )
                .fetchInto(ASBIEP.class);
    }

}
