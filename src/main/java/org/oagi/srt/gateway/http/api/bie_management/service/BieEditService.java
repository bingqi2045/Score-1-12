package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.cc_management.service.BdtRepository;
import org.oagi.srt.gateway.http.api.common.data.OagisComponentType;
import org.oagi.srt.gateway.http.api.common.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class BieEditService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private BieRepository repository;

    @Autowired
    private BdtRepository bdtRepository;

    private String GET_ROOT_NODE_STATEMENT =
            "SELECT top_level_abie_id, top_level_abie.release_id, 'abie' as type, asccp.property_term as name, " +
                    "asbiep.asbiep_id, asbiep.based_asccp_id as asccp_id, abie.abie_id, abie.based_acc_id as acc_id " +
                    "FROM top_level_abie JOIN abie ON top_level_abie.abie_id = abie.abie_id " +
                    "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
                    "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                    "WHERE top_level_abie_id = :top_level_abie_id";

    public BieEditAbieNode getRootNode(long topLevelAbieId) {
        BieEditAbieNode rootNode = jdbcTemplate.queryForObject(GET_ROOT_NODE_STATEMENT, newSqlParameterSource()
                .addValue("top_level_abie_id", topLevelAbieId), BieEditAbieNode.class);
        rootNode.setHasChild(hasChild(rootNode));

        return rootNode;
    }

    public List<BieEditNode> getAsbiepChildren(BieEditAsbiepNode asbiepNode) {
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        long currentAccId = repository.getAcc(asbiepNode.getAccId()).getCurrentAccId();
        long abieId = asbiepNode.getAbieId();
        if (abieId > 0L) {
            asbieMap = repository.getAsbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditAsbie::getBasedAsccId, Function.identity()));
            bbieMap = repository.getBbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));
        } else {
            asbieMap = Collections.emptyMap();
            bbieMap = Collections.emptyMap();
        }

        List<BieEditNode> children = getChildren(asbieMap, bbieMap, currentAccId, asbiepNode);
        return children;
    }

    public List<BieEditNode> getBbiepChildren(BieEditBbiepNode bbiepNode) {
        long bbiepId = bbiepNode.getBbiepId();
        BieEditBccp bccp;
        if (bbiepId > 0L) {
            BieEditBbiep bbiep = repository.getBbiep(bbiepId, bbiepNode.getTopLevelAbieId());
            bccp = repository.getBccp(bbiep.getBasedBccpId());
        } else {
            bccp = repository.getBccp(bbiepNode.getBccpId());
        }

        List<BieEditNode> children = new ArrayList();
        List<BieEditBdtSc> bdtScList = repository.getBdtScListByOwnerDtId(bccp.getBdtId());
        long bbieId = bbiepNode.getBbieId();
        for (BieEditBdtSc bdtSc : bdtScList) {
            BieEditBbieScNode bbieScNode = new BieEditBbieScNode();

            bbieScNode.setTopLevelAbieId(bbiepNode.getTopLevelAbieId());
            bbieScNode.setReleaseId(bbiepNode.getReleaseId());
            bbieScNode.setType("bbie_sc");
            bbieScNode.setName(bdtSc.getName());

            if (bbieId > 0L) {
                BieEditBbieSc bbieSc = repository.getBbieScIdByBbieIdAndDtScId(
                        bbieId, bdtSc.getDtScId(), bbiepNode.getTopLevelAbieId());
                bbieScNode.setBbieScId(bbieSc.getBbieScId());
                bbieScNode.setUsed(bbieSc.isUsed());
            }
            bbieScNode.setDtScId(bdtSc.getDtScId());

            children.add(bbieScNode);
        }

        return children;
    }

    private Stack<BieEditAcc> getAccStack(long currentAccId, long releaseId) {
        Stack<BieEditAcc> accStack = new Stack();
        BieEditAcc acc = repository.getAccByCurrentAccId(currentAccId, releaseId);
        accStack.push(acc);

        while (acc.getBasedAccId() != null) {
            acc = repository.getAccByCurrentAccId(acc.getBasedAccId(), releaseId);
            accStack.push(acc);
        }

        return accStack;
    }

    private List<SeqKeySupportable> getAssociationsByCurrentAccId(long currentAccId, long releaseId) {
        Stack<BieEditAcc> accStack = getAccStack(currentAccId, releaseId);

        List<BieEditBcc> attributeBccList = new ArrayList();
        List<SeqKeySupportable> assocList = new ArrayList();

        while (!accStack.isEmpty()) {
            BieEditAcc acc = accStack.pop();

            long fromAccId = acc.getCurrentAccId();
            List<BieEditAscc> asccList = repository.getAsccListByFromAccId(fromAccId, releaseId);
            List<BieEditBcc> bccList = repository.getBccListByFromAccId(fromAccId, releaseId);

            attributeBccList.addAll(
                    bccList.stream().filter(e -> e.isAttribute()).collect(Collectors.toList()));

            List<SeqKeySupportable> tmpAssocList = new ArrayList();
            tmpAssocList.addAll(asccList);
            tmpAssocList.addAll(
                    bccList.stream().filter(e -> !e.isAttribute()).collect(Collectors.toList()));
            assocList.addAll(tmpAssocList.stream()
                    .sorted(Comparator.comparingInt(SeqKeySupportable::getSeqKey))
                    .collect(Collectors.toList()));
        }

        assocList.addAll(0, attributeBccList);
        return assocList;
    }

    public List<BieEditNode> getAbieChildren(BieEditAbieNode abieNode) {
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        long currentAccId;
        long asbiepId = abieNode.getAsbiepId();
        if (asbiepId > 0L) {
            long abieId = repository.getAbieByAsbiepId(asbiepId).getAbieId();
            asbieMap = repository.getAsbieListByFromAbieId(abieId, abieNode).stream()
                    .collect(toMap(BieEditAsbie::getBasedAsccId, Function.identity()));
            bbieMap = repository.getBbieListByFromAbieId(abieId, abieNode).stream()
                    .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

            currentAccId = repository.getRoleOfAccIdByAsbiepId(asbiepId);
        } else {
            asbieMap = Collections.emptyMap();
            bbieMap = Collections.emptyMap();

            long asccpId = abieNode.getAsccpId();
            currentAccId = repository.getRoleOfAccIdByAsccpId(asccpId);
        }

        List<BieEditNode> children = getChildren(asbieMap, bbieMap, currentAccId, abieNode);
        return children;
    }

    public List<BieEditNode> getChildren(
            Map<Long, BieEditAsbie> asbieMap,
            Map<Long, BieEditBbie> bbieMap,
            long currentAccId,
            BieEditNode node) {
        List<BieEditNode> children = new ArrayList();
        TopLevelAbie topLevelAbie = repository.getTopLevelAbie(node.getTopLevelAbieId());

        List<SeqKeySupportable> assocList = getAssociationsByCurrentAccId(currentAccId, node.getReleaseId());
        for (SeqKeySupportable assoc : assocList) {
            if (assoc instanceof BieEditAscc) {
                BieEditAscc ascc = (BieEditAscc) assoc;
                BieEditAsbie asbie = asbieMap.get(ascc.getAsccId());
                BieEditAsbiepNode asbiepNode = createAsbiepNode(topLevelAbie, asbie, ascc);

                OagisComponentType oagisComponentType = repository.getOagisComponentTypeByAccId(asbiepNode.getAccId());
                if (oagisComponentType.isGroup()) {
                    children.addAll(getAsbiepChildren(asbiepNode));
                } else {
                    children.add(asbiepNode);
                }
            } else {
                BieEditBcc bcc = (BieEditBcc) assoc;
                BieEditBbie bbie = bbieMap.get(bcc.getBccId());
                BieEditBbiepNode bbiepNode = createBbiepNode(topLevelAbie, bbie, bcc);
                children.add(bbiepNode);
            }
        }

        return children;
    }

    public BieEditAsbiepNode createAsbiepNode(TopLevelAbie topLevelAbie, BieEditAsbie asbie, BieEditAscc ascc) {
        BieEditAsbiepNode asbiepNode = new BieEditAsbiepNode();

        asbiepNode.setTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
        asbiepNode.setReleaseId(topLevelAbie.getReleaseId());
        asbiepNode.setType("asbiep");

        if (asbie != null) {
            asbiepNode.setAsbieId(asbie.getAsbieId());
            asbiepNode.setAsbiepId(asbie.getToAsbiepId());

            BieEditAbie abie = repository.getAbieByAsbiepId(asbie.getToAsbiepId());
            asbiepNode.setAbieId(abie.getAbieId());
            asbiepNode.setAccId(abie.getBasedAccId());

            asbiepNode.setName(repository.getAsccpPropertyTermByAsbiepId(asbie.getToAsbiepId()));
            asbiepNode.setUsed(asbie.isUsed());
        }

        asbiepNode.setAsccId(ascc.getAsccId());
        BieEditAsccp asccp = repository.getAsccpByCurrentAsccpId(ascc.getToAsccpId(), topLevelAbie.getReleaseId());
        asbiepNode.setAsccpId(asccp.getAsccpId());

        if (StringUtils.isEmpty(asbiepNode.getName())) {
            asbiepNode.setName(asccp.getPropertyTerm());
        }
        if (asbiepNode.getAccId() == 0L) {
            BieEditAcc acc = repository.getAccByCurrentAccId(asccp.getRoleOfAccId(), topLevelAbie.getReleaseId());
            asbiepNode.setAccId(acc.getAccId());
        }

        asbiepNode.setHasChild(hasChild(asbiepNode));

        return asbiepNode;
    }

    public BieEditBbiepNode createBbiepNode(TopLevelAbie topLevelAbie, BieEditBbie bbie, BieEditBcc bcc) {
        BieEditBbiepNode bbiepNode = new BieEditBbiepNode();

        bbiepNode.setTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
        bbiepNode.setReleaseId(topLevelAbie.getReleaseId());
        bbiepNode.setType("bbiep");
        bbiepNode.setAttribute(bcc.isAttribute());

        if (bbie != null) {
            bbiepNode.setBbieId(bbie.getBbieId());
            bbiepNode.setBbiepId(bbie.getToBbiepId());

            bbiepNode.setName(repository.getBccpPropertyTermByBbiepId(bbie.getToBbiepId()));
            bbiepNode.setUsed(bbie.isUsed());
        }

        bbiepNode.setBccId(bcc.getBccId());
        BieEditBccp bccp = repository.getBccpByCurrentBccpId(bcc.getToBccpId(), topLevelAbie.getReleaseId());
        bbiepNode.setBccpId(bccp.getBccpId());
        bbiepNode.setBdtId(bccp.getBdtId());

        if (StringUtils.isEmpty(bbiepNode.getName())) {
            bbiepNode.setName(bccp.getPropertyTerm());
        }

        bbiepNode.setHasChild(hasChild(bbiepNode));

        return bbiepNode;
    }

    public boolean hasChild(BieEditAbieNode abieNode) {
        long fromAccId;

        long releaseId = abieNode.getReleaseId();
        BieEditAcc acc = null;
        if (abieNode.getTopLevelAbieId() > 0L) {
            fromAccId = repository.getCurrentAccIdByTopLevelAbieId(abieNode.getTopLevelAbieId());
        } else {
            acc = repository.getAcc(abieNode.getAccId());
            fromAccId = acc.getCurrentAccId();
        }

        if (repository.getAsccListByFromAccId(fromAccId, releaseId).size() > 0) {
            return true;
        }
        if (repository.getBccListByFromAccId(fromAccId, releaseId).size() > 0) {
            return true;
        }

        long currentAccId = fromAccId;
        if (acc == null) {
            acc = repository.getAccByCurrentAccId(currentAccId, releaseId);
        }
        if (acc != null && acc.getBasedAccId() != null) {
            BieEditAbieNode basedAbieNode = new BieEditAbieNode();
            basedAbieNode.setReleaseId(releaseId);

            acc = repository.getAccByCurrentAccId(acc.getBasedAccId(), releaseId);
            basedAbieNode.setAccId(acc.getAccId());
            return hasChild(basedAbieNode);
        }

        return false;
    }

    public boolean hasChild(BieEditAsbiepNode asbiepNode) {
        BieEditAbieNode abieNode = new BieEditAbieNode();
        abieNode.setReleaseId(asbiepNode.getReleaseId());
        abieNode.setAccId(asbiepNode.getAccId());

        return hasChild(abieNode);
    }

    public boolean hasChild(BieEditBbiepNode bbiepNode) {
        BieEditBccp bccp = repository.getBccp(bbiepNode.getBccpId());
        return repository.getCountDtScByOwnerDtId(bccp.getBdtId()) > 0;
    }





    /*
     * For Detail
     */

    public BieEditAbieNodeDetail getAbieDetail(BieEditAbieNode abieNode) {
        BieEditAbieNodeDetail detail =
                jdbcTemplate.queryForObject("SELECT version, status, remark, biz_term, definition " +
                                "FROM abie WHERE abie_id = :abie_id", newSqlParameterSource()
                                .addValue("abie_id", abieNode.getAbieId()),
                        BieEditAbieNodeDetail.class);
        return detail.append(abieNode);
    }

    public BieEditAsbiepNodeDetail getAsbiepDetail(BieEditAsbiepNode asbiepNode) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("asbie_id", asbiepNode.getAsbieId())
                .addValue("asbiep_id", asbiepNode.getAsbiepId())
                .addValue("ascc_id", asbiepNode.getAsccId())
                .addValue("asccp_id", asbiepNode.getAsccpId())
                .addValue("acc_id", asbiepNode.getAccId());

        BieEditAsbiepNodeDetail detail;
        if (asbiepNode.getAsbieId() > 0L) {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max, is_used as used, " +
                            "is_nillable as nillable, definition as context_definition " +
                            "FROM asbie WHERE asbie_id = :asbie_id",
                    parameterSource, BieEditAsbiepNodeDetail.class);
        } else {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                            "FROM ascc WHERE ascc_id = :ascc_id",
                    parameterSource, BieEditAsbiepNodeDetail.class);
        }

        if (asbiepNode.getAsbiepId() > 0L) {
            jdbcTemplate.query("SELECT biz_term, remark FROM asbiep WHERE asbiep_id = :asbiep_id",
                    parameterSource, rs -> {
                        detail.setBizTerm(rs.getString("biz_term"));
                        detail.setRemark(rs.getString("remark"));
                    });
        }

        jdbcTemplate.query("SELECT definition FROM ascc WHERE ascc_id = :ascc_id", parameterSource, rs -> {
            detail.setAssociationDefinition(rs.getString("definition"));
        });

        jdbcTemplate.query("SELECT definition FROM asccp WHERE asccp_id = :asccp_id", parameterSource, rs -> {
            detail.setComponentDefinition(rs.getString("definition"));
        });

        jdbcTemplate.query("SELECT definition FROM acc WHERE acc_id = :acc_id", parameterSource, rs -> {
            detail.setTypeDefinition(rs.getString("definition"));
        });

        return detail.append(asbiepNode);
    }

    public BieEditBbiepNodeDetail getBbiepDetail(BieEditBbiepNode bbiepNode) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bbie_id", bbiepNode.getBbieId())
                .addValue("bbiep_id", bbiepNode.getBbiepId())
                .addValue("bcc_id", bbiepNode.getBccId())
                .addValue("bccp_id", bbiepNode.getBccpId());

        BieEditBbiepNodeDetail detail;
        if (bbiepNode.getBbieId() > 0L) {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max, is_used as used, " +
                            "bdt_pri_restri_id, code_list_id, agency_id_list_id, " +
                            "is_nillable as nillable, fixed_value, definition as context_definition " +
                            "FROM bbie WHERE bbie_id = :bbie_id",
                    parameterSource, BieEditBbiepNodeDetail.class);
        } else {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                            "FROM bcc WHERE bcc_id = :bcc_id",
                    parameterSource, BieEditBbiepNodeDetail.class);
        }

        if (bbiepNode.getBbiepId() > 0L) {
            jdbcTemplate.query("SELECT bbiep.biz_term, bbiep.remark, bccp.bdt_id, dt.den as bdt_den " +
                            "FROM bbiep JOIN bccp ON bbiep.based_bccp_id = bccp.bccp_id " +
                            "JOIN dt ON bccp.bdt_id = dt.dt_id " +
                            "WHERE bbiep_id = :bbiep_id",
                    parameterSource, rs -> {
                        detail.setBizTerm(rs.getString("biz_term"));
                        detail.setRemark(rs.getString("remark"));
                        detail.setBdtId(rs.getLong("bdt_id"));
                        detail.setBdtDen(rs.getString("bdt_den"));
                    });
        } else {
            jdbcTemplate.query("SELECT bccp.bdt_id, dt.den as bdt_den " +
                    "FROM bccp JOIN dt ON bccp.bdt_id = dt.dt_id " +
                    "WHERE bccp_id = :bccp_id", parameterSource, rs -> {
                detail.setBdtId(rs.getLong("bdt_id"));
                detail.setBdtDen(rs.getString("bdt_den"));
            });
        }

        if (bbiepNode.getBbieId() == 0L) {
            long defaultBdtPriRestriId = jdbcTemplate.queryForObject(
                    "SELECT bdt_pri_restri_id FROM bdt_pri_restri " +
                            "WHERE bdt_id = :bdt_id AND is_default = :is_default", newSqlParameterSource()
                            .addValue("bdt_id", detail.getBdtId())
                            .addValue("is_default", true), Long.class);
            detail.setBdtPriRestriId(defaultBdtPriRestriId);
        }

        jdbcTemplate.query("SELECT definition FROM bcc WHERE bcc_id = :bcc_id", parameterSource, rs -> {
            detail.setAssociationDefinition(rs.getString("definition"));
        });

        jdbcTemplate.query("SELECT definition FROM bccp WHERE bccp_id = :bccp_id", parameterSource, rs -> {
            detail.setComponentDefinition(rs.getString("definition"));
        });

        return detail.append(bbiepNode);
    }

    public BieEditBdtPriRestri getBdtPriRestri(BieEditBbiepNode bbiepNode) {
        long bdtId = bbiepNode.getBdtId();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bdt_id", bdtId);

        List<BieEditXbt> bieEditXbtList = jdbcTemplate.queryForList(
                "SELECT b.bdt_pri_restri_id AS pri_restri_id, is_default, x.name as xbt_name " +
                        "FROM bdt_pri_restri b JOIN cdt_awd_pri_xps_type_map c " +
                        "ON b.cdt_awd_pri_xps_type_map_id = c.cdt_awd_pri_xps_type_map_id " +
                        "JOIN xbt x ON c.xbt_id = x.xbt_id WHERE bdt_id = :bdt_id", parameterSource, BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = jdbcTemplate.queryForList(
                "SELECT c.code_list_id, c.based_code_list_id, b.is_default, c.name " +
                        "FROM bdt_pri_restri b JOIN code_list c ON b.code_list_id = c.code_list_id " +
                        "WHERE bdt_id = :bdt_id", newSqlParameterSource()
                        .addValue("bdt_id", bdtId), BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = jdbcTemplate.queryForList(
                "SELECT a.agency_id_list_id, b.is_default, a.name " +
                        "FROM bdt_pri_restri b JOIN agency_id_list a ON b.agency_id_list_id = a.agency_id_list_id " +
                        "WHERE bdt_id = :bdt_id", parameterSource, BieEditAgencyIdList.class);

        if (bieEditCodeLists.isEmpty() && bieEditAgencyIdLists.isEmpty()) {
            bieEditCodeLists = getAllCodeLists();
            bieEditAgencyIdLists = getAllAgencyIdLists();
        } else {
            if (!bieEditCodeLists.isEmpty()) {
                List<BieEditCodeList> basedCodeLists = getBieEditCodeListByBasedCodeListIds(
                        bieEditCodeLists.stream().filter(e -> e.getBasedCodeListId() != null)
                                .map(e -> e.getBasedCodeListId()).collect(Collectors.toList())
                );
                bieEditCodeLists.addAll(0, basedCodeLists);
            }
        }

        BieEditBdtPriRestri bdtPriRestri = new BieEditBdtPriRestri();

        bdtPriRestri.setXbtList(bieEditXbtList);
        bdtPriRestri.setCodeLists(bieEditCodeLists);
        bdtPriRestri.setAgencyIdLists(bieEditAgencyIdLists);

        return bdtPriRestri;
    }

    public BieEditBbieScNodeDetail getBbieScDetail(BieEditBbieScNode bbieScNode) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bbie_sc_id", bbieScNode.getBbieScId())
                .addValue("dt_sc_id", bbieScNode.getDtScId());

        BieEditBbieScNodeDetail detail;
        if (bbieScNode.getBbieScId() > 0L) {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max, is_used as used, " +
                            "default_value, fixed_value, biz_term, remark, definition as context_definition " +
                            "FROM bbie_sc WHERE bbie_sc_id = :bbie_sc_id",
                    parameterSource, BieEditBbieScNodeDetail.class);
        } else {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                            "FROM dt_sc WHERE dt_sc_id = :dt_sc_id",
                    parameterSource, BieEditBbieScNodeDetail.class);
        }

        jdbcTemplate.query("SELECT definition FROM dt_sc WHERE dt_sc_id = :dt_sc_id", parameterSource, rs -> {
            detail.setComponentDefinition(rs.getString("definition"));
        });

        return detail.append(bbieScNode);
    }

    private List<BieEditCodeList> getAllCodeLists() {
        return jdbcTemplate.queryForList("SELECT code_list_id, name FROM code_list",
                BieEditCodeList.class);
    }

    private List<BieEditAgencyIdList> getAllAgencyIdLists() {
        return jdbcTemplate.queryForList("SELECT agency_id_list_id, name FROM agency_id_list",
                BieEditAgencyIdList.class);
    }

    private List<BieEditCodeList> getBieEditCodeListByBasedCodeListIds(List<Long> basedCodeListIds) {
        if (basedCodeListIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<BieEditCodeList> bieEditCodeLists = jdbcTemplate.queryForList(
                "SELECT code_list_id, based_code_list_id, name " +
                        "FROM code_list WHERE based_code_list_id IN (:based_code_list_ids)", newSqlParameterSource()
                        .addValue("based_code_list_ids", basedCodeListIds), BieEditCodeList.class);

        List<BieEditCodeList> basedCodeLists =
                getBieEditCodeListByBasedCodeListIds(
                        bieEditCodeLists.stream().filter(e -> e.getBasedCodeListId() != null)
                                .map(e -> e.getBasedCodeListId()).collect(Collectors.toList())
                );

        bieEditCodeLists.addAll(0, basedCodeLists);
        return bieEditCodeLists;
    }

    public BieEditBdtScPriRestri getBdtScPriRestri(BieEditBbieScNode bbieScNode) {
        return null;
    }
}
