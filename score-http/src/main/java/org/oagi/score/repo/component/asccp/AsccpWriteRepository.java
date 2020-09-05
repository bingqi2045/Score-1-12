package org.oagi.score.repo.component.asccp;

import com.google.gson.JsonObject;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.data.RevisionAction;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.oagi.score.repo.RevisionRepository;
import org.oagi.score.repo.domain.RevisionSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.compare;
import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Acc.ACC;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Asccp.ASCCP;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.AsccpManifest.ASCCP_MANIFEST;

@Repository
public class AsccpWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private RevisionSerializer serializer;

    private String objectClassTerm(ULong accId) {
        return dslContext.select(ACC.OBJECT_CLASS_TERM).from(ACC)
                .where(ACC.ACC_ID.eq(accId))
                .fetchOneInto(String.class);
    }

    public CreateAsccpRepositoryResponse createAsccp(CreateAsccpRepositoryRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(request.getUser()));
        LocalDateTime timestamp = request.getLocalDateTime();

        AccManifestRecord roleOfAccManifest = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(request.getRoleOfAccManifestId())))
                .fetchOne();

        AsccpRecord asccp = new AsccpRecord();
        asccp.setGuid(SrtGuid.randomGuid());
        asccp.setPropertyTerm(request.getInitialPropertyTerm());
        asccp.setRoleOfAccId(roleOfAccManifest.getAccId());
        asccp.setDen(asccp.getPropertyTerm() + ". " + objectClassTerm(asccp.getRoleOfAccId()));
        asccp.setState(request.getInitialState().name());
        asccp.setDefinition(request.getDefinition());
        asccp.setDefinitionSource(request.getDefinitionSoruce());
        asccp.setReusableIndicator((byte) (request.isReusable() ? 1 : 0));
        asccp.setIsDeprecated((byte) 0);
        asccp.setIsNillable((byte) 0);
        asccp.setType(request.getInitialType().name());
        if (request.getNamespaceId() != null) {
            asccp.setNamespaceId(ULong.valueOf(request.getNamespaceId()));
        }
        asccp.setCreatedBy(userId);
        asccp.setLastUpdatedBy(userId);
        asccp.setOwnerUserId(userId);
        asccp.setCreationTimestamp(timestamp);
        asccp.setLastUpdateTimestamp(timestamp);

        asccp.setAsccpId(
                dslContext.insertInto(ASCCP)
                        .set(asccp)
                        .returning(ASCCP.ASCCP_ID).fetchOne().getAsccpId()
        );

        AsccpManifestRecord asccpManifest = new AsccpManifestRecord();
        asccpManifest.setAsccpId(asccp.getAsccpId());
        asccpManifest.setRoleOfAccManifestId(roleOfAccManifest.getAccManifestId());
        asccpManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));

        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        asccp,
                        RevisionAction.Added,
                        userId, timestamp);
        asccpManifest.setRevisionId(revisionRecord.getRevisionId());

        asccpManifest.setAsccpManifestId(
                dslContext.insertInto(ASCCP_MANIFEST)
                        .set(asccpManifest)
                        .returning(ASCCP_MANIFEST.ASCCP_MANIFEST_ID).fetchOne().getAsccpManifestId()
        );

        return new CreateAsccpRepositoryResponse(asccpManifest.getAsccpManifestId().toBigInteger());
    }

    public ReviseAsccpRepositoryResponse reviseAsccp(ReviseAsccpRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord prevAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                .fetchOne();

        if (user.isDeveloper()) {
            if (!CcState.Published.equals(CcState.valueOf(prevAsccpRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
            }
        } else {
            if (!CcState.Production.equals(CcState.valueOf(prevAsccpRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Production' state can be revised.");
            }
        }

        ULong workingReleaseId = dslContext.select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);

        ULong targetReleaseId = asccpManifestRecord.getReleaseId();
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
                .where(APP_USER.APP_USER_ID.eq(prevAsccpRecord.getOwnerUserId()))
                .fetchOneInto(Boolean.class);

        if (user.isDeveloper() != ownerIsDeveloper) {
            throw new IllegalArgumentException("It only allows to revise the component for users in the same roles.");
        }

        // creates new asccp for revised record.
        AsccpRecord nextAsccpRecord = prevAsccpRecord.copy();
        nextAsccpRecord.setState(CcState.WIP.name());
        nextAsccpRecord.setCreatedBy(userId);
        nextAsccpRecord.setLastUpdatedBy(userId);
        nextAsccpRecord.setOwnerUserId(userId);
        nextAsccpRecord.setCreationTimestamp(timestamp);
        nextAsccpRecord.setLastUpdateTimestamp(timestamp);
        nextAsccpRecord.setPrevAsccpId(prevAsccpRecord.getAsccpId());
        nextAsccpRecord.setAsccpId(
                dslContext.insertInto(ASCCP)
                        .set(nextAsccpRecord)
                        .returning(ASCCP.ASCCP_ID).fetchOne().getAsccpId()
        );

        prevAsccpRecord.setNextAsccpId(nextAsccpRecord.getAsccpId());
        prevAsccpRecord.update(ASCCP.NEXT_ASCCP_ID);

        // creates new revision for revised record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        nextAsccpRecord, asccpManifestRecord.getRevisionId(),
                        RevisionAction.Revised,
                        userId, timestamp);

        ULong responseAsccpManifestId;

        asccpManifestRecord.setAsccpId(nextAsccpRecord.getAsccpId());
        asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.ASCCP_ID, ASCCP_MANIFEST.REVISION_ID);

        responseAsccpManifestId = asccpManifestRecord.getAsccpManifestId();

        // update `conflict` for ascc_manifests' to_asccp_manifest_id which indicates given asccp manifest.
        dslContext.update(ASCC_MANIFEST)
                .set(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID, responseAsccpManifestId)
                .set(ASCC_MANIFEST.CONFLICT, (byte) 1)
                .where(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.in(Arrays.asList(
                                asccpManifestRecord.getAsccpManifestId(),
                                asccpManifestRecord.getPrevAsccpManifestId()))
                ))
                .execute();

        return new ReviseAsccpRepositoryResponse(responseAsccpManifestId.toBigInteger());
    }

    public UpdateAsccpPropertiesRepositoryResponse updateAsccpProperties(UpdateAsccpPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(asccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!asccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update asccp record.
        UpdateSetFirstStep<AsccpRecord> firstStep = dslContext.update(ASCCP);
        UpdateSetMoreStep<AsccpRecord> moreStep = null;
        if (compare(asccpRecord.getPropertyTerm(), request.getPropertyTerm()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.PROPERTY_TERM, request.getPropertyTerm())
                    .set(ASCCP.DEN, request.getPropertyTerm() + ". " + objectClassTerm(asccpRecord.getRoleOfAccId()));
        }
        if (compare(asccpRecord.getDefinition(), request.getDefinition()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.DEFINITION, request.getDefinition());
        }
        if (compare(asccpRecord.getDefinitionSource(), request.getDefinitionSource()) != 0) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.DEFINITION_SOURCE, request.getDefinitionSource());
        }
        if ((asccpRecord.getReusableIndicator() == 1 ? true : false) != request.isReusable()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.REUSABLE_INDICATOR, (byte) ((request.isReusable()) ? 1 : 0));
        }
        if ((asccpRecord.getIsDeprecated() == 1 ? true : false) != request.isDeprecated()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.IS_DEPRECATED, (byte) ((request.isDeprecated()) ? 1 : 0));
        }
        if ((asccpRecord.getIsNillable() == 1 ? true : false) != request.isNillable()) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.IS_NILLABLE, (byte) ((request.isNillable()) ? 1 : 0));
        }
        if (request.getNamespaceId() == null || request.getNamespaceId().longValue() <= 0L) {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .setNull(ASCCP.NAMESPACE_ID);
        } else {
            moreStep = ((moreStep != null) ? moreStep : firstStep)
                    .set(ASCCP.NAMESPACE_ID, ULong.valueOf(request.getNamespaceId()));
        }

        if (moreStep != null) {
            moreStep.set(ASCCP.LAST_UPDATED_BY, userId)
                    .set(ASCCP.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(ASCCP.ASCCP_ID.eq(asccpRecord.getAsccpId()))
                    .execute();

            asccpRecord = dslContext.selectFrom(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                    .fetchOne();
        }

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        asccpRecord, asccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.REVISION_ID);

        return new UpdateAsccpPropertiesRepositoryResponse(asccpManifestRecord.getAsccpManifestId().toBigInteger());
    }

    public UpdateAsccpRoleOfAccRepositoryResponse updateAsccpBdt(UpdateAsccpRoleOfAccRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(asccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!asccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update asccp record.
        ULong roleOfAccManifestId = ULong.valueOf(request.getRoleOfAccManifestId());
        ULong roleOfAccId = dslContext.select(ACC_MANIFEST.ACC_ID)
                .from(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(roleOfAccManifestId))
                .fetchOneInto(ULong.class);

        asccpRecord.setRoleOfAccId(roleOfAccId);
        asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + objectClassTerm(asccpRecord.getRoleOfAccId()));
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.update(ASCCP.ROLE_OF_ACC_ID, ASCCP.DEN,
                ASCCP.LAST_UPDATED_BY, ASCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        asccpRecord, asccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        asccpManifestRecord.setRoleOfAccManifestId(roleOfAccManifestId);
        asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, ASCCP_MANIFEST.REVISION_ID);

        return new UpdateAsccpRoleOfAccRepositoryResponse(asccpManifestRecord.getAsccpManifestId().toBigInteger());
    }

    public UpdateAsccpStateRepositoryResponse updateAsccpState(UpdateAsccpStateRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(asccpRecord.getState());
        CcState nextState = request.getToState();

        if (prevState != request.getFromState()) {
            throw new IllegalArgumentException("Target core component is not in '" + request.getFromState() + "' state.");
        }

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        // Change owner of CC when it restored.
        if (prevState == CcState.Deleted && nextState == CcState.WIP) {
            asccpRecord.setOwnerUserId(userId);
        } else if (prevState != CcState.Deleted && !asccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update asccp state.
        asccpRecord.setState(nextState.name());
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.update(ASCCP.STATE,
                ASCCP.LAST_UPDATED_BY, ASCCP.LAST_UPDATE_TIMESTAMP, ASCCP.OWNER_USER_ID);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == nextState)
                ? RevisionAction.Restored : RevisionAction.Modified;
        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        asccpRecord, asccpManifestRecord.getRevisionId(),
                        revisionAction,
                        userId, timestamp);

        asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.REVISION_ID);

        return new UpdateAsccpStateRepositoryResponse(asccpManifestRecord.getAsccpManifestId().toBigInteger());
    }

    public DeleteAsccpRepositoryResponse deleteAsccp(DeleteAsccpRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(asccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!asccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update asccp state.
        asccpRecord.setState(CcState.Deleted.name());
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.update(ASCCP.STATE,
                ASCCP.LAST_UPDATED_BY, ASCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for deleted record.
        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        asccpRecord, asccpManifestRecord.getRevisionId(),
                        RevisionAction.Deleted,
                        userId, timestamp);

        asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.REVISION_ID);

        return new DeleteAsccpRepositoryResponse(asccpManifestRecord.getAsccpManifestId().toBigInteger());
    }

    public UpdateAsccpOwnerRepositoryResponse updateAsccpOwner(UpdateAsccpOwnerRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(
                        ULong.valueOf(request.getAsccpManifestId())
                ))
                .fetchOne();

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(asccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!asccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        asccpRecord.setOwnerUserId(ULong.valueOf(request.getOwnerId()));
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.update(ASCCP.OWNER_USER_ID, ASCCP.LAST_UPDATED_BY, ASCCP.LAST_UPDATE_TIMESTAMP);

        RevisionRecord revisionRecord =
                revisionRepository.insertAsccpRevision(
                        asccpRecord, asccpManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.REVISION_ID);

        return new UpdateAsccpOwnerRepositoryResponse(asccpManifestRecord.getAsccpManifestId().toBigInteger());
    }

    public DiscardRevisionAsccpRepositoryResponse discardRevisionAsccp(DiscardRevisionAsccpRepositoryRequest request) {
        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(request.getAsccpManifestId()))).fetchOne();

        if (asccpManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target ASCCP");
        }

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        if (asccpRecord.getPrevAsccpId() == null) {
            throw new IllegalArgumentException("Not found previous revision");
        }

        RevisionRecord cursorRevision = dslContext.selectFrom(REVISION)
                .where(REVISION.REVISION_ID.eq(asccpManifestRecord.getRevisionId())).fetchOne();

        UInteger revisionNum = cursorRevision.getRevisionNum();

        if (cursorRevision.getPrevRevisionId() == null) {
            throw new IllegalArgumentException("There is no change to be reset.");
        }

        List<ULong> deleteRevisionTargets = new ArrayList<>();

        while(cursorRevision.getPrevRevisionId() != null) {
            if(cursorRevision.getRevisionNum().compareTo(revisionNum) < 0) {
                break;
            }
            deleteRevisionTargets.add(cursorRevision.getRevisionId());
            cursorRevision = dslContext.selectFrom(REVISION)
                    .where(REVISION.REVISION_ID.eq(cursorRevision.getPrevRevisionId())).fetchOne();
        }

        // update ASCCP MANIFEST's asccp_id and revision_id
        asccpManifestRecord.setAsccpId(asccpRecord.getPrevAsccpId());
        asccpManifestRecord.setRevisionId(cursorRevision.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.ASCCP_ID, ASCCP_MANIFEST.REVISION_ID);

        // unlink revision
        cursorRevision.setNextRevisionId(null);
        cursorRevision.update(REVISION.NEXT_REVISION_ID);
        dslContext.update(REVISION)
                .setNull(REVISION.PREV_REVISION_ID)
                .setNull(REVISION.NEXT_REVISION_ID)
                .where(REVISION.REVISION_ID.in(deleteRevisionTargets))
                .execute();
        dslContext.deleteFrom(REVISION).where(REVISION.REVISION_ID.in(deleteRevisionTargets)).execute();

        // update ASCCs which using current ASCCP
        dslContext.update(ASCC)
                .set(ASCC.TO_ASCCP_ID, asccpRecord.getPrevAsccpId())
                .where(ASCC.TO_ASCCP_ID.eq(asccpRecord.getAsccpId()))
                .execute();

        AsccpRecord prevAsccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpRecord.getPrevAsccpId())).fetchOne();

        // unlink prev ASCCP
        prevAsccpRecord.setNextAsccpId(null);
        prevAsccpRecord.update(ASCCP.NEXT_ASCCP_ID);

        // delete current ASCCP
        asccpRecord.delete();

        return new DiscardRevisionAsccpRepositoryResponse(request.getAsccpManifestId());
    }

    public DiscardRevisionAsccpRepositoryResponse resetRevisionAsccp(DiscardRevisionAsccpRepositoryRequest request) {
        AsccpManifestRecord asccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(request.getAsccpManifestId()))).fetchOne();

        if (asccpManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target ASCCP");
        }

        AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId())).fetchOne();

        RevisionRecord cursorRevision = dslContext.selectFrom(REVISION)
                .where(REVISION.REVISION_ID.eq(asccpManifestRecord.getRevisionId())).fetchOne();

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

        ULong roleOfAccId = serializer.getSnapshotId(snapshot.get("roleOfAccId"));
        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST).where(and(
                ACC_MANIFEST.ACC_ID.eq(roleOfAccId),
                ACC_MANIFEST.RELEASE_ID.eq(asccpManifestRecord.getReleaseId())
        )).fetchOne();

        if (accManifestRecord == null) {
            throw new IllegalArgumentException("Not found role of ACC.");
        }

        AccRecord accRecord = dslContext.selectFrom(ACC).where(ACC.ACC_ID.eq(accManifestRecord.getAccId())).fetchOne();

        asccpManifestRecord.setRoleOfAccManifestId(accManifestRecord.getAccManifestId());
        asccpManifestRecord.setRevisionId(cursorRevision.getRevisionId());
        asccpManifestRecord.update(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID, ASCCP_MANIFEST.REVISION_ID);

        asccpRecord.setRoleOfAccId(accManifestRecord.getAccId());
        asccpRecord.setPropertyTerm(serializer.getSnapshotString(snapshot.get("propertyTerm")));
        asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + accRecord.getObjectClassTerm());
        asccpRecord.setDefinition(serializer.getSnapshotString(snapshot.get("definition")));
        asccpRecord.setDefinitionSource(serializer.getSnapshotString(snapshot.get("definitionSource")));
        asccpRecord.setNamespaceId(serializer.getSnapshotId(snapshot.get("namespaceId")));
        asccpRecord.setIsDeprecated(serializer.getSnapshotByte(snapshot.get("deprecated")));
        asccpRecord.setIsNillable(serializer.getSnapshotByte(snapshot.get("nillable")));
        asccpRecord.setReusableIndicator(serializer.getSnapshotByte(snapshot.get("reusable")));
        asccpRecord.update();

        cursorRevision.setNextRevisionId(null);
        cursorRevision.update(REVISION.NEXT_REVISION_ID);
        dslContext.update(REVISION)
                .setNull(REVISION.PREV_REVISION_ID)
                .setNull(REVISION.NEXT_REVISION_ID)
                .where(REVISION.REVISION_ID.in(deleteRevisionTargets))
                .execute();
        dslContext.deleteFrom(REVISION).where(REVISION.REVISION_ID.in(deleteRevisionTargets)).execute();

        return new DiscardRevisionAsccpRepositoryResponse(request.getAsccpManifestId());
    }
}
