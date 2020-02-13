/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CdtAwdPri;


/**
 * This table capture allowed primitives of the CDT?s Content Component.  
 * The information in this table is captured from the Allowed Primitive column 
 * in each of the CDT Content Component section/table in CCTS DTC3.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtAwdPriRecord extends UpdatableRecordImpl<CdtAwdPriRecord> implements Record4<ULong, ULong, ULong, Byte> {

    private static final long serialVersionUID = 886228202;

    /**
     * Setter for <code>oagi.cdt_awd_pri.cdt_awd_pri_id</code>. Primary, internal database key.
     */
    public void setCdtAwdPriId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri.cdt_awd_pri_id</code>. Primary, internal database key.
     */
    public ULong getCdtAwdPriId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.cdt_awd_pri.cdt_id</code>. Foreign key pointing to a CDT in the DT table.
     */
    public void setCdtId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri.cdt_id</code>. Foreign key pointing to a CDT in the DT table.
     */
    public ULong getCdtId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.cdt_awd_pri.cdt_pri_id</code>. Foreign key from the CDT_PRI table. It indicates the primative allowed for the CDT identified in the CDT_ID column. 
     */
    public void setCdtPriId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri.cdt_pri_id</code>. Foreign key from the CDT_PRI table. It indicates the primative allowed for the CDT identified in the CDT_ID column. 
     */
    public ULong getCdtPriId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.cdt_awd_pri.is_default</code>. Indicating a default primitive for the CDT?s Content Component. True for a default primitive; False otherwise.
     */
    public void setIsDefault(Byte value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.cdt_awd_pri.is_default</code>. Indicating a default primitive for the CDT?s Content Component. True for a default primitive; False otherwise.
     */
    public Byte getIsDefault() {
        return (Byte) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, ULong, ULong, Byte> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, ULong, ULong, Byte> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CdtAwdPri.CDT_AWD_PRI.CDT_AWD_PRI_ID;
    }

    @Override
    public Field<ULong> field2() {
        return CdtAwdPri.CDT_AWD_PRI.CDT_ID;
    }

    @Override
    public Field<ULong> field3() {
        return CdtAwdPri.CDT_AWD_PRI.CDT_PRI_ID;
    }

    @Override
    public Field<Byte> field4() {
        return CdtAwdPri.CDT_AWD_PRI.IS_DEFAULT;
    }

    @Override
    public ULong component1() {
        return getCdtAwdPriId();
    }

    @Override
    public ULong component2() {
        return getCdtId();
    }

    @Override
    public ULong component3() {
        return getCdtPriId();
    }

    @Override
    public Byte component4() {
        return getIsDefault();
    }

    @Override
    public ULong value1() {
        return getCdtAwdPriId();
    }

    @Override
    public ULong value2() {
        return getCdtId();
    }

    @Override
    public ULong value3() {
        return getCdtPriId();
    }

    @Override
    public Byte value4() {
        return getIsDefault();
    }

    @Override
    public CdtAwdPriRecord value1(ULong value) {
        setCdtAwdPriId(value);
        return this;
    }

    @Override
    public CdtAwdPriRecord value2(ULong value) {
        setCdtId(value);
        return this;
    }

    @Override
    public CdtAwdPriRecord value3(ULong value) {
        setCdtPriId(value);
        return this;
    }

    @Override
    public CdtAwdPriRecord value4(Byte value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public CdtAwdPriRecord values(ULong value1, ULong value2, ULong value3, Byte value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CdtAwdPriRecord
     */
    public CdtAwdPriRecord() {
        super(CdtAwdPri.CDT_AWD_PRI);
    }

    /**
     * Create a detached, initialised CdtAwdPriRecord
     */
    public CdtAwdPriRecord(ULong cdtAwdPriId, ULong cdtId, ULong cdtPriId, Byte isDefault) {
        super(CdtAwdPri.CDT_AWD_PRI);

        set(0, cdtAwdPriId);
        set(1, cdtId);
        set(2, cdtPriId);
        set(3, isDefault);
    }
}
