/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.DtUsageRule;


/**
 * This is an intersection table. Per CCTS, a usage rule may be reused. This 
 * table allows m-m relationships between the usage rule and the DT content 
 * component and usage rules and DT supplementary component. In a particular 
 * record, either a TARGET_DT_ID or TARGET_DT_SC_ID must be present but not 
 * both.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtUsageRuleRecord extends UpdatableRecordImpl<DtUsageRuleRecord> implements Record4<ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = -1527512481;

    /**
     * Setter for <code>oagi.dt_usage_rule.dt_usage_rule_id</code>. Primary key of the table.
     */
    public void setDtUsageRuleId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.dt_usage_rule_id</code>. Primary key of the table.
     */
    public ULong getDtUsageRuleId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.dt_usage_rule.assigned_usage_rule_id</code>. Foreign key to the USAGE_RULE table indicating the usage rule assigned to the DT content component or DT_SC.
     */
    public void setAssignedUsageRuleId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.assigned_usage_rule_id</code>. Foreign key to the USAGE_RULE table indicating the usage rule assigned to the DT content component or DT_SC.
     */
    public ULong getAssignedUsageRuleId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.dt_usage_rule.target_dt_id</code>. Foreing key to the DT_ID for assigning a usage rule to the corresponding DT content component.
     */
    public void setTargetDtId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.target_dt_id</code>. Foreing key to the DT_ID for assigning a usage rule to the corresponding DT content component.
     */
    public ULong getTargetDtId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.dt_usage_rule.target_dt_sc_id</code>. Foreing key to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.
     */
    public void setTargetDtScId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.target_dt_sc_id</code>. Foreing key to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.
     */
    public ULong getTargetDtScId() {
        return (ULong) get(3);
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
    public Row4<ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, ULong, ULong, ULong> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return DtUsageRule.DT_USAGE_RULE.DT_USAGE_RULE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return DtUsageRule.DT_USAGE_RULE.ASSIGNED_USAGE_RULE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return DtUsageRule.DT_USAGE_RULE.TARGET_DT_ID;
    }

    @Override
    public Field<ULong> field4() {
        return DtUsageRule.DT_USAGE_RULE.TARGET_DT_SC_ID;
    }

    @Override
    public ULong component1() {
        return getDtUsageRuleId();
    }

    @Override
    public ULong component2() {
        return getAssignedUsageRuleId();
    }

    @Override
    public ULong component3() {
        return getTargetDtId();
    }

    @Override
    public ULong component4() {
        return getTargetDtScId();
    }

    @Override
    public ULong value1() {
        return getDtUsageRuleId();
    }

    @Override
    public ULong value2() {
        return getAssignedUsageRuleId();
    }

    @Override
    public ULong value3() {
        return getTargetDtId();
    }

    @Override
    public ULong value4() {
        return getTargetDtScId();
    }

    @Override
    public DtUsageRuleRecord value1(ULong value) {
        setDtUsageRuleId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord value2(ULong value) {
        setAssignedUsageRuleId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord value3(ULong value) {
        setTargetDtId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord value4(ULong value) {
        setTargetDtScId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord values(ULong value1, ULong value2, ULong value3, ULong value4) {
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
     * Create a detached DtUsageRuleRecord
     */
    public DtUsageRuleRecord() {
        super(DtUsageRule.DT_USAGE_RULE);
    }

    /**
     * Create a detached, initialised DtUsageRuleRecord
     */
    public DtUsageRuleRecord(ULong dtUsageRuleId, ULong assignedUsageRuleId, ULong targetDtId, ULong targetDtScId) {
        super(DtUsageRule.DT_USAGE_RULE);

        set(0, dtUsageRuleId);
        set(1, assignedUsageRuleId);
        set(2, targetDtId);
        set(3, targetDtScId);
    }
}
