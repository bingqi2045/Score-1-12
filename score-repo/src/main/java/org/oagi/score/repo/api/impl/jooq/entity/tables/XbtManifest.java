/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function7;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row7;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.XbtManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class XbtManifest extends TableImpl<XbtManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.xbt_manifest</code>
     */
    public static final XbtManifest XBT_MANIFEST = new XbtManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<XbtManifestRecord> getRecordType() {
        return XbtManifestRecord.class;
    }

    /**
     * The column <code>oagi.xbt_manifest.xbt_manifest_id</code>. Primary,
     * internal database key.
     */
    public final TableField<XbtManifestRecord, String> XBT_MANIFEST_ID = createField(DSL.name("xbt_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.xbt_manifest.release_id</code>. Foreign key to the
     * RELEASE table.
     */
    public final TableField<XbtManifestRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the RELEASE table.");

    /**
     * The column <code>oagi.xbt_manifest.xbt_id</code>. Foreign key to the XBT
     * table.
     */
    public final TableField<XbtManifestRecord, String> XBT_ID = createField(DSL.name("xbt_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the XBT table.");

    /**
     * The column <code>oagi.xbt_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public final TableField<XbtManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.xbt_manifest.log_id</code>. A foreign key pointed
     * to a log for the current record.
     */
    public final TableField<XbtManifestRecord, String> LOG_ID = createField(DSL.name("log_id"), SQLDataType.CHAR(36), this, "A foreign key pointed to a log for the current record.");

    /**
     * The column <code>oagi.xbt_manifest.prev_xbt_manifest_id</code>.
     */
    public final TableField<XbtManifestRecord, String> PREV_XBT_MANIFEST_ID = createField(DSL.name("prev_xbt_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.xbt_manifest.next_xbt_manifest_id</code>.
     */
    public final TableField<XbtManifestRecord, String> NEXT_XBT_MANIFEST_ID = createField(DSL.name("next_xbt_manifest_id"), SQLDataType.CHAR(36), this, "");

    private XbtManifest(Name alias, Table<XbtManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private XbtManifest(Name alias, Table<XbtManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.xbt_manifest</code> table reference
     */
    public XbtManifest(String alias) {
        this(DSL.name(alias), XBT_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.xbt_manifest</code> table reference
     */
    public XbtManifest(Name alias) {
        this(alias, XBT_MANIFEST);
    }

    /**
     * Create a <code>oagi.xbt_manifest</code> table reference
     */
    public XbtManifest() {
        this(DSL.name("xbt_manifest"), null);
    }

    public <O extends Record> XbtManifest(Table<O> child, ForeignKey<O, XbtManifestRecord> key) {
        super(child, key, XBT_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<XbtManifestRecord> getPrimaryKey() {
        return Keys.KEY_XBT_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<XbtManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.XBT_MANIFEST_RELEASE_ID_FK, Keys.XBT_MANIFEST_XBT_ID_FK, Keys.XBT_MANIFEST_LOG_ID_FK, Keys.XBT_MANIFEST_PREV_XBT_MANIFEST_ID_FK, Keys.XBT_MANIFEST_NEXT_XBT_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient Xbt _xbt;
    private transient Log _log;
    private transient XbtManifest _xbtManifestPrevXbtManifestIdFk;
    private transient XbtManifest _xbtManifestNextXbtManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.XBT_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.xbt</code> table.
     */
    public Xbt xbt() {
        if (_xbt == null)
            _xbt = new Xbt(this, Keys.XBT_MANIFEST_XBT_ID_FK);

        return _xbt;
    }

    /**
     * Get the implicit join path to the <code>oagi.log</code> table.
     */
    public Log log() {
        if (_log == null)
            _log = new Log(this, Keys.XBT_MANIFEST_LOG_ID_FK);

        return _log;
    }

    /**
     * Get the implicit join path to the <code>oagi.xbt_manifest</code> table,
     * via the <code>xbt_manifest_prev_xbt_manifest_id_fk</code> key.
     */
    public XbtManifest xbtManifestPrevXbtManifestIdFk() {
        if (_xbtManifestPrevXbtManifestIdFk == null)
            _xbtManifestPrevXbtManifestIdFk = new XbtManifest(this, Keys.XBT_MANIFEST_PREV_XBT_MANIFEST_ID_FK);

        return _xbtManifestPrevXbtManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.xbt_manifest</code> table,
     * via the <code>xbt_manifest_next_xbt_manifest_id_fk</code> key.
     */
    public XbtManifest xbtManifestNextXbtManifestIdFk() {
        if (_xbtManifestNextXbtManifestIdFk == null)
            _xbtManifestNextXbtManifestIdFk = new XbtManifest(this, Keys.XBT_MANIFEST_NEXT_XBT_MANIFEST_ID_FK);

        return _xbtManifestNextXbtManifestIdFk;
    }

    @Override
    public XbtManifest as(String alias) {
        return new XbtManifest(DSL.name(alias), this);
    }

    @Override
    public XbtManifest as(Name alias) {
        return new XbtManifest(alias, this);
    }

    @Override
    public XbtManifest as(Table<?> alias) {
        return new XbtManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public XbtManifest rename(String name) {
        return new XbtManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public XbtManifest rename(Name name) {
        return new XbtManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public XbtManifest rename(Table<?> name) {
        return new XbtManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, String, Byte, String, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
