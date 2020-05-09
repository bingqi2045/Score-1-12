package org.oagi.srt.repo.component.code_list;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.CodeListManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListRecord;
import org.oagi.srt.entity.jooq.tables.records.RevisionRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.oagi.srt.entity.jooq.Tables.AGENCY_ID_LIST_VALUE;
import static org.oagi.srt.entity.jooq.tables.CodeList.CODE_LIST;
import static org.oagi.srt.entity.jooq.tables.CodeListManifest.CODE_LIST_MANIFEST;

@Repository
public class CodeListCUDRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RevisionRepository revisionRepository;

    public CreateCodeListRepositoryResponse createCodeList(CreateCodeListRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        CodeListRecord codeList = new CodeListRecord();
        codeList.setGuid(SrtGuid.randomGuid());
        codeList.setName(request.getInitialName());
        codeList.setListId(SrtGuid.randomGuid());
        String initialAgencyIdValueName;
        if (user.isDeveloper()) {
            initialAgencyIdValueName = "OAGi (Open Applications Group, Incorporated)";
        } else {
            initialAgencyIdValueName = "Mutually defined";
        }
        codeList.setAgencyId(dslContext.select(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID)
                .from(AGENCY_ID_LIST_VALUE)
                .where(AGENCY_ID_LIST_VALUE.NAME.eq(initialAgencyIdValueName))
                .fetchOneInto(ULong.class));
        codeList.setVersionId("1");
        codeList.setExtensibleIndicator((byte) 1);
        codeList.setIsDeprecated((byte) 0);
        codeList.setState(CcState.WIP.name());
        codeList.setCreatedBy(userId);
        codeList.setLastUpdatedBy(userId);
        codeList.setOwnerUserId(userId);
        codeList.setCreationTimestamp(timestamp);
        codeList.setLastUpdateTimestamp(timestamp);
        codeList.setCodeListId(
                dslContext.insertInto(CODE_LIST)
                        .set(codeList)
                        .returning(CODE_LIST.CODE_LIST_ID).fetchOne().getCodeListId()
        );

        CodeListManifestRecord codeListManifest = new CodeListManifestRecord();
        codeListManifest.setCodeListId(codeList.getCodeListId());
        codeListManifest.setReleaseId(ULong.valueOf(request.getReleaseId()));

        RevisionRecord revisionRecord =
                revisionRepository.insertCodeListRevision(
                        codeList,
                        RevisionAction.Added,
                        userId, timestamp);
        codeListManifest.setRevisionId(revisionRecord.getRevisionId());

        codeListManifest.setCodeListManifestId(
                dslContext.insertInto(CODE_LIST_MANIFEST)
                        .set(codeListManifest)
                        .returning(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID).fetchOne().getCodeListManifestId()
        );

        return new CreateCodeListRepositoryResponse(codeListManifest.getCodeListManifestId().toBigInteger());
    }

    public UpdateCodeListPropertiesRepositoryResponse updateCodeListProperties(UpdateCodeListPropertiesRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(request.getCodeListManifestId())
                ))
                .fetchOne();

        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(codeListRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be modified.");
        }

        if (!codeListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        codeListRecord.setName(request.getCodeListName());
        codeListRecord.setAgencyId((request.getAgencyId() != null) ? ULong.valueOf(request.getAgencyId()) : null);
        codeListRecord.setVersionId(request.getVersionId());
        codeListRecord.setListId(request.getListId());
        codeListRecord.setDefinition(request.getDefinition());
        codeListRecord.setDefinitionSource(request.getDefinitionSource());
        codeListRecord.setExtensibleIndicator((byte) (request.isExtensible() ? 1 : 0));
        codeListRecord.setIsDeprecated((byte) (request.isDeprecated() ? 1 : 0));
        codeListRecord.setLastUpdatedBy(userId);
        codeListRecord.setLastUpdateTimestamp(timestamp);
        codeListRecord.update(CODE_LIST.NAME, CODE_LIST.AGENCY_ID,
                CODE_LIST.VERSION_ID, CODE_LIST.LIST_ID,
                CODE_LIST.DEFINITION, CODE_LIST.DEFINITION_SOURCE,
                CODE_LIST.EXTENSIBLE_INDICATOR, CODE_LIST.IS_DEPRECATED,
                CODE_LIST.LAST_UPDATED_BY, CODE_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionRecord revisionRecord =
                revisionRepository.insertCodeListRevision(
                        codeListRecord, codeListManifestRecord.getRevisionId(),
                        RevisionAction.Modified,
                        userId, timestamp);

        codeListManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        codeListManifestRecord.update(CODE_LIST_MANIFEST.REVISION_ID);

        return new UpdateCodeListPropertiesRepositoryResponse(codeListManifestRecord.getCodeListManifestId().toBigInteger());
    }

    public UpdateCodeListStateRepositoryResponse updateCodeListState(UpdateCodeListStateRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(request.getCodeListManifestId())
                ))
                .fetchOne();

        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                .fetchOne();

        CcState prevState = CcState.valueOf(codeListRecord.getState());
        CcState nextState = request.getState();

        if (!prevState.canMove(nextState)) {
            throw new IllegalArgumentException("The core component in '" + prevState + "' state cannot move to '" + nextState + "' state.");
        }

        if (!codeListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update codeList state.
        codeListRecord.setState(nextState.name());
        codeListRecord.setLastUpdatedBy(userId);
        codeListRecord.setLastUpdateTimestamp(timestamp);
        codeListRecord.update(CODE_LIST.STATE,
                CODE_LIST.LAST_UPDATED_BY, CODE_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for updated record.
        RevisionAction revisionAction = (CcState.Deleted == prevState && CcState.WIP == request.getState())
                ? RevisionAction.Restored : RevisionAction.Modified;
        RevisionRecord revisionRecord =
                revisionRepository.insertCodeListRevision(
                        codeListRecord, codeListManifestRecord.getRevisionId(),
                        revisionAction,
                        userId, timestamp);

        codeListManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        codeListManifestRecord.update(CODE_LIST_MANIFEST.REVISION_ID);

        return new UpdateCodeListStateRepositoryResponse(codeListManifestRecord.getCodeListManifestId().toBigInteger());
    }

    public DeleteCodeListRepositoryResponse deleteCodeList(DeleteCodeListRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(request.getCodeListManifestId())
                ))
                .fetchOne();

        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                .fetchOne();

        if (!CcState.WIP.equals(CcState.valueOf(codeListRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be deleted.");
        }

        if (!codeListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update codeList state.
        codeListRecord.setState(CcState.Deleted.name());
        codeListRecord.setLastUpdatedBy(userId);
        codeListRecord.setLastUpdateTimestamp(timestamp);
        codeListRecord.update(CODE_LIST.STATE,
                CODE_LIST.LAST_UPDATED_BY, CODE_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for deleted record.
        RevisionRecord revisionRecord =
                revisionRepository.insertCodeListRevision(
                        codeListRecord, codeListManifestRecord.getRevisionId(),
                        RevisionAction.Deleted,
                        userId, timestamp);

        codeListManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        codeListManifestRecord.update(CODE_LIST_MANIFEST.REVISION_ID);

        return new DeleteCodeListRepositoryResponse(codeListManifestRecord.getCodeListManifestId().toBigInteger());
    }
}
