/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row12;
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
import org.oagi.srt.entity.jooq.tables.records.DtScRecord;


/**
 * This table represents the supplementary component (SC) of a DT. Revision 
 * is not tracked at the supplementary component. It is considered intrinsic 
 * part of the DT. In other words, when a new revision of a DT is created 
 * a new set of supplementary components is created along with it. 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtSc extends TableImpl<DtScRecord> {

    private static final long serialVersionUID = 629998207;

    /**
     * The reference instance of <code>oagi.dt_sc</code>
     */
    public static final DtSc DT_SC = new DtSc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtScRecord> getRecordType() {
        return DtScRecord.class;
    }

    /**
     * The column <code>oagi.dt_sc.dt_sc_id</code>. Internal, primary database key.
     */
    public final TableField<DtScRecord, ULong> DT_SC_ID = createField(DSL.name("dt_sc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.dt_sc.guid</code>. A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.
     */
    public final TableField<DtScRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.");

    /**
     * The column <code>oagi.dt_sc.property_term</code>. Property term of the SC.
     */
    public final TableField<DtScRecord, String> PROPERTY_TERM = createField(DSL.name("property_term"), org.jooq.impl.SQLDataType.VARCHAR(60), this, "Property term of the SC.");

    /**
     * The column <code>oagi.dt_sc.representation_term</code>. Representation of the supplementary component.
     */
    public final TableField<DtScRecord, String> REPRESENTATION_TERM = createField(DSL.name("representation_term"), org.jooq.impl.SQLDataType.VARCHAR(20), this, "Representation of the supplementary component.");

    /**
     * The column <code>oagi.dt_sc.definition</code>. Description of the supplementary component.
     */
    public final TableField<DtScRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "Description of the supplementary component.");

    /**
     * The column <code>oagi.dt_sc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<DtScRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), org.jooq.impl.SQLDataType.VARCHAR(200), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.dt_sc.owner_dt_id</code>. Foreigned key to the DT table indicating the data type, to which this supplementary component belongs.
     */
    public final TableField<DtScRecord, ULong> OWNER_DT_ID = createField(DSL.name("owner_dt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreigned key to the DT table indicating the data type, to which this supplementary component belongs.");

    /**
     * The column <code>oagi.dt_sc.cardinality_min</code>. The minimum occurrence constraint associated with the supplementary component. The valid values zero or one.
     */
    public final TableField<DtScRecord, Integer> CARDINALITY_MIN = createField(DSL.name("cardinality_min"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "The minimum occurrence constraint associated with the supplementary component. The valid values zero or one.");

    /**
     * The column <code>oagi.dt_sc.cardinality_max</code>. The maximum occurrence constraint associated with the supplementary component. The valid values are zero or one. Zero is used when the SC is restricted from an instantiation in the data type.
     */
    public final TableField<DtScRecord, Integer> CARDINALITY_MAX = createField(DSL.name("cardinality_max"), org.jooq.impl.SQLDataType.INTEGER, this, "The maximum occurrence constraint associated with the supplementary component. The valid values are zero or one. Zero is used when the SC is restricted from an instantiation in the data type.");

    /**
     * The column <code>oagi.dt_sc.based_dt_sc_id</code>. Foreign key to the DT_SC table itself. This column is used when the SC is derived from the based DT.
     */
    public final TableField<DtScRecord, ULong> BASED_DT_SC_ID = createField(DSL.name("based_dt_sc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the DT_SC table itself. This column is used when the SC is derived from the based DT.");

    /**
     * The column <code>oagi.dt_sc.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<DtScRecord, String> DEFAULT_VALUE = createField(DSL.name("default_value"), org.jooq.impl.SQLDataType.CLOB, this, "This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.dt_sc.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<DtScRecord, String> FIXED_VALUE = createField(DSL.name("fixed_value"), org.jooq.impl.SQLDataType.CLOB, this, "This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * Create a <code>oagi.dt_sc</code> table reference
     */
    public DtSc() {
        this(DSL.name("dt_sc"), null);
    }

    /**
     * Create an aliased <code>oagi.dt_sc</code> table reference
     */
    public DtSc(String alias) {
        this(DSL.name(alias), DT_SC);
    }

    /**
     * Create an aliased <code>oagi.dt_sc</code> table reference
     */
    public DtSc(Name alias) {
        this(alias, DT_SC);
    }

    private DtSc(Name alias, Table<DtScRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtSc(Name alias, Table<DtScRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table represents the supplementary component (SC) of a DT. Revision is not tracked at the supplementary component. It is considered intrinsic part of the DT. In other words, when a new revision of a DT is created a new set of supplementary components is created along with it. "));
    }

    public <O extends Record> DtSc(Table<O> child, ForeignKey<O, DtScRecord> key) {
        super(child, key, DT_SC);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.DT_SC_DT_SC_BASED_DT_SC_ID_FK, Indexes.DT_SC_DT_SC_OWNER_DT_ID_FK, Indexes.DT_SC_DT_SC_UK1, Indexes.DT_SC_PRIMARY);
    }

    @Override
    public Identity<DtScRecord, ULong> getIdentity() {
        return Keys.IDENTITY_DT_SC;
    }

    @Override
    public UniqueKey<DtScRecord> getPrimaryKey() {
        return Keys.KEY_DT_SC_PRIMARY;
    }

    @Override
    public List<UniqueKey<DtScRecord>> getKeys() {
        return Arrays.<UniqueKey<DtScRecord>>asList(Keys.KEY_DT_SC_PRIMARY, Keys.KEY_DT_SC_DT_SC_UK1);
    }

    @Override
    public List<ForeignKey<DtScRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DtScRecord, ?>>asList(Keys.DT_SC_OWNER_DT_ID_FK, Keys.DT_SC_BASED_DT_SC_ID_FK);
    }

    public Dt dt() {
        return new Dt(this, Keys.DT_SC_OWNER_DT_ID_FK);
    }

    public org.oagi.srt.entity.jooq.tables.DtSc dtSc() {
        return new org.oagi.srt.entity.jooq.tables.DtSc(this, Keys.DT_SC_BASED_DT_SC_ID_FK);
    }

    @Override
    public DtSc as(String alias) {
        return new DtSc(DSL.name(alias), this);
    }

    @Override
    public DtSc as(Name alias) {
        return new DtSc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DtSc rename(String name) {
        return new DtSc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtSc rename(Name name) {
        return new DtSc(name, null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }
}
