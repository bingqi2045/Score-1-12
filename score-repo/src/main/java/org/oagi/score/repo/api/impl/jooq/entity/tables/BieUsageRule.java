/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function7;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row7;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BieUsageRuleRecord;


/**
 * This is an intersection table. Per CCTS, a usage rule may be reused. This
 * table allows m-m relationships between the usage rule and all kinds of BIEs.
 * In a particular record, either only one of the TARGET_ABIE_ID,
 * TARGET_ASBIE_ID, TARGET_ASBIEP_ID, TARGET_BBIE_ID, or TARGET_BBIEP_ID.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BieUsageRule extends TableImpl<BieUsageRuleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.bie_usage_rule</code>
     */
    public static final BieUsageRule BIE_USAGE_RULE = new BieUsageRule();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BieUsageRuleRecord> getRecordType() {
        return BieUsageRuleRecord.class;
    }

    /**
     * The column <code>oagi.bie_usage_rule.bie_usage_rule_id</code>. Primary,
     * internal database key.
     */
    public final TableField<BieUsageRuleRecord, String> BIE_USAGE_RULE_ID = createField(DSL.name("bie_usage_rule_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.bie_usage_rule.assigned_usage_rule_id</code>.
     * Foreign key to the USAGE_RULE table indicating the usage rule assigned to
     * a BIE.
     */
    public final TableField<BieUsageRuleRecord, String> ASSIGNED_USAGE_RULE_ID = createField(DSL.name("assigned_usage_rule_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the USAGE_RULE table indicating the usage rule assigned to a BIE.");

    /**
     * The column <code>oagi.bie_usage_rule.target_abie_id</code>. Foreign key
     * to the ABIE table indicating the ABIE, to which the usage rule is
     * applied.
     */
    public final TableField<BieUsageRuleRecord, String> TARGET_ABIE_ID = createField(DSL.name("target_abie_id"), SQLDataType.CHAR(36), this, "Foreign key to the ABIE table indicating the ABIE, to which the usage rule is applied.");

    /**
     * The column <code>oagi.bie_usage_rule.target_asbie_id</code>. Foreign key
     * to the ASBIE table indicating the ASBIE, to which the usage rule is
     * applied.
     */
    public final TableField<BieUsageRuleRecord, String> TARGET_ASBIE_ID = createField(DSL.name("target_asbie_id"), SQLDataType.CHAR(36), this, "Foreign key to the ASBIE table indicating the ASBIE, to which the usage rule is applied.");

    /**
     * The column <code>oagi.bie_usage_rule.target_asbiep_id</code>. Foreign key
     * to the ASBIEP table indicating the ASBIEP, to which the usage rule is
     * applied.
     */
    public final TableField<BieUsageRuleRecord, String> TARGET_ASBIEP_ID = createField(DSL.name("target_asbiep_id"), SQLDataType.CHAR(36), this, "Foreign key to the ASBIEP table indicating the ASBIEP, to which the usage rule is applied.");

    /**
     * The column <code>oagi.bie_usage_rule.target_bbie_id</code>. Foreign key
     * to the BBIE table indicating the BBIE, to which the usage rule is
     * applied.
     */
    public final TableField<BieUsageRuleRecord, String> TARGET_BBIE_ID = createField(DSL.name("target_bbie_id"), SQLDataType.CHAR(36), this, "Foreign key to the BBIE table indicating the BBIE, to which the usage rule is applied.");

    /**
     * The column <code>oagi.bie_usage_rule.target_bbiep_id</code>. Foreign key
     * to the BBIEP table indicating the ABIEP, to which the usage rule is
     * applied.
     */
    public final TableField<BieUsageRuleRecord, String> TARGET_BBIEP_ID = createField(DSL.name("target_bbiep_id"), SQLDataType.CHAR(36), this, "Foreign key to the BBIEP table indicating the ABIEP, to which the usage rule is applied.");

    private BieUsageRule(Name alias, Table<BieUsageRuleRecord> aliased) {
        this(alias, aliased, null);
    }

    private BieUsageRule(Name alias, Table<BieUsageRuleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This is an intersection table. Per CCTS, a usage rule may be reused. This table allows m-m relationships between the usage rule and all kinds of BIEs. In a particular record, either only one of the TARGET_ABIE_ID, TARGET_ASBIE_ID, TARGET_ASBIEP_ID, TARGET_BBIE_ID, or TARGET_BBIEP_ID."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.bie_usage_rule</code> table reference
     */
    public BieUsageRule(String alias) {
        this(DSL.name(alias), BIE_USAGE_RULE);
    }

    /**
     * Create an aliased <code>oagi.bie_usage_rule</code> table reference
     */
    public BieUsageRule(Name alias) {
        this(alias, BIE_USAGE_RULE);
    }

    /**
     * Create a <code>oagi.bie_usage_rule</code> table reference
     */
    public BieUsageRule() {
        this(DSL.name("bie_usage_rule"), null);
    }

    public <O extends Record> BieUsageRule(Table<O> child, ForeignKey<O, BieUsageRuleRecord> key) {
        super(child, key, BIE_USAGE_RULE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<BieUsageRuleRecord> getPrimaryKey() {
        return Keys.KEY_BIE_USAGE_RULE_PRIMARY;
    }

    @Override
    public List<ForeignKey<BieUsageRuleRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BIE_USAGE_RULE_ASSIGNED_USAGE_RULE_ID_FK, Keys.BIE_USAGE_RULE_TARGET_ABIE_ID_FK, Keys.BIE_USAGE_RULE_TARGET_ASBIE_ID_FK, Keys.BIE_USAGE_RULE_TARGET_ASBIEP_ID_FK, Keys.BIE_USAGE_RULE_TARGET_BBIE_ID_FK, Keys.BIE_USAGE_RULE_TARGET_BBIEP_ID_FK);
    }

    private transient UsageRule _usageRule;
    private transient Abie _abie;
    private transient Asbie _asbie;
    private transient Asbiep _asbiep;
    private transient Bbie _bbie;
    private transient Bbiep _bbiep;

    /**
     * Get the implicit join path to the <code>oagi.usage_rule</code> table.
     */
    public UsageRule usageRule() {
        if (_usageRule == null)
            _usageRule = new UsageRule(this, Keys.BIE_USAGE_RULE_ASSIGNED_USAGE_RULE_ID_FK);

        return _usageRule;
    }

    /**
     * Get the implicit join path to the <code>oagi.abie</code> table.
     */
    public Abie abie() {
        if (_abie == null)
            _abie = new Abie(this, Keys.BIE_USAGE_RULE_TARGET_ABIE_ID_FK);

        return _abie;
    }

    /**
     * Get the implicit join path to the <code>oagi.asbie</code> table.
     */
    public Asbie asbie() {
        if (_asbie == null)
            _asbie = new Asbie(this, Keys.BIE_USAGE_RULE_TARGET_ASBIE_ID_FK);

        return _asbie;
    }

    /**
     * Get the implicit join path to the <code>oagi.asbiep</code> table.
     */
    public Asbiep asbiep() {
        if (_asbiep == null)
            _asbiep = new Asbiep(this, Keys.BIE_USAGE_RULE_TARGET_ASBIEP_ID_FK);

        return _asbiep;
    }

    /**
     * Get the implicit join path to the <code>oagi.bbie</code> table.
     */
    public Bbie bbie() {
        if (_bbie == null)
            _bbie = new Bbie(this, Keys.BIE_USAGE_RULE_TARGET_BBIE_ID_FK);

        return _bbie;
    }

    /**
     * Get the implicit join path to the <code>oagi.bbiep</code> table.
     */
    public Bbiep bbiep() {
        if (_bbiep == null)
            _bbiep = new Bbiep(this, Keys.BIE_USAGE_RULE_TARGET_BBIEP_ID_FK);

        return _bbiep;
    }

    @Override
    public BieUsageRule as(String alias) {
        return new BieUsageRule(DSL.name(alias), this);
    }

    @Override
    public BieUsageRule as(Name alias) {
        return new BieUsageRule(alias, this);
    }

    @Override
    public BieUsageRule as(Table<?> alias) {
        return new BieUsageRule(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BieUsageRule rename(String name) {
        return new BieUsageRule(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BieUsageRule rename(Name name) {
        return new BieUsageRule(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BieUsageRule rename(Table<?> name) {
        return new BieUsageRule(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, String, String, String, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
