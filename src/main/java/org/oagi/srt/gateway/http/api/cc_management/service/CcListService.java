package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.info.data.SummaryCcExt;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repository.ReleaseRepository;
import org.oagi.srt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CcListRepository repository;

    @Autowired
    private CcNodeService ccNodeService;

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private DSLContext dslContext;

    public PageResponse<CcList> getCcList(CcListRequest request) {
        List<CcList> ccLists = getCoreComponents(request);
        Stream<CcList> ccListStream = ccLists.stream();
        Comparator<CcList> comparator = getComparator(request.getPageRequest());
        if (comparator != null) {
            ccListStream = ccListStream.sorted(comparator);
        }

        PageResponse<CcList> pageResponse = getPageResponse(
                ccListStream.collect(Collectors.toList()), request.getPageRequest());
        return pageResponse;
    }

    private List<CcList> getCoreComponents(CcListRequest request) {
        request.setUsernameMap(userRepository.getUsernameMap());

        List<CcList> coreComponents = new ArrayList();
        coreComponents.addAll(repository.getAccList(request));
        coreComponents.addAll(repository.getAsccList(request));
        coreComponents.addAll(repository.getBccList(request));
        coreComponents.addAll(repository.getAsccpList(request));
        coreComponents.addAll(repository.getBccpList(request));
        coreComponents.addAll(repository.getBdtList(request));

        return coreComponents;
    }

    private Comparator<CcList> getComparator(PageRequest pageRequest) {
        Comparator<CcList> comparator = null;
        switch (pageRequest.getSortActive()) {
            case "den":
                comparator = Comparator.comparing(CcList::getDen);
                break;

            case "lastUpdateTimestamp":
                comparator = Comparator.comparing(CcList::getLastUpdateTimestamp);
                break;
        }

        if (comparator != null) {
            switch (pageRequest.getSortDirection()) {
                case "desc":
                    comparator = comparator.reversed();
                    break;
            }
        }

        return comparator;
    }

    private PageResponse<CcList> getPageResponse(List<CcList> list, PageRequest page) {
        PageResponse pageResponse = new PageResponse();

        int pageIndex = page.getPageIndex();
        pageResponse.setPage(pageIndex);

        int pageSize = page.getPageSize();
        pageResponse.setSize(pageSize);

        pageResponse.setLength(list.size());

        if (pageIndex < 0 || pageSize <= 0) {
            pageResponse.setList(Collections.emptyList());
        } else {
            if (list.size() > pageSize) {
                List<List<CcList>> partition = Lists.partition(list, pageSize);
                if (partition.size() > pageIndex) {
                    list = Lists.partition(list, pageSize).get(pageIndex);
                } else {
                    list = Lists.partition(list, pageSize).get(0);
                    pageResponse.setPage(0);
                }
            }
            pageResponse.setList(list);
        }

        return pageResponse;
    }

    public ACC getAcc(long id) {
        return dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ACC.class);
    }

    @Transactional
    public void transferOwnership(User user, String type, long manifestId, String targetLoginId) {
        long targetAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.equalIgnoreCase(targetLoginId))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (targetAppUserId == 0L) {
            throw new IllegalArgumentException("Not found a target user.");
        }

        ULong target = ULong.valueOf(targetAppUserId);
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();

        switch (type) {
            case "ACC":
                AccManifestRecord accManifest = manifestRepository.getAccManifestById(ULong.valueOf(manifestId));
                if (accManifest == null) {
                    throw new IllegalArgumentException("Not found a target ACC.");
                }

                ccNodeService.updateAccOwnerUserId(accManifest.getAccManifestId(), target, userId, timestamp);
                break;

            case "ASCCP":
                AsccpManifestRecord asccpManifest = manifestRepository.getAsccpManifestById(ULong.valueOf(manifestId));
                if (asccpManifest == null) {
                    throw new IllegalArgumentException("Not found a target ASCCP.");
                }

                ccNodeService.updateAsccpOwnerUserId(asccpManifest.getAsccpManifestId(), target, userId, timestamp);
                break;

            case "BCCP":
                BccpManifestRecord bccpManifest = manifestRepository.getBccpManifestById(ULong.valueOf(manifestId));
                if (bccpManifest == null) {
                    throw new IllegalArgumentException("Not found a target ASCCP.");
                }

                ccNodeService.updateBccpOwnerUserId(bccpManifest.getBccpManifestId(), target, userId, timestamp);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    public List<SummaryCcExt> getMyExtensionsUnusedInBIEs(User user) {
        long requesterId = sessionService.userId(user);

        Release workingRelease = releaseRepository.getWorkingRelease();

        List<ULong> uegIds = dslContext.select(ACC.ACC_ID.as("id"))
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .where(and(
                        ACC.OAGIS_COMPONENT_TYPE.eq(OagisComponentType.UserExtensionGroup.getValue()),
                        ACC_MANIFEST.RELEASE_ID.greaterThan(ULong.valueOf(workingRelease.getReleaseId())),
                        ACC.OWNER_USER_ID.eq(ULong.valueOf(requesterId))
                ))
                .fetchInto(ULong.class);

        byte isUsed = (byte) 0;

        List<SummaryCcExt> summaryCcExtListForAscc = dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.GUID,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.STATE,
                Tables.ACC.LAST_UPDATE_TIMESTAMP,
                APP_USER.LOGIN_ID,
                APP_USER.APP_USER_ID,
                APP_USER.as("updater").LOGIN_ID,
                TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                TOP_LEVEL_ABIE.STATE,
                ASCCP.as("bie").PROPERTY_TERM,
                ASCCP.PROPERTY_TERM,
                ASBIE.SEQ_KEY)
                .from(ASCC)
                .join(ASCC_MANIFEST).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
                .join(ACC).on(ASCC.FROM_ACC_ID.eq(ACC.ACC_ID))
                .join(ASBIE).on(and(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ASBIE.BASED_ASCC_MANIFEST_ID), ASBIE.IS_USED.eq(isUsed)))
                .join(ASCCP).on(ASCC.TO_ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(APP_USER).on(ACC.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .join(APP_USER.as("updater")).on(ACC.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .join(TOP_LEVEL_ABIE).on(ASBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID))
                .join(ABIE).on(TOP_LEVEL_ABIE.ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASBIEP).on(ABIE.ABIE_ID.eq(ASBIEP.ROLE_OF_ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASBIEP.BASED_ASCCP_MANIFEST_ID))
                .join(ASCCP.as("bie")).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.as("bie").ASCCP_ID))
                .where(ACC.ACC_ID.in(uegIds))
                .fetchStream().map(e -> {
                    SummaryCcExt item = new SummaryCcExt();
                    item.setAccId(e.get(Tables.ACC.ACC_ID).toBigInteger());
                    item.setGuid(e.get(Tables.ACC.GUID));
                    item.setObjectClassTerm(e.get(Tables.ACC.OBJECT_CLASS_TERM));
                    item.setState(CcState.valueOf(e.get(Tables.ACC.STATE)));
                    item.setLastUpdateTimestamp(e.get(Tables.ACC.LAST_UPDATE_TIMESTAMP));
                    item.setLastUpdateUser(e.get(APP_USER.as("updater").LOGIN_ID));
                    item.setOwnerUsername(e.get(APP_USER.LOGIN_ID));
                    item.setOwnerUserId(e.get(APP_USER.APP_USER_ID).toBigInteger());
                    item.setTopLevelAbieId(e.get(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID).toBigInteger());
                    item.setBieState(BieState.valueOf(e.get(TOP_LEVEL_ABIE.STATE).intValue()));
                    item.setPropertyTerm(e.get(ASCCP.as("bie").PROPERTY_TERM));
                    item.setAssociationPropertyTerm(e.get(ASCCP.PROPERTY_TERM));
                    item.setSeqKey(e.get(ASBIE.SEQ_KEY).intValue());
                    return item;
                }).collect(Collectors.toList());

        List<SummaryCcExt> summaryCcExtListForBcc = dslContext.select(
                Tables.ACC.ACC_ID,
                Tables.ACC.GUID,
                Tables.ACC.OBJECT_CLASS_TERM,
                Tables.ACC.STATE,
                Tables.ACC.LAST_UPDATE_TIMESTAMP,
                APP_USER.LOGIN_ID,
                APP_USER.APP_USER_ID,
                APP_USER.as("updater").LOGIN_ID,
                TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                TOP_LEVEL_ABIE.STATE,
                ASCCP.as("bie").PROPERTY_TERM,
                BCCP.PROPERTY_TERM,
                BBIE.SEQ_KEY)
                .from(BCC)
                .join(ACC).on(BCC.FROM_ACC_ID.eq(ACC.ACC_ID))
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .join(BBIE).on(and(BCC_MANIFEST.BCC_MANIFEST_ID.eq(BBIE.BASED_BCC_MANIFEST_ID), BBIE.IS_USED.eq(isUsed)))
                .join(BCCP).on(BCC.TO_BCCP_ID.eq(BCCP.BCCP_ID))
                .join(APP_USER).on(ACC.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .join(APP_USER.as("updater")).on(ACC.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID))
                .join(TOP_LEVEL_ABIE).on(BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID))
                .join(ABIE).on(TOP_LEVEL_ABIE.ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASBIEP).on(ABIE.ABIE_ID.eq(ASBIEP.ROLE_OF_ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP.as("bie")).on(ASCCP.as("bie").ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ACC.ACC_ID.in(uegIds))
                .fetchStream().map(e -> {
                    SummaryCcExt item = new SummaryCcExt();
                    item.setAccId(e.get(Tables.ACC.ACC_ID).toBigInteger());
                    item.setGuid(e.get(Tables.ACC.GUID));
                    item.setObjectClassTerm(e.get(Tables.ACC.OBJECT_CLASS_TERM));
                    item.setState(CcState.valueOf(e.get(Tables.ACC.STATE)));
                    item.setLastUpdateTimestamp(e.get(Tables.ACC.LAST_UPDATE_TIMESTAMP));
                    item.setLastUpdateUser(e.get(APP_USER.as("updater").LOGIN_ID));
                    item.setOwnerUsername(e.get(APP_USER.LOGIN_ID));
                    item.setOwnerUserId(e.get(APP_USER.APP_USER_ID).toBigInteger());
                    item.setTopLevelAbieId(e.get(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID).toBigInteger());
                    item.setBieState(BieState.valueOf(e.get(TOP_LEVEL_ABIE.STATE).intValue()));
                    item.setPropertyTerm(e.get(ASCCP.as("bie").PROPERTY_TERM));
                    item.setAssociationPropertyTerm(e.get(BCCP.PROPERTY_TERM));
                    item.setSeqKey(e.get(BBIE.SEQ_KEY).intValue());
                    return item;
                }).collect(Collectors.toList());

        Set<SummaryCcExt> set = new HashSet();
        set.addAll(summaryCcExtListForAscc);
        set.addAll(summaryCcExtListForBcc);

        List<SummaryCcExt> result = new ArrayList(set);
        result.sort((o1, o2) -> {
            int compFirst = o1.getAccId().compareTo(o2.getAccId());
            if (compFirst == 0) {
                return Integer.compare(o1.getSeqKey(), o2.getSeqKey());
            }
            return compFirst;
        });
        return result;
    }
}

