package org.oagi.srt.gateway.http.api.cc_management.service;

import com.google.common.collect.Lists;
import org.oagi.srt.data.ASCCP;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.BCC;
import org.oagi.srt.data.BCCP;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListTypes;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcListRepository;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class CcListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CcListRepository repository;

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    public PageResponse<CcList> getCcList(long releaseId, CcListTypes types, List<CcState> states,
                                          String filter, PageRequest pageRequest) {


        List<CcList> ccLists = getCoreComponents(releaseId, types, states, filter);
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
                                           String filter) {

        List<CcList> coreComponents = new ArrayList();
        if (types.isAcc()) {
            coreComponents.addAll(repository.getAccList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isAscc()) {
            coreComponents.addAll(repository.getAsccList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isBcc()) {
            coreComponents.addAll(repository.getBccList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isAsccp()) {
            coreComponents.addAll(repository.getAsccpList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }

        if (types.isBccp()) {
            coreComponents.addAll(repository.getBccpList(releaseId).stream()
                    .filter(statesFilter(states))
                    .filter(getDenFilter(filter))
                    .collect(Collectors.toList()));
        }
        return coreComponents;
    }

    private Predicate<CcList> statesFilter(List<CcState> states) {
        return coreComponent -> states.contains(coreComponent.getState());
    }

    private Predicate<CcList> getDenFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            List<String> filters = Arrays.asList(filter.toLowerCase().split(" "));

            return coreComponent -> {
                List<String> den = Arrays.asList(coreComponent.getDen().toLowerCase().split(" "));
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

    public ASCCP getAsccp(long id) {
        return jdbcTemplate.queryForObject("SELECT asccp.asccp_id, asccp.guid, " +
                "asccp.property_term, asccp.definition, " +
                "asccp.definition_source, asccp.role_of_acc_id, " +
                "asccp.den, asccp.created_by, asccp.owner_user_id, asccp.last_updated_by, asccp.creation_timestamp, " +
                "asccp.last_update_timestamp, asccp.state, asccp.module_id, asccp.namespace_id, asccp.reusable_indicator, " +
                "asccp.is_deprecated, asccp.revision_num, asccp.revision_tracking_num, asccp.revision_action, asccp.release_id, " +
                "asccp.current_asccp_id, asccp.is_nillable " +
                "FROM asccp " +
                "WHERE asccp_id = :asccp_id", newSqlParameterSource()
                .addValue("asccp_id", id), ASCCP.class);

    }

    public ACC getAcc(long id) {
        return jdbcTemplate.queryForObject("SELECT acc.acc_id, acc.guid, " +
                "acc.object_class_term, acc.den, " +
                "acc.definition, acc.definition_source, acc.based_acc_id, acc.object_class_qualifier, " +
                "acc.oagis_component_type, acc.created_by, acc.owner_user_id, acc.last_updated_by, acc.creation_timestamp, " +
                "acc.last_update_timestamp, acc.state, acc.module_id, acc.namespace_id, acc.release_id, " +
                "acc.revision_num, acc.revision_tracking_num, acc.revision_action, acc.current_acc_id, " +
                "acc.is_deprecated, acc.is_abstract " +
                "FROM acc " +
                "WHERE acc_id = :acc_id", newSqlParameterSource()
                .addValue("acc_id", id), ACC.class);

    }

    public BCCP getBccp(long id) {
        return jdbcTemplate.queryForObject("SELECT bccp.bccp_id, bccp.guid, " +
                "bccp.property_term, bccp.representation_term, " +
                "bccp.bdt_id, bccp.den, bccp.definition, bccp.definition_source, " +
                "bccp.module_id, bccp.namespace_id, bccp.is_deprecated, " +
                "bccp.created_by, bccp.owner_user_id, bccp.last_updated_by, bccp.creation_timestamp, " +
                "bccp.last_update_timestamp, bccp.state, bccp.release_id, " +
                "bccp.revision_num, bccp.revision_tracking_num, bccp.revision_action, bccp.current_bccp_id, " +
                "bccp.is_nillable, bccp.default_value " +
                "FROM bccp " +
                "WHERE bccp_id = :bccp_id", newSqlParameterSource()
                .addValue("bccp_id", id), BCCP.class);

    }

    public List<BCC> getBccs(long id) {
        return jdbcTemplate.queryForList("SELECT bcc.bcc_id, bcc.guid, " +
                "bcc.cardinality_min, bcc.cardinality_max, " +
                "bcc.to_bccp_id, bcc.from_acc_id, bcc.seq_key, bcc.entity_type, " +
                "bcc.den, bcc.definition, bcc.definition_source, " +
                "bcc.created_by, bcc.owner_user_id, bcc.last_updated_by, bcc.creation_timestamp, " +
                "bcc.last_update_timestamp, bcc.state, bcc.release_id, " +
                "bcc.revision_num, bcc.revision_tracking_num, bcc.revision_action, bcc.current_bcc_id, " +
                "bcc.is_nillable, bcc.default_value, bcc.is_deprecated " +
                "FROM bcc " +
                "WHERE from_acc_id = :from_acc_id", newSqlParameterSource()
                .addValue("from_acc_id", id), BCC.class);

    }
}

