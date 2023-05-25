/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BieUsageRule;


/**
 * This is an intersection table. Per CCTS, a usage rule may be reused. This
 * table allows m-m relationships between the usage rule and all kinds of BIEs.
 * In a particular record, either only one of the TARGET_ABIE_ID,
 * TARGET_ASBIE_ID, TARGET_ASBIEP_ID, TARGET_BBIE_ID, or TARGET_BBIEP_ID.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BieUsageRuleRecord extends UpdatableRecordImpl<BieUsageRuleRecord> implements Record7<ULong, ULong, ULong, ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bie_usage_rule.bie_usage_rule_id</code>. Primary
     * key of the table.
     */
    public void setBieUsageRuleId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.bie_usage_rule_id</code>. Primary
     * key of the table.
     */
    public ULong getBieUsageRuleId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bie_usage_rule.assigned_usage_rule_id</code>.
     * Foreign key to the USAGE_RULE table indicating the usage rule assigned to
     * a BIE.
     */
    public void setAssignedUsageRuleId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.assigned_usage_rule_id</code>.
     * Foreign key to the USAGE_RULE table indicating the usage rule assigned to
     * a BIE.
     */
    public ULong getAssignedUsageRuleId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.bie_usage_rule.target_abie_id</code>. Foreign key
     * to the ABIE table indicating the ABIE, to which the usage rule is
     * applied.
     */
    public void setTargetAbieId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.target_abie_id</code>. Foreign key
     * to the ABIE table indicating the ABIE, to which the usage rule is
     * applied.
     */
    public ULong getTargetAbieId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bie_usage_rule.target_asbie_id</code>. Foreign key
     * to the ASBIE table indicating the ASBIE, to which the usage rule is
     * applied.
     */
    public void setTargetAsbieId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.target_asbie_id</code>. Foreign key
     * to the ASBIE table indicating the ASBIE, to which the usage rule is
     * applied.
     */
    public ULong getTargetAsbieId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.bie_usage_rule.target_asbiep_id</code>. Foreign key
     * to the ASBIEP table indicating the ASBIEP, to which the usage rule is
     * applied.
     */
    public void setTargetAsbiepId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.target_asbiep_id</code>. Foreign key
     * to the ASBIEP table indicating the ASBIEP, to which the usage rule is
     * applied.
     */
    public ULong getTargetAsbiepId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bie_usage_rule.target_bbie_id</code>. Foreign key
     * to the BBIE table indicating the BBIE, to which the usage rule is
     * applied.
     */
    public void setTargetBbieId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.target_bbie_id</code>. Foreign key
     * to the BBIE table indicating the BBIE, to which the usage rule is
     * applied.
     */
    public ULong getTargetBbieId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.bie_usage_rule.target_bbiep_id</code>. Foreign key
     * to the BBIEP table indicating the ABIEP, to which the usage rule is
     * applied.
     */
    public void setTargetBbiepId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bie_usage_rule.target_bbiep_id</code>. Foreign key
     * to the BBIEP table indicating the ABIEP, to which the usage rule is
     * applied.
     */
    public ULong getTargetBbiepId() {
        return (ULong) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, ULong, ULong, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<ULong, ULong, ULong, ULong, ULong, ULong, ULong> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BieUsageRule.BIE_USAGE_RULE.BIE_USAGE_RULE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return BieUsageRule.BIE_USAGE_RULE.ASSIGNED_USAGE_RULE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return BieUsageRule.BIE_USAGE_RULE.TARGET_ABIE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return BieUsageRule.BIE_USAGE_RULE.TARGET_ASBIE_ID;
    }

    @Override
    public Field<ULong> field5() {
        return BieUsageRule.BIE_USAGE_RULE.TARGET_ASBIEP_ID;
    }

    @Override
    public Field<ULong> field6() {
        return BieUsageRule.BIE_USAGE_RULE.TARGET_BBIE_ID;
    }

    @Override
    public Field<ULong> field7() {
        return BieUsageRule.BIE_USAGE_RULE.TARGET_BBIEP_ID;
    }

    @Override
    public ULong component1() {
        return getBieUsageRuleId();
    }

    @Override
    public ULong component2() {
        return getAssignedUsageRuleId();
    }

    @Override
    public ULong component3() {
        return getTargetAbieId();
    }

    @Override
    public ULong component4() {
        return getTargetAsbieId();
    }

    @Override
    public ULong component5() {
        return getTargetAsbiepId();
    }

    @Override
    public ULong component6() {
        return getTargetBbieId();
    }

    @Override
    public ULong component7() {
        return getTargetBbiepId();
    }

    @Override
    public ULong value1() {
        return getBieUsageRuleId();
    }

    @Override
    public ULong value2() {
        return getAssignedUsageRuleId();
    }

    @Override
    public ULong value3() {
        return getTargetAbieId();
    }

    @Override
    public ULong value4() {
        return getTargetAsbieId();
    }

    @Override
    public ULong value5() {
        return getTargetAsbiepId();
    }

    @Override
    public ULong value6() {
        return getTargetBbieId();
    }

    @Override
    public ULong value7() {
        return getTargetBbiepId();
    }

    @Override
    public BieUsageRuleRecord value1(ULong value) {
        setBieUsageRuleId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord value2(ULong value) {
        setAssignedUsageRuleId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord value3(ULong value) {
        setTargetAbieId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord value4(ULong value) {
        setTargetAsbieId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord value5(ULong value) {
        setTargetAsbiepId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord value6(ULong value) {
        setTargetBbieId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord value7(ULong value) {
        setTargetBbiepId(value);
        return this;
    }

    @Override
    public BieUsageRuleRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, ULong value6, ULong value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BieUsageRuleRecord
     */
    public BieUsageRuleRecord() {
        super(BieUsageRule.BIE_USAGE_RULE);
    }

    /**
     * Create a detached, initialised BieUsageRuleRecord
     */
    public BieUsageRuleRecord(ULong bieUsageRuleId, ULong assignedUsageRuleId, ULong targetAbieId, ULong targetAsbieId, ULong targetAsbiepId, ULong targetBbieId, ULong targetBbiepId) {
        super(BieUsageRule.BIE_USAGE_RULE);

        setBieUsageRuleId(bieUsageRuleId);
        setAssignedUsageRuleId(assignedUsageRuleId);
        setTargetAbieId(targetAbieId);
        setTargetAsbieId(targetAsbieId);
        setTargetAsbiepId(targetAsbiepId);
        setTargetBbieId(targetBbieId);
        setTargetBbiepId(targetBbiepId);
        resetChangedOnNotNull();
    }
}
