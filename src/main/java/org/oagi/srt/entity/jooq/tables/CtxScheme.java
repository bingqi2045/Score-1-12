/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.CtxSchemeRecord;


/**
 * This table represents a context scheme (a classification scheme) for a 
 * context category.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxScheme extends TableImpl<CtxSchemeRecord> {

    private static final long serialVersionUID = -8438479;

    /**
     * The reference instance of <code>oagi.ctx_scheme</code>
     */
    public static final CtxScheme CTX_SCHEME = new CtxScheme();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CtxSchemeRecord> getRecordType() {
        return CtxSchemeRecord.class;
    }

    /**
     * The column <code>oagi.ctx_scheme.ctx_scheme_id</code>. Internal, primary, database key.
     */
    public final TableField<CtxSchemeRecord, ULong> CTX_SCHEME_ID = createField("ctx_scheme_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary, database key.");

    /**
     * The column <code>oagi.ctx_scheme.guid</code>. GUID of the classification scheme. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<CtxSchemeRecord, String> GUID = createField("guid", org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "GUID of the classification scheme. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.ctx_scheme.scheme_id</code>. External identification of the scheme. 
     */
    public final TableField<CtxSchemeRecord, String> SCHEME_ID = createField("scheme_id", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "External identification of the scheme. ");

    /**
     * The column <code>oagi.ctx_scheme.scheme_name</code>. Pretty print name of the context scheme.
     */
    public final TableField<CtxSchemeRecord, String> SCHEME_NAME = createField("scheme_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Pretty print name of the context scheme.");

    /**
     * The column <code>oagi.ctx_scheme.description</code>. Description of the context scheme.
     */
    public final TableField<CtxSchemeRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB, this, "Description of the context scheme.");

    /**
     * The column <code>oagi.ctx_scheme.scheme_agency_id</code>. Identification of the agency maintaining the scheme. This column currently does not use the AGENCY_ID_LIST table. It is just a free form text at this point.
     */
    public final TableField<CtxSchemeRecord, String> SCHEME_AGENCY_ID = createField("scheme_agency_id", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "Identification of the agency maintaining the scheme. This column currently does not use the AGENCY_ID_LIST table. It is just a free form text at this point.");

    /**
     * The column <code>oagi.ctx_scheme.scheme_version_id</code>. Version number of the context scheme.
     */
    public final TableField<CtxSchemeRecord, String> SCHEME_VERSION_ID = createField("scheme_version_id", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "Version number of the context scheme.");

    /**
     * The column <code>oagi.ctx_scheme.ctx_category_id</code>. This the foreign key to the CTX_CATEGORY table. It identifies the context category associated with this context scheme.
     */
    public final TableField<CtxSchemeRecord, ULong> CTX_CATEGORY_ID = createField("ctx_category_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This the foreign key to the CTX_CATEGORY table. It identifies the context category associated with this context scheme.");

    /**
     * The column <code>oagi.ctx_scheme.code_list_id</code>.
     */
    public final TableField<CtxSchemeRecord, ULong> CODE_LIST_ID = createField("code_list_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.ctx_scheme.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this context scheme.
     */
    public final TableField<CtxSchemeRecord, ULong> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this context scheme.");

    /**
     * The column <code>oagi.ctx_scheme.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the context scheme.
     */
    public final TableField<CtxSchemeRecord, ULong> LAST_UPDATED_BY = createField("last_updated_by", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the context scheme.");

    /**
     * The column <code>oagi.ctx_scheme.creation_timestamp</code>. Timestamp when the scheme was created.
     */
    public final TableField<CtxSchemeRecord, Timestamp> CREATION_TIMESTAMP = createField("creation_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the scheme was created.");

    /**
     * The column <code>oagi.ctx_scheme.last_update_timestamp</code>. Timestamp when the scheme was last updated.
     */
    public final TableField<CtxSchemeRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField("last_update_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the scheme was last updated.");

    /**
     * Create a <code>oagi.ctx_scheme</code> table reference
     */
    public CtxScheme() {
        this(DSL.name("ctx_scheme"), null);
    }

    /**
     * Create an aliased <code>oagi.ctx_scheme</code> table reference
     */
    public CtxScheme(String alias) {
        this(DSL.name(alias), CTX_SCHEME);
    }

    /**
     * Create an aliased <code>oagi.ctx_scheme</code> table reference
     */
    public CtxScheme(Name alias) {
        this(alias, CTX_SCHEME);
    }

    private CtxScheme(Name alias, Table<CtxSchemeRecord> aliased) {
        this(alias, aliased, null);
    }

    private CtxScheme(Name alias, Table<CtxSchemeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table represents a context scheme (a classification scheme) for a context category."));
    }

    public <O extends Record> CtxScheme(Table<O> child, ForeignKey<O, CtxSchemeRecord> key) {
        super(child, key, CTX_SCHEME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CTX_SCHEME_CTX_SCHEME_CREATED_BY_FK, Indexes.CTX_SCHEME_CTX_SCHEME_CTX_CATEGORY_ID_FK, Indexes.CTX_SCHEME_CTX_SCHEME_LAST_UPDATED_BY_FK, Indexes.CTX_SCHEME_CTX_SCHEME_UK1, Indexes.CTX_SCHEME_FK_CODE_LIST_ID, Indexes.CTX_SCHEME_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<CtxSchemeRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CTX_SCHEME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CtxSchemeRecord> getPrimaryKey() {
        return Keys.KEY_CTX_SCHEME_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CtxSchemeRecord>> getKeys() {
        return Arrays.<UniqueKey<CtxSchemeRecord>>asList(Keys.KEY_CTX_SCHEME_PRIMARY, Keys.KEY_CTX_SCHEME_CTX_SCHEME_UK1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CtxSchemeRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CtxSchemeRecord, ?>>asList(Keys.CTX_SCHEME_CTX_CATEGORY_ID_FK, Keys.CTX_SCHEME_IBFK_1, Keys.CTX_SCHEME_CREATED_BY_FK, Keys.CTX_SCHEME_LAST_UPDATED_BY_FK);
    }

    public CtxCategory ctxCategory() {
        return new CtxCategory(this, Keys.CTX_SCHEME_CTX_CATEGORY_ID_FK);
    }

    public CodeList codeList() {
        return new CodeList(this, Keys.CTX_SCHEME_IBFK_1);
    }

    public AppUser ctxSchemeCreatedByFk() {
        return new AppUser(this, Keys.CTX_SCHEME_CREATED_BY_FK);
    }

    public AppUser ctxSchemeLastUpdatedByFk() {
        return new AppUser(this, Keys.CTX_SCHEME_LAST_UPDATED_BY_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxScheme as(String alias) {
        return new CtxScheme(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxScheme as(Name alias) {
        return new CtxScheme(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CtxScheme rename(String name) {
        return new CtxScheme(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CtxScheme rename(Name name) {
        return new CtxScheme(name, null);
    }
}
