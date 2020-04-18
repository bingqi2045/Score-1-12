/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.AsccManifestRecord;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class AsccManifest extends TableImpl<AsccManifestRecord> {

    private static final long serialVersionUID = -480552459;

    /**
     * The reference instance of <code>oagi.ascc_manifest</code>
     */
    public static final AsccManifest ASCC_MANIFEST = new AsccManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsccManifestRecord> getRecordType() {
        return AsccManifestRecord.class;
    }

    /**
     * The column <code>oagi.ascc_manifest.ascc_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> ASCC_MANIFEST_ID = createField(DSL.name("ascc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.ascc_manifest.release_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.ascc_manifest.ascc_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> ASCC_ID = createField(DSL.name("ascc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.ascc_manifest.from_acc_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> FROM_ACC_MANIFEST_ID = createField(DSL.name("from_acc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.ascc_manifest.to_asccp_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> TO_ASCCP_MANIFEST_ID = createField(DSL.name("to_asccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>oagi.ascc_manifest</code> table reference
     */
    public AsccManifest() {
        this(DSL.name("ascc_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.ascc_manifest</code> table reference
     */
    public AsccManifest(String alias) {
        this(DSL.name(alias), ASCC_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.ascc_manifest</code> table reference
     */
    public AsccManifest(Name alias) {
        this(alias, ASCC_MANIFEST);
    }

    private AsccManifest(Name alias, Table<AsccManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private AsccManifest(Name alias, Table<AsccManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> AsccManifest(Table<O> child, ForeignKey<O, AsccManifestRecord> key) {
        super(child, key, ASCC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<AsccManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_ASCC_MANIFEST;
    }

    @Override
    public UniqueKey<AsccManifestRecord> getPrimaryKey() {
        return Keys.KEY_ASCC_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<AsccManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<AsccManifestRecord>>asList(Keys.KEY_ASCC_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<AsccManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AsccManifestRecord, ?>>asList(Keys.ASCC_MANIFEST_RELEASE_ID_FK, Keys.ASCC_MANIFEST_ASCC_ID_FK, Keys.ASCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK, Keys.ASCC_MANIFEST_TO_ASCCP_MANIFEST_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.ASCC_MANIFEST_RELEASE_ID_FK);
    }

    public Ascc ascc() {
        return new Ascc(this, Keys.ASCC_MANIFEST_ASCC_ID_FK);
    }

    public AccManifest accManifest() {
        return new AccManifest(this, Keys.ASCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK);
    }

    public AsccpManifest asccpManifest() {
        return new AsccpManifest(this, Keys.ASCC_MANIFEST_TO_ASCCP_MANIFEST_ID_FK);
    }

    @Override
    public AsccManifest as(String alias) {
        return new AsccManifest(DSL.name(alias), this);
    }

    @Override
    public AsccManifest as(Name alias) {
        return new AsccManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccManifest rename(String name) {
        return new AsccManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccManifest rename(Name name) {
        return new AsccManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<ULong, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
