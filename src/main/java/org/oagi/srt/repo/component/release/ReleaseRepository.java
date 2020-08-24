package org.oagi.srt.repo.component.release;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record8;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.CcType;
import org.oagi.srt.gateway.http.api.cc_management.service.CcNodeService;
import org.oagi.srt.gateway.http.api.code_list_management.service.CodeListService;
import org.oagi.srt.gateway.http.api.release_management.data.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repository.SrtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.or;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.Candidate;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.ReleaseDraft;
import static org.oagi.srt.gateway.http.api.release_management.data.ReleaseState.*;

@Repository
public class ReleaseRepository implements SrtRepository<Release> {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CcNodeService ccNodeService;

    @Autowired
    private CodeListService codeListService;

    @Override
    public List<Release> findAll() {
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.GUID, RELEASE.RELEASE_NUM,
                RELEASE.RELEASE_NOTE, RELEASE.RELEASE_LICENSE, RELEASE.STATE,
                RELEASE.NAMESPACE_ID, RELEASE.CREATED_BY, RELEASE.CREATION_TIMESTAMP,
                RELEASE.LAST_UPDATED_BY, RELEASE.LAST_UPDATE_TIMESTAMP)
                .from(RELEASE).fetchInto(Release.class);
    }

    @Override
    public Release findById(BigInteger id) {
        if (id == null || id.longValue() <= 0L) {
            return null;
        }
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.GUID, RELEASE.RELEASE_NUM,
                RELEASE.RELEASE_NOTE, RELEASE.RELEASE_LICENSE, RELEASE.STATE,
                RELEASE.NAMESPACE_ID, RELEASE.CREATED_BY, RELEASE.CREATION_TIMESTAMP,
                RELEASE.LAST_UPDATED_BY, RELEASE.LAST_UPDATE_TIMESTAMP)
                .from(RELEASE).where(RELEASE.RELEASE_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(Release.class);
    }

    public List<Release> findByReleaseNum(String releaseNum) {
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.GUID, RELEASE.RELEASE_NUM,
                RELEASE.RELEASE_NOTE, RELEASE.RELEASE_LICENSE, RELEASE.STATE,
                RELEASE.NAMESPACE_ID, RELEASE.CREATED_BY, RELEASE.CREATION_TIMESTAMP,
                RELEASE.LAST_UPDATED_BY, RELEASE.LAST_UPDATE_TIMESTAMP)
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

    private void ensureUniqueReleaseNum(BigInteger releaseId, String releaseNum) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq(releaseNum));
        if (releaseId != null) {
            conditions.add(RELEASE.RELEASE_ID.ne(ULong.valueOf(releaseId)));
        }
        if (dslContext.selectCount()
                .from(RELEASE)
                .where(conditions)
                .fetchOptionalInto(Integer.class).orElse(0) > 0) {
            throw new IllegalArgumentException("'" + releaseNum + "' is already exist.");
        }
    }

    public ReleaseRecord create(BigInteger userId,
                                String releaseNum,
                                String releaseNote,
                                String releaseLicense,
                                BigInteger namespaceId) {

        ensureUniqueReleaseNum(null, releaseNum);

        LocalDateTime timestamp = LocalDateTime.now();
        ReleaseRecord releaseRecord = dslContext.insertInto(RELEASE)
                .set(RELEASE.GUID, UUID.randomUUID().toString())
                .set(RELEASE.RELEASE_NUM, releaseNum)
                .set(RELEASE.RELEASE_NOTE, releaseNote)
                .set(RELEASE.RELEASE_LICENSE, releaseLicense)
                .set(RELEASE.NAMESPACE_ID, (namespaceId != null) ? ULong.valueOf(namespaceId) : null)
                .set(RELEASE.STATE, Initialized.name())
                .set(RELEASE.CREATED_BY, ULong.valueOf(userId))
                .set(RELEASE.CREATION_TIMESTAMP, timestamp)
                .set(RELEASE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(RELEASE.LAST_UPDATE_TIMESTAMP, timestamp)
                .returning().fetchOne();

        return releaseRecord;
    }

    public void update(BigInteger userId,
                       BigInteger releaseId,
                       String releaseNum,
                       String releaseNote,
                       String releaseLicense,
                       BigInteger namespaceId) {

        ensureUniqueReleaseNum(releaseId, releaseNum);

        LocalDateTime timestamp = LocalDateTime.now();
        dslContext.update(RELEASE)
                .set(RELEASE.RELEASE_NUM, releaseNum)
                .set(RELEASE.RELEASE_NOTE, releaseNote)
                .set(RELEASE.RELEASE_LICENSE, releaseLicense)
                .set(RELEASE.NAMESPACE_ID, (namespaceId != null) ? ULong.valueOf(namespaceId) : null)
                .set(RELEASE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(RELEASE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .execute();
    }

    public void discard(ReleaseRepositoryDiscardRequest request) {
        ReleaseRecord releaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())))
                .fetchOne();

        if ("Working".equals(releaseRecord.getReleaseNum())) {
            throw new IllegalArgumentException("'Working' release cannot be discarded.");
        }

        if (Initialized != ReleaseState.valueOf(releaseRecord.getState())) {
            throw new IllegalArgumentException("Only the release in '" + Initialized + "' can discard.");
        }

        AppUser user = sessionService.getAppUser(request.getUser());
        if (!user.isDeveloper()) {
            throw new IllegalArgumentException("It only allows to discard the release for developers.");
        }

        releaseRecord.delete();
    }

    public void updateState(BigInteger userId,
                            BigInteger releaseId,
                            ReleaseState releaseState) {
        AppUser appUser = sessionService.getAppUser(userId);
        if (!appUser.isDeveloper()) {
            throw new IllegalArgumentException("It only allows to update the state of the release for developers.");
        }

        LocalDateTime timestamp = LocalDateTime.now();
        dslContext.update(RELEASE)
                .set(RELEASE.STATE, releaseState.name())
                .set(RELEASE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(RELEASE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .execute();
    }

    public void copyWorkingManifestsTo(BigInteger releaseId) {
        copyWorkingManifestsTo(
                releaseId, Arrays.asList(CcState.Published),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
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
            List<BigInteger> bccpManifestIds,
            List<BigInteger> codeListManifestIds,
            List<BigInteger> bdtManifestIds,
            List<BigInteger> bdtScManifestIds,
            List<BigInteger> xbtManifestIds) {

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

            List<DtManifestRecord> bdtManifestRecords = getBdtManifestRecordsInWorking(states, bdtManifestIds);
            Map<ULong, ULong> prevNextBdtManifestIdMap =  copyBdtManifests(releaseRecord, bdtManifestRecords);

            List<BccpManifestRecord> bccpManifestRecords = getBccpManifestRecordsInWorking(
                    states, bccpManifestIds);
            Map<ULong, ULong> prevNextBccpManifestIdMap = copyBccpManifests(
                    releaseRecord, bccpManifestRecords, prevNextBdtManifestIdMap);

            List<AsccManifestRecord> asccManifestRecords = getAsccManifestRecordsInWorking(
                    states, accManifestIds);
            copyAsccManifests(releaseRecord, asccManifestRecords,
                    prevNextAccManifestIdMap, prevNextAsccpManifestIdMap);

            List<BccManifestRecord> bccManifestRecords = getBccManifestRecordsInWorking(
                    states, accManifestIds);
            copyBccManifests(releaseRecord, bccManifestRecords,
                    prevNextAccManifestIdMap, prevNextBccpManifestIdMap);

            List<DtScManifestRecord> bdtScManifestRecords = getBdtScManifestRecordsInWorking(states, bdtScManifestIds);
            copyBdtScManifests(releaseRecord, bdtScManifestRecords, prevNextBdtManifestIdMap);

            List<XbtManifestRecord> XbtManifestRecords = getXbtManifestRecordsInWorking(states, xbtManifestIds);
            copyXbtManifests(releaseRecord, XbtManifestRecords);

            List<CodeListManifestRecord> codeListManifestRecords = getCodeListManifestRecordsInWorking(
                    states, codeListManifestIds);
            copyCodeListManifests(releaseRecord, codeListManifestRecords);

        } catch (Exception e) {
            releaseRecord.setReleaseNote(e.getMessage());
            releaseRecord.update(RELEASE.RELEASE_NOTE);
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

    private List<DtManifestRecord> getBdtManifestRecordsInWorking(List<CcState> states,
                                                                  List<BigInteger> bdtManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(DT.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));

        if (bdtManifestIds != null && bdtManifestIds.size() > 0) {
            conditions.add(DT_MANIFEST.DT_MANIFEST_ID.in(bdtManifestIds));
        }

        return dslContext.select(DT_MANIFEST.fields())
                .from(DT_MANIFEST)
                .join(RELEASE).on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(DT).on(DT_MANIFEST.DT_ID.eq(DT.DT_ID))
                .where(conditions)
                .fetchInto(DtManifestRecord.class);
    }

    private List<DtScManifestRecord> getBdtScManifestRecordsInWorking(List<CcState> states,
                                                                      List<BigInteger> bdtScManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));

        if (bdtScManifestIds != null && bdtScManifestIds.size() > 0) {
            conditions.add(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.in(bdtScManifestIds));
        }

        if (states.indexOf(CcState.Published) > -1) {
            return dslContext.select(DT_SC_MANIFEST.fields())
                    .from(DT_SC_MANIFEST)
                    .join(RELEASE).on(DT_SC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                    .where(conditions)
                    .fetchInto(DtScManifestRecord.class);
        }

        return Collections.emptyList();
    }

    private List<XbtManifestRecord> getXbtManifestRecordsInWorking(List<CcState> states,
                                                                   List<BigInteger> xbtManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));

        if (xbtManifestIds != null && xbtManifestIds.size() > 0) {
            conditions.add(XBT_MANIFEST.XBT_MANIFEST_ID.in(xbtManifestIds));
        }

        if (states.indexOf(CcState.Published) > -1) {
            return dslContext.select(XBT_MANIFEST.fields())
                    .from(XBT_MANIFEST)
                    .join(RELEASE).on(XBT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                    .where(conditions)
                    .fetchInto(XbtManifestRecord.class);
        }

        return Collections.emptyList();
    }

    private List<CodeListManifestRecord> getCodeListManifestRecordsInWorking(List<CcState> states,
                                                                     List<BigInteger> codeListManifestIds) {
        List<Condition> conditions = new ArrayList();
        conditions.add(RELEASE.RELEASE_NUM.eq("Working"));
        conditions.add(CODE_LIST.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
        if (codeListManifestIds != null && codeListManifestIds.size() > 0) {
            conditions.add(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.in(codeListManifestIds));
        }

        return dslContext.select(CODE_LIST_MANIFEST.fields())
                .from(CODE_LIST_MANIFEST)
                .join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(conditions)
                .fetchInto(CodeListManifestRecord.class);
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
                                                List<BccpManifestRecord> bccpManifestRecords,
                                                Map<ULong, ULong> prevNextBdtManifestIdMap) {
        Map<ULong, ULong> prevNextBccpManifestIdMap = new HashMap();
        bccpManifestRecords.forEach(e -> {
            ULong prevBccpManifestId = e.getBccpManifestId();

            e.setBccpManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setBdtManifestId(prevNextBdtManifestIdMap.get(e.getBdtManifestId()));
            e.setBccpManifestId(
                    dslContext.insertInto(BCCP_MANIFEST)
                            .set(e).returning(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                            .fetchOne().getBccpManifestId()
            );

            prevNextBccpManifestIdMap.put(prevBccpManifestId, e.getBccpManifestId());
        });

        return prevNextBccpManifestIdMap;
    }

    private Map<ULong, ULong> copyBdtManifests(ReleaseRecord releaseRecord,
                                                    List<DtManifestRecord> bdtManifestRecords) {
        Map<ULong, ULong> prevNextbdtManifestIdMap = new HashMap();
        bdtManifestRecords.forEach(e -> {
            ULong prevBdtManifestId = e.getDtManifestId();

            e.setDtManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setDtManifestId(
                    dslContext.insertInto(DT_MANIFEST)
                            .set(e).returning(DT_MANIFEST.DT_MANIFEST_ID)
                            .fetchOne().getDtManifestId()
            );

            prevNextbdtManifestIdMap.put(prevBdtManifestId, e.getDtManifestId());
        });

        return prevNextbdtManifestIdMap;
    }

    private Map<ULong, ULong> copyBdtScManifests(ReleaseRecord releaseRecord,
                                                 List<DtScManifestRecord> bdtScManifestRecords,
                                                 Map<ULong, ULong> prevNextBdtManifestIdMap) {
        Map<ULong, ULong> prevNextbdtScManifestIdMap = new HashMap();
        bdtScManifestRecords.forEach(e -> {
            ULong prevBdtScManifestId = e.getDtScManifestId();

            e.setDtScManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setOwnerDtManifestId(prevNextBdtManifestIdMap.get(e.getOwnerDtManifestId()));
            e.setDtScManifestId(
                    dslContext.insertInto(DT_SC_MANIFEST)
                            .set(e).returning(DT_SC_MANIFEST.DT_SC_MANIFEST_ID)
                            .fetchOne().getDtScManifestId()
            );

            prevNextbdtScManifestIdMap.put(prevBdtScManifestId, e.getDtScManifestId());
        });

        return prevNextbdtScManifestIdMap;
    }

    private Map<ULong, ULong> copyXbtManifests(ReleaseRecord releaseRecord,
                                                 List<XbtManifestRecord> xbtManifestRecords) {
        Map<ULong, ULong> prevNextXbtManifestIdMap = new HashMap();
        xbtManifestRecords.forEach(e -> {
            ULong prevXbtManifestId = e.getXbtManifestId();

            e.setXbtManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setXbtManifestId(
                    dslContext.insertInto(XBT_MANIFEST)
                            .set(e).returning(XBT_MANIFEST.XBT_MANIFEST_ID)
                            .fetchOne().getXbtManifestId()
            );

            prevNextXbtManifestIdMap.put(prevXbtManifestId, e.getXbtManifestId());
        });

        return prevNextXbtManifestIdMap;
    }

    private Map<ULong, ULong> copyCodeListManifests(ReleaseRecord releaseRecord,
                                                List<CodeListManifestRecord> codeListManifestRecords) {
        Map<ULong, ULong> prevNextCodeListManifestIdMap = new HashMap();
        codeListManifestRecords.forEach(e -> {
            ULong prevCodeListManifestId = e.getCodeListManifestId();

            e.setCodeListManifestId(null);
            e.setReleaseId(releaseRecord.getReleaseId());
            e.setCodeListManifestId(
                    dslContext.insertInto(CODE_LIST_MANIFEST)
                            .set(e).returning(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID)
                            .fetchOne().getCodeListManifestId()
            );

            prevNextCodeListManifestIdMap.put(prevCodeListManifestId, e.getCodeListManifestId());
        });

        return prevNextCodeListManifestIdMap;
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
        Map<ULong, List<Record8<
                ULong, String, String, LocalDateTime, String,
                String, UInteger, UInteger>>> map =
                dslContext.select(
                        ACC_MANIFEST.ACC_MANIFEST_ID, ACC.DEN, RELEASE.RELEASE_NUM,
                        ACC.LAST_UPDATE_TIMESTAMP, APP_USER.LOGIN_ID, ACC.STATE,
                        REVISION.REVISION_NUM, REVISION.REVISION_TRACKING_NUM)
                        .from(ACC_MANIFEST)
                        .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                        .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                        .join(APP_USER).on(ACC.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                        .join(REVISION).on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                        .where(and(
                                or(
                                        RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                        RELEASE.RELEASE_NUM.eq("Working")
                                ),
                                ACC.STATE.notEqual(Published.name())
                        ))
                        .fetchStream()
                        .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
            AssignableNode node = new AssignableNode();
            node.setManifestId(e.get(0).value1().toBigInteger());
            node.setDen(e.get(0).value2());
            node.setTimestamp(e.get(0).value4());
            node.setOwnerUserId(e.get(0).value5());
            node.setState(CcState.valueOf(e.get(0).value6()));
            node.setRevision(e.get(0).value7().toBigInteger());
            node.setType(CcType.ACC);
            if (e.size() == 2) { // manifest are located at both sides.
                assignComponents.addUnassignableAccManifest(
                        node.getManifestId(), node);
            }
            // manifest is only located at 'Working' release side.
            else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
                assignComponents.addAssignableAccManifest(
                        node.getManifestId(), node);
            }
        });

        // ASCCPs
        map = dslContext.select(
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID, ASCCP.DEN, RELEASE.RELEASE_NUM,
                ASCCP.LAST_UPDATE_TIMESTAMP, APP_USER.LOGIN_ID, ASCCP.STATE,
                REVISION.REVISION_NUM, REVISION.REVISION_TRACKING_NUM)
                .from(ASCCP_MANIFEST)
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(APP_USER).on(ASCCP.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .join(REVISION).on(ASCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(and(
                        or(
                                RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                RELEASE.RELEASE_NUM.eq("Working")
                        ),
                        ASCCP.STATE.notEqual(Published.name())
                ))
                .fetchStream()
                .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
            AssignableNode node = new AssignableNode();
            node.setManifestId(e.get(0).value1().toBigInteger());
            node.setDen(e.get(0).value2());
            node.setTimestamp(e.get(0).value4());
            node.setOwnerUserId(e.get(0).value5());
            node.setState(CcState.valueOf(e.get(0).value6()));
            node.setRevision(e.get(0).value7().toBigInteger());
            node.setType(CcType.ASCCP);
            if (e.size() == 2) { // manifest are located at both sides.
                assignComponents.addUnassignableAsccpManifest(
                        node.getManifestId(), node);
            }
            // manifest is only located at 'Working' release side.
            else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
                assignComponents.addAssignableAsccpManifest(
                        node.getManifestId(), node);
            }
        });

        // BCCPs
        map = dslContext.select(
                BCCP_MANIFEST.BCCP_MANIFEST_ID, BCCP.DEN, RELEASE.RELEASE_NUM,
                BCCP.LAST_UPDATE_TIMESTAMP, APP_USER.LOGIN_ID, BCCP.STATE,
                REVISION.REVISION_NUM, REVISION.REVISION_TRACKING_NUM)
                .from(BCCP_MANIFEST)
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .join(APP_USER).on(BCCP.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .join(REVISION).on(BCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(and(
                        or(
                                RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                RELEASE.RELEASE_NUM.eq("Working")
                        ),
                        BCCP.STATE.notEqual(Published.name())
                ))
                .fetchStream()
                .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
            AssignableNode node = new AssignableNode();
            node.setManifestId(e.get(0).value1().toBigInteger());
            node.setDen(e.get(0).value2());
            node.setTimestamp(e.get(0).value4());
            node.setOwnerUserId(e.get(0).value5());
            node.setState(CcState.valueOf(e.get(0).value6()));
            node.setRevision(e.get(0).value7().toBigInteger());
            node.setType(CcType.BCCP);
            if (e.size() == 2) { // manifest are located at both sides.
                assignComponents.addUnassignableBccpManifest(
                        node.getManifestId(), node);
            }
            // manifest is only located at 'Working' release side.
            else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
                assignComponents.addAssignableBccpManifest(
                        node.getManifestId(), node);
            }
        });

        // CODE_LISTs
        map = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID, CODE_LIST.NAME, RELEASE.RELEASE_NUM,
                CODE_LIST.LAST_UPDATE_TIMESTAMP, APP_USER.LOGIN_ID, CODE_LIST.STATE,
                REVISION.REVISION_NUM, REVISION.REVISION_TRACKING_NUM)
                .from(CODE_LIST_MANIFEST)
                .join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(APP_USER).on(CODE_LIST.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .join(REVISION).on(CODE_LIST_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .where(and(
                        or(
                                RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                                RELEASE.RELEASE_NUM.eq("Working")
                        ),
                        CODE_LIST.STATE.notEqual(Published.name())
                ))
                .fetchStream()
                .collect(groupingBy(e -> e.value1()));

        map.values().forEach(e -> {
            AssignableNode node = new AssignableNode();
            node.setManifestId(e.get(0).value1().toBigInteger());
            node.setDen(e.get(0).value2());
            node.setTimestamp(e.get(0).value4());
            node.setOwnerUserId(e.get(0).value5());
            node.setState(CcState.valueOf(e.get(0).value6()));
            node.setRevision(e.get(0).value7().toBigInteger());
            node.setType(CcType.CODE_LIST);
            if (e.size() == 2) { // manifest are located at both sides.
                assignComponents.addUnassignableCodeListManifest(
                        node.getManifestId(), node);
            }
            // manifest is only located at 'Working' release side.
            else if (e.size() == 1 && "Working".equals(e.get(0).value3())) {
                assignComponents.addAssignableCodeListManifest(
                        node.getManifestId(), node);
            }
        });

        return assignComponents;
    }

    public void transitState(AuthenticatedPrincipal user,
                             TransitStateRequest request) {

        ReleaseRecord releaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())))
                .fetchOne();

        ReleaseState requestState = ReleaseState.valueOf(request.getState());
        CcState fromCcState = null;
        CcState toCcState = null;

        switch (ReleaseState.valueOf(releaseRecord.getState())) {
            case Initialized:
                if (requestState != Draft) {
                    throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot transit to '" + requestState + "' state.");
                }

                requestState = Processing;
                fromCcState = Candidate;
                toCcState = ReleaseDraft;
                break;

            case Draft:
                if (requestState != Initialized && requestState != Published) {
                    throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot transit to '" + requestState + "' state.");
                }

                if (requestState == Initialized) {
                    fromCcState = ReleaseDraft;
                    toCcState = Candidate;
                } else if (requestState == Published) {
                    requestState = Processing;
                    fromCcState = ReleaseDraft;
                    toCcState = CcState.Published;
                }

                break;

            case Processing:
            case Published:
                throw new IllegalArgumentException("The release in '" + releaseRecord.getState() + "' state cannot be transited.");
        }

        AppUser appUser = sessionService.getAppUser(user);
        ULong userId = ULong.valueOf(appUser.getAppUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        if (!appUser.isDeveloper()) {
            throw new IllegalArgumentException("It only allows to modify the release by the developer.");
        }

        releaseRecord.setState(requestState.name());
        releaseRecord.setLastUpdatedBy(userId);
        releaseRecord.setLastUpdateTimestamp(timestamp);
        releaseRecord.update(RELEASE.STATE, RELEASE.LAST_UPDATED_BY, RELEASE.LAST_UPDATE_TIMESTAMP);

        // update CCs' states by transited release state.
        if (fromCcState != null && toCcState != null) {
            if (toCcState == ReleaseDraft) {
                ReleaseValidationRequest validationRequest = request.getValidationRequest();
                for (BigInteger accManifestId : validationRequest.getAssignedAccComponentManifestIds()) {
                    ccNodeService.updateAccState(user, accManifestId, fromCcState, toCcState);
                }
                for (BigInteger asccpManifestId : validationRequest.getAssignedAsccpComponentManifestIds()) {
                    ccNodeService.updateAsccpState(user, asccpManifestId, fromCcState, toCcState);
                }
                for (BigInteger bccpManifestId : validationRequest.getAssignedBccpComponentManifestIds()) {
                    ccNodeService.updateBccpState(user, bccpManifestId, fromCcState, toCcState);
                }
                for (BigInteger codeListManifestId : validationRequest.getAssignedCodeListComponentManifestIds()) {
                    codeListService.updateCodeListState(user, timestamp, codeListManifestId, toCcState);
                }
            } else if (toCcState == Candidate) {
                updateCCStates(user, fromCcState, toCcState, timestamp);

                dslContext.deleteFrom(BCC_MANIFEST)
                        .where(BCC_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(BCCP_MANIFEST)
                        .where(BCCP_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(XBT_MANIFEST)
                        .where(XBT_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(DT_SC_MANIFEST)
                        .where(DT_SC_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(DT_MANIFEST)
                        .where(DT_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(ASCC_MANIFEST)
                        .where(ASCC_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(ASCCP_MANIFEST)
                        .where(ASCCP_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.update(ACC_MANIFEST)
                        .setNull(ACC_MANIFEST.BASED_ACC_MANIFEST_ID)
                        .where(ACC_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();
                dslContext.deleteFrom(CODE_LIST_MANIFEST)
                        .where(CODE_LIST_MANIFEST.RELEASE_ID.eq(releaseRecord.getReleaseId()))
                        .execute();

            } else if (toCcState == CcState.Published) {
                updateCCStates(user, fromCcState, toCcState, timestamp);
            }
        }
    }

    private void updateCCStates(AuthenticatedPrincipal user, CcState fromCcState, CcState toCcState, LocalDateTime timestamp) {
        for (BigInteger accManifestId : dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ACC.STATE.eq(fromCcState.name()),
                        RELEASE.RELEASE_NUM.eq("Working")
                ))
                .fetchInto(BigInteger.class)) {
            ccNodeService.updateAccState(user, accManifestId, fromCcState, toCcState);
        }
        for (BigInteger asccpManifestId : dslContext.select(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                .from(ASCCP_MANIFEST)
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ASCCP.STATE.eq(fromCcState.name()),
                        RELEASE.RELEASE_NUM.eq("Working")
                ))
                .fetchInto(BigInteger.class)) {
            ccNodeService.updateAsccpState(user, asccpManifestId, fromCcState, toCcState);
        }
        for (BigInteger bccpManifestId : dslContext.select(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                .from(BCCP_MANIFEST)
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        BCCP.STATE.eq(fromCcState.name()),
                        RELEASE.RELEASE_NUM.eq("Working")
                ))
                .fetchInto(BigInteger.class)) {
            ccNodeService.updateBccpState(user, bccpManifestId, fromCcState, toCcState);
        }
        for (BigInteger codeListManifestId : dslContext.select(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID)
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        CODE_LIST.STATE.eq(fromCcState.name()),
                        RELEASE.RELEASE_NUM.eq("Working")
                ))
                .fetchInto(BigInteger.class)) {
            codeListService.updateCodeListState(user, timestamp, codeListManifestId, toCcState);
        }
    }

    public boolean isThereAnyDraftRelease(BigInteger releaseId) {
        return dslContext.selectCount()
                .from(RELEASE)
                .where(and(
                        RELEASE.STATE.in(Draft.name(), Processing.name()),
                        RELEASE.RELEASE_ID.ne(ULong.valueOf(releaseId))
                ))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public void cleanUp(BigInteger releaseId) {
        ULong workingReleaseId = dslContext.select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);
        ULong currentReleaseId = ULong.valueOf(releaseId);

        // ACCs
        dslContext.selectFrom(ACC_MANIFEST)
                .fetchStream().collect(groupingBy(AccManifestRecord::getAccId))
                .values().forEach(accManifestRecords -> {

            AccManifestRecord workingRecord = null;
            AccManifestRecord currentRecord = null;
            List<AccManifestRecord> restOfRecords = new ArrayList();
            for (AccManifestRecord accManifestRecord : accManifestRecords) {
                if (workingReleaseId.equals(accManifestRecord.getReleaseId())) {
                    workingRecord = accManifestRecord;
                } else if (currentReleaseId.equals(accManifestRecord.getReleaseId())) {
                    currentRecord = accManifestRecord;
                } else {
                    restOfRecords.add(accManifestRecord);
                }

                accManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }

            currentRecord.setPrevAccManifestId(workingRecord.getPrevAccManifestId());
            currentRecord.setNextAccManifestId(workingRecord.getAccManifestId());
            currentRecord.update(ACC_MANIFEST.CONFLICT,
                    ACC_MANIFEST.PREV_ACC_MANIFEST_ID, ACC_MANIFEST.NEXT_ACC_MANIFEST_ID);

            workingRecord.setPrevAccManifestId(currentRecord.getAccManifestId());
            workingRecord.update(ACC_MANIFEST.CONFLICT,
                    ACC_MANIFEST.PREV_ACC_MANIFEST_ID);

            for (AccManifestRecord record : restOfRecords) {
                if (workingRecord.getAccManifestId().equals(record.getNextAccManifestId())) {
                    record.setNextAccManifestId(currentRecord.getAccManifestId());
                    record.update(ACC_MANIFEST.CONFLICT,
                            ACC_MANIFEST.NEXT_ACC_MANIFEST_ID);
                }
            }
        });

        // ASCCs
        dslContext.selectFrom(ASCC_MANIFEST)
                .fetchStream().collect(groupingBy(AsccManifestRecord::getAsccId))
                .values().forEach(asccManifestRecords -> {

            AsccManifestRecord workingRecord = null;
            AsccManifestRecord currentRecord = null;
            List<AsccManifestRecord> restOfRecords = new ArrayList();
            for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
                if (workingReleaseId.equals(asccManifestRecord.getReleaseId())) {
                    workingRecord = asccManifestRecord;
                } else if (currentReleaseId.equals(asccManifestRecord.getReleaseId())) {
                    currentRecord = asccManifestRecord;
                } else {
                    restOfRecords.add(asccManifestRecord);
                }

                asccManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }

            currentRecord.setPrevAsccManifestId(workingRecord.getPrevAsccManifestId());
            currentRecord.setNextAsccManifestId(workingRecord.getAsccManifestId());
            currentRecord.update(ASCC_MANIFEST.CONFLICT,
                    ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID, ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID);

            workingRecord.setPrevAsccManifestId(currentRecord.getAsccManifestId());
            workingRecord.update(ASCC_MANIFEST.CONFLICT,
                    ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID);

            for (AsccManifestRecord record : restOfRecords) {
                if (workingRecord.getAsccManifestId().equals(record.getNextAsccManifestId())) {
                    record.setNextAsccManifestId(currentRecord.getAsccManifestId());
                    record.update(ASCC_MANIFEST.CONFLICT,
                            ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID);
                }
            }
        });

        // BCCs
        dslContext.selectFrom(BCC_MANIFEST)
                .fetchStream().collect(groupingBy(BccManifestRecord::getBccId))
                .values().forEach(bccManifestRecords -> {

            BccManifestRecord workingRecord = null;
            BccManifestRecord currentRecord = null;
            List<BccManifestRecord> restOfRecords = new ArrayList();
            for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
                if (workingReleaseId.equals(bccManifestRecord.getReleaseId())) {
                    workingRecord = bccManifestRecord;
                } else if (currentReleaseId.equals(bccManifestRecord.getReleaseId())) {
                    currentRecord = bccManifestRecord;
                } else {
                    restOfRecords.add(bccManifestRecord);
                }

                bccManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }

            currentRecord.setPrevBccManifestId(workingRecord.getPrevBccManifestId());
            currentRecord.setNextBccManifestId(workingRecord.getBccManifestId());
            currentRecord.update(BCC_MANIFEST.CONFLICT,
                    BCC_MANIFEST.PREV_BCC_MANIFEST_ID, BCC_MANIFEST.NEXT_BCC_MANIFEST_ID);

            workingRecord.setPrevBccManifestId(currentRecord.getBccManifestId());
            workingRecord.update(BCC_MANIFEST.CONFLICT,
                    BCC_MANIFEST.PREV_BCC_MANIFEST_ID);

            for (BccManifestRecord record : restOfRecords) {
                if (workingRecord.getBccManifestId().equals(record.getNextBccManifestId())) {
                    record.setNextBccManifestId(currentRecord.getBccManifestId());
                    record.update(BCC_MANIFEST.CONFLICT,
                            BCC_MANIFEST.NEXT_BCC_MANIFEST_ID);
                }
            }
        });

        // ASCCPs
        dslContext.selectFrom(ASCCP_MANIFEST)
                .fetchStream().collect(groupingBy(AsccpManifestRecord::getAsccpId))
                .values().forEach(asccpManifestRecords -> {

            AsccpManifestRecord workingRecord = null;
            AsccpManifestRecord currentRecord = null;
            List<AsccpManifestRecord> restOfRecords = new ArrayList();
            for (AsccpManifestRecord asccpManifestRecord : asccpManifestRecords) {
                if (workingReleaseId.equals(asccpManifestRecord.getReleaseId())) {
                    workingRecord = asccpManifestRecord;
                } else if (currentReleaseId.equals(asccpManifestRecord.getReleaseId())) {
                    currentRecord = asccpManifestRecord;
                } else {
                    restOfRecords.add(asccpManifestRecord);
                }

                asccpManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }

            currentRecord.setPrevAsccpManifestId(workingRecord.getPrevAsccpManifestId());
            currentRecord.setNextAsccpManifestId(workingRecord.getAsccpManifestId());
            currentRecord.update(ASCCP_MANIFEST.CONFLICT,
                    ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID, ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID);

            workingRecord.setPrevAsccpManifestId(currentRecord.getAsccpManifestId());
            workingRecord.update(ASCCP_MANIFEST.CONFLICT,
                    ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID);

            for (AsccpManifestRecord record : restOfRecords) {
                if (workingRecord.getAsccpManifestId().equals(record.getNextAsccpManifestId())) {
                    record.setNextAsccpManifestId(currentRecord.getAsccpManifestId());
                    record.update(ASCCP_MANIFEST.CONFLICT,
                            ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID);
                }
            }
        });

        // BCCPs
        dslContext.selectFrom(BCCP_MANIFEST)
                .fetchStream().collect(groupingBy(BccpManifestRecord::getBccpId))
                .values().forEach(bccpManifestRecords -> {

            BccpManifestRecord workingRecord = null;
            BccpManifestRecord currentRecord = null;
            List<BccpManifestRecord> restOfRecords = new ArrayList();
            for (BccpManifestRecord bccpManifestRecord : bccpManifestRecords) {
                if (workingReleaseId.equals(bccpManifestRecord.getReleaseId())) {
                    workingRecord = bccpManifestRecord;
                } else if (currentReleaseId.equals(bccpManifestRecord.getReleaseId())) {
                    currentRecord = bccpManifestRecord;
                } else {
                    restOfRecords.add(bccpManifestRecord);
                }

                bccpManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }

            currentRecord.setPrevBccpManifestId(workingRecord.getPrevBccpManifestId());
            currentRecord.setNextBccpManifestId(workingRecord.getBccpManifestId());
            currentRecord.update(BCCP_MANIFEST.CONFLICT,
                    BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID, BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID);

            workingRecord.setPrevBccpManifestId(currentRecord.getBccpManifestId());
            workingRecord.update(BCCP_MANIFEST.CONFLICT,
                    BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID);

            for (BccpManifestRecord record : restOfRecords) {
                if (workingRecord.getBccpManifestId().equals(record.getNextBccpManifestId())) {
                    record.setNextBccpManifestId(currentRecord.getBccpManifestId());
                    record.update(BCCP_MANIFEST.CONFLICT,
                            BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID);
                }
            }
        });

        // CODE_LISTs
        dslContext.selectFrom(CODE_LIST_MANIFEST)
                .fetchStream().collect(groupingBy(CodeListManifestRecord::getCodeListId))
                .values().forEach(codeListManifestRecords -> {

            CodeListManifestRecord workingRecord = null;
            CodeListManifestRecord currentRecord = null;
            List<CodeListManifestRecord> restOfRecords = new ArrayList();
            for (CodeListManifestRecord codeListManifestRecord : codeListManifestRecords) {
                if (workingReleaseId.equals(codeListManifestRecord.getReleaseId())) {
                    workingRecord = codeListManifestRecord;
                } else if (currentReleaseId.equals(codeListManifestRecord.getReleaseId())) {
                    currentRecord = codeListManifestRecord;
                } else {
                    restOfRecords.add(codeListManifestRecord);
                }

                codeListManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }

            currentRecord.setPrevCodeListManifestId(workingRecord.getPrevCodeListManifestId());
            currentRecord.setNextCodeListManifestId(workingRecord.getCodeListManifestId());
            currentRecord.update(CODE_LIST_MANIFEST.CONFLICT,
                    CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID);

            workingRecord.setPrevCodeListManifestId(currentRecord.getCodeListManifestId());
            workingRecord.update(CODE_LIST_MANIFEST.CONFLICT,
                    CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID);

            for (CodeListManifestRecord record : restOfRecords) {
                if (workingRecord.getCodeListManifestId().equals(record.getNextCodeListManifestId())) {
                    record.setNextCodeListManifestId(currentRecord.getCodeListManifestId());
                    record.update(CODE_LIST_MANIFEST.CONFLICT,
                            CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID);
                }
            }
        });


    }
}
