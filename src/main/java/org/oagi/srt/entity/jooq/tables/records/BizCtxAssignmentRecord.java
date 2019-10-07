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
import org.oagi.srt.entity.jooq.tables.BizCtxAssignment;


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
public class BizCtxAssignmentRecord extends UpdatableRecordImpl<BizCtxAssignmentRecord> implements Record3<ULong, ULong, ULong> {

    private static final long serialVersionUID = -1153624481;

    /**
     * Setter for <code>oagi.biz_ctx_assignment.biz_ctx_assignment_id</code>.
     */
    public void setBizCtxAssignmentId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_assignment.biz_ctx_assignment_id</code>.
     */
    public ULong getBizCtxAssignmentId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.biz_ctx_assignment.biz_ctx_id</code>.
     */
    public void setBizCtxId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_assignment.biz_ctx_id</code>.
     */
    public ULong getBizCtxId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.biz_ctx_assignment.top_level_abie_id</code>.
     */
    public void setTopLevelAbieId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx_assignment.top_level_abie_id</code>.
     */
    public ULong getTopLevelAbieId() {
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
        return BizCtxAssignment.BIZ_CTX_ASSIGNMENT.BIZ_CTX_ASSIGNMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field2() {
        return BizCtxAssignment.BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field3() {
        return BizCtxAssignment.BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getBizCtxAssignmentId();
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
        return getTopLevelAbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getBizCtxAssignmentId();
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
        return getTopLevelAbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxAssignmentRecord value1(ULong value) {
        setBizCtxAssignmentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxAssignmentRecord value2(ULong value) {
        setBizCtxId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxAssignmentRecord value3(ULong value) {
        setTopLevelAbieId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BizCtxAssignmentRecord values(ULong value1, ULong value2, ULong value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BizCtxAssignmentRecord
     */
    public BizCtxAssignmentRecord() {
        super(BizCtxAssignment.BIZ_CTX_ASSIGNMENT);
    }

    /**
     * Create a detached, initialised BizCtxAssignmentRecord
     */
    public BizCtxAssignmentRecord(ULong bizCtxAssignmentId, ULong bizCtxId, ULong topLevelAbieId) {
        super(BizCtxAssignment.BIZ_CTX_ASSIGNMENT);

        set(0, bizCtxAssignmentId);
        set(1, bizCtxId);
        set(2, topLevelAbieId);
    }
}
