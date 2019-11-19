package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.Release;
import org.oagi.srt.data.ReleaseState;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.ReleaseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ReleaseRepository implements SrtRepository<Release> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<Release> findAll() {
        return dslContext.select(Tables.RELEASE.RELEASE_ID, Tables.RELEASE.RELEASE_NUM, Tables.RELEASE.LAST_UPDATED_BY,
                Tables.RELEASE.NAMESPACE_ID, Tables.RELEASE.CREATED_BY, Tables.RELEASE.STATE,
                Tables.RELEASE.LAST_UPDATE_TIMESTAMP, Tables.RELEASE.CREATION_TIMESTAMP, Tables.RELEASE.RELEASE_NOTE)
                .from(Tables.RELEASE).fetchInto(Release.class);
    }

    @Override
    public Release findById(long id) {
        return dslContext.select(Tables.RELEASE.RELEASE_ID, Tables.RELEASE.RELEASE_NUM, Tables.RELEASE.LAST_UPDATED_BY,
                Tables.RELEASE.NAMESPACE_ID, Tables.RELEASE.CREATED_BY, Tables.RELEASE.STATE,
                Tables.RELEASE.LAST_UPDATE_TIMESTAMP, Tables.RELEASE.CREATION_TIMESTAMP, Tables.RELEASE.RELEASE_NOTE)
                .from(Tables.RELEASE).where(Tables.RELEASE.RELEASE_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(Release.class);
    }

    public List<Release> findByReleaseNum(String releaseNum) {
        return dslContext.select(Tables.RELEASE.RELEASE_ID, Tables.RELEASE.RELEASE_NUM, Tables.RELEASE.LAST_UPDATED_BY,
                Tables.RELEASE.NAMESPACE_ID, Tables.RELEASE.CREATED_BY, Tables.RELEASE.STATE,
                Tables.RELEASE.LAST_UPDATE_TIMESTAMP, Tables.RELEASE.CREATION_TIMESTAMP, Tables.RELEASE.RELEASE_NOTE)
                .from(Tables.RELEASE).where(Tables.RELEASE.RELEASE_NUM.eq(releaseNum))
                .fetchInto(Release.class);
    }

    public ReleaseRecord create(long userId, String releaseNum, String releaseNote, long namespaceId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return dslContext.insertInto(Tables.RELEASE)
                .set(Tables.RELEASE.RELEASE_NUM, releaseNum)
                .set(Tables.RELEASE.RELEASE_NOTE, releaseNote)
                .set(Tables.RELEASE.CREATED_BY, ULong.valueOf(userId))
                .set(Tables.RELEASE.CREATION_TIMESTAMP, timestamp)
                .set(Tables.RELEASE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(Tables.RELEASE.LAST_UPDATE_TIMESTAMP, timestamp)
                .set(Tables.RELEASE.STATE, ReleaseState.Draft.getValue())
                .set(Tables.RELEASE.NAMESPACE_ID, ULong.valueOf(namespaceId)).returning().fetchOne();
    }
}
