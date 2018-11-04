package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.jooq.impl.DSL.max;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CcListService ccListService;

    public CcAccNode getAccNode(User user, long accId, Long releaseId) {
        return repository.getAccNodeByAccId(accId, releaseId);
    }

    public CcAsccpNode getAsccpNode(User user, long asccpId, Long releaseId) {
        return repository.getAsccpNodeByAsccpId(asccpId, releaseId);
    }

    public CcBccpNode getBccpNode(User user, long bccpId, Long releaseId) {
        return repository.getBccpNodeByBccpId(bccpId, releaseId);
    }

    public CcAccNode getExtensionNode(User user, long extensionId, Long releaseId) {
        CcAccNode ueAcc = repository.getAccNodeByAccId(extensionId, null);
        CcAsccpNode asccpNode = repository.getAsccpNodeByRoleOfAccId(ueAcc.getAccId(), null);
        return repository.getAccNodeByAsccpIdFromAscc(asccpNode.getAsccpId(), releaseId);
    }

    @Transactional
    public void appendAsccp(User user, long extensionId, Long releaseId, long asccpId) {
        int nextSeqKey = getNextSeqKey(extensionId);

        asccpId = dslContext.select(ASCCP.CURRENT_ASCCP_ID)
                .from(ASCCP).where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(Long.class);

        org.oagi.srt.data.ASCC ascc = createASCC(user, extensionId, asccpId, nextSeqKey);
        createASCCHistory(ascc, releaseId);
    }

    private org.oagi.srt.data.ASCC createASCC(User user, long accId, long asccpId, int seqKey) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("ascc")
                .usingColumns("guid", "cardinality_min", "cardinality_max", "seq_key", "from_acc_id", "to_asccp_id",
                        "den", "is_deprecated",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num")
                .usingGeneratedKeyColumns("ascc_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(String.class);
        String asccpDen = dslContext.select(ASCCP.DEN)
                .from(ASCCP).where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOneInto(String.class);

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("cardinality_min", 0)
                .addValue("cardinality_max", 1)
                .addValue("seq_key", seqKey)
                .addValue("from_acc_id", accId)
                .addValue("to_asccp_id", asccpId)
                .addValue("den", accObjectClassTerm + ". " + asccpDen)
                .addValue("is_deprecated", false)
                .addValue("state", CcState.Editing.getValue())
                .addValue("revision_num", 0)
                .addValue("revision_tracking_num", 0)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("owner_user_id", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long asccId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return ccListService.getAscc(asccId);
    }

    private void createASCCHistory(org.oagi.srt.data.ASCC ascc, long releaseId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("ascc")
                .usingColumns("guid", "cardinality_min", "cardinality_max", "seq_key", "from_acc_id", "to_asccp_id",
                        "den", "is_deprecated", "current_ascc_id",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "revision_action", "release_id")
                .usingGeneratedKeyColumns("ascc_id");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", ascc.getGuid())
                .addValue("cardinality_min", ascc.getCardinalityMin())
                .addValue("cardinality_max", ascc.getCardinalityMax())
                .addValue("seq_key", ascc.getSeqKey())
                .addValue("from_acc_id", ascc.getFromAccId())
                .addValue("to_asccp_id", ascc.getToAsccpId())
                .addValue("den", ascc.getDen())
                .addValue("is_deprecated", ascc.isDeprecated())
                .addValue("current_ascc_id", ascc.getAsccId())
                .addValue("definition", ascc.getDefinition())
                .addValue("state", ascc.getState())
                .addValue("revision_num", 1)
                .addValue("revision_tracking_num", 1)
                .addValue("revision_action", RevisionAction.Insert.getValue())
                .addValue("release_id", releaseId)
                .addValue("created_by", ascc.getCreatedBy())
                .addValue("last_updated_by", ascc.getLastUpdatedBy())
                .addValue("owner_user_id", ascc.getOwnerUserId())
                .addValue("creation_timestamp", ascc.getCreationTimestamp())
                .addValue("last_update_timestamp", ascc.getLastUpdateTimestamp());

        jdbcInsert.execute(parameterSource);
    }

    @Transactional
    public void appendBccp(User user, long extensionId, Long releaseId, long bccpId) {
        int nextSeqKey = getNextSeqKey(extensionId);

        bccpId = dslContext.select(BCCP.CURRENT_BCCP_ID)
                .from(BCCP).where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(Long.class);

        org.oagi.srt.data.BCC bcc = createBCC(user, extensionId, bccpId, nextSeqKey);
        createBCCHistory(bcc, releaseId);
    }

    private org.oagi.srt.data.BCC createBCC(User user, long accId, long bccpId, int seqKey) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("bcc")
                .usingColumns("guid", "cardinality_min", "cardinality_max", "seq_key", "entity_type",
                        "from_acc_id", "to_bccp_id", "den", "is_deprecated",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num")
                .usingGeneratedKeyColumns("ascc_id");

        long userId = sessionService.userId(user);
        Date timestamp = new Date();

        String accObjectClassTerm = dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC).where(ACC.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(String.class);
        String asccpDen = dslContext.select(BCCP.DEN)
                .from(BCCP).where(BCCP.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOneInto(String.class);

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", SrtGuid.randomGuid())
                .addValue("cardinality_min", 0)
                .addValue("cardinality_max", 1)
                .addValue("seq_key", seqKey)
                .addValue("entity_type", BCCEntityType.Element.getValue())
                .addValue("from_acc_id", accId)
                .addValue("to_bccp_id", bccpId)
                .addValue("den", accObjectClassTerm + ". " + asccpDen)
                .addValue("is_deprecated", false)
                .addValue("state", CcState.Editing.getValue())
                .addValue("revision_num", 0)
                .addValue("revision_tracking_num", 0)
                .addValue("created_by", userId)
                .addValue("last_updated_by", userId)
                .addValue("owner_user_id", userId)
                .addValue("creation_timestamp", timestamp)
                .addValue("last_update_timestamp", timestamp);

        long bccId = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return ccListService.getBcc(bccId);
    }

    private void createBCCHistory(org.oagi.srt.data.BCC bcc, long releaseId) {
        SimpleJdbcInsert jdbcInsert = jdbcTemplate.insert()
                .withTableName("bcc")
                .usingColumns("guid", "cardinality_min", "cardinality_max", "seq_key", "entity_type",
                        "from_acc_id", "to_bccp_id", "den", "is_deprecated", "current_bcc_id",
                        "created_by", "last_updated_by", "owner_user_id", "creation_timestamp", "last_update_timestamp",
                        "state", "revision_num", "revision_tracking_num", "revision_action", "release_id")
                .usingGeneratedKeyColumns("ascc_id");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("guid", bcc.getGuid())
                .addValue("cardinality_min", bcc.getCardinalityMin())
                .addValue("cardinality_max", bcc.getCardinalityMax())
                .addValue("seq_key", bcc.getSeqKey())
                .addValue("entity_type", bcc.getEntityType())
                .addValue("from_acc_id", bcc.getFromAccId())
                .addValue("to_bccp_id", bcc.getToBccpId())
                .addValue("den", bcc.getDen())
                .addValue("is_deprecated", bcc.isDeprecated())
                .addValue("current_bcc_id", bcc.getBccId())
                .addValue("definition", bcc.getDefinition())
                .addValue("state", bcc.getState())
                .addValue("revision_num", 1)
                .addValue("revision_tracking_num", 1)
                .addValue("revision_action", RevisionAction.Insert.getValue())
                .addValue("release_id", releaseId)
                .addValue("created_by", bcc.getCreatedBy())
                .addValue("last_updated_by", bcc.getLastUpdatedBy())
                .addValue("owner_user_id", bcc.getOwnerUserId())
                .addValue("creation_timestamp", bcc.getCreationTimestamp())
                .addValue("last_update_timestamp", bcc.getLastUpdateTimestamp());

        jdbcInsert.execute(parameterSource);
    }

    private int getNextSeqKey(long accId) {
        Integer asccMaxSeqKey = dslContext.select(max(ASCC.SEQ_KEY))
                .from(ASCC)
                .where(ASCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (asccMaxSeqKey == null) {
            asccMaxSeqKey = 0;
        }

        Integer bccMaxSeqKey = dslContext.select(max(BCC.SEQ_KEY))
                .from(BCC)
                .where(BCC.FROM_ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOneInto(Integer.class);
        if (bccMaxSeqKey == null) {
            bccMaxSeqKey = 0;
        }

        return Math.max(asccMaxSeqKey, bccMaxSeqKey) + 1;
    }

    @Transactional
    public int getAccMaxId() {
        return repository.getAccMaxId();
    }

    public List<? extends CcNode> getDescendants(User user, CcAccNode accNode) {
        return repository.getDescendants(user, accNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        return repository.getDescendants(user, asccpNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        return repository.getDescendants(user, bccpNode);
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        return repository.getAccNodeDetail(user, accNode);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        return repository.getAsccpNodeDetail(user, asccpNode);
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        return repository.getBccpNodeDetail(user, bccpNode);
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        return repository.getBdtScNodeDetail(user, bdtScNode);
    }

    @Transactional
    public long createAcc(User user, CcAccNode ccAccNode) {
        return repository.createAcc(user, ccAccNode);
    }

    @Transactional
    public void updateAcc(User user, CcAccNode ccAccNode, long accId) {
        repository.updateAcc(user, ccAccNode);
    }

}

