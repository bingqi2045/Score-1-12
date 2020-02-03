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
import org.oagi.srt.entity.jooq.tables.UsageRuleExpression;


/**
 * The USAGE_RULE_EXPRESSION provides a representation of a usage rule in 
 * a particular syntax indicated by the CONSTRAINT_TYPE column. One of the 
 * syntaxes can be unstructured, which works a description of the usage rule.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsageRuleExpressionRecord extends UpdatableRecordImpl<UsageRuleExpressionRecord> implements Record4<ULong, Integer, String, ULong> {

    private static final long serialVersionUID = 1624172139;

    /**
     * Setter for <code>oagi.usage_rule_expression.usage_rule_expression_id</code>. Primary key of the usage rule expression
     */
    public void setUsageRuleExpressionId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.usage_rule_expression.usage_rule_expression_id</code>. Primary key of the usage rule expression
     */
    public ULong getUsageRuleExpressionId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.usage_rule_expression.constraint_type</code>. Constraint type according to the CC spec. It represents the expression language (syntax) used in the CONSTRAINT column. It is a value list column. 0 = 'Unstructured' which is basically a description of the rule, 1 = 'Schematron'.
     */
    public void setConstraintType(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.usage_rule_expression.constraint_type</code>. Constraint type according to the CC spec. It represents the expression language (syntax) used in the CONSTRAINT column. It is a value list column. 0 = 'Unstructured' which is basically a description of the rule, 1 = 'Schematron'.
     */
    public Integer getConstraintType() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>oagi.usage_rule_expression.constraint_text</code>. This column capture the constraint expressing the usage rule. In other words, this is the expression.
     */
    public void setConstraintText(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.usage_rule_expression.constraint_text</code>. This column capture the constraint expressing the usage rule. In other words, this is the expression.
     */
    public String getConstraintText() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.usage_rule_expression.represented_usage_rule_id</code>. The usage rule which the expression represents
     */
    public void setRepresentedUsageRuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.usage_rule_expression.represented_usage_rule_id</code>. The usage rule which the expression represents
     */
    public ULong getRepresentedUsageRuleId() {
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
    public Row4<ULong, Integer, String, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, Integer, String, ULong> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return UsageRuleExpression.USAGE_RULE_EXPRESSION.USAGE_RULE_EXPRESSION_ID;
    }

    @Override
    public Field<Integer> field2() {
        return UsageRuleExpression.USAGE_RULE_EXPRESSION.CONSTRAINT_TYPE;
    }

    @Override
    public Field<String> field3() {
        return UsageRuleExpression.USAGE_RULE_EXPRESSION.CONSTRAINT_TEXT;
    }

    @Override
    public Field<ULong> field4() {
        return UsageRuleExpression.USAGE_RULE_EXPRESSION.REPRESENTED_USAGE_RULE_ID;
    }

    @Override
    public ULong component1() {
        return getUsageRuleExpressionId();
    }

    @Override
    public Integer component2() {
        return getConstraintType();
    }

    @Override
    public String component3() {
        return getConstraintText();
    }

    @Override
    public ULong component4() {
        return getRepresentedUsageRuleId();
    }

    @Override
    public ULong value1() {
        return getUsageRuleExpressionId();
    }

    @Override
    public Integer value2() {
        return getConstraintType();
    }

    @Override
    public String value3() {
        return getConstraintText();
    }

    @Override
    public ULong value4() {
        return getRepresentedUsageRuleId();
    }

    @Override
    public UsageRuleExpressionRecord value1(ULong value) {
        setUsageRuleExpressionId(value);
        return this;
    }

    @Override
    public UsageRuleExpressionRecord value2(Integer value) {
        setConstraintType(value);
        return this;
    }

    @Override
    public UsageRuleExpressionRecord value3(String value) {
        setConstraintText(value);
        return this;
    }

    @Override
    public UsageRuleExpressionRecord value4(ULong value) {
        setRepresentedUsageRuleId(value);
        return this;
    }

    @Override
    public UsageRuleExpressionRecord values(ULong value1, Integer value2, String value3, ULong value4) {
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
     * Create a detached UsageRuleExpressionRecord
     */
    public UsageRuleExpressionRecord() {
        super(UsageRuleExpression.USAGE_RULE_EXPRESSION);
    }

    /**
     * Create a detached, initialised UsageRuleExpressionRecord
     */
    public UsageRuleExpressionRecord(ULong usageRuleExpressionId, Integer constraintType, String constraintText, ULong representedUsageRuleId) {
        super(UsageRuleExpression.USAGE_RULE_EXPRESSION);

        set(0, usageRuleExpressionId);
        set(1, constraintType);
        set(2, constraintText);
        set(3, representedUsageRuleId);
    }
}
