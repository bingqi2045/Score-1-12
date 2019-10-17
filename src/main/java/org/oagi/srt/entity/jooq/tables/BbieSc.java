/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row17;
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
import org.oagi.srt.entity.jooq.tables.records.BbieScRecord;


/**
 * Because there is no single table that is a contextualized counterpart of 
 * the DT table (which stores both CDT and BDT), The context specific constraints 
 * associated with the DT are stored in the BBIE table, while this table stores 
 * the constraints associated with the DT's SCs. 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbieSc extends TableImpl<BbieScRecord> {

    private static final long serialVersionUID = 674013791;

    /**
     * The reference instance of <code>oagi.bbie_sc</code>
     */
    public static final BbieSc BBIE_SC = new BbieSc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BbieScRecord> getRecordType() {
        return BbieScRecord.class;
    }

    /**
     * The column <code>oagi.bbie_sc.bbie_sc_id</code>. A internal, primary database key of a BBIE_SC.
     */
    public final TableField<BbieScRecord, ULong> BBIE_SC_ID = createField(DSL.name("bbie_sc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of a BBIE_SC.");

    /**
     * The column <code>oagi.bbie_sc.guid</code>. A globally unique identifier (GUID). It is different from the GUID fo the SC on the CC side. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<BbieScRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID). It is different from the GUID fo the SC on the CC side. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.bbie_sc.bbie_id</code>. The BBIE this BBIE_SC applies to.
     */
    public final TableField<BbieScRecord, ULong> BBIE_ID = createField(DSL.name("bbie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "The BBIE this BBIE_SC applies to.");

    /**
     * The column <code>oagi.bbie_sc.dt_sc_id</code>. Foreign key to the DT_SC table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.
     */
    public final TableField<BbieScRecord, ULong> DT_SC_ID = createField(DSL.name("dt_sc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the DT_SC table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.");

    /**
     * The column <code>oagi.bbie_sc.dt_sc_pri_restri_id</code>. This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).

It is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.

This column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.
     */
    public final TableField<BbieScRecord, ULong> DT_SC_PRI_RESTRI_ID = createField(DSL.name("dt_sc_pri_restri_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).\n\nIt is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.\n\nThis column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.");

    /**
     * The column <code>oagi.bbie_sc.code_list_id</code>. This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.

This column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.
     */
    public final TableField<BbieScRecord, ULong> CODE_LIST_ID = createField(DSL.name("code_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.\n\nThis column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.");

    /**
     * The column <code>oagi.bbie_sc.agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.

This column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.
     */
    public final TableField<BbieScRecord, ULong> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.\n\nThis column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.");

    /**
     * The column <code>oagi.bbie_sc.cardinality_min</code>. The minimum occurrence constraint for the BBIE SC. A valid value is 0 or 1.
     */
    public final TableField<BbieScRecord, Integer> CARDINALITY_MIN = createField(DSL.name("cardinality_min"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "The minimum occurrence constraint for the BBIE SC. A valid value is 0 or 1.");

    /**
     * The column <code>oagi.bbie_sc.cardinality_max</code>. Maximum occurence constraint of the BBIE SC. A valid value is 0 or 1.
     */
    public final TableField<BbieScRecord, Integer> CARDINALITY_MAX = createField(DSL.name("cardinality_max"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Maximum occurence constraint of the BBIE SC. A valid value is 0 or 1.");

    /**
     * The column <code>oagi.bbie_sc.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<BbieScRecord, String> DEFAULT_VALUE = createField(DSL.name("default_value"), org.jooq.impl.SQLDataType.CLOB, this, "This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bbie_sc.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<BbieScRecord, String> FIXED_VALUE = createField(DSL.name("fixed_value"), org.jooq.impl.SQLDataType.CLOB, this, "This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bbie_sc.definition</code>. Description to override the BDT SC definition. If NULL, it means that the definition should be inherited from the based BDT SC.
     */
    public final TableField<BbieScRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "Description to override the BDT SC definition. If NULL, it means that the definition should be inherited from the based BDT SC.");

    /**
     * The column <code>oagi.bbie_sc.example_text_content_id</code>.
     */
    public final TableField<BbieScRecord, ULong> EXAMPLE_TEXT_CONTENT_ID = createField(DSL.name("example_text_content_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.bbie_sc.remark</code>. This column allows the user to specify a very context-specific usage of the BBIE SC. It is different from the Definition column in that the Definition column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. 
     */
    public final TableField<BbieScRecord, String> REMARK = createField(DSL.name("remark"), org.jooq.impl.SQLDataType.VARCHAR(225), this, "This column allows the user to specify a very context-specific usage of the BBIE SC. It is different from the Definition column in that the Definition column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. ");

    /**
     * The column <code>oagi.bbie_sc.biz_term</code>. Business term to indicate what the BBIE SC is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public final TableField<BbieScRecord, String> BIZ_TERM = createField(DSL.name("biz_term"), org.jooq.impl.SQLDataType.VARCHAR(225), this, "Business term to indicate what the BBIE SC is called in a particular business context. With this current design, only one business term is allowed per business context.");

    /**
     * The column <code>oagi.bbie_sc.is_used</code>. Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated.
     */
    public final TableField<BbieScRecord, Byte> IS_USED = createField(DSL.name("is_used"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated.");

    /**
     * The column <code>oagi.bbie_sc.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE. It specifies the top-level ABIE, which owns this BBIE_SC record.
     */
    public final TableField<BbieScRecord, ULong> OWNER_TOP_LEVEL_ABIE_ID = createField(DSL.name("owner_top_level_abie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This is a foriegn key to the ABIE. It specifies the top-level ABIE, which owns this BBIE_SC record.");

    /**
     * Create a <code>oagi.bbie_sc</code> table reference
     */
    public BbieSc() {
        this(DSL.name("bbie_sc"), null);
    }

    /**
     * Create an aliased <code>oagi.bbie_sc</code> table reference
     */
    public BbieSc(String alias) {
        this(DSL.name(alias), BBIE_SC);
    }

    /**
     * Create an aliased <code>oagi.bbie_sc</code> table reference
     */
    public BbieSc(Name alias) {
        this(alias, BBIE_SC);
    }

    private BbieSc(Name alias, Table<BbieScRecord> aliased) {
        this(alias, aliased, null);
    }

    private BbieSc(Name alias, Table<BbieScRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Because there is no single table that is a contextualized counterpart of the DT table (which stores both CDT and BDT), The context specific constraints associated with the DT are stored in the BBIE table, while this table stores the constraints associated with the DT's SCs. "));
    }

    public <O extends Record> BbieSc(Table<O> child, ForeignKey<O, BbieScRecord> key) {
        super(child, key, BBIE_SC);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BBIE_SC_BBIE_SC_AGENCY_ID_LIST_ID_FK, Indexes.BBIE_SC_BBIE_SC_BBIE_ID_FK, Indexes.BBIE_SC_BBIE_SC_CODE_LIST_ID_FK, Indexes.BBIE_SC_BBIE_SC_DT_SC_ID_FK, Indexes.BBIE_SC_BBIE_SC_DT_SC_PRI_RESTRI_ID_FK, Indexes.BBIE_SC_BBIE_SC_EXAMPLE_TEXT_CONTENT_ID_FK, Indexes.BBIE_SC_BBIE_SC_OWNER_TOP_LEVEL_ABIE_ID_FK, Indexes.BBIE_SC_PRIMARY);
    }

    @Override
    public Identity<BbieScRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BBIE_SC;
    }

    @Override
    public UniqueKey<BbieScRecord> getPrimaryKey() {
        return Keys.KEY_BBIE_SC_PRIMARY;
    }

    @Override
    public List<UniqueKey<BbieScRecord>> getKeys() {
        return Arrays.<UniqueKey<BbieScRecord>>asList(Keys.KEY_BBIE_SC_PRIMARY);
    }

    @Override
    public List<ForeignKey<BbieScRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BbieScRecord, ?>>asList(Keys.BBIE_SC_BBIE_ID_FK, Keys.BBIE_SC_DT_SC_ID_FK, Keys.BBIE_SC_DT_SC_PRI_RESTRI_ID_FK, Keys.BBIE_SC_CODE_LIST_ID_FK, Keys.BBIE_SC_AGENCY_ID_LIST_ID_FK, Keys.BBIE_SC_EXAMPLE_TEXT_CONTENT_ID_FK, Keys.BBIE_SC_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    public Bbie bbie() {
        return new Bbie(this, Keys.BBIE_SC_BBIE_ID_FK);
    }

    public DtSc dtSc() {
        return new DtSc(this, Keys.BBIE_SC_DT_SC_ID_FK);
    }

    public BdtScPriRestri bdtScPriRestri() {
        return new BdtScPriRestri(this, Keys.BBIE_SC_DT_SC_PRI_RESTRI_ID_FK);
    }

    public CodeList codeList() {
        return new CodeList(this, Keys.BBIE_SC_CODE_LIST_ID_FK);
    }

    public AgencyIdList agencyIdList() {
        return new AgencyIdList(this, Keys.BBIE_SC_AGENCY_ID_LIST_ID_FK);
    }

    public TextContent textContent() {
        return new TextContent(this, Keys.BBIE_SC_EXAMPLE_TEXT_CONTENT_ID_FK);
    }

    public TopLevelAbie topLevelAbie() {
        return new TopLevelAbie(this, Keys.BBIE_SC_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    @Override
    public BbieSc as(String alias) {
        return new BbieSc(DSL.name(alias), this);
    }

    @Override
    public BbieSc as(Name alias) {
        return new BbieSc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BbieSc rename(String name) {
        return new BbieSc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BbieSc rename(Name name) {
        return new BbieSc(name, null);
    }

    // -------------------------------------------------------------------------
    // Row17 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row17<ULong, String, ULong, ULong, ULong, ULong, ULong, Integer, Integer, String, String, String, ULong, String, String, Byte, ULong> fieldsRow() {
        return (Row17) super.fieldsRow();
    }
}
