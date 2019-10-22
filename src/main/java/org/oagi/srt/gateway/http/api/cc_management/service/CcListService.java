package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
                list = Lists.partition(list, pageSize).get(pageIndex);
            }
            pageResponse.setList(list);
        }

        return pageResponse;
    }

    public List<AsccpForAppendAsccp> getAsccpForAppendAsccpList(User user, long releaseId, long extensionId) {
        return dslContext.select(
                ASCCP.ASCCP_ID,
                ASCCP.PROPERTY_TERM,
                ASCCP.GUID,
                MODULE.MODULE_.as("module"),
                ASCCP.DEFINITION,
                ASCCP.IS_DEPRECATED.as("deprecated"),
                ASCCP.STATE,
                ASCCP_RELEASE_MANIFEST.RELEASE_ID,
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM)
                .from(ASCCP)
                .join(ASCCP_RELEASE_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_RELEASE_MANIFEST.ASCCP_ID))
                .leftJoin(MODULE).on(ASCCP.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(and(
                        ASCCP_RELEASE_MANIFEST.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId)),
                        ASCCP.STATE.eq(CcState.Published.getValue()))
                ).fetchInto(AsccpForAppendAsccp.class)
                .stream().collect(Collectors.toList());
    }

    public List<BccpForAppendBccp> getBccpForAppendBccpList(User user, long releaseId, long extensionId) {
        return dslContext.select(
                BCCP.BCCP_ID,
                BCCP.PROPERTY_TERM,
                BCCP.GUID,
                MODULE.MODULE_.as("module"),
                BCCP.DEFINITION,
                BCCP.IS_DEPRECATED.as("deprecated"),
                BCCP.STATE,
                BCCP_RELEASE_MANIFEST.RELEASE_ID,
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM)
                .from(BCCP)
                .join(BCCP_RELEASE_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_RELEASE_MANIFEST.BCCP_ID))
                .leftJoin(MODULE).on(BCCP.MODULE_ID.eq(MODULE.MODULE_ID))
                .where(and(
                        BCCP_RELEASE_MANIFEST.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId)),
                        BCCP.STATE.eq(CcState.Published.getValue()))
                ).fetchInto(BccpForAppendBccp.class)
                .stream().collect(Collectors.toList());
    }

    public ACC getAcc(long id) {
        return dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ACC.class);
    }
}

