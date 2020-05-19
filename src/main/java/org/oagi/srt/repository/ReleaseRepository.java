package org.oagi.srt.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.enums.ReleaseState;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.release_management.data.AssignComponents;
import org.oagi.srt.gateway.http.api.release_management.data.TransitStateRequest;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.or;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.enums.ReleaseState.*;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.Candidate;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.ReleaseDraft;

@Repository
public class ReleaseRepository implements SrtRepository<Release> {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

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

    public void copyWorkingManifestsTo(BigInteger releaseId) {
        copyWorkingManifestsTo(
                releaseId, Arrays.asList(CcState.Published),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public void copyWorkingManifestsTo(
            BigInteger releaseId,
            List<CcState> states,
            List<BigInteger> accManifestIds,
            List<BigInteger> asccpManifestIds,
            List<BigInteger> bccpManifestIds) {

        ReleaseRecord releaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchOne();

        try {
            // copying manifests from 'Working' release
            List<AccManifestRecord> accManifestRecords = getAccManifestRecordsInWorking(
                    states, accManifestIds);
            Map<ULong, ULong> prevNextAccManifestIdMap = copyAccManifests(
                    releaseRecord, accManifestRecords);

            List<AsccpManifestRecord> asccpManifestRecords = getAsccpManifestRecordsInWorking(
                    states, asccpManifestIds);
            Map<ULong, ULong> prevNextAsccpManifestIdMap = copyAsccpManifests(
                    releaseRecord, asccpManifestRecords, prevNextAccManifestIdMap);

            List<BccpManifestRecord> bccpManifestRecords = getBccpManifestRecordsInWorking(
                    states, bccpManifestIds);
            Map<ULong, ULong> prevNextBccpManifestIdMap = copyBccpManifests(
                    releaseRecord, bccpManifestRecords);

            List<AsccManifestRecord> asccManifestRecords = getAsccManifestRecordsInWorking(
                    states, accManifestIds);
            copyAsccManifests(releaseRecord, asccManifestRecords,
                    prevNextAccManifestIdMap, prevNextAsccpManifestIdMap);

            List<BccManifestRecord> bccManifestRecords = getBccManifestRecordsInWorking(
                    states, accManifestIds);
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

    private List<AccManifestRecord> getAccManifestRecordsInWorking(List<CcState> states,
                                                                   List<BigInteger> accManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(ACC.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
        if (accManifestIds != null && accManifestIds.size() > 0) {
            conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds));
        }

        return dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST)
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .where(conditions)
                .fetchInto(AccManifestRecord.class);
    }

    private List<AsccManifestRecord> getAsccManifestRecordsInWorking(List<CcState> states,
                                                                     List<BigInteger> accManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(ACC.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
        if (accManifestIds != null && accManifestIds.size() > 0) {
            conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds));
        }

        return dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(conditions)
                .fetchInto(AsccManifestRecord.class);
    }

    private List<BccManifestRecord> getBccManifestRecordsInWorking(List<CcState> states,
                                                                   List<BigInteger> accManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(ACC.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
        if (accManifestIds != null && accManifestIds.size() > 0) {
            conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds));
        }

        return dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(conditions)
                .fetchInto(BccManifestRecord.class);
    }

    private List<AsccpManifestRecord> getAsccpManifestRecordsInWorking(List<CcState> states,
                                                                       List<BigInteger> asccpManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(ASCCP.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
        if (asccpManifestIds != null && asccpManifestIds.size() > 0) {
            conditions.add(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(asccpManifestIds));
        }

        return dslContext.select(ASCCP_MANIFEST.fields())
                .from(ASCCP_MANIFEST)
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(conditions)
                .fetchInto(AsccpManifestRecord.class);
    }

    private List<BccpManifestRecord> getBccpManifestRecordsInWorking(List<CcState> states,
                                                                     List<BigInteger> bccpManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(BCCP.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
        if (bccpManifestIds != null && bccpManifestIds.size() > 0) {
            conditions.add(BCCP_MANIFEST.BCCP_MANIFEST_ID.in(bccpManifestIds));
        }

        return dslContext.select(BCCP_MANIFEST.fields())
                .from(BCCP_MANIFEST)
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .where(conditions)
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

    public void unassignManifests(
            BigInteger releaseId,
            List<BigInteger> accManifestIds,
            List<BigInteger> asccpManifestIds,
            List<BigInteger> bccpManifestIds) {

        // ensure all manifests in given request are in 'Candidate' state.
        // check ACCs
        dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID, ACC.STATE)
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .where(and(
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds)
                ))
                .fetchStream().forEach(e -> {
            CcState ccState = CcState.valueOf(e.value2());
            if (ccState != Candidate) {
                throw new IllegalArgumentException(e.value1() + " is an invalid manifest ID.");
            }
        });

        // check ASCCPs
        dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID, ASCCP.STATE)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(and(
                        ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(asccpManifestIds)
                ))
                .fetchStream().forEach(e -> {
            CcState ccState = CcState.valueOf(e.value2());
            if (ccState != Candidate) {
                throw new IllegalArgumentException(e.value1() + " is an invalid manifest ID.");
            }
        });

        // check BCCPs
        dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID, BCCP.STATE)
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(and(
                        BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        BCCP_MANIFEST.BCCP_MANIFEST_ID.in(bccpManifestIds)
                ))
                .fetchStream().forEach(e -> {
            CcState ccState = CcState.valueOf(e.value2());
            if (ccState != Candidate) {
                throw new IllegalArgumentException(e.value1() + " is an invalid manifest ID.");
            }
        });

