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
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CdtPriRecord;


/**
 * This table stores the CDT primitives.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtPri extends TableImpl<CdtPriRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.cdt_pri</code>
     */
    public static final CdtPri CDT_PRI = new CdtPri();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CdtPriRecord> getRecordType() {
        return CdtPriRecord.class;
    }

    /**
     * The column <code>oagi.cdt_pri.cdt_pri_id</code>. Internal, primary
     * database key.
     */
    public final TableField<CdtPriRecord, ULong> CDT_PRI_ID = createField(DSL.name("cdt_pri_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.cdt_pri.name</code>. Name of the CDT primitive per
     * the CCTS datatype catalog, e.g., Decimal.
     */
    public final TableField<CdtPriRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(45).nullable(false), this, "Name of the CDT primitive per the CCTS datatype catalog, e.g., Decimal.");

    private CdtPri(Name alias, Table<CdtPriRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtPri(Name alias, Table<CdtPriRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table stores the CDT primitives."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.cdt_pri</code> table reference
     */
    public CdtPri(String alias) {
        this(DSL.name(alias), CDT_PRI);
    }

    /**
     * Create an aliased <code>oagi.cdt_pri</code> table reference
     */
    public CdtPri(Name alias) {
        this(alias, CDT_PRI);
    }

    /**
     * Create a <code>oagi.cdt_pri</code> table reference
     */
    public CdtPri() {
        this(DSL.name("cdt_pri"), null);
    }

    public <O extends Record> CdtPri(Table<O> child, ForeignKey<O, CdtPriRecord> key) {
        super(child, key, CDT_PRI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<CdtPriRecord, ULong> getIdentity() {
        return (Identity<CdtPriRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<CdtPriRecord> getPrimaryKey() {
        return Keys.KEY_CDT_PRI_PRIMARY;
    }

    @Override
    public List<UniqueKey<CdtPriRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_CDT_PRI_CDT_PRI_UK1);
    }

    @Override
    public CdtPri as(String alias) {
        return new CdtPri(DSL.name(alias), this);
    }

    @Override
    public CdtPri as(Name alias) {
        return new CdtPri(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtPri rename(String name) {
        return new CdtPri(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtPri rename(Name name) {
        return new CdtPri(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
