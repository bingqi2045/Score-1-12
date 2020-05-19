package org.oagi.srt.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.enums.ReleaseState;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class ReleaseRepository implements SrtRepository<Release> {

    @Autowired
    private DSLContext dslContext;

    @Override
    public List<Release> findAll() {
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM, RELEASE.LAST_UPDATED_BY,
                RELEASE.NAMESPACE_ID, RELEASE.CREATED_BY, RELEASE.STATE,
                RELEASE.LAST_UPDATE_TIMESTAMP, RELEASE.CREATION_TIMESTAMP, RELEASE.RELEASE_NOTE)
                .from(RELEASE).fetchInto(Release.class);
    }

    @Override
    public Release findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM, RELEASE.LAST_UPDATED_BY,
                RELEASE.NAMESPACE_ID, RELEASE.CREATED_BY, RELEASE.STATE,
                RELEASE.LAST_UPDATE_TIMESTAMP, RELEASE.CREATION_TIMESTAMP, RELEASE.RELEASE_NOTE)
                .from(RELEASE).where(RELEASE.RELEASE_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(Release.class);
    }

    public List<Release> findByReleaseNum(String releaseNum) {
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM, RELEASE.LAST_UPDATED_BY,
                RELEASE.NAMESPACE_ID, RELEASE.CREATED_BY, RELEASE.STATE,
                RELEASE.LAST_UPDATE_TIMESTAMP, RELEASE.CREATION_TIMESTAMP, RELEASE.RELEASE_NOTE)
                .from(RELEASE).where(RELEASE.RELEASE_NUM.eq(releaseNum))
                .fetchInto(Release.class);
    }

    public Release getWorkingRelease() {
        List<Release> releases = findByReleaseNum("Working");
        if (releases.size() != 1) {
            throw new IllegalStateException();
        }
        return releases.get(0);
    }

    public ReleaseRecord create(BigInteger userId, String releaseNum, String releaseNote, long namespaceId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ReleaseRecord releaseRecord = dslContext.insertInto(RELEASE)
                .set(RELEASE.RELEASE_NUM, releaseNum)
                .set(RELEASE.RELEASE_NOTE, releaseNote)
                .set(RELEASE.CREATED_BY, ULong.valueOf(userId))
                .set(RELEASE.CREATION_TIMESTAMP, timestamp.toLocalDateTime())
                .set(RELEASE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(RELEASE.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                .set(RELEASE.STATE, ReleaseState.Instantiating)
                .set(RELEASE.NAMESPACE_ID, ULong.valueOf(namespaceId)).returning().fetchOne();

        return releaseRecord;
    }

    public void copyWorkingManifestsTo(BigInteger targetReleaseId) {

        ReleaseRecord releaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(targetReleaseId)))
                .fetchOne();

        try {
            // copying manifests from 'Working' release
            List<AccManifestRecord> accManifestRecords = getAccManifestRecordsInWorking();
            Map<ULong, ULong> prevNextAccManifestIdMap = copyAccManifests(
                    releaseRecord, accManifestRecords);

            List<AsccpManifestRecord> asccpManifestRecords = getAsccpManifestRecordsInWorking();
            Map<ULong, ULong> prevNextAsccpManifestIdMap = copyAsccpManifests(
                    releaseRecord, asccpManifestRecords, prevNextAccManifestIdMap);

            List<BccpManifestRecord> bccpManifestRecords = getBccpManifestRecordsInWorking();
            Map<ULong, ULong> prevNextBccpManifestIdMap = copyBccpManifests(
                    releaseRecord, bccpManifestRecords);

            List<AsccManifestRecord> asccManifestRecords = getAsccManifestRecordsInWorking();
            copyAsccManifests(releaseRecord, asccManifestRecords,
                    prevNextAccManifestIdMap, prevNextAsccpManifestIdMap);

            List<BccManifestRecord> bccManifestRecords = getBccManifestRecordsInWorking();
            copyBccManifests(releaseRecord, bccManifestRecords,
                    prevNextAccManifestIdMap, prevNextBccpManifestIdMap);

            releaseRecord.setState(ReleaseState.WIP);
            releaseRecord.update(RELEASE.STATE);
        } catch (Exception e) {
            releaseRecord.setState(ReleaseState.Failure);
            releaseRecord.setReleaseNote(e.getMessage());
            releaseRecord.update(RELEASE.STATE);
        }
    }

    private List<AccManifestRecord> getAccManifestRecordsInWorking() {
        return dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST)
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.eq("Working"),
                        ACC.STATE.in(Arrays.asList(CcState.Candidate.name(), CcState.Published.name()))
                ))
                .fetchInto(AccManifestRecord.class);
    }

    private List<AsccManifestRecord> getAsccManifestRecordsInWorking() {
        return dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.eq("Working"),
                        ASCC.STATE.in(Arrays.asList(CcState.Candidate.name(), CcState.Published.name()))
                ))
                .fetchInto(AsccManifestRecord.class);
    }

    private List<BccManifestRecord> getBccManifestRecordsInWorking() {
        return dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.eq("Working"),
                        BCC.STATE.in(Arrays.asList(CcState.Candidate.name(), CcState.Published.name()))
                ))
                .fetchInto(BccManifestRecord.class);
    }

    private List<AsccpManifestRecord> getAsccpManifestRecordsInWorking() {
        return dslContext.select(ASCCP_MANIFEST.fields())
                .from(ASCCP_MANIFEST)
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.eq("Working"),
                        ASCCP.STATE.in(Arrays.asList(CcState.Candidate.name(), CcState.Published.name()))
                ))
                .fetchInto(AsccpManifestRecord.class);
    }

    private List<BccpManifestRecord> getBccpManifestRecordsInWorking() {
        return dslContext.select(BCCP_MANIFEST.fields())
                .from(BCCP_MANIFEST)
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.eq("Working"),
                        BCCP.STATE.in(Arrays.asList(CcState.Candidate.name(), CcState.Published.name()))
                ))
                .fetchInto(BccpManifestRecord.class);
    }

    private Map<ULong, ULong> copyAccManifests(ReleaseRecord releaseRecord,
                                               List<AccManifestRecord> accManifestRecords) {
        Map<ULong, ULong> prevNextAccManifestIdMap = new HashMap();
        accManifestRecords.forEach(e -> {
            ULong prevAccManifestId = e.getAccManifestId();

            e.setAccManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setAccManifestId(
                    dslContext.insertInto(ACC_MANIFEST)
                            .set(e).returning(ACC_MANIFEST.ACC_MANIFEST_ID)
                            .fetchOne().getAccManifestId()
            );

            prevNextAccManifestIdMap.put(prevAccManifestId, e.getAccManifestId());
        });

        accManifestRecords.stream().filter(e -> e.getBasedAccManifestId() != null)
                .forEach(e -> {
                    if (prevNextAccManifestIdMap.containsKey(e.getBasedAccManifestId())) {
                        e.setBasedAccManifestId(
                                prevNextAccManifestIdMap.get(e.getBasedAccManifestId())
                        );
                        e.update(ACC_MANIFEST.BASED_ACC_MANIFEST_ID);
                    }
                });

        return prevNextAccManifestIdMap;
    }

    private Map<ULong, ULong> copyAsccpManifests(ReleaseRecord releaseRecord,
                                                 List<AsccpManifestRecord> asccpManifestRecords,
                                                 Map<ULong, ULong> prevNextAccManifestIdMap) {
        Map<ULong, ULong> prevNextAsccpManifestIdMap = new HashMap();
        asccpManifestRecords.forEach(e -> {
            ULong prevAsccpManifestId = e.getAsccpManifestId();

            e.setAsccpManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            if (prevNextAccManifestIdMap.containsKey(e.getRoleOfAccManifestId())) {
                e.setRoleOfAccManifestId(
                        prevNextAccManifestIdMap.get(e.getRoleOfAccManifestId())
                );
            }
            e.setAsccpManifestId(
                    dslContext.insertInto(ASCCP_MANIFEST)
                            .set(e).returning(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                            .fetchOne().getAsccpManifestId()
            );

            prevNextAsccpManifestIdMap.put(prevAsccpManifestId, e.getAsccpManifestId());
        });

        return prevNextAsccpManifestIdMap;
    }

    private Map<ULong, ULong> copyBccpManifests(ReleaseRecord releaseRecord,
                                                List<BccpManifestRecord> bccpManifestRecords) {
        Map<ULong, ULong> prevNextBccpManifestIdMap = new HashMap();
        bccpManifestRecords.forEach(e -> {
            ULong prevBccpManifestId = e.getBccpManifestId();

            e.setBccpManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setBccpManifestId(
                    dslContext.insertInto(BCCP_MANIFEST)
                            .set(e).returning(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                            .fetchOne().getBccpManifestId()
            );

            prevNextBccpManifestIdMap.put(prevBccpManifestId, e.getBccpManifestId());
        });

        return prevNextBccpManifestIdMap;
    }

    private Map<ULong, ULong> copyAsccManifests(ReleaseRecord releaseRecord,
                                                List<AsccManifestRecord> asccManifestRecords,
                                                Map<ULong, ULong> prevNextAccManifestIdMap,
                                                Map<ULong, ULong> prevNextAsccpManifestIdMap) {

        Map<ULong, ULong> prevNextAsccManifestIdMap = new HashMap();
        asccManifestRecords.forEach(e -> {
            ULong prevAsccManifestId = e.getAsccManifestId();

            e.setAsccManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            if (prevNextAccManifestIdMap.containsKey(e.getFromAccManifestId())) {
                e.setFromAccManifestId(
                        prevNextAccManifestIdMap.get(e.getFromAccManifestId())
                );
            }
            if (prevNextAsccpManifestIdMap.containsKey(e.getToAsccpManifestId())) {
                e.setToAsccpManifestId(
                        prevNextAsccpManifestIdMap.get(e.getToAsccpManifestId())
                );
            }
            e.setAsccManifestId(
                    dslContext.insertInto(ASCC_MANIFEST)
                            .set(e).returning(ASCC_MANIFEST.ASCC_MANIFEST_ID)
                            .fetchOne().getAsccManifestId()
            );

            prevNextAsccManifestIdMap.put(prevAsccManifestId, e.getAsccManifestId());
        });

        return prevNextAsccManifestIdMap;
    }

    private Map<ULong, ULong> copyBccManifests(ReleaseRecord releaseRecord,
                                               List<BccManifestRecord> bccManifestRecords,
                                               Map<ULong, ULong> prevNextAccManifestIdMap,
                                               Map<ULong, ULong> prevNextBccpManifestIdMap) {

        Map<ULong, ULong> prevNextBccManifestIdMap = new HashMap();
        bccManifestRecords.forEach(e -> {
            ULong prevBccManifestId = e.getBccManifestId();

            e.setBccManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            if (prevNextAccManifestIdMap.containsKey(e.getFromAccManifestId())) {
                e.setFromAccManifestId(
                        prevNextAccManifestIdMap.get(e.getFromAccManifestId())
                );
            }
            if (prevNextBccpManifestIdMap.containsKey(e.getToBccpManifestId())) {
                e.setToBccpManifestId(
                        prevNextBccpManifestIdMap.get(e.getToBccpManifestId())
                );
            }
            e.setBccManifestId(
                    dslContext.insertInto(BCC_MANIFEST)
                            .set(e).returning(BCC_MANIFEST.BCC_MANIFEST_ID)
                            .fetchOne().getBccManifestId()
            );

            prevNextBccManifestIdMap.put(prevBccManifestId, e.getBccManifestId());
        });

        return prevNextBccManifestIdMap;
    }
}