        deleteManifests(releaseId, accManifestIds, asccpManifestIds, bccpManifestIds);
    }

    private void deleteManifests(
            BigInteger releaseId,
            List<BigInteger> accManifestIds,
            List<BigInteger> asccpManifestIds,
            List<BigInteger> bccpManifestIds) {

        if (accManifestIds != null && !accManifestIds.isEmpty()) {
            dslContext.deleteFrom(ASCC_MANIFEST)
                    .where(and(
                            RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.in(accManifestIds)
                    ))
                    .execute();

            dslContext.deleteFrom(BCC_MANIFEST)
                    .where(and(
                            RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                            BCC_MANIFEST.FROM_ACC_MANIFEST_ID.in(accManifestIds)
                    ))
                    .execute();
        }

        if (bccpManifestIds != null && !bccpManifestIds.isEmpty()) {
            dslContext.deleteFrom(BCCP_MANIFEST)
                    .where(and(
                            RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                            BCCP_MANIFEST.BCCP_MANIFEST_ID.in(bccpManifestIds)
                    ))
                    .execute();
        }

        if (asccpManifestIds != null && !asccpManifestIds.isEmpty()) {
            dslContext.deleteFrom(ASCCP_MANIFEST)
                    .where(and(
                            RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                            ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(asccpManifestIds)
                    ))
                    .execute();
        }

        if (accManifestIds != null && !accManifestIds.isEmpty()) {
            dslContext.deleteFrom(ACC_MANIFEST)
                    .where(and(
                            RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                            ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds)
                    ))
                    .execute();
        }
    }

    public AssignComponents getAssignComponents(BigInteger releaseId) {
        AssignComponents assignComponents = new AssignComponents();

        // ACCs
        Map<ULong, List<Record3<ULong, String, String>>> map = dslContext.select(
                ACC_MANIFEST.ACC_MANIFEST_ID, ACC.OBJECT_CLASS_TERM, RELEASE.RELEASE_NUM)
                .from(ACC_MANIFEST)
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .where(and(
                        or(
                                RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                RELEASE.RELEASE_NUM.eq("Working")
                        ),
                        ACC.STATE.eq(Candidate.name())
                ))
                .fetchStream()
                .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
           if (e.size() == 2) { // manifest are located at both sides.
               assignComponents.addUnassignableAccManifest(
                       e.get(0).value1().toBigInteger(), e.get(0).value2());
           }
           // manifest is only located at 'Working' release side.
           else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
               assignComponents.addAssignableAccManifest(
                       e.get(0).value1().toBigInteger(), e.get(0).value2());
           }
        });

        // ASCCPs
        map = dslContext.select(
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID, ASCCP.PROPERTY_TERM, RELEASE.RELEASE_NUM)
                .from(ASCCP_MANIFEST)
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(and(
                        or(
                                RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                RELEASE.RELEASE_NUM.eq("Working")
                        ),
                        ASCCP.STATE.eq(Candidate.name())
                ))
                .fetchStream()
                .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
            if (e.size() == 2) { // manifest are located at both sides.
                assignComponents.addUnassignableAsccpManifest(
                        e.get(0).value1().toBigInteger(), e.get(0).value2());
            }
            // manifest is only located at 'Working' release side.
            else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
                assignComponents.addAssignableAsccpManifest(
                        e.get(0).value1().toBigInteger(), e.get(0).value2());
            }
        });

        // BCCPs
        map = dslContext.select(
                BCCP_MANIFEST.BCCP_MANIFEST_ID, BCCP.PROPERTY_TERM, RELEASE.RELEASE_NUM)
                .from(BCCP_MANIFEST)
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .where(and(
                        or(
                                RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                RELEASE.RELEASE_NUM.eq("Working")
                        ),
                        BCCP.STATE.eq(Candidate.name())
                ))
                .fetchStream()
                .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
            if (e.size() == 2) { // manifest are located at both sides.
                assignComponents.addUnassignableBccpManifest(
                        e.get(0).value1().toBigInteger(), e.get(0).value2());
            }
            // manifest is only located at 'Working' release side.
            else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
                assignComponents.addAssignableBccpManifest(
                        e.get(0).value1().toBigInteger(), e.get(0).value2());
            }
        });

        return assignComponents;
    }

    public void transitState(User user,
                             TransitStateRequest request) {

        ReleaseRecord releaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())))
                .fetchOne();

        ReleaseState requestState = ReleaseState.valueOf(request.getState());
        CcState fromCcState = null;
        CcState toCcState = null;

        switch (releaseRecord.getState()) {
            case WIP:
                if (requestState != Draft) {
                    throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot transit to '" + requestState + "' state.");
                }
                fromCcState = Candidate;
                toCcState = ReleaseDraft;
                break;

            case Draft:
                if (requestState != WIP || requestState != Approved) {
                    throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot transit to '" + requestState + "' state.");
                }
                if (requestState == WIP) {
                    fromCcState = ReleaseDraft;
                    toCcState = Candidate;
                }
                break;

            case Approved:
                if (requestState != WIP || requestState != Draft || requestState != Published) {
                    throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot transit to '" + requestState + "' state.");
                }
                if (requestState == WIP) {
                    fromCcState = ReleaseDraft;
                    toCcState = Candidate;
                } else if (requestState == Published) {
                    fromCcState = ReleaseDraft;
                    toCcState = CcState.Published;
                }
                break;

            case Instantiating:
            case Failure:
            case Published:
                throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot be transited.");
        }

        AppUser appUser = sessionService.getAppUser(user);
        ULong userId = ULong.valueOf(appUser.getAppUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        if (!appUser.isDeveloper()) {
            throw new IllegalArgumentException("It only allows to modify the release by the developer.");
        }

        releaseRecord.setState(requestState);
        releaseRecord.setLastUpdatedBy(userId);
        releaseRecord.setLastUpdateTimestamp(timestamp);
        releaseRecord.update(RELEASE.STATE, RELEASE.LAST_UPDATED_BY, RELEASE.LAST_UPDATE_TIMESTAMP);

        // update CCs' states by transited release state.
        if (fromCcState != null && toCcState != null) {
            AssignComponents assignComponents = getAssignComponents(request.getReleaseId());

            Collection<BigInteger> accManifestIds = assignComponents.getUnassignableAccManifestMap().keySet();
            dslContext.update(ACC)
                    .set(ACC.STATE, toCcState.name())
                    .where(and(
                            ACC.STATE.eq(fromCcState.name()),
                            ACC.ACC_ID.in(
                                    dslContext.select(ACC.ACC_ID)
                                            .from(ACC)
                                            .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                                            .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                                            .where(and(
                                                    RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                                    ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds)
                                            )).fetchStream().collect(Collectors.toList())
                            )
                    ))
                    .execute();

            dslContext.update(ASCC)
                    .set(ASCC.STATE, toCcState.name())
                    .where(and(
                            ASCC.STATE.eq(fromCcState.name()),
                            ASCC.ASCC_ID.in(
                                    dslContext.select(ASCC.ASCC_ID)
                                            .from(ASCC)
                                            .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                                            .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                                            .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                                            .where(and(
                                                    RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                                    ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds)
                                            )).fetchStream().collect(Collectors.toList())
                            )
                    ))
                    .execute();

            dslContext.update(BCC)
                    .set(BCC.STATE, toCcState.name())
                    .where(and(
                            BCC.STATE.eq(fromCcState.name()),
                            BCC.BCC_ID.in(
                                    dslContext.select(BCC.BCC_ID)
                                            .from(BCC)
                                            .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                                            .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                                            .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                                            .where(and(
                                                    RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                                    ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds)
                                            )).fetchStream().collect(Collectors.toList())
                            )
                    ))
                    .execute();

            Collection<BigInteger> asccpManifestIds = assignComponents.getUnassignableAsccpManifestMap().keySet();
            dslContext.update(ASCCP)
                    .set(ASCCP.STATE, toCcState.name())
                    .where(and(
                            ASCCP.STATE.eq(fromCcState.name()),
                            ASCCP.ASCCP_ID.in(
                                    dslContext.select(ASCCP.ASCCP_ID)
                                            .from(ASCCP)
                                            .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                                            .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                                            .where(and(
                                                    RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                                    ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(asccpManifestIds)
                                            )).fetchStream().collect(Collectors.toList())
                            )
                    ))
                    .execute();

            Collection<BigInteger> bccpManifestIds = assignComponents.getUnassignableBccpManifestMap().keySet();
            dslContext.update(BCCP)
                    .set(BCCP.STATE, toCcState.name())
                    .where(and(
                            BCCP.STATE.eq(fromCcState.name()),
                            BCCP.BCCP_ID.in(
                                    dslContext.select(BCCP.BCCP_ID)
                                            .from(BCCP)
                                            .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                                            .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                                            .where(and(
                                                    RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                                    BCCP_MANIFEST.BCCP_MANIFEST_ID.in(bccpManifestIds)
                                            )).fetchStream().collect(Collectors.toList())
                            )
                    ))
                    .execute();

            if (toCcState == CcState.Published) {
                ensureReleaseIntegrity(request.getReleaseId());
                cleanUp(request.getReleaseId());
            }
        }
    }

    private void ensureReleaseIntegrity(BigInteger releaseId) {
        throw new UnsupportedOperationException();
    }

    private void cleanUp(BigInteger releaseId) {
        throw new UnsupportedOperationException();
    }
}
