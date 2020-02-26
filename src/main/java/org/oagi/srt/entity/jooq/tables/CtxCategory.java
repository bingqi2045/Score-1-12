/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.CtxCategoryRecord;


/**
 * This table captures the context category. Examples of context categories 
 * as described in the CCTS are business process, industry, etc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxCategory extends TableImpl<CtxCategoryRecord> {

    private static final long serialVersionUID = -1327013301;

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
    public final TableField<CtxCategoryRecord, ULong> CTX_CATEGORY_ID = createField(DSL.name("ctx_category_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary, database key.");

    /**
     * The column <code>oagi.ctx_category.guid</code>. GUID of the context category.  Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<CtxCategoryRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "GUID of the context category.  Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.ctx_category.name</code>. Short name of the context category.
     */
    public final TableField<CtxCategoryRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(45), this, "Short name of the context category.");

    /**
     * The column <code>oagi.ctx_category.description</code>. Explanation of what the context category is.
     */
    public final TableField<CtxCategoryRecord, String> DESCRIPTION = createField(DSL.name("description"), org.jooq.impl.SQLDataType.CLOB, this, "Explanation of what the context category is.");

    /**
     * Create a <code>oagi.ctx_category</code> table reference
     */
    public CtxCategory() {
        this(DSL.name("ctx_category"), null);
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

    private CtxCategory(Name alias, Table<CtxCategoryRecord> aliased) {
        this(alias, aliased, null);
    }

    private CtxCategory(Name alias, Table<CtxCategoryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures the context category. Examples of context categories as described in the CCTS are business process, industry, etc."), TableOptions.table());
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
        return Keys.IDENTITY_CTX_CATEGORY;
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
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, String, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
