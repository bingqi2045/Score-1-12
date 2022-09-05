/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function6;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BlobContentManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BlobContentManifest extends TableImpl<BlobContentManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.blob_content_manifest</code>
     */
    public static final BlobContentManifest BLOB_CONTENT_MANIFEST = new BlobContentManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BlobContentManifestRecord> getRecordType() {
        return BlobContentManifestRecord.class;
    }

    /**
     * The column
     * <code>oagi.blob_content_manifest.blob_content_manifest_id</code>.
     * Primary, internal database key.
     */
    public final TableField<BlobContentManifestRecord, String> BLOB_CONTENT_MANIFEST_ID = createField(DSL.name("blob_content_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.blob_content_manifest.blob_content_id</code>.
     */
    public final TableField<BlobContentManifestRecord, String> BLOB_CONTENT_ID = createField(DSL.name("blob_content_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.blob_content_manifest.release_id</code>. Foreign
     * key to the RELEASE table.
     */
    public final TableField<BlobContentManifestRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the RELEASE table.");

    /**
     * The column <code>oagi.blob_content_manifest.conflict</code>. This
     * indicates that there is a conflict between self and relationship.
     */
    public final TableField<BlobContentManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column
     * <code>oagi.blob_content_manifest.prev_blob_content_manifest_id</code>.
     */
    public final TableField<BlobContentManifestRecord, String> PREV_BLOB_CONTENT_MANIFEST_ID = createField(DSL.name("prev_blob_content_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column
     * <code>oagi.blob_content_manifest.next_blob_content_manifest_id</code>.
     */
    public final TableField<BlobContentManifestRecord, String> NEXT_BLOB_CONTENT_MANIFEST_ID = createField(DSL.name("next_blob_content_manifest_id"), SQLDataType.CHAR(36), this, "");

    private BlobContentManifest(Name alias, Table<BlobContentManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private BlobContentManifest(Name alias, Table<BlobContentManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.blob_content_manifest</code> table reference
     */
    public BlobContentManifest(String alias) {
        this(DSL.name(alias), BLOB_CONTENT_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.blob_content_manifest</code> table reference
     */
    public BlobContentManifest(Name alias) {
        this(alias, BLOB_CONTENT_MANIFEST);
    }

    /**
     * Create a <code>oagi.blob_content_manifest</code> table reference
     */
    public BlobContentManifest() {
        this(DSL.name("blob_content_manifest"), null);
    }

    public <O extends Record> BlobContentManifest(Table<O> child, ForeignKey<O, BlobContentManifestRecord> key) {
        super(child, key, BLOB_CONTENT_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<BlobContentManifestRecord> getPrimaryKey() {
        return Keys.KEY_BLOB_CONTENT_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<BlobContentManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BLOB_CONTENT_MANIFEST_BLOB_CONTENT_ID_FK, Keys.BLOB_CONTENT_MANIFEST_RELEASE_ID_FK, Keys.BLOB_CONTENT_MANIFEST_PREV_BLOB_CONTENT_MANIFEST_ID_FK, Keys.BLOB_CONTENT_MANIFEST_NEXT_BLOB_CONTENT_MANIFEST_ID_FK);
    }

    private transient BlobContent _blobContent;
    private transient Release _release;
    private transient BlobContentManifest _blobContentManifestPrevBlobContentManifestIdFk;
    private transient BlobContentManifest _blobContentManifestNextBlobContentManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.blob_content</code> table.
     */
    public BlobContent blobContent() {
        if (_blobContent == null)
            _blobContent = new BlobContent(this, Keys.BLOB_CONTENT_MANIFEST_BLOB_CONTENT_ID_FK);

        return _blobContent;
    }

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.BLOB_CONTENT_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.blob_content_manifest</code>
     * table, via the
     * <code>blob_content_manifest_prev_blob_content_manifest_id_fk</code> key.
     */
    public BlobContentManifest blobContentManifestPrevBlobContentManifestIdFk() {
        if (_blobContentManifestPrevBlobContentManifestIdFk == null)
            _blobContentManifestPrevBlobContentManifestIdFk = new BlobContentManifest(this, Keys.BLOB_CONTENT_MANIFEST_PREV_BLOB_CONTENT_MANIFEST_ID_FK);

        return _blobContentManifestPrevBlobContentManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.blob_content_manifest</code>
     * table, via the
     * <code>blob_content_manifest_next_blob_content_manifest_id_fk</code> key.
     */
    public BlobContentManifest blobContentManifestNextBlobContentManifestIdFk() {
        if (_blobContentManifestNextBlobContentManifestIdFk == null)
            _blobContentManifestNextBlobContentManifestIdFk = new BlobContentManifest(this, Keys.BLOB_CONTENT_MANIFEST_NEXT_BLOB_CONTENT_MANIFEST_ID_FK);

        return _blobContentManifestNextBlobContentManifestIdFk;
    }

    @Override
    public BlobContentManifest as(String alias) {
        return new BlobContentManifest(DSL.name(alias), this);
    }

    @Override
    public BlobContentManifest as(Name alias) {
        return new BlobContentManifest(alias, this);
    }

    @Override
    public BlobContentManifest as(Table<?> alias) {
        return new BlobContentManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BlobContentManifest rename(String name) {
        return new BlobContentManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BlobContentManifest rename(Name name) {
        return new BlobContentManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BlobContentManifest rename(Table<?> name) {
        return new BlobContentManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, Byte, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function6<? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function6<? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
