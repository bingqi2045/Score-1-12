package org.oagi.score.service.codelist;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.codelist.model.CodeListUpliftingRequest;
import org.oagi.score.service.codelist.model.CodeListUpliftingResponse;
import org.oagi.score.service.log.LogRepository;
import org.oagi.score.service.log.model.LogAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.utils.StringUtils.hasLength;

@Service
@Transactional(readOnly = true)
public class CodeListUpliftingService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private LogRepository logRepository;

    @Transactional
    public CodeListUpliftingResponse upliftCodeList(CodeListUpliftingRequest request) {
        ScoreUser requester = request.getRequester();

        CodeListManifestRecord codeListManifest = dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(request.getCodeListManifestId())))
                .fetchOptional().orElse(null);

        List<CodeListValueManifestRecord> codeListValueManifestList = dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(request.getCodeListManifestId())))
                .fetch();

        CodeListRecord codeList = dslContext.selectFrom(CODE_LIST)
                .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifest.getCodeListId()))
                .fetchOptional().orElse(null);

        List<CodeListValueRecord> codeListValueList = dslContext.selectFrom(CODE_LIST_VALUE)
                .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.in(
                        codeListValueManifestList.stream().map(e -> e.getCodeListValueId()).collect(Collectors.toList())
                ))
                .fetch();

        ULong targetReleaseId = ULong.valueOf(request.getTargetReleaseId());
        LocalDateTime timestamp = LocalDateTime.now();
        CodeListManifestRecord newCodeListManifest = new CodeListManifestRecord();
        newCodeListManifest.setReleaseId(targetReleaseId);

        CodeListRecord newCodeList;

        // Issue #1073
        // If the source CL has the base CL, all CL values should be copying from the base again.
        if (codeListManifest.getBasedCodeListManifestId() != null) {
            ULong sourceBasedCodeListManifestId = codeListManifest.getBasedCodeListManifestId();
            CodeListManifestRecord targetBasedCodeListManifest = dslContext.selectFrom(CODE_LIST_MANIFEST)
                    .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(sourceBasedCodeListManifestId))
                    .fetchOptional().orElse(null);

            // Find a target code list manifest recursively
            while (targetBasedCodeListManifest != null && !targetBasedCodeListManifest.getReleaseId().equals(targetReleaseId)) {
                targetBasedCodeListManifest = dslContext.selectFrom(CODE_LIST_MANIFEST)
                        .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(targetBasedCodeListManifest.getNextCodeListManifestId()))
                        .fetchOptional().orElse(null);
            }

            if (targetBasedCodeListManifest == null) {
                throw new IllegalStateException();
            }

            newCodeList = copyCodeList(codeList, requester, timestamp);
            newCodeList.setBasedCodeListId(targetBasedCodeListManifest.getCodeListId());
            newCodeList.setCodeListId(
                    dslContext.insertInto(CODE_LIST)
                            .set(newCodeList)
                            .returning(CODE_LIST.CODE_LIST_ID)
                            .fetchOne().getCodeListId()
            );

            newCodeListManifest.setCodeListId(newCodeList.getCodeListId());
            newCodeListManifest.setBasedCodeListManifestId(targetBasedCodeListManifest.getCodeListManifestId());
            newCodeListManifest.setCodeListManifestId(
                    dslContext.insertInto(CODE_LIST_MANIFEST)
                            .set(newCodeListManifest)
                            .returning(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID)
                            .fetchOne().getCodeListManifestId()
            );

            List<CodeListValueManifestRecord> targetBasedCodeListValueManifestList = dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                    .where(CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID.eq(targetBasedCodeListManifest.getCodeListManifestId()))
                    .fetch();

            List<CodeListValueRecord> targetBasedCodeListValueList = dslContext.selectFrom(CODE_LIST_VALUE)
                    .where(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.in(
                            targetBasedCodeListValueManifestList.stream().map(e -> e.getCodeListValueId()).collect(Collectors.toList())
                    ))
                    .fetch();

            // Use case-insensitive a value set to prevent duplicated CL values
            Set<String> basedCodeListValueSet = new HashSet();
            targetBasedCodeListValueList.forEach(e -> {
                CodeListValueRecord newCodeListValue = copyCodeListValue(e, newCodeList, requester, timestamp);
                newCodeListValue.setCodeListValueId(
                        dslContext.insertInto(CODE_LIST_VALUE)
                                .set(newCodeListValue)
                                .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID)
                                .fetchOne().getCodeListValueId()
                );

                CodeListValueManifestRecord newCodeListValueManifest = new CodeListValueManifestRecord();
                newCodeListValueManifest.setReleaseId(targetReleaseId);
                newCodeListValueManifest.setCodeListValueId(newCodeListValue.getCodeListValueId());
                newCodeListValueManifest.setCodeListManifestId(newCodeListManifest.getCodeListManifestId());
                dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                        .set(newCodeListValueManifest)
                        .execute();

                basedCodeListValueSet.add(newCodeListValue.getValue().toLowerCase());
            });

            codeListValueList.stream().forEach(e -> {
                if (basedCodeListValueSet.contains(e.getValue().toLowerCase())) {
                    return;
                }

                CodeListValueRecord newCodeListValue = copyCodeListValue(e, newCodeList, requester, timestamp);
                newCodeListValue.setCodeListValueId(
                        dslContext.insertInto(CODE_LIST_VALUE)
                                .set(newCodeListValue)
                                .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID)
                                .fetchOne().getCodeListValueId()
                );

                CodeListValueManifestRecord newCodeListValueManifest = new CodeListValueManifestRecord();
                newCodeListValueManifest.setReleaseId(targetReleaseId);
                newCodeListValueManifest.setCodeListValueId(newCodeListValue.getCodeListValueId());
                newCodeListValueManifest.setCodeListManifestId(newCodeListManifest.getCodeListManifestId());
                dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                        .set(newCodeListValueManifest)
                        .execute();
            });

        } else {
            newCodeList = copyCodeList(codeList, requester, timestamp);
            newCodeList.setCodeListId(
                    dslContext.insertInto(CODE_LIST)
                            .set(newCodeList)
                            .returning(CODE_LIST.CODE_LIST_ID)
                            .fetchOne().getCodeListId()
            );

            newCodeListManifest.setCodeListId(newCodeList.getCodeListId());
            newCodeListManifest.setCodeListManifestId(
                    dslContext.insertInto(CODE_LIST_MANIFEST)
                            .set(newCodeListManifest)
                            .returning(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID)
                            .fetchOne().getCodeListManifestId()
            );

            codeListValueList.stream().forEach(e -> {
                CodeListValueRecord newCodeListValue = copyCodeListValue(e, newCodeList, requester, timestamp);
                newCodeListValue.setCodeListValueId(
                        dslContext.insertInto(CODE_LIST_VALUE)
                                .set(newCodeListValue)
                                .returning(CODE_LIST_VALUE.CODE_LIST_VALUE_ID)
                                .fetchOne().getCodeListValueId()
                );

                CodeListValueManifestRecord newCodeListValueManifest = new CodeListValueManifestRecord();
                newCodeListValueManifest.setReleaseId(targetReleaseId);
                newCodeListValueManifest.setCodeListValueId(newCodeListValue.getCodeListValueId());
                newCodeListValueManifest.setCodeListManifestId(newCodeListManifest.getCodeListManifestId());
                dslContext.insertInto(CODE_LIST_VALUE_MANIFEST)
                        .set(newCodeListValueManifest)
                        .execute();
            });
        }

        LogRecord logRecord = logRepository.insertCodeListLog(newCodeListManifest,
                newCodeList,
                LogAction.Added,
                ULong.valueOf(requester.getUserId()),
                timestamp);
        newCodeListManifest.setLogId(logRecord.getLogId());
        dslContext.update(CODE_LIST_MANIFEST)
                .set(CODE_LIST_MANIFEST.LOG_ID, logRecord.getLogId())
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(newCodeListManifest.getCodeListManifestId()))
                .execute();

        CodeListUpliftingResponse response = new CodeListUpliftingResponse();
        response.setCodeListManifestId(newCodeListManifest.getCodeListManifestId().toBigInteger());
        return response;
    }

    private CodeListValueRecord copyCodeListValue(CodeListValueRecord codeListValue,
                                                  CodeListRecord newCodeList,
                                                  ScoreUser requester, LocalDateTime timestamp) {
        CodeListValueRecord newCodeListValue = new CodeListValueRecord();
        newCodeListValue.setGuid(ScoreGuidUtils.randomGuid());
        newCodeListValue.setCodeListId(newCodeList.getCodeListId());
        newCodeListValue.setValue(codeListValue.getValue());
        newCodeListValue.setMeaning(codeListValue.getMeaning());
        newCodeListValue.setDefinition(codeListValue.getDefinition());
        newCodeListValue.setDefinitionSource(codeListValue.getDefinitionSource());
        newCodeListValue.setUsedIndicator(codeListValue.getUsedIndicator());
        newCodeListValue.setLockedIndicator(codeListValue.getLockedIndicator());
        newCodeListValue.setExtensionIndicator(codeListValue.getExtensionIndicator());
        newCodeListValue.setIsDeprecated(codeListValue.getIsDeprecated());
        newCodeListValue.setOwnerUserId(ULong.valueOf(requester.getUserId()));
        newCodeListValue.setCreatedBy(ULong.valueOf(requester.getUserId()));
        newCodeListValue.setCreationTimestamp(timestamp);
        newCodeListValue.setLastUpdatedBy(ULong.valueOf(requester.getUserId()));
        newCodeListValue.setLastUpdateTimestamp(timestamp);
        return newCodeListValue;
    }

    private CodeListRecord copyCodeList(CodeListRecord codeList,
                                        ScoreUser requester, LocalDateTime timestamp) {
        CodeListRecord newCodeList = new CodeListRecord();
        newCodeList.setGuid(ScoreGuidUtils.randomGuid());
        if (hasLength(codeList.getEnumTypeGuid())) {
            newCodeList.setEnumTypeGuid(ScoreGuidUtils.randomGuid());
        }
        newCodeList.setBasedCodeListId(codeList.getBasedCodeListId());
        newCodeList.setName(codeList.getName());
        newCodeList.setListId(codeList.getListId());
        newCodeList.setAgencyId(codeList.getAgencyId());
        newCodeList.setVersionId(codeList.getVersionId());
        newCodeList.setRemark(codeList.getRemark());
        newCodeList.setDefinition(codeList.getDefinition());
        newCodeList.setDefinitionSource(codeList.getDefinitionSource());
        newCodeList.setNamespaceId(codeList.getNamespaceId());
        newCodeList.setExtensibleIndicator(codeList.getExtensibleIndicator());
        newCodeList.setIsDeprecated(codeList.getIsDeprecated());
        newCodeList.setOwnerUserId(ULong.valueOf(requester.getUserId()));
        newCodeList.setCreatedBy(ULong.valueOf(requester.getUserId()));
        newCodeList.setCreationTimestamp(timestamp);
        newCodeList.setLastUpdatedBy(ULong.valueOf(requester.getUserId()));
        newCodeList.setLastUpdateTimestamp(timestamp);
        newCodeList.setState(CcState.WIP.name());
        return newCodeList;
    }
}
