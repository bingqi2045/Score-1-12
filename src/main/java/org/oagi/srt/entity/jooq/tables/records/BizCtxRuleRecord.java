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
import org.oagi.srt.entity.jooq.tables.BizCtxRule;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BizCtxRuleRecord extends UpdatableRecordImpl<BizCtxRuleRecord> implements Record3<ULong, ULong, ULong> {

    private static final long serialVersionUID = -1522778173;

    /**
     * Setter for <code>oagi.biz_ctx_rule.biz_ctx_rule_id</code>.
     */
    public void setBizCtxRuleId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_rule.biz_ctx_rule_id</code>.
     */
    public ULong getBizCtxRuleId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.biz_ctx_rule.from_biz_ctx_id</code>.
     */
    public void setFromBizCtxId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_rule.from_biz_ctx_id</code>.
     */
    public ULong getFromBizCtxId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.biz_ctx_rule.top_level_bie_id</code>.
     */
    public void setTopLevelBieId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_rule.top_level_bie_id</code>.
     */
    public ULong getTopLevelBieId() {
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
        return BizCtxRule.BIZ_CTX_RULE.BIZ_CTX_RULE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field2() {
        return BizCtxRule.BIZ_CTX_RULE.FROM_BIZ_CTX_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field3() {
        return BizCtxRule.BIZ_CTX_RULE.TOP_LEVEL_BIE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getBizCtxRuleId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component2() {
        return getFromBizCtxId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component3() {
        return getTopLevelBieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getBizCtxRuleId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value2() {
        return getFromBizCtxId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value3() {
        return getTopLevelBieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxRuleRecord value1(ULong value) {
        setBizCtxRuleId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxRuleRecord value2(ULong value) {
        setFromBizCtxId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxRuleRecord value3(ULong value) {
        setTopLevelBieId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxRuleRecord values(ULong value1, ULong value2, ULong value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BizCtxRuleRecord
     */
    public BizCtxRuleRecord() {
        super(BizCtxRule.BIZ_CTX_RULE);
    }

    /**
     * Create a detached, initialised BizCtxRuleRecord
     */
    public BizCtxRuleRecord(ULong bizCtxRuleId, ULong fromBizCtxId, ULong topLevelBieId) {
        super(BizCtxRule.BIZ_CTX_RULE);

        set(0, bizCtxRuleId);
        set(1, fromBizCtxId);
        set(2, topLevelBieId);
    }
}
