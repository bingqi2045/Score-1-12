/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CdtRefSpecRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtRefSpec extends TableImpl<CdtRefSpecRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.cdt_ref_spec</code>
     */
    public static final CdtRefSpec CDT_REF_SPEC = new CdtRefSpec();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CdtRefSpecRecord> getRecordType() {
        return CdtRefSpecRecord.class;
    }

    /**
     * The column <code>oagi.cdt_ref_spec.cdt_ref_spec_id</code>.
     */
    public final TableField<CdtRefSpecRecord, ULong> CDT_REF_SPEC_ID = createField(DSL.name("cdt_ref_spec_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.cdt_ref_spec.cdt_id</code>.
     */
    public final TableField<CdtRefSpecRecord, ULong> CDT_ID = createField(DSL.name("cdt_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.cdt_ref_spec.ref_spec_id</code>.
     */
    public final TableField<CdtRefSpecRecord, ULong> REF_SPEC_ID = createField(DSL.name("ref_spec_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    private CdtRefSpec(Name alias, Table<CdtRefSpecRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtRefSpec(Name alias, Table<CdtRefSpecRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.cdt_ref_spec</code> table reference
     */
    public CdtRefSpec(String alias) {
        this(DSL.name(alias), CDT_REF_SPEC);
    }

    /**
     * Create an aliased <code>oagi.cdt_ref_spec</code> table reference
     */
    public CdtRefSpec(Name alias) {
        this(alias, CDT_REF_SPEC);
    }

    /**
     * Create a <code>oagi.cdt_ref_spec</code> table reference
     */
    public CdtRefSpec() {
        this(DSL.name("cdt_ref_spec"), null);
    }

    public <O extends Record> CdtRefSpec(Table<O> child, ForeignKey<O, CdtRefSpecRecord> key) {
        super(child, key, CDT_REF_SPEC);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<CdtRefSpecRecord, ULong> getIdentity() {
        return (Identity<CdtRefSpecRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<CdtRefSpecRecord> getPrimaryKey() {
        return Keys.KEY_CDT_REF_SPEC_PRIMARY;
    }

    @Override
    public List<ForeignKey<CdtRefSpecRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CDT_REF_SPEC_CDT_ID_FK, Keys.CDT_REF_SPEC_REF_SPEC_ID_FK);
    }

    private transient Dt _dt;
    private transient RefSpec _refSpec;

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table.
     */
    public Dt dt() {
        if (_dt == null)
            _dt = new Dt(this, Keys.CDT_REF_SPEC_CDT_ID_FK);

        return _dt;
    }

    /**
     * Get the implicit join path to the <code>oagi.ref_spec</code> table.
     */
    public RefSpec refSpec() {
        if (_refSpec == null)
            _refSpec = new RefSpec(this, Keys.CDT_REF_SPEC_REF_SPEC_ID_FK);

        return _refSpec;
    }

    @Override
    public CdtRefSpec as(String alias) {
        return new CdtRefSpec(DSL.name(alias), this);
    }

    @Override
    public CdtRefSpec as(Name alias) {
        return new CdtRefSpec(alias, this);
    }

    @Override
    public CdtRefSpec as(Table<?> alias) {
        return new CdtRefSpec(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtRefSpec rename(String name) {
        return new CdtRefSpec(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtRefSpec rename(Name name) {
        return new CdtRefSpec(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtRefSpec rename(Table<?> name) {
        return new CdtRefSpec(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
