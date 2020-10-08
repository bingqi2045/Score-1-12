package org.oagi.score.repo.component.bccp;

import com.google.gson.JsonObject;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.data.RevisionAction;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.RevisionRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.component.bcc.BccWriteRepository;
import org.oagi.score.repo.component.bcc.UpdateBccPropertiesRepositoryRequest;
import org.oagi.score.repo.domain.RevisionSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Bccp.BCCP;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class BccpWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BccWriteRepository bccWriteRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private RevisionSerializer serializer;

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
        bccp.setGuid(ScoreGuid.randomGuid());
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

        if (user.isDeveloper()) {
            if (!CcState.Published.equals(CcState.valueOf(prevBccpRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
            }
        } else {
            if (!CcState.Production.equals(CcState.valueOf(prevBccpRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Production' state can be revised.");
            }
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
        bccpManifestRecord.setBccpId(nextBccpRecord.getBccpId());
        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.BCCP_ID, BCCP_MANIFEST.REVISION_ID);

        responseBccpManifestId = bccpManifestRecord.getBccpManifestId();

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
        UpdateSetFirstStep<BccpRecord> firstStep = dslContext.update(BCCP);
        UpdateSetMoreStep<BccpRecord> moreStep = null;
        boolean propertyTermChanged = false;
        if (compare(bccpRecord.getPropertyTerm(), request.getPropertyTerm()) != 0) {
            propertyTermChanged = true;
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(BCCP.PROPERTY_TERM, request.getPropertyTerm())
                    .set(BCCP.DEN, request.getPropertyTerm() + ". " + bccpRecord.getRepresentationTerm());
        }
        if (StringUtils.isEmpty(request.getDefaultValue()) && StringUtils.isEmpty(request.getFixedValue())) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .setNull(BCCP.DEFAULT_VALUE)
                    .setNull(BCCP.FIXED_VALUE);
        } else {
            if (compare(bccpRecord.getDefaultValue(), request.getDefaultValue()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .set(BCCP.DEFAULT_VALUE, request.getDefaultValue())
                        .setNull(BCCP.FIXED_VALUE);
            } else if (compare(bccpRecord.getFixedValue(), request.getFixedValue()) != 0) {
                moreStep = ((moreStep != null) ? moreStep : firstStep)
                        .setNull(BCCP.DEFAULT_VALUE)
                        .set(BCCP.FIXED_VALUE, request.getFixedValue());
            }
        }
        if (compare(bccpRecord.getDefinition(), request.getDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(BCCP.DEFINITION, request.getDefinition());
        }
        if (compare(bccpRecord.getDefinitionSource(), request.getDefinitionSource()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(BCCP.DEFINITION_SOURCE, request.getDefinitionSource());
        }
        if ((bccpRecord.getIsDeprecated() == 1 ? true : false) != request.isDeprecated()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(BCCP.IS_DEPRECATED, (byte) ((request.isDeprecated()) ? 1 : 0));
        }
        if ((bccpRecord.getIsNillable() == 1 ? true : false) != request.isNillable()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(BCCP.IS_NILLABLE, (byte) ((request.isNillable()) ? 1 : 0));
        }
        if (request.getNamespaceId() == null || request.getNamespaceId().longValue() <= 0L) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .setNull(BCCP.NAMESPACE_ID);
        } else {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(BCCP.NAMESPACE_ID, ULong.valueOf(request.getNamespaceId()));
        }

        if (moreStep != null) {
            moreStep.set(BCCP.LAST_UPDATED_BY, userId)
                    .set(BCCP.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(BCCP.BCCP_ID.eq(bccpRecord.getBccpId()))
                    .execute();

            bccpRecord = dslContext.selectFrom(BCCP)
                    .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                    .fetchOne();
        }

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.REVISION_ID);

        if (propertyTermChanged) {
            for (ULong bccManifestId : dslContext.select(BCC_MANIFEST.BCC_MANIFEST_ID)
                    .from(BCC_MANIFEST)
                    .where(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(bccpManifestRecord.getBccpManifestId()))
                    .fetchInto(ULong.class)) {

                UpdateBccPropertiesRepositoryRequest updateBccPropertiesRepositoryRequest =
                        new UpdateBccPropertiesRepositoryRequest(request.getUser(), request.getLocalDateTime(),
                                bccManifestId.toBigInteger());
                updateBccPropertiesRepositoryRequest.setPropagation(true);
                bccWriteRepository.updateBccProperties(updateBccPropertiesRepositoryRequest);
            }
        }

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
        CcState nextState = request.getToState();

        if (prevState != request.getFromState()) {
            throw new IllegalArgumentException("Target core component is not in '" + request.getFromState() + "' state.");
        }

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        // Change owner of CC when it restored.
        if (prevState == CcState.Deleted && nextState == CcState.WIP) {
            bccpRecord.setOwnerUserId(userId);
        } else if (prevState != CcState.Deleted && !bccpRecord.getOwnerUserId().equals(userId)
                && !prevState.canForceMove(request.getToState())) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        } else if (bccpRecord.getNamespaceId() == null) {
            throw new IllegalArgumentException("'" + bccpRecord.getDen() + "' dose not have NamespaceId.");
        }

        // update bccp state.
        bccpRecord.setState(nextState.name());
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.update(BCCP.STATE,
                BCCP.LAST_UPDATED_BY, BCCP.LAST_UPDATE_TIMESTAMP, BCCP.OWNER_USER_ID);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == nextState)
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

    public UpdateBccpOwnerRepositoryResponse updateBccpOwner(UpdateBccpOwnerRepositoryRequest request) {
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

        bccpRecord.setOwnerUserId(ULong.valueOf(request.getOwnerId()));
        bccpRecord.setLastUpdatedBy(userId);
        bccpRecord.setLastUpdateTimestamp(timestamp);
        bccpRecord.update(BCCP.OWNER_USER_ID, BCCP.LAST_UPDATED_BY, BCCP.LAST_UPDATE_TIMESTAMP);

        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        bccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.REVISION_ID);

        return new UpdateBccpOwnerRepositoryResponse(bccpManifestRecord.getBccpManifestId().toBigInteger());
    }

    public CancelRevisionBccpRepositoryResponse cancelRevisionBccp(CancelRevisionBccpRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(request.getBccpManifestId()))).fetchOne();

        if (bccpManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target BCCP");
        }

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        if (bccpRecord.getPrevBccpId() == null) {
            throw new IllegalArgumentException("Not found previous revision");
        }

        BccpRecord prevBccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpRecord.getPrevBccpId())).fetchOne();

        // creates new revision for canceled record.
        RevisionRecord revisionRecord =
                revisionRepository.insertBccpRevision(
                        prevBccpRecord, bccpManifestRecord.getRevisionId(),
                        RevisionAction.Canceled,
                        userId, timestamp);

        // update BCCP MANIFEST's bccp_id and revision_id
        if (prevBccpRecord.getBdtId() != null) {
            String prevBdtGuid = dslContext.select(DT.GUID)
                    .from(DT).where(DT.DT_ID.eq(prevBccpRecord.getBdtId())).fetchOneInto(String.class);
            DtManifestRecord bdtManifest = dslContext.select(DT_MANIFEST.fields()).from(DT)
                    .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                    .where(and(DT_MANIFEST.RELEASE_ID.eq(bccpManifestRecord.getReleaseId()),
                            DT.GUID.eq(prevBdtGuid))).fetchOneInto(DtManifestRecord.class);
            bccpManifestRecord.setBdtManifestId(bdtManifest.getDtManifestId());
        }
        bccpManifestRecord.setBccpId(bccpRecord.getPrevBccpId());
        bccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.BCCP_ID, BCCP_MANIFEST.REVISION_ID, BCCP_MANIFEST.BDT_MANIFEST_ID);

        // update BCCs which using current BCCP
        dslContext.update(BCC)
                .set(BCC.TO_BCCP_ID, bccpRecord.getPrevBccpId())
                .where(BCC.TO_BCCP_ID.eq(bccpRecord.getBccpId()))
                .execute();

        // unlink prev BCCP
        prevBccpRecord.setNextBccpId(null);
        prevBccpRecord.update(BCCP.NEXT_BCCP_ID);

        // delete current BCCP
        bccpRecord.delete();

        return new CancelRevisionBccpRepositoryResponse(request.getBccpManifestId());
    }

    public CancelRevisionBccpRepositoryResponse resetRevisionBccp(CancelRevisionBccpRepositoryRequest request) {
        BccpManifestRecord bccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(request.getBccpManifestId()))).fetchOne();

        if (bccpManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target BCCP");
        }

        BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId())).fetchOne();

        RevisionRecord cursorRevision = dslContext.selectFrom(REVISION)
                .where(REVISION.REVISION_ID.eq(bccpManifestRecord.getRevisionId())).fetchOne();

        UInteger revisionNum = cursorRevision.getRevisionNum();

        if (cursorRevision.getPrevRevisionId() == null) {
            throw new IllegalArgumentException("There is no change to be reset.");
        }

        List<ULong> deleteRevisionTargets = new ArrayList<>();

        while(cursorRevision.getPrevRevisionId() != null) {
            if (!cursorRevision.getRevisionNum().equals(revisionNum)) {
                throw new IllegalArgumentException("Can not found reset point");
            }
            if(cursorRevision.getRevisionTrackingNum().equals(UInteger.valueOf(1))) {
                break;
            }
            deleteRevisionTargets.add(cursorRevision.getRevisionId());
            cursorRevision = dslContext.selectFrom(REVISION)
                    .where(REVISION.REVISION_ID.eq(cursorRevision.getPrevRevisionId())).fetchOne();
        }

        JsonObject snapshot = serializer.deserialize(cursorRevision.getSnapshot().toString());

        ULong bdtId = serializer.getSnapshotId(snapshot.get("bdtId"));
        DtManifestRecord bdtManifestRecord = dslContext.selectFrom(DT_MANIFEST).where(and(
                DT_MANIFEST.DT_ID.eq(bdtId),
                DT_MANIFEST.RELEASE_ID.eq(bccpManifestRecord.getReleaseId())
        )).fetchOne();

        if (bdtManifestRecord == null) {
            throw new IllegalArgumentException("Not found based BDT.");
        }

        bccpManifestRecord.setBdtManifestId(bdtManifestRecord.getDtManifestId());
        bccpManifestRecord.setRevisionId(cursorRevision.getRevisionId());
        bccpManifestRecord.update(BCCP_MANIFEST.BDT_MANIFEST_ID, BCCP_MANIFEST.REVISION_ID);

        bccpRecord.setBdtId(bdtManifestRecord.getDtId());
        bccpRecord.setPropertyTerm(serializer.getSnapshotString(snapshot.get("propertyTerm")));
        bccpRecord.setRepresentationTerm(serializer.getSnapshotString(snapshot.get("representationTerm")));
        bccpRecord.setDen(bccpRecord.getPropertyTerm() + ". " + bccpRecord.getRepresentationTerm());
        bccpRecord.setDefinition(serializer.getSnapshotString(snapshot.get("definition")));
        bccpRecord.setDefinitionSource(serializer.getSnapshotString(snapshot.get("definitionSource")));
        bccpRecord.setNamespaceId(serializer.getSnapshotId(snapshot.get("namespaceId")));
        bccpRecord.setIsDeprecated(serializer.getSnapshotByte(snapshot.get("deprecated")));
        bccpRecord.setIsNillable(serializer.getSnapshotByte(snapshot.get("nillable")));
        bccpRecord.setDefaultValue(serializer.getSnapshotString(snapshot.get("defaultValue")));
        bccpRecord.setFixedValue(serializer.getSnapshotString(snapshot.get("fixedValue")));
        bccpRecord.update();

        cursorRevision.setNextRevisionId(null);
        cursorRevision.update(REVISION.NEXT_REVISION_ID);
        dslContext.update(REVISION)
                .setNull(REVISION.PREV_REVISION_ID)
                .setNull(REVISION.NEXT_REVISION_ID)
                .where(REVISION.REVISION_ID.in(deleteRevisionTargets))
                .execute();
        dslContext.deleteFrom(REVISION).where(REVISION.REVISION_ID.in(deleteRevisionTargets)).execute();

        return new CancelRevisionBccpRepositoryResponse(request.getBccpManifestId());
    }
}