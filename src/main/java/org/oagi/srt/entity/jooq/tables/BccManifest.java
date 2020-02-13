/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
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
import org.oagi.srt.entity.jooq.tables.records.BccManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccManifest extends TableImpl<BccManifestRecord> {

    private static final long serialVersionUID = 1633307371;

    /**
     * The reference instance of <code>oagi.bcc_manifest</code>
     */
    public static final BccManifest BCC_MANIFEST = new BccManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BccManifestRecord> getRecordType() {
        return BccManifestRecord.class;
    }

    /**
     * The column <code>oagi.bcc_manifest.bcc_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, ULong> BCC_MANIFEST_ID = createField(DSL.name("bcc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.bcc_manifest.release_id</code>.
     */
    public final TableField<BccManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bcc_manifest.bcc_id</code>.
     */
    public final TableField<BccManifestRecord, ULong> BCC_ID = createField(DSL.name("bcc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bcc_manifest.from_acc_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, ULong> FROM_ACC_MANIFEST_ID = createField(DSL.name("from_acc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bcc_manifest.to_bccp_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, ULong> TO_BCCP_MANIFEST_ID = createField(DSL.name("to_bccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>oagi.bcc_manifest</code> table reference
     */
    public BccManifest() {
        this(DSL.name("bcc_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.bcc_manifest</code> table reference
     */
    public BccManifest(String alias) {
        this(DSL.name(alias), BCC_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.bcc_manifest</code> table reference
     */
    public BccManifest(Name alias) {
        this(alias, BCC_MANIFEST);
    }

    private BccManifest(Name alias, Table<BccManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private BccManifest(Name alias, Table<BccManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> BccManifest(Table<O> child, ForeignKey<O, BccManifestRecord> key) {
        super(child, key, BCC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BCC_MANIFEST_BCC_MANIFEST_BCC_ID_FK, Indexes.BCC_MANIFEST_BCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK, Indexes.BCC_MANIFEST_BCC_MANIFEST_RELEASE_ID_FK, Indexes.BCC_MANIFEST_BCC_MANIFEST_TO_BCCP_MANIFEST_ID_FK, Indexes.BCC_MANIFEST_PRIMARY);
    }

    @Override
    public Identity<BccManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BCC_MANIFEST;
    }

    @Override
    public UniqueKey<BccManifestRecord> getPrimaryKey() {
        return Keys.KEY_BCC_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<BccManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<BccManifestRecord>>asList(Keys.KEY_BCC_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<BccManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BccManifestRecord, ?>>asList(Keys.BCC_MANIFEST_RELEASE_ID_FK, Keys.BCC_MANIFEST_BCC_ID_FK, Keys.BCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK, Keys.BCC_MANIFEST_TO_BCCP_MANIFEST_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.BCC_MANIFEST_RELEASE_ID_FK);
    }

    public Bcc bcc() {
        return new Bcc(this, Keys.BCC_MANIFEST_BCC_ID_FK);
    }

    public AccManifest accManifest() {
        return new AccManifest(this, Keys.BCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK);
    }

    public BccpManifest bccpManifest() {
        return new BccpManifest(this, Keys.BCC_MANIFEST_TO_BCCP_MANIFEST_ID_FK);
    }

    @Override
    public BccManifest as(String alias) {
        return new BccManifest(DSL.name(alias), this);
    }

    @Override
    public BccManifest as(Name alias) {
        return new BccManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BccManifest rename(String name) {
        return new BccManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BccManifest rename(Name name) {
        return new BccManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<ULong, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
