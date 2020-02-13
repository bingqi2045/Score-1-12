/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
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
import org.oagi.srt.entity.jooq.tables.records.ReleaseRecord;


/**
 * The is table store the release information.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Release extends TableImpl<ReleaseRecord> {

    private static final long serialVersionUID = 1299452035;

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
    public final TableField<ReleaseRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "RELEASE_ID must be an incremental integer. RELEASE_ID that is more than another RELEASE_ID is interpreted to be released later than the other.");

    /**
     * The column <code>oagi.release.release_num</code>. Release number such has 10.0, 10.1, etc. 
     */
    public final TableField<ReleaseRecord, String> RELEASE_NUM = createField(DSL.name("release_num"), org.jooq.impl.SQLDataType.VARCHAR(45), this, "Release number such has 10.0, 10.1, etc. ");

    /**
     * The column <code>oagi.release.release_note</code>. Description or note associated with the release.
     */
    public final TableField<ReleaseRecord, String> RELEASE_NOTE = createField(DSL.name("release_note"), org.jooq.impl.SQLDataType.CLOB, this, "Description or note associated with the release.");

    /**
     * The column <code>oagi.release.namespace_id</code>. Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.
     */
    public final TableField<ReleaseRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.");

    /**
     * The column <code>oagi.release.created_by</code>. Foreign key to the APP_USER table identifying user who created the namespace.
     */
    public final TableField<ReleaseRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table identifying user who created the namespace.");

    /**
     * The column <code>oagi.release.last_updated_by</code>. Foreign key to the APP_USER table identifying the user who last updated the record.
     */
    public final TableField<ReleaseRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table identifying the user who last updated the record.");

    /**
     * The column <code>oagi.release.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ReleaseRecord, Timestamp> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.release.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ReleaseRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * The column <code>oagi.release.state</code>. 1 = DRAFT, 2 = FINAL. This the revision life cycle state of the Release.
     */
    public final TableField<ReleaseRecord, Integer> STATE = createField(DSL.name("state"), org.jooq.impl.SQLDataType.INTEGER, this, "1 = DRAFT, 2 = FINAL. This the revision life cycle state of the Release.");

    /**
     * Create a <code>oagi.release</code> table reference
     */
    public Release() {
        this(DSL.name("release"), null);
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

    private Release(Name alias, Table<ReleaseRecord> aliased) {
        this(alias, aliased, null);
    }

    private Release(Name alias, Table<ReleaseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The is table store the release information."));
    }

    public <O extends Record> Release(Table<O> child, ForeignKey<O, ReleaseRecord> key) {
        super(child, key, RELEASE);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.RELEASE_PRIMARY, Indexes.RELEASE_RELEASE_CREATED_BY_FK, Indexes.RELEASE_RELEASE_LAST_UPDATED_BY_FK, Indexes.RELEASE_RELEASE_NAMESPACE_ID_FK);
    }

    @Override
    public Identity<ReleaseRecord, ULong> getIdentity() {
        return Keys.IDENTITY_RELEASE;
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
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, String, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
