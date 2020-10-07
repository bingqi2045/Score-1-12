package org.oagi.score.repo.component.release;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record8;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.data.Release;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;
import org.oagi.score.gateway.http.api.cc_management.service.CcNodeService;
import org.oagi.score.gateway.http.api.code_list_management.service.CodeListService;
import org.oagi.score.gateway.http.api.release_management.data.*;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repository.SrtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.gateway.http.api.cc_management.data.CcState.Candidate;
import static org.oagi.score.gateway.http.api.cc_management.data.CcState.ReleaseDraft;
import static org.oagi.score.gateway.http.api.release_management.data.ReleaseState.*;

@Repository
public class ReleaseRepository implements SrtRepository<Release> {
    private Map<ULong, ULong> prevNextAccManifestIdMap = new HashMap<>();
    private Map<ULong, ULong> prevNextAsccpManifestIdMap = new HashMap<>();
    private Map<ULong, ULong> prevNextBccpManifestIdMap = new HashMap<>();
    private Map<ULong, ULong> prevNextBdtManifestIdMap = new HashMap<>();

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

    public void clear() {
        prevNextAccManifestIdMap.clear();
        prevNextAsccpManifestIdMap.clear();
        prevNextBccpManifestIdMap.clear();
        prevNextBdtManifestIdMap.clear();
    }

