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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BizCtxRecord;


/**
 * This table represents a business context. A business context is a combination
 * of one or more business context values.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BizCtx extends TableImpl<BizCtxRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.biz_ctx</code>
     */
    public static final BizCtx BIZ_CTX = new BizCtx();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BizCtxRecord> getRecordType() {
        return BizCtxRecord.class;
    }

    /**
     * The column <code>oagi.biz_ctx.biz_ctx_id</code>. Primary, internal
     * database key.
     */
    public final TableField<BizCtxRecord, ULong> BIZ_CTX_ID = createField(DSL.name("biz_ctx_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.biz_ctx.guid</code>. A globally unique identifier
     * (GUID).
     */
    public final TableField<BizCtxRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.biz_ctx.name</code>. Short, descriptive name of the
     * business context.
     */
    public final TableField<BizCtxRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100), this, "Short, descriptive name of the business context.");

    /**
     * The column <code>oagi.biz_ctx.created_by</code>. Foreign key to the
     * APP_USER table referring to the user who creates the entity.
     */
    public final TableField<BizCtxRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table referring to the user who creates the entity.");

    /**
     * The column <code>oagi.biz_ctx.last_updated_by</code>. Foreign key to the
     * APP_USER table  referring to the last user who has updated the business
     * context.
     */
    public final TableField<BizCtxRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table  referring to the last user who has updated the business context.");

    /**
     * The column <code>oagi.biz_ctx.creation_timestamp</code>. Timestamp when
     * the business context record was first created. 
     */
    public final TableField<BizCtxRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the business context record was first created. ");

    /**
     * The column <code>oagi.biz_ctx.last_update_timestamp</code>. The timestamp
     * when the business context was last updated.
     */
    public final TableField<BizCtxRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the business context was last updated.");

    private BizCtx(Name alias, Table<BizCtxRecord> aliased) {
        this(alias, aliased, null);
    }

    private BizCtx(Name alias, Table<BizCtxRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table represents a business context. A business context is a combination of one or more business context values."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.biz_ctx</code> table reference
     */
    public BizCtx(String alias) {
        this(DSL.name(alias), BIZ_CTX);
    }

    /**
     * Create an aliased <code>oagi.biz_ctx</code> table reference
     */
    public BizCtx(Name alias) {
        this(alias, BIZ_CTX);
    }

    /**
     * Create a <code>oagi.biz_ctx</code> table reference
     */
    public BizCtx() {
        this(DSL.name("biz_ctx"), null);
    }

    public <O extends Record> BizCtx(Table<O> child, ForeignKey<O, BizCtxRecord> key) {
        super(child, key, BIZ_CTX);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<BizCtxRecord, ULong> getIdentity() {
        return (Identity<BizCtxRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<BizCtxRecord> getPrimaryKey() {
        return Keys.KEY_BIZ_CTX_PRIMARY;
    }

    @Override
    public List<UniqueKey<BizCtxRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_BIZ_CTX_BIZ_CTX_UK1);
    }

    @Override
    public List<ForeignKey<BizCtxRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BIZ_CTX_CREATED_BY_FK, Keys.BIZ_CTX_LAST_UPDATED_BY_FK);
    }

    private transient AppUser _bizCtxCreatedByFk;
    private transient AppUser _bizCtxLastUpdatedByFk;

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>biz_ctx_created_by_fk</code> key.
     */
    public AppUser bizCtxCreatedByFk() {
        if (_bizCtxCreatedByFk == null)
            _bizCtxCreatedByFk = new AppUser(this, Keys.BIZ_CTX_CREATED_BY_FK);

        return _bizCtxCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>biz_ctx_last_updated_by_fk</code> key.
     */
    public AppUser bizCtxLastUpdatedByFk() {
        if (_bizCtxLastUpdatedByFk == null)
            _bizCtxLastUpdatedByFk = new AppUser(this, Keys.BIZ_CTX_LAST_UPDATED_BY_FK);

        return _bizCtxLastUpdatedByFk;
    }

    @Override
    public BizCtx as(String alias) {
        return new BizCtx(DSL.name(alias), this);
    }

    @Override
    public BizCtx as(Name alias) {
        return new BizCtx(alias, this);
    }

    @Override
    public BizCtx as(Table<?> alias) {
        return new BizCtx(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtx rename(String name) {
        return new BizCtx(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtx rename(Name name) {
        return new BizCtx(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtx rename(Table<?> name) {
        return new BizCtx(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super ULong, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super ULong, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
