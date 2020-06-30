package org.oagi.srt.gateway.http.api.code_list_management.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.CodeListManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListValueManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListValueRecord;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.code_list_management.data.*;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.redis.event.EventHandler;
import org.oagi.srt.repo.RevisionRepository;
import org.oagi.srt.repo.component.code_list.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.filter.ContainsFilterBuilder.contains;

@Service
@Transactional(readOnly = true)
public class CodeListService extends EventHandler {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CodeListWriteRepository codeListWriteRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    private SelectOnConditionStep<Record17<
            ULong, String, String, ULong, String,
            String, ULong, String, String, LocalDateTime,
            ULong, String, String, Byte, String,
            Byte, UInteger>> getSelectOnConditionStep() {
        return dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST.GUID,
                CODE_LIST.NAME.as("code_list_name"),
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                CODE_LIST.as("based_code_list").NAME.as("based_code_list_name"),
                CODE_LIST.LIST_ID,
                CODE_LIST.AGENCY_ID,
                AGENCY_ID_LIST_VALUE.NAME.as("agency_id_name"),
                CODE_LIST.VERSION_ID,
                CODE_LIST.LAST_UPDATE_TIMESTAMP,
                APP_USER.as("owner").APP_USER_ID.as("owner_id"),
                APP_USER.as("owner").LOGIN_ID.as("owner"),
                APP_USER.as("updater").LOGIN_ID.as("last_update_user"),
                CODE_LIST.EXTENSIBLE_INDICATOR.as("extensible"),
                CODE_LIST.STATE,
                CODE_LIST.IS_DEPRECATED.as("deprecated"),
                REVISION.REVISION_NUM.as("revision"))
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(APP_USER.as("owner")).on(CODE_LIST.OWNER_USER_ID.eq(APP_USER.as("owner").APP_USER_ID))
                .join(APP_USER.as("updater")).on(CODE_LIST.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .join(REVISION).on(CODE_LIST_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .leftJoin(CODE_LIST_MANIFEST.as("based")).on(CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.as("based").CODE_LIST_MANIFEST_ID))
                .leftJoin(CODE_LIST.as("based_code_list")).on(CODE_LIST_MANIFEST.as("based").CODE_LIST_ID.eq(CODE_LIST.as("based_code_list").CODE_LIST_ID))
                .leftJoin(AGENCY_ID_LIST_VALUE).on(CODE_LIST.AGENCY_ID.eq(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID));
    }

    public PageResponse<CodeListForList> getCodeLists(User user, CodeListForListRequest request) {
        SelectOnConditionStep<
                Record17<ULong, String, String, ULong, String,
                        String, ULong, String, String, LocalDateTime,
                        ULong, String, String, Byte, String,
                        Byte, UInteger>> step = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        conditions.add(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));

