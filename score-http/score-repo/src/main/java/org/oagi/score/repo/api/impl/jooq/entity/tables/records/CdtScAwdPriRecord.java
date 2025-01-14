/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtScAwdPri;


/**
 * This table capture the CDT primitives allowed for a particular SC of a CDT.
 * It also stores the CDT primitives allowed for a SC of a BDT that extends its
 * base (such SC is not defined in the CCTS data type catalog specification).
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtScAwdPriRecord extends UpdatableRecordImpl<CdtScAwdPriRecord> implements Record4<ULong, ULong, ULong, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.cdt_sc_awd_pri.cdt_sc_awd_pri_id</code>. Internal,
     * primary database key.
     */
    public void setCdtScAwdPriId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.cdt_sc_awd_pri.cdt_sc_awd_pri_id</code>. Internal,
     * primary database key.
     */
    public ULong getCdtScAwdPriId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.cdt_sc_awd_pri.cdt_sc_id</code>. Foreign key
     * pointing to the supplementary component (SC).
     */
    public void setCdtScId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.cdt_sc_awd_pri.cdt_sc_id</code>. Foreign key
     * pointing to the supplementary component (SC).
     */
    public ULong getCdtScId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.cdt_sc_awd_pri.cdt_pri_id</code>. A foreign key
     * pointing to the CDT_Pri table. It represents a CDT primitive allowed for
     * the suppliement component identified in the CDT_SC_ID column.
     */
    public void setCdtPriId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.cdt_sc_awd_pri.cdt_pri_id</code>. A foreign key
     * pointing to the CDT_Pri table. It represents a CDT primitive allowed for
     * the suppliement component identified in the CDT_SC_ID column.
     */
    public ULong getCdtPriId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.cdt_sc_awd_pri.is_default</code>. Indicating
     * whether the primitive is the default primitive of the supplementary
     * component.
     */
    public void setIsDefault(Byte value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.cdt_sc_awd_pri.is_default</code>. Indicating
     * whether the primitive is the default primitive of the supplementary
     * component.
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
        return CdtScAwdPri.CDT_SC_AWD_PRI.CDT_SC_AWD_PRI_ID;
    }

    @Override
    public Field<ULong> field2() {
        return CdtScAwdPri.CDT_SC_AWD_PRI.CDT_SC_ID;
    }

    @Override
    public Field<ULong> field3() {
        return CdtScAwdPri.CDT_SC_AWD_PRI.CDT_PRI_ID;
    }

    @Override
    public Field<Byte> field4() {
        return CdtScAwdPri.CDT_SC_AWD_PRI.IS_DEFAULT;
    }

    @Override
    public ULong component1() {
        return getCdtScAwdPriId();
    }

    @Override
    public ULong component2() {
        return getCdtScId();
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
        return getCdtScAwdPriId();
    }

    @Override
    public ULong value2() {
        return getCdtScId();
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
    public CdtScAwdPriRecord value1(ULong value) {
        setCdtScAwdPriId(value);
        return this;
    }

    @Override
    public CdtScAwdPriRecord value2(ULong value) {
        setCdtScId(value);
        return this;
    }

    @Override
    public CdtScAwdPriRecord value3(ULong value) {
        setCdtPriId(value);
        return this;
    }

    @Override
    public CdtScAwdPriRecord value4(Byte value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public CdtScAwdPriRecord values(ULong value1, ULong value2, ULong value3, Byte value4) {
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
     * Create a detached CdtScAwdPriRecord
     */
    public CdtScAwdPriRecord() {
        super(CdtScAwdPri.CDT_SC_AWD_PRI);
    }

    /**
     * Create a detached, initialised CdtScAwdPriRecord
     */
    public CdtScAwdPriRecord(ULong cdtScAwdPriId, ULong cdtScId, ULong cdtPriId, Byte isDefault) {
        super(CdtScAwdPri.CDT_SC_AWD_PRI);

        setCdtScAwdPriId(cdtScAwdPriId);
        setCdtScId(cdtScId);
        setCdtPriId(cdtPriId);
        setIsDefault(isDefault);
    }
}
