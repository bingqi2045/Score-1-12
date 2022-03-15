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
import org.jooq.Row10;
import org.jooq.Schema;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AgencyIdListManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListManifest extends TableImpl<AgencyIdListManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.agency_id_list_manifest</code>
     */
    public static final AgencyIdListManifest AGENCY_ID_LIST_MANIFEST = new AgencyIdListManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AgencyIdListManifestRecord> getRecordType() {
        return AgencyIdListManifestRecord.class;
    }

    /**
     * The column
     * <code>oagi.agency_id_list_manifest.agency_id_list_manifest_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> AGENCY_ID_LIST_MANIFEST_ID = createField(DSL.name("agency_id_list_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.agency_id_list_manifest.release_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.agency_id_list_manifest.agency_id_list_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column
     * <code>oagi.agency_id_list_manifest.agency_id_list_value_manifest_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> AGENCY_ID_LIST_VALUE_MANIFEST_ID = createField(DSL.name("agency_id_list_value_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column
     * <code>oagi.agency_id_list_manifest.based_agency_id_list_manifest_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> BASED_AGENCY_ID_LIST_MANIFEST_ID = createField(DSL.name("based_agency_id_list_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.agency_id_list_manifest.conflict</code>. This
     * indicates that there is a conflict between self and relationship.
     */
    public final TableField<AgencyIdListManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.agency_id_list_manifest.log_id</code>. A foreign
     * key pointed to a log for the current record.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> LOG_ID = createField(DSL.name("log_id"), SQLDataType.BIGINTUNSIGNED, this, "A foreign key pointed to a log for the current record.");

    /**
     * The column
     * <code>oagi.agency_id_list_manifest.replacement_agency_id_list_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> REPLACEMENT_AGENCY_ID_LIST_MANIFEST_ID = createField(DSL.name("replacement_agency_id_list_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column
     * <code>oagi.agency_id_list_manifest.prev_agency_id_list_manifest_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> PREV_AGENCY_ID_LIST_MANIFEST_ID = createField(DSL.name("prev_agency_id_list_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column
     * <code>oagi.agency_id_list_manifest.next_agency_id_list_manifest_id</code>.
     */
    public final TableField<AgencyIdListManifestRecord, ULong> NEXT_AGENCY_ID_LIST_MANIFEST_ID = createField(DSL.name("next_agency_id_list_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    private AgencyIdListManifest(Name alias, Table<AgencyIdListManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private AgencyIdListManifest(Name alias, Table<AgencyIdListManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.agency_id_list_manifest</code> table
     * reference
     */
    public AgencyIdListManifest(String alias) {
        this(DSL.name(alias), AGENCY_ID_LIST_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.agency_id_list_manifest</code> table
     * reference
     */
    public AgencyIdListManifest(Name alias) {
        this(alias, AGENCY_ID_LIST_MANIFEST);
    }

    /**
     * Create a <code>oagi.agency_id_list_manifest</code> table reference
     */
    public AgencyIdListManifest() {
        this(DSL.name("agency_id_list_manifest"), null);
    }

    public <O extends Record> AgencyIdListManifest(Table<O> child, ForeignKey<O, AgencyIdListManifestRecord> key) {
        super(child, key, AGENCY_ID_LIST_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<AgencyIdListManifestRecord, ULong> getIdentity() {
        return (Identity<AgencyIdListManifestRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<AgencyIdListManifestRecord> getPrimaryKey() {
        return Keys.KEY_AGENCY_ID_LIST_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<AgencyIdListManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.AGENCY_ID_LIST_MANIFEST_RELEASE_ID_FK, Keys.AGENCY_ID_LIST_MANIFEST_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_ID_FK, Keys.AGENCY_ID_LIST_MANIFEST_BASED_AGENCY_ID_LIST_MANIFEST_ID_FK, Keys.AGENCY_ID_LIST_MANIFEST_LOG_ID_FK, Keys.AGENCY_ID_LIST_REPLACEMENT_AGENCY_ID_LIST_MANIFEST_ID_FK, Keys.AGENCY_ID_LIST_MANIFEST_PREV_AGENCY_ID_LIST_MANIFEST_ID_FK, Keys.AGENCY_ID_LIST_MANIFEST_NEXT_AGENCY_ID_LIST_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient AgencyIdList _agencyIdList;
    private transient AgencyIdListValueManifest _agencyIdListValueManifest;
    private transient AgencyIdListManifest _agencyIdListManifestBasedAgencyIdListManifestIdFk;
    private transient Log _log;
    private transient AgencyIdListManifest _agencyIdListReplacementAgencyIdListManifestIdFk;
    private transient AgencyIdListManifest _agencyIdListManifestPrevAgencyIdListManifestIdFk;
    private transient AgencyIdListManifest _agencyIdListManifestNextAgencyIdListManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.AGENCY_ID_LIST_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table.
     */
    public AgencyIdList agencyIdList() {
        if (_agencyIdList == null)
            _agencyIdList = new AgencyIdList(this, Keys.AGENCY_ID_LIST_MANIFEST_AGENCY_ID_LIST_ID_FK);

        return _agencyIdList;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.agency_id_list_value_manifest</code> table.
     */
    public AgencyIdListValueManifest agencyIdListValueManifest() {
        if (_agencyIdListValueManifest == null)
            _agencyIdListValueManifest = new AgencyIdListValueManifest(this, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_ID_FK);

        return _agencyIdListValueManifest;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.agency_id_list_manifest</code> table, via the
     * <code>agency_id_list_manifest_based_agency_id_list_manifest_id_fk</code>
     * key.
     */
    public AgencyIdListManifest agencyIdListManifestBasedAgencyIdListManifestIdFk() {
        if (_agencyIdListManifestBasedAgencyIdListManifestIdFk == null)
            _agencyIdListManifestBasedAgencyIdListManifestIdFk = new AgencyIdListManifest(this, Keys.AGENCY_ID_LIST_MANIFEST_BASED_AGENCY_ID_LIST_MANIFEST_ID_FK);

        return _agencyIdListManifestBasedAgencyIdListManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.log</code> table.
     */
    public Log log() {
        if (_log == null)
            _log = new Log(this, Keys.AGENCY_ID_LIST_MANIFEST_LOG_ID_FK);

        return _log;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.agency_id_list_manifest</code> table, via the
     * <code>agency_id_list_replacement_agency_id_list_manifest_id_fk</code>
     * key.
     */
    public AgencyIdListManifest agencyIdListReplacementAgencyIdListManifestIdFk() {
        if (_agencyIdListReplacementAgencyIdListManifestIdFk == null)
            _agencyIdListReplacementAgencyIdListManifestIdFk = new AgencyIdListManifest(this, Keys.AGENCY_ID_LIST_REPLACEMENT_AGENCY_ID_LIST_MANIFEST_ID_FK);

        return _agencyIdListReplacementAgencyIdListManifestIdFk;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.agency_id_list_manifest</code> table, via the
     * <code>agency_id_list_manifest_prev_agency_id_list_manifest_id_fk</code>
     * key.
     */
    public AgencyIdListManifest agencyIdListManifestPrevAgencyIdListManifestIdFk() {
        if (_agencyIdListManifestPrevAgencyIdListManifestIdFk == null)
            _agencyIdListManifestPrevAgencyIdListManifestIdFk = new AgencyIdListManifest(this, Keys.AGENCY_ID_LIST_MANIFEST_PREV_AGENCY_ID_LIST_MANIFEST_ID_FK);

        return _agencyIdListManifestPrevAgencyIdListManifestIdFk;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.agency_id_list_manifest</code> table, via the
     * <code>agency_id_list_manifest_next_agency_id_list_manifest_id_fk</code>
     * key.
     */
    public AgencyIdListManifest agencyIdListManifestNextAgencyIdListManifestIdFk() {
        if (_agencyIdListManifestNextAgencyIdListManifestIdFk == null)
            _agencyIdListManifestNextAgencyIdListManifestIdFk = new AgencyIdListManifest(this, Keys.AGENCY_ID_LIST_MANIFEST_NEXT_AGENCY_ID_LIST_MANIFEST_ID_FK);

        return _agencyIdListManifestNextAgencyIdListManifestIdFk;
    }

    @Override
    public AgencyIdListManifest as(String alias) {
        return new AgencyIdListManifest(DSL.name(alias), this);
    }

    @Override
    public AgencyIdListManifest as(Name alias) {
        return new AgencyIdListManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListManifest rename(String name) {
        return new AgencyIdListManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListManifest rename(Name name) {
        return new AgencyIdListManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
