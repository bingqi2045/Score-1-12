package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.ASCC;
import org.oagi.srt.data.ASCCP;
import org.oagi.srt.data.BCCP;
import org.oagi.srt.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CcListRepository repository;

    @Autowired
    private DSLContext dslContext;

    public PageResponse<CcList> getCcList(long releaseId, CcListTypes types,
                                          List<CcState> states, List<String> loginIds,
                                          String den, String definition, String module,
                                          PageRequest pageRequest) {

        List<CcList> ccLists = getCoreComponents(releaseId, types, states, loginIds, den, definition, module);
        Stream<CcList> ccListStream = ccLists.stream();

        Comparator<CcList> comparator = getComparator(pageRequest);
        if (comparator != null) {
            ccListStream = ccListStream.sorted(comparator);
        }

        PageResponse<CcList> pageResponse = getPageResponse(ccListStream.collect(Collectors.toList()), pageRequest);
        return pageResponse;
    }

    private List<CcList> getCoreComponents(long releaseId,
                                           CcListTypes types,
                                           List<CcState> states,
                                           List<String> loginIds,
                                           String den,
                                           String definition,
                                           String module) {

        List<CcList> coreComponents = new ArrayList();
        if (types.isAcc()) {
            coreComponents.addAll(repository.getAccList(releaseId).stream()
                    .filter(acc -> {
                        if (acc.getOagisComponentType() == OagisComponentType.UserExtensionGroup) {
                            if (releaseId > 0L) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .filter(e -> states.isEmpty() ? true : states.contains(e.getState()))
                    .filter(e -> loginIds.isEmpty() ? true : loginIds.contains(e.getOwner()))
                    .filter(getDenFilter(den))
                    .filter(getDefinitionFilter(definition))
                    .filter(getModuleFilter(module))
                    .collect(Collectors.toList()));
        }

        if (types.isAscc() && StringUtils.isEmpty(module)) {
            coreComponents.addAll(repository.getAsccList(releaseId).stream()
                    .filter(ascc -> (!ascc.getDen().endsWith("User Extension Group")))
                    .filter(e -> states.isEmpty() ? true : states.contains(e.getState()))
                    .filter(e -> loginIds.isEmpty() ? true : loginIds.contains(e.getOwner()))
                    .filter(getDenFilter(den))
                    .filter(getDefinitionFilter(definition))
                    .collect(Collectors.toList()));
        }

        if (types.isBcc() && StringUtils.isEmpty(module)) {
            coreComponents.addAll(repository.getBccList(releaseId).stream()
                    .filter(e -> states.isEmpty() ? true : states.contains(e.getState()))
                    .filter(e -> loginIds.isEmpty() ? true : loginIds.contains(e.getOwner()))
                    .filter(getDenFilter(den))
                    .filter(getDefinitionFilter(definition))
                    .collect(Collectors.toList()));
        }

        if (types.isAsccp()) {
            coreComponents.addAll(repository.getAsccpList(releaseId).stream()
                    .filter(asccp -> (!asccp.getDen().endsWith("User Extension Group")))
                    .filter(e -> states.isEmpty() ? true : states.contains(e.getState()))
                    .filter(e -> loginIds.isEmpty() ? true : loginIds.contains(e.getOwner()))
                    .filter(getDenFilter(den))
                    .filter(getDefinitionFilter(definition))
                    .filter(getModuleFilter(module))
                    .collect(Collectors.toList()));
        }

        if (types.isBccp()) {
            coreComponents.addAll(repository.getBccpList(releaseId).stream()
                    .filter(e -> states.isEmpty() ? true : states.contains(e.getState()))
                    .filter(e -> loginIds.isEmpty() ? true : loginIds.contains(e.getOwner()))
                    .filter(getDenFilter(den))
                    .filter(getDefinitionFilter(definition))
                    .filter(getModuleFilter(module))
                    .collect(Collectors.toList()));
        }
        return coreComponents;
    }

    private Predicate<CcList> getDenFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            List<String> filters = Arrays.asList(filter.toLowerCase().split(" ")).stream()
                    .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());

            return coreComponent -> {
                List<String> den = Arrays.asList(coreComponent.getDen().toLowerCase().split(" ")).stream()
                        .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());

                for (String partialFilter : filters) {
                    if (!den.contains(partialFilter)) {
                        return false;
                    }
                }

                return true;
            };
        } else {
            return coreComponent -> true;
        }
    }

    private Predicate<CcList> getDefinitionFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            return coreComponent -> {
                String definition = coreComponent.getDefinition();
                if (StringUtils.isEmpty(definition)) {
                    return false;
                }
                return definition.toLowerCase().contains(filter.toLowerCase());
            };
        } else {
            return coreComponent -> true;
        }
    }

    private Predicate<CcList> getModuleFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            return coreComponent -> {
                String module = coreComponent.getModule();
                if (StringUtils.isEmpty(module)) {
                    return false;
                }
                return module.toLowerCase().contains(filter.toLowerCase());
            };
        } else {
            return coreComponent -> true;
        }
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
        return dslContext.select(ASCCP.ASCCP_ID,
                ASCCP.PROPERTY_TERM,
                ASCCP.GUID,
                MODULE.MODULE_.as("module"),
                ASCCP.DEFINITION,
                ASCCP.IS_DEPRECATED.as("deprecated"),
                ASCCP.STATE,
                ASCCP.RELEASE_ID,
                ASCCP.REVISION_NUM,
                ASCCP.REVISION_TRACKING_NUM
                ).from(ASCCP.join(MODULE).on(ASCCP.MODULE_ID.eq(MODULE.MODULE_ID)))
                .where(
                        and(ASCCP.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId)),
                                ASCCP.STATE.eq(CcState.Published.getValue()))
                ).fetchInto(AsccpForAppendAsccp.class).stream().collect(groupingBy(AsccpForAppendAsccp::getGuid))
                .values().stream().map(e -> CcUtility.getLatestEntity(releaseId, e)).collect(Collectors.toList());
    }

    public List<BccpForAppendBccp> getBccpForAppendBccpList(User user, long releaseId, long extensionId) {
        return dslContext.select(BCCP.BCCP_ID,
                BCCP.PROPERTY_TERM,
                BCCP.GUID,
                MODULE.MODULE_.as("module"),
                BCCP.DEFINITION,
                BCCP.IS_DEPRECATED.as("deprecated"),
                BCCP.STATE,
                BCCP.RELEASE_ID,
                BCCP.REVISION_NUM,
                BCCP.REVISION_TRACKING_NUM
        ).from(BCCP.join(MODULE).on(BCCP.MODULE_ID.eq(MODULE.MODULE_ID)))
                .where(
                        and(BCCP.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId)),
                                BCCP.STATE.eq(CcState.Published.getValue()))
                ).fetchInto(BccpForAppendBccp.class).stream().collect(groupingBy(BccpForAppendBccp::getGuid))
                .values().stream().map(e -> CcUtility.getLatestEntity(releaseId, e)).collect(Collectors.toList());
    }

    public ASCCP getAsccp(long id) {
        return dslContext.selectFrom(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ASCCP.class);
    }

    public ACC getAcc(long id) {
        return dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ACC.class);
    }

    public ACC getAccByCurrentAccId(long currentAccId, long releaseId) {
        List<ACC> accList = dslContext.selectFrom(ACC)
                .where(and(
                        ACC.REVISION_NUM.greaterThan(0),
                        ACC.CURRENT_ACC_ID.eq(ULong.valueOf(currentAccId)),
                        ACC.RELEASE_ID.lessOrEqual(ULong.valueOf(releaseId))
                ))
                .fetchInto(ACC.class);
        return CcUtility.getLatestEntity(releaseId, accList);
    }

    public BCCP getBccp(long id) {
        return dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(BCCP.class);
    }

    public ASCC getAscc(long id) {
        return dslContext.selectFrom(ASCC)
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(ASCC.class);
    }

    public org.oagi.srt.data.BCC getBcc(long id) {
        return dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(ULong.valueOf(id)))
                .fetchOneInto(org.oagi.srt.data.BCC.class);
    }
}

