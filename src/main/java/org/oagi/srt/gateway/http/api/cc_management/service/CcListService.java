package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.srt.entity.jooq.Tables.ACC;
import static org.oagi.srt.entity.jooq.Tables.APP_USER;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CcListRepository repository;

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

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

                ULong accId = repository.updateAccOwnerUserId(accManifest, target, userId, timestamp);
                repository.updateAsccOwnerUserId(accManifest.getAccManifestId(), accId, target, userId, timestamp);
                repository.updateBccOwnerUserId(accManifest.getAccManifestId(), accId, target, userId, timestamp);
                break;

            case "ASCCP":
                AsccpManifestRecord asccpManifest = manifestRepository.getAsccpManifestById(ULong.valueOf(manifestId));
                if (asccpManifest == null) {
                    throw new IllegalArgumentException("Not found a target ASCCP.");
                }

                repository.updateAsccpOwnerUserId(asccpManifest, target, userId, timestamp);
                break;

            case "BCCP":
                BccpManifestRecord bccpManifest = manifestRepository.getBccpManifestById(ULong.valueOf(manifestId));
                if (bccpManifest == null) {
                    throw new IllegalArgumentException("Not found a target ASCCP.");
                }

                repository.updateBccpOwnerUserId(bccpManifest, target, userId, timestamp);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }
}

