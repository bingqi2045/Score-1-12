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
import org.jooq.Row7;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AgencyIdListValueManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListValueManifest extends TableImpl<AgencyIdListValueManifestRecord> {

    private static final long serialVersionUID = -695372703;

    /**
     * The reference instance of <code>oagi.agency_id_list_value_manifest</code>
     */
    public static final AgencyIdListValueManifest AGENCY_ID_LIST_VALUE_MANIFEST = new AgencyIdListValueManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AgencyIdListValueManifestRecord> getRecordType() {
        return AgencyIdListValueManifestRecord.class;
    }

    /**
     * The column <code>oagi.agency_id_list_value_manifest.agency_id_list_value_manifest_id</code>.
     */
    public final TableField<AgencyIdListValueManifestRecord, ULong> AGENCY_ID_LIST_VALUE_MANIFEST_ID = createField(DSL.name("agency_id_list_value_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.agency_id_list_value_manifest.release_id</code>.
     */
    public final TableField<AgencyIdListValueManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.agency_id_list_value_manifest.agency_id_list_value_id</code>.
     */
    public final TableField<AgencyIdListValueManifestRecord, ULong> AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("agency_id_list_value_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.agency_id_list_value_manifest.agency_id_list_manifest_id</code>.
     */
    public final TableField<AgencyIdListValueManifestRecord, ULong> AGENCY_ID_LIST_MANIFEST_ID = createField(DSL.name("agency_id_list_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.agency_id_list_value_manifest.conflict</code>. This indicates that there is a conflict between self and relationship.
     */
    public final TableField<AgencyIdListValueManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.agency_id_list_value_manifest.prev_agency_id_list_value_manifest_id</code>.
     */
    public final TableField<AgencyIdListValueManifestRecord, ULong> PREV_AGENCY_ID_LIST_VALUE_MANIFEST_ID = createField(DSL.name("prev_agency_id_list_value_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.agency_id_list_value_manifest.next_agency_id_list_value_manifest_id</code>.
     */
    public final TableField<AgencyIdListValueManifestRecord, ULong> NEXT_AGENCY_ID_LIST_VALUE_MANIFEST_ID = createField(DSL.name("next_agency_id_list_value_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * Create a <code>oagi.agency_id_list_value_manifest</code> table reference
     */
    public AgencyIdListValueManifest() {
        this(DSL.name("agency_id_list_value_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.agency_id_list_value_manifest</code> table reference
     */
    public AgencyIdListValueManifest(String alias) {
        this(DSL.name(alias), AGENCY_ID_LIST_VALUE_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.agency_id_list_value_manifest</code> table reference
     */
    public AgencyIdListValueManifest(Name alias) {
        this(alias, AGENCY_ID_LIST_VALUE_MANIFEST);
    }

    private AgencyIdListValueManifest(Name alias, Table<AgencyIdListValueManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private AgencyIdListValueManifest(Name alias, Table<AgencyIdListValueManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> AgencyIdListValueManifest(Table<O> child, ForeignKey<O, AgencyIdListValueManifestRecord> key) {
        super(child, key, AGENCY_ID_LIST_VALUE_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<AgencyIdListValueManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_AGENCY_ID_LIST_VALUE_MANIFEST;
    }

    @Override
    public UniqueKey<AgencyIdListValueManifestRecord> getPrimaryKey() {
        return Keys.KEY_AGENCY_ID_LIST_VALUE_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<AgencyIdListValueManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<AgencyIdListValueManifestRecord>>asList(Keys.KEY_AGENCY_ID_LIST_VALUE_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<AgencyIdListValueManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AgencyIdListValueManifestRecord, ?>>asList(Keys.AGENCY_ID_LIST_VALUE_MANIFEST_RELEASE_ID_FK, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_AGENCY_ID_LIST_VALUE_ID_FK, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_AGENCY_ID_LIST_MANIFEST_ID_FK, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_PREV_AGENCY_ID_LIST_VALUE_MANIF_FK, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_NEXT_AGENCY_ID_LIST_VALUE_MANIF_FK);
    }

    public Release release() {
        return new Release(this, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_RELEASE_ID_FK);
    }

    public AgencyIdListValue agencyIdListValue() {
        return new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_AGENCY_ID_LIST_VALUE_ID_FK);
    }

    public AgencyIdListManifest agencyIdListManifest() {
        return new AgencyIdListManifest(this, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_AGENCY_ID_LIST_MANIFEST_ID_FK);
    }

    public AgencyIdListValueManifest agencyIdListValueManifestPrevAgencyIdListValueManifFk() {
        return new AgencyIdListValueManifest(this, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_PREV_AGENCY_ID_LIST_VALUE_MANIF_FK);
    }

    public AgencyIdListValueManifest agencyIdListValueManifestNextAgencyIdListValueManifFk() {
        return new AgencyIdListValueManifest(this, Keys.AGENCY_ID_LIST_VALUE_MANIFEST_NEXT_AGENCY_ID_LIST_VALUE_MANIF_FK);
    }

    @Override
    public AgencyIdListValueManifest as(String alias) {
        return new AgencyIdListValueManifest(DSL.name(alias), this);
    }

    @Override
    public AgencyIdListValueManifest as(Name alias) {
        return new AgencyIdListValueManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListValueManifest rename(String name) {
        return new AgencyIdListValueManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListValueManifest rename(Name name) {
        return new AgencyIdListValueManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, ULong, ULong, ULong, Byte, ULong, ULong> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
