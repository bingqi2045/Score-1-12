/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.Keys;
import org.oagi.score.entity.jooq.Oagi;
import org.oagi.score.entity.jooq.tables.records.UsageRuleExpressionRecord;


/**
 * The USAGE_RULE_EXPRESSION provides a representation of a usage rule in 
 * a particular syntax indicated by the CONSTRAINT_TYPE column. One of the 
 * syntaxes can be unstructured, which works a description of the usage rule.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsageRuleExpression extends TableImpl<UsageRuleExpressionRecord> {

    private static final long serialVersionUID = -630847907;

    /**
     * The reference instance of <code>oagi.usage_rule_expression</code>
     */
    public static final UsageRuleExpression USAGE_RULE_EXPRESSION = new UsageRuleExpression();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UsageRuleExpressionRecord> getRecordType() {
        return UsageRuleExpressionRecord.class;
    }

    /**
     * The column <code>oagi.usage_rule_expression.usage_rule_expression_id</code>. Primary key of the usage rule expression
     */
    public final TableField<UsageRuleExpressionRecord, ULong> USAGE_RULE_EXPRESSION_ID = createField(DSL.name("usage_rule_expression_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key of the usage rule expression");

    /**
     * The column <code>oagi.usage_rule_expression.constraint_type</code>. Constraint type according to the CC spec. It represents the expression language (syntax) used in the CONSTRAINT column. It is a value list column. 0 = 'Unstructured' which is basically a description of the rule, 1 = 'Schematron'.
     */
    public final TableField<UsageRuleExpressionRecord, Integer> CONSTRAINT_TYPE = createField(DSL.name("constraint_type"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Constraint type according to the CC spec. It represents the expression language (syntax) used in the CONSTRAINT column. It is a value list column. 0 = 'Unstructured' which is basically a description of the rule, 1 = 'Schematron'.");

    /**
     * The column <code>oagi.usage_rule_expression.constraint_text</code>. This column capture the constraint expressing the usage rule. In other words, this is the expression.
     */
    public final TableField<UsageRuleExpressionRecord, String> CONSTRAINT_TEXT = createField(DSL.name("constraint_text"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "This column capture the constraint expressing the usage rule. In other words, this is the expression.");

    /**
     * The column <code>oagi.usage_rule_expression.represented_usage_rule_id</code>. The usage rule which the expression represents
     */
    public final TableField<UsageRuleExpressionRecord, ULong> REPRESENTED_USAGE_RULE_ID = createField(DSL.name("represented_usage_rule_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "The usage rule which the expression represents");

    /**
     * Create a <code>oagi.usage_rule_expression</code> table reference
     */
    public UsageRuleExpression() {
        this(DSL.name("usage_rule_expression"), null);
    }

    /**
     * Create an aliased <code>oagi.usage_rule_expression</code> table reference
     */
    public UsageRuleExpression(String alias) {
        this(DSL.name(alias), USAGE_RULE_EXPRESSION);
    }

    /**
     * Create an aliased <code>oagi.usage_rule_expression</code> table reference
     */
    public UsageRuleExpression(Name alias) {
        this(alias, USAGE_RULE_EXPRESSION);
    }

    private UsageRuleExpression(Name alias, Table<UsageRuleExpressionRecord> aliased) {
        this(alias, aliased, null);
    }

    private UsageRuleExpression(Name alias, Table<UsageRuleExpressionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The USAGE_RULE_EXPRESSION provides a representation of a usage rule in a particular syntax indicated by the CONSTRAINT_TYPE column. One of the syntaxes can be unstructured, which works a description of the usage rule."), TableOptions.table());
    }

    public <O extends Record> UsageRuleExpression(Table<O> child, ForeignKey<O, UsageRuleExpressionRecord> key) {
        super(child, key, USAGE_RULE_EXPRESSION);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<UsageRuleExpressionRecord, ULong> getIdentity() {
        return Keys.IDENTITY_USAGE_RULE_EXPRESSION;
    }

    @Override
    public UniqueKey<UsageRuleExpressionRecord> getPrimaryKey() {
        return Keys.KEY_USAGE_RULE_EXPRESSION_PRIMARY;
    }

    @Override
    public List<UniqueKey<UsageRuleExpressionRecord>> getKeys() {
        return Arrays.<UniqueKey<UsageRuleExpressionRecord>>asList(Keys.KEY_USAGE_RULE_EXPRESSION_PRIMARY);
    }

    @Override
    public List<ForeignKey<UsageRuleExpressionRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<UsageRuleExpressionRecord, ?>>asList(Keys.USAGE_RULE_EXPRESSION_REPRESENTED_USAGE_RULE_ID_FK);
    }

    public UsageRule usageRule() {
        return new UsageRule(this, Keys.USAGE_RULE_EXPRESSION_REPRESENTED_USAGE_RULE_ID_FK);
    }

    @Override
    public UsageRuleExpression as(String alias) {
        return new UsageRuleExpression(DSL.name(alias), this);
    }

    @Override
    public UsageRuleExpression as(Name alias) {
        return new UsageRuleExpression(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UsageRuleExpression rename(String name) {
        return new UsageRuleExpression(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UsageRuleExpression rename(Name name) {
        return new UsageRuleExpression(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, Integer, String, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
