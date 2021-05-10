/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CtxCategoryRecord;


/**
 * This table captures the context category. Examples of context categories 
 * as described in the CCTS are business process, industry, etc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxCategory extends TableImpl<CtxCategoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.ctx_category</code>
     */
    public static final CtxCategory CTX_CATEGORY = new CtxCategory();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CtxCategoryRecord> getRecordType() {
        return CtxCategoryRecord.class;
    }

    /**
     * The column <code>oagi.ctx_category.ctx_category_id</code>. Internal, primary, database key.
     */
    public final TableField<CtxCategoryRecord, ULong> CTX_CATEGORY_ID = createField(DSL.name("ctx_category_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary, database key.");

    /**
     * The column <code>oagi.ctx_category.guid</code>. A globally unique identifier (GUID).
     */
    public final TableField<CtxCategoryRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.ctx_category.name</code>. Short name of the context category.
     */
    public final TableField<CtxCategoryRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(45), this, "Short name of the context category.");

    /**
     * The column <code>oagi.ctx_category.description</code>. Explanation of what the context category is.
     */
    public final TableField<CtxCategoryRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "Explanation of what the context category is.");

    /**
     * The column <code>oagi.ctx_category.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the context category.
     */
    public final TableField<CtxCategoryRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created the context category.");

    /**
     * The column <code>oagi.ctx_category.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the context category.
     */
    public final TableField<CtxCategoryRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the context category.");

    /**
     * The column <code>oagi.ctx_category.creation_timestamp</code>. Timestamp when the context category was created.
     */
    public final TableField<CtxCategoryRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the context category was created.");

    /**
     * The column <code>oagi.ctx_category.last_update_timestamp</code>. Timestamp when the context category was last updated.
     */
    public final TableField<CtxCategoryRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the context category was last updated.");

    private CtxCategory(Name alias, Table<CtxCategoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private CtxCategory(Name alias, Table<CtxCategoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures the context category. Examples of context categories as described in the CCTS are business process, industry, etc."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.ctx_category</code> table reference
     */
    public CtxCategory(String alias) {
        this(DSL.name(alias), CTX_CATEGORY);
    }

    /**
     * Create an aliased <code>oagi.ctx_category</code> table reference
     */
    public CtxCategory(Name alias) {
        this(alias, CTX_CATEGORY);
    }

    /**
     * Create a <code>oagi.ctx_category</code> table reference
     */
    public CtxCategory() {
        this(DSL.name("ctx_category"), null);
    }

    public <O extends Record> CtxCategory(Table<O> child, ForeignKey<O, CtxCategoryRecord> key) {
        super(child, key, CTX_CATEGORY);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<CtxCategoryRecord, ULong> getIdentity() {
        return (Identity<CtxCategoryRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<CtxCategoryRecord> getPrimaryKey() {
        return Keys.KEY_CTX_CATEGORY_PRIMARY;
    }

    @Override
    public List<UniqueKey<CtxCategoryRecord>> getKeys() {
        return Arrays.<UniqueKey<CtxCategoryRecord>>asList(Keys.KEY_CTX_CATEGORY_PRIMARY, Keys.KEY_CTX_CATEGORY_CTX_CATEGORY_UK1);
    }

    @Override
    public List<ForeignKey<CtxCategoryRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CtxCategoryRecord, ?>>asList(Keys.CTX_CATEGORY_CREATED_BY_FK, Keys.CTX_CATEGORY_LAST_UPDATED_BY_FK);
    }

    private transient AppUser _ctxCategoryCreatedByFk;
    private transient AppUser _ctxCategoryLastUpdatedByFk;

    public AppUser ctxCategoryCreatedByFk() {
        if (_ctxCategoryCreatedByFk == null)
            _ctxCategoryCreatedByFk = new AppUser(this, Keys.CTX_CATEGORY_CREATED_BY_FK);

        return _ctxCategoryCreatedByFk;
    }

    public AppUser ctxCategoryLastUpdatedByFk() {
        if (_ctxCategoryLastUpdatedByFk == null)
            _ctxCategoryLastUpdatedByFk = new AppUser(this, Keys.CTX_CATEGORY_LAST_UPDATED_BY_FK);

        return _ctxCategoryLastUpdatedByFk;
    }

    @Override
    public CtxCategory as(String alias) {
        return new CtxCategory(DSL.name(alias), this);
    }

    @Override
    public CtxCategory as(Name alias) {
        return new CtxCategory(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CtxCategory rename(String name) {
        return new CtxCategory(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CtxCategory rename(Name name) {
        return new CtxCategory(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
