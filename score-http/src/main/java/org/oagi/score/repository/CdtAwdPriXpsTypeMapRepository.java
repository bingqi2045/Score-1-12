package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.CdtAwdPriXpsTypeMap;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CdtAwdPriXpsTypeMapRepository implements ScoreRepository<CdtAwdPriXpsTypeMap, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<CdtAwdPriXpsTypeMap> findAll() {
        return dslContext.select(Tables.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                Tables.CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID, Tables.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_ID)
                .from(Tables.CDT_AWD_PRI_XPS_TYPE_MAP).fetchInto(CdtAwdPriXpsTypeMap.class);
    }

    @Override
    public CdtAwdPriXpsTypeMap findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.select(Tables.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID,
                Tables.CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID, Tables.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_ID)
                .from(Tables.CDT_AWD_PRI_XPS_TYPE_MAP)
                .where(Tables.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(id))
                .fetchOneInto(CdtAwdPriXpsTypeMap.class);
    }

}
