/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AccManifestTagRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccManifestTag extends TableImpl<AccManifestTagRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.acc_manifest_tag</code>
     */
    public static final AccManifestTag ACC_MANIFEST_TAG = new AccManifestTag();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AccManifestTagRecord> getRecordType() {
        return AccManifestTagRecord.class;
    }

    /**
     * The column <code>oagi.acc_manifest_tag.acc_manifest_id</code>.
     */
    public final TableField<AccManifestTagRecord, String> ACC_MANIFEST_ID = createField(DSL.name("acc_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.acc_manifest_tag.cc_tag_id</code>.
     */
    public final TableField<AccManifestTagRecord, String> CC_TAG_ID = createField(DSL.name("cc_tag_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    private AccManifestTag(Name alias, Table<AccManifestTagRecord> aliased) {
        this(alias, aliased, null);
    }

    private AccManifestTag(Name alias, Table<AccManifestTagRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.acc_manifest_tag</code> table reference
     */
    public AccManifestTag(String alias) {
        this(DSL.name(alias), ACC_MANIFEST_TAG);
    }

    /**
     * Create an aliased <code>oagi.acc_manifest_tag</code> table reference
     */
    public AccManifestTag(Name alias) {
        this(alias, ACC_MANIFEST_TAG);
    }

    /**
     * Create a <code>oagi.acc_manifest_tag</code> table reference
     */
    public AccManifestTag() {
        this(DSL.name("acc_manifest_tag"), null);
    }

    public <O extends Record> AccManifestTag(Table<O> child, ForeignKey<O, AccManifestTagRecord> key) {
        super(child, key, ACC_MANIFEST_TAG);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<ForeignKey<AccManifestTagRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ACC_MANIFEST_TAG_ACC_MANIFEST_ID_FK, Keys.ACC_MANIFEST_TAG_CC_TAG_ID_FK);
    }

    private transient AccManifest _accManifest;
    private transient CcTag _ccTag;

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table.
     */
    public AccManifest accManifest() {
        if (_accManifest == null)
            _accManifest = new AccManifest(this, Keys.ACC_MANIFEST_TAG_ACC_MANIFEST_ID_FK);

        return _accManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.cc_tag</code> table.
     */
    public CcTag ccTag() {
        if (_ccTag == null)
            _ccTag = new CcTag(this, Keys.ACC_MANIFEST_TAG_CC_TAG_ID_FK);

        return _ccTag;
    }

    @Override
    public AccManifestTag as(String alias) {
        return new AccManifestTag(DSL.name(alias), this);
    }

    @Override
    public AccManifestTag as(Name alias) {
        return new AccManifestTag(alias, this);
    }

    @Override
    public AccManifestTag as(Table<?> alias) {
        return new AccManifestTag(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AccManifestTag rename(String name) {
        return new AccManifestTag(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AccManifestTag rename(Name name) {
        return new AccManifestTag(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AccManifestTag rename(Table<?> name) {
        return new AccManifestTag(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
