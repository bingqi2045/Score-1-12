/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


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
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.DtScManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtScManifest extends TableImpl<DtScManifestRecord> {

    private static final long serialVersionUID = -1838187493;

    /**
     * The reference instance of <code>oagi.dt_sc_manifest</code>
     */
    public static final DtScManifest DT_SC_MANIFEST = new DtScManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtScManifestRecord> getRecordType() {
        return DtScManifestRecord.class;
    }

    /**
     * The column <code>oagi.dt_sc_manifest.dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> DT_SC_MANIFEST_ID = createField(DSL.name("dt_sc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.release_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.dt_sc_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> DT_SC_ID = createField(DSL.name("dt_sc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.owner_dt_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> OWNER_DT_MANIFEST_ID = createField(DSL.name("owner_dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.conflict</code>. This indicates that there is a conflict between self and relationship.
     */
    public final TableField<DtScManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.dt_sc_manifest.prev_dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> PREV_DT_SC_MANIFEST_ID = createField(DSL.name("prev_dt_sc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.next_dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> NEXT_DT_SC_MANIFEST_ID = createField(DSL.name("next_dt_sc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.revision_id</code>. Indicates whether this is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public final TableField<DtScManifestRecord, Byte> REVISION_ID = createField(DSL.name("revision_id"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Indicates whether this is deprecated and should not be reused (i.e., no new reference to this record should be created).");

    /**
     * The column <code>oagi.dt_sc_manifest.replaced_manifest_by</code>. This alternative refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<DtScManifestRecord, ULong> REPLACED_MANIFEST_BY = createField(DSL.name("replaced_manifest_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This alternative refers to a replacement manifest if the record is deprecated.");

    /**
     * Create a <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest() {
        this(DSL.name("dt_sc_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest(String alias) {
        this(DSL.name(alias), DT_SC_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest(Name alias) {
        this(alias, DT_SC_MANIFEST);
    }

    private DtScManifest(Name alias, Table<DtScManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtScManifest(Name alias, Table<DtScManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> DtScManifest(Table<O> child, ForeignKey<O, DtScManifestRecord> key) {
        super(child, key, DT_SC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<DtScManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_DT_SC_MANIFEST;
    }

    @Override
    public UniqueKey<DtScManifestRecord> getPrimaryKey() {
        return Keys.KEY_DT_SC_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<DtScManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<DtScManifestRecord>>asList(Keys.KEY_DT_SC_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<DtScManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DtScManifestRecord, ?>>asList(Keys.DT_SC_MANIFEST_RELEASE_ID_FK, Keys.DT_SC_MANIFEST_DT_SC_ID_FK, Keys.DT_SC_MANIFEST_OWNER_DT_MANIFEST_ID_FK, Keys.DT_SC_PREV_DT_SC_MANIFEST_ID_FK, Keys.DT_SC_NEXT_DT_SC_MANIFEST_ID_FK, Keys.DT_SC_REPLACED_MANIFEST_BY_FK);
    }

    public Release release() {
        return new Release(this, Keys.DT_SC_MANIFEST_RELEASE_ID_FK);
    }

    public DtSc dtSc() {
        return new DtSc(this, Keys.DT_SC_MANIFEST_DT_SC_ID_FK);
    }

    public DtManifest dtManifest() {
        return new DtManifest(this, Keys.DT_SC_MANIFEST_OWNER_DT_MANIFEST_ID_FK);
    }

    public DtScManifest dtScPrevDtScManifestIdFk() {
        return new DtScManifest(this, Keys.DT_SC_PREV_DT_SC_MANIFEST_ID_FK);
    }

    public DtScManifest dtScNextDtScManifestIdFk() {
        return new DtScManifest(this, Keys.DT_SC_NEXT_DT_SC_MANIFEST_ID_FK);
    }

    public DtScManifest dtScReplacedManifestByFk() {
        return new DtScManifest(this, Keys.DT_SC_REPLACED_MANIFEST_BY_FK);
    }

    @Override
    public DtScManifest as(String alias) {
        return new DtScManifest(DSL.name(alias), this);
    }

    @Override
    public DtScManifest as(Name alias) {
        return new DtScManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DtScManifest rename(String name) {
        return new DtScManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtScManifest rename(Name name) {
        return new DtScManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, ULong, ULong, ULong, Byte, ULong, ULong, Byte, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
