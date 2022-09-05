package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.oagi.score.data.TopLevelAsbiep;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.TOP_LEVEL_ASBIEP;

@Repository
public class TopLevelAsbiepRepository implements ScoreRepository<TopLevelAsbiep, String> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<TopLevelAsbiep> findAll() {
        return dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .fetchInto(TopLevelAsbiep.class);
    }

    @Override
    public TopLevelAsbiep findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(id))
                .fetchOneInto(TopLevelAsbiep.class);
    }

    public List<TopLevelAsbiep> findByIdIn(List<String> topLevelAsbiepIds) {
        return dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.in(topLevelAsbiepIds))
                .fetchInto(TopLevelAsbiep.class);
    }
}
