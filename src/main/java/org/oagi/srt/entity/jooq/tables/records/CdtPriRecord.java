/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CdtPri;


/**
 * This table stores the CDT primitives.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class CdtPriRecord extends UpdatableRecordImpl<CdtPriRecord> implements Record2<ULong, String> {

    private static final long serialVersionUID = 1111891475;

    /**
     * Setter for <code>oagi.cdt_pri.cdt_pri_id</code>. Internal, primary database key.
     */
    public void setCdtPriId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.cdt_pri.cdt_pri_id</code>. Internal, primary database key.
     */
    public ULong getCdtPriId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.cdt_pri.name</code>. Name of the CDT primitive per the CCTS datatype catalog, e.g., Decimal.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.cdt_pri.name</code>. Name of the CDT primitive per the CCTS datatype catalog, e.g., Decimal.
     */
    public String getName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<ULong, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CdtPri.CDT_PRI.CDT_PRI_ID;
    }

    @Override
    public Field<String> field2() {
        return CdtPri.CDT_PRI.NAME;
    }

    @Override
    public ULong component1() {
        return getCdtPriId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public ULong value1() {
        return getCdtPriId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public CdtPriRecord value1(ULong value) {
        setCdtPriId(value);
        return this;
    }

    @Override
    public CdtPriRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public CdtPriRecord values(ULong value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CdtPriRecord
     */
    public CdtPriRecord() {
        super(CdtPri.CDT_PRI);
    }

    /**
     * Create a detached, initialised CdtPriRecord
     */
    public CdtPriRecord(ULong cdtPriId, String name) {
        super(CdtPri.CDT_PRI);

        set(0, cdtPriId);
        set(1, name);
    }
}
