/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
import org.oagi.srt.entity.jooq.tables.records.BccpReleaseManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccpReleaseManifest extends TableImpl<BccpReleaseManifestRecord> {

    private static final long serialVersionUID = 1006666709;

    /**
     * The reference instance of <code>oagi.bccp_release_manifest</code>
     */
    public static final BccpReleaseManifest BCCP_RELEASE_MANIFEST = new BccpReleaseManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BccpReleaseManifestRecord> getRecordType() {
        return BccpReleaseManifestRecord.class;
    }

    /**
     * The column <code>oagi.bccp_release_manifest.bccp_release_manifest_id</code>.
     */
    public final TableField<BccpReleaseManifestRecord, ULong> BCCP_RELEASE_MANIFEST_ID = createField(DSL.name("bccp_release_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.bccp_release_manifest.release_id</code>.
     */
    public final TableField<BccpReleaseManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bccp_release_manifest.bccp_id</code>.
     */
    public final TableField<BccpReleaseManifestRecord, ULong> BCCP_ID = createField(DSL.name("bccp_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bccp_release_manifest.bdt_id</code>.
     */
    public final TableField<BccpReleaseManifestRecord, ULong> BDT_ID = createField(DSL.name("bdt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>oagi.bccp_release_manifest</code> table reference
     */
    public BccpReleaseManifest() {
        this(DSL.name("bccp_release_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.bccp_release_manifest</code> table reference
     */
    public BccpReleaseManifest(String alias) {
        this(DSL.name(alias), BCCP_RELEASE_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.bccp_release_manifest</code> table reference
     */
    public BccpReleaseManifest(Name alias) {
        this(alias, BCCP_RELEASE_MANIFEST);
    }

    private BccpReleaseManifest(Name alias, Table<BccpReleaseManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private BccpReleaseManifest(Name alias, Table<BccpReleaseManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> BccpReleaseManifest(Table<O> child, ForeignKey<O, BccpReleaseManifestRecord> key) {
        super(child, key, BCCP_RELEASE_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BCCP_RELEASE_MANIFEST_BCCP_RELEASE_MANIFEST_BCCP_ID_FK, Indexes.BCCP_RELEASE_MANIFEST_BCCP_RELEASE_MANIFEST_BDT_ID_FK, Indexes.BCCP_RELEASE_MANIFEST_BCCP_RELEASE_MANIFEST_RELEASE_ID_FK, Indexes.BCCP_RELEASE_MANIFEST_PRIMARY);
    }

    @Override
    public Identity<BccpReleaseManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BCCP_RELEASE_MANIFEST;
    }

    @Override
    public UniqueKey<BccpReleaseManifestRecord> getPrimaryKey() {
        return Keys.KEY_BCCP_RELEASE_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<BccpReleaseManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<BccpReleaseManifestRecord>>asList(Keys.KEY_BCCP_RELEASE_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<BccpReleaseManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BccpReleaseManifestRecord, ?>>asList(Keys.BCCP_RELEASE_MANIFEST_RELEASE_ID_FK, Keys.BCCP_RELEASE_MANIFEST_BCCP_ID_FK, Keys.BCCP_RELEASE_MANIFEST_BDT_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.BCCP_RELEASE_MANIFEST_RELEASE_ID_FK);
    }

    public Bccp bccp() {
        return new Bccp(this, Keys.BCCP_RELEASE_MANIFEST_BCCP_ID_FK);
    }

    public Dt dt() {
        return new Dt(this, Keys.BCCP_RELEASE_MANIFEST_BDT_ID_FK);
    }

    @Override
    public BccpReleaseManifest as(String alias) {
        return new BccpReleaseManifest(DSL.name(alias), this);
    }

    @Override
    public BccpReleaseManifest as(Name alias) {
        return new BccpReleaseManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpReleaseManifest rename(String name) {
        return new BccpReleaseManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpReleaseManifest rename(Name name) {
        return new BccpReleaseManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
