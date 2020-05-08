package org.oagi.srt.repo.component.asccp;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpRecord;
import org.oagi.srt.entity.jooq.tables.records.RevisionRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Asccp.ASCCP;
import static org.oagi.srt.entity.jooq.tables.AsccpManifest.ASCCP_MANIFEST;
import static org.oagi.srt.entity.jooq.tables.BccpManifest.BCCP_MANIFEST;

@Repository
public class AsccpCUDRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

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
        asccp.setState(CcState.WIP.name());
        asccp.setReusableIndicator((byte) 1);
        asccp.setIsDeprecated((byte) 0);
        asccp.setIsNillable((byte) 0);
        asccp.setNamespaceId(null);
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

        if (!CcState.Published.equals(CcState.valueOf(prevAsccpRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
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
        // Developers only work for 'Working' branch whose manifest records are initially generated by system,
        // so it doesn't need to create new manifest.
        if (user.isDeveloper()) {
            asccpManifestRecord.setAsccpId(nextAsccpRecord.getAsccpId());
            asccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            asccpManifestRecord.update(ASCCP_MANIFEST.ASCCP_ID, ASCCP_MANIFEST.REVISION_ID);

            responseAsccpManifestId = asccpManifestRecord.getAsccpManifestId();
        } else {
            // creates new asccp manifest for revised record.
            AsccpManifestRecord nextAsccpManifestRecord = asccpManifestRecord.copy();
            nextAsccpManifestRecord.setAsccpId(nextAsccpRecord.getAsccpId());
            nextAsccpManifestRecord.setReleaseId(targetReleaseId);
            nextAsccpManifestRecord.setRevisionId(revisionRecord.getRevisionId());
            nextAsccpManifestRecord.setPrevAsccpManifestId(asccpManifestRecord.getAsccpManifestId());
            nextAsccpManifestRecord.setAsccpManifestId(
                    dslContext.insertInto(ASCCP_MANIFEST)
                            .set(nextAsccpManifestRecord)
                            .returning(ASCCP_MANIFEST.ASCCP_MANIFEST_ID).fetchOne().getAsccpManifestId()
            );

            asccpManifestRecord.setNextAsccpManifestId(nextAsccpManifestRecord.getAsccpManifestId());
            asccpManifestRecord.update(ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID);

            responseAsccpManifestId = nextAsccpManifestRecord.getAsccpManifestId();
        }

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
        asccpRecord.setPropertyTerm(request.getPropertyTerm());
        asccpRecord.setDen(asccpRecord.getPropertyTerm() + ". " + objectClassTerm(asccpRecord.getRoleOfAccId()));
        asccpRecord.setDefinition(request.getDefinition());
        asccpRecord.setDefinitionSource(request.getDefinitionSource());
        asccpRecord.setReusableIndicator((byte) ((request.isReusable()) ? 1 : 0));
        asccpRecord.setIsDeprecated((byte) ((request.isDeprecated()) ? 1 : 0));
        asccpRecord.setIsNillable((byte) ((request.isNillable()) ? 1 : 0));
        asccpRecord.setNamespaceId(request.getNamespaceId() == null ? null : ULong.valueOf(request.getNamespaceId()));
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.update(ASCCP.PROPERTY_TERM, ASCCP.DEN,
                ASCCP.DEFINITION, ASCCP.DEFINITION_SOURCE,
                ASCCP.REUSABLE_INDICATOR,
                ASCCP.IS_DEPRECATED, ASCCP.IS_NILLABLE,
                ASCCP.NAMESPACE_ID,
                ASCCP.LAST_UPDATED_BY, ASCCP.LAST_UPDATE_TIMESTAMP);

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
        CcState nextState = request.getState();

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        if (!asccpRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update asccp state.
        asccpRecord.setState(nextState.name());
        asccpRecord.setLastUpdatedBy(userId);
        asccpRecord.setLastUpdateTimestamp(timestamp);
        asccpRecord.update(ASCCP.STATE,
                ASCCP.LAST_UPDATED_BY, ASCCP.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == request.getState())
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
}
