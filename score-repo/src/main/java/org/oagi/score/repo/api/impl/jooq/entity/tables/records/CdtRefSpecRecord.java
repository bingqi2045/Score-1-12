/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CdtRefSpec;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtRefSpecRecord extends UpdatableRecordImpl<CdtRefSpecRecord> implements Record3<String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.cdt_ref_spec.cdt_ref_spec_id</code>. Primary,
     * internal database key.
     */
    public void setCdtRefSpecId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.cdt_ref_spec.cdt_ref_spec_id</code>. Primary,
     * internal database key.
     */
    public String getCdtRefSpecId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.cdt_ref_spec.cdt_id</code>.
     */
    public void setCdtId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.cdt_ref_spec.cdt_id</code>.
     */
    public String getCdtId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.cdt_ref_spec.ref_spec_id</code>.
     */
    public void setRefSpecId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.cdt_ref_spec.ref_spec_id</code>.
     */
    public String getRefSpecId() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return CdtRefSpec.CDT_REF_SPEC.CDT_REF_SPEC_ID;
    }

    @Override
    public Field<String> field2() {
        return CdtRefSpec.CDT_REF_SPEC.CDT_ID;
    }

    @Override
    public Field<String> field3() {
        return CdtRefSpec.CDT_REF_SPEC.REF_SPEC_ID;
    }

    @Override
    public String component1() {
        return getCdtRefSpecId();
    }

    @Override
    public String component2() {
        return getCdtId();
    }

    @Override
    public String component3() {
        return getRefSpecId();
    }

    @Override
    public String value1() {
        return getCdtRefSpecId();
    }

    @Override
    public String value2() {
        return getCdtId();
    }

    @Override
    public String value3() {
        return getRefSpecId();
    }

    @Override
    public CdtRefSpecRecord value1(String value) {
        setCdtRefSpecId(value);
        return this;
    }

    @Override
    public CdtRefSpecRecord value2(String value) {
        setCdtId(value);
        return this;
    }

    @Override
    public CdtRefSpecRecord value3(String value) {
        setRefSpecId(value);
        return this;
    }

    @Override
    public CdtRefSpecRecord values(String value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CdtRefSpecRecord
     */
    public CdtRefSpecRecord() {
        super(CdtRefSpec.CDT_REF_SPEC);
    }

    /**
     * Create a detached, initialised CdtRefSpecRecord
     */
    public CdtRefSpecRecord(String cdtRefSpecId, String cdtId, String refSpecId) {
        super(CdtRefSpec.CDT_REF_SPEC);

        setCdtRefSpecId(cdtRefSpecId);
        setCdtId(cdtId);
        setRefSpecId(refSpecId);
    }
}
