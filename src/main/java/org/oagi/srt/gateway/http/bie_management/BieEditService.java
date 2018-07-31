package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.*;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
public class BieEditService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_ROOT_NODE_STATEMENT =
            "SELECT 'abie' as type, asbiep.asbiep_id as bie_id, asbiep.based_asccp_id as cc_id, asccp.property_term as name " +
                    "FROM top_level_abie JOIN abie ON top_level_abie.abie_id = abie.abie_id " +
                    "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
                    "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                    "WHERE top_level_abie_id = :top_level_abie_id";

    public BieEditNode getRootNode(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("top_level_abie_id", topLevelAbieId);

        List<BieEditNode> res = jdbcTemplate.query(GET_ROOT_NODE_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditNode.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        BieEditNode rootNode = res.get(0);
        rootNode.setTopLevelAbieId(topLevelAbieId);

        long accId = jdbcTemplate.queryForObject("SELECT acc.current_acc_id " +
                "FROM abie JOIN top_level_abie ON abie.abie_id = top_level_abie.abie_id " +
                "JOIN acc ON abie.based_acc_id = acc.acc_id " +
                "WHERE top_level_abie_id = :top_level_abie_id", parameterSource, Long.class);

        TopLevelAbie topLevelAbie = getTopLevelAbie(topLevelAbieId);
        long releaseId = topLevelAbie.getReleaseId();
        rootNode.setHasChild(hasChild(releaseId, "ACC", accId));

        return rootNode;
    }

    public List<BieEditNode> getChildren(BieEditNode node) {
        TopLevelAbie topLevelAbie = getTopLevelAbie(node.getTopLevelAbieId());
        node.setReleaseId(topLevelAbie.getReleaseId());

        switch (node.getType().toLowerCase()) {
            case "abie": {
                long asbiepId = node.getBieId();
                long asccpId = node.getCcId();

                Map<Long, BieEditAsbie> asbieMap;
                Map<Long, BieEditBbie> bbieMap;

                long accId;
                if (asbiepId > 0L) {
                    long abieId = getAbieIdByAsbiepId(asbiepId);
                    asbieMap = getAsbieListByFromAbieId(abieId, node).stream()
                            .collect(toMap(BieEditAsbie::getBasedAsccpId, Function.identity()));
                    bbieMap = getBbieListByFromAbieId(abieId, node).stream()
                            .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

                    accId = getRoleOfAccIdByAsbiepId(asbiepId);
                } else {
                    asbieMap = Collections.emptyMap();
                    bbieMap = Collections.emptyMap();

                    accId = getRoleOfAccIdByAsccpId(asccpId);
                }

                Stack<BieEditAcc> accStack = new Stack();
                BieEditAcc acc = getAccByCurrentAccId(accId, node.getReleaseId());
                accStack.push(acc);

                while (acc.getBasedAccId() > 0L) {
                    acc = getAccByCurrentAccId(acc.getBasedAccId(), node.getReleaseId());
                    accStack.push(acc);
                }


                List<BieEditBcc> attributeBccList = new ArrayList();
                List<SeqKeySupportable> assocList = new ArrayList();

                while (!accStack.isEmpty()) {
                    acc = accStack.pop();
                    long fromAccId = acc.getCurrentAccId();
                    List<BieEditAscc> asccList = getAsccListByFromAccId(fromAccId, node);
                    List<BieEditBcc> bccList = getBccListByFromAccId(fromAccId, node);

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

                List<BieEditNode> children = new ArrayList();

                for (BieEditBcc attributeBcc : attributeBccList) {
                    BieEditBbie bbie = bbieMap.get(attributeBcc.getBccId());

                    BieEditNode child = new BieEditNode();
                    child.setType("bbiep");
                    if (bbie != null) {
                        child.setBieId(bbie.getBbieId());
                        child.setName(getBccpPropertyTermByBbiepId(bbie.getToBbiepId()));
                    }

                    child.setCcId(attributeBcc.getBccId());
                    if (StringUtils.isEmpty(child.getName())) {
                        BieEditBccp bccp = getBccpByCurrentBccpId(attributeBcc.getToBccpId(), node.getReleaseId());
                        child.setName(bccp.getPropertyTerm());
                    }

                    children.add(child);
                }

                for (SeqKeySupportable assoc : assocList) {
                    if (assoc instanceof BieEditAscc) {
                        BieEditAscc ascc = (BieEditAscc) assoc;

                        BieEditAsbie asbie = asbieMap.get(ascc.getAsccId());

                        BieEditNode child = new BieEditNode();
                        child.setType("asbiep");
                        if (asbie != null) {
                            child.setBieId(asbie.getAsbieId());
                            child.setName(getAsccpPropertyTermByAsbiepId(asbie.getToAsbiepId()));
                        }

                        child.setCcId(ascc.getAsccId());
                        if (StringUtils.isEmpty(child.getName())) {
                            BieEditAsccp asccp = getAsccpByCurrentAsccpId(ascc.getToAsccpId(), node.getReleaseId());
                            child.setName(asccp.getPropertyTerm());
                        }

                        children.add(child);

                    } else {
                        BieEditBcc bcc = (BieEditBcc) assoc;
                        BieEditBbie bbie = bbieMap.get(bcc.getBccId());

                        BieEditNode child = new BieEditNode();
                        child.setType("BBIEP");
                        if (bbie != null) {
                            child.setBieId(bbie.getBbieId());
                            child.setName(getBccpPropertyTermByBbiepId(bbie.getToBbiepId()));
                        }

                        child.setCcId(bcc.getBccId());
                        if (StringUtils.isEmpty(child.getName())) {
                            BieEditBccp bccp = getBccpByCurrentBccpId(bcc.getToBccpId(), node.getReleaseId());
                            child.setName(bccp.getPropertyTerm());
                        }

                        children.add(child);
                    }
                }

                return children;
            }
        }
        return Collections.emptyList();
    }

    public String getAsccpPropertyTermByAsbiepId(long asbiepId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("asbiep_id", asbiepId);
        return jdbcTemplate.queryForObject("SELECT asccp.property_term " +
                "FROM asccp JOIN asbiep ON asccp.asccp_id = asbiep.based_asccp_id " +
                "WHERE asbiep_id = :asbiep_id", parameterSource, String.class);
    }

    public String getBccpPropertyTermByBbiepId(long bbiepId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("bbiep_id", bbiepId);
        return jdbcTemplate.queryForObject("SELECT bccp.property_term " +
                "FROM bccp JOIN bbiep ON bccp.bccp_id = bbiep.based_bccp_id " +
                "WHERE bbiep_id = :bbiep_id", parameterSource, String.class);
    }

    public BieEditAsccp getAsccpByCurrentAsccpId(long currentAsccpId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("current_asccp_id", currentAsccpId);
        List<BieEditAsccp> asccpList =
                jdbcTemplate.query("SELECT asccp_id, guid, property_term, role_of_acc_id, current_asccp_id, " +
                        "revision_num, revision_tracking_num, revision_action, release_id " +
                        "FROM asccp WHERE revision_num > 0 AND current_asccp_id = :current_asccp_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditAsccp.class));
        return CcUtility.getLatestEntity(releaseId, asccpList);
    }

    public BieEditBccp getBccpByCurrentBccpId(long currentBccpId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("current_bccp_id", currentBccpId);
        List<BieEditBccp> bccpList =
                jdbcTemplate.query("SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                        "revision_num, revision_tracking_num, revision_action, release_id " +
                        "FROM bccp WHERE revision_num > 0 AND current_bccp_id = :current_bccp_id", parameterSource, new BeanPropertyRowMapper(BieEditBccp.class));
        return CcUtility.getLatestEntity(releaseId, bccpList);
    }

    public long getAbieIdByAsbiepId(long asbiepId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("asbiep_id", asbiepId);
        return jdbcTemplate.queryForObject("SELECT abie.abie_id " +
                "FROM asbiep JOIN abie ON asbiep.role_of_abie_id = abie.abie_id " +
                "WHERE asbiep.asbiep_id = :asbiep_id", parameterSource, Long.class);
    }

    public List<BieEditAsbie> getAsbieListByFromAbieId(long fromAbieId, BieEditNode node) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", node.getTopLevelAbieId());
        parameterSource.addValue("from_abie_id", fromAbieId);
        return jdbcTemplate.query("SELECT asbie_id, from_abie_id, to_asbiep_id, based_ascc_id, is_used as used " +
                        "FROM asbie WHERE owner_top_level_abie_id = :owner_top_level_abie_id AND from_abie_id = :from_abie_id",
                parameterSource, new BeanPropertyRowMapper(BieEditAsbie.class));
    }

    public List<BieEditBbie> getBbieListByFromAbieId(long fromAbieId, BieEditNode node) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", node.getTopLevelAbieId());
        parameterSource.addValue("from_abie_id", fromAbieId);
        return jdbcTemplate.query("SELECT bbie_id, from_abie_id, to_bbiep_id, based_bcc_id, is_used as used " +
                        "FROM bbie WHERE owner_top_level_abie_id = :owner_top_level_abie_id AND from_abie_id = :from_abie_id",
                parameterSource, new BeanPropertyRowMapper(BieEditBbie.class));
    }

    public long getRoleOfAccIdByAsbiepId(long asbiepId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("asbiep_id", asbiepId);
        return jdbcTemplate.queryForObject("SELECT asccp.role_of_acc_id " +
                "FROM asbiep JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                "WHERE asbiep_id = :asbiep_id", parameterSource, Long.class);
    }

    public long getRoleOfAccIdByAsccpId(long asccpId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("asccp_id", asccpId);
        return jdbcTemplate.queryForObject("SELECT asccp.role_of_acc_id " +
                "FROM asccp WHERE asccp_id = :asccp_id", parameterSource, Long.class);
    }

    public List<BieEditAscc> getAsccListByFromAccId(long fromAccId, BieEditNode node) {
        long releaseId = node.getReleaseId();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("from_acc_id", fromAccId);
        parameterSource.addValue("release_id", releaseId);
        List<BieEditAscc> asccList =
                jdbcTemplate.query("SELECT ascc_id, guid, from_acc_id, to_asccp_id, seq_key, current_ascc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id " +
                                "FROM ascc WHERE revision_num > 0 AND from_acc_id = :from_acc_id AND release_id <= :release_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditAscc.class));
        return asccList.stream().collect(groupingBy(BieEditAscc::getGuid)).values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null).collect(Collectors.toList());
    }

    public List<BieEditBcc> getBccListByFromAccId(long fromAccId, BieEditNode node) {
        long releaseId = node.getReleaseId();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("from_acc_id", fromAccId);
        parameterSource.addValue("release_id", releaseId);
        List<BieEditBcc> bccList =
                jdbcTemplate.query("SELECT bcc_id, guid, from_acc_id, to_bccp_id, seq_key, entity_type, current_bcc_id, " +
                                "revision_num, revision_tracking_num, revision_action, release_id " +
                                "FROM bcc WHERE revision_num > 0 AND from_acc_id = :from_acc_id AND release_id <= :release_id",
                        parameterSource, new BeanPropertyRowMapper(BieEditBcc.class));
        return bccList.stream().collect(groupingBy(BieEditBcc::getGuid)).values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null).collect(Collectors.toList());
    }

    public boolean hasChild(long releaseId, String type, long id) {
        switch (type) {
            case "ACC": {
                long fromAccId = id;
                if (getAsccListByFromAccId(fromAccId, releaseId).size() > 0) {
                    return true;
                }
                if (getBccListByFromAccId(fromAccId, releaseId).size() > 0) {
                    return true;
                }

                long currentAccId = id;
                BieEditAcc acc = getAccByCurrentAccId(currentAccId, releaseId);
                if (acc.getBasedAccId() > 0L) {
                    return hasChild(releaseId, "ACC", acc.getBasedAccId());
                }
            }
            break;
        }

        return false;
    }

    private String GET_ACC_LIST_BY_CURRENT_ACC_ID_STATEMENT =
            "SELECT acc_id, guid, based_acc_id, current_acc_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM acc " +
                    "WHERE revision_num > 0 AND current_acc_id = :current_acc_id";

    public BieEditAcc getAccByCurrentAccId(long currentAccId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("current_acc_id", currentAccId);
        List<BieEditAcc> accList =
                jdbcTemplate.query(GET_ACC_LIST_BY_CURRENT_ACC_ID_STATEMENT, parameterSource,
                        new BeanPropertyRowMapper(BieEditAcc.class));
        return CcUtility.getLatestEntity(releaseId, accList);
    }


    private String GET_ASCC_LIST_BY_FROM_ACC_ID_STATEMENT =
            "SELECT ascc_id, guid, from_acc_id, to_asccp_id, seq_key, current_ascc_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM ascc " +
                    "WHERE revision_num > 0 AND from_acc_id = :from_acc_id";

    public List<BieEditAscc> getAsccListByFromAccId(long fromAccId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("from_acc_id", fromAccId);

        List<BieEditAscc> asccList = jdbcTemplate.query(GET_ASCC_LIST_BY_FROM_ACC_ID_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditAscc.class));

        return asccList.stream().collect(groupingBy(BieEditAscc::getGuid)).values()
                .stream().map(e -> CcUtility.getLatestEntity(releaseId, e)).filter(e -> e != null)
                .collect(Collectors.toList());
    }

    private String GET_BCC_LIST_BY_FROM_ACC_ID_STATEMENT =
            "SELECT bcc_id, guid, from_acc_id, to_bccp_id, seq_key, entity_type, current_bcc_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM bcc " +
                    "WHERE revision_num > 0 AND from_acc_id = :from_acc_id";

    public List<BieEditBcc> getBccListByFromAccId(long fromAccId, long releaseId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("from_acc_id", fromAccId);

        List<BieEditBcc> bccList = jdbcTemplate.query(GET_BCC_LIST_BY_FROM_ACC_ID_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditBcc.class));

        return bccList.stream().collect(groupingBy(BieEditBcc::getGuid)).values()
                .stream().map(e -> CcUtility.getLatestEntity(releaseId, e)).filter(e -> e != null)
                .collect(Collectors.toList());
    }


    private String GET_TOP_LEVEL_ABIE_STATEMENT =
            "SELECT top_level_abie_id, abie_id, owner_user_id, release_id, state " +
                    "FROM top_level_abie WHERE top_level_abie_id = :top_level_abie_id";

    public TopLevelAbie getTopLevelAbie(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("top_level_abie_id", topLevelAbieId);

        List<TopLevelAbie> res = jdbcTemplate.query(GET_TOP_LEVEL_ABIE_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(TopLevelAbie.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
    }


    public BieTree getBieTree(long topLevelAbieId) {
        TopLevelAbie topLevelAbie = getTopLevelAbie(topLevelAbieId);
        BieTreeDataSource dataSource = new BieTreeDataSource(topLevelAbie);
        return createBieTree(topLevelAbie, dataSource);
    }

    private BieTree createBieTree(TopLevelAbie topLevelAbie, BieTreeDataSource dataSource) {
        BieEditAbie abie = dataSource.getAbie(topLevelAbie.getAbieId());
        BieEditAcc acc = dataSource.getAcc(abie.getBasedAccId());

        List<BieEditAsbiep> asbiepList = dataSource.getAsbiepByRoleOfAbieId(abie.getAbieId());
        assert asbiepList.size() == 1;

        BieEditAsbiep asbiep = asbiepList.get(0);
        BieEditAsccp asccp = dataSource.getAsccp(asbiep.getBasedAsccpId());

        BieTree tree = new BieTree(asbiep, asccp);
        BieTreeNode rootNode = tree.getNode();
        BieTreeNode abieNode = tree.createNode(abie, acc);
        rootNode.addChildren(abieNode);

        fillChildren(abieNode, tree, dataSource);

        return tree;
    }

    private void fillChildren(BieTreeNode node, BieTree tree,
                              BieTreeDataSource dataSource) {

        switch (node.getType()) {
            case "ABIE": {
                long abieId = node.getBieId();

                BieEditAcc acc;
                Map<Long, BieEditAsbie> asbieMapByAsccId;
                Map<Long, BieEditBbie> bbieMapByBccId;

                Stack<BieEditAcc> accStack = new Stack();
                if (abieId > 0L) {
                    BieEditAbie abie = dataSource.getAbie(abieId);

                    asbieMapByAsccId = dataSource.getAsbieListByFromAbieId(abieId).stream()
                            .collect(toMap(BieEditAsbie::getBasedAsccpId, Function.identity()));
                    bbieMapByBccId = dataSource.getBbieListByFromAbieId(abieId).stream()
                            .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

                    acc = dataSource.getAcc(abie.getBasedAccId());
                } else {
                    long accId = node.getCcId();

                    asbieMapByAsccId = Collections.emptyMap();
                    bbieMapByBccId = Collections.emptyMap();

                    acc = dataSource.getAcc(accId);
                }

                if (acc == null) {
                    throw new IllegalStateException();
                }

                accStack.push(acc);
                while (acc.getBasedAccId() > 0L) {
                    acc = dataSource.getAccByCurrentAccId(acc.getBasedAccId());
                    accStack.push(acc);
                }

                // seq_key = 0
                List<BieEditBcc> attributeBccList = new ArrayList();
                // others
                List<SeqKeySupportable> assocList = new ArrayList();

                while (!accStack.isEmpty()) {
                    acc = accStack.pop();

                    long fromAccId = acc.getCurrentAccId();
                    List<BieEditAscc> tmpAsccList = dataSource.getAsccListByFromAccId(fromAccId);
                    List<BieEditBcc> tmpBccList = dataSource.getBccListByFromAccId(fromAccId);

                    attributeBccList.addAll(
                            tmpBccList.stream().filter(e -> e.isAttribute())
                                    .collect(Collectors.toList()));

                    List<SeqKeySupportable> assocs = new ArrayList();
                    assocs.addAll(tmpAsccList);
                    assocs.addAll(tmpBccList.stream().filter(e -> !e.isAttribute()).collect(Collectors.toList()));
                    assocs = assocs.stream().sorted(Comparator.comparingInt(SeqKeySupportable::getSeqKey))
                            .collect(Collectors.toList());
                    assocList.addAll(assocs);
                }

                for (BieEditBcc bcc : attributeBccList) {
                    BieEditBbie bbie = bbieMapByBccId.get(bcc.getBccId());
                    BieTreeNode attributeBbieNode = tree.createNode(bbie, bcc);
                    node.addChildren(attributeBbieNode);
                }

                for (SeqKeySupportable assoc : assocList) {
                    if (assoc instanceof BieEditAscc) {
                        BieEditAscc ascc = (BieEditAscc) assoc;
                        BieEditAsbie asbie = asbieMapByAsccId.get(ascc.getAsccId());
                        BieTreeNode asbieNode = tree.createNode(asbie, ascc);
                        node.addChildren(asbieNode);
                    } else {
                        BieEditBcc bcc = (BieEditBcc) assoc;
                        BieEditBbie bbie = bbieMapByBccId.get(bcc.getBccId());
                        BieTreeNode bbieNode = tree.createNode(bbie, bcc);
                        node.addChildren(bbieNode);
                    }
                }
            }

            break;

            case "ASBIE": {
                BieEditAsbie asbie = dataSource.getAsbie(node.getBieId());

                BieEditAsbiep asbiep = null;
                BieEditAsccp asccp;

                if (asbie != null) {
                    asbiep = dataSource.getAsbiep(asbie.getToAsbiepId());
                    asccp = dataSource.getAsccp(asbiep.getBasedAsccpId());
                } else {
                    BieEditAscc ascc = dataSource.getAscc(node.getCcId());
                    asccp = dataSource.getAsccpByCurrentAsccpId(ascc.getToAsccpId());
                }

                node.addChildren(tree.createNode(asbiep, asccp));
            }

            break;

            case "BBIE": {
                BieEditBbie bbie = dataSource.getBbie(node.getBieId());

                BieEditBbiep bbiep = null;
                BieEditBccp bccp;

                if (bbie != null) {
                    bbiep = dataSource.getBbiep(bbie.getToBbiepId());
                    bccp = dataSource.getBccp(bbiep.getBasedBccpId());
                } else {
                    BieEditBcc bcc = dataSource.getBcc(node.getCcId());
                    bccp = dataSource.getBccpByCurrentBccpId(bcc.getToBccpId());
                }

                BieTreeNode bbiepNode = tree.createNode(bbiep, bccp);
                node.addChildren(bbiepNode);


                Map<Long, BieEditBbieSc> bbieScMap;
                if (bbie != null) {
                    bbieScMap = dataSource.getBbieScByBbieId(bbie.getBbieId()).stream()
                            .collect(toMap(BieEditBbieSc::getDtScId, Function.identity()));
                } else {
                    bbieScMap = Collections.emptyMap();
                }

                List<BieEditBdtSc> bdtScList = dataSource.getBdtScByOwnerDtId(bccp.getBdtId());
                for (BieEditBdtSc bdtSc : bdtScList) {
                    BieEditBbieSc bbieSc = bbieScMap.get(bdtSc.getDtScId());
                    bbiepNode.addChildren(tree.createNode(bbieSc, bdtSc));
                }

                return;
            }

            case "ASBIEP": {
                BieEditAsbiep asbiep = dataSource.getAsbiep(node.getBieId());

                BieEditAbie abie = null;
                BieEditAcc acc;

                if (asbiep != null) {
                    abie = dataSource.getAbie(asbiep.getRoleOfAbieId());
                    acc = dataSource.getAcc(abie.getBasedAccId());
                } else {
                    BieEditAsccp asccp = dataSource.getAsccp(node.getCcId());
                    acc = dataSource.getAccByCurrentAccId(asccp.getRoleOfAccId());
                }

                node.addChildren(tree.createNode(abie, acc));
            }

            break;
        }

        node.getChildren().parallelStream().forEach(child -> {
            fillChildren(child, tree, dataSource);
        });
    }

    private class BieTreeDataSource {
        TopLevelAbie topLevelAbie;

        List<BieEditAcc> accList;
        Map<Long, BieEditAcc> accMap;
        Map<Long, List<BieEditAcc>> accByCurrentAccIdMap;

        List<BieEditAscc> asccList;
        Map<Long, BieEditAscc> asccMap;
        Map<Long, List<BieEditAscc>> asccListByFromAccIdMap;
        List<BieEditBcc> bccList;
        Map<Long, BieEditBcc> bccMap;
        Map<Long, List<BieEditBcc>> bccListByFromAccIdMap;

        List<BieEditAsccp> asccpList;
        Map<Long, BieEditAsccp> asccpMap;
        Map<Long, List<BieEditAsccp>> asccpByCurrentAsccpIdMap;

        List<BieEditBccp> bccpList;
        Map<Long, BieEditBccp> bccpMap;
        Map<Long, List<BieEditBccp>> bccpByCurrentBccpIdMap;

        List<BieEditBdtSc> bdtScList;
        Map<Long, List<BieEditBdtSc>> bdtScListByBdtIdMap;

        List<BieEditAbie> abieList;
        Map<Long, BieEditAbie> abieMap;

        List<BieEditAsbie> asbieList;
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, List<BieEditAsbie>> asbieListByFromAbieIdMap;
        List<BieEditBbie> bbieList;
        Map<Long, BieEditBbie> bbieMap;
        Map<Long, List<BieEditBbie>> bbieListByFromAbieIdMap;

        List<BieEditAsbiep> asbiepList;
        Map<Long, BieEditAsbiep> asbiepMap;
        Map<Long, List<BieEditAsbiep>> asbiepListByRoleOfAbieIdMap;

        List<BieEditBbiep> bbiepList;
        Map<Long, BieEditBbiep> bbiepMap;

        List<BieEditBbieSc> bbieScList;
        Map<Long, List<BieEditBbieSc>> bbieScListByBbieIdMap;

        public BieTreeDataSource(TopLevelAbie topLevelAbie) {
            this.topLevelAbie = topLevelAbie;

            accList = getBieEditAccList();
            accMap = accList.stream().collect(toMap(BieEditAcc::getAccId, Function.identity()));
            accByCurrentAccIdMap = accList.stream().collect(groupingBy(BieEditAcc::getCurrentAccId));

            asccList = getBieEditAsccList();
            asccMap = asccList.stream().collect(toMap(BieEditAscc::getAsccId, Function.identity()));
            asccListByFromAccIdMap = asccList.stream().collect(groupingBy(BieEditAscc::getFromAccId));
            bccList = getBieEditBccList();
            bccMap = bccList.stream().collect(toMap(BieEditBcc::getBccId, Function.identity()));
            bccListByFromAccIdMap = bccList.stream().collect(groupingBy(BieEditBcc::getFromAccId));

            asccpList = getBieEditAsccpList();
            asccpMap = asccpList.stream().collect(toMap(BieEditAsccp::getAsccpId, Function.identity()));
            asccpByCurrentAsccpIdMap = asccpList.stream().collect(groupingBy(BieEditAsccp::getCurrentAsccpId));

            bccpList = getBieEditBccpList();
            bccpMap = bccpList.stream().collect(toMap(BieEditBccp::getBccpId, Function.identity()));
            bccpByCurrentBccpIdMap = bccpList.stream().collect(groupingBy(BieEditBccp::getCurrentBccpId));

            bdtScList = getBieEditBdtScList();
            bdtScListByBdtIdMap = bdtScList.stream().collect(groupingBy(BieEditBdtSc::getOwnerDtId));

            long topLevelAbieId = topLevelAbie.getTopLevelAbieId();
            abieList = getBieEditAbieList(topLevelAbieId);
            abieMap = abieList.stream().collect(toMap(BieEditAbie::getAbieId, Function.identity()));

            asbieList = getBieEditAsbieList(topLevelAbieId);
            asbieMap = asbieList.stream().collect(toMap(BieEditAsbie::getAsbieId, Function.identity()));
            asbieListByFromAbieIdMap = asbieList.stream().collect(groupingBy(BieEditAsbie::getFromAbieId));
            bbieList = getBieEditBbieList(topLevelAbieId);
            bbieMap = bbieList.stream().collect(toMap(BieEditBbie::getBbieId, Function.identity()));
            bbieListByFromAbieIdMap = bbieList.stream().collect(groupingBy(BieEditBbie::getFromAbieId));

            asbiepList = getBieEditAsbiepList(topLevelAbieId);
            asbiepMap = asbiepList.stream().collect(toMap(BieEditAsbiep::getAsbiepId, Function.identity()));
            asbiepListByRoleOfAbieIdMap = asbiepList.stream().collect(groupingBy(BieEditAsbiep::getRoleOfAbieId));

            bbiepList = getBieEditBbiepList(topLevelAbieId);
            bbiepMap = bbiepList.stream().collect(toMap(BieEditBbiep::getBbiepId, Function.identity()));

            bbieScList = getBieEditBbieScList(topLevelAbieId);
            bbieScListByBbieIdMap = bbieScList.stream().collect(groupingBy(BieEditBbieSc::getBbieId));
        }

        public BieEditAcc getAcc(long accId) {
            return accMap.get(accId);
        }

        public BieEditAcc getAccByCurrentAccId(long currentAccId) {
            List<BieEditAcc> accList = accByCurrentAccIdMap.get(currentAccId);
            if (accList == null) {
                return null;
            }
            return CcUtility.getLatestEntity(topLevelAbie.getReleaseId(), accList);
        }

        public BieEditAbie getAbie(long abieId) {
            return abieMap.get(abieId);
        }

        public BieEditAscc getAscc(long asccId) {
            return asccMap.get(asccId);
        }

        public List<BieEditAscc> getAsccListByFromAccId(long fromAccId) {
            List<BieEditAscc> asccList = asccListByFromAccIdMap.get(fromAccId);
            if (asccList == null) {
                return Collections.emptyList();
            }

            return asccList.stream().collect(groupingBy(BieEditAscc::getGuid)).values().stream()
                    .map(e -> CcUtility.getLatestEntity(topLevelAbie.getReleaseId(), e))
                    .filter(e -> e != null)
                    .collect(Collectors.toList());
        }

        public BieEditBcc getBcc(long bccId) {
            return bccMap.get(bccId);
        }

        public List<BieEditBcc> getBccListByFromAccId(long fromAccId) {
            List<BieEditBcc> bccList = bccListByFromAccIdMap.get(fromAccId);
            if (bccList == null) {
                return Collections.emptyList();
            }

            return bccList.stream().collect(groupingBy(BieEditBcc::getGuid)).values().stream()
                    .map(e -> CcUtility.getLatestEntity(topLevelAbie.getReleaseId(), e))
                    .filter(e -> e != null)
                    .collect(Collectors.toList());
        }

        public BieEditAsbie getAsbie(long asbieId) {
            return asbieMap.get(asbieId);
        }

        public List<BieEditAsbie> getAsbieListByFromAbieId(long fromAbieId) {
            List<BieEditAsbie> asbieList = asbieListByFromAbieIdMap.get(fromAbieId);
            return (asbieList != null) ? asbieList : Collections.emptyList();
        }

        public BieEditBbie getBbie(long bbieId) {
            return bbieMap.get(bbieId);
        }

        public List<BieEditBbie> getBbieListByFromAbieId(long fromAbieId) {
            List<BieEditBbie> bbieList = bbieListByFromAbieIdMap.get(fromAbieId);
            return (bbieList != null) ? bbieList : Collections.emptyList();
        }

        public BieEditAsccp getAsccp(long asccpId) {
            return asccpMap.get(asccpId);
        }

        public BieEditAsccp getAsccpByCurrentAsccpId(long currentAsccpId) {
            List<BieEditAsccp> asccpList = asccpByCurrentAsccpIdMap.get(currentAsccpId);
            if (asccpList == null) {
                return null;
            }
            return CcUtility.getLatestEntity(topLevelAbie.getReleaseId(), asccpList);
        }

        public BieEditAsbiep getAsbiep(long asbiepId) {
            return asbiepMap.get(asbiepId);
        }

        public List<BieEditAsbiep> getAsbiepByRoleOfAbieId(long roleOfAbieId) {
            List<BieEditAsbiep> asbiepList = asbiepListByRoleOfAbieIdMap.get(roleOfAbieId);
            return (asbiepList != null) ? asbiepList : Collections.emptyList();
        }

        public BieEditBccp getBccp(long bccpId) {
            return bccpMap.get(bccpId);
        }

        public BieEditBccp getBccpByCurrentBccpId(long currentBccpId) {
            List<BieEditBccp> bccpList = bccpByCurrentBccpIdMap.get(currentBccpId);
            if (bccpList == null) {
                return null;
            }
            return CcUtility.getLatestEntity(topLevelAbie.getReleaseId(), bccpList);
        }

        public List<BieEditBdtSc> getBdtScByOwnerDtId(long ownerDtId) {
            List<BieEditBdtSc> bdtScList = bdtScListByBdtIdMap.get(ownerDtId);
            return (bdtScList != null) ? bdtScList : Collections.emptyList();
        }

        public BieEditBbiep getBbiep(long bbiepId) {
            return bbiepMap.get(bbiepId);
        }

        public List<BieEditBbieSc> getBbieScByBbieId(long bbieId) {
            List<BieEditBbieSc> bbieScList = bbieScListByBbieIdMap.get(bbieId);
            return (bbieScList != null) ? bbieScList : Collections.emptyList();
        }

    }


    private String GET_BIE_EDIT_ACC_LIST_STATEMENT =
            "SELECT acc_id, guid, based_acc_id, current_acc_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM acc WHERE revision_num > 0";

    public List<BieEditAcc> getBieEditAccList() {
        return jdbcTemplate.query(GET_BIE_EDIT_ACC_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieEditAcc.class));
    }

    private String GET_BIE_EDIT_ASCC_LIST_STATEMENT =
            "SELECT ascc_id, guid, from_acc_id, to_asccp_id, seq_key, current_ascc_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM ascc WHERE revision_num > 0";

    public List<BieEditAscc> getBieEditAsccList() {
        return jdbcTemplate.query(GET_BIE_EDIT_ASCC_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieEditAscc.class));
    }

    private String GET_BIE_EDIT_BCC_LIST_STATEMENT =
            "SELECT bcc_id, guid, from_acc_id, to_bccp_id, seq_key, entity_type, current_bcc_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM bcc WHERE revision_num > 0";

    public List<BieEditBcc> getBieEditBccList() {
        return jdbcTemplate.query(GET_BIE_EDIT_BCC_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieEditBcc.class));
    }

    private String GET_BIE_EDIT_ASCCP_LIST_STATEMENT =
            "SELECT asccp_id, guid, property_term, role_of_acc_id, current_asccp_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM asccp WHERE revision_num > 0";

    public List<BieEditAsccp> getBieEditAsccpList() {
        return jdbcTemplate.query(GET_BIE_EDIT_ASCCP_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieEditAsccp.class));
    }

    private String GET_BIE_EDIT_BCCP_LIST_STATEMENT =
            "SELECT bccp_id, guid, property_term, bdt_id, current_bccp_id, " +
                    "revision_num, revision_tracking_num, revision_action, release_id FROM bccp WHERE revision_num > 0";

    public List<BieEditBccp> getBieEditBccpList() {
        return jdbcTemplate.query(GET_BIE_EDIT_BCCP_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieEditBccp.class));
    }

    private String GET_BIE_EDIT_BDT_SC_LIST_STATEMENT =
            "SELECT dt_sc_id, guid, property_term, representation_term, owner_dt_id FROM dt_sc WHERE cardinality_max != 0";

    public List<BieEditBdtSc> getBieEditBdtScList() {
        return jdbcTemplate.query(GET_BIE_EDIT_BDT_SC_LIST_STATEMENT,
                new BeanPropertyRowMapper(BieEditBdtSc.class));
    }

    private String GET_BIE_EDIT_ABIE_LIST_STATEMENT =
            "SELECT abie_id, based_acc_id FROM abie WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditAbie> getBieEditAbieList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_ABIE_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditAbie.class));
    }

    private String GET_BIE_EDIT_ASBIE_LIST_STATEMENT =
            "SELECT asbie_id, from_abie_id, to_asbiep_id, based_ascc_id, is_used as used " +
                    "FROM asbie WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditAsbie> getBieEditAsbieList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_ASBIE_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditAsbie.class));
    }

    private String GET_BIE_EDIT_BBIE_LIST_STATEMENT =
            "SELECT bbie_id, from_abie_id, to_bbiep_id, based_bcc_id, is_used as used " +
                    "FROM bbie WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditBbie> getBieEditBbieList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_BBIE_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditBbie.class));
    }

    private String GET_BIE_EDIT_ASBIEP_LIST_STATEMENT =
            "SELECT asbiep_id, based_asccp_id, role_of_abie_id " +
                    "FROM asbiep WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditAsbiep> getBieEditAsbiepList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_ASBIEP_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditAsbiep.class));
    }

    private String GET_BIE_EDIT_BBIEP_LIST_STATEMENT =
            "SELECT bbiep_id, based_bccp_id " +
                    "FROM bbiep WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditBbiep> getBieEditBbiepList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_BBIEP_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditBbiep.class));
    }

    private String GET_BIE_EDIT_BBIE_SC_LIST_STATEMENT =
            "SELECT bbie_sc_id, bbie_id, dt_sc_id, is_used as used " +
                    "FROM bbie_sc WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditBbieSc> getBieEditBbieScList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_BBIE_SC_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditBbieSc.class));
    }

}
