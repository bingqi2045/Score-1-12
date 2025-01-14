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
import org.jooq.Function11;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row11;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.LogRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Log extends TableImpl<LogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.log</code>
     */
    public static final Log LOG = new Log();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LogRecord> getRecordType() {
        return LogRecord.class;
    }

    /**
     * The column <code>oagi.log.log_id</code>.
     */
    public final TableField<LogRecord, ULong> LOG_ID = createField(DSL.name("log_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.log.hash</code>. The unique hash to identify the
     * log.
     */
    public final TableField<LogRecord, String> HASH = createField(DSL.name("hash"), SQLDataType.CHAR(40).nullable(false), this, "The unique hash to identify the log.");

    /**
     * The column <code>oagi.log.revision_num</code>. This is an incremental
     * integer. It tracks changes in each component. If a change is made to a
     * component after it has been published, the component receives a new
     * revision number. Revision number can be 1, 2, and so on.
     */
    public final TableField<LogRecord, UInteger> REVISION_NUM = createField(DSL.name("revision_num"), SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGERUNSIGNED)), this, "This is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 1, 2, and so on.");

    /**
     * The column <code>oagi.log.revision_tracking_num</code>. This supports the
     * ability to undo changes during a revision (life cycle of a revision is
     * from the component's WIP state to PUBLISHED state). REVISION_TRACKING_NUM
     * can be 1, 2, and so on.
     */
    public final TableField<LogRecord, UInteger> REVISION_TRACKING_NUM = createField(DSL.name("revision_tracking_num"), SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGERUNSIGNED)), this, "This supports the ability to undo changes during a revision (life cycle of a revision is from the component's WIP state to PUBLISHED state). REVISION_TRACKING_NUM can be 1, 2, and so on.");

    /**
     * The column <code>oagi.log.log_action</code>. This indicates the action
     * associated with the record.
     */
    public final TableField<LogRecord, String> LOG_ACTION = createField(DSL.name("log_action"), SQLDataType.VARCHAR(20), this, "This indicates the action associated with the record.");

    /**
     * The column <code>oagi.log.reference</code>.
     */
    public final TableField<LogRecord, String> REFERENCE = createField(DSL.name("reference"), SQLDataType.VARCHAR(100).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>oagi.log.snapshot</code>.
     */
    public final TableField<LogRecord, JSON> SNAPSHOT = createField(DSL.name("snapshot"), SQLDataType.JSON, this, "");

    /**
     * The column <code>oagi.log.prev_log_id</code>.
     */
    public final TableField<LogRecord, ULong> PREV_LOG_ID = createField(DSL.name("prev_log_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.log.next_log_id</code>.
     */
    public final TableField<LogRecord, ULong> NEXT_LOG_ID = createField(DSL.name("next_log_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.log.created_by</code>.
     */
    public final TableField<LogRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.log.creation_timestamp</code>.
     */
    public final TableField<LogRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private Log(Name alias, Table<LogRecord> aliased) {
        this(alias, aliased, null);
    }

    private Log(Name alias, Table<LogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.log</code> table reference
     */
    public Log(String alias) {
        this(DSL.name(alias), LOG);
    }

    /**
     * Create an aliased <code>oagi.log</code> table reference
     */
    public Log(Name alias) {
        this(alias, LOG);
    }

    /**
     * Create a <code>oagi.log</code> table reference
     */
    public Log() {
        this(DSL.name("log"), null);
    }

    public <O extends Record> Log(Table<O> child, ForeignKey<O, LogRecord> key) {
        super(child, key, LOG);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.LOG_REFERENCE);
    }

    @Override
    public Identity<LogRecord, ULong> getIdentity() {
        return (Identity<LogRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<LogRecord> getPrimaryKey() {
        return Keys.KEY_LOG_PRIMARY;
    }

    @Override
    public List<ForeignKey<LogRecord, ?>> getReferences() {
        return Arrays.asList(Keys.LOG_PREV_LOG_ID_FK, Keys.LOG_NEXT_LOG_ID_FK, Keys.LOG_CREATED_BY_FK);
    }

    private transient Log _logPrevLogIdFk;
    private transient Log _logNextLogIdFk;
    private transient AppUser _appUser;

    /**
     * Get the implicit join path to the <code>oagi.log</code> table, via the
     * <code>log_prev_log_id_fk</code> key.
     */
    public Log logPrevLogIdFk() {
        if (_logPrevLogIdFk == null)
            _logPrevLogIdFk = new Log(this, Keys.LOG_PREV_LOG_ID_FK);

        return _logPrevLogIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.log</code> table, via the
     * <code>log_next_log_id_fk</code> key.
     */
    public Log logNextLogIdFk() {
        if (_logNextLogIdFk == null)
            _logNextLogIdFk = new Log(this, Keys.LOG_NEXT_LOG_ID_FK);

        return _logNextLogIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table.
     */
    public AppUser appUser() {
        if (_appUser == null)
            _appUser = new AppUser(this, Keys.LOG_CREATED_BY_FK);

        return _appUser;
    }

    @Override
    public Log as(String alias) {
        return new Log(DSL.name(alias), this);
    }

    @Override
    public Log as(Name alias) {
        return new Log(alias, this);
    }

    @Override
    public Log as(Table<?> alias) {
        return new Log(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Log rename(String name) {
        return new Log(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Log rename(Name name) {
        return new Log(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Log rename(Table<?> name) {
        return new Log(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<ULong, String, UInteger, UInteger, String, String, JSON, ULong, ULong, ULong, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function11<? super ULong, ? super String, ? super UInteger, ? super UInteger, ? super String, ? super String, ? super JSON, ? super ULong, ? super ULong, ? super ULong, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function11<? super ULong, ? super String, ? super UInteger, ? super UInteger, ? super String, ? super String, ? super JSON, ? super ULong, ? super ULong, ? super ULong, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
