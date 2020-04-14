package org.oagi.srt.gateway.http.api.code_list_management.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.CodeListManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.CodeListRecord;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.code_list_management.data.*;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            Record14<ULong, String, String, ULong, String,
                    String, ULong, String, String, LocalDateTime,
                    String, String, Byte, String>> getSelectOnConditionStep() {
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
                CODE_LIST.STATE)
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
                Record14<ULong, String, String, ULong, String,
                        String, ULong, String, String, LocalDateTime,
                        String, String, Byte, String>> step = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        conditions.add(CODE_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));

        if (!StringUtils.isEmpty(request.getName())) {
            conditions.addAll(contains(request.getName(), CODE_LIST.NAME));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(CODE_LIST.STATE.in(request.getStates()));
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

        SelectConnectByStep<Record14<ULong, String, String, ULong, String,
                        String, ULong, String, String, LocalDateTime,
                        String, String, Byte, String>> conditionStep = step;
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


        SelectWithTiesAfterOffsetStep<Record14<ULong, String, String, ULong, String,
                        String, ULong, String, String, LocalDateTime,
                        String, String, Byte, String>> offsetStep = null;
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

        boolean isPublished = CodeListState.Published.name().equals(codeList.getState());
        List<Condition> conditions = new ArrayList();
        conditions.add(CODE_LIST_VALUE.CODE_LIST_ID.eq(ULong.valueOf(manifestId)));
        if (isPublished) {
            conditions.add(CODE_LIST_VALUE.LOCKED_INDICATOR.eq((byte) 0));
        }

        List<CodeListValue> codeListValues = dslContext.select(
                CODE_LIST_VALUE.CODE_LIST_VALUE_ID,
                CODE_LIST_VALUE.VALUE,
                CODE_LIST_VALUE.NAME,
                CODE_LIST_VALUE.DEFINITION,
                CODE_LIST_VALUE.DEFINITION_SOURCE,
                CODE_LIST_VALUE.USED_INDICATOR.as("used"),
                CODE_LIST_VALUE.LOCKED_INDICATOR.as("locked"),
                CODE_LIST_VALUE.EXTENSION_INDICATOR.as("extension"))
                .from(CODE_LIST_VALUE)
                .where(conditions)
                .fetchInto(CodeListValue.class);
        codeList.setCodeListValues(codeListValues);

        return codeList;
    }

    @Transactional
    public void insert(User user, CodeList codeList) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();

        ULong codeListId = dslContext.insertInto(CODE_LIST,
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
                CODE_LIST.REVISION_NUM,
                CODE_LIST.REVISION_TRACKING_NUM,
                CODE_LIST.REVISION_ACTION,
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
                1, 1, RevisionAction.Insert.getValue(),
                userId, userId, userId, timestamp, timestamp)
                .returning(CODE_LIST.CODE_LIST_ID).fetchOne().getValue(CODE_LIST.CODE_LIST_ID);

        CodeListManifestRecord manifestRecord = new CodeListManifestRecord();
        manifestRecord.setReleaseId(ULong.valueOf(codeList.getReleaseId()));
        manifestRecord.setCodeListId(codeListId);
        if (codeList.getBasedCodeListManifestId() != null) {
            manifestRecord.setBasedCodeListManifestId(ULong.valueOf(codeList.getBasedCodeListManifestId()));
        }

        dslContext.insertInto(CODE_LIST_MANIFEST)
                .set(manifestRecord)
                .execute();

        for (CodeListValue codeListValue : codeList.getCodeListValues()) {
            insert(codeListId.longValue(), codeListValue);
        }
    }

    private void insert(long codeListId, CodeListValue codeListValue) {

        boolean locked = codeListValue.isLocked();
        boolean used = codeListValue.isUsed();
        boolean extension = codeListValue.isExtension();
        if (locked) {
            used = false;
            extension = false;
        }

        dslContext.insertInto(CODE_LIST_VALUE,
                CODE_LIST_VALUE.CODE_LIST_ID,
                CODE_LIST_VALUE.VALUE,
                CODE_LIST_VALUE.NAME,
                CODE_LIST_VALUE.DEFINITION,
                CODE_LIST_VALUE.DEFINITION_SOURCE,
                CODE_LIST_VALUE.USED_INDICATOR,
                CODE_LIST_VALUE.LOCKED_INDICATOR,
                CODE_LIST_VALUE.EXTENSION_INDICATOR).values(
                ULong.valueOf(codeListId),
                codeListValue.getValue(),
                codeListValue.getName(),
                codeListValue.getDefinition(),
                codeListValue.getDefinitionSource(),
                (byte) ((used) ? 1 : 0),
                (byte) ((locked) ? 1 : 0),
                (byte) ((extension) ? 1 : 0))
                .execute();
    }

    @Transactional
    public void update(User user, CodeList codeList) {
        String state = codeList.getState();
        CodeListManifestRecord codeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(codeList.getCodeListManifestId())))
                .fetchOne();
        CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
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

        codeListRecord.setRevisionTrackingNum(codeListRecord.getRevisionTrackingNum() + 1);
        codeListRecord.setCreatedBy(requesterId);
        codeListRecord.setOwnerUserId(requesterId);
        codeListRecord.setLastUpdatedBy(requesterId);
        codeListRecord.setCreationTimestamp(timestamp);
        codeListRecord.setLastUpdateTimestamp(timestamp);
        codeListRecord.setPrevCodeListId(prevCodeListId);

        ULong nextCodeListId = dslContext.insertInto(CODE_LIST)
                .set(codeListRecord)
                .returning(CODE_LIST.CODE_LIST_ID).fetchOne().getCodeListId();

        dslContext.update(CODE_LIST)
                .set(CODE_LIST.NEXT_CODE_LIST_ID, nextCodeListId)
                .where(CODE_LIST.CODE_LIST_ID.eq(prevCodeListId))
                .execute();

        dslContext.update(CODE_LIST_MANIFEST)
                .set(CODE_LIST_MANIFEST.CODE_LIST_ID, nextCodeListId)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(codeListManifestRecord.getCodeListManifestId()))
                .execute();

        List<CodeListValue> codeListValues = codeList.getCodeListValues();
        if (CodeListState.Published.name().equals(state)) {
            codeListValues.stream().forEach(e -> {
                if (!e.isUsed()) {
                    e.setLocked(true);
                }
            });
        }

        update(codeList.getCodeListManifestId(), codeListValues);
    }

    @Transactional
    public void update(long codeListId, List<CodeListValue> codeListValues) {
        List<Long> oldCodeListValueIds = dslContext.select(CODE_LIST_VALUE.CODE_LIST_VALUE_ID)
                .from(CODE_LIST_VALUE)
                .where(CODE_LIST_VALUE.CODE_LIST_ID.eq(ULong.valueOf(codeListId)))
                .fetchInto(Long.class);

        Map<Long, CodeListValue> newCodeListValues = codeListValues.stream()
                .filter(e -> e.getCodeListValueId() > 0L)
                .collect(Collectors.toMap(CodeListValue::getCodeListValueId, Function.identity()));

        oldCodeListValueIds.removeAll(newCodeListValues.keySet());
        for (long deleteCodeListValueId : oldCodeListValueIds) {
            delete(codeListId, deleteCodeListValueId);
        }

        for (CodeListValue CodeListValue : newCodeListValues.values()) {
            update(codeListId, CodeListValue);
        }

        for (CodeListValue CodeListValue : codeListValues.stream()
                .filter(e -> e.getCodeListValueId() == 0L)
                .collect(Collectors.toList())) {
            insert(codeListId, CodeListValue);
        }
    }

    @Transactional
    public void update(long codeListId, CodeListValue codeListValue) {
        boolean locked = codeListValue.isLocked();
        boolean used = codeListValue.isUsed();
        boolean extension = codeListValue.isExtension();
        if (locked) {
            used = false;
            extension = false;
        }

        dslContext.update(CODE_LIST_VALUE)
                .set(CODE_LIST_VALUE.VALUE, codeListValue.getValue())
                .set(CODE_LIST_VALUE.NAME, codeListValue.getName())
                .set(CODE_LIST_VALUE.DEFINITION, codeListValue.getDefinition())
                .set(CODE_LIST_VALUE.DEFINITION_SOURCE, codeListValue.getDefinitionSource())
                .set(CODE_LIST_VALUE.USED_INDICATOR, (byte) ((used) ? 1 : 0))
                .set(CODE_LIST_VALUE.LOCKED_INDICATOR, (byte) ((locked) ? 1 : 0))
                .set(CODE_LIST_VALUE.EXTENSION_INDICATOR, (byte) ((extension) ? 1 : 0))
                .where(and(
                        CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(ULong.valueOf(codeListValue.getCodeListValueId())),
                        CODE_LIST_VALUE.CODE_LIST_ID.eq(ULong.valueOf(codeListId))
                ))
                .execute();
    }

    @Transactional
    public void delete(long codeListId, long codeListValueId) {
        ensureProperDeleteCodeListRequest(ULong.valueOf(codeListId));

        dslContext.deleteFrom(CODE_LIST_VALUE)
                .where(and(
                        CODE_LIST_VALUE.CODE_LIST_VALUE_ID.eq(ULong.valueOf(codeListValueId)),
                        CODE_LIST_VALUE.CODE_LIST_ID.eq(ULong.valueOf(codeListId))
                ))
                .execute();
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
