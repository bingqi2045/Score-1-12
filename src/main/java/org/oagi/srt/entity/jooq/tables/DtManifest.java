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
import org.oagi.srt.entity.jooq.tables.records.DtManifestRecord;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtManifest extends TableImpl<DtManifestRecord> {

    private static final long serialVersionUID = -1992182031;

    /**
     * The reference instance of <code>oagi.dt_manifest</code>
     */
    public static final DtManifest DT_MANIFEST = new DtManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtManifestRecord> getRecordType() {
        return DtManifestRecord.class;
    }

    /**
     * The column <code>oagi.dt_manifest.dt_manifest_id</code>.
     */
    public final TableField<DtManifestRecord, ULong> DT_MANIFEST_ID = createField(DSL.name("dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.dt_manifest.release_id</code>.
     */
    public final TableField<DtManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_manifest.dt_id</code>.
     */
    public final TableField<DtManifestRecord, ULong> DT_ID = createField(DSL.name("dt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_manifest.conflict</code>. This indicates that there is a conflict between self and relationship.
     */
    public final TableField<DtManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.dt_manifest.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public final TableField<DtManifestRecord, ULong> REVISION_ID = createField(DSL.name("revision_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A foreign key pointed to revision for the current record.");

    /**
     * The column <code>oagi.dt_manifest.prev_dt_manifest_id</code>.
     */
    public final TableField<DtManifestRecord, ULong> PREV_DT_MANIFEST_ID = createField(DSL.name("prev_dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.dt_manifest.next_dt_manifest_id</code>.
     */
    public final TableField<DtManifestRecord, ULong> NEXT_DT_MANIFEST_ID = createField(DSL.name("next_dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * Create a <code>oagi.dt_manifest</code> table reference
     */
    public DtManifest() {
        this(DSL.name("dt_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.dt_manifest</code> table reference
     */
    public DtManifest(String alias) {
        this(DSL.name(alias), DT_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.dt_manifest</code> table reference
     */
    public DtManifest(Name alias) {
        this(alias, DT_MANIFEST);
    }

    private DtManifest(Name alias, Table<DtManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtManifest(Name alias, Table<DtManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> DtManifest(Table<O> child, ForeignKey<O, DtManifestRecord> key) {
        super(child, key, DT_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<DtManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_DT_MANIFEST;
    }

    @Override
    public UniqueKey<DtManifestRecord> getPrimaryKey() {
        return Keys.KEY_DT_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<DtManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<DtManifestRecord>>asList(Keys.KEY_DT_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<DtManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DtManifestRecord, ?>>asList(Keys.DT_MANIFEST_RELEASE_ID_FK, Keys.DT_MANIFEST_DT_ID_FK, Keys.DT_MANIFEST_REVISION_ID_FK, Keys.DT_MANIFEST_PREV_DT_MANIFEST_ID_FK, Keys.DT_MANIFEST_NEXT_DT_MANIFEST_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.DT_MANIFEST_RELEASE_ID_FK);
    }

    public Dt dt() {
        return new Dt(this, Keys.DT_MANIFEST_DT_ID_FK);
    }

    public Revision revision() {
        return new Revision(this, Keys.DT_MANIFEST_REVISION_ID_FK);
    }

    public DtManifest dtManifestPrevDtManifestIdFk() {
        return new DtManifest(this, Keys.DT_MANIFEST_PREV_DT_MANIFEST_ID_FK);
    }

    public DtManifest dtManifestNextDtManifestIdFk() {
        return new DtManifest(this, Keys.DT_MANIFEST_NEXT_DT_MANIFEST_ID_FK);
    }

    @Override
    public DtManifest as(String alias) {
        return new DtManifest(DSL.name(alias), this);
    }

    @Override
    public DtManifest as(Name alias) {
        return new DtManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DtManifest rename(String name) {
        return new DtManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtManifest rename(Name name) {
        return new DtManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
