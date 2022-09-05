package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.CdtScAwdPriXpsTypeMap;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CdtScAwdPriXpsTypeMapRepository implements ScoreRepository<CdtScAwdPriXpsTypeMap, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<CdtScAwdPriXpsTypeMap> findAll() {
        return dslContext.select(Tables.CDT_SC_AWD_PRI_XPS_TYPE_MAP.fields())
                .from(Tables.CDT_SC_AWD_PRI_XPS_TYPE_MAP).fetchInto(CdtScAwdPriXpsTypeMap.class);
    }

    @Override
    public CdtScAwdPriXpsTypeMap findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.CDT_SC_AWD_PRI_XPS_TYPE_MAP.fields())
                .from(Tables.CDT_SC_AWD_PRI_XPS_TYPE_MAP)
                .where(Tables.CDT_SC_AWD_PRI_XPS_TYPE_MAP.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID.eq(id))
                .fetchOneInto(CdtScAwdPriXpsTypeMap.class);
    }

}
