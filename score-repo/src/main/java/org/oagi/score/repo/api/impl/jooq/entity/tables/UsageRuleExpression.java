/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.UsageRuleExpressionRecord;


/**
 * The USAGE_RULE_EXPRESSION provides a representation of a usage rule in a
 * particular syntax indicated by the CONSTRAINT_TYPE column. One of the
 * syntaxes can be unstructured, which works a description of the usage rule.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsageRuleExpression extends TableImpl<UsageRuleExpressionRecord> {

    private static final long serialVersionUID = 1L;

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
     * The column
     * <code>oagi.usage_rule_expression.usage_rule_expression_id</code>.
     * Primary, internal database key.
     */
    public final TableField<UsageRuleExpressionRecord, String> USAGE_RULE_EXPRESSION_ID = createField(DSL.name("usage_rule_expression_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.usage_rule_expression.constraint_type</code>.
     * Constraint type according to the CC spec. It represents the expression
     * language (syntax) used in the CONSTRAINT column. It is a value list
     * column. 0 = 'Unstructured' which is basically a description of the rule,
     * 1 = 'Schematron'.
     */
    public final TableField<UsageRuleExpressionRecord, Integer> CONSTRAINT_TYPE = createField(DSL.name("constraint_type"), SQLDataType.INTEGER.nullable(false), this, "Constraint type according to the CC spec. It represents the expression language (syntax) used in the CONSTRAINT column. It is a value list column. 0 = 'Unstructured' which is basically a description of the rule, 1 = 'Schematron'.");

    /**
     * The column <code>oagi.usage_rule_expression.constraint_text</code>. This
     * column capture the constraint expressing the usage rule. In other words,
     * this is the expression.
     */
    public final TableField<UsageRuleExpressionRecord, String> CONSTRAINT_TEXT = createField(DSL.name("constraint_text"), SQLDataType.CLOB.nullable(false), this, "This column capture the constraint expressing the usage rule. In other words, this is the expression.");

    /**
     * The column
     * <code>oagi.usage_rule_expression.represented_usage_rule_id</code>.
     */
    public final TableField<UsageRuleExpressionRecord, String> REPRESENTED_USAGE_RULE_ID = createField(DSL.name("represented_usage_rule_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    private UsageRuleExpression(Name alias, Table<UsageRuleExpressionRecord> aliased) {
        this(alias, aliased, null);
    }

    private UsageRuleExpression(Name alias, Table<UsageRuleExpressionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The USAGE_RULE_EXPRESSION provides a representation of a usage rule in a particular syntax indicated by the CONSTRAINT_TYPE column. One of the syntaxes can be unstructured, which works a description of the usage rule."), TableOptions.table());
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

    /**
     * Create a <code>oagi.usage_rule_expression</code> table reference
     */
    public UsageRuleExpression() {
        this(DSL.name("usage_rule_expression"), null);
    }

    public <O extends Record> UsageRuleExpression(Table<O> child, ForeignKey<O, UsageRuleExpressionRecord> key) {
        super(child, key, USAGE_RULE_EXPRESSION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<UsageRuleExpressionRecord> getPrimaryKey() {
        return Keys.KEY_USAGE_RULE_EXPRESSION_PRIMARY;
    }

    @Override
    public List<ForeignKey<UsageRuleExpressionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.USAGE_RULE_EXPRESSION_REPRESENTED_USAGE_RULE_ID_FK);
    }

    private transient UsageRule _usageRule;

    /**
     * Get the implicit join path to the <code>oagi.usage_rule</code> table.
     */
    public UsageRule usageRule() {
        if (_usageRule == null)
            _usageRule = new UsageRule(this, Keys.USAGE_RULE_EXPRESSION_REPRESENTED_USAGE_RULE_ID_FK);

        return _usageRule;
    }

    @Override
    public UsageRuleExpression as(String alias) {
        return new UsageRuleExpression(DSL.name(alias), this);
    }

    @Override
    public UsageRuleExpression as(Name alias) {
        return new UsageRuleExpression(alias, this);
    }

    @Override
    public UsageRuleExpression as(Table<?> alias) {
        return new UsageRuleExpression(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public UsageRuleExpression rename(Table<?> name) {
        return new UsageRuleExpression(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, Integer, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super String, ? super Integer, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super String, ? super Integer, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
