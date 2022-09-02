/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function7;
import org.jooq.Identity;
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
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccBiztermRecord;


/**
 * The ascc_bizterm table stores information about the aggregation between the
 * business term and ASCC. TODO: Placeholder, definition is missing.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccBizterm extends TableImpl<AsccBiztermRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.ascc_bizterm</code>
     */
    public static final AsccBizterm ASCC_BIZTERM = new AsccBizterm();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsccBiztermRecord> getRecordType() {
        return AsccBiztermRecord.class;
    }

    /**
     * The column <code>oagi.ascc_bizterm.ascc_bizterm_id</code>. An internal,
     * primary database key of an Business term.
     */
    public final TableField<AsccBiztermRecord, ULong> ASCC_BIZTERM_ID = createField(DSL.name("ascc_bizterm_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "An internal, primary database key of an Business term.");

    /**
     * The column <code>oagi.ascc_bizterm.business_term_id</code>. An internal
     * ID of the associated business term
     */
    public final TableField<AsccBiztermRecord, ULong> BUSINESS_TERM_ID = createField(DSL.name("business_term_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "An internal ID of the associated business term");

    /**
     * The column <code>oagi.ascc_bizterm.ascc_id</code>. An internal ID of the
     * associated ASCC
     */
    public final TableField<AsccBiztermRecord, String> ASCC_ID = createField(DSL.name("ascc_id"), SQLDataType.CHAR(36), this, "An internal ID of the associated ASCC");

    /**
     * The column <code>oagi.ascc_bizterm.created_by</code>. A foreign key
     * referring to the user who creates the ASCC_BIZTERM record. The creator of
     * the ASCC_BIZTERM is also its owner by default.
     */
    public final TableField<AsccBiztermRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key referring to the user who creates the ASCC_BIZTERM record. The creator of the ASCC_BIZTERM is also its owner by default.");

    /**
     * The column <code>oagi.ascc_bizterm.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the ASCC_BIZTERM record. This
     * may be the user who is in the same group as the creator.
     */
    public final TableField<AsccBiztermRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key referring to the last user who has updated the ASCC_BIZTERM record. This may be the user who is in the same group as the creator.");

    /**
     * The column <code>oagi.ascc_bizterm.creation_timestamp</code>. Timestamp
     * when the ascc_bizterm record was first created.
     */
    public final TableField<AsccBiztermRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the ascc_bizterm record was first created.");

    /**
     * The column <code>oagi.ascc_bizterm.last_update_timestamp</code>. The
     * timestamp when the ascc_bizterm was last updated.
     */
    public final TableField<AsccBiztermRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the ascc_bizterm was last updated.");

    private AsccBizterm(Name alias, Table<AsccBiztermRecord> aliased) {
        this(alias, aliased, null);
    }

    private AsccBizterm(Name alias, Table<AsccBiztermRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The ascc_bizterm table stores information about the aggregation between the business term and ASCC. TODO: Placeholder, definition is missing."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.ascc_bizterm</code> table reference
     */
    public AsccBizterm(String alias) {
        this(DSL.name(alias), ASCC_BIZTERM);
    }

    /**
     * Create an aliased <code>oagi.ascc_bizterm</code> table reference
     */
    public AsccBizterm(Name alias) {
        this(alias, ASCC_BIZTERM);
    }

    /**
     * Create a <code>oagi.ascc_bizterm</code> table reference
     */
    public AsccBizterm() {
        this(DSL.name("ascc_bizterm"), null);
    }

    public <O extends Record> AsccBizterm(Table<O> child, ForeignKey<O, AsccBiztermRecord> key) {
        super(child, key, ASCC_BIZTERM);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<AsccBiztermRecord, ULong> getIdentity() {
        return (Identity<AsccBiztermRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<AsccBiztermRecord> getPrimaryKey() {
        return Keys.KEY_ASCC_BIZTERM_PRIMARY;
    }

    @Override
    public List<ForeignKey<AsccBiztermRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ASCC_BIZTERM_BUSINESS_TERM_FK, Keys.ASCC_BIZTERM_ASCC_ID_FK, Keys.ASCC_BIZTERM_CREATED_BY_FK, Keys.ASCC_BIZTERM_LAST_UPDATED_BY_FK);
    }

    private transient BusinessTerm _businessTerm;
    private transient Ascc _ascc;
    private transient AppUser _asccBiztermCreatedByFk;
    private transient AppUser _asccBiztermLastUpdatedByFk;

    /**
     * Get the implicit join path to the <code>oagi.business_term</code> table.
     */
    public BusinessTerm businessTerm() {
        if (_businessTerm == null)
            _businessTerm = new BusinessTerm(this, Keys.ASCC_BIZTERM_BUSINESS_TERM_FK);

        return _businessTerm;
    }

    /**
     * Get the implicit join path to the <code>oagi.ascc</code> table.
     */
    public Ascc ascc() {
        if (_ascc == null)
            _ascc = new Ascc(this, Keys.ASCC_BIZTERM_ASCC_ID_FK);

        return _ascc;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>ascc_bizterm_created_by_fk</code> key.
     */
    public AppUser asccBiztermCreatedByFk() {
        if (_asccBiztermCreatedByFk == null)
            _asccBiztermCreatedByFk = new AppUser(this, Keys.ASCC_BIZTERM_CREATED_BY_FK);

        return _asccBiztermCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>ascc_bizterm_last_updated_by_fk</code> key.
     */
    public AppUser asccBiztermLastUpdatedByFk() {
        if (_asccBiztermLastUpdatedByFk == null)
            _asccBiztermLastUpdatedByFk = new AppUser(this, Keys.ASCC_BIZTERM_LAST_UPDATED_BY_FK);

        return _asccBiztermLastUpdatedByFk;
    }

    @Override
    public AsccBizterm as(String alias) {
        return new AsccBizterm(DSL.name(alias), this);
    }

    @Override
    public AsccBizterm as(Name alias) {
        return new AsccBizterm(alias, this);
    }

    @Override
    public AsccBizterm as(Table<?> alias) {
        return new AsccBizterm(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccBizterm rename(String name) {
        return new AsccBizterm(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccBizterm rename(Name name) {
        return new AsccBizterm(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccBizterm rename(Table<?> name) {
        return new AsccBizterm(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, ULong, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super ULong, ? super ULong, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super ULong, ? super ULong, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
