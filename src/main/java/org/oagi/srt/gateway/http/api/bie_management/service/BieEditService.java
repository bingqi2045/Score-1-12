package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
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
                            "is_nillable as nillable, fixed_value, definition as context_definition " +
                            "FROM bbie WHERE bbie_id = :bbie_id",
                    parameterSource, BieEditBbiepNodeDetail.class);
        } else {
            detail = jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max " +
                            "FROM bcc WHERE bcc_id = :bcc_id",
                    parameterSource, BieEditBbiepNodeDetail.class);
        }

        if (bbiepNode.getBbiepId() > 0L) {
            jdbcTemplate.query("SELECT bbiep.biz_term, bbiep.remark, bccp.bdt_id " +
                            "FROM bbiep JOIN bccp ON bbiep.based_bccp_id = bccp.bccp_id " +
                            "WHERE bbiep_id = :bbiep_id",
                    parameterSource, rs -> {
                        detail.setBizTerm(rs.getString("biz_term"));
                        detail.setRemark(rs.getString("remark"));
                        detail.setBdtId(rs.getLong("bdt_id"));
                    });
        } else {
            jdbcTemplate.query("SELECT bdt_id FROM bccp WHERE bccp_id = :bccp_id", parameterSource, rs -> {
                detail.setBdtId(rs.getLong("bdt_id"));
            });
        }

        jdbcTemplate.query("SELECT definition FROM bcc WHERE bcc_id = :bcc_id", parameterSource, rs -> {
            detail.setAssociationDefinition(rs.getString("definition"));
        });

        jdbcTemplate.query("SELECT definition FROM bccp WHERE bccp_id = :bccp_id", parameterSource, rs -> {
            detail.setComponentDefinition(rs.getString("definition"));
        });

        return detail.append(bbiepNode);
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


    public BdtPriRestri getBdtPriRestri(long bdtId) {
        return null;
    }

    public BdtScPriRestri getBdtScPriRestri(long dtScId) {
        return null;
    }
}
