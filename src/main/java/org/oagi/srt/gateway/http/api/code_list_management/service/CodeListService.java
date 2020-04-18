package org.oagi.srt.gateway.http.api.code_list_management.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.CodeListManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListValueManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListValueRecord;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.code_list_management.data.*;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.api.code_list_management.data.CodeListState.WIP;
import static org.oagi.srt.gateway.http.helper.filter.ContainsFilterBuilder.contains;

@Service
@Transactional(readOnly = true)
public class CodeListService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    private SelectOnConditionStep<
            Record15<ULong, String, String, ULong, String,
                    String, ULong, String, String, LocalDateTime,
                    String, String, Byte, String, Byte>> getSelectOnConditionStep() {
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
                APP_USER.as("owner").LOGIN_ID.as("owner"),
                APP_USER.as("updater").LOGIN_ID.as("last_update_user"),
                CODE_LIST.EXTENSIBLE_INDICATOR.as("extensible"),
                CODE_LIST.STATE,
                CODE_LIST.IS_DEPRECATED)
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(APP_USER.as("owner")).on(CODE_LIST.OWNER_USER_ID.eq(APP_USER.as("owner").APP_USER_ID))
                .join(APP_USER.as("updater")).on(CODE_LIST.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .leftJoin(CODE_LIST_MANIFEST.as("based")).on(CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.as("based").CODE_LIST_MANIFEST_ID))
                .leftJoin(CODE_LIST.as("based_code_list")).on(CODE_LIST_MANIFEST.as("based").CODE_LIST_ID.eq(CODE_LIST.as("based_code_list").CODE_LIST_ID))
                .leftJoin(AGENCY_ID_LIST_VALUE).on(CODE_LIST.AGENCY_ID.eq(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID));
    }

    public PageResponse<CodeListForList> getCodeLists(CodeListForListRequest request) {
        SelectOnConditionStep<
                Record15<ULong, String, String, ULong, String,
                        String, ULong, String, String, LocalDateTime,
                        String, String, Byte, String, Byte>> step = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        conditions.add(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));

        if (!StringUtils.isEmpty(request.getName())) {
            conditions.addAll(contains(request.getName(), CODE_LIST.NAME));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(CODE_LIST.STATE.in(request.getStates()));
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

        SelectConnectByStep<Record15<ULong, String, String, ULong, String,
                String, ULong, String, String, LocalDateTime,
                String, String, Byte, String, Byte>> conditionStep = step;
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


        SelectWithTiesAfterOffsetStep<Record15<ULong, String, String, ULong, String,
                String, ULong, String, String, LocalDateTime,
                String, String, Byte, String, Byte>> offsetStep = null;
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

    public CodeList getCodeList(long manifestId) {
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
                CODE_LIST.STATE)
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .leftJoin(CODE_LIST_MANIFEST.as("based")).on(CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(CODE_LIST_MANIFEST.as("based").CODE_LIST_MANIFEST_ID))
                .leftJoin(CODE_LIST.as("based_code_list")).on(CODE_LIST_MANIFEST.as("based").CODE_LIST_ID.eq(CODE_LIST.as("based_code_list").CODE_LIST_ID))
                .leftJoin(AGENCY_ID_LIST_VALUE).on(CODE_LIST.AGENCY_ID.eq(AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID))
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOptionalInto(CodeList.class).orElse(null);

        if (codeList == null) {
            throw new EmptyResultDataAccessException(1);
        }

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
                CODE_LIST_VALUE.DEFINITION,
                CODE_LIST_VALUE.DEFINITION_SOURCE,
                CODE_LIST_VALUE.USED_INDICATOR.as("used"),
                CODE_LIST_VALUE.LOCKED_INDICATOR.as("locked"),
                CODE_LIST_VALUE.EXTENSION_INDICATOR.as("extension"))
                .from(CODE_LIST_VALUE_MANIFEST)
                .join(CODE_LIST_VALUE).on(CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID.eq(CODE_LIST_VALUE.CODE_LIST_VALUE_ID))
                .where(conditions)
                .fetchInto(CodeListValue.class);
        codeList.setCodeListValues(codeListValues);

        return codeList;
    }

    @Transactional
    public void insert(User user, CodeList codeList) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();

        CodeListRecord codeListRecord = dslContext.insertInto(CODE_LIST,
                CODE_LIST.GUID,
                CODE_LIST.NAME,
                CODE_LIST.LIST_ID,
                CODE_LIST.AGENCY_ID,
                CODE_LIST.VERSION_ID,
                CODE_LIST.REMARK,
                CODE_LIST.DEFINITION,
                CODE_LIST.DEFINITION_SOURCE,
                CODE_LIST.EXTENSIBLE_INDICATOR,
                CODE_LIST.STATE,
                CODE_LIST.IS_DEPRECATED,
                CODE_LIST.CREATED_BY,
                CODE_LIST.OWNER_USER_ID,
                CODE_LIST.LAST_UPDATED_BY,
                CODE_LIST.CREATION_TIMESTAMP,
                CODE_LIST.LAST_UPDATE_TIMESTAMP).values(
                SrtGuid.randomGuid(),
                codeList.getCodeListName(),
                codeList.getListId(),
                ULong.valueOf(codeList.getAgencyId()),
                codeList.getVersionId(),
                codeList.getRemark(),
                codeList.getDefinition(),
                codeList.getDefinitionSource(),
                (byte) ((codeList.isExtensible()) ? 1 : 0),
                WIP.name(),
                (byte) 0,
                userId, userId, userId, timestamp, timestamp)
                .returning().fetchOne();

        CodeListManifestRecord manifestRecord = new CodeListManifestRecord();
        manifestRecord.setReleaseId(ULong.valueOf(codeList.getReleaseId()));
        manifestRecord.setCodeListId(codeListRecord.getCodeListId());
        if (codeList.getBasedCodeListManifestId() != null) {
            manifestRecord.setBasedCodeListManifestId(ULong.valueOf(codeList.getBasedCodeListManifestId()));
        }

        manifestRecord = dslContext.insertInto(CODE_LIST_MANIFEST)
                .set(manifestRecord)
                .returning().fetchOne();

        for (CodeListValue codeListValue : codeList.getCodeListValues()) {
            insert(codeListRecord, manifestRecord, codeListValue);
        }
    }

    private void insert(CodeListRecord codeListRecord, CodeListManifestRecord manifestRecord,
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
        String state = codeList.getState();
        CodeListManifestRecord manifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(codeList.getCodeListManifestId())))
                .fetchOne();
        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(manifestRecord.getCodeListId()))
                .fetchOne();

        ULong requesterId = ULong.valueOf(sessionService.userId(user));
        if (!codeListRecord.getOwnerUserId().equals(requesterId)) {
            throw new DataAccessForbiddenException("'" + user.getUsername() +
                    "' doesn't have an access privilege.");
        }
        
        LocalDateTime timestamp = LocalDateTime.now();
        ULong prevCodeListId = codeListRecord.getCodeListId();

        if (!StringUtils.isEmpty(state)) {
            codeListRecord.setState(state);
        } else {
            if (!StringUtils.equals(codeListRecord.getName(), codeList.getCodeListName())) {
                codeListRecord.setName(codeList.getCodeListName());
            }
            if (!StringUtils.equals(codeListRecord.getListId(), codeList.getListId())) {
                codeListRecord.setListId(codeList.getListId());
            }
            if (codeListRecord.getAgencyId().longValue() != codeList.getAgencyId()) {
                codeListRecord.setAgencyId(ULong.valueOf(codeList.getAgencyId()));
            }
            if (!StringUtils.equals(codeListRecord.getVersionId(), codeList.getVersionId())) {
                codeListRecord.setVersionId(codeList.getVersionId());
            }
            if (!StringUtils.equals(codeListRecord.getDefinition(), codeList.getDefinition())) {
                codeListRecord.setDefinition(codeList.getDefinition());
            }
            if (!StringUtils.equals(codeListRecord.getRemark(), codeList.getRemark())) {
                codeListRecord.setRemark(codeList.getRemark());
            }
            if (!StringUtils.equals(codeListRecord.getDefinitionSource(), codeList.getDefinitionSource())) {
                codeListRecord.setDefinitionSource(codeList.getDefinitionSource());
            }
            if (codeList.isExtensible() != ((codeListRecord.getExtensibleIndicator() == 1) ? true : false)) {
                codeListRecord.setExtensibleIndicator((byte) ((codeList.isExtensible()) ? 1 : 0));
            }
        }

        codeListRecord.setCodeListId(null);
        codeListRecord.setCreatedBy(requesterId);
        codeListRecord.setOwnerUserId(requesterId);
        codeListRecord.setLastUpdatedBy(requesterId);
        codeListRecord.setCreationTimestamp(timestamp);
        codeListRecord.setLastUpdateTimestamp(timestamp);
        codeListRecord.setPrevCodeListId(prevCodeListId);

        CodeListRecord nextCodeList = dslContext.insertInto(CODE_LIST)
                .set(codeListRecord)
                .returning().fetchOne();

        dslContext.update(CODE_LIST)
                .set(CODE_LIST.NEXT_CODE_LIST_ID, nextCodeList.getCodeListId())
                .where(CODE_LIST.CODE_LIST_ID.eq(prevCodeListId))
                .execute();

        dslContext.update(CODE_LIST_MANIFEST)
                .set(CODE_LIST_MANIFEST.CODE_LIST_ID, nextCodeList.getCodeListId())
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(manifestRecord.getCodeListManifestId()))
                .execute();

        List<CodeListValue> codeListValues = codeList.getCodeListValues();
        if (CodeListState.Published.name().equals(state)) {
            codeListValues.stream().forEach(e -> {
                if (!e.isUsed()) {
                    e.setLocked(true);
                }
            });
        }

        update(user, nextCodeList, manifestRecord, codeListValues);
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
    public void delete(long codeListId) {
        ensureProperDeleteCodeListRequest(ULong.valueOf(codeListId));

        dslContext.deleteFrom(CODE_LIST_VALUE)
                .where(CODE_LIST_VALUE.CODE_LIST_ID.eq(ULong.valueOf(codeListId)))
                .execute();

        dslContext.deleteFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(ULong.valueOf(codeListId)))
                .execute();
    }

    @Transactional
    public void delete(List<Long> codeListIds) {
        codeListIds.stream().forEach(e -> delete(e));
    }

    private void ensureProperDeleteCodeListRequest(ULong codeListId) {
        String state = dslContext.select(CODE_LIST.STATE)
                .from(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListId))
                .fetchOptionalInto(String.class).orElse(null);

        if (state == null) {
            throw new IllegalArgumentException();
        }
        CodeListState codeListState = CodeListState.valueOf(state);
        if (WIP != codeListState) {
            throw new DataAccessForbiddenException("Not allowed to delete the code list in '" + codeListState + "' state.");
        }
    }

    public boolean hasSameCodeList(SameCodeListParams params) {
        List<Condition> conditions = new ArrayList();
        conditions.add(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(params.getReleaseId())));

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
        conditions.add(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(params.getReleaseId())));

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
