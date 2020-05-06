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
import org.jooq.Row9;
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
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccpManifest extends TableImpl<BccpManifestRecord> {

    private static final long serialVersionUID = -1380636219;

    /**
     * The reference instance of <code>oagi.bccp_manifest</code>
     */
    public static final BccpManifest BCCP_MANIFEST = new BccpManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BccpManifestRecord> getRecordType() {
        return BccpManifestRecord.class;
    }

    /**
     * The column <code>oagi.bccp_manifest.bccp_manifest_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> BCCP_MANIFEST_ID = createField(DSL.name("bccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.bccp_manifest.release_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bccp_manifest.module_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.bccp_manifest.bccp_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> BCCP_ID = createField(DSL.name("bccp_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bccp_manifest.bdt_manifest_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> BDT_MANIFEST_ID = createField(DSL.name("bdt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bccp_manifest.conflict</code>. This indicates that there is a conflict between self and relationship.
     */
    public final TableField<BccpManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.bccp_manifest.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public final TableField<BccpManifestRecord, ULong> REVISION_ID = createField(DSL.name("revision_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A foreign key pointed to revision for the current record.");

    /**
     * The column <code>oagi.bccp_manifest.prev_bccp_manifest_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> PREV_BCCP_MANIFEST_ID = createField(DSL.name("prev_bccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.bccp_manifest.next_bccp_manifest_id</code>.
     */
    public final TableField<BccpManifestRecord, ULong> NEXT_BCCP_MANIFEST_ID = createField(DSL.name("next_bccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * Create a <code>oagi.bccp_manifest</code> table reference
     */
    public BccpManifest() {
        this(DSL.name("bccp_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.bccp_manifest</code> table reference
     */
    public BccpManifest(String alias) {
        this(DSL.name(alias), BCCP_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.bccp_manifest</code> table reference
     */
    public BccpManifest(Name alias) {
        this(alias, BCCP_MANIFEST);
    }

    private BccpManifest(Name alias, Table<BccpManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private BccpManifest(Name alias, Table<BccpManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> BccpManifest(Table<O> child, ForeignKey<O, BccpManifestRecord> key) {
        super(child, key, BCCP_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<BccpManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BCCP_MANIFEST;
    }

    @Override
    public UniqueKey<BccpManifestRecord> getPrimaryKey() {
        return Keys.KEY_BCCP_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<BccpManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<BccpManifestRecord>>asList(Keys.KEY_BCCP_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<BccpManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BccpManifestRecord, ?>>asList(Keys.BCCP_MANIFEST_RELEASE_ID_FK, Keys.BCCP_MANIFEST_MODULE_ID_FK, Keys.BCCP_MANIFEST_BCCP_ID_FK, Keys.BCCP_MANIFEST_BDT_MANIFEST_ID_FK, Keys.BCCP_MANIFEST_REVISION_ID_FK, Keys.BCCP_MANIFEST_PREV_BCCP_MANIFEST_ID_FK, Keys.BCCP_MANIFEST_NEXT_BCCP_MANIFEST_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.BCCP_MANIFEST_RELEASE_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.BCCP_MANIFEST_MODULE_ID_FK);
    }

    public Bccp bccp() {
        return new Bccp(this, Keys.BCCP_MANIFEST_BCCP_ID_FK);
    }

    public DtManifest dtManifest() {
        return new DtManifest(this, Keys.BCCP_MANIFEST_BDT_MANIFEST_ID_FK);
    }

    public Revision revision() {
        return new Revision(this, Keys.BCCP_MANIFEST_REVISION_ID_FK);
    }

    public BccpManifest bccpManifestPrevBccpManifestIdFk() {
        return new BccpManifest(this, Keys.BCCP_MANIFEST_PREV_BCCP_MANIFEST_ID_FK);
    }

    public BccpManifest bccpManifestNextBccpManifestIdFk() {
        return new BccpManifest(this, Keys.BCCP_MANIFEST_NEXT_BCCP_MANIFEST_ID_FK);
    }

    @Override
    public BccpManifest as(String alias) {
        return new BccpManifest(DSL.name(alias), this);
    }

    @Override
    public BccpManifest as(Name alias) {
        return new BccpManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpManifest rename(String name) {
        return new BccpManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpManifest rename(Name name) {
        return new BccpManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