        if (!StringUtils.isEmpty(request.getName())) {
            conditions.addAll(contains(request.getName(), CODE_LIST.NAME));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(CODE_LIST.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (request.getDeprecated() != null) {
            conditions.add(CODE_LIST.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getExtensible() != null) {
            conditions.add(CODE_LIST.EXTENSIBLE_INDICATOR.eq((byte) (request.getExtensible() ? 1 : 0)));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("owner").LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(CODE_LIST.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(CODE_LIST.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        SelectConnectByStep<Record17<ULong, String, String, ULong, String,
                String, ULong, String, String, LocalDateTime,
                ULong, String, String, Byte, String,
                Byte, UInteger>> conditionStep = step;
        if (!conditions.isEmpty()) {
            conditionStep = step.where(conditions);
        }

        PageRequest pageRequest = request.getPageRequest();
        String sortDirection = pageRequest.getSortDirection();
        SortField sortField = null;
        if (!StringUtils.isEmpty(pageRequest.getSortActive())) {
            switch (pageRequest.getSortActive()) {
                case "codeListName":
                    if ("asc".equals(sortDirection)) {
                        sortField = CODE_LIST.NAME.asc();
                    } else if ("desc".equals(sortDirection)) {
                        sortField = CODE_LIST.NAME.desc();
                    }

                    break;

                case "lastUpdateTimestamp":
                    if ("asc".equals(sortDirection)) {
                        sortField = CODE_LIST.LAST_UPDATE_TIMESTAMP.asc();
                    } else if ("desc".equals(sortDirection)) {
                        sortField = CODE_LIST.LAST_UPDATE_TIMESTAMP.desc();
                    }

                    break;
            }
        }


        SelectWithTiesAfterOffsetStep<Record17<ULong, String, String, ULong, String,
                String, ULong, String, String, LocalDateTime,
                ULong, String, String, Byte, String,
                Byte, UInteger>> offsetStep = null;
        if (sortField != null) {
            offsetStep = conditionStep.orderBy(sortField)
                    .limit(pageRequest.getOffset(), pageRequest.getPageSize());
        } else {
            if (pageRequest.getPageIndex() >= 0 && pageRequest.getPageSize() > 0) {
                offsetStep = conditionStep
                        .limit(pageRequest.getOffset(), pageRequest.getPageSize());
            }
        }

        List<CodeListForList> result = (offsetStep != null) ?
                offsetStep.fetchInto(CodeListForList.class) : conditionStep.fetchInto(CodeListForList.class);

        AppUser requester = sessionService.getAppUser(user);
        result.stream().forEach(e -> {
            e.setAccess(
                    AccessPrivilege.toAccessPrivilege(requester, sessionService.getAppUser(e.getOwnerId()),
                            CcState.valueOf(e.getState()))
            );
            e.setOwnerId(null); // hide sensitive information
        });

        PageResponse<CodeListForList> response = new PageResponse();
        response.setList(result);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(dslContext.selectCount()
                .from(CODE_LIST)
                .join(CODE_LIST_MANIFEST).on(CODE_LIST.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID))
                .join(APP_USER.as("owner")).on(CODE_LIST.OWNER_USER_ID.eq(APP_USER.as("owner").APP_USER_ID))
                .join(APP_USER.as("updater")).on(CODE_LIST.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .where(conditions)
                .fetchOptionalInto(Integer.class).orElse(0));

        return response;
    }

    public CodeList getCodeList(User user, BigInteger manifestId) {
        CodeList codeList = dslContext.select(
                CODE_LIST_MANIFEST.RELEASE_ID,
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST.NAME.as("code_list_name"),
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                CODE_LIST.as("based_code_list").NAME.as("based_code_list_name"),
                CODE_LIST.AGENCY_ID,
                AGENCY_ID_LIST_VALUE.NAME.as("agency_id_name"),
                CODE_LIST.VERSION_ID,
                CODE_LIST.GUID,
                CODE_LIST.LIST_ID,
                CODE_LIST.DEFINITION,
                CODE_LIST.DEFINITION_SOURCE,
                CODE_LIST.REMARK,
                CODE_LIST.EXTENSIBLE_INDICATOR.as("extensible"),
                APP_USER.as("owner").APP_USER_ID.as("owner_id"),
                CODE_LIST.STATE,
                CODE_LIST.IS_DEPRECATED.as("deprecated"),
                REVISION.REVISION_NUM.as("revision"))
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(REVISION).on(CODE_LIST_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(APP_USER.as("owner")).on(CODE_LIST.OWNER_USER_ID.eq(APP_USER.as("owner").APP_USER_ID))
                .leftJoin(CODE_LIST_MANIFEST.as("based")).on(CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.as("based").CODE_LIST_MANIFEST_ID))
                .leftJoin(CODE_LIST.as("based_code_list")).on(CODE_LIST_MANIFEST.as("based").CODE_LIST_ID.eq(CODE_LIST.as("based_code_list").CODE_LIST_ID))
                .leftJoin(AGENCY_ID_LIST_VALUE).on(CODE_LIST.AGENCY_ID.eq(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID))
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOptionalInto(CodeList.class).orElse(null);

        if (codeList == null) {
            throw new EmptyResultDataAccessException(1);
        }

        AppUser requester = sessionService.getAppUser(user);
        codeList.setAccess(
                AccessPrivilege.toAccessPrivilege(requester, sessionService.getAppUser(codeList.getOwnerId()),
                        CcState.valueOf(codeList.getState()))
        );
        codeList.setOwnerId(null); // hide sensitive information

        boolean isPublished = CodeListState.Published.name().equals(codeList.getState());

        List<Condition> conditions = new ArrayList();
        conditions.add(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(manifestId)));
        if (isPublished) {
            conditions.add(CODE_LIST_VALUE.LOCKED_INDICATOR.eq((byte) 0));
        }

        List<CodeListValue> codeListValues = dslContext.select(
                CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID,
                CODE_LIST_VALUE.VALUE,
                CODE_LIST_VALUE.NAME,
                CODE_LIST_VALUE.GUID,
                CODE_LIST_VALUE.DEFINITION,
                CODE_LIST_VALUE.DEFINITION_SOURCE,
                CODE_LIST_VALUE.IS_DEPRECATED.as("deprecated"),
                CODE_LIST_VALUE.USED_INDICATOR.as("used"),
                CODE_LIST_VALUE.LOCKED_INDICATOR.as("locked"),
                CODE_LIST_VALUE.EXTENSION_INDICATOR.as("extension"))
                .from(CODE_LIST_VALUE)
                .join(CODE_LIST_VALUE_MANIFEST).on(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID))
                .join(CODE_LIST_MANIFEST)
                    .on(and(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID),
                            CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(CODE_LIST_MANIFEST.RELEASE_ID)))
                .where(conditions)
                .fetchInto(CodeListValue.class);
        codeList.setCodeListValues(codeListValues);

        return codeList;
    }

    @Transactional
    public BigInteger createCodeList(User user, CodeList codeList) {
        LocalDateTime timestamp = LocalDateTime.now();
        CreateCodeListRepositoryRequest repositoryRequest =
                new CreateCodeListRepositoryRequest(user, timestamp, codeList.getBasedCodeListManifestId(),
                        codeList.getReleaseId());

        CreateCodeListRepositoryResponse repositoryResponse =
                codeListWriteRepository.createCodeList(repositoryRequest);

        fireEvent(new CreatedCodeListEvent());

        return repositoryResponse.getCodeListManifestId();
    }

    private void createCodeList(CodeListRecord codeListRecord, CodeListManifestRecord manifestRecord,
                                CodeListValue codeListValue) {

        boolean locked = codeListValue.isLocked();
        boolean used = codeListValue.isUsed();
        boolean extension = codeListValue.isExtension();
        if (locked) {
            used = false;
            extension = false;
        }

        ULong codeListValueId = dslContext.insertInto(CODE_LIST_VALUE,
                CODE_LIST_VALUE.CODE_LIST_ID,
                CODE_LIST_VALUE.VALUE,
                CODE_LIST_VALUE.NAME,
                CODE_LIST_VALUE.DEFINITION,
                CODE_LIST_VALUE.DEFINITION_SOURCE,
                CODE_LIST_VALUE.USED_INDICATOR,
                CODE_LIST_VALUE.LOCKED_INDICATOR,
                CODE_LIST_VALUE.EXTENSION_INDICATOR,
                CODE_LIST_VALUE.CREATED_BY,
                CODE_LIST_VALUE.OWNER_USER_ID,
                CODE_LIST_VALUE.LAST_UPDATED_BY,
                CODE_LIST_VALUE.CREATION_TIMESTAMP,
                CODE_LIST_VALUE.LAST_UPDATE_TIMESTAMP).values(
                manifestRecord.getCodeListId(),
                codeListValue.getValue(),
                codeListValue.getName(),
                codeListValue.getDefinition(),
                codeListValue.getDefinitionSource(),
                (byte) ((used) ? 1 : 0),
                (byte) ((locked) ? 1 : 0),
                (byte) ((extension) ? 1 : 0),
                codeListRecord.getCreatedBy(),
                codeListRecord.getOwnerUserId(),
                codeListRecord.getLastUpdatedBy(),
                codeListRecord.getCreationTimestamp(),
                codeListRecord.getLastUpdateTimestamp())
                .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID).fetchOne().getCodeListValueId();

        dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                .set(CODE_LIST_VALUE_MANIFEST.RELEASE_ID, manifestRecord.getReleaseId())
                .set(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID, codeListValueId)
                .set(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID, manifestRecord.getCodeListManifestId())
                .execute();
    }

    @Transactional
    public void update(User user, CodeList codeList) {
        LocalDateTime timestamp = LocalDateTime.now();
        if (!StringUtils.isEmpty(codeList.getState())) {
            updateCodeListState(user, timestamp, codeList.getCodeListManifestId(), CcState.valueOf(codeList.getState()));
        } else {
            updateCodeListProperties(user, timestamp, codeList);
            updateCodeListValues(user, timestamp, codeList);
        }
    }

    @Transactional
    public BigInteger makeNewRevision(User user, BigInteger codeListManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ReviseCodeListRepositoryRequest reviseCodeListRepositoryRequest
                = new ReviseCodeListRepositoryRequest(user, codeListManifestId, timestamp);

        ReviseCodeListRepositoryResponse reviseCodeListRepositoryResponse =
                codeListWriteRepository.reviseCodeList(reviseCodeListRepositoryRequest);

        fireEvent(new ReviseCodeListEvent());

        return reviseCodeListRepositoryResponse.getCodeListManifestId();
    }

    public CodeList getCodeListRevision(User user, BigInteger manifestId) {
        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
        if (codeListManifestRecord == null) {
            throw new IllegalArgumentException("Unknown CodeList: " + manifestId);
        }

        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                .fetchOne();

        ULong lastPublishedCodeListId = codeListRecord.getPrevCodeListId();
        if (lastPublishedCodeListId == null) {
            // rev = 1 return null
            return null;
        }
        CodeList codeList = dslContext.select(
                CODE_LIST.NAME.as("code_list_name"),
                CODE_LIST.AGENCY_ID,
                AGENCY_ID_LIST_VALUE.NAME.as("agency_id_name"),
                CODE_LIST.VERSION_ID,
                CODE_LIST.GUID,
                CODE_LIST.LIST_ID,
                CODE_LIST.DEFINITION,
                CODE_LIST.DEFINITION_SOURCE,
                CODE_LIST.REMARK,
                CODE_LIST.EXTENSIBLE_INDICATOR.as("extensible"),
                APP_USER.as("owner").APP_USER_ID.as("owner_id"),
                CODE_LIST.STATE,
                CODE_LIST.IS_DEPRECATED.as("deprecated"))
                .from(CODE_LIST)
                .join(APP_USER.as("owner")).on(CODE_LIST.OWNER_USER_ID.eq(APP_USER.as("owner").APP_USER_ID))
                .leftJoin(AGENCY_ID_LIST_VALUE).on(CODE_LIST.AGENCY_ID.eq(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID))
                .where(CODE_LIST.CODE_LIST_ID.eq(lastPublishedCodeListId))
                .fetchOptionalInto(CodeList.class).orElse(null);

        if (codeList == null) {
            throw new EmptyResultDataAccessException(1);
        }

        List<CodeListValue> codeListValues = dslContext.select(
                CODE_LIST_VALUE.VALUE,
                CODE_LIST_VALUE.NAME,
                CODE_LIST_VALUE.GUID,
                CODE_LIST_VALUE.DEFINITION,
                CODE_LIST_VALUE.DEFINITION_SOURCE,
                CODE_LIST_VALUE.IS_DEPRECATED.as("deprecated"),
                CODE_LIST_VALUE.USED_INDICATOR.as("used"),
                CODE_LIST_VALUE.LOCKED_INDICATOR.as("locked"),
                CODE_LIST_VALUE.EXTENSION_INDICATOR.as("extension"))
                .from(CODE_LIST_VALUE)
                .where(CODE_LIST_VALUE.CODE_LIST_ID.eq(lastPublishedCodeListId))
                .fetchInto(CodeListValue.class);
        codeList.setCodeListValues(codeListValues);

        return codeList;
    }

    public void updateCodeListState(User user, LocalDateTime timestamp, BigInteger codeListManifestId, CcState state) {
        UpdateCodeListStateRepositoryRequest request =
                new UpdateCodeListStateRepositoryRequest(user, timestamp, codeListManifestId, state);

        UpdateCodeListStateRepositoryResponse response =
                codeListWriteRepository.updateCodeListState(request);

        fireEvent(new UpdatedCodeListStateEvent());
    }

    private void updateCodeListProperties(User user, LocalDateTime timestamp, CodeList codeList) {
        UpdateCodeListPropertiesRepositoryRequest request =
                new UpdateCodeListPropertiesRepositoryRequest(user, timestamp, codeList.getCodeListManifestId());

        request.setCodeListName(codeList.getCodeListName());
        request.setAgencyId(codeList.getAgencyId());
        request.setVersionId(codeList.getVersionId());
        request.setListId(codeList.getListId());
        request.setDefinition(codeList.getDefinition());
        request.setDefinitionSource(codeList.getDefinitionSource());
        request.setRemark(codeList.getRemark());
        request.setExtensible(codeList.isExtensible());
        request.setDeprecated(codeList.isDeprecated());

        UpdateCodeListPropertiesRepositoryResponse response =
                codeListWriteRepository.updateCodeListProperties(request);

        fireEvent(new UpdatedCodeListPropertiesEvent());
    }

    private void updateCodeListValues(User user, LocalDateTime timestamp, CodeList codeList) {
        ModifyCodeListValuesRepositoryRequest request =
                new ModifyCodeListValuesRepositoryRequest(user, timestamp,
                        codeList.getCodeListManifestId());

        request.setCodeListValueList(
                codeList.getCodeListValues().stream().map(e -> {
                    ModifyCodeListValuesRepositoryRequest.CodeListValue codeListValue =
                            new ModifyCodeListValuesRepositoryRequest.CodeListValue();

                    codeListValue.setName(e.getName());
                    codeListValue.setValue(e.getValue());
                    codeListValue.setDefinition(e.getDefinition());
                    codeListValue.setDefinitionSource(e.getDefinitionSource());
                    codeListValue.setUsed(e.isUsed());
                    codeListValue.setDeprecated(e.isDeprecated());

                    if (codeListValue.isLocked()) {
                        codeListValue.setUsed(false);
                        codeListValue.setExtension(false);
                    }

                    codeListValue.setLocked(e.isLocked());
                    codeListValue.setUsed(e.isUsed());
                    codeListValue.setExtension(e.isExtension());

                    return codeListValue;
                }).collect(Collectors.toList())
        );

        ModifyCodeListValuesRepositoryResponse response =
                codeListWriteRepository.modifyCodeListValues(request);
    }

    @Transactional
    public void update(User user,
                       CodeListRecord codeListRecord, CodeListManifestRecord manifestRecord,
                       List<CodeListValue> codeListValues) {

        Map<ULong, CodeListValueManifestRecord> codeListValueManifestRecordMap = dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                .where(and(
                        CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(manifestRecord.getCodeListManifestId()),
                        CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(manifestRecord.getReleaseId()))
                ).fetchStreamInto(CodeListValueManifestRecord.class)
                .collect(Collectors.toMap(CodeListValueManifestRecord::getCodeListValueManifestId, Function.identity()));

        Map<ULong, CodeListValueRecord> codeListValueRecordMap = dslContext.selectFrom(CODE_LIST_VALUE)
                .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.in(
                        codeListValueManifestRecordMap.values().stream().map(e -> e.getCodeListValueId()).collect(Collectors.toList())
                )).fetchStreamInto(CodeListValueRecord.class)
                .collect(Collectors.toMap(CodeListValueRecord::getCodeListValueId, Function.identity()));

        // deletion begins
        Set<ULong> codeListValueManifestIds = codeListValues.stream()
                .filter(e -> e.getCodeListValueManifestId() != null && e.getCodeListValueManifestId() > 0L)
                .map(e -> ULong.valueOf(e.getCodeListValueManifestId()))
                .collect(Collectors.toSet());

        Set<ULong> existingCodeListValueManifestIds = new HashSet(codeListValueManifestRecordMap.keySet()); // deep copy
        existingCodeListValueManifestIds.removeAll(codeListValueManifestIds);

        if (!existingCodeListValueManifestIds.isEmpty()) {
            for (ULong codeListValueManifestId : existingCodeListValueManifestIds) {
                CodeListValueManifestRecord codeListValueManifestRecord =
                        codeListValueManifestRecordMap.get(codeListValueManifestId);
                CodeListValueRecord codeListValueRecord =
                        codeListValueRecordMap.get(codeListValueManifestRecord.getCodeListValueId());

                ULong prevCodeListValueId = codeListValueRecord.getCodeListValueId();

                codeListValueRecord.setCodeListValueId(null);
                codeListValueRecord.setPrevCodeListValueId(prevCodeListValueId);

                ULong requesterId = codeListRecord.getOwnerUserId();
                LocalDateTime timestamp = codeListRecord.getLastUpdateTimestamp();

                if (!codeListValueRecord.getOwnerUserId().equals(requesterId)) {
                    throw new DataAccessForbiddenException("'" + user.getUsername() +
                            "' doesn't have an access privilege.");
                }

                codeListValueRecord.setLastUpdatedBy(requesterId);
                codeListValueRecord.setLastUpdateTimestamp(timestamp);

                codeListValueRecord = dslContext.insertInto(CODE_LIST_VALUE)
                        .set(codeListValueRecord)
                        .returning().fetchOne();

                dslContext.update(CODE_LIST_VALUE)
                        .set(CODE_LIST_VALUE.NEXT_CODE_LIST_VALUE_ID, codeListValueRecord.getCodeListValueId())
                        .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(prevCodeListValueId))
                        .execute();

                dslContext.deleteFrom(CODE_LIST_VALUE_MANIFEST)
                        .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID.eq(codeListValueManifestId))
                        .execute();
            }
        }
        // deletion ends

        // insertion / updating begins
        for (CodeListValue codeListValue : codeListValues) {
            CodeListValueRecord codeListValueRecord;
            Long codeListValueManifestId = codeListValue.getCodeListValueManifestId();
            if (codeListValueManifestId == null || codeListValueManifestId <= 0L) {
                codeListValueRecord = new CodeListValueRecord();
            } else {
                CodeListValueManifestRecord codeListValueManifestRecord =
                        codeListValueManifestRecordMap.get(ULong.valueOf(codeListValueManifestId));
                if (codeListValueManifestRecord == null) {
                    throw new IllegalArgumentException();
                }
                codeListValueRecord = codeListValueRecordMap.get(
                        codeListValueManifestRecord.getCodeListValueId()
                );
            }

            ULong prevCodeListValueId = codeListValueRecord.getCodeListValueId();
            boolean isUpdate = prevCodeListValueId != null;

            codeListValueRecord.setCodeListValueId(null);
            codeListValueRecord.setPrevCodeListValueId(prevCodeListValueId);

            codeListValueRecord.setCodeListId(codeListRecord.getCodeListId());
            codeListValueRecord.setValue(codeListValue.getValue());
            codeListValueRecord.setName(codeListValue.getName());

            codeListValueRecord.setDefinition(codeListValue.getDefinition());
            codeListValueRecord.setDefinitionSource(codeListValue.getDefinitionSource());

            codeListValueRecord.setUsedIndicator((byte) (codeListValue.isUsed() ? 1 : 0));
            codeListValueRecord.setExtensionIndicator((byte) (codeListValue.isExtension() ? 1 : 0));
            codeListValueRecord.setLockedIndicator((byte) (codeListValue.isLocked() ? 1 : 0));

            ULong requesterId = codeListRecord.getOwnerUserId();
            LocalDateTime timestamp = codeListRecord.getLastUpdateTimestamp();

            if (!isUpdate) {
                codeListValueRecord.setCreatedBy(requesterId);
                codeListValueRecord.setCreationTimestamp(timestamp);
            }

            if (codeListValueRecord.getOwnerUserId() != null &&
                    !codeListValueRecord.getOwnerUserId().equals(requesterId)) {
                throw new DataAccessForbiddenException("'" + user.getUsername() +
                        "' doesn't have an access privilege.");
            } else {
                codeListValueRecord.setOwnerUserId(requesterId);
            }

            codeListValueRecord.setLastUpdatedBy(requesterId);
            codeListValueRecord.setLastUpdateTimestamp(timestamp);

            codeListValueRecord = dslContext.insertInto(CODE_LIST_VALUE)
                    .set(codeListValueRecord)
                    .returning().fetchOne();

            if (isUpdate) {
                dslContext.update(CODE_LIST_VALUE)
                        .set(CODE_LIST_VALUE.NEXT_CODE_LIST_VALUE_ID, codeListValueRecord.getCodeListValueId())
                        .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(prevCodeListValueId))
                        .execute();

                dslContext.update(CODE_LIST_VALUE_MANIFEST)
                        .set(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID, codeListValueRecord.getCodeListValueId())
                        .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID.eq(ULong.valueOf(codeListValueManifestId)))
                        .execute();
            } else {
                CodeListValueManifestRecord codeListValueManifestRecord = new CodeListValueManifestRecord();

                codeListValueManifestRecord.setCodeListManifestId(manifestRecord.getCodeListManifestId());
                codeListValueManifestRecord.setCodeListValueId(codeListValueRecord.getCodeListValueId());
                codeListValueManifestRecord.setReleaseId(manifestRecord.getReleaseId());

                dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                        .set(codeListValueManifestRecord)
                        .execute();
            }
        }
        // insertion / updating ends
    }

    @Transactional
    public void deleteCodeList(User user, BigInteger codeListId) {
        DeleteCodeListRepositoryRequest repositoryRequest =
                new DeleteCodeListRepositoryRequest(user, codeListId);

        DeleteCodeListRepositoryResponse repositoryResponse =
                codeListWriteRepository.deleteCodeList(repositoryRequest);

        fireEvent(new DeletedCodeListEvent());
    }

    @Transactional
    public void deleteCodeList(User user, List<BigInteger> codeListIds) {
        codeListIds.stream().forEach(e -> deleteCodeList(user, e));
    }

    public boolean hasSameCodeList(SameCodeListParams params) {
        List<Condition> conditions = new ArrayList();
        conditions.add(and(
                CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(params.getReleaseId())),
                CODE_LIST.STATE.notEqual(CcState.Deleted.name())
        ));

        if (params.getCodeListManifestId() != null) {
            conditions.add(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.ne(ULong.valueOf(params.getCodeListManifestId())));
        }
        conditions.add(and(
                CODE_LIST.LIST_ID.eq(params.getListId()),
                CODE_LIST.AGENCY_ID.eq(ULong.valueOf(params.getAgencyId())),
                CODE_LIST.VERSION_ID.eq(params.getVersionId())
        ));

        return dslContext.selectCount()
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(conditions).fetchOneInto(Integer.class) > 0;
    }

    public boolean hasSameNameCodeList(SameNameCodeListParams params) {
        List<Condition> conditions = new ArrayList();
        conditions.add(and(
                CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(params.getReleaseId())),
                CODE_LIST.STATE.notEqual(CcState.Deleted.name())
        ));

        if (params.getCodeListManifestId() != null) {
            conditions.add(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.ne(ULong.valueOf(params.getCodeListManifestId())));
        }
        conditions.add(CODE_LIST.NAME.eq(params.getCodeListName()));

        return dslContext.selectCount()
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(conditions).fetchOneInto(Integer.class) > 0;
    }
}
