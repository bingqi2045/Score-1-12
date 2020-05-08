package org.oagi.srt.repo.component.bccp;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Bccp.BCCP;
import static org.oagi.srt.entity.jooq.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class BccpCUDRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    public CreateBccpRepositoryResponse createBccp(CreateBccpRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        DtManifestRecord bdtManifest = dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(request.getBdtManifestId())))
                .fetchOne();

        DtRecord bdt = dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(bdtManifest.getDtId()))
                .fetchOne();

        BccpRecord bccp = new BccpRecord();
        bccp.setGuid(SrtGuid.randomGuid());
        bccp.setPropertyTerm(request.getInitialPropertyTerm());
        bccp.setRepresentationTerm(bdt.getDataTypeTerm());
        bccp.setDen(bccp.getPropertyTerm() + ". " + bccp.getRepresentationTerm());
        bccp.setBdtId(bdt.getDtId());
        bccp.setState(CcState.WIP.name());
        bccp.setIsDeprecated((byte) 0);
        bccp.setIsNillable((byte) 0);
        bccp.setNamespaceId(null);
        bccp.setCreatedBy(userId);
        bccp.setLastUpdatedBy(userId);
        bccp.setOwnerUserId(userId);
        bccp.setCreationTimestamp(timestamp);
        bccp.setLastUpdateTimestamp(timestamp);

        bccp.setBccpId(
                dslContext.insertInto(BCCP)
                        .set(bccp)
                        .returning(BCCP.BCCP_ID).fetchOne().getBccpId()
        );

        BccpManifestRecord bccpManifest = new BccpManifestRecord();
        bccpManifest.setBccpId(bccp.getBccpId());
        bccpManifest.setBdtManifestId(bdtManifest.getDtManifestId());
        bccpManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));

        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccp,
                        RevisionAction.Added,
                        userId, timestamp);
        bccpManifest.setRevisionId(revisionRecord.getRevisionId());

        bccpManifest.setBccpManifestId(
                dslContext.insertInto(BCCP_MANIFEST)
                        .set(bccpManifest)
                        .returning(BCCP_MANIFEST.BCCP_MANIFEST_ID).fetchOne().getBccpManifestId()
        );

        return new CreateBccpRepositoryResponse(bccpManifest.getBccpManifestId().toBigInteger());
    }

    public ReviseBccpRepositoryResponse reviseBccp(ReviseBccpRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccpManifestId())
                ))
                .fetchOne();

        BccpRecord prevBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                .fetchOne();

        if (!CcState.Published.equals(CcState.valueOf(prevBccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
        }

        ULong workingReleaseId = dslContext.select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);

        ULong targetReleaseId = bccpManifestRecord.getReleaseId();
        if (user.isDeveloper()) {
            if (!targetReleaseId.equals(workingReleaseId)) {
                throw new IllegalArgumentException("It only allows to revise the component in 'Working' branch for developers.");
            }
        } else {
            if (targetReleaseId.equals(workingReleaseId)) {
                throw new IllegalArgumentException("It only allows to revise the component in non-'Working' branch for end-users.");
            }
        }

        boolean ownerIsDeveloper = dslContext.select(APP_USER.IS_DEVELOPER)
                .from(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(prevBccpRecord.getOwnerUserId()))
                .fetchOneInto(Boolean.class);

        if (user.isDeveloper() != ownerIsDeveloper) {
            throw new IllegalArgumentException("It only allows to revise the component for users in the same roles.");
        }

        // creates new bccp for revised record.
        BccpRecord nextBccpRecord = prevBccpRecord.copy();
        nextBccpRecord.setState(CcState.WIP.name());
        nextBccpRecord.setCreatedBy(userId);
        nextBccpRecord.setLastUpdatedBy(userId);
        nextBccpRecord.setOwnerUserId(userId);
        nextBccpRecord.setCreationTimestamp(timestamp);
        nextBccpRecord.setLastUpdateTimestamp(timestamp);
        nextBccpRecord.setPrevBccpId(prevBccpRecord.getBccpId());
        nextBccpRecord.setBccpId(
                dslContext.insertInto(BCCP)
                        .set(nextBccpRecord)
                        .returning(BCCP.BCCP_ID).fetchOne().getBccpId()
        );

        prevBccpRecord.setNextBccpId(nextBccpRecord.getBccpId());
        prevBccpRecord.update(BCCP.NEXT_BCCP_ID);

        // creates new revision for revised record.
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        nextBccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Revised,
                        userId, timestamp);

        ULong responseBccpManifestId;
        // Developers only work for 'Working' branch whose manifest records are initially generated by system,
        // so it doesn't need to create new manifest.
        if (user.isDeveloper()) {
            bccpManifestRecord.setBccpId(nextBccpRecord.getBccpId());
            bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            bccpManifestRecord.update(BCCP_MANIFEST.BCCP_ID, BCCP_MANIFEST.REVISION_ID);

            responseBccpManifestId = bccpManifestRecord.getBccpManifestId();
        } else {
            // creates new bccp manifest for revised record.
            BccpManifestRecord nextBccpManifestRecord = bccpManifestRecord.copy();
            nextBccpManifestRecord.setBccpId(nextBccpRecord.getBccpId());
            nextBccpManifestRecord.setReleaseId(targetReleaseId);
            nextBccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            nextBccpManifestRecord.setPrevBccpManifestId(bccpManifestRecord.getBccpManifestId());
            nextBccpManifestRecord.setBccpManifestId(
                    dslContext.insertInto(BCCP_MANIFEST)
                            .set(nextBccpManifestRecord)
                            .returning(BCCP_MANIFEST.BCCP_MANIFEST_ID).fetchOne().getBccpManifestId()
            );

            bccpManifestRecord.setNextBccpManifestId(nextBccpManifestRecord.getBccpManifestId());
            bccpManifestRecord.update(BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID);

            responseBccpManifestId = nextBccpManifestRecord.getBccpManifestId();
        }

        // update `conflict` for bcc_manifests' to_bccp_manifest_id which indicates given bccp manifest.
        dslContext.update(BCC_MANIFEST)
                .set(BCC_MANIFEST.TO_BCCP_MANIFEST_ID, responseBccpManifestId)
                .set(BCC_MANIFEST.CONFLICT, (byte) 1)
                .where(and(
                        BCC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        BCC_MANIFEST.TO_BCCP_MANIFEST_ID.in(Arrays.asList(
                                bccpManifestRecord.getBccpManifestId(),
                                bccpManifestRecord.getPrevBccpManifestId()))
                ))
                .execute();

        return new ReviseBccpRepositoryResponse(responseBccpManifestId.toBigInteger());
    }

    public UpdateBccpPropertiesRepositoryResponse updateBccpProperties(UpdateBccpPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccpManifestId())
                ))
                .fetchOne();

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(bccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!bccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bccp record.
        bccpRecord.setPropertyTerm(request.getPropertyTerm());
        bccpRecord.setDen(bccpRecord.getPropertyTerm() + ". " + bccpRecord.getRepresentationTerm());
        if (StringUtils.isEmpty(request.getDefaultValue()) && StringUtils.isEmpty(request.getFixedValue())) {
            bccpRecord.setDefaultValue(null);
            bccpRecord.setFixedValue(null);
        } else {
            if (!StringUtils.isEmpty(request.getDefaultValue())) {
                bccpRecord.setDefaultValue(request.getDefaultValue());
                bccpRecord.setFixedValue(null);
            } else {
                bccpRecord.setDefaultValue(null);
                bccpRecord.setFixedValue(request.getFixedValue());
            }
        }
        bccpRecord.setDefinition(request.getDefinition());
        bccpRecord.setDefinitionSource(request.getDefinitionSource());
        bccpRecord.setIsDeprecated((byte) ((request.isDeprecated()) ? 1 : 0));
        bccpRecord.setIsNillable((byte) ((request.isNillable()) ? 1 : 0));
        bccpRecord.setNamespaceId(ULong.valueOf(request.getNamespaceId()));
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.update(BCCP.PROPERTY_TERM, BCCP.DEN,
                BCCP.DEFAULT_VALUE, BCCP.FIXED_VALUE,
                BCCP.DEFINITION, BCCP.DEFINITION_SOURCE,
                BCCP.IS_DEPRECATED, BCCP.IS_NILLABLE,
                BCCP.NAMESPACE_ID,
                BCCP.LAST_UPDATED_BY, BCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.REVISION_ID);

        return new UpdateBccpPropertiesRepositoryResponse(bccpManifestRecord.getBccpManifestId().toBigInteger());
    }

    public UpdateBccpBdtRepositoryResponse updateBccpBdt(UpdateBccpBdtRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccpManifestId())
                ))
                .fetchOne();

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(bccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!bccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bccp record.
        ULong bdtManifestId = ULong.valueOf(request.getBdtManifestId());
        Record2<ULong, String> result = dslContext.select(DT.DT_ID, DT.DATA_TYPE_TERM)
                .from(DT)
                .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(bdtManifestId))
                .fetchOne();

        bccpRecord.setBdtId(result.get(DT.DT_ID));
        bccpRecord.setRepresentationTerm(result.get(DT.DATA_TYPE_TERM));
        bccpRecord.setDen(bccpRecord.getPropertyTerm() + ". " + bccpRecord.getRepresentationTerm());
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.update(BCCP.BDT_ID,
                BCCP.REPRESENTATION_TERM, BCCP.DEN,
                BCCP.LAST_UPDATED_BY, BCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        bccpManifestRecord.setBdtManifestId(bdtManifestId);
        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.BDT_MANIFEST_ID, BCCP_MANIFEST.REVISION_ID);

        return new UpdateBccpBdtRepositoryResponse(bccpManifestRecord.getBccpManifestId().toBigInteger());
    }

    public UpdateBccpStateRepositoryResponse updateBccpState(UpdateBccpStateRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccpManifestId())
                ))
                .fetchOne();

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(bccpRecord.getState());
        CcState nextState = request.getState();

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        if (!bccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bccp state.
        bccpRecord.setState(nextState.name());
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.update(BCCP.STATE,
                BCCP.LAST_UPDATED_BY, BCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == request.getState())
                ? RevisionAction.Restored : RevisionAction.Modified;
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccpRecord, bccpManifestRecord.getRevisionId(),
                        revisionAction,
                        userId, timestamp);

        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.REVISION_ID);

        return new UpdateBccpStateRepositoryResponse(bccpManifestRecord.getBccpManifestId().toBigInteger());
    }

    public DeleteBccpRepositoryResponse deleteBccp(DeleteBccpRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getBccpManifestId())
                ))
                .fetchOne();

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(bccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!bccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update bccp state.
        bccpRecord.setState(CcState.Deleted.name());
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.update(BCCP.STATE,
                BCCP.LAST_UPDATED_BY, BCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for deleted record.
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Deleted,
                        userId, timestamp);

        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.REVISION_ID);

        return new DeleteBccpRepositoryResponse(bccpManifestRecord.getBccpManifestId().toBigInteger());
    }
}
