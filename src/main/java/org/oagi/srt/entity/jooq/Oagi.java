/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.oagi.srt.entity.jooq.tables.Abie;
import org.oagi.srt.entity.jooq.tables.Acc;
import org.oagi.srt.entity.jooq.tables.AccManifest;
import org.oagi.srt.entity.jooq.tables.AgencyIdList;
import org.oagi.srt.entity.jooq.tables.AgencyIdListValue;
import org.oagi.srt.entity.jooq.tables.AppGroup;
import org.oagi.srt.entity.jooq.tables.AppGroupUser;
import org.oagi.srt.entity.jooq.tables.AppPermission;
import org.oagi.srt.entity.jooq.tables.AppPermissionGroup;
import org.oagi.srt.entity.jooq.tables.AppUser;
import org.oagi.srt.entity.jooq.tables.Asbie;
import org.oagi.srt.entity.jooq.tables.Asbiep;
import org.oagi.srt.entity.jooq.tables.Ascc;
import org.oagi.srt.entity.jooq.tables.AsccManifest;
import org.oagi.srt.entity.jooq.tables.Asccp;
import org.oagi.srt.entity.jooq.tables.AsccpManifest;
import org.oagi.srt.entity.jooq.tables.Bbie;
import org.oagi.srt.entity.jooq.tables.BbieSc;
import org.oagi.srt.entity.jooq.tables.Bbiep;
import org.oagi.srt.entity.jooq.tables.Bcc;
import org.oagi.srt.entity.jooq.tables.BccManifest;
import org.oagi.srt.entity.jooq.tables.Bccp;
import org.oagi.srt.entity.jooq.tables.BccpManifest;
import org.oagi.srt.entity.jooq.tables.BdtPriRestri;
import org.oagi.srt.entity.jooq.tables.BdtScPriRestri;
import org.oagi.srt.entity.jooq.tables.BieUsageRule;
import org.oagi.srt.entity.jooq.tables.BieUserExtRevision;
import org.oagi.srt.entity.jooq.tables.BizCtx;
import org.oagi.srt.entity.jooq.tables.BizCtxAssignment;
import org.oagi.srt.entity.jooq.tables.BizCtxValue;
import org.oagi.srt.entity.jooq.tables.BlobContent;
import org.oagi.srt.entity.jooq.tables.CdtAwdPri;
import org.oagi.srt.entity.jooq.tables.CdtAwdPriXpsTypeMap;
import org.oagi.srt.entity.jooq.tables.CdtPri;
import org.oagi.srt.entity.jooq.tables.CdtScAwdPri;
import org.oagi.srt.entity.jooq.tables.CdtScAwdPriXpsTypeMap;
import org.oagi.srt.entity.jooq.tables.Client;
import org.oagi.srt.entity.jooq.tables.CodeList;
import org.oagi.srt.entity.jooq.tables.CodeListManifest;
import org.oagi.srt.entity.jooq.tables.CodeListValue;
import org.oagi.srt.entity.jooq.tables.CodeListValueManifest;
import org.oagi.srt.entity.jooq.tables.Comment;
import org.oagi.srt.entity.jooq.tables.CtxCategory;
import org.oagi.srt.entity.jooq.tables.CtxScheme;
import org.oagi.srt.entity.jooq.tables.CtxSchemeValue;
import org.oagi.srt.entity.jooq.tables.Dt;
import org.oagi.srt.entity.jooq.tables.DtManifest;
import org.oagi.srt.entity.jooq.tables.DtSc;
import org.oagi.srt.entity.jooq.tables.DtScManifest;
import org.oagi.srt.entity.jooq.tables.DtUsageRule;
import org.oagi.srt.entity.jooq.tables.Module;
import org.oagi.srt.entity.jooq.tables.ModuleDep;
import org.oagi.srt.entity.jooq.tables.Namespace;
import org.oagi.srt.entity.jooq.tables.Release;
import org.oagi.srt.entity.jooq.tables.Revision;
import org.oagi.srt.entity.jooq.tables.TopLevelAbie;
import org.oagi.srt.entity.jooq.tables.UsageRule;
import org.oagi.srt.entity.jooq.tables.UsageRuleExpression;
import org.oagi.srt.entity.jooq.tables.Xbt;
import org.oagi.srt.entity.jooq.tables.XbtManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Oagi extends SchemaImpl {

    private static final long serialVersionUID = -391601871;

    /**
     * The reference instance of <code>oagi</code>
     */
    public static final Oagi OAGI = new Oagi();

    /**
     * The ABIE table stores information about an ABIE, which is a contextualized ACC. The context is represented by the BUSINESS_CTX_ID column that refers to a business context. Each ABIE must have a business context and a based ACC.

It should be noted that, per design document, there is no corresponding ABIE created for an ACC which will not show up in the instance document such as ACCs of OAGIS_COMPONENT_TYPE "SEMANTIC_GROUP", "USER_EXTENSION_GROUP", etc.
     */
    public final Abie ABIE = Abie.ABIE;

    /**
     * The ACC table holds information about complex data structured concepts. For example, OAGIS's Components, Nouns, and BODs are captured in the ACC table.

Note that only Extension is supported when deriving ACC from another ACC. (So if there is a restriction needed, maybe that concept should placed higher in the derivation hierarchy rather than lower.)

In OAGIS, all XSD extensions will be treated as a qualification of an ACC.
     */
    public final Acc ACC = Acc.ACC;

    /**
     * The table <code>oagi.acc_manifest</code>.
     */
    public final AccManifest ACC_MANIFEST = AccManifest.ACC_MANIFEST;

    /**
     * The AGENCY_ID_LIST table stores information about agency identification lists. The list's values are however kept in the AGENCY_ID_LIST_VALUE.
     */
    public final AgencyIdList AGENCY_ID_LIST = AgencyIdList.AGENCY_ID_LIST;

    /**
     * This table captures the values within an agency identification list.
     */
    public final AgencyIdListValue AGENCY_ID_LIST_VALUE = AgencyIdListValue.AGENCY_ID_LIST_VALUE;

    /**
     * The table <code>oagi.app_group</code>.
     */
    public final AppGroup APP_GROUP = AppGroup.APP_GROUP;

    /**
     * The table <code>oagi.app_group_user</code>.
     */
    public final AppGroupUser APP_GROUP_USER = AppGroupUser.APP_GROUP_USER;

    /**
     * The table <code>oagi.app_permission</code>.
     */
    public final AppPermission APP_PERMISSION = AppPermission.APP_PERMISSION;

    /**
     * The table <code>oagi.app_permission_group</code>.
     */
    public final AppPermissionGroup APP_PERMISSION_GROUP = AppPermissionGroup.APP_PERMISSION_GROUP;

    /**
     * This table captures the user information for authentication and authorization purposes.
     */
    public final AppUser APP_USER = AppUser.APP_USER;

    /**
     * An ASBIE represents a relationship/association between two ABIEs through an ASBIEP. It is a contextualization of an ASCC.
     */
    public final Asbie ASBIE = Asbie.ASBIE;

    /**
     * ASBIEP represents a role in a usage of an ABIE. It is a contextualization of an ASCCP.
     */
    public final Asbiep ASBIEP = Asbiep.ASBIEP;

    /**
     * An ASCC represents a relationship/association between two ACCs through an ASCCP. 
     */
    public final Ascc ASCC = Ascc.ASCC;

    /**
     * The table <code>oagi.ascc_manifest</code>.
     */
    public final AsccManifest ASCC_MANIFEST = AsccManifest.ASCC_MANIFEST;

    /**
     * An ASCCP specifies a role (or property) an ACC may play under another ACC.
     */
    public final Asccp ASCCP = Asccp.ASCCP;

    /**
     * The table <code>oagi.asccp_manifest</code>.
     */
    public final AsccpManifest ASCCP_MANIFEST = AsccpManifest.ASCCP_MANIFEST;

    /**
     * A BBIE represents a relationship/association between an ABIE and a BBIEP. It is a contextualization of a BCC. The BBIE table also stores some information about the specific constraints related to the BDT associated with the BBIEP. In particular, the three columns including the BDT_PRI_RESTRI_ID, CODE_LIST_ID, and AGENCY_ID_LIST_ID allows for capturing of the specific primitive to be used in the context. Only one column among the three can have a value in a particular record.
     */
    public final Bbie BBIE = Bbie.BBIE;

    /**
     * Because there is no single table that is a contextualized counterpart of the DT table (which stores both CDT and BDT), The context specific constraints associated with the DT are stored in the BBIE table, while this table stores the constraints associated with the DT's SCs. 
     */
    public final BbieSc BBIE_SC = BbieSc.BBIE_SC;

    /**
     * BBIEP represents the usage of basic property in a specific business context. It is a contextualization of a BCCP.
     */
    public final Bbiep BBIEP = Bbiep.BBIEP;

    /**
     * A BCC represents a relationship/association between an ACC and a BCCP. It creates a data element for an ACC. 
     */
    public final Bcc BCC = Bcc.BCC;

    /**
     * The table <code>oagi.bcc_manifest</code>.
     */
    public final BccManifest BCC_MANIFEST = BccManifest.BCC_MANIFEST;

    /**
     * An BCCP specifies a property concept and data type associated with it. A BCCP can be then added as a property of an ACC.
     */
    public final Bccp BCCP = Bccp.BCCP;

    /**
     * The table <code>oagi.bccp_manifest</code>.
     */
    public final BccpManifest BCCP_MANIFEST = BccpManifest.BCCP_MANIFEST;

    /**
     * This table captures the allowed primitives for a BDT. The allowed primitives are captured by three columns the CDT_AWD_PRI_XPS_TYPE_MAP_ID, CODE_LIST_ID, and AGENCY_ID_LIST_ID. The first column specifies the primitive by the built-in type of an expression language such as the XML Schema built-in type. The second specifies the primitive, which is a code list, while the last one specifies the primitive which is an agency identification list. Only one column among the three can have a value in a particular record.
     */
    public final BdtPriRestri BDT_PRI_RESTRI = BdtPriRestri.BDT_PRI_RESTRI;

    /**
     * This table is similar to the BDT_PRI_RESTRI table but it is for the BDT SC. The allowed primitives are captured by three columns the CDT_SC_AWD_PRI_XPS_TYPE_MAP, CODE_LIST_ID, and AGENCY_ID_LIST_ID. The first column specifies the primitive by the built-in type of an expression language such as the XML Schema built-in type. The second specifies the primitive, which is a code list, while the last one specifies the primitive which is an agency identification list. Only one column among the three can have a value in a particular record.

It should be noted that the table does not store the fact about primitive restriction hierarchical relationships. In other words, if a BDT SC is derived from another BDT SC and the derivative BDT SC applies some primitive restrictions, that relationship will not be explicitly stored. The derivative BDT SC points directly to the CDT_AWD_PRI_XPS_TYPE_MAP key rather than the BDT_SC_PRI_RESTRI key.
     */
    public final BdtScPriRestri BDT_SC_PRI_RESTRI = BdtScPriRestri.BDT_SC_PRI_RESTRI;

    /**
     * This is an intersection table. Per CCTS, a usage rule may be reused. This table allows m-m relationships between the usage rule and all kinds of BIEs. In a particular record, either only one of the TARGET_ABIE_ID, TARGET_ASBIE_ID, TARGET_ASBIEP_ID, TARGET_BBIE_ID, or TARGET_BBIEP_ID.
     */
    public final BieUsageRule BIE_USAGE_RULE = BieUsageRule.BIE_USAGE_RULE;

    /**
     * This table is a log of events. It keeps track of the User Extension ACC (the specific revision) used by an Extension ABIE. This can be a named extension (such as ApplicationAreaExtension) or the AllExtension. The REVISED_INDICATOR flag is designed such that a revision of a User Extension can notify the user of a top-level ABIE by setting this flag to true. The TOP_LEVEL_ABIE_ID column makes it more efficient to when opening a top-level ABIE, the user can be notified of any new revision of the extension. A record in this table is created only when there is a user extension to the the OAGIS extension component/ACC.
     */
    public final BieUserExtRevision BIE_USER_EXT_REVISION = BieUserExtRevision.BIE_USER_EXT_REVISION;

    /**
     * This table represents a business context. A business context is a combination of one or more business context values.
     */
    public final BizCtx BIZ_CTX = BizCtx.BIZ_CTX;

    /**
     * The table <code>oagi.biz_ctx_assignment</code>.
     */
    public final BizCtxAssignment BIZ_CTX_ASSIGNMENT = BizCtxAssignment.BIZ_CTX_ASSIGNMENT;

    /**
     * This table represents business context values for business contexts. It provides the associations between a business context and a context scheme value.
     */
    public final BizCtxValue BIZ_CTX_VALUE = BizCtxValue.BIZ_CTX_VALUE;

    /**
     * This table stores schemas whose content is only imported as a whole and is represented in Blob.
     */
    public final BlobContent BLOB_CONTENT = BlobContent.BLOB_CONTENT;

    /**
     * This table capture allowed primitives of the CDT?s Content Component.  The information in this table is captured from the Allowed Primitive column in each of the CDT Content Component section/table in CCTS DTC3.
     */
    public final CdtAwdPri CDT_AWD_PRI = CdtAwdPri.CDT_AWD_PRI;

    /**
     * This table allows for concrete mapping between the CDT Primitives and types in a particular expression such as XML Schema, JSON. At this point, it is not clear whether a separate table will be needed for each expression. The current table holds the map to XML Schema built-in types. 

For each additional expression, a column similar to the XBT_ID column will need to be added to this table for mapping to data types in another expression.

If we use a separate table for each expression, then we need binding all the way to BDT (or even BBIE) for every new expression. That would be almost like just store a BDT file. But using a column may not work with all kinds of expressions, particulary if it does not map well to the XML schema data types. 
     */
    public final CdtAwdPriXpsTypeMap CDT_AWD_PRI_XPS_TYPE_MAP = CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP;

    /**
     * This table stores the CDT primitives.
     */
    public final CdtPri CDT_PRI = CdtPri.CDT_PRI;

    /**
     * This table capture the CDT primitives allowed for a particular SC of a CDT. It also stores the CDT primitives allowed for a SC of a BDT that extends its base (such SC is not defined in the CCTS data type catalog specification).
     */
    public final CdtScAwdPri CDT_SC_AWD_PRI = CdtScAwdPri.CDT_SC_AWD_PRI;

    /**
     * The purpose of this table is the same as that of the CDT_AWD_PRI_XPS_TYPE_MAP, but it is for the supplementary component (SC). It allows for the concrete mapping between the CDT Primitives and types in a particular expression such as XML Schema, JSON. 
     */
    public final CdtScAwdPriXpsTypeMap CDT_SC_AWD_PRI_XPS_TYPE_MAP = CdtScAwdPriXpsTypeMap.CDT_SC_AWD_PRI_XPS_TYPE_MAP;

    /**
     * This table captures a client organization. It is used, for example, to indicate the customer, for which the BIE was generated.
     */
    public final Client CLIENT = Client.CLIENT;

    /**
     * This table stores information about a code list. When a code list is derived from another code list, the whole set of code values belonging to the based code list will be copied.
     */
    public final CodeList CODE_LIST = CodeList.CODE_LIST;

    /**
     * The table <code>oagi.code_list_manifest</code>.
     */
    public final CodeListManifest CODE_LIST_MANIFEST = CodeListManifest.CODE_LIST_MANIFEST;

    /**
     * Each record in this table stores a code list value of a code list. A code list value may be inherited from another code list on which it is based. However, inherited value may be restricted (i.e., disabled and cannot be used) in this code list, i.e., the USED_INDICATOR = false. If the value cannot be used since the based code list, then the LOCKED_INDICATOR = TRUE, because the USED_INDICATOR of such code list value is FALSE by default and can no longer be changed.
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
     * This table captures the context category. Examples of context categories as described in the CCTS are business process, industry, etc.
     */
    public final CtxCategory CTX_CATEGORY = CtxCategory.CTX_CATEGORY;

    /**
     * This table represents a context scheme (a classification scheme) for a context category.
     */
    public final CtxScheme CTX_SCHEME = CtxScheme.CTX_SCHEME;

    /**
     * This table stores the context scheme values for a particular context scheme in the CTX_SCHEME table.
     */
    public final CtxSchemeValue CTX_SCHEME_VALUE = CtxSchemeValue.CTX_SCHEME_VALUE;

    /**
     * The DT table stores both CDT and BDT. The two types of DTs are differentiated by the TYPE column.
     */
    public final Dt DT = Dt.DT;

    /**
     * The table <code>oagi.dt_manifest</code>.
     */
    public final DtManifest DT_MANIFEST = DtManifest.DT_MANIFEST;

    /**
     * This table represents the supplementary component (SC) of a DT. Revision is not tracked at the supplementary component. It is considered intrinsic part of the DT. In other words, when a new revision of a DT is created a new set of supplementary components is created along with it. 
     */
    public final DtSc DT_SC = DtSc.DT_SC;

    /**
     * The table <code>oagi.dt_sc_manifest</code>.
     */
    public final DtScManifest DT_SC_MANIFEST = DtScManifest.DT_SC_MANIFEST;

    /**
     * This is an intersection table. Per CCTS, a usage rule may be reused. This table allows m-m relationships between the usage rule and the DT content component and usage rules and DT supplementary component. In a particular record, either a TARGET_DT_ID or TARGET_DT_SC_ID must be present but not both.
     */
    public final DtUsageRule DT_USAGE_RULE = DtUsageRule.DT_USAGE_RULE;

    /**
     * The module table stores information about a physical file, into which CC components will be generated during the expression generation.
     */
    public final Module MODULE = Module.MODULE;

    /**
     * This table carries the dependency between modules in the MODULE table.
     */
    public final ModuleDep MODULE_DEP = ModuleDep.MODULE_DEP;

    /**
     * This table stores information about a namespace. Namespace is the namespace as in the XML schema specification.
     */
    public final Namespace NAMESPACE = Namespace.NAMESPACE;

    /**
     * The is table store the release information.
     */
    public final Release RELEASE = Release.RELEASE;

    /**
     * The table <code>oagi.revision</code>.
     */
    public final Revision REVISION = Revision.REVISION;

    /**
     * This table indexes the ABIE which is a top-level ABIE. This table and the owner_top_level_abie_id column in all BIE tables allow all related BIEs to be retrieved all at once speeding up the profile BOD transactions.
     */
    public final TopLevelAbie TOP_LEVEL_ABIE = TopLevelAbie.TOP_LEVEL_ABIE;

    /**
     * This table captures a usage rule information. A usage rule may be expressed in multiple expressions. Each expression is captured in the USAGE_RULE_EXPRESSION table. To capture a description of a usage rule, create a usage rule expression with the unstructured constraint type.
     */
    public final UsageRule USAGE_RULE = UsageRule.USAGE_RULE;

    /**
     * The USAGE_RULE_EXPRESSION provides a representation of a usage rule in a particular syntax indicated by the CONSTRAINT_TYPE column. One of the syntaxes can be unstructured, which works a description of the usage rule.
     */
    public final UsageRuleExpression USAGE_RULE_EXPRESSION = UsageRuleExpression.USAGE_RULE_EXPRESSION;

    /**
     * This table stores XML schema built-in types and OAGIS built-in types. OAGIS built-in types are those types defined in the XMLSchemaBuiltinType and the XMLSchemaBuiltinType Patterns schemas.
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
        return Arrays.<Table<?>>asList(
            Abie.ABIE,
            Acc.ACC,
            AccManifest.ACC_MANIFEST,
            AgencyIdList.AGENCY_ID_LIST,
            AgencyIdListValue.AGENCY_ID_LIST_VALUE,
            AppGroup.APP_GROUP,
            AppGroupUser.APP_GROUP_USER,
            AppPermission.APP_PERMISSION,
            AppPermissionGroup.APP_PERMISSION_GROUP,
            AppUser.APP_USER,
            Asbie.ASBIE,
            Asbiep.ASBIEP,
            Ascc.ASCC,
            AsccManifest.ASCC_MANIFEST,
            Asccp.ASCCP,
            AsccpManifest.ASCCP_MANIFEST,
            Bbie.BBIE,
            BbieSc.BBIE_SC,
            Bbiep.BBIEP,
            Bcc.BCC,
            BccManifest.BCC_MANIFEST,
            Bccp.BCCP,
            BccpManifest.BCCP_MANIFEST,
            BdtPriRestri.BDT_PRI_RESTRI,
            BdtScPriRestri.BDT_SC_PRI_RESTRI,
            BieUsageRule.BIE_USAGE_RULE,
            BieUserExtRevision.BIE_USER_EXT_REVISION,
            BizCtx.BIZ_CTX,
            BizCtxAssignment.BIZ_CTX_ASSIGNMENT,
            BizCtxValue.BIZ_CTX_VALUE,
            BlobContent.BLOB_CONTENT,
            CdtAwdPri.CDT_AWD_PRI,
            CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP,
            CdtPri.CDT_PRI,
            CdtScAwdPri.CDT_SC_AWD_PRI,
            CdtScAwdPriXpsTypeMap.CDT_SC_AWD_PRI_XPS_TYPE_MAP,
            Client.CLIENT,
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
            DtSc.DT_SC,
            DtScManifest.DT_SC_MANIFEST,
            DtUsageRule.DT_USAGE_RULE,
            Module.MODULE,
            ModuleDep.MODULE_DEP,
            Namespace.NAMESPACE,
            Release.RELEASE,
            Revision.REVISION,
            TopLevelAbie.TOP_LEVEL_ABIE,
            UsageRule.USAGE_RULE,
            UsageRuleExpression.USAGE_RULE_EXPRESSION,
            Xbt.XBT,
            XbtManifest.XBT_MANIFEST);
    }
}
