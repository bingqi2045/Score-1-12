/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.BizCtxValue;


/**
 * This table represents business context values for business contexts. It 
 * provides the associations between a business context and a context scheme 
 * value.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BizCtxValueRecord extends UpdatableRecordImpl<BizCtxValueRecord> implements Record3<ULong, ULong, ULong> {

    private static final long serialVersionUID = 609590318;

    /**
     * Setter for <code>oagi.biz_ctx_value.biz_ctx_value_id</code>. Primary, internal database key.
     */
    public void setBizCtxValueId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_value.biz_ctx_value_id</code>. Primary, internal database key.
     */
    public ULong getBizCtxValueId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.biz_ctx_value.biz_ctx_id</code>. Foreign key to the biz_ctx table.
     */
    public void setBizCtxId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_value.biz_ctx_id</code>. Foreign key to the biz_ctx table.
     */
    public ULong getBizCtxId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.biz_ctx_value.ctx_scheme_value_id</code>. Foreign key to the CTX_SCHEME_VALUE table.
     */
    public void setCtxSchemeValueId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_value.ctx_scheme_value_id</code>. Foreign key to the CTX_SCHEME_VALUE table.
     */
    public ULong getCtxSchemeValueId() {
        return (ULong) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<ULong, ULong, ULong> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field1() {
        return BizCtxValue.BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field2() {
        return BizCtxValue.BIZ_CTX_VALUE.BIZ_CTX_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field3() {
        return BizCtxValue.BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getBizCtxValueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component2() {
        return getBizCtxId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component3() {
        return getCtxSchemeValueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getBizCtxValueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value2() {
        return getBizCtxId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value3() {
        return getCtxSchemeValueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxValueRecord value1(ULong value) {
        setBizCtxValueId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxValueRecord value2(ULong value) {
        setBizCtxId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxValueRecord value3(ULong value) {
        setCtxSchemeValueId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxValueRecord values(ULong value1, ULong value2, ULong value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BizCtxValueRecord
     */
    public BizCtxValueRecord() {
        super(BizCtxValue.BIZ_CTX_VALUE);
    }

    /**
     * Create a detached, initialised BizCtxValueRecord
     */
    public BizCtxValueRecord(ULong bizCtxValueId, ULong bizCtxId, ULong ctxSchemeValueId) {
        super(BizCtxValue.BIZ_CTX_VALUE);

        set(0, bizCtxValueId);
        set(1, bizCtxId);
        set(2, ctxSchemeValueId);
    }
}
