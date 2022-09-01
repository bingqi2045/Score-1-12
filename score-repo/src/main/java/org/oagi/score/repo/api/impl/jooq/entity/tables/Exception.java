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
import org.jooq.Function6;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row6;
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
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ExceptionRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Exception extends TableImpl<ExceptionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.exception</code>
     */
    public static final Exception EXCEPTION = new Exception();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ExceptionRecord> getRecordType() {
        return ExceptionRecord.class;
    }

    /**
     * The column <code>oagi.exception.exception_id</code>. Internal, primary
     * database key.
     */
    public final TableField<ExceptionRecord, ULong> EXCEPTION_ID = createField(DSL.name("exception_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.exception.tag</code>. A tag of the exception for
     * the purpose of the searching facilitation
     */
    public final TableField<ExceptionRecord, String> TAG = createField(DSL.name("tag"), SQLDataType.VARCHAR(50), this, "A tag of the exception for the purpose of the searching facilitation");

    /**
     * The column <code>oagi.exception.message</code>. The exception message.
     */
    public final TableField<ExceptionRecord, String> MESSAGE = createField(DSL.name("message"), SQLDataType.CLOB, this, "The exception message.");

    /**
     * The column <code>oagi.exception.stacktrace</code>. The serialized
     * stacktrace object.
     */
    public final TableField<ExceptionRecord, byte[]> STACKTRACE = createField(DSL.name("stacktrace"), SQLDataType.BLOB, this, "The serialized stacktrace object.");

    /**
     * The column <code>oagi.exception.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who is working on when the
     * exception occurs.
     */
    public final TableField<ExceptionRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who is working on when the exception occurs.");

    /**
     * The column <code>oagi.exception.creation_timestamp</code>. Timestamp when
     * the exception was created.
     */
    public final TableField<ExceptionRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the exception was created.");

    private Exception(Name alias, Table<ExceptionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Exception(Name alias, Table<ExceptionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.exception</code> table reference
     */
    public Exception(String alias) {
        this(DSL.name(alias), EXCEPTION);
    }

    /**
     * Create an aliased <code>oagi.exception</code> table reference
     */
    public Exception(Name alias) {
        this(alias, EXCEPTION);
    }

    /**
     * Create a <code>oagi.exception</code> table reference
     */
    public Exception() {
        this(DSL.name("exception"), null);
    }

    public <O extends Record> Exception(Table<O> child, ForeignKey<O, ExceptionRecord> key) {
        super(child, key, EXCEPTION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.EXCEPTION_EXCEPTION_TAG_IDX);
    }

    @Override
    public Identity<ExceptionRecord, ULong> getIdentity() {
        return (Identity<ExceptionRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<ExceptionRecord> getPrimaryKey() {
        return Keys.KEY_EXCEPTION_PRIMARY;
    }

    @Override
    public List<ForeignKey<ExceptionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.EXCEPTION_CREATED_BY_FK);
    }

    private transient AppUser _appUser;

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table.
     */
    public AppUser appUser() {
        if (_appUser == null)
            _appUser = new AppUser(this, Keys.EXCEPTION_CREATED_BY_FK);

        return _appUser;
    }

    @Override
    public Exception as(String alias) {
        return new Exception(DSL.name(alias), this);
    }

    @Override
    public Exception as(Name alias) {
        return new Exception(alias, this);
    }

    @Override
    public Exception as(Table<?> alias) {
        return new Exception(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Exception rename(String name) {
        return new Exception(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Exception rename(Name name) {
        return new Exception(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Exception rename(Table<?> name) {
        return new Exception(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<ULong, String, String, byte[], String, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function6<? super ULong, ? super String, ? super String, ? super byte[], ? super String, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function6<? super ULong, ? super String, ? super String, ? super byte[], ? super String, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
