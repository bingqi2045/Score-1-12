/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Abie;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Acc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AccManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AccManifestTag;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdList;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdListManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdListValue;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdListValueManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AppOauth2User;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AppUser;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Asbie;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsbieBizterm;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Asbiep;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Ascc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsccBizterm;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsccManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Asccp;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsccpManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsccpManifestTag;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bbie;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BbieBizterm;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BbieSc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bbiep;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bcc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccBizterm;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bccp;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccpManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccpManifestTag;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BdtPriRestri;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BdtScPriRestri;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BieUsageRule;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BieUserExtRevision;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BizCtx;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BizCtxAssignment;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BizCtxValue;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BlobContent;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BlobContentManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BusinessTerm;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CcTag;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtAwdPri;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtAwdPriXpsTypeMap;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtPri;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtRefSpec;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtScAwdPri;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtScAwdPriXpsTypeMap;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtScRefSpec;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeList;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeListManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeListValue;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeListValueManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Comment;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CtxCategory;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CtxScheme;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CtxSchemeValue;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Dt;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtManifestTag;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtSc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtScManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtUsageRule;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Exception;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Log;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Message;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Module;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleAccManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleAgencyIdListManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleAsccpManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleBccpManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleBlobContentManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleCodeListManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleDtManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleSet;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleSetRelease;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleXbtManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Namespace;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Oauth2App;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Oauth2AppScope;
import org.oagi.score.repo.api.impl.jooq.entity.tables.RefSpec;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Release;
import org.oagi.score.repo.api.impl.jooq.entity.tables.SeqKey;
import org.oagi.score.repo.api.impl.jooq.entity.tables.TopLevelAsbiep;
import org.oagi.score.repo.api.impl.jooq.entity.tables.UsageRule;
import org.oagi.score.repo.api.impl.jooq.entity.tables.UsageRuleExpression;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Xbt;
import org.oagi.score.repo.api.impl.jooq.entity.tables.XbtManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Oagi extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi</code>
     */
    public static final Oagi OAGI = new Oagi();

    /**
     * The ABIE table stores information about an ABIE, which is a
     * contextualized ACC. The context is represented by the BUSINESS_CTX_ID
     * column that refers to a business context. Each ABIE must have a business
     * context and a based ACC.
     * 
     * It should be noted that, per design document, there is no corresponding
     * ABIE created for an ACC which will not show up in the instance document
     * such as ACCs of OAGIS_COMPONENT_TYPE "SEMANTIC_GROUP",
     * "USER_EXTENSION_GROUP", etc.
     */
    public final Abie ABIE = Abie.ABIE;

    /**
     * The ACC table holds information about complex data structured concepts.
     * For example, OAGIS's Components, Nouns, and BODs are captured in the ACC
     * table.
     * 
     * Note that only Extension is supported when deriving ACC from another ACC.
     * (So if there is a restriction needed, maybe that concept should placed
     * higher in the derivation hierarchy rather than lower.)
     * 
     * In OAGIS, all XSD extensions will be treated as a qualification of an
     * ACC.
     */
    public final Acc ACC = Acc.ACC;

    /**
     * The table <code>oagi.acc_manifest</code>.
     */
    public final AccManifest ACC_MANIFEST = AccManifest.ACC_MANIFEST;

    /**
     * The table <code>oagi.acc_manifest_tag</code>.
     */
    public final AccManifestTag ACC_MANIFEST_TAG = AccManifestTag.ACC_MANIFEST_TAG;

    /**
     * The AGENCY_ID_LIST table stores information about agency identification
     * lists. The list's values are however kept in the AGENCY_ID_LIST_VALUE.
     */
    public final AgencyIdList AGENCY_ID_LIST = AgencyIdList.AGENCY_ID_LIST;

    /**
     * The table <code>oagi.agency_id_list_manifest</code>.
     */
    public final AgencyIdListManifest AGENCY_ID_LIST_MANIFEST = AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST;

    /**
     * This table captures the values within an agency identification list.
     */
    public final AgencyIdListValue AGENCY_ID_LIST_VALUE = AgencyIdListValue.AGENCY_ID_LIST_VALUE;

    /**
     * The table <code>oagi.agency_id_list_value_manifest</code>.
     */
    public final AgencyIdListValueManifest AGENCY_ID_LIST_VALUE_MANIFEST = AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST;

    /**
     * The table <code>oagi.app_oauth2_user</code>.
     */
    public final AppOauth2User APP_OAUTH2_USER = AppOauth2User.APP_OAUTH2_USER;

    /**
     * This table captures the user information for authentication and
     * authorization purposes.
     */
    public final AppUser APP_USER = AppUser.APP_USER;

    /**
     * An ASBIE represents a relationship/association between two ABIEs through
     * an ASBIEP. It is a contextualization of an ASCC.
     */
    public final Asbie ASBIE = Asbie.ASBIE;

    /**
     * The asbie_bizterm table stores information about the aggregation between
     * the ascc_bizterm and ASBIE. TODO: Placeholder, definition is missing.
     */
    public final AsbieBizterm ASBIE_BIZTERM = AsbieBizterm.ASBIE_BIZTERM;

    /**
     * ASBIEP represents a role in a usage of an ABIE. It is a contextualization
     * of an ASCCP.
     */
    public final Asbiep ASBIEP = Asbiep.ASBIEP;

    /**
     * An ASCC represents a relationship/association between two ACCs through an
     * ASCCP. 
     */
    public final Ascc ASCC = Ascc.ASCC;

    /**
     * The ascc_bizterm table stores information about the aggregation between
     * the business term and ASCC. TODO: Placeholder, definition is missing.
     */
    public final AsccBizterm ASCC_BIZTERM = AsccBizterm.ASCC_BIZTERM;

    /**
     * The table <code>oagi.ascc_manifest</code>.
     */
    public final AsccManifest ASCC_MANIFEST = AsccManifest.ASCC_MANIFEST;

    /**
     * An ASCCP specifies a role (or property) an ACC may play under another
     * ACC.
     */
    public final Asccp ASCCP = Asccp.ASCCP;

    /**
     * The table <code>oagi.asccp_manifest</code>.
     */
    public final AsccpManifest ASCCP_MANIFEST = AsccpManifest.ASCCP_MANIFEST;

    /**
     * The table <code>oagi.asccp_manifest_tag</code>.
     */
    public final AsccpManifestTag ASCCP_MANIFEST_TAG = AsccpManifestTag.ASCCP_MANIFEST_TAG;

    /**
     * A BBIE represents a relationship/association between an ABIE and a BBIEP.
     * It is a contextualization of a BCC. The BBIE table also stores some
     * information about the specific constraints related to the BDT associated
     * with the BBIEP. In particular, the three columns including the
     * BDT_PRI_RESTRI_ID, CODE_LIST_ID, and AGENCY_ID_LIST_ID allows for
     * capturing of the specific primitive to be used in the context. Only one
     * column among the three can have a value in a particular record.
     */
    public final Bbie BBIE = Bbie.BBIE;

    /**
     * The bbie_bizterm table stores information about the aggregation between
     * the bbie_bizterm and BBIE. TODO: Placeholder, definition is missing.
     */
    public final BbieBizterm BBIE_BIZTERM = BbieBizterm.BBIE_BIZTERM;

    /**
     * Because there is no single table that is a contextualized counterpart of
     * the DT table (which stores both CDT and BDT), The context specific
     * constraints associated with the DT are stored in the BBIE table, while
     * this table stores the constraints associated with the DT's SCs. 
     */
    public final BbieSc BBIE_SC = BbieSc.BBIE_SC;

    /**
     * BBIEP represents the usage of basic property in a specific business
     * context. It is a contextualization of a BCCP.
     */
    public final Bbiep BBIEP = Bbiep.BBIEP;

    /**
     * A BCC represents a relationship/association between an ACC and a BCCP. It
     * creates a data element for an ACC. 
     */
    public final Bcc BCC = Bcc.BCC;

    /**
     * The bcc_bizterm table stores information about the aggregation between
     * the business term and BCC. TODO: Placeholder, definition is missing.
     */
    public final BccBizterm BCC_BIZTERM = BccBizterm.BCC_BIZTERM;

    /**
     * The table <code>oagi.bcc_manifest</code>.
     */
    public final BccManifest BCC_MANIFEST = BccManifest.BCC_MANIFEST;

    /**
     * An BCCP specifies a property concept and data type associated with it. A
     * BCCP can be then added as a property of an ACC.
     */
    public final Bccp BCCP = Bccp.BCCP;

    /**
     * The table <code>oagi.bccp_manifest</code>.
     */
    public final BccpManifest BCCP_MANIFEST = BccpManifest.BCCP_MANIFEST;

    /**
     * The table <code>oagi.bccp_manifest_tag</code>.
     */
    public final BccpManifestTag BCCP_MANIFEST_TAG = BccpManifestTag.BCCP_MANIFEST_TAG;

    /**
     * This table captures the allowed primitives for a BDT. The allowed
     * primitives are captured by three columns the CDT_AWD_PRI_XPS_TYPE_MAP_ID,
     * CODE_LIST_ID, and AGENCY_ID_LIST_ID. The first column specifies the
     * primitive by the built-in type of an expression language such as the XML
     * Schema built-in type. The second specifies the primitive, which is a code
     * list, while the last one specifies the primitive which is an agency
     * identification list. Only one column among the three can have a value in
     * a particular record.
     */
    public final BdtPriRestri BDT_PRI_RESTRI = BdtPriRestri.BDT_PRI_RESTRI;

    /**
     * This table is similar to the BDT_PRI_RESTRI table but it is for the BDT
     * SC. The allowed primitives are captured by three columns the
     * CDT_SC_AWD_PRI_XPS_TYPE_MAP, CODE_LIST_ID, and AGENCY_ID_LIST_ID. The
     * first column specifies the primitive by the built-in type of an
     * expression language such as the XML Schema built-in type. The second
     * specifies the primitive, which is a code list, while the last one
     * specifies the primitive which is an agency identification list. Only one
     * column among the three can have a value in a particular record.
     * 
     * It should be noted that the table does not store the fact about primitive
     * restriction hierarchical relationships. In other words, if a BDT SC is
     * derived from another BDT SC and the derivative BDT SC applies some
     * primitive restrictions, that relationship will not be explicitly stored.
     * The derivative BDT SC points directly to the CDT_AWD_PRI_XPS_TYPE_MAP key
     * rather than the BDT_SC_PRI_RESTRI key.
     */
    public final BdtScPriRestri BDT_SC_PRI_RESTRI = BdtScPriRestri.BDT_SC_PRI_RESTRI;

    /**
     * This is an intersection table. Per CCTS, a usage rule may be reused. This
     * table allows m-m relationships between the usage rule and all kinds of
     * BIEs. In a particular record, either only one of the TARGET_ABIE_ID,
     * TARGET_ASBIE_ID, TARGET_ASBIEP_ID, TARGET_BBIE_ID, or TARGET_BBIEP_ID.
     */
    public final BieUsageRule BIE_USAGE_RULE = BieUsageRule.BIE_USAGE_RULE;

    /**
     * This table is a log of events. It keeps track of the User Extension ACC
     * (the specific revision) used by an Extension ABIE. This can be a named
     * extension (such as ApplicationAreaExtension) or the AllExtension. The
     * REVISED_INDICATOR flag is designed such that a revision of a User
     * Extension can notify the user of a top-level ABIE by setting this flag to
     * true. The TOP_LEVEL_ABIE_ID column makes it more efficient to when
     * opening a top-level ABIE, the user can be notified of any new revision of
     * the extension. A record in this table is created only when there is a
     * user extension to the the OAGIS extension component/ACC.
     */
    public final BieUserExtRevision BIE_USER_EXT_REVISION = BieUserExtRevision.BIE_USER_EXT_REVISION;

    /**
     * This table represents a business context. A business context is a
     * combination of one or more business context values.
     */
    public final BizCtx BIZ_CTX = BizCtx.BIZ_CTX;

    /**
     * The table <code>oagi.biz_ctx_assignment</code>.
     */
    public final BizCtxAssignment BIZ_CTX_ASSIGNMENT = BizCtxAssignment.BIZ_CTX_ASSIGNMENT;

    /**
     * This table represents business context values for business contexts. It
     * provides the associations between a business context and a context scheme
     * value.
     */
    public final BizCtxValue BIZ_CTX_VALUE = BizCtxValue.BIZ_CTX_VALUE;

    /**
     * This table stores schemas whose content is only imported as a whole and
     * is represented in Blob.
     */
    public final BlobContent BLOB_CONTENT = BlobContent.BLOB_CONTENT;

    /**
     * The table <code>oagi.blob_content_manifest</code>.
     */
    public final BlobContentManifest BLOB_CONTENT_MANIFEST = BlobContentManifest.BLOB_CONTENT_MANIFEST;

    /**
     * The Business Term table stores information about the business term, which
     * is usually associated to BIE or CC.
     */
    public final BusinessTerm BUSINESS_TERM = BusinessTerm.BUSINESS_TERM;

    /**
     * The table <code>oagi.cc_tag</code>.
     */
    public final CcTag CC_TAG = CcTag.CC_TAG;

    /**
     * This table capture allowed primitives of the CDT?s Content Component. 
     * The information in this table is captured from the Allowed Primitive
     * column in each of the CDT Content Component section/table in CCTS DTC3.
     */
    public final CdtAwdPri CDT_AWD_PRI = CdtAwdPri.CDT_AWD_PRI;

    /**
     * This table allows for concrete mapping between the CDT Primitives and
     * types in a particular expression such as XML Schema, JSON. At this point,
     * it is not clear whether a separate table will be needed for each
     * expression. The current table holds the map to XML Schema built-in types.
     * 
     * 
     * For each additional expression, a column similar to the XBT_ID column
     * will need to be added to this table for mapping to data types in another
     * expression.
     * 
     * If we use a separate table for each expression, then we need binding all
     * the way to BDT (or even BBIE) for every new expression. That would be
     * almost like just store a BDT file. But using a column may not work with
     * all kinds of expressions, particulary if it does not map well to the XML
     * schema data types. 
     */
    public final CdtAwdPriXpsTypeMap CDT_AWD_PRI_XPS_TYPE_MAP = CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP;

    /**
     * This table stores the CDT primitives.
     */
    public final CdtPri CDT_PRI = CdtPri.CDT_PRI;

    /**
     * The table <code>oagi.cdt_ref_spec</code>.
     */
    public final CdtRefSpec CDT_REF_SPEC = CdtRefSpec.CDT_REF_SPEC;

    /**
     * This table capture the CDT primitives allowed for a particular SC of a
     * CDT. It also stores the CDT primitives allowed for a SC of a BDT that
     * extends its base (such SC is not defined in the CCTS data type catalog
     * specification).
     */
    public final CdtScAwdPri CDT_SC_AWD_PRI = CdtScAwdPri.CDT_SC_AWD_PRI;

    /**
     * The purpose of this table is the same as that of the
     * CDT_AWD_PRI_XPS_TYPE_MAP, but it is for the supplementary component (SC).
     * It allows for the concrete mapping between the CDT Primitives and types
     * in a particular expression such as XML Schema, JSON. 
     */
    public final CdtScAwdPriXpsTypeMap CDT_SC_AWD_PRI_XPS_TYPE_MAP = CdtScAwdPriXpsTypeMap.CDT_SC_AWD_PRI_XPS_TYPE_MAP;

    /**
     * The table <code>oagi.cdt_sc_ref_spec</code>.
     */
    public final CdtScRefSpec CDT_SC_REF_SPEC = CdtScRefSpec.CDT_SC_REF_SPEC;

    /**
     * This table stores information about a code list. When a code list is
     * derived from another code list, the whole set of code values belonging to
     * the based code list will be copied.
     */
    public final CodeList CODE_LIST = CodeList.CODE_LIST;

    /**
     * The table <code>oagi.code_list_manifest</code>.
     */
    public final CodeListManifest CODE_LIST_MANIFEST = CodeListManifest.CODE_LIST_MANIFEST;

    /**
     * Each record in this table stores a code list value of a code list. A code
     * list value may be inherited from another code list on which it is based.
     * However, inherited value may be restricted (i.e., disabled and cannot be
     * used) in this code list, i.e., the USED_INDICATOR = false. If the value
     * cannot be used since the based code list, then the LOCKED_INDICATOR =
     * TRUE, because the USED_INDICATOR of such code list value is FALSE by
     * default and can no longer be changed.
     */
    public final CodeListValue CODE_LIST_VALUE = CodeListValue.CODE_LIST_VALUE;

    /**
     * The table <code>oagi.code_list_value_manifest</code>.
     */
    public final CodeListValueManifest CODE_LIST_VALUE_MANIFEST = CodeListValueManifest.CODE_LIST_VALUE_MANIFEST;

    /**
     * The table <code>oagi.comment</code>.
     */
    public final Comment COMMENT = Comment.COMMENT;

    /**
     * This table captures the context category. Examples of context categories
     * as described in the CCTS are business process, industry, etc.
     */
    public final CtxCategory CTX_CATEGORY = CtxCategory.CTX_CATEGORY;

    /**
     * This table represents a context scheme (a classification scheme) for a
     * context category.
     */
    public final CtxScheme CTX_SCHEME = CtxScheme.CTX_SCHEME;

    /**
     * This table stores the context scheme values for a particular context
     * scheme in the CTX_SCHEME table.
     */
    public final CtxSchemeValue CTX_SCHEME_VALUE = CtxSchemeValue.CTX_SCHEME_VALUE;

    /**
     * The DT table stores both CDT and BDT. The two types of DTs are
     * differentiated by the TYPE column.
     */
    public final Dt DT = Dt.DT;

    /**
     * The table <code>oagi.dt_manifest</code>.
     */
    public final DtManifest DT_MANIFEST = DtManifest.DT_MANIFEST;

    /**
     * The table <code>oagi.dt_manifest_tag</code>.
     */
    public final DtManifestTag DT_MANIFEST_TAG = DtManifestTag.DT_MANIFEST_TAG;

    /**
     * This table represents the supplementary component (SC) of a DT. Revision
     * is not tracked at the supplementary component. It is considered intrinsic
     * part of the DT. In other words, when a new revision of a DT is created a
     * new set of supplementary components is created along with it. 
     */
    public final DtSc DT_SC = DtSc.DT_SC;

    /**
     * The table <code>oagi.dt_sc_manifest</code>.
     */
    public final DtScManifest DT_SC_MANIFEST = DtScManifest.DT_SC_MANIFEST;

    /**
     * This is an intersection table. Per CCTS, a usage rule may be reused. This
     * table allows m-m relationships between the usage rule and the DT content
     * component and usage rules and DT supplementary component. In a particular
     * record, either a TARGET_DT_ID or TARGET_DT_SC_ID must be present but not
     * both.
     */
    public final DtUsageRule DT_USAGE_RULE = DtUsageRule.DT_USAGE_RULE;

    /**
     * The table <code>oagi.exception</code>.
     */
    public final Exception EXCEPTION = Exception.EXCEPTION;

    /**
     * The table <code>oagi.log</code>.
     */
    public final Log LOG = Log.LOG;

    /**
     * The table <code>oagi.message</code>.
     */
    public final Message MESSAGE = Message.MESSAGE;

    /**
     * The module table stores information about a physical file, into which CC
     * components will be generated during the expression generation.
     */
    public final Module MODULE = Module.MODULE;

    /**
     * The table <code>oagi.module_acc_manifest</code>.
     */
    public final ModuleAccManifest MODULE_ACC_MANIFEST = ModuleAccManifest.MODULE_ACC_MANIFEST;

    /**
     * The table <code>oagi.module_agency_id_list_manifest</code>.
     */
    public final ModuleAgencyIdListManifest MODULE_AGENCY_ID_LIST_MANIFEST = ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST;

    /**
     * The table <code>oagi.module_asccp_manifest</code>.
     */
    public final ModuleAsccpManifest MODULE_ASCCP_MANIFEST = ModuleAsccpManifest.MODULE_ASCCP_MANIFEST;

    /**
     * The table <code>oagi.module_bccp_manifest</code>.
     */
    public final ModuleBccpManifest MODULE_BCCP_MANIFEST = ModuleBccpManifest.MODULE_BCCP_MANIFEST;

    /**
     * The table <code>oagi.module_blob_content_manifest</code>.
     */
    public final ModuleBlobContentManifest MODULE_BLOB_CONTENT_MANIFEST = ModuleBlobContentManifest.MODULE_BLOB_CONTENT_MANIFEST;

    /**
     * The table <code>oagi.module_code_list_manifest</code>.
     */
    public final ModuleCodeListManifest MODULE_CODE_LIST_MANIFEST = ModuleCodeListManifest.MODULE_CODE_LIST_MANIFEST;

    /**
     * The table <code>oagi.module_dt_manifest</code>.
     */
    public final ModuleDtManifest MODULE_DT_MANIFEST = ModuleDtManifest.MODULE_DT_MANIFEST;

    /**
     * The table <code>oagi.module_set</code>.
     */
    public final ModuleSet MODULE_SET = ModuleSet.MODULE_SET;

    /**
     * The table <code>oagi.module_set_release</code>.
     */
    public final ModuleSetRelease MODULE_SET_RELEASE = ModuleSetRelease.MODULE_SET_RELEASE;

    /**
     * The table <code>oagi.module_xbt_manifest</code>.
     */
    public final ModuleXbtManifest MODULE_XBT_MANIFEST = ModuleXbtManifest.MODULE_XBT_MANIFEST;

    /**
     * This table stores information about a namespace. Namespace is the
     * namespace as in the XML schema specification.
     */
    public final Namespace NAMESPACE = Namespace.NAMESPACE;

    /**
     * The table <code>oagi.oauth2_app</code>.
     */
    public final Oauth2App OAUTH2_APP = Oauth2App.OAUTH2_APP;

    /**
     * The table <code>oagi.oauth2_app_scope</code>.
     */
    public final Oauth2AppScope OAUTH2_APP_SCOPE = Oauth2AppScope.OAUTH2_APP_SCOPE;

    /**
     * The table <code>oagi.ref_spec</code>.
     */
    public final RefSpec REF_SPEC = RefSpec.REF_SPEC;

    /**
     * The is table store the release information.
     */
    public final Release RELEASE = Release.RELEASE;

    /**
     * The table <code>oagi.seq_key</code>.
     */
    public final SeqKey SEQ_KEY = SeqKey.SEQ_KEY;

    /**
     * This table indexes the ASBIEP which is a top-level ASBIEP. This table and
     * the owner_top_level_asbiep_id column in all BIE tables allow all related
     * BIEs to be retrieved all at once speeding up the profile BOD
     * transactions.
     */
    public final TopLevelAsbiep TOP_LEVEL_ASBIEP = TopLevelAsbiep.TOP_LEVEL_ASBIEP;

    /**
     * This table captures a usage rule information. A usage rule may be
     * expressed in multiple expressions. Each expression is captured in the
     * USAGE_RULE_EXPRESSION table. To capture a description of a usage rule,
     * create a usage rule expression with the unstructured constraint type.
     */
    public final UsageRule USAGE_RULE = UsageRule.USAGE_RULE;

    /**
     * The USAGE_RULE_EXPRESSION provides a representation of a usage rule in a
     * particular syntax indicated by the CONSTRAINT_TYPE column. One of the
     * syntaxes can be unstructured, which works a description of the usage
     * rule.
     */
    public final UsageRuleExpression USAGE_RULE_EXPRESSION = UsageRuleExpression.USAGE_RULE_EXPRESSION;

    /**
     * This table stores XML schema built-in types and OAGIS built-in types.
     * OAGIS built-in types are those types defined in the XMLSchemaBuiltinType
     * and the XMLSchemaBuiltinType Patterns schemas.
     */
    public final Xbt XBT = Xbt.XBT;

    /**
     * The table <code>oagi.xbt_manifest</code>.
     */
    public final XbtManifest XBT_MANIFEST = XbtManifest.XBT_MANIFEST;

    /**
     * No further instances allowed
     */
    private Oagi() {
        super("oagi", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Abie.ABIE,
            Acc.ACC,
            AccManifest.ACC_MANIFEST,
            AccManifestTag.ACC_MANIFEST_TAG,
            AgencyIdList.AGENCY_ID_LIST,
            AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST,
            AgencyIdListValue.AGENCY_ID_LIST_VALUE,
            AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST,
            AppOauth2User.APP_OAUTH2_USER,
            AppUser.APP_USER,
            Asbie.ASBIE,
            AsbieBizterm.ASBIE_BIZTERM,
            Asbiep.ASBIEP,
            Ascc.ASCC,
            AsccBizterm.ASCC_BIZTERM,
            AsccManifest.ASCC_MANIFEST,
            Asccp.ASCCP,
            AsccpManifest.ASCCP_MANIFEST,
            AsccpManifestTag.ASCCP_MANIFEST_TAG,
            Bbie.BBIE,
            BbieBizterm.BBIE_BIZTERM,
            BbieSc.BBIE_SC,
            Bbiep.BBIEP,
            Bcc.BCC,
            BccBizterm.BCC_BIZTERM,
            BccManifest.BCC_MANIFEST,
            Bccp.BCCP,
            BccpManifest.BCCP_MANIFEST,
            BccpManifestTag.BCCP_MANIFEST_TAG,
            BdtPriRestri.BDT_PRI_RESTRI,
            BdtScPriRestri.BDT_SC_PRI_RESTRI,
            BieUsageRule.BIE_USAGE_RULE,
            BieUserExtRevision.BIE_USER_EXT_REVISION,
            BizCtx.BIZ_CTX,
            BizCtxAssignment.BIZ_CTX_ASSIGNMENT,
            BizCtxValue.BIZ_CTX_VALUE,
            BlobContent.BLOB_CONTENT,
            BlobContentManifest.BLOB_CONTENT_MANIFEST,
            BusinessTerm.BUSINESS_TERM,
            CcTag.CC_TAG,
            CdtAwdPri.CDT_AWD_PRI,
            CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP,
            CdtPri.CDT_PRI,
            CdtRefSpec.CDT_REF_SPEC,
            CdtScAwdPri.CDT_SC_AWD_PRI,
            CdtScAwdPriXpsTypeMap.CDT_SC_AWD_PRI_XPS_TYPE_MAP,
            CdtScRefSpec.CDT_SC_REF_SPEC,
            CodeList.CODE_LIST,
            CodeListManifest.CODE_LIST_MANIFEST,
            CodeListValue.CODE_LIST_VALUE,
            CodeListValueManifest.CODE_LIST_VALUE_MANIFEST,
            Comment.COMMENT,
            CtxCategory.CTX_CATEGORY,
            CtxScheme.CTX_SCHEME,
            CtxSchemeValue.CTX_SCHEME_VALUE,
            Dt.DT,
            DtManifest.DT_MANIFEST,
            DtManifestTag.DT_MANIFEST_TAG,
            DtSc.DT_SC,
            DtScManifest.DT_SC_MANIFEST,
            DtUsageRule.DT_USAGE_RULE,
            Exception.EXCEPTION,
            Log.LOG,
            Message.MESSAGE,
            Module.MODULE,
            ModuleAccManifest.MODULE_ACC_MANIFEST,
            ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST,
            ModuleAsccpManifest.MODULE_ASCCP_MANIFEST,
            ModuleBccpManifest.MODULE_BCCP_MANIFEST,
            ModuleBlobContentManifest.MODULE_BLOB_CONTENT_MANIFEST,
            ModuleCodeListManifest.MODULE_CODE_LIST_MANIFEST,
            ModuleDtManifest.MODULE_DT_MANIFEST,
            ModuleSet.MODULE_SET,
            ModuleSetRelease.MODULE_SET_RELEASE,
            ModuleXbtManifest.MODULE_XBT_MANIFEST,
            Namespace.NAMESPACE,
            Oauth2App.OAUTH2_APP,
            Oauth2AppScope.OAUTH2_APP_SCOPE,
            RefSpec.REF_SPEC,
            Release.RELEASE,
            SeqKey.SEQ_KEY,
            TopLevelAsbiep.TOP_LEVEL_ASBIEP,
            UsageRule.USAGE_RULE,
            UsageRuleExpression.USAGE_RULE_EXPRESSION,
            Xbt.XBT,
            XbtManifest.XBT_MANIFEST
        );
    }
}
