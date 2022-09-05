package org.oagi.score.repository;

import org.jooq.DSLContext;
import org.jooq.Record12;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.UInteger;
import org.oagi.score.data.DTSC;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DTSCRepository implements ScoreRepository<DTSC, String> {

    @Autowired
    private DSLContext dslContext;

    private SelectOnConditionStep<Record12<String, String, String, String, String, String, String, String, Integer,
            Integer, String, UInteger>> getSelectJoinStep() {
        return dslContext.select(
                Tables.DT_SC_MANIFEST.DT_SC_MANIFEST_ID,
                Tables.DT_SC.DT_SC_ID,
                Tables.DT_SC.GUID,
                Tables.DT_SC.PROPERTY_TERM,
                Tables.DT_SC.REPRESENTATION_TERM,
                Tables.DT_SC.DEFINITION,
                Tables.DT_SC.DEFINITION_SOURCE,
                Tables.DT_SC.OWNER_DT_ID,
                Tables.DT_SC.CARDINALITY_MIN,
                Tables.DT_SC.CARDINALITY_MAX,
                Tables.DT_SC.BASED_DT_SC_ID,
                Tables.LOG.REVISION_NUM)
                .from(Tables.DT_SC)
                .join(Tables.DT_SC_MANIFEST).on(Tables.DT_SC.DT_SC_ID.eq(Tables.DT_SC_MANIFEST.DT_SC_ID))
                .join(Tables.DT_MANIFEST).on(Tables.DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID.eq(Tables.DT_MANIFEST.DT_MANIFEST_ID))
                .join(Tables.LOG).on(Tables.DT_MANIFEST.LOG_ID.eq(Tables.LOG.LOG_ID));
    }

    @Override
    public List<DTSC> findAll() {
        return getSelectJoinStep().fetchInto(DTSC.class);
    }

    @Override
    public List<DTSC> findAllByReleaseId(String releaseId) {
        return getSelectJoinStep()
                .where(Tables.DT_SC_MANIFEST.RELEASE_ID.eq(releaseId))
                .fetchInto(DTSC.class);
    }

    @Override
    public DTSC findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return getSelectJoinStep()
                .where(Tables.DT_SC.DT_SC_ID.eq(id))
                .fetchOptionalInto(DTSC.class).orElse(null);
    }
}
