/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.Keys;
import org.oagi.score.entity.jooq.Oagi;
import org.oagi.score.entity.jooq.tables.records.CdtAwdPriXpsTypeMapRecord;


/**
 * This table allows for concrete mapping between the CDT Primitives and types 
 * in a particular expression such as XML Schema, JSON. At this point, it 
 * is not clear whether a separate table will be needed for each expression. 
 * The current table holds the map to XML Schema built-in types. 
 * 
 * For each additional expression, a column similar to the XBT_ID column will 
 * need to be added to this table for mapping to data types in another expression.
 * 
 * If we use a separate table for each expression, then we need binding all 
 * the way to BDT (or even BBIE) for every new expression. That would be almost 
 * like just store a BDT file. But using a column may not work with all kinds 
 * of expressions, particulary if it does not map well to the XML schema data 
 * types. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtAwdPriXpsTypeMap extends TableImpl<CdtAwdPriXpsTypeMapRecord> {

    private static final long serialVersionUID = -1921614119;

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
     * The column <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_xps_type_map_id</code>. Internal, primary database key.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, ULong> CDT_AWD_PRI_XPS_TYPE_MAP_ID = createField(DSL.name("cdt_awd_pri_xps_type_map_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_id</code>. Foreign key to the CDT_AWD_PRI table.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, ULong> CDT_AWD_PRI_ID = createField(DSL.name("cdt_awd_pri_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the CDT_AWD_PRI table.");

    /**
     * The column <code>oagi.cdt_awd_pri_xps_type_map.xbt_id</code>. Foreign key and to the XBT table. It identifies the XML schema built-in types that can be mapped to the CDT primivite identified in the CDT_AWD_PRI_ID column. The CDT primitives are typically broad and hence it usually maps to more than one XML schema built-in types.
     */
    public final TableField<CdtAwdPriXpsTypeMapRecord, ULong> XBT_ID = createField(DSL.name("xbt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key and to the XBT table. It identifies the XML schema built-in types that can be mapped to the CDT primivite identified in the CDT_AWD_PRI_ID column. The CDT primitives are typically broad and hence it usually maps to more than one XML schema built-in types.");

    /**
     * Create a <code>oagi.cdt_awd_pri_xps_type_map</code> table reference
     */
    public CdtAwdPriXpsTypeMap() {
        this(DSL.name("cdt_awd_pri_xps_type_map"), null);
    }

    /**
     * Create an aliased <code>oagi.cdt_awd_pri_xps_type_map</code> table reference
     */
    public CdtAwdPriXpsTypeMap(String alias) {
        this(DSL.name(alias), CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    /**
     * Create an aliased <code>oagi.cdt_awd_pri_xps_type_map</code> table reference
     */
    public CdtAwdPriXpsTypeMap(Name alias) {
        this(alias, CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    private CdtAwdPriXpsTypeMap(Name alias, Table<CdtAwdPriXpsTypeMapRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtAwdPriXpsTypeMap(Name alias, Table<CdtAwdPriXpsTypeMapRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table allows for concrete mapping between the CDT Primitives and types in a particular expression such as XML Schema, JSON. At this point, it is not clear whether a separate table will be needed for each expression. The current table holds the map to XML Schema built-in types. \n\nFor each additional expression, a column similar to the XBT_ID column will need to be added to this table for mapping to data types in another expression.\n\nIf we use a separate table for each expression, then we need binding all the way to BDT (or even BBIE) for every new expression. That would be almost like just store a BDT file. But using a column may not work with all kinds of expressions, particulary if it does not map well to the XML schema data types. "), TableOptions.table());
    }

    public <O extends Record> CdtAwdPriXpsTypeMap(Table<O> child, ForeignKey<O, CdtAwdPriXpsTypeMapRecord> key) {
        super(child, key, CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<CdtAwdPriXpsTypeMapRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CDT_AWD_PRI_XPS_TYPE_MAP;
    }

    @Override
    public UniqueKey<CdtAwdPriXpsTypeMapRecord> getPrimaryKey() {
        return Keys.KEY_CDT_AWD_PRI_XPS_TYPE_MAP_PRIMARY;
    }

    @Override
    public List<UniqueKey<CdtAwdPriXpsTypeMapRecord>> getKeys() {
        return Arrays.<UniqueKey<CdtAwdPriXpsTypeMapRecord>>asList(Keys.KEY_CDT_AWD_PRI_XPS_TYPE_MAP_PRIMARY);
    }

    @Override
    public List<ForeignKey<CdtAwdPriXpsTypeMapRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CdtAwdPriXpsTypeMapRecord, ?>>asList(Keys.CDT_AWD_PRI_XPS_TYPE_MAP_CDT_AWD_PRI_ID_FK, Keys.CDT_AWD_PRI_XPS_TYPE_MAP_XBT_ID_FK);
    }

    public CdtAwdPri cdtAwdPri() {
        return new CdtAwdPri(this, Keys.CDT_AWD_PRI_XPS_TYPE_MAP_CDT_AWD_PRI_ID_FK);
    }

    public Xbt xbt() {
        return new Xbt(this, Keys.CDT_AWD_PRI_XPS_TYPE_MAP_XBT_ID_FK);
    }

    @Override
    public CdtAwdPriXpsTypeMap as(String alias) {
        return new CdtAwdPriXpsTypeMap(DSL.name(alias), this);
    }

    @Override
    public CdtAwdPriXpsTypeMap as(Name alias) {
        return new CdtAwdPriXpsTypeMap(alias, this);
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

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
