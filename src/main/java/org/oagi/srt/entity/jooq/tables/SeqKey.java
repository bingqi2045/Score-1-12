/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.enums.SeqKeyType;
import org.oagi.srt.entity.jooq.tables.records.SeqKeyRecord;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SeqKey extends TableImpl<SeqKeyRecord> {

    private static final long serialVersionUID = -223152508;

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
    public final TableField<SeqKeyRecord, ULong> SEQ_KEY_ID = createField(DSL.name("seq_key_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.seq_key.from_acc_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> FROM_ACC_ID = createField(DSL.name("from_acc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.seq_key.type</code>.
     */
    public final TableField<SeqKeyRecord, SeqKeyType> TYPE = createField(DSL.name("type"), org.jooq.impl.SQLDataType.VARCHAR(4).asEnumDataType(org.oagi.srt.entity.jooq.enums.SeqKeyType.class), this, "");

    /**
     * The column <code>oagi.seq_key.cc_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> CC_ID = createField(DSL.name("cc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.seq_key.prev_seq_key_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> PREV_SEQ_KEY_ID = createField(DSL.name("prev_seq_key_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.seq_key.next_seq_key_id</code>.
     */
    public final TableField<SeqKeyRecord, ULong> NEXT_SEQ_KEY_ID = createField(DSL.name("next_seq_key_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * Create a <code>oagi.seq_key</code> table reference
     */
    public SeqKey() {
        this(DSL.name("seq_key"), null);
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

    private SeqKey(Name alias, Table<SeqKeyRecord> aliased) {
        this(alias, aliased, null);
    }

    private SeqKey(Name alias, Table<SeqKeyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> SeqKey(Table<O> child, ForeignKey<O, SeqKeyRecord> key) {
        super(child, key, SEQ_KEY);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SEQ_KEY_SEQ_KEY_FROM_ACC_ID);
    }

    @Override
    public Identity<SeqKeyRecord, ULong> getIdentity() {
        return Keys.IDENTITY_SEQ_KEY;
    }

    @Override
    public UniqueKey<SeqKeyRecord> getPrimaryKey() {
        return Keys.KEY_SEQ_KEY_PRIMARY;
    }

    @Override
    public List<UniqueKey<SeqKeyRecord>> getKeys() {
        return Arrays.<UniqueKey<SeqKeyRecord>>asList(Keys.KEY_SEQ_KEY_PRIMARY);
    }

    @Override
    public List<ForeignKey<SeqKeyRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SeqKeyRecord, ?>>asList(Keys.SEQ_KEY_FROM_ACC_ID_FK, Keys.SEQ_KEY_PREV_SEQ_KEY_ID_FK, Keys.SEQ_KEY_NEXT_SEQ_KEY_ID_FK);
    }

    public Acc acc() {
        return new Acc(this, Keys.SEQ_KEY_FROM_ACC_ID_FK);
    }

    public SeqKey seqKeyPrevSeqKeyIdFk() {
        return new SeqKey(this, Keys.SEQ_KEY_PREV_SEQ_KEY_ID_FK);
    }

    public SeqKey seqKeyNextSeqKeyIdFk() {
        return new SeqKey(this, Keys.SEQ_KEY_NEXT_SEQ_KEY_ID_FK);
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
    public Row6<ULong, ULong, SeqKeyType, ULong, ULong, ULong> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
