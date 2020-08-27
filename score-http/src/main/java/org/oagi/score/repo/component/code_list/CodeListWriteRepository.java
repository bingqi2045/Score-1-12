package org.oagi.score.repo.component.code_list;

import org.jooq.DSLContext;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.data.RevisionAction;
import org.oagi.score.repo.entity.jooq.tables.records.*;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.oagi.score.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.entity.jooq.Tables.*;
import static org.oagi.score.repo.entity.jooq.tables.CodeList.CODE_LIST;
import static org.oagi.score.repo.entity.jooq.tables.CodeListManifest.CODE_LIST_MANIFEST;

@Repository
public class CodeListWriteRepository {

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
        codeList.setListId(SrtGuid.randomGuid());
        codeList.setState(CcState.WIP.name());
        codeList.setCreatedBy(userId);
        codeList.setLastUpdatedBy(userId);
        codeList.setOwnerUserId(userId);
        codeList.setCreationTimestamp(timestamp);
        codeList.setLastUpdateTimestamp(timestamp);

        List<CodeListValueManifestRecord> basedCodeListValueManifestList = null;
        CodeListManifestRecord basedCodeListManifestRecord = null;

        if (request.getbasedCodeListManifestId() != null) {
            basedCodeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                    .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID
                            .eq(ULong.valueOf(request.getbasedCodeListManifestId())))
                    .fetchOne();
            if (basedCodeListManifestRecord == null) {
                throw new IllegalArgumentException("Can not found Based Code List");
            }

            CodeListRecord basedCodeListRecord = dslContext.selectFrom(CODE_LIST)
                    .where(CODE_LIST.CODE_LIST_ID
                            .eq(basedCodeListManifestRecord.getCodeListId()))
                    .fetchOne();

            codeList.setName(basedCodeListRecord.getName());
            codeList.setAgencyId(basedCodeListRecord.getAgencyId());
            codeList.setVersionId(basedCodeListRecord.getVersionId());
            codeList.setExtensibleIndicator(basedCodeListRecord.getExtensibleIndicator());
            codeList.setIsDeprecated(basedCodeListRecord.getIsDeprecated());

