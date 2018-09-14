/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CtxSchemeValue;


/**
 * This table stores the context scheme values for a particular context scheme 
 * in the CTX_SCHEME table.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxSchemeValueRecord extends UpdatableRecordImpl<CtxSchemeValueRecord> implements Record5<ULong, String, String, String, ULong> {

    private static final long serialVersionUID = -1329256375;

    /**
     * Setter for <code>oagi.ctx_scheme_value.ctx_scheme_value_id</code>. Primary, internal database key.
     */
    public void setCtxSchemeValueId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme_value.ctx_scheme_value_id</code>. Primary, internal database key.
     */
    public ULong getCtxSchemeValueId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.ctx_scheme_value.guid</code>. GUID of the context scheme value. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme_value.guid</code>. GUID of the context scheme value. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ctx_scheme_value.value</code>. A short value for the scheme value similar to the code list value.
     */
    public void setValue(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme_value.value</code>. A short value for the scheme value similar to the code list value.
     */
    public String getValue() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.ctx_scheme_value.meaning</code>. The description, explanatiion of the scheme value.
     */
    public void setMeaning(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme_value.meaning</code>. The description, explanatiion of the scheme value.
     */
    public String getMeaning() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.ctx_scheme_value.owner_ctx_scheme_id</code>. Foreign key to the CTX_SCHEME table. It identifies the context scheme, to which this scheme value belongs.
     */
    public void setOwnerCtxSchemeId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme_value.owner_ctx_scheme_id</code>. Foreign key to the CTX_SCHEME table. It identifies the context scheme, to which this scheme value belongs.
     */
    public ULong getOwnerCtxSchemeId() {
        return (ULong) get(4);
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
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<ULong, String, String, String, ULong> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<ULong, String, String, String, ULong> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field1() {
        return CtxSchemeValue.CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return CtxSchemeValue.CTX_SCHEME_VALUE.GUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return CtxSchemeValue.CTX_SCHEME_VALUE.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return CtxSchemeValue.CTX_SCHEME_VALUE.MEANING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field5() {
        return CtxSchemeValue.CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getCtxSchemeValueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getGuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getMeaning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component5() {
        return getOwnerCtxSchemeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getCtxSchemeValueId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getMeaning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value5() {
        return getOwnerCtxSchemeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeValueRecord value1(ULong value) {
        setCtxSchemeValueId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeValueRecord value2(String value) {
        setGuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeValueRecord value3(String value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeValueRecord value4(String value) {
        setMeaning(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeValueRecord value5(ULong value) {
        setOwnerCtxSchemeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeValueRecord values(ULong value1, String value2, String value3, String value4, ULong value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CtxSchemeValueRecord
     */
    public CtxSchemeValueRecord() {
        super(CtxSchemeValue.CTX_SCHEME_VALUE);
    }

    /**
     * Create a detached, initialised CtxSchemeValueRecord
     */
    public CtxSchemeValueRecord(ULong ctxSchemeValueId, String guid, String value, String meaning, ULong ownerCtxSchemeId) {
        super(CtxSchemeValue.CTX_SCHEME_VALUE);

        set(0, ctxSchemeValueId);
        set(1, guid);
        set(2, value);
        set(3, meaning);
        set(4, ownerCtxSchemeId);
    }
}
