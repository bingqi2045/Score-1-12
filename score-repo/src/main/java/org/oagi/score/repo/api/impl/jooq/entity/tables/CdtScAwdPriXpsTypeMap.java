/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CdtScAwdPriXpsTypeMapRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * The purpose of this table is the same as that of the
 * CDT_AWD_PRI_XPS_TYPE_MAP, but it is for the supplementary component (SC). It
 * allows for the concrete mapping between the CDT Primitives and types in a
 * particular expression such as XML Schema, JSON. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtScAwdPriXpsTypeMap extends TableImpl<CdtScAwdPriXpsTypeMapRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.cdt_sc_awd_pri_xps_type_map</code>
     */
    public static final CdtScAwdPriXpsTypeMap CDT_SC_AWD_PRI_XPS_TYPE_MAP = new CdtScAwdPriXpsTypeMap();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CdtScAwdPriXpsTypeMapRecord> getRecordType() {
        return CdtScAwdPriXpsTypeMapRecord.class;
    }

    /**
     * The column
     * <code>oagi.cdt_sc_awd_pri_xps_type_map.cdt_sc_awd_pri_xps_type_map_id</code>.
     * Primary, internal database key.
     */
    public final TableField<CdtScAwdPriXpsTypeMapRecord, String> CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID = createField(DSL.name("cdt_sc_awd_pri_xps_type_map_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column
     * <code>oagi.cdt_sc_awd_pri_xps_type_map.cdt_sc_awd_pri_id</code>. Foreign
     * key to the CDT_SC_AWD_PRI table.
     */
    public final TableField<CdtScAwdPriXpsTypeMapRecord, String> CDT_SC_AWD_PRI_ID = createField(DSL.name("cdt_sc_awd_pri_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the CDT_SC_AWD_PRI table.");

    /**
     * The column <code>oagi.cdt_sc_awd_pri_xps_type_map.xbt_id</code>. Foreign
     * key to the XBT table.
     */
    public final TableField<CdtScAwdPriXpsTypeMapRecord, String> XBT_ID = createField(DSL.name("xbt_id"), SQLDataType.CHAR(36), this, "Foreign key to the XBT table.");

    /**
     * The column <code>oagi.cdt_sc_awd_pri_xps_type_map.is_default</code>.
     * Indicating a default value domain mapping.
     */
    public final TableField<CdtScAwdPriXpsTypeMapRecord, Byte> IS_DEFAULT = createField(DSL.name("is_default"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicating a default value domain mapping.");

    private CdtScAwdPriXpsTypeMap(Name alias, Table<CdtScAwdPriXpsTypeMapRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtScAwdPriXpsTypeMap(Name alias, Table<CdtScAwdPriXpsTypeMapRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The purpose of this table is the same as that of the CDT_AWD_PRI_XPS_TYPE_MAP, but it is for the supplementary component (SC). It allows for the concrete mapping between the CDT Primitives and types in a particular expression such as XML Schema, JSON. "), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.cdt_sc_awd_pri_xps_type_map</code> table
     * reference
     */
    public CdtScAwdPriXpsTypeMap(String alias) {
        this(DSL.name(alias), CDT_SC_AWD_PRI_XPS_TYPE_MAP);
    }

    /**
     * Create an aliased <code>oagi.cdt_sc_awd_pri_xps_type_map</code> table
     * reference
     */
    public CdtScAwdPriXpsTypeMap(Name alias) {
        this(alias, CDT_SC_AWD_PRI_XPS_TYPE_MAP);
    }

    /**
     * Create a <code>oagi.cdt_sc_awd_pri_xps_type_map</code> table reference
     */
    public CdtScAwdPriXpsTypeMap() {
        this(DSL.name("cdt_sc_awd_pri_xps_type_map"), null);
    }

    public <O extends Record> CdtScAwdPriXpsTypeMap(Table<O> child, ForeignKey<O, CdtScAwdPriXpsTypeMapRecord> key) {
        super(child, key, CDT_SC_AWD_PRI_XPS_TYPE_MAP);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<CdtScAwdPriXpsTypeMapRecord> getPrimaryKey() {
        return Keys.KEY_CDT_SC_AWD_PRI_XPS_TYPE_MAP_PRIMARY;
    }

    @Override
    public List<ForeignKey<CdtScAwdPriXpsTypeMapRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CDT_SC_AWD_PRI_XPS_TYPE_MAP_CDT_SC_AWD_PRI_ID_FK, Keys.CDT_SC_AWD_PRI_XPS_TYPE_MAP_XBT_ID_FK);
    }

    private transient CdtScAwdPri _cdtScAwdPri;
    private transient Xbt _xbt;

    /**
     * Get the implicit join path to the <code>oagi.cdt_sc_awd_pri</code> table.
     */
    public CdtScAwdPri cdtScAwdPri() {
        if (_cdtScAwdPri == null)
            _cdtScAwdPri = new CdtScAwdPri(this, Keys.CDT_SC_AWD_PRI_XPS_TYPE_MAP_CDT_SC_AWD_PRI_ID_FK);

        return _cdtScAwdPri;
    }

    /**
     * Get the implicit join path to the <code>oagi.xbt</code> table.
     */
    public Xbt xbt() {
        if (_xbt == null)
            _xbt = new Xbt(this, Keys.CDT_SC_AWD_PRI_XPS_TYPE_MAP_XBT_ID_FK);

        return _xbt;
    }

    @Override
    public CdtScAwdPriXpsTypeMap as(String alias) {
        return new CdtScAwdPriXpsTypeMap(DSL.name(alias), this);
    }

    @Override
    public CdtScAwdPriXpsTypeMap as(Name alias) {
        return new CdtScAwdPriXpsTypeMap(alias, this);
    }

    @Override
    public CdtScAwdPriXpsTypeMap as(Table<?> alias) {
        return new CdtScAwdPriXpsTypeMap(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtScAwdPriXpsTypeMap rename(String name) {
        return new CdtScAwdPriXpsTypeMap(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtScAwdPriXpsTypeMap rename(Name name) {
        return new CdtScAwdPriXpsTypeMap(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtScAwdPriXpsTypeMap rename(Table<?> name) {
        return new CdtScAwdPriXpsTypeMap(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, Byte> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super String, ? super String, ? super String, ? super Byte, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super String, ? super String, ? super String, ? super Byte, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
