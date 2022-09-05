package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.oagi.score.data.BCCP;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BCCPRepository implements ScoreRepository<BCCP, String> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record> getSelectOnConditionStep() {
        return dslContext.select(
                Tables.BCCP_MANIFEST.BCCP_MANIFEST_ID,
                Tables.BCCP.BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.PROPERTY_TERM,
                Tables.BCCP.REPRESENTATION_TERM,
                Tables.BCCP.DEFAULT_VALUE,
                Tables.BCCP.BDT_ID,
                Tables.BCCP.DEN,
                Tables.BCCP.DEFINITION,
                Tables.BCCP.DEFINITION_SOURCE,
                Tables.BCCP.NAMESPACE_ID,
                Tables.BCCP.CREATED_BY,
                Tables.BCCP.OWNER_USER_ID,
                Tables.BCCP.LAST_UPDATED_BY,
                Tables.BCCP.CREATION_TIMESTAMP,
                Tables.BCCP.LAST_UPDATE_TIMESTAMP,
                Tables.BCCP.STATE,
                Tables.BCCP_MANIFEST.RELEASE_ID,
                Tables.RELEASE.RELEASE_NUM,
                Tables.BCCP_MANIFEST.LOG_ID,
                Tables.LOG.REVISION_NUM,
                Tables.LOG.REVISION_TRACKING_NUM,
                Tables.BCCP.IS_DEPRECATED.as("deprecated"),
                Tables.BCCP.IS_NILLABLE.as("nillable"))
                .from(Tables.BCCP)
                .join(Tables.BCCP_MANIFEST)
                .on(Tables.BCCP.BCCP_ID.eq(Tables.BCCP_MANIFEST.BCCP_ID))
                .join(Tables.RELEASE)
                .on(Tables.BCCP_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(Tables.LOG)
                .on(Tables.BCCP_MANIFEST.LOG_ID.eq(Tables.LOG.LOG_ID));
    }

    @Override
    public List<BCCP> findAll() {
        return getSelectOnConditionStep().fetchInto(BCCP.class);
    }

    @Override
    public List<BCCP> findAllByReleaseId(String releaseId) {
        return getSelectOnConditionStep()
                .where(Tables.BCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchInto(BCCP.class);
    }

    @Override
    public BCCP findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return getSelectOnConditionStep()
                .where(Tables.BCCP.BCCP_ID.eq(id))
                .fetchOptionalInto(BCCP.class).orElse(null);
    }
}
