/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CdtAwdPriXpsTypeMapRecord;


/**
 * This table allows for concrete mapping between the CDT Primitives and types
 * in a particular expression such as XML Schema, JSON. At this point, it is not
 * clear whether a separate table will be needed for each expression. The
 * current table holds the map to XML Schema built-in types. 
 * 
 * For each additional expression, a column similar to the XBT_ID column will
 * need to be added to this table for mapping to data types in another
 * expression.
 * 
 * If we use a separate table for each expression, then we need binding all the
 * way to BDT (or even BBIE) for every new expression. That would be almost like
 * just store a BDT file. But using a column may not work with all kinds of
 * expressions, particulary if it does not map well to the XML schema data
 * types. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtAwdPriXpsTypeMap extends TableImpl<CdtAwdPriXpsTypeMapRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.cdt_awd_pri_xps_type_map</code>
     */
    public static final CdtAwdPriXpsTypeMap CDT_AWD_PRI_XPS_TYPE_MAP = new CdtAwdPriXpsTypeMap();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CdtAwdPriXpsTypeMapRecord> getRecordType() {
        return CdtAwdPriXpsTypeMapRecord.class;
    }

    /**
     * The column
     * <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_xps_type_map_id</code>.
     * Primary, internal database key.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, String> CDT_AWD_PRI_XPS_TYPE_MAP_ID = createField(DSL.name("cdt_awd_pri_xps_type_map_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_id</code>.
     * Foreign key to the CDT_AWD_PRI table.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, String> CDT_AWD_PRI_ID = createField(DSL.name("cdt_awd_pri_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the CDT_AWD_PRI table.");

    /**
     * The column <code>oagi.cdt_awd_pri_xps_type_map.xbt_id</code>. Foreign key
     * to the XBT table.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, String> XBT_ID = createField(DSL.name("xbt_id"), SQLDataType.CHAR(36), this, "Foreign key to the XBT table.");

    /**
     * The column <code>oagi.cdt_awd_pri_xps_type_map.is_default</code>.
     * Indicating a default value domain mapping.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, Byte> IS_DEFAULT = createField(DSL.name("is_default"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicating a default value domain mapping.");

    private CdtAwdPriXpsTypeMap(Name alias, Table<CdtAwdPriXpsTypeMapRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtAwdPriXpsTypeMap(Name alias, Table<CdtAwdPriXpsTypeMapRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table allows for concrete mapping between the CDT Primitives and types in a particular expression such as XML Schema, JSON. At this point, it is not clear whether a separate table will be needed for each expression. The current table holds the map to XML Schema built-in types. \n\nFor each additional expression, a column similar to the XBT_ID column will need to be added to this table for mapping to data types in another expression.\n\nIf we use a separate table for each expression, then we need binding all the way to BDT (or even BBIE) for every new expression. That would be almost like just store a BDT file. But using a column may not work with all kinds of expressions, particulary if it does not map well to the XML schema data types. "), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.cdt_awd_pri_xps_type_map</code> table
     * reference
     */
    public CdtAwdPriXpsTypeMap(String alias) {
        this(DSL.name(alias), CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    /**
     * Create an aliased <code>oagi.cdt_awd_pri_xps_type_map</code> table
     * reference
     */
    public CdtAwdPriXpsTypeMap(Name alias) {
        this(alias, CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    /**
     * Create a <code>oagi.cdt_awd_pri_xps_type_map</code> table reference
     */
    public CdtAwdPriXpsTypeMap() {
        this(DSL.name("cdt_awd_pri_xps_type_map"), null);
    }

    public <O extends Record> CdtAwdPriXpsTypeMap(Table<O> child, ForeignKey<O, CdtAwdPriXpsTypeMapRecord> key) {
        super(child, key, CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<CdtAwdPriXpsTypeMapRecord> getPrimaryKey() {
        return Keys.KEY_CDT_AWD_PRI_XPS_TYPE_MAP_PRIMARY;
    }

    @Override
    public List<ForeignKey<CdtAwdPriXpsTypeMapRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CDT_AWD_PRI_XPS_TYPE_MAP_CDT_AWD_PRI_ID_FK, Keys.CDT_AWD_PRI_XPS_TYPE_MAP_XBT_ID_FK);
    }

    private transient CdtAwdPri _cdtAwdPri;
    private transient Xbt _xbt;

    /**
     * Get the implicit join path to the <code>oagi.cdt_awd_pri</code> table.
     */
    public CdtAwdPri cdtAwdPri() {
        if (_cdtAwdPri == null)
            _cdtAwdPri = new CdtAwdPri(this, Keys.CDT_AWD_PRI_XPS_TYPE_MAP_CDT_AWD_PRI_ID_FK);

        return _cdtAwdPri;
    }

    /**
     * Get the implicit join path to the <code>oagi.xbt</code> table.
     */
    public Xbt xbt() {
        if (_xbt == null)
            _xbt = new Xbt(this, Keys.CDT_AWD_PRI_XPS_TYPE_MAP_XBT_ID_FK);

        return _xbt;
    }

    @Override
    public CdtAwdPriXpsTypeMap as(String alias) {
        return new CdtAwdPriXpsTypeMap(DSL.name(alias), this);
    }

    @Override
    public CdtAwdPriXpsTypeMap as(Name alias) {
        return new CdtAwdPriXpsTypeMap(alias, this);
    }

    @Override
    public CdtAwdPriXpsTypeMap as(Table<?> alias) {
        return new CdtAwdPriXpsTypeMap(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtAwdPriXpsTypeMap rename(String name) {
        return new CdtAwdPriXpsTypeMap(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtAwdPriXpsTypeMap rename(Name name) {
        return new CdtAwdPriXpsTypeMap(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtAwdPriXpsTypeMap rename(Table<?> name) {
        return new CdtAwdPriXpsTypeMap(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, Byte> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super String, ? super String, ? super String, ? super Byte, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super String, ? super String, ? super String, ? super Byte, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
