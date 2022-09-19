/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtUsageRule;


/**
 * This is an intersection table. Per CCTS, a usage rule may be reused. This
 * table allows m-m relationships between the usage rule and the DT content
 * component and usage rules and DT supplementary component. In a particular
 * record, either a TARGET_DT_ID or TARGET_DT_SC_ID must be present but not
 * both.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtUsageRuleRecord extends UpdatableRecordImpl<DtUsageRuleRecord> implements Record4<String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.dt_usage_rule.dt_usage_rule_id</code>. Primary,
     * internal database key.
     */
    public void setDtUsageRuleId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.dt_usage_rule_id</code>. Primary,
     * internal database key.
     */
    public String getDtUsageRuleId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.dt_usage_rule.assigned_usage_rule_id</code>.
     * Foreign key to the USAGE_RULE table indicating the usage rule assigned to
     * a BIE.
     */
    public void setAssignedUsageRuleId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.assigned_usage_rule_id</code>.
     * Foreign key to the USAGE_RULE table indicating the usage rule assigned to
     * a BIE.
     */
    public String getAssignedUsageRuleId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.dt_usage_rule.target_dt_id</code>. Foreign key to
     * the DT_ID for assigning a usage rule to the corresponding DT content
     * component.
     */
    public void setTargetDtId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.target_dt_id</code>. Foreign key to
     * the DT_ID for assigning a usage rule to the corresponding DT content
     * component.
     */
    public String getTargetDtId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.dt_usage_rule.target_dt_sc_id</code>. Foreign key
     * to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.
     */
    public void setTargetDtScId(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.dt_usage_rule.target_dt_sc_id</code>. Foreign key
     * to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.
     */
    public String getTargetDtScId() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<String, String, String, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return DtUsageRule.DT_USAGE_RULE.DT_USAGE_RULE_ID;
    }

    @Override
    public Field<String> field2() {
        return DtUsageRule.DT_USAGE_RULE.ASSIGNED_USAGE_RULE_ID;
    }

    @Override
    public Field<String> field3() {
        return DtUsageRule.DT_USAGE_RULE.TARGET_DT_ID;
    }

    @Override
    public Field<String> field4() {
        return DtUsageRule.DT_USAGE_RULE.TARGET_DT_SC_ID;
    }

    @Override
    public String component1() {
        return getDtUsageRuleId();
    }

    @Override
    public String component2() {
        return getAssignedUsageRuleId();
    }

    @Override
    public String component3() {
        return getTargetDtId();
    }

    @Override
    public String component4() {
        return getTargetDtScId();
    }

    @Override
    public String value1() {
        return getDtUsageRuleId();
    }

    @Override
    public String value2() {
        return getAssignedUsageRuleId();
    }

    @Override
    public String value3() {
        return getTargetDtId();
    }

    @Override
    public String value4() {
        return getTargetDtScId();
    }

    @Override
    public DtUsageRuleRecord value1(String value) {
        setDtUsageRuleId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord value2(String value) {
        setAssignedUsageRuleId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord value3(String value) {
        setTargetDtId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord value4(String value) {
        setTargetDtScId(value);
        return this;
    }

    @Override
    public DtUsageRuleRecord values(String value1, String value2, String value3, String value4) {
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
    public DtUsageRuleRecord(String dtUsageRuleId, String assignedUsageRuleId, String targetDtId, String targetDtScId) {
        super(DtUsageRule.DT_USAGE_RULE);

        setDtUsageRuleId(dtUsageRuleId);
        setAssignedUsageRuleId(assignedUsageRuleId);
        setTargetDtId(targetDtId);
        setTargetDtScId(targetDtScId);
    }
}
