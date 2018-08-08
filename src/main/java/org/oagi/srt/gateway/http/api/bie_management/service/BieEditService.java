package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.bie_management.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.common.data.OagisComponentType;
import org.oagi.srt.gateway.http.api.common.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.userdetails.User;
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
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;


    private void ensureOwnerUser(User user, long topLevelAbieId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("top_level_abie_id", topLevelAbieId);

        int intState = jdbcTemplate.queryForObject("SELECT state FROM top_level_abie " +
                        "WHERE top_level_abie_id = :top_level_abie_id",
                parameterSource, Integer.class);
        BieState state = BieState.valueOf(intState);
        if (state == BieState.Editing) {
            Integer res = jdbcTemplate.queryForObject("SELECT count(*) FROM top_level_abie " +
                    "WHERE top_level_abie_id = :top_level_abie_id AND owner_user_id = :owner_user_id", parameterSource
                    .addValue("owner_user_id", sessionService.userId(user)), Integer.class);
            if (res == null || res == 0) {
                throw new DataAccessForbiddenException("'" + user.getUsername() + "' doesn't have an access privilege.");
            }
        }

    }

    private String GET_ROOT_NODE_STATEMENT =
            "SELECT top_level_abie_id, top_level_abie.release_id, 'abie' as type, top_level_abie.state as top_level_abie_state, " +
                    "asccp.guid, asccp.property_term as name, " +
                    "asbiep.asbiep_id, asbiep.based_asccp_id as asccp_id, abie.abie_id, abie.based_acc_id as acc_id " +
                    "FROM top_level_abie JOIN abie ON top_level_abie.abie_id = abie.abie_id " +
                    "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
                    "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
                    "WHERE top_level_abie_id = :top_level_abie_id";

    public BieEditAbieNode getRootNode(User user, long topLevelAbieId) {
        ensureOwnerUser(user, topLevelAbieId);

        BieEditAbieNode rootNode = jdbcTemplate.queryForObject(GET_ROOT_NODE_STATEMENT, newSqlParameterSource()
                .addValue("top_level_abie_id", topLevelAbieId), BieEditAbieNode.class);
        rootNode.setHasChild(hasChild(rootNode));

        return rootNode;
    }

    @Transactional
    public void updateState(User user, long topLevelAbieId, BieState state) {
        ensureOwnerUser(user, topLevelAbieId);

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("state", state.getValue())
                .addValue("top_level_abie_id", topLevelAbieId);

        jdbcTemplate.update("UPDATE top_level_abie SET state = :state " +
                "WHERE top_level_abie_id = :top_level_abie_id", parameterSource);

        jdbcTemplate.update("UPDATE abie SET state = :state " +
                "WHERE owner_top_level_abie_id = :top_level_abie_id", parameterSource);
    }

    @Transactional
    public List<BieEditNode> getAsbiepChildren(User user, BieEditAsbiepNode asbiepNode) {
        ensureOwnerUser(user, asbiepNode.getTopLevelAbieId());
        return _getAsbiepChildren(user, asbiepNode);
    }

    private List<BieEditNode> _getAsbiepChildren(User user, BieEditAsbiepNode asbiepNode) {
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        BieEditAcc acc = repository.getAcc(asbiepNode.getAccId());
        long currentAccId = acc.getCurrentAccId();
        long abieId = asbiepNode.getAbieId();
        if (abieId == 0L) {
            abieId = repository.createAbie(user, acc.getAccId(), asbiepNode.getTopLevelAbieId());
        }

        asbieMap = repository.getAsbieListByFromAbieId(abieId, asbiepNode).stream()
                .collect(toMap(BieEditAsbie::getBasedAsccId, Function.identity()));
        bbieMap = repository.getBbieListByFromAbieId(abieId, asbiepNode).stream()
                .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

        List<BieEditNode> children = _getChildren(user, asbieMap, bbieMap, abieId, currentAccId, asbiepNode);
        return children;
    }

    @Transactional
    public List<BieEditNode> getBbiepChildren(User user, BieEditBbiepNode bbiepNode) {
        ensureOwnerUser(user, bbiepNode.getTopLevelAbieId());

        long bbiepId = bbiepNode.getBbiepId();
        long topLevelAbieId = bbiepNode.getTopLevelAbieId();

        BieEditBbiep bbiep = repository.getBbiep(bbiepId, topLevelAbieId);
        BieEditBccp bccp = repository.getBccp(bbiep.getBasedBccpId());

        List<BieEditNode> children = new ArrayList();
        List<BieEditBdtSc> bdtScList = repository.getBdtScListByOwnerDtId(bccp.getBdtId());
        long bbieId = bbiepNode.getBbieId();
        for (BieEditBdtSc bdtSc : bdtScList) {
            BieEditBbieScNode bbieScNode = new BieEditBbieScNode();

            bbieScNode.setTopLevelAbieId(bbiepNode.getTopLevelAbieId());
            bbieScNode.setReleaseId(bbiepNode.getReleaseId());
            bbieScNode.setType("bbie_sc");
            bbieScNode.setGuid(bdtSc.getGuid());
            bbieScNode.setName(bdtSc.getName());

            long dtScId = bdtSc.getDtScId();
            BieEditBbieSc bbieSc;
            try {
                bbieSc = repository.getBbieScIdByBbieIdAndDtScId(bbieId, dtScId, topLevelAbieId);
            } catch (EmptyResultDataAccessException e) {
                long bbieScId = repository.createBbieSc(user, bbieId, dtScId, topLevelAbieId);
                bbieSc = new BieEditBbieSc();
                bbieSc.setBbieScId(bbieScId);
            }

            bbieScNode.setBbieScId(bbieSc.getBbieScId());
            bbieScNode.setUsed(bbieSc.isUsed());
            bbieScNode.setDtScId(dtScId);

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

    private List<SeqKeySupportable> _getAssociationsByCurrentAccId(long currentAccId, long releaseId) {
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

    @Transactional
    public List<BieEditNode> getAbieChildren(User user, BieEditAbieNode abieNode) {
        ensureOwnerUser(user, abieNode.getTopLevelAbieId());

        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        long currentAccId;
        long asbiepId = abieNode.getAsbiepId();
        long abieId = repository.getAbieByAsbiepId(asbiepId).getAbieId();
        asbieMap = repository.getAsbieListByFromAbieId(abieId, abieNode).stream()
                .collect(toMap(BieEditAsbie::getBasedAsccId, Function.identity()));
        bbieMap = repository.getBbieListByFromAbieId(abieId, abieNode).stream()
                .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

        currentAccId = repository.getRoleOfAccIdByAsbiepId(asbiepId);

        List<BieEditNode> children = _getChildren(user, asbieMap, bbieMap, abieId, currentAccId, abieNode);
        return children;
    }

    private List<BieEditNode> _getChildren(
            User user,
            Map<Long, BieEditAsbie> asbieMap,
            Map<Long, BieEditBbie> bbieMap,
            long fromAbieId, long currentAccId,
            BieEditNode node) {
        List<BieEditNode> children = new ArrayList();
        TopLevelAbie topLevelAbie = repository.getTopLevelAbie(node.getTopLevelAbieId());

        List<SeqKeySupportable> assocList = _getAssociationsByCurrentAccId(currentAccId, node.getReleaseId());
        int seqKey = 1;
        for (SeqKeySupportable assoc : assocList) {
            if (assoc instanceof BieEditAscc) {
                BieEditAscc ascc = (BieEditAscc) assoc;
                BieEditAsbie asbie = asbieMap.get(ascc.getAsccId());
                BieEditAsbiepNode asbiepNode = _createAsbiepNode(user, topLevelAbie, fromAbieId, seqKey++, asbie, ascc);

                OagisComponentType oagisComponentType = repository.getOagisComponentTypeByAccId(asbiepNode.getAccId());
                if (oagisComponentType.isGroup()) {
                    children.addAll(_getAsbiepChildren(user, asbiepNode));
                } else {
                    children.add(asbiepNode);
                }
            } else {
                BieEditBcc bcc = (BieEditBcc) assoc;
                BieEditBbie bbie = bbieMap.get(bcc.getBccId());
                BieEditBbiepNode bbiepNode;
                if (bcc.isAttribute()) {
                    bbiepNode = _createBbiepNode(user, topLevelAbie, fromAbieId, 0, bbie, bcc);
                } else {
                    bbiepNode = _createBbiepNode(user, topLevelAbie, fromAbieId, seqKey++, bbie, bcc);
                }
                children.add(bbiepNode);
            }
        }

        return children;
    }

    public BieEditAsbiepNode _createAsbiepNode(User user, TopLevelAbie topLevelAbie,
                                               long fromAbieId, int seqKey,
                                               BieEditAsbie asbie, BieEditAscc ascc) {
        BieEditAsbiepNode asbiepNode = new BieEditAsbiepNode();
        long topLevelAbieId = topLevelAbie.getTopLevelAbieId();

        asbiepNode.setTopLevelAbieId(topLevelAbieId);
        asbiepNode.setReleaseId(topLevelAbieId);
        asbiepNode.setType("asbiep");
        asbiepNode.setGuid(ascc.getGuid());

        asbiepNode.setAsccId(ascc.getAsccId());
        BieEditAsccp asccp = repository.getAsccpByCurrentAsccpId(ascc.getToAsccpId(), topLevelAbieId);
        asbiepNode.setAsccpId(asccp.getAsccpId());

        BieEditAcc acc = repository.getAccByCurrentAccId(asccp.getRoleOfAccId(), topLevelAbieId);
        asbiepNode.setAccId(acc.getAccId());

        if (asbie == null) {
            long abieId = repository.createAbie(user, acc.getAccId(), topLevelAbieId);
            long asbiepId = repository.createAsbiep(user, asccp.getAsccpId(), abieId, topLevelAbieId);
            long asbieId = repository.createAsbie(user, fromAbieId, asbiepId, ascc.getAsccId(),
                    seqKey, topLevelAbieId);

            asbie = new BieEditAsbie();
            asbie.setAsbieId(asbieId);
            asbie.setBasedAsccId(ascc.getAsccId());
            asbie.setFromAbieId(fromAbieId);
            asbie.setToAsbiepId(asbiepId);
        }

        asbiepNode.setAsbieId(asbie.getAsbieId());
        asbiepNode.setAsbiepId(asbie.getToAsbiepId());

        BieEditAbie abie = repository.getAbieByAsbiepId(asbie.getToAsbiepId());
        asbiepNode.setAbieId(abie.getAbieId());
        asbiepNode.setAccId(abie.getBasedAccId());

        asbiepNode.setName(repository.getAsccpPropertyTermByAsbiepId(asbie.getToAsbiepId()));
        asbiepNode.setUsed(asbie.isUsed());

        if (StringUtils.isEmpty(asbiepNode.getName())) {
            asbiepNode.setName(asccp.getPropertyTerm());
        }

        asbiepNode.setHasChild(hasChild(asbiepNode));

        return asbiepNode;
    }

    public BieEditBbiepNode _createBbiepNode(User user, TopLevelAbie topLevelAbie,
                                             long fromAbieId, int seqKey,
                                             BieEditBbie bbie, BieEditBcc bcc) {
        BieEditBbiepNode bbiepNode = new BieEditBbiepNode();
        long topLevelAbieId = topLevelAbie.getTopLevelAbieId();

        bbiepNode.setTopLevelAbieId(topLevelAbieId);
        bbiepNode.setReleaseId(topLevelAbie.getReleaseId());
        bbiepNode.setType("bbiep");
        bbiepNode.setGuid(bcc.getGuid());
        bbiepNode.setAttribute(bcc.isAttribute());

        bbiepNode.setBccId(bcc.getBccId());
        BieEditBccp bccp = repository.getBccpByCurrentBccpId(bcc.getToBccpId(), topLevelAbieId);
        bbiepNode.setBccpId(bccp.getBccpId());
        bbiepNode.setBdtId(bccp.getBdtId());

        if (bbie == null) {
            long bbiepId = repository.createBbiep(user, bccp.getBccpId(), topLevelAbieId);
            long bbieId = repository.createBbie(user, fromAbieId, bbiepId,
                    bcc.getBccId(), bccp.getBdtId(), seqKey, topLevelAbieId);

            bbie = new BieEditBbie();
            bbie.setBasedBccId(bcc.getBccId());
            bbie.setBbieId(bbieId);
            bbie.setFromAbieId(fromAbieId);
            bbie.setToBbiepId(bbiepId);
        }

        bbiepNode.setBbieId(bbie.getBbieId());
        bbiepNode.setBbiepId(bbie.getToBbiepId());

        bbiepNode.setName(repository.getBccpPropertyTermByBbiepId(bbie.getToBbiepId()));
        bbiepNode.setUsed(bbie.isUsed());

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

    public BieEditAbieNodeDetail getAbieDetail(User user, BieEditAbieNode abieNode) {
        ensureOwnerUser(user, abieNode.getTopLevelAbieId());

        BieEditAbieNodeDetail detail =
                jdbcTemplate.queryForObject("SELECT version, status, remark, biz_term, definition " +
                                "FROM abie WHERE abie_id = :abie_id", newSqlParameterSource()
                                .addValue("abie_id", abieNode.getAbieId()),
                        BieEditAbieNodeDetail.class);
        return detail.append(abieNode);
    }

    public BieEditAsbiepNodeDetail getAsbiepDetail(User user, BieEditAsbiepNode asbiepNode) {
        ensureOwnerUser(user, asbiepNode.getTopLevelAbieId());

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("asbie_id", asbiepNode.getAsbieId())
                .addValue("asbiep_id", asbiepNode.getAsbiepId())
                .addValue("ascc_id", asbiepNode.getAsccId())
                .addValue("asccp_id", asbiepNode.getAsccpId())
                .addValue("acc_id", asbiepNode.getAccId());

        BieEditAsbiepNodeDetail detail =
                jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max, is_used as used, " +
                                "is_nillable as nillable, definition as context_definition " +
                                "FROM asbie WHERE asbie_id = :asbie_id",
                        parameterSource, BieEditAsbiepNodeDetail.class);

        jdbcTemplate.query("SELECT biz_term, remark FROM asbiep WHERE asbiep_id = :asbiep_id",
                parameterSource, rs -> {
                    detail.setBizTerm(rs.getString("biz_term"));
                    detail.setRemark(rs.getString("remark"));
                });

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

    public BieEditBbiepNodeDetail getBbiepDetail(User user, BieEditBbiepNode bbiepNode) {
        ensureOwnerUser(user, bbiepNode.getTopLevelAbieId());

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bbie_id", bbiepNode.getBbieId())
                .addValue("bbiep_id", bbiepNode.getBbiepId())
                .addValue("bcc_id", bbiepNode.getBccId())
                .addValue("bccp_id", bbiepNode.getBccpId());

        BieEditBbiepNodeDetail detail =
                jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max, is_used as used, " +
                                "bdt_pri_restri_id, code_list_id, agency_id_list_id, " +
                                "is_nillable as nillable, fixed_value, definition as context_definition " +
                                "FROM bbie WHERE bbie_id = :bbie_id",
                        parameterSource, BieEditBbiepNodeDetail.class);

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

        BieEditBdtPriRestri bdtPriRestri = getBdtPriRestri(bbiepNode);
        detail.setXbtList(bdtPriRestri.getXbtList());
        detail.setCodeLists(bdtPriRestri.getCodeLists());
        detail.setAgencyIdLists(bdtPriRestri.getAgencyIdLists());

        jdbcTemplate.query("SELECT definition FROM bcc WHERE bcc_id = :bcc_id", parameterSource, rs -> {
            detail.setAssociationDefinition(rs.getString("definition"));
        });

        jdbcTemplate.query("SELECT definition FROM bccp WHERE bccp_id = :bccp_id", parameterSource, rs -> {
            detail.setComponentDefinition(rs.getString("definition"));
        });

        return detail.append(bbiepNode);
    }

    public BieEditBbieScNodeDetail getBbieScDetail(User user, BieEditBbieScNode bbieScNode) {
        ensureOwnerUser(user, bbieScNode.getTopLevelAbieId());

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bbie_sc_id", bbieScNode.getBbieScId())
                .addValue("dt_sc_id", bbieScNode.getDtScId());

        BieEditBbieScNodeDetail detail =
                jdbcTemplate.queryForObject("SELECT cardinality_min, cardinality_max, is_used as used, " +
                                "dt_sc_pri_restri_id, code_list_id, agency_id_list_id, " +
                                "default_value, fixed_value, biz_term, remark, definition as context_definition " +
                                "FROM bbie_sc WHERE bbie_sc_id = :bbie_sc_id",
                        parameterSource, BieEditBbieScNodeDetail.class);

        BieEditBdtScPriRestri bdtScPriRestri = getBdtScPriRestri(bbieScNode);
        detail.setXbtList(bdtScPriRestri.getXbtList());
        detail.setCodeLists(bdtScPriRestri.getCodeLists());
        detail.setAgencyIdLists(bdtScPriRestri.getAgencyIdLists());

        jdbcTemplate.query("SELECT definition FROM dt_sc WHERE dt_sc_id = :dt_sc_id", parameterSource, rs -> {
            detail.setComponentDefinition(rs.getString("definition"));
        });

        return detail.append(bbieScNode);
    }

    private List<BieEditCodeList> getAllCodeLists() {
        return jdbcTemplate.queryForList("SELECT code_list_id, name as code_list_name FROM code_list",
                BieEditCodeList.class);
    }

    private List<BieEditAgencyIdList> getAllAgencyIdLists() {
        return jdbcTemplate.queryForList("SELECT agency_id_list_id, name as agency_id_list_name FROM agency_id_list",
                BieEditAgencyIdList.class);
    }

    private List<BieEditCodeList> getBieEditCodeListByBasedCodeListIds(List<Long> basedCodeListIds) {
        if (basedCodeListIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<BieEditCodeList> bieEditCodeLists = jdbcTemplate.queryForList(
                "SELECT code_list_id, based_code_list_id, name as code_list_name " +
                        "FROM code_list WHERE code_list_id IN (:based_code_list_ids)", newSqlParameterSource()
                        .addValue("based_code_list_ids", basedCodeListIds), BieEditCodeList.class);

        List<BieEditCodeList> basedCodeLists =
                getBieEditCodeListByBasedCodeListIds(
                        bieEditCodeLists.stream().filter(e -> e.getBasedCodeListId() != null)
                                .map(e -> e.getBasedCodeListId()).collect(Collectors.toList())
                );

        bieEditCodeLists.addAll(0, basedCodeLists);
        return bieEditCodeLists;
    }

    public BieEditBdtPriRestri getBdtPriRestri(BieEditBbiepNode bbiepNode) {
        long bdtId = bbiepNode.getBdtId();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bdt_id", bdtId);

        List<BieEditXbt> bieEditXbtList = jdbcTemplate.queryForList(
                "SELECT b.bdt_pri_restri_id AS pri_restri_id, is_default, x.xbt_id, x.name as xbt_name " +
                        "FROM bdt_pri_restri b JOIN cdt_awd_pri_xps_type_map c " +
                        "ON b.cdt_awd_pri_xps_type_map_id = c.cdt_awd_pri_xps_type_map_id " +
                        "JOIN xbt x ON c.xbt_id = x.xbt_id WHERE bdt_id = :bdt_id", parameterSource, BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = jdbcTemplate.queryForList(
                "SELECT c.code_list_id, c.based_code_list_id, b.is_default, c.name as code_list_name " +
                        "FROM bdt_pri_restri b JOIN code_list c ON b.code_list_id = c.code_list_id " +
                        "WHERE bdt_id = :bdt_id", newSqlParameterSource()
                        .addValue("bdt_id", bdtId), BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = jdbcTemplate.queryForList(
                "SELECT a.agency_id_list_id, b.is_default, a.name as agency_id_list_name " +
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

    public BieEditBdtScPriRestri getBdtScPriRestri(BieEditBbieScNode bbieScNode) {
        long dtScId = bbieScNode.getDtScId();

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("bdt_sc_id", dtScId);

        List<BieEditXbt> bieEditXbtList = jdbcTemplate.queryForList(
                "SELECT b.bdt_sc_pri_restri_id AS pri_restri_id, is_default, x.xbt_id, x.name as xbt_name " +
                        "FROM bdt_sc_pri_restri b JOIN cdt_sc_awd_pri_xps_type_map c " +
                        "ON b.cdt_sc_awd_pri_xps_type_map_id = c.cdt_sc_awd_pri_xps_type_map_id " +
                        "JOIN xbt x ON c.xbt_id = x.xbt_id WHERE bdt_sc_id = :bdt_sc_id", parameterSource, BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = jdbcTemplate.queryForList(
                "SELECT c.code_list_id, c.based_code_list_id, b.is_default, c.name as code_list_name " +
                        "FROM bdt_sc_pri_restri b JOIN code_list c ON b.code_list_id = c.code_list_id " +
                        "WHERE bdt_sc_id = :bdt_sc_id", parameterSource, BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = jdbcTemplate.queryForList(
                "SELECT a.agency_id_list_id, b.is_default, a.name as agency_id_list_name " +
                        "FROM bdt_sc_pri_restri b JOIN agency_id_list a ON b.agency_id_list_id = a.agency_id_list_id " +
                        "WHERE bdt_sc_id = :bdt_sc_id", parameterSource, BieEditAgencyIdList.class);

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

        BieEditBdtScPriRestri bdtScPriRestri = new BieEditBdtScPriRestri();

        bdtScPriRestri.setXbtList(bieEditXbtList);
        bdtScPriRestri.setCodeLists(bieEditCodeLists);
        bdtScPriRestri.setAgencyIdLists(bieEditAgencyIdLists);

        return bdtScPriRestri;
    }


    @Transactional
    public BieEditUpdateResponse updateDetails(User user, BieEditUpdateRequest request) {
        long topLevelAbieId = request.getTopLevelAbieId();
        ensureOwnerUser(user, topLevelAbieId);

        BieEditUpdateResponse response = new BieEditUpdateResponse();
        BieEditAbieNodeDetail abieNodeDetail = request.getAbieNodeDetail();
        if (abieNodeDetail != null) {
            updateAbieNodeDetail(user, abieNodeDetail);
            response.setAbieNodeResult(true);
        }

        for (BieEditAsbiepNodeDetail asbiepNodeDetail : request.getAsbiepNodeDetails()) {
            updateAsbiepNodeDetail(user, asbiepNodeDetail);
            response.getAsbiepNodeResults().put(asbiepNodeDetail.getGuid(), true);
        }

        for (BieEditBbiepNodeDetail bbiepNodeDetail : request.getBbiepNodeDetails()) {
            updateBbiepNodeDetail(user, bbiepNodeDetail);
            response.getBbiepNodeResults().put(bbiepNodeDetail.getGuid(), true);
        }

        for (BieEditBbieScNodeDetail bbieScNodeDetail : request.getBbieScNodeDetails()) {
            updateBbieScNodeDetail(user, bbieScNodeDetail);
            response.getBbieScNodeResults().put(bbieScNodeDetail.getGuid(), true);
        }

        return response;
    }

    private void updateAbieNodeDetail(User user, BieEditAbieNodeDetail abieNodeDetail) {
        jdbcTemplate.update("UPDATE abie SET version = :version, status = :status, remark = :remark, " +
                "biz_term = :biz_term, definition = :definition, " +
                "last_updated_by = :last_updated_by, last_update_timestamp = :last_update_timestamp " +
                "WHERE abie_id = :abie_id", newSqlParameterSource()
                .addValue("version", emptyToNull(abieNodeDetail.getVersion()))
                .addValue("status", emptyToNull(abieNodeDetail.getStatus()))
                .addValue("remark", emptyToNull(abieNodeDetail.getRemark()))
                .addValue("biz_term", emptyToNull(abieNodeDetail.getBizTerm()))
                .addValue("definition", emptyToNull(abieNodeDetail.getDefinition()))
                .addValue("last_updated_by", sessionService.userId(user))
                .addValue("last_update_timestamp", new Date())
                .addValue("abie_id", abieNodeDetail.getAbieId()));
    }

    private void updateAsbiepNodeDetail(User user, BieEditAsbiepNodeDetail asbiepNodeDetail) {
        List<String> assignmentList = new ArrayList();

        MapSqlParameterSource parameterSource = newSqlParameterSource();
        if (asbiepNodeDetail.getCardinalityMin() != null) {
            parameterSource.addValue("cardinality_min", asbiepNodeDetail.getCardinalityMin());
            assignmentList.add("cardinality_min = :cardinality_min");
        }
        if (asbiepNodeDetail.getCardinalityMax() != null) {
            parameterSource.addValue("cardinality_max", asbiepNodeDetail.getCardinalityMax());
            assignmentList.add("cardinality_max = :cardinality_max");
        }
        if (asbiepNodeDetail.getNillable() != null) {
            parameterSource.addValue("nillable", asbiepNodeDetail.getNillable());
            assignmentList.add("is_nillable = :nillable");
        }

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        parameterSource.addValue("used", asbiepNodeDetail.isUsed())
                .addValue("definition", emptyToNull(asbiepNodeDetail.getContextDefinition()))
                .addValue("last_updated_by", userId)
                .addValue("last_update_timestamp", timestamp)
                .addValue("asbie_id", asbiepNodeDetail.getAsbieId());

        assignmentList.add("is_used = :used");
        assignmentList.add("definition = :definition");
        assignmentList.add("last_updated_by = :last_updated_by");
        assignmentList.add("last_update_timestamp = :last_update_timestamp");

        jdbcTemplate.update("UPDATE asbie SET " +
                assignmentList.stream().collect(Collectors.joining(", ")) +
                " WHERE asbie_id = :asbie_id", parameterSource);

        jdbcTemplate.update("UPDATE asbiep SET biz_term = :biz_term, remark = :remark, " +
                "last_updated_by = :last_updated_by, last_update_timestamp = :last_update_timestamp " +
                "WHERE asbiep_id = :asbiep_id", newSqlParameterSource()
                .addValue("biz_term", emptyToNull(asbiepNodeDetail.getBizTerm()))
                .addValue("remark", emptyToNull(asbiepNodeDetail.getRemark()))
                .addValue("last_updated_by", userId)
                .addValue("last_update_timestamp", timestamp)
                .addValue("asbiep_id", asbiepNodeDetail.getAsbiepId()));
    }

    private void updateBbiepNodeDetail(User user, BieEditBbiepNodeDetail bbiepNodeDetail) {
        List<String> assignmentList = new ArrayList();

        MapSqlParameterSource parameterSource = newSqlParameterSource();
        if (bbiepNodeDetail.getCardinalityMin() != null) {
            parameterSource.addValue("cardinality_min", bbiepNodeDetail.getCardinalityMin());
            assignmentList.add("cardinality_min = :cardinality_min");
        }
        if (bbiepNodeDetail.getCardinalityMax() != null) {
            parameterSource.addValue("cardinality_max", bbiepNodeDetail.getCardinalityMax());
            assignmentList.add("cardinality_max = :cardinality_max");
        }
        if (bbiepNodeDetail.getNillable() != null) {
            parameterSource.addValue("nillable", bbiepNodeDetail.getNillable());
            assignmentList.add("is_nillable = :nillable");
        }

        Long bdtPriRestriId = bbiepNodeDetail.getBdtPriRestriId();
        Long codeListId = bbiepNodeDetail.getCodeListId();
        Long agencyIdListId = bbiepNodeDetail.getAgencyIdListId();

        if (bdtPriRestriId != null || codeListId != null || agencyIdListId != null) {
            parameterSource.addValue("bdt_pri_restri_id", bdtPriRestriId);
            assignmentList.add("bdt_pri_restri_id = :bdt_pri_restri_id");

            parameterSource.addValue("code_list_id", codeListId);
            assignmentList.add("code_list_id = :code_list_id");

            parameterSource.addValue("agency_id_list_id", agencyIdListId);
            assignmentList.add("agency_id_list_id = :agency_id_list_id");
        }

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        parameterSource.addValue("used", bbiepNodeDetail.isUsed())
                .addValue("fixed_value", emptyToNull(bbiepNodeDetail.getFixedValue()))
                .addValue("definition", emptyToNull(bbiepNodeDetail.getContextDefinition()))
                .addValue("last_updated_by", userId)
                .addValue("last_update_timestamp", timestamp)
                .addValue("bbie_id", bbiepNodeDetail.getBbieId());

        assignmentList.add("is_used = :used");
        assignmentList.add("fixed_value = :fixed_value");
        assignmentList.add("definition = :definition");
        assignmentList.add("last_updated_by = :last_updated_by");
        assignmentList.add("last_update_timestamp = :last_update_timestamp");

        jdbcTemplate.update("UPDATE bbie SET " +
                assignmentList.stream().collect(Collectors.joining(", ")) +
                " WHERE bbie_id = :bbie_id", parameterSource);

        jdbcTemplate.update("UPDATE bbiep SET biz_term = :biz_term, remark = :remark, " +
                "last_updated_by = :last_updated_by, last_update_timestamp = :last_update_timestamp " +
                "WHERE bbiep_id = :bbiep_id", newSqlParameterSource()
                .addValue("biz_term", emptyToNull(bbiepNodeDetail.getBizTerm()))
                .addValue("remark", emptyToNull(bbiepNodeDetail.getRemark()))
                .addValue("last_updated_by", userId)
                .addValue("last_update_timestamp", timestamp)
                .addValue("bbiep_id", bbiepNodeDetail.getBbiepId()));
    }

    private void updateBbieScNodeDetail(User user, BieEditBbieScNodeDetail bbieScNodeDetail) {
        List<String> assignmentList = new ArrayList();

        MapSqlParameterSource parameterSource = newSqlParameterSource();
        if (bbieScNodeDetail.getCardinalityMin() != null) {
            parameterSource.addValue("cardinality_min", bbieScNodeDetail.getCardinalityMin());
            assignmentList.add("cardinality_min = :cardinality_min");
        }
        if (bbieScNodeDetail.getCardinalityMax() != null) {
            parameterSource.addValue("cardinality_max", bbieScNodeDetail.getCardinalityMax());
            assignmentList.add("cardinality_max = :cardinality_max");
        }

        Long dtScPriRestriId = bbieScNodeDetail.getDtScPriRestriId();
        Long codeListId = bbieScNodeDetail.getCodeListId();
        Long agencyIdListId = bbieScNodeDetail.getAgencyIdListId();
        if (dtScPriRestriId != null || codeListId != null || agencyIdListId != null) {
            parameterSource.addValue("dt_sc_pri_restri_id", dtScPriRestriId);
            assignmentList.add("dt_sc_pri_restri_id = :dt_sc_pri_restri_id");

            parameterSource.addValue("code_list_id", codeListId);
            assignmentList.add("code_list_id = :code_list_id");

            parameterSource.addValue("agency_id_list_id", agencyIdListId);
            assignmentList.add("agency_id_list_id = :agency_id_list_id");
        }

        parameterSource.addValue("used", bbieScNodeDetail.isUsed())
                .addValue("default_value", emptyToNull(bbieScNodeDetail.getDefaultValue()))
                .addValue("fixed_value", emptyToNull(bbieScNodeDetail.getFixedValue()))
                .addValue("definition", emptyToNull(bbieScNodeDetail.getContextDefinition()))
                .addValue("biz_term", emptyToNull(bbieScNodeDetail.getBizTerm()))
                .addValue("remark", emptyToNull(bbieScNodeDetail.getRemark()))
                .addValue("bbie_sc_id", bbieScNodeDetail.getBbieScId());

        assignmentList.add("is_used = :used");
        assignmentList.add("default_value = :default_value");
        assignmentList.add("fixed_value = :fixed_value");
        assignmentList.add("definition = :definition");
        assignmentList.add("biz_term = :biz_term");
        assignmentList.add("remark = :remark");

        jdbcTemplate.update("UPDATE bbie_sc SET " +
                assignmentList.stream().collect(Collectors.joining(", ")) +
                " WHERE bbie_sc_id = :bbie_sc_id", parameterSource);
    }

    private String emptyToNull(String str) {
        if (str != null) {
            str = str.trim();
        }
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str;
    }
}
