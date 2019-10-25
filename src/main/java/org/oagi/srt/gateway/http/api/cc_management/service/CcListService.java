package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.srt.entity.jooq.Tables.ACC;

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

    public ACC getAcc(long id) {
        return dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ACC.class);
    }
}

