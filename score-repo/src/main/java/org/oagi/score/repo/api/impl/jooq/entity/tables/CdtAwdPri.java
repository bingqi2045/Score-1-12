/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CdtAwdPriRecord;


/**
 * This table capture allowed primitives of the CDT?s Content Component.  
 * The information in this table is captured from the Allowed Primitive column 
 * in each of the CDT Content Component section/table in CCTS DTC3.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtAwdPri extends TableImpl<CdtAwdPriRecord> {

    private static final long serialVersionUID = 820838459;

    /**
     * The reference instance of <code>oagi.cdt_awd_pri</code>
     */
    public static final CdtAwdPri CDT_AWD_PRI = new CdtAwdPri();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CdtAwdPriRecord> getRecordType() {
        return CdtAwdPriRecord.class;
    }

    /**
     * The column <code>oagi.cdt_awd_pri.cdt_awd_pri_id</code>. Primary, internal database key.
     */
    public final TableField<CdtAwdPriRecord, ULong> CDT_AWD_PRI_ID = createField(DSL.name("cdt_awd_pri_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.cdt_awd_pri.cdt_id</code>. Foreign key pointing to a CDT in the DT table.
     */
    public final TableField<CdtAwdPriRecord, ULong> CDT_ID = createField(DSL.name("cdt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key pointing to a CDT in the DT table.");

    /**
     * The column <code>oagi.cdt_awd_pri.cdt_pri_id</code>. Foreign key from the CDT_PRI table. It indicates the primative allowed for the CDT identified in the CDT_ID column. 
     */
    public final TableField<CdtAwdPriRecord, ULong> CDT_PRI_ID = createField(DSL.name("cdt_pri_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key from the CDT_PRI table. It indicates the primative allowed for the CDT identified in the CDT_ID column. ");

    /**
     * The column <code>oagi.cdt_awd_pri.is_default</code>. Indicating a default primitive for the CDT?s Content Component. True for a default primitive; False otherwise.
     */
    public final TableField<CdtAwdPriRecord, Byte> IS_DEFAULT = createField(DSL.name("is_default"), org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "Indicating a default primitive for the CDT?s Content Component. True for a default primitive; False otherwise.");

    /**
     * Create a <code>oagi.cdt_awd_pri</code> table reference
     */
    public CdtAwdPri() {
        this(DSL.name("cdt_awd_pri"), null);
    }

    /**
     * Create an aliased <code>oagi.cdt_awd_pri</code> table reference
     */
    public CdtAwdPri(String alias) {
        this(DSL.name(alias), CDT_AWD_PRI);
    }

    /**
     * Create an aliased <code>oagi.cdt_awd_pri</code> table reference
     */
    public CdtAwdPri(Name alias) {
        this(alias, CDT_AWD_PRI);
    }

    private CdtAwdPri(Name alias, Table<CdtAwdPriRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtAwdPri(Name alias, Table<CdtAwdPriRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table capture allowed primitives of the CDT?s Content Component.  The information in this table is captured from the Allowed Primitive column in each of the CDT Content Component section/table in CCTS DTC3."), TableOptions.table());
    }

    public <O extends Record> CdtAwdPri(Table<O> child, ForeignKey<O, CdtAwdPriRecord> key) {
        super(child, key, CDT_AWD_PRI);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<CdtAwdPriRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CDT_AWD_PRI;
    }

    @Override
    public UniqueKey<CdtAwdPriRecord> getPrimaryKey() {
        return Keys.KEY_CDT_AWD_PRI_PRIMARY;
    }

    @Override
    public List<UniqueKey<CdtAwdPriRecord>> getKeys() {
        return Arrays.<UniqueKey<CdtAwdPriRecord>>asList(Keys.KEY_CDT_AWD_PRI_PRIMARY);
    }

    @Override
    public List<ForeignKey<CdtAwdPriRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CdtAwdPriRecord, ?>>asList(Keys.CDT_AWD_PRI_CDT_ID_FK, Keys.CDT_AWD_PRI_CDT_PRI_ID_FK);
    }

    public Dt dt() {
        return new Dt(this, Keys.CDT_AWD_PRI_CDT_ID_FK);
    }

    public CdtPri cdtPri() {
        return new CdtPri(this, Keys.CDT_AWD_PRI_CDT_PRI_ID_FK);
    }

    @Override
    public CdtAwdPri as(String alias) {
        return new CdtAwdPri(DSL.name(alias), this);
    }

    @Override
    public CdtAwdPri as(Name alias) {
        return new CdtAwdPri(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtAwdPri rename(String name) {
        return new CdtAwdPri(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtAwdPri rename(Name name) {
        return new CdtAwdPri(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, ULong, ULong, Byte> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}