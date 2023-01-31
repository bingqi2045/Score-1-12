/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.e2e.impl.api.jooq.entity.tables;


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
import org.oagi.score.e2e.impl.api.jooq.entity.Keys;
import org.oagi.score.e2e.impl.api.jooq.entity.Oagi;
import org.oagi.score.e2e.impl.api.jooq.entity.tables.records.TenantBusinessCtxRecord;


/**
 * This table captures the tenant role and theirs business contexts.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TenantBusinessCtx extends TableImpl<TenantBusinessCtxRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.tenant_business_ctx</code>
     */
    public static final TenantBusinessCtx TENANT_BUSINESS_CTX = new TenantBusinessCtx();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TenantBusinessCtxRecord> getRecordType() {
        return TenantBusinessCtxRecord.class;
    }

    /**
     * The column <code>oagi.tenant_business_ctx.tenant_business_ctx_id</code>.
     * Primary key column.
     */
    public final TableField<TenantBusinessCtxRecord, ULong> TENANT_BUSINESS_CTX_ID = createField(DSL.name("tenant_business_ctx_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key column.");

    /**
     * The column <code>oagi.tenant_business_ctx.tenant_id</code>. Tenant role.
     */
    public final TableField<TenantBusinessCtxRecord, ULong> TENANT_ID = createField(DSL.name("tenant_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Tenant role.");

    /**
     * The column <code>oagi.tenant_business_ctx.biz_ctx_id</code>. Concrete
     * business context for the company.
     */
    public final TableField<TenantBusinessCtxRecord, ULong> BIZ_CTX_ID = createField(DSL.name("biz_ctx_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Concrete business context for the company.");

    private TenantBusinessCtx(Name alias, Table<TenantBusinessCtxRecord> aliased) {
        this(alias, aliased, null);
    }

    private TenantBusinessCtx(Name alias, Table<TenantBusinessCtxRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures the tenant role and theirs business contexts."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.tenant_business_ctx</code> table reference
     */
    public TenantBusinessCtx(String alias) {
        this(DSL.name(alias), TENANT_BUSINESS_CTX);
    }

    /**
     * Create an aliased <code>oagi.tenant_business_ctx</code> table reference
     */
    public TenantBusinessCtx(Name alias) {
        this(alias, TENANT_BUSINESS_CTX);
    }

    /**
     * Create a <code>oagi.tenant_business_ctx</code> table reference
     */
    public TenantBusinessCtx() {
        this(DSL.name("tenant_business_ctx"), null);
    }

    public <O extends Record> TenantBusinessCtx(Table<O> child, ForeignKey<O, TenantBusinessCtxRecord> key) {
        super(child, key, TENANT_BUSINESS_CTX);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<TenantBusinessCtxRecord, ULong> getIdentity() {
        return (Identity<TenantBusinessCtxRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<TenantBusinessCtxRecord> getPrimaryKey() {
        return Keys.KEY_TENANT_BUSINESS_CTX_PRIMARY;
    }

    @Override
    public List<UniqueKey<TenantBusinessCtxRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.KEY_TENANT_BUSINESS_CTX_TENANT_BUSINESS_CTX_PAIR);
    }

    @Override
    public List<ForeignKey<TenantBusinessCtxRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TENANT_BUSINESS_CTX_TENANT_ID_FK, Keys.ORGANIZATION_BUSINESS_CTX_BIZ_CTX_ID_FK);
    }

    private transient Tenant _tenant;
    private transient BizCtx _bizCtx;

    /**
     * Get the implicit join path to the <code>oagi.tenant</code> table.
     */
    public Tenant tenant() {
        if (_tenant == null)
            _tenant = new Tenant(this, Keys.TENANT_BUSINESS_CTX_TENANT_ID_FK);

        return _tenant;
    }

    /**
     * Get the implicit join path to the <code>oagi.biz_ctx</code> table.
     */
    public BizCtx bizCtx() {
        if (_bizCtx == null)
            _bizCtx = new BizCtx(this, Keys.ORGANIZATION_BUSINESS_CTX_BIZ_CTX_ID_FK);

        return _bizCtx;
    }

    @Override
    public TenantBusinessCtx as(String alias) {
        return new TenantBusinessCtx(DSL.name(alias), this);
    }

    @Override
    public TenantBusinessCtx as(Name alias) {
        return new TenantBusinessCtx(alias, this);
    }

    @Override
    public TenantBusinessCtx as(Table<?> alias) {
        return new TenantBusinessCtx(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TenantBusinessCtx rename(String name) {
        return new TenantBusinessCtx(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TenantBusinessCtx rename(Name name) {
        return new TenantBusinessCtx(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TenantBusinessCtx rename(Table<?> name) {
        return new TenantBusinessCtx(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}