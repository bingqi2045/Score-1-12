/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.SeqKeyRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SeqKey extends TableImpl<SeqKeyRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.seq_key</code>
     */
    public static final SeqKey SEQ_KEY = new SeqKey();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SeqKeyRecord> getRecordType() {
        return SeqKeyRecord.class;
    }

    /**
     * The column <code>oagi.seq_key.seq_key_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> SEQ_KEY_ID = createField(DSL.name("seq_key_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.seq_key.from_acc_manifest_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> FROM_ACC_MANIFEST_ID = createField(DSL.name("from_acc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.seq_key.ascc_manifest_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> ASCC_MANIFEST_ID = createField(DSL.name("ascc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.seq_key.bcc_manifest_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> BCC_MANIFEST_ID = createField(DSL.name("bcc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.seq_key.prev_seq_key_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> PREV_SEQ_KEY_ID = createField(DSL.name("prev_seq_key_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.seq_key.next_seq_key_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> NEXT_SEQ_KEY_ID = createField(DSL.name("next_seq_key_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    private SeqKey(Name alias, Table<SeqKeyRecord> aliased) {
        this(alias, aliased, null);
    }

    private SeqKey(Name alias, Table<SeqKeyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.seq_key</code> table reference
     */
    public SeqKey(String alias) {
        this(DSL.name(alias), SEQ_KEY);
    }

    /**
     * Create an aliased <code>oagi.seq_key</code> table reference
     */
    public SeqKey(Name alias) {
        this(alias, SEQ_KEY);
    }

    /**
     * Create a <code>oagi.seq_key</code> table reference
     */
    public SeqKey() {
        this(DSL.name("seq_key"), null);
    }

    public <O extends Record> SeqKey(Table<O> child, ForeignKey<O, SeqKeyRecord> key) {
        super(child, key, SEQ_KEY);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.SEQ_KEY_SEQ_KEY_ASCC_MANIFEST_ID, Indexes.SEQ_KEY_SEQ_KEY_BCC_MANIFEST_ID, Indexes.SEQ_KEY_SEQ_KEY_FROM_ACC_MANIFEST_ID);
    }

    @Override
    public Identity<SeqKeyRecord, ULong> getIdentity() {
        return (Identity<SeqKeyRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<SeqKeyRecord> getPrimaryKey() {
        return Keys.KEY_SEQ_KEY_PRIMARY;
    }

    @Override
    public List<ForeignKey<SeqKeyRecord, ?>> getReferences() {
        return Arrays.asList(Keys.SEQ_KEY_FROM_ACC_MANIFEST_ID_FK, Keys.SEQ_KEY_ASCC_MANIFEST_ID_FK, Keys.SEQ_KEY_BCC_MANIFEST_ID_FK, Keys.SEQ_KEY_PREV_SEQ_KEY_ID_FK, Keys.SEQ_KEY_NEXT_SEQ_KEY_ID_FK);
    }

    private transient AccManifest _accManifest;
    private transient AsccManifest _asccManifest;
    private transient BccManifest _bccManifest;
    private transient SeqKey _seqKeyPrevSeqKeyIdFk;
    private transient SeqKey _seqKeyNextSeqKeyIdFk;

    public AccManifest accManifest() {
        if (_accManifest == null)
            _accManifest = new AccManifest(this, Keys.SEQ_KEY_FROM_ACC_MANIFEST_ID_FK);

        return _accManifest;
    }

    public AsccManifest asccManifest() {
        if (_asccManifest == null)
            _asccManifest = new AsccManifest(this, Keys.SEQ_KEY_ASCC_MANIFEST_ID_FK);

        return _asccManifest;
    }

    public BccManifest bccManifest() {
        if (_bccManifest == null)
            _bccManifest = new BccManifest(this, Keys.SEQ_KEY_BCC_MANIFEST_ID_FK);

        return _bccManifest;
    }

    public SeqKey seqKeyPrevSeqKeyIdFk() {
        if (_seqKeyPrevSeqKeyIdFk == null)
            _seqKeyPrevSeqKeyIdFk = new SeqKey(this, Keys.SEQ_KEY_PREV_SEQ_KEY_ID_FK);

        return _seqKeyPrevSeqKeyIdFk;
    }

    public SeqKey seqKeyNextSeqKeyIdFk() {
        if (_seqKeyNextSeqKeyIdFk == null)
            _seqKeyNextSeqKeyIdFk = new SeqKey(this, Keys.SEQ_KEY_NEXT_SEQ_KEY_ID_FK);

        return _seqKeyNextSeqKeyIdFk;
    }

    @Override
    public SeqKey as(String alias) {
        return new SeqKey(DSL.name(alias), this);
    }

    @Override
    public SeqKey as(Name alias) {
        return new SeqKey(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SeqKey rename(String name) {
        return new SeqKey(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SeqKey rename(Name name) {
        return new SeqKey(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<ULong, ULong, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
