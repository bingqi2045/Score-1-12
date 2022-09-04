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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.DtUsageRuleRecord;


/**
 * This is an intersection table. Per CCTS, a usage rule may be reused. This
 * table allows m-m relationships between the usage rule and the DT content
 * component and usage rules and DT supplementary component. In a particular
 * record, either a TARGET_DT_ID or TARGET_DT_SC_ID must be present but not
 * both.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtUsageRule extends TableImpl<DtUsageRuleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.dt_usage_rule</code>
     */
    public static final DtUsageRule DT_USAGE_RULE = new DtUsageRule();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtUsageRuleRecord> getRecordType() {
        return DtUsageRuleRecord.class;
    }

    /**
     * The column <code>oagi.dt_usage_rule.dt_usage_rule_id</code>. Primary,
     * internal database key.
     */
    public final TableField<DtUsageRuleRecord, String> DT_USAGE_RULE_ID = createField(DSL.name("dt_usage_rule_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.dt_usage_rule.assigned_usage_rule_id</code>.
     * Foreign key to the USAGE_RULE table indicating the usage rule assigned to
     * a BIE.
     */
    public final TableField<DtUsageRuleRecord, String> ASSIGNED_USAGE_RULE_ID = createField(DSL.name("assigned_usage_rule_id"), SQLDataType.CHAR(36), this, "Foreign key to the USAGE_RULE table indicating the usage rule assigned to a BIE.");

    /**
     * The column <code>oagi.dt_usage_rule.target_dt_id</code>. Foreign key to
     * the DT_ID for assigning a usage rule to the corresponding DT content
     * component.
     */
    public final TableField<DtUsageRuleRecord, String> TARGET_DT_ID = createField(DSL.name("target_dt_id"), SQLDataType.CHAR(36), this, "Foreign key to the DT_ID for assigning a usage rule to the corresponding DT content component.");

    /**
     * The column <code>oagi.dt_usage_rule.target_dt_sc_id</code>. Foreign key
     * to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.
     */
    public final TableField<DtUsageRuleRecord, String> TARGET_DT_SC_ID = createField(DSL.name("target_dt_sc_id"), SQLDataType.CHAR(36), this, "Foreign key to the DT_SC_ID for assigning a usage rule to the corresponding DT_SC.");

    private DtUsageRule(Name alias, Table<DtUsageRuleRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtUsageRule(Name alias, Table<DtUsageRuleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This is an intersection table. Per CCTS, a usage rule may be reused. This table allows m-m relationships between the usage rule and the DT content component and usage rules and DT supplementary component. In a particular record, either a TARGET_DT_ID or TARGET_DT_SC_ID must be present but not both."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.dt_usage_rule</code> table reference
     */
    public DtUsageRule(String alias) {
        this(DSL.name(alias), DT_USAGE_RULE);
    }

    /**
     * Create an aliased <code>oagi.dt_usage_rule</code> table reference
     */
    public DtUsageRule(Name alias) {
        this(alias, DT_USAGE_RULE);
    }

    /**
     * Create a <code>oagi.dt_usage_rule</code> table reference
     */
    public DtUsageRule() {
        this(DSL.name("dt_usage_rule"), null);
    }

    public <O extends Record> DtUsageRule(Table<O> child, ForeignKey<O, DtUsageRuleRecord> key) {
        super(child, key, DT_USAGE_RULE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<DtUsageRuleRecord> getPrimaryKey() {
        return Keys.KEY_DT_USAGE_RULE_PRIMARY;
    }

    @Override
    public List<ForeignKey<DtUsageRuleRecord, ?>> getReferences() {
        return Arrays.asList(Keys.DT_USAGE_RULE_ASSIGNED_USAGE_RULE_ID_FK, Keys.DT_USAGE_RULE_TARGET_DT_ID_FK, Keys.DT_USAGE_RULE_TARGET_DT_SC_ID_FK);
    }

    private transient UsageRule _usageRule;
    private transient Dt _dt;
    private transient DtSc _dtSc;

    /**
     * Get the implicit join path to the <code>oagi.usage_rule</code> table.
     */
    public UsageRule usageRule() {
        if (_usageRule == null)
            _usageRule = new UsageRule(this, Keys.DT_USAGE_RULE_ASSIGNED_USAGE_RULE_ID_FK);

        return _usageRule;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table.
     */
    public Dt dt() {
        if (_dt == null)
            _dt = new Dt(this, Keys.DT_USAGE_RULE_TARGET_DT_ID_FK);

        return _dt;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_sc</code> table.
     */
    public DtSc dtSc() {
        if (_dtSc == null)
            _dtSc = new DtSc(this, Keys.DT_USAGE_RULE_TARGET_DT_SC_ID_FK);

        return _dtSc;
    }

    @Override
    public DtUsageRule as(String alias) {
        return new DtUsageRule(DSL.name(alias), this);
    }

    @Override
    public DtUsageRule as(Name alias) {
        return new DtUsageRule(alias, this);
    }

    @Override
    public DtUsageRule as(Table<?> alias) {
        return new DtUsageRule(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public DtUsageRule rename(String name) {
        return new DtUsageRule(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtUsageRule rename(Name name) {
        return new DtUsageRule(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtUsageRule rename(Table<?> name) {
        return new DtUsageRule(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function4<? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function4<? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