            basedCodeListValueManifestList =
                    dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                    .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID
                            .eq(basedCodeListManifestRecord.getCodeListManifestId()))
                    .fetch();
        } else {
            codeList.setName(request.getInitialName());
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
        }

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
        if (basedCodeListManifestRecord != null) {
            codeListManifest.setBasedCodeListManifestId(basedCodeListManifestRecord.getCodeListManifestId());
        }

        codeListManifest.setCodeListManifestId(
                dslContext.insertInto(CODE_LIST_MANIFEST)
                        .set(codeListManifest)
                        .returning(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID).fetchOne().getCodeListManifestId());

        if (basedCodeListValueManifestList != null) {
            for (CodeListValueManifestRecord basedCodeListValueManifest : basedCodeListValueManifestList) {
                CodeListValueRecord basedCodeListValue = dslContext.selectFrom(CODE_LIST_VALUE)
                        .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID
                                .eq(basedCodeListValueManifest.getCodeListValueId()))
                        .fetchOne();

                CodeListValueRecord codeListValueRecord = basedCodeListValue.copy();
                codeListValueRecord.setCodeListId(codeList.getCodeListId());
                codeListValueRecord.setGuid(SrtGuid.randomGuid());
                codeListValueRecord.setCreatedBy(userId);
                codeListValueRecord.setLastUpdatedBy(userId);
                codeListValueRecord.setOwnerUserId(userId);
                codeListValueRecord.setCreationTimestamp(timestamp);
                codeListValueRecord.setLastUpdateTimestamp(timestamp);
                codeListValueRecord.setPrevCodeListValueId(null);
                codeListValueRecord.setNextCodeListValueId(null);

                codeListValueRecord.setCodeListValueId(
                        dslContext.insertInto(CODE_LIST_VALUE)
                                .set(codeListValueRecord)
                                .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID).fetchOne().getCodeListValueId()
                );

                CodeListValueManifestRecord codeListValueManifestRecord = basedCodeListValueManifest.copy();
                codeListValueManifestRecord.setReleaseId(ULong.valueOf(request.getReleaseId()));
                codeListValueManifestRecord.setCodeListValueId(codeListValueRecord.getCodeListValueId());
                codeListValueManifestRecord.setCodeListManifestId(codeListManifest.getCodeListManifestId());
                codeListValueManifestRecord.setPrevCodeListValueManifestId(null);
                codeListValueManifestRecord.setNextCodeListValueManifestId(null);

                dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                        .set(codeListValueManifestRecord)
                        .execute();
            }
        }

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

    public ModifyCodeListValuesRepositoryResponse modifyCodeListValues(ModifyCodeListValuesRepositoryRequest request) {
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

        List<CodeListValueManifestRecord> codeListValueManifestRecordList =
                dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                        .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(
                                ULong.valueOf(request.getCodeListManifestId())))
                        .fetch();

        List<CodeListValueRecord> codeListValueRecordList =
                dslContext.selectFrom(CODE_LIST_VALUE)
                        .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.in(
                                codeListValueManifestRecordList.stream()
                                        .map(e -> e.getCodeListValueId()).collect(Collectors.toList()))
                        )
                        .fetch();

        // add
        addCodeListValues(user, userId, timestamp,
                codeListManifestRecord, codeListRecord,
                request, codeListValueManifestRecordList, codeListValueRecordList);

        // update
        updateCodeListValues(user, userId, timestamp,
                codeListManifestRecord, codeListRecord,
                request, codeListValueManifestRecordList, codeListValueRecordList);

        // delete
        deleteCodeListValues(user, userId, timestamp,
                codeListManifestRecord, codeListRecord,
                request, codeListValueManifestRecordList, codeListValueRecordList);

        return new ModifyCodeListValuesRepositoryResponse();
    }

    private void addCodeListValues(
            AppUser user, ULong userId, LocalDateTime timestamp,
            CodeListManifestRecord codeListManifestRecord, CodeListRecord codeListRecord,
            ModifyCodeListValuesRepositoryRequest request,
            List<CodeListValueManifestRecord> codeListValueManifestRecordList,
            List<CodeListValueRecord> codeListValueRecordList
    ) {
        Map<String, CodeListValueRecord> codeListValueRecordMapByName =
                codeListValueRecordList.stream()
                        .collect(Collectors.toMap(CodeListValueRecord::getName, Function.identity()));

        for (ModifyCodeListValuesRepositoryRequest.CodeListValue codeListValue : request.getCodeListValueList()) {
            if (codeListValueRecordMapByName.containsKey(codeListValue.getName())) {
                continue;
            }

            CodeListValueRecord codeListValueRecord = new CodeListValueRecord();

            codeListValueRecord.setCodeListId(codeListRecord.getCodeListId());
            codeListValueRecord.setGuid(SrtGuid.randomGuid());
            codeListValueRecord.setName(codeListValue.getName());
            codeListValueRecord.setValue(codeListValue.getValue());
            codeListValueRecord.setDefinition(codeListValue.getDefinition());
            codeListValueRecord.setDefinitionSource(codeListValue.getDefinitionSource());
            codeListValueRecord.setLockedIndicator((byte) (codeListValue.isLocked() ? 1 : 0));
            codeListValueRecord.setUsedIndicator((byte) (codeListValue.isUsed() ? 1 : 0));
            codeListValueRecord.setExtensionIndicator((byte) (codeListValue.isExtension() ? 1 : 0));
            codeListValueRecord.setCreatedBy(userId);
            codeListValueRecord.setOwnerUserId(userId);
            codeListValueRecord.setLastUpdatedBy(userId);
            codeListValueRecord.setCreationTimestamp(timestamp);
            codeListValueRecord.setLastUpdateTimestamp(timestamp);
            codeListValueRecord.setIsDeprecated((byte) (0));

            codeListValueRecord.setCodeListValueId(
                    dslContext.insertInto(CODE_LIST_VALUE)
                            .set(codeListValueRecord)
                            .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID)
                            .fetchOne().getCodeListValueId()
            );

            CodeListValueManifestRecord codeListValueManifestRecord = new CodeListValueManifestRecord();

            codeListValueManifestRecord.setReleaseId(codeListManifestRecord.getReleaseId());
            codeListValueManifestRecord.setCodeListValueId(codeListValueRecord.getCodeListValueId());
            codeListValueManifestRecord.setCodeListManifestId(codeListManifestRecord.getCodeListManifestId());

            codeListValueManifestRecord.setCodeListValueManifestId(
                    dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                            .set(codeListValueManifestRecord)
                            .returning(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID)
                            .fetchOne().getCodeListValueManifestId()
            );
        }
    }

    private void updateCodeListValues(
            AppUser user, ULong userId, LocalDateTime timestamp,
            CodeListManifestRecord codeListManifestRecord, CodeListRecord codeListRecord,
            ModifyCodeListValuesRepositoryRequest request,
            List<CodeListValueManifestRecord> codeListValueManifestRecordList,
            List<CodeListValueRecord> codeListValueRecordList
    ) {
        Map<String, CodeListValueRecord> codeListValueRecordMapByName =
                codeListValueRecordList.stream()
                        .collect(Collectors.toMap(CodeListValueRecord::getName, Function.identity()));

        for (ModifyCodeListValuesRepositoryRequest.CodeListValue codeListValue : request.getCodeListValueList()) {
            if (!codeListValueRecordMapByName.containsKey(codeListValue.getName())) {
                continue;
            }

            CodeListValueRecord codeListValueRecord = codeListValueRecordMapByName.get(codeListValue.getName());

            codeListValueRecord.setValue(codeListValue.getValue());
            codeListValueRecord.setDefinition(codeListValue.getDefinition());
            codeListValueRecord.setDefinitionSource(codeListValue.getDefinitionSource());
            codeListValueRecord.setLockedIndicator((byte) (codeListValue.isLocked() ? 1 : 0));
            codeListValueRecord.setUsedIndicator((byte) (codeListValue.isUsed() ? 1 : 0));
            codeListValueRecord.setExtensionIndicator((byte) (codeListValue.isExtension() ? 1 : 0));
            codeListValueRecord.setIsDeprecated((byte) (codeListValue.isDeprecated() ? 1 : 0));
            codeListValueRecord.setLastUpdatedBy(userId);
            codeListValueRecord.setLastUpdateTimestamp(timestamp);

            codeListValueRecord.update(
                    CODE_LIST_VALUE.VALUE,
                    CODE_LIST_VALUE.DEFINITION, CODE_LIST_VALUE.DEFINITION_SOURCE,
                    CODE_LIST_VALUE.LOCKED_INDICATOR, CODE_LIST_VALUE.USED_INDICATOR,
                    CODE_LIST_VALUE.EXTENSION_INDICATOR, CODE_LIST_VALUE.IS_DEPRECATED,
                    CODE_LIST_VALUE.LAST_UPDATED_BY, CODE_LIST_VALUE.LAST_UPDATE_TIMESTAMP);
        }
    }

    private void deleteCodeListValues(
            AppUser user, ULong userId, LocalDateTime timestamp,
            CodeListManifestRecord codeListManifestRecord, CodeListRecord codeListRecord,
            ModifyCodeListValuesRepositoryRequest request,
            List<CodeListValueManifestRecord> codeListValueManifestRecordList,
            List<CodeListValueRecord> codeListValueRecordList
    ) {
        Map<String, CodeListValueRecord> codeListValueRecordMapByName =
                codeListValueRecordList.stream()
                        .collect(Collectors.toMap(CodeListValueRecord::getName, Function.identity()));

        for (ModifyCodeListValuesRepositoryRequest.CodeListValue codeListValue : request.getCodeListValueList()) {
            codeListValueRecordMapByName.remove(codeListValue.getName());
        }

        Map<ULong, CodeListValueManifestRecord> codeListValueManifestRecordMapById =
                codeListValueManifestRecordList.stream()
                        .collect(Collectors.toMap(CodeListValueManifestRecord::getCodeListValueId, Function.identity()));

        for (CodeListValueRecord codeListValueRecord : codeListValueRecordMapByName.values()) {
            codeListValueManifestRecordMapById.get(
                    codeListValueRecord.getCodeListValueId()
            ).delete();

            codeListValueRecord.delete();
        }
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

    public RestoreCodeListRepositoryResponse restoreCodeList(RestoreCodeListRepositoryRequest request) {
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

        if (!CcState.Deleted.equals(CcState.valueOf(codeListRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'Deleted' state can be deleted.");
        }

        if (!codeListRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        // update codeList state.
        codeListRecord.setState(CcState.WIP.name());
        codeListRecord.setLastUpdatedBy(userId);
        codeListRecord.setLastUpdateTimestamp(timestamp);
        codeListRecord.update(CODE_LIST.STATE,
                CODE_LIST.LAST_UPDATED_BY, CODE_LIST.LAST_UPDATE_TIMESTAMP);

        // creates new revision for deleted record.
        RevisionRecord revisionRecord =
                revisionRepository.insertCodeListRevision(
                        codeListRecord, codeListManifestRecord.getRevisionId(),
                        RevisionAction.Restored,
                        userId, timestamp);

        codeListManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        codeListManifestRecord.update(CODE_LIST_MANIFEST.REVISION_ID);

        return new RestoreCodeListRepositoryResponse(codeListManifestRecord.getCodeListManifestId().toBigInteger());
    }

    public ReviseCodeListRepositoryResponse reviseCodeList(ReviseCodeListRepositoryRequest request) {
        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());
        LocalDateTime timestamp = request.getLocalDateTime();

        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(
                        ULong.valueOf(request.getCodeListManifestId())))
                .fetchOne();

        CodeListRecord prevCodeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(
                        codeListManifestRecord.getCodeListId()))
                .fetchOne();

        if (user.isDeveloper()) {
            if (!CcState.Published.equals(CcState.valueOf(prevCodeListRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Published' state can be revised.");
            }
        } else {
            if (!CcState.Production.equals(CcState.valueOf(prevCodeListRecord.getState()))) {
                throw new IllegalArgumentException("Only the core component in 'Production' state can be revised.");
            }
        }

        ULong workingReleaseId = dslContext.select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq("Working"))
                .fetchOneInto(ULong.class);

        ULong targetReleaseId = codeListManifestRecord.getReleaseId();
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
                .where(APP_USER.APP_USER_ID.eq(prevCodeListRecord.getOwnerUserId()))
                .fetchOneInto(Boolean.class);

        if (user.isDeveloper() != ownerIsDeveloper) {
            throw new IllegalArgumentException("It only allows to revise the component for users in the same roles.");
        }

        CodeListRecord nextCodeListRecord = prevCodeListRecord.copy();
        nextCodeListRecord.setState(CcState.WIP.name());
        nextCodeListRecord.setVersionId(nextCodeListRecord.getVersionId() + "_New");
        nextCodeListRecord.setCreatedBy(userId);
        nextCodeListRecord.setLastUpdatedBy(userId);
        nextCodeListRecord.setOwnerUserId(userId);
        nextCodeListRecord.setCreationTimestamp(timestamp);
        nextCodeListRecord.setLastUpdateTimestamp(timestamp);
        nextCodeListRecord.setPrevCodeListId(prevCodeListRecord.getCodeListId());
        nextCodeListRecord.setCodeListId(
                dslContext.insertInto(CODE_LIST)
                        .set(nextCodeListRecord)
                        .returning(CODE_LIST.CODE_LIST_ID).fetchOne().getCodeListId()
        );

        prevCodeListRecord.setNextCodeListId(nextCodeListRecord.getCodeListId());
        prevCodeListRecord.update(CODE_LIST.NEXT_CODE_LIST_ID);

        createNewCodeListValueForRevisedRecord(user, codeListManifestRecord, nextCodeListRecord, targetReleaseId, timestamp);

        // creates new revision for revised record.
        RevisionRecord revisionRecord =
                revisionRepository.insertCodeListRevision(
                        nextCodeListRecord, codeListManifestRecord.getRevisionId(),
                        RevisionAction.Revised,
                        userId, timestamp);

        ULong responseCodeListManifestId;
        codeListManifestRecord.setCodeListId(nextCodeListRecord.getCodeListId());
        codeListManifestRecord.setRevisionId(revisionRecord.getRevisionId());
        codeListManifestRecord.update(CODE_LIST_MANIFEST.CODE_LIST_ID, CODE_LIST_MANIFEST.REVISION_ID);

        responseCodeListManifestId = codeListManifestRecord.getCodeListManifestId();

        return new ReviseCodeListRepositoryResponse(responseCodeListManifestId.toBigInteger());
    }

    public DiscardRevisionCodeListRepositoryResponse discardRevisionCodeList(DiscardRevisionCodeListRepositoryRequest request) {
        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(request.getCodeListManifestId()))).fetchOne();

        if (codeListManifestRecord == null) {
            throw new IllegalArgumentException("Not found a target Code List");
        }

        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId())).fetchOne();

        if (codeListRecord.getPrevCodeListId() == null) {
            throw new IllegalArgumentException("Not found previous revision");
        }

        RevisionRecord cursorRevision = dslContext.selectFrom(REVISION)
                .where(REVISION.REVISION_ID.eq(codeListManifestRecord.getRevisionId())).fetchOne();

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

        // update BIE CODE LIST IDs
        dslContext.update(BBIE)
                .set(BBIE.CODE_LIST_ID, codeListRecord.getPrevCodeListId())
                .where(BBIE.CODE_LIST_ID.eq(codeListRecord.getCodeListId()))
                .execute();

        dslContext.update(BBIE_SC)
                .set(BBIE_SC.CODE_LIST_ID, codeListRecord.getPrevCodeListId())
                .where(BBIE_SC.CODE_LIST_ID.eq(codeListRecord.getCodeListId()))
                .execute();

        // update CODE LIST MANIFEST's codeList_id and revision_id
        codeListManifestRecord.setCodeListId(codeListRecord.getPrevCodeListId());
        codeListManifestRecord.setRevisionId(cursorRevision.getRevisionId());
        codeListManifestRecord.update(CODE_LIST_MANIFEST.CODE_LIST_ID, CODE_LIST_MANIFEST.REVISION_ID);

        // unlink revision
        cursorRevision.setNextRevisionId(null);
        cursorRevision.update(REVISION.NEXT_REVISION_ID);
        dslContext.update(REVISION)
                .setNull(REVISION.PREV_REVISION_ID)
                .setNull(REVISION.NEXT_REVISION_ID)
                .where(REVISION.REVISION_ID.in(deleteRevisionTargets))
                .execute();
        dslContext.deleteFrom(REVISION).where(REVISION.REVISION_ID.in(deleteRevisionTargets)).execute();

        discardRevisionCodeListValues(codeListManifestRecord, codeListRecord);

        CodeListRecord prevCodeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListRecord.getPrevCodeListId())).fetchOne();

        // unlink prev CODE_LIST
        prevCodeListRecord.setNextCodeListId(null);
        prevCodeListRecord.update(CODE_LIST.NEXT_CODE_LIST_ID);

        // delete current CODE_LIST
        codeListRecord.delete();

        return new DiscardRevisionCodeListRepositoryResponse(request.getCodeListManifestId());
    }

    private void discardRevisionCodeListValues(CodeListManifestRecord codeListManifestRecord, CodeListRecord codeListRecord) {
        List<CodeListValueManifestRecord> codeListValueManifests = dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(codeListManifestRecord.getCodeListManifestId()))
                .fetch();

        for (CodeListValueManifestRecord codeListValueManifest : codeListValueManifests) {
            CodeListValueRecord codeListValue = dslContext.selectFrom(CODE_LIST_VALUE)
                    .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(codeListValueManifest.getCodeListValueId()))
                    .fetchOne();

            if (codeListValue.getPrevCodeListValueId() == null) {
                //delete code list value and code list manifest which added this revision
                codeListValueManifest.delete();
                codeListValue.delete();
            } else {
                //delete code list value and update code list value manifest
                CodeListValueRecord prevCodeListValue = dslContext.selectFrom(CODE_LIST_VALUE)
                        .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(codeListValue.getPrevCodeListValueId()))
                        .fetchOne();
                prevCodeListValue.setNextCodeListValueId(null);
                prevCodeListValue.update(CODE_LIST_VALUE.NEXT_CODE_LIST_VALUE_ID);
                codeListValueManifest.setCodeListValueId(prevCodeListValue.getCodeListValueId());
                codeListValueManifest.update(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID);
                codeListValue.delete();
            }
        }
    }

    private void createNewCodeListValueForRevisedRecord(
            AppUser user,
            CodeListManifestRecord manifestRecord,
            CodeListRecord nextCodeListRecord,
            ULong targetReleaseId,
            LocalDateTime timestamp) {
        for (CodeListValueManifestRecord codeListValueManifestRecord : dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                .where(and(
                        CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(targetReleaseId),
                        CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(manifestRecord.getCodeListManifestId())
                ))
                .fetch()) {

            CodeListValueRecord prevCodeListValueRecord = dslContext.selectFrom(CODE_LIST_VALUE)
                    .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(codeListValueManifestRecord.getCodeListValueId()))
                    .fetchOne();

            CodeListValueRecord nextCodeListValueRecord = prevCodeListValueRecord.copy();
            nextCodeListValueRecord.setCodeListId(nextCodeListRecord.getCodeListId());
            nextCodeListValueRecord.setCreatedBy(ULong.valueOf(user.getAppUserId()));
            nextCodeListValueRecord.setLastUpdatedBy(ULong.valueOf(user.getAppUserId()));
            nextCodeListValueRecord.setOwnerUserId(ULong.valueOf(user.getAppUserId()));
            nextCodeListValueRecord.setCreationTimestamp(timestamp);
            nextCodeListValueRecord.setLastUpdateTimestamp(timestamp);
            nextCodeListValueRecord.setPrevCodeListValueId(prevCodeListValueRecord.getCodeListValueId());
            nextCodeListValueRecord.setCodeListValueId(
                    dslContext.insertInto(CODE_LIST_VALUE)
                            .set(nextCodeListValueRecord)
                            .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID).fetchOne().getCodeListValueId()
            );

            prevCodeListValueRecord.setNextCodeListValueId(nextCodeListValueRecord.getCodeListValueId());
            prevCodeListValueRecord.update(CODE_LIST_VALUE.NEXT_CODE_LIST_VALUE_ID);

            codeListValueManifestRecord.setCodeListValueId(nextCodeListValueRecord.getCodeListValueId());
            codeListValueManifestRecord.setCodeListManifestId(manifestRecord.getCodeListManifestId());
            codeListValueManifestRecord.update(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID,
                    CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID);
        }
    }
}
