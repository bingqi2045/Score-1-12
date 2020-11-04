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
import org.jooq.Row11;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ReleaseRecord;


/**
 * The is table store the release information.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Release extends TableImpl<ReleaseRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.release</code>
     */
    public static final Release RELEASE = new Release();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ReleaseRecord> getRecordType() {
        return ReleaseRecord.class;
    }

    /**
     * The column <code>oagi.release.release_id</code>. RELEASE_ID must be an incremental integer. RELEASE_ID that is more than another RELEASE_ID is interpreted to be released later than the other.
     */
    public final TableField<ReleaseRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "RELEASE_ID must be an incremental integer. RELEASE_ID that is more than another RELEASE_ID is interpreted to be released later than the other.");

    /**
     * The column <code>oagi.release.guid</code>. A globally unique identifier (GUID).
     */
    public final TableField<ReleaseRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.release.release_num</code>. Release number such has 10.0, 10.1, etc. 
     */
    public final TableField<ReleaseRecord, String> RELEASE_NUM = createField(DSL.name("release_num"), SQLDataType.VARCHAR(45), this, "Release number such has 10.0, 10.1, etc. ");

    /**
     * The column <code>oagi.release.release_note</code>. Description or note associated with the release.
     */
    public final TableField<ReleaseRecord, String> RELEASE_NOTE = createField(DSL.name("release_note"), SQLDataType.CLOB, this, "Description or note associated with the release.");

    /**
     * The column <code>oagi.release.release_license</code>. License associated with the release.
     */
    public final TableField<ReleaseRecord, String> RELEASE_LICENSE = createField(DSL.name("release_license"), SQLDataType.CLOB, this, "License associated with the release.");

    /**
     * The column <code>oagi.release.namespace_id</code>. Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.
     */
    public final TableField<ReleaseRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.");

    /**
     * The column <code>oagi.release.created_by</code>. Foreign key to the APP_USER table identifying user who created the namespace.
     */
    public final TableField<ReleaseRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table identifying user who created the namespace.");

    /**
     * The column <code>oagi.release.last_updated_by</code>. Foreign key to the APP_USER table identifying the user who last updated the record.
     */
    public final TableField<ReleaseRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table identifying the user who last updated the record.");

    /**
     * The column <code>oagi.release.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ReleaseRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.release.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ReleaseRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * The column <code>oagi.release.state</code>. This indicates the revision life cycle state of the Release.
     */
    public final TableField<ReleaseRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(20).defaultValue(DSL.inline("Initialized", SQLDataType.VARCHAR)), this, "This indicates the revision life cycle state of the Release.");

    private Release(Name alias, Table<ReleaseRecord> aliased) {
        this(alias, aliased, null);
    }

    private Release(Name alias, Table<ReleaseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The is table store the release information."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.release</code> table reference
     */
    public Release(String alias) {
        this(DSL.name(alias), RELEASE);
    }

    /**
     * Create an aliased <code>oagi.release</code> table reference
     */
    public Release(Name alias) {
        this(alias, RELEASE);
    }

    /**
     * Create a <code>oagi.release</code> table reference
     */
    public Release() {
        this(DSL.name("release"), null);
    }

    public <O extends Record> Release(Table<O> child, ForeignKey<O, ReleaseRecord> key) {
        super(child, key, RELEASE);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ReleaseRecord, ULong> getIdentity() {
        return (Identity<ReleaseRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<ReleaseRecord> getPrimaryKey() {
        return Keys.KEY_RELEASE_PRIMARY;
    }

    @Override
    public List<UniqueKey<ReleaseRecord>> getKeys() {
        return Arrays.<UniqueKey<ReleaseRecord>>asList(Keys.KEY_RELEASE_PRIMARY);
    }

    @Override
    public List<ForeignKey<ReleaseRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ReleaseRecord, ?>>asList(Keys.RELEASE_NAMESPACE_ID_FK, Keys.RELEASE_CREATED_BY_FK, Keys.RELEASE_LAST_UPDATED_BY_FK);
    }

    public Namespace namespace() {
        return new Namespace(this, Keys.RELEASE_NAMESPACE_ID_FK);
    }

    public AppUser releaseCreatedByFk() {
        return new AppUser(this, Keys.RELEASE_CREATED_BY_FK);
    }

    public AppUser releaseLastUpdatedByFk() {
        return new AppUser(this, Keys.RELEASE_LAST_UPDATED_BY_FK);
    }

    @Override
    public Release as(String alias) {
        return new Release(DSL.name(alias), this);
    }

    @Override
    public Release as(Name alias) {
        return new Release(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Release rename(String name) {
        return new Release(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Release rename(Name name) {
        return new Release(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<ULong, String, String, String, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