    public void copyWorkingManifestsTo(
            BigInteger releaseId,
            List<BigInteger> accManifestIds,
            List<BigInteger> asccpManifestIds,
            List<BigInteger> bccpManifestIds,
            List<BigInteger> codeListManifestIds,
            List<BigInteger> bdtManifestIds,
            List<BigInteger> bdtScManifestIds,
            List<BigInteger> xbtManifestIds,
            List<BigInteger> agencyIdListManifestIds) {

        ReleaseRecord releaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchOne();
        ReleaseRecord workingReleaseRecord = dslContext.selectFrom(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOne();

        if (workingReleaseRecord == null) {
            throw new IllegalStateException("Can not find 'Working' release");
        }

        try {
            // copying manifests from 'Working' release
            copyAccManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), accManifestIds);
            updateAccBasedAccManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyDtManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), bdtManifestIds);

            copyAsccpManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), asccpManifestIds);
            updateAsccpRoleOfAccManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyBccpManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), bccpManifestIds);
            updateBccpDtManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyAsccManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), accManifestIds);
            updateAsccRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyBccManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), accManifestIds);
            updateBccRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyDtScManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), bdtScManifestIds);
            updateBdtScRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyXbtManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), xbtManifestIds);

            copyCodeListManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), codeListManifestIds);
            updateCodeListRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyCodeListValueManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), codeListManifestIds);
            updateCodeListValueRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyAgencyIdListManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), agencyIdListManifestIds);
            updateAgencyIdListRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

            copyAgencyIdListValueManifestRecordsFromWorking(releaseRecord.getReleaseId().toBigInteger(),
                    workingReleaseRecord.getReleaseId().toBigInteger(), agencyIdListManifestIds);
            updateAgencyIdListValueRelationManifestIds(releaseRecord.getReleaseId().toBigInteger());

        } catch (Exception e) {
            releaseRecord.setReleaseNote(e.getMessage());
            releaseRecord.update(RELEASE.RELEASE_NOTE);
        }
    }

    private void copyAccManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                   List<BigInteger> accManifestIds) {
        dslContext.insertInto(ACC_MANIFEST,
                ACC_MANIFEST.RELEASE_ID,
                ACC_MANIFEST.ACC_ID,
                ACC_MANIFEST.BASED_ACC_MANIFEST_ID,
                ACC_MANIFEST.CONFLICT,
                ACC_MANIFEST.REVISION_ID,
                ACC_MANIFEST.PREV_ACC_MANIFEST_ID,
                ACC_MANIFEST.NEXT_ACC_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        ACC_MANIFEST.ACC_ID,
                        ACC_MANIFEST.BASED_ACC_MANIFEST_ID,
                        ACC_MANIFEST.CONFLICT,
                        ACC_MANIFEST.REVISION_ID,
                        ACC_MANIFEST.PREV_ACC_MANIFEST_ID,
                        ACC_MANIFEST.ACC_MANIFEST_ID)
                        .from(ACC_MANIFEST)
                        .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                        .where(and(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(ACC.STATE.eq(CcState.Published.name()), // WIP
                                        ACC_MANIFEST.ACC_MANIFEST_ID.in(accManifestIds)))))).execute();
    }

    private void copyDtManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                   List<BigInteger> dtManifestIds) {
        dslContext.insertInto(DT_MANIFEST,
                DT_MANIFEST.RELEASE_ID,
                DT_MANIFEST.DT_ID,
                DT_MANIFEST.CONFLICT,
                DT_MANIFEST.REVISION_ID,
                DT_MANIFEST.PREV_DT_MANIFEST_ID,
                DT_MANIFEST.NEXT_DT_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        DT_MANIFEST.DT_ID,
                        DT_MANIFEST.CONFLICT,
                        DT_MANIFEST.REVISION_ID,
                        DT_MANIFEST.PREV_DT_MANIFEST_ID,
                        DT_MANIFEST.DT_MANIFEST_ID)
                        .from(DT_MANIFEST)
                        .join(DT).on(DT_MANIFEST.DT_ID.eq(DT.DT_ID))
                        .where(and(DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(DT.STATE.eq(CcState.Published.name()),
                                        DT_MANIFEST.DT_MANIFEST_ID.in(dtManifestIds)))))).execute();
    }

    private void copyAsccpManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                   List<BigInteger> asccpManifestIds) {
        dslContext.insertInto(ASCCP_MANIFEST,
                ASCCP_MANIFEST.RELEASE_ID,
                ASCCP_MANIFEST.ASCCP_ID,
                ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID,
                ASCCP_MANIFEST.CONFLICT,
                ASCCP_MANIFEST.REVISION_ID,
                ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID,
                ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        ASCCP_MANIFEST.ASCCP_ID,
                        ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID,
                        ASCCP_MANIFEST.CONFLICT,
                        ASCCP_MANIFEST.REVISION_ID,
                        ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID,
                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                        .from(ASCCP_MANIFEST)
                        .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                        .where(and(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(ASCCP.STATE.eq(CcState.Published.name()),
                                        ASCCP_MANIFEST.ASCCP_MANIFEST_ID.in(asccpManifestIds)))))).execute();
    }

    private void copyBccpManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                     List<BigInteger> bccpManifestIds) {
        dslContext.insertInto(BCCP_MANIFEST,
                BCCP_MANIFEST.RELEASE_ID,
                BCCP_MANIFEST.BCCP_ID,
                BCCP_MANIFEST.BDT_MANIFEST_ID,
                BCCP_MANIFEST.CONFLICT,
                BCCP_MANIFEST.REVISION_ID,
                BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID,
                BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        BCCP_MANIFEST.BCCP_ID,
                        BCCP_MANIFEST.BDT_MANIFEST_ID,
                        BCCP_MANIFEST.CONFLICT,
                        BCCP_MANIFEST.REVISION_ID,
                        BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID,
                        BCCP_MANIFEST.BCCP_MANIFEST_ID)
                        .from(BCCP_MANIFEST)
                        .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                        .where(and(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(BCCP.STATE.eq(CcState.Published.name()),
                                        BCCP_MANIFEST.BCCP_MANIFEST_ID.in(bccpManifestIds)))))).execute();
    }

    private void copyAsccManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                   List<BigInteger> accManifestIds) {
        dslContext.insertInto(ASCC_MANIFEST,
                ASCC_MANIFEST.RELEASE_ID,
                ASCC_MANIFEST.ASCC_ID,
                ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                ASCC_MANIFEST.CONFLICT,
                ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID,
                ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        ASCC_MANIFEST.ASCC_ID,
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                        ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID,
                        ASCC_MANIFEST.CONFLICT,
                        ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID,
                        ASCC_MANIFEST.ASCC_MANIFEST_ID)
                        .from(ASCC_MANIFEST)
                        .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                        .where(and(ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(ASCC.STATE.eq(CcState.Published.name()),
                                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.in(accManifestIds)))))).execute();
    }

    private void copyBccManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                    List<BigInteger> accManifestIds) {
        dslContext.insertInto(BCC_MANIFEST,
                BCC_MANIFEST.RELEASE_ID,
                BCC_MANIFEST.BCC_ID,
                BCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                BCC_MANIFEST.TO_BCCP_MANIFEST_ID,
                BCC_MANIFEST.CONFLICT,
                BCC_MANIFEST.PREV_BCC_MANIFEST_ID,
                BCC_MANIFEST.NEXT_BCC_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        BCC_MANIFEST.BCC_ID,
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID,
                        BCC_MANIFEST.TO_BCCP_MANIFEST_ID,
                        BCC_MANIFEST.CONFLICT,
                        BCC_MANIFEST.PREV_BCC_MANIFEST_ID,
                        BCC_MANIFEST.BCC_MANIFEST_ID)
                        .from(BCC_MANIFEST)
                        .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                        .where(and(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(BCC.STATE.eq(CcState.Published.name()),
                                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.in(accManifestIds)))))).execute();
    }

    private void copyDtScManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                   List<BigInteger> dtScManifestIds) {
        dslContext.insertInto(DT_SC_MANIFEST,
                DT_SC_MANIFEST.RELEASE_ID,
                DT_SC_MANIFEST.DT_SC_ID,
                DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID,
                DT_SC_MANIFEST.CONFLICT,
                DT_SC_MANIFEST.REVISION_ID,
                DT_SC_MANIFEST.PREV_DT_SC_MANIFEST_ID,
                DT_SC_MANIFEST.NEXT_DT_SC_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        DT_SC_MANIFEST.DT_SC_ID,
                        DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID,
                        DT_SC_MANIFEST.CONFLICT,
                        DT_SC_MANIFEST.REVISION_ID,
                        DT_SC_MANIFEST.PREV_DT_SC_MANIFEST_ID,
                        DT_SC_MANIFEST.DT_SC_MANIFEST_ID)
                        .from(DT_SC_MANIFEST)
                        .where(DT_SC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)))).execute();
    }

    private void copyXbtManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                    List<BigInteger> xbtManifestIds) {
        dslContext.insertInto(XBT_MANIFEST,
                XBT_MANIFEST.RELEASE_ID,
                XBT_MANIFEST.XBT_ID,
                XBT_MANIFEST.CONFLICT,
                XBT_MANIFEST.PREV_XBT_MANIFEST_ID,
                XBT_MANIFEST.NEXT_XBT_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        XBT_MANIFEST.XBT_ID,
                        XBT_MANIFEST.CONFLICT,
                        XBT_MANIFEST.PREV_XBT_MANIFEST_ID,
                        XBT_MANIFEST.XBT_MANIFEST_ID)
                        .from(XBT_MANIFEST)
                        .where(XBT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId))))
                        .execute();
    }

    private void copyCodeListManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                    List<BigInteger> codeListManifestIds) {
        dslContext.insertInto(CODE_LIST_MANIFEST,
                CODE_LIST_MANIFEST.RELEASE_ID,
                CODE_LIST_MANIFEST.CODE_LIST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.CONFLICT,
                CODE_LIST_MANIFEST.REVISION_ID,
                CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        CODE_LIST_MANIFEST.CODE_LIST_ID,
                        CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                        CODE_LIST_MANIFEST.CONFLICT,
                        CODE_LIST_MANIFEST.REVISION_ID,
                        CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID,
                        CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID)
                        .from(CODE_LIST_MANIFEST)
                        .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                        .where(and(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(CODE_LIST.STATE.eq(CcState.Published.name()),
                                        CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.in(codeListManifestIds)))))).execute();
    }

    private void copyCodeListValueManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                        List<BigInteger> codeListManifestIds) {
        dslContext.insertInto(CODE_LIST_VALUE_MANIFEST,
                CODE_LIST_VALUE_MANIFEST.RELEASE_ID,
                CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID,
                CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_VALUE_MANIFEST.CONFLICT,
                CODE_LIST_VALUE_MANIFEST.PREV_CODE_LIST_VALUE_MANIFEST_ID,
                CODE_LIST_VALUE_MANIFEST.NEXT_CODE_LIST_VALUE_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID,
                        CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID,
                        CODE_LIST_VALUE_MANIFEST.CONFLICT,
                        CODE_LIST_VALUE_MANIFEST.PREV_CODE_LIST_VALUE_MANIFEST_ID,
                        CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID)
                        .from(CODE_LIST_VALUE_MANIFEST)
                        .join(CODE_LIST_MANIFEST).on(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID))
                        .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                        .where(and(CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(CODE_LIST.STATE.eq(CcState.Published.name()),
                                        CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.in(codeListManifestIds)))))).execute();
    }

    private void copyAgencyIdListManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                        List<BigInteger> agencyIdListManifestIds) {
        dslContext.insertInto(AGENCY_ID_LIST_MANIFEST,
                AGENCY_ID_LIST_MANIFEST.RELEASE_ID,
                AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID,
                AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID,
                AGENCY_ID_LIST_MANIFEST.CONFLICT,
                AGENCY_ID_LIST_MANIFEST.REVISION_ID,
                AGENCY_ID_LIST_MANIFEST.PREV_AGENCY_ID_LIST_MANIFEST_ID,
                AGENCY_ID_LIST_MANIFEST.NEXT_AGENCY_ID_LIST_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID,
                        AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID,
                        AGENCY_ID_LIST_MANIFEST.CONFLICT,
                        AGENCY_ID_LIST_MANIFEST.REVISION_ID,
                        AGENCY_ID_LIST_MANIFEST.PREV_AGENCY_ID_LIST_MANIFEST_ID,
                        AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID)
                        .from(AGENCY_ID_LIST_MANIFEST)
                        .join(AGENCY_ID_LIST).on(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                        .where(and(AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(AGENCY_ID_LIST.STATE.eq(CcState.Published.name()),
                                        AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.in(agencyIdListManifestIds)))))).execute();
    }

    private void copyAgencyIdListValueManifestRecordsFromWorking(BigInteger releaseId, BigInteger workingReleaseId,
                                                             List<BigInteger> agencyIdListManifestIds) {
        dslContext.insertInto(AGENCY_ID_LIST_VALUE_MANIFEST,
                AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID,
                AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID,
                AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                AGENCY_ID_LIST_VALUE_MANIFEST.CONFLICT,
                AGENCY_ID_LIST_VALUE_MANIFEST.PREV_AGENCY_ID_LIST_VALUE_MANIFEST_ID,
                AGENCY_ID_LIST_VALUE_MANIFEST.NEXT_AGENCY_ID_LIST_VALUE_MANIFEST_ID)
                .select(dslContext.select(
                        inline(ULong.valueOf(releaseId)),
                        AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID,
                        AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                        AGENCY_ID_LIST_VALUE_MANIFEST.CONFLICT,
                        AGENCY_ID_LIST_VALUE_MANIFEST.PREV_AGENCY_ID_LIST_VALUE_MANIFEST_ID,
                        AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID)
                        .from(AGENCY_ID_LIST_VALUE_MANIFEST)
                        .join(AGENCY_ID_LIST_MANIFEST).on(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID))
                        .join(AGENCY_ID_LIST).on(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                        .where(and(AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(workingReleaseId)),
                                (or(AGENCY_ID_LIST.STATE.eq(CcState.Published.name()),
                                        AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.in(agencyIdListManifestIds)))))).execute();
    }
    
    private void updateAccBasedAccManifestIds(BigInteger releaseId) {
        dslContext.update(ACC_MANIFEST.join(ACC_MANIFEST.as("based"))
                .on(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ACC_MANIFEST.as("based").NEXT_ACC_MANIFEST_ID)))
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, ACC_MANIFEST.as("based").ACC_MANIFEST_ID)
                .where(and(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ACC_MANIFEST.as("based").RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateAsccpRoleOfAccManifestIds(BigInteger releaseId) {
        dslContext.update(ASCCP_MANIFEST.join(ACC_MANIFEST)
                .on(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ACC_MANIFEST.NEXT_ACC_MANIFEST_ID)))
                .set(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, ACC_MANIFEST.ACC_MANIFEST_ID)
                .where(and(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateBccpDtManifestIds(BigInteger releaseId) {
        dslContext.update(BCCP_MANIFEST.join(DT_MANIFEST)
                .on(BCCP_MANIFEST.BDT_MANIFEST_ID.eq(DT_MANIFEST.NEXT_DT_MANIFEST_ID)))
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, DT_MANIFEST.DT_MANIFEST_ID)
                .where(and(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateAsccRelationManifestIds(BigInteger releaseId) {
        dslContext.update(ASCC_MANIFEST
                .join(ACC_MANIFEST)
                .on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.NEXT_ACC_MANIFEST_ID))
                .join(ASCCP_MANIFEST)
                .on(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID)))
                .set(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID, ACC_MANIFEST.ACC_MANIFEST_ID)
                .set(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID, ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                .where(and(ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateBccRelationManifestIds(BigInteger releaseId) {
        dslContext.update(BCC_MANIFEST.join(ACC_MANIFEST)
                .on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.NEXT_ACC_MANIFEST_ID))
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID)))
                .set(BCC_MANIFEST.FROM_ACC_MANIFEST_ID, ACC_MANIFEST.ACC_MANIFEST_ID)
                .set(BCC_MANIFEST.TO_BCCP_MANIFEST_ID, BCCP_MANIFEST.BCCP_MANIFEST_ID)
                .where(and(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateBdtScRelationManifestIds(BigInteger releaseId) {
        dslContext.update(DT_SC_MANIFEST.join(DT_MANIFEST)
                .on(DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID.eq(DT_MANIFEST.NEXT_DT_MANIFEST_ID)))
                .set(DT_SC_MANIFEST.OWNER_DT_MANIFEST_ID, DT_MANIFEST.DT_MANIFEST_ID)
                .where(and(DT_SC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateCodeListRelationManifestIds(BigInteger releaseId) {
        dslContext.update(CODE_LIST_MANIFEST.join(CODE_LIST_MANIFEST.as("based"))
                .on(CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.as("based").NEXT_CODE_LIST_MANIFEST_ID)))
                .set(CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.as("based").CODE_LIST_MANIFEST_ID)
                .where(and(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        CODE_LIST_MANIFEST.as("based").RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateCodeListValueRelationManifestIds(BigInteger releaseId) {
        dslContext.update(CODE_LIST_VALUE_MANIFEST.join(CODE_LIST_MANIFEST)
                .on(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID)))
                .set(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID)
                .where(and(CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateAgencyIdListRelationManifestIds(BigInteger releaseId) {
        dslContext.update(AGENCY_ID_LIST_MANIFEST.join(AGENCY_ID_LIST_MANIFEST.as("based"))
                .on(AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID.eq(AGENCY_ID_LIST_MANIFEST.as("based").NEXT_AGENCY_ID_LIST_MANIFEST_ID)))
                .set(AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID, AGENCY_ID_LIST_MANIFEST.as("based").AGENCY_ID_LIST_MANIFEST_ID)
                .where(and(AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        AGENCY_ID_LIST_MANIFEST.as("based").RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
    }

    private void updateAgencyIdListValueRelationManifestIds(BigInteger releaseId) {
        dslContext.update(AGENCY_ID_LIST_VALUE_MANIFEST.join(AGENCY_ID_LIST_MANIFEST)
                .on(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(AGENCY_ID_LIST_MANIFEST.NEXT_AGENCY_ID_LIST_MANIFEST_ID)))
                .set(AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID, AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID)
                .where(and(AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .execute();
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
                                ACC.STATE.notEqual(CcState.Published.name())
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
                        ASCCP.STATE.notEqual(CcState.Published.name())
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
                        BCCP.STATE.notEqual(CcState.Published.name())
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
                        CODE_LIST.STATE.notEqual(CcState.Published.name())
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
            for (AccManifestRecord accManifestRecord : accManifestRecords) {
                if (workingReleaseId.equals(accManifestRecord.getReleaseId())) {
                    workingRecord = accManifestRecord;
                } else if (currentReleaseId.equals(accManifestRecord.getReleaseId())) {
                    currentRecord = accManifestRecord;
                }
                accManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }
            dslContext.update(ACC_MANIFEST).set(ACC_MANIFEST.NEXT_ACC_MANIFEST_ID, currentRecord.getAccManifestId())
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(workingRecord.getPrevAccManifestId())).execute();
            currentRecord.setPrevAccManifestId(workingRecord.getPrevAccManifestId());
            currentRecord.setNextAccManifestId(workingRecord.getAccManifestId());
            currentRecord.update(ACC_MANIFEST.CONFLICT,
                    ACC_MANIFEST.PREV_ACC_MANIFEST_ID, ACC_MANIFEST.NEXT_ACC_MANIFEST_ID);

            workingRecord.setPrevAccManifestId(currentRecord.getAccManifestId());
            workingRecord.update(ACC_MANIFEST.CONFLICT,
                    ACC_MANIFEST.PREV_ACC_MANIFEST_ID);
        });

        // ASCCs
        dslContext.selectFrom(ASCC_MANIFEST)
                .fetchStream().collect(groupingBy(AsccManifestRecord::getAsccId))
                .values().forEach(asccManifestRecords -> {

            AsccManifestRecord workingRecord = null;
            AsccManifestRecord currentRecord = null;
            for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
                if (workingReleaseId.equals(asccManifestRecord.getReleaseId())) {
                    workingRecord = asccManifestRecord;
                } else if (currentReleaseId.equals(asccManifestRecord.getReleaseId())) {
                    currentRecord = asccManifestRecord;
                }

                asccManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }
            dslContext.update(ASCC_MANIFEST).set(ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID, currentRecord.getAsccManifestId())
                    .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(workingRecord.getPrevAsccManifestId())).execute();
            currentRecord.setPrevAsccManifestId(workingRecord.getPrevAsccManifestId());
            currentRecord.setNextAsccManifestId(workingRecord.getAsccManifestId());
            currentRecord.update(ASCC_MANIFEST.CONFLICT,
                    ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID, ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID);

            workingRecord.setPrevAsccManifestId(currentRecord.getAsccManifestId());
            workingRecord.update(ASCC_MANIFEST.CONFLICT,
                    ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID);
        });

        // BCCs
        dslContext.selectFrom(BCC_MANIFEST)
                .fetchStream().collect(groupingBy(BccManifestRecord::getBccId))
                .values().forEach(bccManifestRecords -> {

            BccManifestRecord workingRecord = null;
            BccManifestRecord currentRecord = null;
            for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
                if (workingReleaseId.equals(bccManifestRecord.getReleaseId())) {
                    workingRecord = bccManifestRecord;
                } else if (currentReleaseId.equals(bccManifestRecord.getReleaseId())) {
                    currentRecord = bccManifestRecord;
                }

                bccManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }
            dslContext.update(BCC_MANIFEST).set(BCC_MANIFEST.NEXT_BCC_MANIFEST_ID, currentRecord.getBccManifestId())
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(workingRecord.getPrevBccManifestId())).execute();
            currentRecord.setPrevBccManifestId(workingRecord.getPrevBccManifestId());
            currentRecord.setNextBccManifestId(workingRecord.getBccManifestId());
            currentRecord.update(BCC_MANIFEST.CONFLICT,
                    BCC_MANIFEST.PREV_BCC_MANIFEST_ID, BCC_MANIFEST.NEXT_BCC_MANIFEST_ID);

            workingRecord.setPrevBccManifestId(currentRecord.getBccManifestId());
            workingRecord.update(BCC_MANIFEST.CONFLICT,
                    BCC_MANIFEST.PREV_BCC_MANIFEST_ID);
        });

        // ASCCPs
        dslContext.selectFrom(ASCCP_MANIFEST)
                .fetchStream().collect(groupingBy(AsccpManifestRecord::getAsccpId))
                .values().forEach(asccpManifestRecords -> {

            AsccpManifestRecord workingRecord = null;
            AsccpManifestRecord currentRecord = null;
            for (AsccpManifestRecord asccpManifestRecord : asccpManifestRecords) {
                if (workingReleaseId.equals(asccpManifestRecord.getReleaseId())) {
                    workingRecord = asccpManifestRecord;
                } else if (currentReleaseId.equals(asccpManifestRecord.getReleaseId())) {
                    currentRecord = asccpManifestRecord;
                }
                asccpManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }
            dslContext.update(ASCCP_MANIFEST).set(ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID, currentRecord.getAsccpManifestId())
                    .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(workingRecord.getPrevAsccpManifestId())).execute();
            currentRecord.setPrevAsccpManifestId(workingRecord.getPrevAsccpManifestId());
            currentRecord.setNextAsccpManifestId(workingRecord.getAsccpManifestId());
            currentRecord.update(ASCCP_MANIFEST.CONFLICT,
                    ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID, ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID);

            workingRecord.setPrevAsccpManifestId(currentRecord.getAsccpManifestId());
            workingRecord.update(ASCCP_MANIFEST.CONFLICT,
                    ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID);
        });

        // BCCPs
        dslContext.selectFrom(BCCP_MANIFEST)
                .fetchStream().collect(groupingBy(BccpManifestRecord::getBccpId))
                .values().forEach(bccpManifestRecords -> {

            BccpManifestRecord workingRecord = null;
            BccpManifestRecord currentRecord = null;
            for (BccpManifestRecord bccpManifestRecord : bccpManifestRecords) {
                if (workingReleaseId.equals(bccpManifestRecord.getReleaseId())) {
                    workingRecord = bccpManifestRecord;
                } else if (currentReleaseId.equals(bccpManifestRecord.getReleaseId())) {
                    currentRecord = bccpManifestRecord;
                }

                bccpManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }
            dslContext.update(BCCP_MANIFEST).set(BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID, currentRecord.getBccpManifestId())
                    .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(workingRecord.getPrevBccpManifestId())).execute();
            currentRecord.setPrevBccpManifestId(workingRecord.getPrevBccpManifestId());
            currentRecord.setNextBccpManifestId(workingRecord.getBccpManifestId());
            currentRecord.update(BCCP_MANIFEST.CONFLICT,
                    BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID, BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID);

            workingRecord.setPrevBccpManifestId(currentRecord.getBccpManifestId());
            workingRecord.update(BCCP_MANIFEST.CONFLICT,
                    BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID);
        });

        // CODE_LISTs
        dslContext.selectFrom(CODE_LIST_MANIFEST)
                .fetchStream().collect(groupingBy(CodeListManifestRecord::getCodeListId))
                .values().forEach(codeListManifestRecords -> {

            CodeListManifestRecord workingRecord = null;
            CodeListManifestRecord currentRecord = null;
            for (CodeListManifestRecord codeListManifestRecord : codeListManifestRecords) {
                if (workingReleaseId.equals(codeListManifestRecord.getReleaseId())) {
                    workingRecord = codeListManifestRecord;
                } else if (currentReleaseId.equals(codeListManifestRecord.getReleaseId())) {
                    currentRecord = codeListManifestRecord;
                }
                codeListManifestRecord.setConflict((byte) 0);
            }

            if (currentRecord == null) {
                return;
            }
            dslContext.update(CODE_LIST_MANIFEST).set(CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID, currentRecord.getCodeListManifestId())
                    .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(workingRecord.getPrevCodeListManifestId())).execute();
            currentRecord.setPrevCodeListManifestId(workingRecord.getPrevCodeListManifestId());
            currentRecord.setNextCodeListManifestId(workingRecord.getCodeListManifestId());
            currentRecord.update(CODE_LIST_MANIFEST.CONFLICT,
                    CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID);

            workingRecord.setPrevCodeListManifestId(currentRecord.getCodeListManifestId());
            workingRecord.update(CODE_LIST_MANIFEST.CONFLICT,
                    CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID);
        });


    }
}
