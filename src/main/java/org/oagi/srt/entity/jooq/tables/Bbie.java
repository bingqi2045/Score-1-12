/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.BbieRecord;


/**
 * A BBIE represents a relationship/association between an ABIE and a BBIEP. 
 * It is a contextualization of a BCC. The BBIE table also stores some information 
 * about the specific constraints related to the BDT associated with the BBIEP. 
 * In particular, the three columns including the BDT_PRI_RESTRI_ID, CODE_LIST_ID, 
 * and AGENCY_ID_LIST_ID allows for capturing of the specific primitive to 
 * be used in the context. Only one column among the three can have a value 
 * in a particular record.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bbie extends TableImpl<BbieRecord> {

    private static final long serialVersionUID = -230442730;

    /**
     * The reference instance of <code>oagi.bbie</code>
     */
    public static final Bbie BBIE = new Bbie();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BbieRecord> getRecordType() {
        return BbieRecord.class;
    }

    /**
     * The column <code>oagi.bbie.bbie_id</code>. A internal, primary database key of a BBIE.
     */
    public final TableField<BbieRecord, ULong> BBIE_ID = createField(DSL.name("bbie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of a BBIE.");

    /**
     * The column <code>oagi.bbie.guid</code>. A globally unique identifier (GUID) of an SC. GUID of a BBIE's SC  is different from the one in the DT_SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<BbieRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an SC. GUID of a BBIE's SC  is different from the one in the DT_SC. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.bbie.based_bcc_id</code>. The BASED_BCC_ID column refers to the BCC record, which this BBIE contextualizes.
     */
    public final TableField<BbieRecord, ULong> BASED_BCC_ID = createField(DSL.name("based_bcc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "The BASED_BCC_ID column refers to the BCC record, which this BBIE contextualizes.");

    /**
     * The column <code>oagi.bbie.from_abie_id</code>. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_BCC_ID.
     */
    public final TableField<BbieRecord, ULong> FROM_ABIE_ID = createField(DSL.name("from_abie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_BCC_ID.");

    /**
     * The column <code>oagi.bbie.to_bbiep_id</code>. TO_BBIEP_ID is a foreign key to the BBIEP table. TO_BBIEP_ID basically refers to a child data element of the FROM_ABIE_ID. TO_BBIEP_ID must be based on the TO_BCCP_ID in the based BCC.
     */
    public final TableField<BbieRecord, ULong> TO_BBIEP_ID = createField(DSL.name("to_bbiep_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "TO_BBIEP_ID is a foreign key to the BBIEP table. TO_BBIEP_ID basically refers to a child data element of the FROM_ABIE_ID. TO_BBIEP_ID must be based on the TO_BCCP_ID in the based BCC.");

    /**
     * The column <code>oagi.bbie.bdt_pri_restri_id</code>. This is the foreign key to the BDT_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.
     */
    public final TableField<BbieRecord, ULong> BDT_PRI_RESTRI_ID = createField(DSL.name("bdt_pri_restri_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is the foreign key to the BDT_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.");

    /**
     * The column <code>oagi.bbie.code_list_id</code>. This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the BDT_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.
     */
    public final TableField<BbieRecord, ULong> CODE_LIST_ID = createField(DSL.name("code_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the BDT_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.");

    /**
     * The column <code>oagi.bbie.agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.
     */
    public final TableField<BbieRecord, ULong> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.");

    /**
     * The column <code>oagi.bbie.cardinality_min</code>. The minimum occurrence constraint for the BBIE. A valid value is a non-negative integer.
     */
    public final TableField<BbieRecord, Integer> CARDINALITY_MIN = createField(DSL.name("cardinality_min"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "The minimum occurrence constraint for the BBIE. A valid value is a non-negative integer.");

    /**
     * The column <code>oagi.bbie.cardinality_max</code>. Maximum occurence constraint of the TO_BBIEP_ID. A valid value is an integer from -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public final TableField<BbieRecord, Integer> CARDINALITY_MAX = createField(DSL.name("cardinality_max"), org.jooq.impl.SQLDataType.INTEGER, this, "Maximum occurence constraint of the TO_BBIEP_ID. A valid value is an integer from -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.");

    /**
     * The column <code>oagi.bbie.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<BbieRecord, String> DEFAULT_VALUE = createField(DSL.name("default_value"), org.jooq.impl.SQLDataType.CLOB, this, "This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bbie.is_nillable</code>. Indicate whether the field can have a null  This is corresponding to the nillable flag in the XML schema.
     */
    public final TableField<BbieRecord, Byte> IS_NILLABLE = createField(DSL.name("is_nillable"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Indicate whether the field can have a null  This is corresponding to the nillable flag in the XML schema.");

    /**
     * The column <code>oagi.bbie.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<BbieRecord, String> FIXED_VALUE = createField(DSL.name("fixed_value"), org.jooq.impl.SQLDataType.CLOB, this, "This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bbie.is_null</code>. This column indicates whether the field is fixed to NULL. IS_NULLl can be true only if the IS_NILLABLE is true. If IS_NULL is true then the FIX_VALUE and DEFAULT_VALUE columns cannot have a value.
     */
    public final TableField<BbieRecord, Byte> IS_NULL = createField(DSL.name("is_null"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This column indicates whether the field is fixed to NULL. IS_NULLl can be true only if the IS_NILLABLE is true. If IS_NULL is true then the FIX_VALUE and DEFAULT_VALUE columns cannot have a value.");

    /**
     * The column <code>oagi.bbie.definition</code>. Description to override the BCC definition. If NULLl, it means that the definition should be inherited from the based BCC.
     */
    public final TableField<BbieRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "Description to override the BCC definition. If NULLl, it means that the definition should be inherited from the based BCC.");

    /**
     * The column <code>oagi.bbie.example</code>.
     */
    public final TableField<BbieRecord, String> EXAMPLE = createField(DSL.name("example"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.bbie.remark</code>. This column allows the user to specify very context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public final TableField<BbieRecord, String> REMARK = createField(DSL.name("remark"), org.jooq.impl.SQLDataType.VARCHAR(225), this, "This column allows the user to specify very context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be \"Type of BOM should be recognized in the BOM/typeCode.\"");

    /**
     * The column <code>oagi.bbie.created_by</code>. A foreign key referring to the user who creates the BBIE. The creator of the BBIE is also its owner by default. BBIEs created as children of another ABIE have the same CREATED_BY.
     */
    public final TableField<BbieRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the user who creates the BBIE. The creator of the BBIE is also its owner by default. BBIEs created as children of another ABIE have the same CREATED_BY.");

    /**
     * The column <code>oagi.bbie.last_updated_by</code>. A foreign key referring to the user who has last updated the ASBIE record. 
     */
    public final TableField<BbieRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the user who has last updated the ASBIE record. ");

    /**
     * The column <code>oagi.bbie.creation_timestamp</code>. Timestamp when the BBIE record was first created. BBIEs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public final TableField<BbieRecord, Timestamp> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the BBIE record was first created. BBIEs created as children of another ABIE have the same CREATION_TIMESTAMP.");

    /**
     * The column <code>oagi.bbie.last_update_timestamp</code>. The timestamp when the ASBIE was last updated.
     */
    public final TableField<BbieRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the ASBIE was last updated.");

    /**
     * The column <code>oagi.bbie.seq_key</code>. This indicates the order of the associations among other siblings. The SEQ_KEY for BIEs is decimal in order to accomodate the removal of inheritance hierarchy and group. For example, children of the most abstract ACC will have SEQ_KEY = 1.1, 1.2, 1.3, and so on; and SEQ_KEY of the next abstraction level ACC will have SEQ_KEY = 2.1, 2.2, 2.3 and so on so forth.
     */
    public final TableField<BbieRecord, BigDecimal> SEQ_KEY = createField(DSL.name("seq_key"), org.jooq.impl.SQLDataType.DECIMAL(10, 2), this, "This indicates the order of the associations among other siblings. The SEQ_KEY for BIEs is decimal in order to accomodate the removal of inheritance hierarchy and group. For example, children of the most abstract ACC will have SEQ_KEY = 1.1, 1.2, 1.3, and so on; and SEQ_KEY of the next abstraction level ACC will have SEQ_KEY = 2.1, 2.2, 2.3 and so on so forth.");

    /**
     * The column <code>oagi.bbie.is_used</code>. Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated in the expression generation.
     */
    public final TableField<BbieRecord, Byte> IS_USED = createField(DSL.name("is_used"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated in the expression generation.");

    /**
     * The column <code>oagi.bbie.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE, which owns this BBIE record.
     */
    public final TableField<BbieRecord, ULong> OWNER_TOP_LEVEL_ABIE_ID = createField(DSL.name("owner_top_level_abie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This is a foriegn key to the ABIE table. It specifies the top-level ABIE, which owns this BBIE record.");

    /**
     * Create a <code>oagi.bbie</code> table reference
     */
    public Bbie() {
        this(DSL.name("bbie"), null);
    }

    /**
     * Create an aliased <code>oagi.bbie</code> table reference
     */
    public Bbie(String alias) {
        this(DSL.name(alias), BBIE);
    }

    /**
     * Create an aliased <code>oagi.bbie</code> table reference
     */
    public Bbie(Name alias) {
        this(alias, BBIE);
    }

    private Bbie(Name alias, Table<BbieRecord> aliased) {
        this(alias, aliased, null);
    }

    private Bbie(Name alias, Table<BbieRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("A BBIE represents a relationship/association between an ABIE and a BBIEP. It is a contextualization of a BCC. The BBIE table also stores some information about the specific constraints related to the BDT associated with the BBIEP. In particular, the three columns including the BDT_PRI_RESTRI_ID, CODE_LIST_ID, and AGENCY_ID_LIST_ID allows for capturing of the specific primitive to be used in the context. Only one column among the three can have a value in a particular record."));
    }

    public <O extends Record> Bbie(Table<O> child, ForeignKey<O, BbieRecord> key) {
        super(child, key, BBIE);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BBIE_BBIE_AGENCY_ID_LIST_ID_FK, Indexes.BBIE_BBIE_BASED_BCC_ID_FK, Indexes.BBIE_BBIE_BDT_PRI_RESTRI_ID_FK, Indexes.BBIE_BBIE_CODE_LIST_ID_FK, Indexes.BBIE_BBIE_CREATED_BY_FK, Indexes.BBIE_BBIE_FROM_ABIE_ID_FK, Indexes.BBIE_BBIE_LAST_UPDATED_BY_FK, Indexes.BBIE_BBIE_OWNER_TOP_LEVEL_ABIE_ID_FK, Indexes.BBIE_BBIE_TO_BBIEP_ID_FK, Indexes.BBIE_PRIMARY);
    }

    @Override
    public Identity<BbieRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BBIE;
    }

    @Override
    public UniqueKey<BbieRecord> getPrimaryKey() {
        return Keys.KEY_BBIE_PRIMARY;
    }

    @Override
    public List<UniqueKey<BbieRecord>> getKeys() {
        return Arrays.<UniqueKey<BbieRecord>>asList(Keys.KEY_BBIE_PRIMARY);
    }

    @Override
    public List<ForeignKey<BbieRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BbieRecord, ?>>asList(Keys.BBIE_BASED_BCC_ID_FK, Keys.BBIE_FROM_ABIE_ID_FK, Keys.BBIE_TO_BBIEP_ID_FK, Keys.BBIE_BDT_PRI_RESTRI_ID_FK, Keys.BBIE_CODE_LIST_ID_FK, Keys.BBIE_AGENCY_ID_LIST_ID_FK, Keys.BBIE_CREATED_BY_FK, Keys.BBIE_LAST_UPDATED_BY_FK, Keys.BBIE_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    public Bcc bcc() {
        return new Bcc(this, Keys.BBIE_BASED_BCC_ID_FK);
    }

    public Abie abie() {
        return new Abie(this, Keys.BBIE_FROM_ABIE_ID_FK);
    }

    public Bbiep bbiep() {
        return new Bbiep(this, Keys.BBIE_TO_BBIEP_ID_FK);
    }

    public BdtPriRestri bdtPriRestri() {
        return new BdtPriRestri(this, Keys.BBIE_BDT_PRI_RESTRI_ID_FK);
    }

    public CodeList codeList() {
        return new CodeList(this, Keys.BBIE_CODE_LIST_ID_FK);
    }

    public AgencyIdList agencyIdList() {
        return new AgencyIdList(this, Keys.BBIE_AGENCY_ID_LIST_ID_FK);
    }

    public AppUser bbieCreatedByFk() {
        return new AppUser(this, Keys.BBIE_CREATED_BY_FK);
    }

    public AppUser bbieLastUpdatedByFk() {
        return new AppUser(this, Keys.BBIE_LAST_UPDATED_BY_FK);
    }

    public TopLevelAbie topLevelAbie() {
        return new TopLevelAbie(this, Keys.BBIE_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    @Override
    public Bbie as(String alias) {
        return new Bbie(DSL.name(alias), this);
    }

    @Override
    public Bbie as(Name alias) {
        return new Bbie(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Bbie rename(String name) {
        return new Bbie(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Bbie rename(Name name) {
        return new Bbie(name, null);
    }
}
