/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function10;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row10;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccManifest extends TableImpl<BccManifestRecord> {

    private static final long serialVersionUID = 1L;

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
     * The column <code>oagi.bcc_manifest.bcc_manifest_id</code>. Primary,
     * internal database key.
     */
    public final TableField<BccManifestRecord, String> BCC_MANIFEST_ID = createField(DSL.name("bcc_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.bcc_manifest.release_id</code>.
     */
    public final TableField<BccManifestRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.bcc_manifest.bcc_id</code>.
     */
    public final TableField<BccManifestRecord, String> BCC_ID = createField(DSL.name("bcc_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.bcc_manifest.seq_key_id</code>.
     */
    public final TableField<BccManifestRecord, String> SEQ_KEY_ID = createField(DSL.name("seq_key_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.bcc_manifest.from_acc_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, String> FROM_ACC_MANIFEST_ID = createField(DSL.name("from_acc_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.bcc_manifest.to_bccp_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, String> TO_BCCP_MANIFEST_ID = createField(DSL.name("to_bccp_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.bcc_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public final TableField<BccManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.bcc_manifest.replacement_bcc_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<BccManifestRecord, String> REPLACEMENT_BCC_MANIFEST_ID = createField(DSL.name("replacement_bcc_manifest_id"), SQLDataType.CHAR(36), this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column <code>oagi.bcc_manifest.prev_bcc_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, String> PREV_BCC_MANIFEST_ID = createField(DSL.name("prev_bcc_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.bcc_manifest.next_bcc_manifest_id</code>.
     */
    public final TableField<BccManifestRecord, String> NEXT_BCC_MANIFEST_ID = createField(DSL.name("next_bcc_manifest_id"), SQLDataType.CHAR(36), this, "");

    private BccManifest(Name alias, Table<BccManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private BccManifest(Name alias, Table<BccManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
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

    /**
     * Create a <code>oagi.bcc_manifest</code> table reference
     */
    public BccManifest() {
        this(DSL.name("bcc_manifest"), null);
    }

    public <O extends Record> BccManifest(Table<O> child, ForeignKey<O, BccManifestRecord> key) {
        super(child, key, BCC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<BccManifestRecord> getPrimaryKey() {
        return Keys.KEY_BCC_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<BccManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BCC_MANIFEST_RELEASE_ID_FK, Keys.BCC_MANIFEST_BCC_ID_FK, Keys.BCC_MANIFEST_SEQ_KEY_ID_FK, Keys.BCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK, Keys.BCC_MANIFEST_TO_BCCP_MANIFEST_ID_FK, Keys.BCC_MANIFEST_REPLACEMENT_BCC_MANIFEST_ID_FK, Keys.BCC_MANIFEST_PREV_BCC_MANIFEST_ID_FK, Keys.BCC_MANIFEST_NEXT_BCC_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient Bcc _bcc;
    private transient SeqKey _seqKey;
    private transient AccManifest _accManifest;
    private transient BccpManifest _bccpManifest;
    private transient BccManifest _bccManifestReplacementBccManifestIdFk;
    private transient BccManifest _bccManifestPrevBccManifestIdFk;
    private transient BccManifest _bccManifestNextBccManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.BCC_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.bcc</code> table.
     */
    public Bcc bcc() {
        if (_bcc == null)
            _bcc = new Bcc(this, Keys.BCC_MANIFEST_BCC_ID_FK);

        return _bcc;
    }

    /**
     * Get the implicit join path to the <code>oagi.seq_key</code> table.
     */
    public SeqKey seqKey() {
        if (_seqKey == null)
            _seqKey = new SeqKey(this, Keys.BCC_MANIFEST_SEQ_KEY_ID_FK);

        return _seqKey;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table.
     */
    public AccManifest accManifest() {
        if (_accManifest == null)
            _accManifest = new AccManifest(this, Keys.BCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK);

        return _accManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.bccp_manifest</code> table.
     */
    public BccpManifest bccpManifest() {
        if (_bccpManifest == null)
            _bccpManifest = new BccpManifest(this, Keys.BCC_MANIFEST_TO_BCCP_MANIFEST_ID_FK);

        return _bccpManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.bcc_manifest</code> table,
     * via the <code>bcc_manifest_replacement_bcc_manifest_id_fk</code> key.
     */
    public BccManifest bccManifestReplacementBccManifestIdFk() {
        if (_bccManifestReplacementBccManifestIdFk == null)
            _bccManifestReplacementBccManifestIdFk = new BccManifest(this, Keys.BCC_MANIFEST_REPLACEMENT_BCC_MANIFEST_ID_FK);

        return _bccManifestReplacementBccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.bcc_manifest</code> table,
     * via the <code>bcc_manifest_prev_bcc_manifest_id_fk</code> key.
     */
    public BccManifest bccManifestPrevBccManifestIdFk() {
        if (_bccManifestPrevBccManifestIdFk == null)
            _bccManifestPrevBccManifestIdFk = new BccManifest(this, Keys.BCC_MANIFEST_PREV_BCC_MANIFEST_ID_FK);

        return _bccManifestPrevBccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.bcc_manifest</code> table,
     * via the <code>bcc_manifest_next_bcc_manifest_id_fk</code> key.
     */
    public BccManifest bccManifestNextBccManifestIdFk() {
        if (_bccManifestNextBccManifestIdFk == null)
            _bccManifestNextBccManifestIdFk = new BccManifest(this, Keys.BCC_MANIFEST_NEXT_BCC_MANIFEST_ID_FK);

        return _bccManifestNextBccManifestIdFk;
    }

    @Override
    public BccManifest as(String alias) {
        return new BccManifest(DSL.name(alias), this);
    }

    @Override
    public BccManifest as(Name alias) {
        return new BccManifest(alias, this);
    }

    @Override
    public BccManifest as(Table<?> alias) {
        return new BccManifest(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public BccManifest rename(Table<?> name) {
        return new BccManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, String, Byte, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
