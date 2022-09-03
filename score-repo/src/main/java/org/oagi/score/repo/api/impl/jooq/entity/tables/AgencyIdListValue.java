/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function17;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row17;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AgencyIdListValueRecord;


/**
 * This table captures the values within an agency identification list.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListValue extends TableImpl<AgencyIdListValueRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.agency_id_list_value</code>
     */
    public static final AgencyIdListValue AGENCY_ID_LIST_VALUE = new AgencyIdListValue();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AgencyIdListValueRecord> getRecordType() {
        return AgencyIdListValueRecord.class;
    }

    /**
     * The column
     * <code>oagi.agency_id_list_value.agency_id_list_value_id</code>. Primary,
     * internal database key.
     */
    public final TableField<AgencyIdListValueRecord, String> AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("agency_id_list_value_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.agency_id_list_value.guid</code>. A globally unique
     * identifier (GUID).
     */
    public final TableField<AgencyIdListValueRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.agency_id_list_value.value</code>. A value in the
     * agency identification list.
     */
    public final TableField<AgencyIdListValueRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.VARCHAR(150).nullable(false), this, "A value in the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list_value.name</code>. Descriptive or
     * short name of the value.
     */
    public final TableField<AgencyIdListValueRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(150), this, "Descriptive or short name of the value.");

    /**
     * The column <code>oagi.agency_id_list_value.definition</code>. The meaning
     * of the value.
     */
    public final TableField<AgencyIdListValueRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "The meaning of the value.");

    /**
     * The column <code>oagi.agency_id_list_value.definition_source</code>. This
     * is typically a URL which indicates the source of the agency id list value
     * DEFINITION.
     */
    public final TableField<AgencyIdListValueRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), SQLDataType.VARCHAR(100), this, "This is typically a URL which indicates the source of the agency id list value DEFINITION.");

    /**
     * The column <code>oagi.agency_id_list_value.owner_list_id</code>. Foreign
     * key to the agency identification list in the AGENCY_ID_LIST table this
     * value belongs to.
     */
    public final TableField<AgencyIdListValueRecord, String> OWNER_LIST_ID = createField(DSL.name("owner_list_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.");

    /**
     * The column
     * <code>oagi.agency_id_list_value.based_agency_id_list_value_id</code>.
     * Foreign key to the AGENCY_ID_LIST_VALUE table itself. This column is used
     * when the AGENCY_ID_LIST_VALUE is derived from the based
     * AGENCY_ID_LIST_VALUE.
     */
    public final TableField<AgencyIdListValueRecord, String> BASED_AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("based_agency_id_list_value_id"), SQLDataType.CHAR(36), this, "Foreign key to the AGENCY_ID_LIST_VALUE table itself. This column is used when the AGENCY_ID_LIST_VALUE is derived from the based AGENCY_ID_LIST_VALUE.");

    /**
     * The column <code>oagi.agency_id_list_value.is_deprecated</code>.
     * Indicates whether the code list value is deprecated and should not be
     * reused (i.e., no new reference to this record should be allowed).
     */
    public final TableField<AgencyIdListValueRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), SQLDataType.TINYINT.defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).");

    /**
     * The column
     * <code>oagi.agency_id_list_value.replacement_agency_id_list_value_id</code>.
     * This refers to a replacement if the record is deprecated.
     */
    public final TableField<AgencyIdListValueRecord, String> REPLACEMENT_AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("replacement_agency_id_list_value_id"), SQLDataType.CHAR(36), this, "This refers to a replacement if the record is deprecated.");

    /**
     * The column <code>oagi.agency_id_list_value.created_by</code>. Foreign key
     * to the APP_USER table. It indicates the user who created the agency ID
     * list value.
     */
    public final TableField<AgencyIdListValueRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created the agency ID list value.");

    /**
     * The column <code>oagi.agency_id_list_value.owner_user_id</code>. Foreign
     * key to the APP_USER table. This is the user who owns the entity, is
     * allowed to edit the entity, and who can transfer the ownership to another
     * user.
     * 
     * The ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership.
     */
    public final TableField<AgencyIdListValueRecord, String> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.agency_id_list_value.last_updated_by</code>.
     * Foreign key to the APP_USER table. It identifies the user who last
     * updated the agency ID list value.
     */
    public final TableField<AgencyIdListValueRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list value.");

    /**
     * The column <code>oagi.agency_id_list_value.creation_timestamp</code>.
     * Timestamp when the code list was created.
     */
    public final TableField<AgencyIdListValueRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the code list was created.");

    /**
     * The column <code>oagi.agency_id_list_value.last_update_timestamp</code>.
     * Timestamp when the code list was last updated.
     */
    public final TableField<AgencyIdListValueRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the code list was last updated.");

    /**
     * The column
     * <code>oagi.agency_id_list_value.prev_agency_id_list_value_id</code>. A
     * self-foreign key to indicate the previous history record.
     */
    public final TableField<AgencyIdListValueRecord, String> PREV_AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("prev_agency_id_list_value_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column
     * <code>oagi.agency_id_list_value.next_agency_id_list_value_id</code>. A
     * self-foreign key to indicate the next history record.
     */
    public final TableField<AgencyIdListValueRecord, String> NEXT_AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("next_agency_id_list_value_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the next history record.");

    private AgencyIdListValue(Name alias, Table<AgencyIdListValueRecord> aliased) {
        this(alias, aliased, null);
    }

    private AgencyIdListValue(Name alias, Table<AgencyIdListValueRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table captures the values within an agency identification list."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.agency_id_list_value</code> table reference
     */
    public AgencyIdListValue(String alias) {
        this(DSL.name(alias), AGENCY_ID_LIST_VALUE);
    }

    /**
     * Create an aliased <code>oagi.agency_id_list_value</code> table reference
     */
    public AgencyIdListValue(Name alias) {
        this(alias, AGENCY_ID_LIST_VALUE);
    }

    /**
     * Create a <code>oagi.agency_id_list_value</code> table reference
     */
    public AgencyIdListValue() {
        this(DSL.name("agency_id_list_value"), null);
    }

    public <O extends Record> AgencyIdListValue(Table<O> child, ForeignKey<O, AgencyIdListValueRecord> key) {
        super(child, key, AGENCY_ID_LIST_VALUE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<AgencyIdListValueRecord> getPrimaryKey() {
        return Keys.KEY_AGENCY_ID_LIST_VALUE_PRIMARY;
    }

    @Override
    public List<ForeignKey<AgencyIdListValueRecord, ?>> getReferences() {
        return Arrays.asList(Keys.AGENCY_ID_LIST_VALUE_OWNER_LIST_ID_FK, Keys.AGENCY_ID_LIST_VALUE_BASED_AGENCY_ID_LIST_VALUE_ID_FK, Keys.AGENCY_ID_LIST_VALUE_REPLACEMENT_AGENCY_ID_LIST_VALUE_ID_FK, Keys.AGENCY_ID_LIST_VALUE_CREATED_BY_FK, Keys.AGENCY_ID_LIST_VALUE_OWNER_USER_ID_FK, Keys.AGENCY_ID_LIST_VALUE_LAST_UPDATED_BY_FK, Keys.AGENCY_ID_LIST_VALUE_PREV_AGENCY_ID_LIST_VALUE_ID_FK, Keys.AGENCY_ID_LIST_VALUE_NEXT_AGENCY_ID_LIST_VALUE_ID_FK);
    }

    private transient AgencyIdList _agencyIdList;
    private transient AgencyIdListValue _agencyIdListValueBasedAgencyIdListValueIdFk;
    private transient AgencyIdListValue _agencyIdListValueReplacementAgencyIdListValueIdFk;
    private transient AppUser _agencyIdListValueCreatedByFk;
    private transient AppUser _agencyIdListValueOwnerUserIdFk;
    private transient AppUser _agencyIdListValueLastUpdatedByFk;
    private transient AgencyIdListValue _agencyIdListValuePrevAgencyIdListValueIdFk;
    private transient AgencyIdListValue _agencyIdListValueNextAgencyIdListValueIdFk;

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table.
     */
    public AgencyIdList agencyIdList() {
        if (_agencyIdList == null)
            _agencyIdList = new AgencyIdList(this, Keys.AGENCY_ID_LIST_VALUE_OWNER_LIST_ID_FK);

        return _agencyIdList;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list_value</code>
     * table, via the
     * <code>agency_id_list_value_based_agency_id_list_value_id_fk</code> key.
     */
    public AgencyIdListValue agencyIdListValueBasedAgencyIdListValueIdFk() {
        if (_agencyIdListValueBasedAgencyIdListValueIdFk == null)
            _agencyIdListValueBasedAgencyIdListValueIdFk = new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_VALUE_BASED_AGENCY_ID_LIST_VALUE_ID_FK);

        return _agencyIdListValueBasedAgencyIdListValueIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list_value</code>
     * table, via the
     * <code>agency_id_list_value_replacement_agency_id_list_value_id_fk</code>
     * key.
     */
    public AgencyIdListValue agencyIdListValueReplacementAgencyIdListValueIdFk() {
        if (_agencyIdListValueReplacementAgencyIdListValueIdFk == null)
            _agencyIdListValueReplacementAgencyIdListValueIdFk = new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_VALUE_REPLACEMENT_AGENCY_ID_LIST_VALUE_ID_FK);

        return _agencyIdListValueReplacementAgencyIdListValueIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>agency_id_list_value_created_by_fk</code> key.
     */
    public AppUser agencyIdListValueCreatedByFk() {
        if (_agencyIdListValueCreatedByFk == null)
            _agencyIdListValueCreatedByFk = new AppUser(this, Keys.AGENCY_ID_LIST_VALUE_CREATED_BY_FK);

        return _agencyIdListValueCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>agency_id_list_value_owner_user_id_fk</code> key.
     */
    public AppUser agencyIdListValueOwnerUserIdFk() {
        if (_agencyIdListValueOwnerUserIdFk == null)
            _agencyIdListValueOwnerUserIdFk = new AppUser(this, Keys.AGENCY_ID_LIST_VALUE_OWNER_USER_ID_FK);

        return _agencyIdListValueOwnerUserIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>agency_id_list_value_last_updated_by_fk</code> key.
     */
    public AppUser agencyIdListValueLastUpdatedByFk() {
        if (_agencyIdListValueLastUpdatedByFk == null)
            _agencyIdListValueLastUpdatedByFk = new AppUser(this, Keys.AGENCY_ID_LIST_VALUE_LAST_UPDATED_BY_FK);

        return _agencyIdListValueLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list_value</code>
     * table, via the
     * <code>agency_id_list_value_prev_agency_id_list_value_id_fk</code> key.
     */
    public AgencyIdListValue agencyIdListValuePrevAgencyIdListValueIdFk() {
        if (_agencyIdListValuePrevAgencyIdListValueIdFk == null)
            _agencyIdListValuePrevAgencyIdListValueIdFk = new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_VALUE_PREV_AGENCY_ID_LIST_VALUE_ID_FK);

        return _agencyIdListValuePrevAgencyIdListValueIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list_value</code>
     * table, via the
     * <code>agency_id_list_value_next_agency_id_list_value_id_fk</code> key.
     */
    public AgencyIdListValue agencyIdListValueNextAgencyIdListValueIdFk() {
        if (_agencyIdListValueNextAgencyIdListValueIdFk == null)
            _agencyIdListValueNextAgencyIdListValueIdFk = new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_VALUE_NEXT_AGENCY_ID_LIST_VALUE_ID_FK);

        return _agencyIdListValueNextAgencyIdListValueIdFk;
    }

    @Override
    public AgencyIdListValue as(String alias) {
        return new AgencyIdListValue(DSL.name(alias), this);
    }

    @Override
    public AgencyIdListValue as(Name alias) {
        return new AgencyIdListValue(alias, this);
    }

    @Override
    public AgencyIdListValue as(Table<?> alias) {
        return new AgencyIdListValue(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListValue rename(String name) {
        return new AgencyIdListValue(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListValue rename(Name name) {
        return new AgencyIdListValue(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdListValue rename(Table<?> name) {
        return new AgencyIdListValue(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row17 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row17<String, String, String, String, String, String, String, String, Byte, String, String, String, String, LocalDateTime, LocalDateTime, String, String> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function17<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function17<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
