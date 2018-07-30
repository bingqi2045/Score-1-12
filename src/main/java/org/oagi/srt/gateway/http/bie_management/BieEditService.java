package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.*;
import org.oagi.srt.gateway.http.cc_management.CcUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(readOnly = true)
public class BieEditService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private String GET_ROOT_NODE_STATEMENT =
            "SELECT 'ASCCP' as type, asbiep.asbiep_id as bie_id, asbiep.based_asccp_id as cc_id, asccp.property_term as name " +
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

        return res.get(0);
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
            case "ABIE":
                long abieId = node.getBieId();
                long accId = node.getCcId();

        }

    }


    private class BieTreeDataSource {
        TopLevelAbie topLevelAbie;

        List<BieEditAcc> accList;
        Map<Long, BieEditAcc> accMap;

        List<BieEditAscc> asccList;
        Map<Long, List<BieEditAscc>> asccListByFromAccIdMap;
        List<BieEditBcc> bccList;
        Map<Long, List<BieEditBcc>> bccListByFromAccIdMap;

        List<BieEditAsccp> asccpList;
        Map<Long, BieEditAsccp> asccpMap;
        List<BieEditBccp> bccpList;
        List<BieEditBdtSc> bdtScList;

        List<BieEditAbie> abieList;
        Map<Long, BieEditAbie> abieMap;

        List<BieEditAsbie> asbieList;
        Map<Long, List<BieEditAsbie>> asbieListByFromAbieIdMap;
        List<BieEditBbie> bbieList;
        Map<Long, List<BieEditBbie>> bbieListByFromAbieIdMap;

        List<BieEditAsbiep> asbiepList;
        Map<Long, List<BieEditAsbiep>> asbiepListByRoleOfAbieIdMap;
        List<BieEditBbiep> bbiepList;
        List<BieEditBbieSc> bbieScList;

        public BieTreeDataSource(TopLevelAbie topLevelAbie) {
            this.topLevelAbie = topLevelAbie;

            accList = getBieEditAccList();
            accMap = accList.stream().collect(Collectors.toMap(BieEditAcc::getAccId, Function.identity()));

            asccList = getBieEditAsccList();
            asccListByFromAccIdMap = asccList.stream().collect(groupingBy(BieEditAscc::getFromAccId));
            bccList = getBieEditBccList();
            bccListByFromAccIdMap = bccList.stream().collect(groupingBy(BieEditBcc::getFromAccId));

            asccpList = getBieEditAsccpList();
            asccpMap = asccpList.stream().collect(Collectors.toMap(BieEditAsccp::getAsccpId, Function.identity()));
            bccpList = getBieEditBccpList();
            bdtScList = getBieEditBdtScList();

            long topLevelAbieId = topLevelAbie.getTopLevelAbieId();
            abieList = getBieEditAbieList(topLevelAbieId);
            abieMap = abieList.stream().collect(Collectors.toMap(BieEditAbie::getAbieId, Function.identity()));

            asbieList = getBieEditAsbieList(topLevelAbieId);
            asbieListByFromAbieIdMap = asbieList.stream().collect(groupingBy(BieEditAsbie::getFromAbieId));
            bbieList = getBieEditBbieList(topLevelAbieId);
            bbieListByFromAbieIdMap = bbieList.stream().collect(groupingBy(BieEditBbie::getFromAbieId));

            asbiepList = getBieEditAsbiepList(topLevelAbieId);
            asbiepListByRoleOfAbieIdMap = asbiepList.stream().collect(groupingBy(BieEditAsbiep::getRoleOfAbieId));

            bbiepList = getBieEditBbiepList(topLevelAbieId);
            bbieScList = getBieEditBbieScList(topLevelAbieId);
        }

        public BieEditAcc getAcc(long accId) {
            return accMap.get(accId);
        }
        public BieEditAbie getAbie(long abieId) {
            return abieMap.get(abieId);
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
        public List<BieEditAsbiep> getAsbiepByRoleOfAbieId(long roleOfAbieId) {
            List<BieEditAsbiep> asbiepList = asbiepListByRoleOfAbieIdMap.get(roleOfAbieId);
            return (asbiepList != null) ? asbiepList : Collections.emptyList();
        }
        public BieEditAsccp getAsccp(long asccpId) {
            return asccpMap.get(asccpId);
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
            "SELECT bbiep_id, based_bccp_id, is_used as used " +
                    "FROM asbiep WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditBbiep> getBieEditBbiepList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_BBIEP_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditBbiep.class));
    }

    private String GET_BIE_EDIT_BBIE_SC_LIST_STATEMENT =
            "SELECT bbie_sc_id, bbie_id, is_used as used " +
                    "FROM bbie_sc WHERE owner_top_level_abie_id = :owner_top_level_abie_id";

    public List<BieEditBbieSc> getBieEditBbieScList(long topLevelAbieId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("owner_top_level_abie_id", topLevelAbieId);
        return jdbcTemplate.query(GET_BIE_EDIT_BBIE_SC_LIST_STATEMENT, parameterSource,
                new BeanPropertyRowMapper(BieEditBbieSc.class));
    }

}
