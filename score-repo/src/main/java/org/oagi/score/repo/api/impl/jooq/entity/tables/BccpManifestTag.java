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
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccpManifestTagRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccpManifestTag extends TableImpl<BccpManifestTagRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.bccp_manifest_tag</code>
     */
    public static final BccpManifestTag BCCP_MANIFEST_TAG = new BccpManifestTag();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BccpManifestTagRecord> getRecordType() {
        return BccpManifestTagRecord.class;
    }

    /**
     * The column <code>oagi.bccp_manifest_tag.bccp_manifest_id</code>.
     */
    public final TableField<BccpManifestTagRecord, ULong> BCCP_MANIFEST_ID = createField(DSL.name("bccp_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.bccp_manifest_tag.cc_tag_id</code>.
     */
    public final TableField<BccpManifestTagRecord, String> CC_TAG_ID = createField(DSL.name("cc_tag_id"), SQLDataType.CHAR(36), this, "");

    private BccpManifestTag(Name alias, Table<BccpManifestTagRecord> aliased) {
        this(alias, aliased, null);
    }

    private BccpManifestTag(Name alias, Table<BccpManifestTagRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.bccp_manifest_tag</code> table reference
     */
    public BccpManifestTag(String alias) {
        this(DSL.name(alias), BCCP_MANIFEST_TAG);
    }

    /**
     * Create an aliased <code>oagi.bccp_manifest_tag</code> table reference
     */
    public BccpManifestTag(Name alias) {
        this(alias, BCCP_MANIFEST_TAG);
    }

    /**
     * Create a <code>oagi.bccp_manifest_tag</code> table reference
     */
    public BccpManifestTag() {
        this(DSL.name("bccp_manifest_tag"), null);
    }

    public <O extends Record> BccpManifestTag(Table<O> child, ForeignKey<O, BccpManifestTagRecord> key) {
        super(child, key, BCCP_MANIFEST_TAG);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<BccpManifestTagRecord> getPrimaryKey() {
        return Keys.KEY_BCCP_MANIFEST_TAG_PRIMARY;
    }

    @Override
    public List<ForeignKey<BccpManifestTagRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BCCP_MANIFEST_TAG_BCCP_MANIFEST_ID_FK, Keys.BCCP_MANIFEST_TAG_CC_TAG_ID_FK);
    }

    private transient BccpManifest _bccpManifest;
    private transient CcTag _ccTag;

    /**
     * Get the implicit join path to the <code>oagi.bccp_manifest</code> table.
     */
    public BccpManifest bccpManifest() {
        if (_bccpManifest == null)
            _bccpManifest = new BccpManifest(this, Keys.BCCP_MANIFEST_TAG_BCCP_MANIFEST_ID_FK);

        return _bccpManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.cc_tag</code> table.
     */
    public CcTag ccTag() {
        if (_ccTag == null)
            _ccTag = new CcTag(this, Keys.BCCP_MANIFEST_TAG_CC_TAG_ID_FK);

        return _ccTag;
    }

    @Override
    public BccpManifestTag as(String alias) {
        return new BccpManifestTag(DSL.name(alias), this);
    }

    @Override
    public BccpManifestTag as(Name alias) {
        return new BccpManifestTag(alias, this);
    }

    @Override
    public BccpManifestTag as(Table<?> alias) {
        return new BccpManifestTag(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpManifestTag rename(String name) {
        return new BccpManifestTag(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpManifestTag rename(Name name) {
        return new BccpManifestTag(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BccpManifestTag rename(Table<?> name) {
        return new BccpManifestTag(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function2<? super ULong, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function2<? super ULong, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
