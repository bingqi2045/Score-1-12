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
import org.jooq.Row3;
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
import org.oagi.score.entity.jooq.tables.records.BizCtxValueRecord;


/**
 * This table represents business context values for business contexts. It 
 * provides the associations between a business context and a context scheme 
 * value.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BizCtxValue extends TableImpl<BizCtxValueRecord> {

    private static final long serialVersionUID = 1386557966;

    /**
     * The reference instance of <code>oagi.biz_ctx_value</code>
     */
    public static final BizCtxValue BIZ_CTX_VALUE = new BizCtxValue();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BizCtxValueRecord> getRecordType() {
        return BizCtxValueRecord.class;
    }

    /**
     * The column <code>oagi.biz_ctx_value.biz_ctx_value_id</code>. Primary, internal database key.
     */
    public final TableField<BizCtxValueRecord, ULong> BIZ_CTX_VALUE_ID = createField(DSL.name("biz_ctx_value_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.biz_ctx_value.biz_ctx_id</code>. Foreign key to the biz_ctx table.
     */
    public final TableField<BizCtxValueRecord, ULong> BIZ_CTX_ID = createField(DSL.name("biz_ctx_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the biz_ctx table.");

    /**
     * The column <code>oagi.biz_ctx_value.ctx_scheme_value_id</code>. Foreign key to the CTX_SCHEME_VALUE table.
     */
    public final TableField<BizCtxValueRecord, ULong> CTX_SCHEME_VALUE_ID = createField(DSL.name("ctx_scheme_value_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the CTX_SCHEME_VALUE table.");

    /**
     * Create a <code>oagi.biz_ctx_value</code> table reference
     */
    public BizCtxValue() {
        this(DSL.name("biz_ctx_value"), null);
    }

    /**
     * Create an aliased <code>oagi.biz_ctx_value</code> table reference
     */
    public BizCtxValue(String alias) {
        this(DSL.name(alias), BIZ_CTX_VALUE);
    }

    /**
     * Create an aliased <code>oagi.biz_ctx_value</code> table reference
     */
    public BizCtxValue(Name alias) {
        this(alias, BIZ_CTX_VALUE);
    }

    private BizCtxValue(Name alias, Table<BizCtxValueRecord> aliased) {
        this(alias, aliased, null);
    }

    private BizCtxValue(Name alias, Table<BizCtxValueRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table represents business context values for business contexts. It provides the associations between a business context and a context scheme value."), TableOptions.table());
    }

    public <O extends Record> BizCtxValue(Table<O> child, ForeignKey<O, BizCtxValueRecord> key) {
        super(child, key, BIZ_CTX_VALUE);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<BizCtxValueRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BIZ_CTX_VALUE;
    }

    @Override
    public UniqueKey<BizCtxValueRecord> getPrimaryKey() {
        return Keys.KEY_BIZ_CTX_VALUE_PRIMARY;
    }

    @Override
    public List<UniqueKey<BizCtxValueRecord>> getKeys() {
        return Arrays.<UniqueKey<BizCtxValueRecord>>asList(Keys.KEY_BIZ_CTX_VALUE_PRIMARY);
    }

    @Override
    public List<ForeignKey<BizCtxValueRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BizCtxValueRecord, ?>>asList(Keys.BIZ_CTX_VALUE_BIZ_CTX_ID_FK, Keys.BIZ_CTX_VALUE_CTX_SCHEME_VALUE_ID_FK);
    }

    public BizCtx bizCtx() {
        return new BizCtx(this, Keys.BIZ_CTX_VALUE_BIZ_CTX_ID_FK);
    }

    public CtxSchemeValue ctxSchemeValue() {
        return new CtxSchemeValue(this, Keys.BIZ_CTX_VALUE_CTX_SCHEME_VALUE_ID_FK);
    }

    @Override
    public BizCtxValue as(String alias) {
        return new BizCtxValue(DSL.name(alias), this);
    }

    @Override
    public BizCtxValue as(Name alias) {
        return new BizCtxValue(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtxValue rename(String name) {
        return new BizCtxValue(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BizCtxValue rename(Name name) {
        return new BizCtxValue(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
