/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CdtAwdPriXpsTypeMap;


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
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtAwdPriXpsTypeMapRecord extends UpdatableRecordImpl<CdtAwdPriXpsTypeMapRecord> implements Record3<ULong, ULong, ULong> {

    private static final long serialVersionUID = -351511357;

    /**
     * Setter for <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_xps_type_map_id</code>. Internal, primary database key.
     */
    public void setCdtAwdPriXpsTypeMapId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_xps_type_map_id</code>. Internal, primary database key.
     */
    public ULong getCdtAwdPriXpsTypeMapId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_id</code>. Foreign key to the CDT_AWD_PRI table.
     */
    public void setCdtAwdPriId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri_xps_type_map.cdt_awd_pri_id</code>. Foreign key to the CDT_AWD_PRI table.
     */
    public ULong getCdtAwdPriId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.cdt_awd_pri_xps_type_map.xbt_id</code>. Foreign key and to the XBT table. It identifies the XML schema built-in types that can be mapped to the CDT primivite identified in the CDT_AWD_PRI_ID column. The CDT primitives are typically broad and hence it usually maps to more than one XML schema built-in types.
     */
    public void setXbtId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri_xps_type_map.xbt_id</code>. Foreign key and to the XBT table. It identifies the XML schema built-in types that can be mapped to the CDT primivite identified in the CDT_AWD_PRI_ID column. The CDT primitives are typically broad and hence it usually maps to more than one XML schema built-in types.
     */
    public ULong getXbtId() {
        return (ULong) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<ULong, ULong, ULong> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID;
    }

    @Override
    public Field<ULong> field2() {
        return CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_ID;
    }

    @Override
    public Field<ULong> field3() {
        return CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID;
    }

    @Override
    public ULong component1() {
        return getCdtAwdPriXpsTypeMapId();
    }

    @Override
    public ULong component2() {
        return getCdtAwdPriId();
    }

    @Override
    public ULong component3() {
        return getXbtId();
    }

    @Override
    public ULong value1() {
        return getCdtAwdPriXpsTypeMapId();
    }

    @Override
    public ULong value2() {
        return getCdtAwdPriId();
    }

    @Override
    public ULong value3() {
        return getXbtId();
    }

    @Override
    public CdtAwdPriXpsTypeMapRecord value1(ULong value) {
        setCdtAwdPriXpsTypeMapId(value);
        return this;
    }

    @Override
    public CdtAwdPriXpsTypeMapRecord value2(ULong value) {
        setCdtAwdPriId(value);
        return this;
    }

    @Override
    public CdtAwdPriXpsTypeMapRecord value3(ULong value) {
        setXbtId(value);
        return this;
    }

    @Override
    public CdtAwdPriXpsTypeMapRecord values(ULong value1, ULong value2, ULong value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CdtAwdPriXpsTypeMapRecord
     */
    public CdtAwdPriXpsTypeMapRecord() {
        super(CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP);
    }

    /**
     * Create a detached, initialised CdtAwdPriXpsTypeMapRecord
     */
    public CdtAwdPriXpsTypeMapRecord(ULong cdtAwdPriXpsTypeMapId, ULong cdtAwdPriId, ULong xbtId) {
        super(CdtAwdPriXpsTypeMap.CDT_AWD_PRI_XPS_TYPE_MAP);

        set(0, cdtAwdPriXpsTypeMapId);
        set(1, cdtAwdPriId);
        set(2, xbtId);
    }
}
