package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.*;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.oagi.srt.gateway.http.helper.Utility;
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
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("top_level_abie_id", topLevelAbieId);

        List<BieEditAbieNode> res = jdbcTemplate.query(GET_ROOT_NODE_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditAbieNode.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        BieEditAbieNode rootNode = res.get(0);
        rootNode.setHasChild(hasChild(rootNode));

        return rootNode;
    }

    public List<BieEditNode> getAsbiepChildren(BieEditAsbiepNode asbiepNode) {
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        long currentAccId = repository.getAcc(asbiepNode.getAccId()).getCurrentAccId();
        long abieId = asbiepNode.getAbieId();
        if (abieId > 0L) {
            asbieMap = getAsbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditAsbie::getBasedAsccpId, Function.identity()));
            bbieMap = getBbieListByFromAbieId(abieId, asbiepNode).stream()
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

            String name;
            if (bdtSc.getRepresentationTerm().equalsIgnoreCase("Text") ||
                    bdtSc.getPropertyTerm().contains(bdtSc.getRepresentationTerm())) {
                name = Utility.spaceSeparator(bdtSc.getPropertyTerm());
            } else {
                name = Utility.spaceSeparator(bdtSc.getPropertyTerm().concat(bdtSc.getRepresentationTerm()));
            }

            bbieScNode.setName(name);
            if (bbieId > 0L) {
                bbieScNode.setBbieScId(repository.getBbieScIdByBbieIdAndDtScId(
                        bbieId, bdtSc.getDtScId(), bbiepNode.getTopLevelAbieId()));
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

        while (acc.getBasedAccId() > 0L) {
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
            List<BieEditAscc> asccList = getAsccListByFromAccId(fromAccId, releaseId);
            List<BieEditBcc> bccList = getBccListByFromAccId(fromAccId, releaseId);

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
            long abieId = getAbieByAsbiepId(asbiepId).getAbieId();
            asbieMap = getAsbieListByFromAbieId(abieId, abieNode).stream()
                    .collect(toMap(BieEditAsbie::getBasedAsccpId, Function.identity()));
            bbieMap = getBbieListByFromAbieId(abieId, abieNode).stream()
                    .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

            currentAccId = getRoleOfAccIdByAsbiepId(asbiepId);
        } else {
            asbieMap = Collections.emptyMap();
            bbieMap = Collections.emptyMap();

            long asccpId = abieNode.getAsccpId();
            currentAccId = getRoleOfAccIdByAsccpId(asccpId);
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
        TopLevelAbie topLevelAbie = getTopLevelAbie(node.getTopLevelAbieId());

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

            BieEditAbie abie = getAbieByAsbiepId(asbie.getToAsbiepId());
            asbiepNode.setAbieId(abie.getAbieId());
            asbiepNode.setAccId(abie.getBasedAccId());

            asbiepNode.setName(getAsccpPropertyTermByAsbiepId(asbie.getToAsbiepId()));
        }

        asbiepNode.setAsccId(ascc.getAsccId());
        BieEditAsccp asccp = getAsccpByCurrentAsccpId(ascc.getToAsccpId(), topLevelAbie.getReleaseId());
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

            bbiepNode.setName(getBccpPropertyTermByBbiepId(bbie.getToBbiepId()));
        }

        bbiepNode.setBccId(bcc.getBccId());
        BieEditBccp bccp = getBccpByCurrentBccpId(bcc.getToBccpId(), topLevelAbie.getReleaseId());
        bbiepNode.setBccpId(bccp.getBccpId());

        if (StringUtils.isEmpty(bbiepNode.getName())) {
            bbiepNode.setName(bccp.getPropertyTerm());
        }

        bbiepNode.setHasChild(hasChild(bbiepNode));

        return bbiepNode;
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

    public BieEditAbie getAbieByAsbiepId(long asbiepId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("asbiep_id", asbiepId);
        List<BieEditAbie> res = jdbcTemplate.query("SELECT abie.abie_id, abie.based_acc_id " +
                "FROM asbiep JOIN abie ON asbiep.role_of_abie_id = abie.abie_id " +
                "WHERE asbiep.asbiep_id = :asbiep_id", parameterSource, new BeanPropertyRowMapper(BieEditAbie.class));
        if (res.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return res.get(0);
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

    public List<BieEditAscc> getAsccListByFromAccId(long fromAccId, long releaseId) {
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

    public List<BieEditBcc> getBccListByFromAccId(long fromAccId, long releaseId) {
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

    public boolean hasChild(BieEditAbieNode abieNode) {
        long fromAccId;
        if (abieNode.getTopLevelAbieId() > 0L) {
            fromAccId = repository.getCurrentAccIdByTopLevelAbieId(abieNode.getTopLevelAbieId());
        } else {
            fromAccId = abieNode.getAccId();
        }

        long releaseId = abieNode.getReleaseId();
        if (getAsccListByFromAccId(fromAccId, releaseId).size() > 0) {
            return true;
        }
        if (getBccListByFromAccId(fromAccId, releaseId).size() > 0) {
            return true;
        }

        long currentAccId = fromAccId;
        BieEditAcc acc = repository.getAccByCurrentAccId(currentAccId, releaseId);
        if (acc.getBasedAccId() > 0L) {
            BieEditAbieNode basedAbieNode = new BieEditAbieNode();
            basedAbieNode.setReleaseId(releaseId);
            basedAbieNode.setAccId(acc.getBasedAccId());
            return hasChild(basedAbieNode);
        }

        return false;
    }

    public boolean hasChild(BieEditAsbiepNode asbiepNode) {
        BieEditAbieNode abieNode = new BieEditAbieNode();
        abieNode.setTopLevelAbieId(asbiepNode.getTopLevelAbieId());
        abieNode.setReleaseId(asbiepNode.getReleaseId());
        abieNode.setAccId(asbiepNode.getAccId());

        return hasChild(abieNode);
    }

    public boolean hasChild(BieEditBbiepNode bbiepNode) {
        BieEditBccp bccp = repository.getBccp(bbiepNode.getBccpId());
        return repository.getCountDtScByOwnerDtId(bccp.getBdtId()) > 0;
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
