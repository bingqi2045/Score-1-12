/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;
import org.oagi.srt.entity.jooq.tables.*;


/**
 * A class modelling indexes of tables of the <code>oagi</code> schema.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index ACC_ACC_GUID_IDX = Indexes0.ACC_ACC_GUID_IDX;
    public static final Index ACC_ACC_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.ACC_ACC_LAST_UPDATE_TIMESTAMP_DESC_IDX;
    public static final Index APP_GROUP_USER_APP_USER_ID = Indexes0.APP_GROUP_USER_APP_USER_ID;
    public static final Index APP_PERMISSION_GROUP_APP_PERMISSION_ID = Indexes0.APP_PERMISSION_GROUP_APP_PERMISSION_ID;
    public static final Index ASCC_ASCC_GUID_IDX = Indexes0.ASCC_ASCC_GUID_IDX;
    public static final Index ASCC_ASCC_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.ASCC_ASCC_LAST_UPDATE_TIMESTAMP_DESC_IDX;
    public static final Index ASCCP_ASCCP_GUID_IDX = Indexes0.ASCCP_ASCCP_GUID_IDX;
    public static final Index ASCCP_ASCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.ASCCP_ASCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX;
    public static final Index BCC_BCC_GUID_IDX = Indexes0.BCC_BCC_GUID_IDX;
    public static final Index BCC_BCC_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.BCC_BCC_LAST_UPDATE_TIMESTAMP_DESC_IDX;
    public static final Index BCCP_BCCP_GUID_IDX = Indexes0.BCCP_BCCP_GUID_IDX;
    public static final Index BCCP_BCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.BCCP_BCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX;
    public static final Index BIZ_CTX_ASSIGNMENT_BIZ_CTX_ID = Indexes0.BIZ_CTX_ASSIGNMENT_BIZ_CTX_ID;
    public static final Index BIZ_CTX_ASSIGNMENT_TOP_LEVEL_ABIE_ID = Indexes0.BIZ_CTX_ASSIGNMENT_TOP_LEVEL_ABIE_ID;
    public static final Index COMMENT_REFERENCE = Indexes0.COMMENT_REFERENCE;
    public static final Index DT_DT_GUID_IDX = Indexes0.DT_DT_GUID_IDX;
    public static final Index DT_DT_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.DT_DT_LAST_UPDATE_TIMESTAMP_DESC_IDX;
    public static final Index DT_SC_DT_SC_GUID_IDX = Indexes0.DT_SC_DT_SC_GUID_IDX;
    public static final Index REVISION_REFERENCE = Indexes0.REVISION_REFERENCE;
    public static final Index XBT_XBT_GUID_IDX = Indexes0.XBT_XBT_GUID_IDX;
    public static final Index XBT_XBT_LAST_UPDATE_TIMESTAMP_DESC_IDX = Indexes0.XBT_XBT_LAST_UPDATE_TIMESTAMP_DESC_IDX;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index ACC_ACC_GUID_IDX = Internal.createIndex("acc_guid_idx", Acc.ACC, new OrderField[]{Acc.ACC.GUID}, false);
        public static Index ACC_ACC_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("acc_last_update_timestamp_desc_idx", Acc.ACC, new OrderField[]{Acc.ACC.LAST_UPDATE_TIMESTAMP}, false);
        public static Index APP_GROUP_USER_APP_USER_ID = Internal.createIndex("app_user_id", AppGroupUser.APP_GROUP_USER, new OrderField[]{AppGroupUser.APP_GROUP_USER.APP_USER_ID}, false);
        public static Index APP_PERMISSION_GROUP_APP_PERMISSION_ID = Internal.createIndex("app_permission_id", AppPermissionGroup.APP_PERMISSION_GROUP, new OrderField[]{AppPermissionGroup.APP_PERMISSION_GROUP.APP_PERMISSION_ID}, false);
        public static Index ASCC_ASCC_GUID_IDX = Internal.createIndex("ascc_guid_idx", Ascc.ASCC, new OrderField[]{Ascc.ASCC.GUID}, false);
        public static Index ASCC_ASCC_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("ascc_last_update_timestamp_desc_idx", Ascc.ASCC, new OrderField[]{Ascc.ASCC.LAST_UPDATE_TIMESTAMP}, false);
        public static Index ASCCP_ASCCP_GUID_IDX = Internal.createIndex("asccp_guid_idx", Asccp.ASCCP, new OrderField[]{Asccp.ASCCP.GUID}, false);
        public static Index ASCCP_ASCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("asccp_last_update_timestamp_desc_idx", Asccp.ASCCP, new OrderField[]{Asccp.ASCCP.LAST_UPDATE_TIMESTAMP}, false);
        public static Index BCC_BCC_GUID_IDX = Internal.createIndex("bcc_guid_idx", Bcc.BCC, new OrderField[]{Bcc.BCC.GUID}, false);
        public static Index BCC_BCC_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("bcc_last_update_timestamp_desc_idx", Bcc.BCC, new OrderField[]{Bcc.BCC.LAST_UPDATE_TIMESTAMP}, false);
        public static Index BCCP_BCCP_GUID_IDX = Internal.createIndex("bccp_guid_idx", Bccp.BCCP, new OrderField[]{Bccp.BCCP.GUID}, false);
        public static Index BCCP_BCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("bccp_last_update_timestamp_desc_idx", Bccp.BCCP, new OrderField[]{Bccp.BCCP.LAST_UPDATE_TIMESTAMP}, false);
        public static Index BIZ_CTX_ASSIGNMENT_BIZ_CTX_ID = Internal.createIndex("biz_ctx_id", BizCtxAssignment.BIZ_CTX_ASSIGNMENT, new OrderField[]{BizCtxAssignment.BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID}, false);
        public static Index BIZ_CTX_ASSIGNMENT_TOP_LEVEL_ABIE_ID = Internal.createIndex("top_level_abie_id", BizCtxAssignment.BIZ_CTX_ASSIGNMENT, new OrderField[]{BizCtxAssignment.BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID}, false);
        public static Index COMMENT_REFERENCE = Internal.createIndex("reference", Comment.COMMENT, new OrderField[]{Comment.COMMENT.REFERENCE}, false);
        public static Index DT_DT_GUID_IDX = Internal.createIndex("dt_guid_idx", Dt.DT, new OrderField[]{Dt.DT.GUID}, false);
        public static Index DT_DT_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("dt_last_update_timestamp_desc_idx", Dt.DT, new OrderField[]{Dt.DT.LAST_UPDATE_TIMESTAMP}, false);
        public static Index DT_SC_DT_SC_GUID_IDX = Internal.createIndex("dt_sc_guid_idx", DtSc.DT_SC, new OrderField[]{DtSc.DT_SC.GUID}, false);
        public static Index REVISION_REFERENCE = Internal.createIndex("reference", Revision.REVISION, new OrderField[]{Revision.REVISION.REFERENCE}, false);
        public static Index XBT_XBT_GUID_IDX = Internal.createIndex("xbt_guid_idx", Xbt.XBT, new OrderField[]{Xbt.XBT.GUID}, false);
        public static Index XBT_XBT_LAST_UPDATE_TIMESTAMP_DESC_IDX = Internal.createIndex("xbt_last_update_timestamp_desc_idx", Xbt.XBT, new OrderField[]{Xbt.XBT.LAST_UPDATE_TIMESTAMP}, false);
    }
}
