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
import org.jooq.Function22;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row22;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AgencyIdListRecord;


/**
 * The AGENCY_ID_LIST table stores information about agency identification
 * lists. The list's values are however kept in the AGENCY_ID_LIST_VALUE.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdList extends TableImpl<AgencyIdListRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.agency_id_list</code>
     */
    public static final AgencyIdList AGENCY_ID_LIST = new AgencyIdList();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AgencyIdListRecord> getRecordType() {
        return AgencyIdListRecord.class;
    }

    /**
     * The column <code>oagi.agency_id_list.agency_id_list_id</code>. Primary,
     * internal database key.
     */
    public final TableField<AgencyIdListRecord, String> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.agency_id_list.guid</code>. A globally unique
     * identifier (GUID).
     */
    public final TableField<AgencyIdListRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.agency_id_list.enum_type_guid</code>. This column
     * stores the GUID of the type containing the enumerated values. In OAGIS,
     * most code lists and agnecy ID lists are defined by an XyzCodeContentType
     * (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or
     * XyzAgencyIdentificationEnumerationContentType). However, some don't have
     * the enumeration type. When that is the case, this column is null.
     */
    public final TableField<AgencyIdListRecord, String> ENUM_TYPE_GUID = createField(DSL.name("enum_type_guid"), SQLDataType.VARCHAR(41).nullable(false), this, "This column stores the GUID of the type containing the enumerated values. In OAGIS, most code lists and agnecy ID lists are defined by an XyzCodeContentType (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or XyzAgencyIdentificationEnumerationContentType). However, some don't have the enumeration type. When that is the case, this column is null.");

    /**
     * The column <code>oagi.agency_id_list.name</code>. Name of the agency
     * identification list.
     */
    public final TableField<AgencyIdListRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100), this, "Name of the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list.list_id</code>. This is a business
     * or standard identification assigned to the agency identification list.
     */
    public final TableField<AgencyIdListRecord, String> LIST_ID = createField(DSL.name("list_id"), SQLDataType.VARCHAR(100), this, "This is a business or standard identification assigned to the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list.agency_id_list_value_id</code>. This
     * is the identification of the agency or organization which developed
     * and/or maintains the list. Theoretically, this can be modeled as a
     * self-reference foreign key, but it is not implemented at this point.
     */
    public final TableField<AgencyIdListRecord, String> AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("agency_id_list_value_id"), SQLDataType.CHAR(36), this, "This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.");

    /**
     * The column <code>oagi.agency_id_list.version_id</code>. Version number of
     * the agency identification list (assigned by the agency).
     */
    public final TableField<AgencyIdListRecord, String> VERSION_ID = createField(DSL.name("version_id"), SQLDataType.VARCHAR(100), this, "Version number of the agency identification list (assigned by the agency).");

    /**
     * The column <code>oagi.agency_id_list.based_agency_id_list_id</code>. This
     * is a foreign key to the AGENCY_ID_LIST table itself. This identifies the
     * agency id list on which this agency id list is based, if any. The
     * derivation may be restriction and/or extension.
     */
    public final TableField<AgencyIdListRecord, String> BASED_AGENCY_ID_LIST_ID = createField(DSL.name("based_agency_id_list_id"), SQLDataType.CHAR(36), this, "This is a foreign key to the AGENCY_ID_LIST table itself. This identifies the agency id list on which this agency id list is based, if any. The derivation may be restriction and/or extension.");

    /**
     * The column <code>oagi.agency_id_list.definition</code>. Description of
     * the agency identification list.
     */
    public final TableField<AgencyIdListRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Description of the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list.definition_source</code>. This is
     * typically a URL which indicates the source of the agency id list
     * DEFINITION.
     */
    public final TableField<AgencyIdListRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), SQLDataType.VARCHAR(100), this, "This is typically a URL which indicates the source of the agency id list DEFINITION.");

    /**
     * The column <code>oagi.agency_id_list.remark</code>. Usage information
     * about the agency id list.
     */
    public final TableField<AgencyIdListRecord, String> REMARK = createField(DSL.name("remark"), SQLDataType.VARCHAR(225), this, "Usage information about the agency id list.");

    /**
     * The column <code>oagi.agency_id_list.namespace_id</code>. Foreign key to
     * the NAMESPACE table.
     */
    public final TableField<AgencyIdListRecord, String> NAMESPACE_ID = createField(DSL.name("namespace_id"), SQLDataType.CHAR(36), this, "Foreign key to the NAMESPACE table.");

    /**
     * The column <code>oagi.agency_id_list.created_by</code>. Foreign key to
     * the APP_USER table. It indicates the user who created the agency ID list.
     */
    public final TableField<AgencyIdListRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created the agency ID list.");

    /**
     * The column <code>oagi.agency_id_list.last_updated_by</code>. Foreign key
     * to the APP_USER table. It identifies the user who last updated the agency
     * ID list.
     */
    public final TableField<AgencyIdListRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.");

    /**
     * The column <code>oagi.agency_id_list.creation_timestamp</code>. Timestamp
     * when the agency ID list was created.
     */
    public final TableField<AgencyIdListRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the agency ID list was created.");

    /**
     * The column <code>oagi.agency_id_list.last_update_timestamp</code>.
     * Timestamp when the agency ID list was last updated.
     */
    public final TableField<AgencyIdListRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the agency ID list was last updated.");

    /**
     * The column <code>oagi.agency_id_list.state</code>. Life cycle state of
     * the agency ID list. Possible values are Editing, Published, or Deleted.
     * Only the agency ID list in published state is available for derivation
     * and for used by the CC and BIE. Once the agency ID list is published, it
     * cannot go back to Editing. A new version would have to be created.
     */
    public final TableField<AgencyIdListRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(20), this, "Life cycle state of the agency ID list. Possible values are Editing, Published, or Deleted. Only the agency ID list in published state is available for derivation and for used by the CC and BIE. Once the agency ID list is published, it cannot go back to Editing. A new version would have to be created.");

    /**
     * The column <code>oagi.agency_id_list.is_deprecated</code>. Indicates
     * whether the agency id list is deprecated and should not be reused (i.e.,
     * no new reference to this record should be allowed).
     */
    public final TableField<AgencyIdListRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), SQLDataType.TINYINT.defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicates whether the agency id list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).");

    /**
     * The column
     * <code>oagi.agency_id_list.replacement_agency_id_list_id</code>. This
     * refers to a replacement if the record is deprecated.
     */
    public final TableField<AgencyIdListRecord, String> REPLACEMENT_AGENCY_ID_LIST_ID = createField(DSL.name("replacement_agency_id_list_id"), SQLDataType.CHAR(36), this, "This refers to a replacement if the record is deprecated.");

    /**
     * The column <code>oagi.agency_id_list.owner_user_id</code>. Foreign key to
     * the APP_USER table. This is the user who owns the entity, is allowed to
     * edit the entity, and who can transfer the ownership to another user.
     * 
     * The ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership.
     */
    public final TableField<AgencyIdListRecord, String> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.agency_id_list.prev_agency_id_list_id</code>. A
     * self-foreign key to indicate the previous history record.
     */
    public final TableField<AgencyIdListRecord, String> PREV_AGENCY_ID_LIST_ID = createField(DSL.name("prev_agency_id_list_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.agency_id_list.next_agency_id_list_id</code>. A
     * self-foreign key to indicate the next history record.
     */
    public final TableField<AgencyIdListRecord, String> NEXT_AGENCY_ID_LIST_ID = createField(DSL.name("next_agency_id_list_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the next history record.");

    private AgencyIdList(Name alias, Table<AgencyIdListRecord> aliased) {
        this(alias, aliased, null);
    }

    private AgencyIdList(Name alias, Table<AgencyIdListRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The AGENCY_ID_LIST table stores information about agency identification lists. The list's values are however kept in the AGENCY_ID_LIST_VALUE."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.agency_id_list</code> table reference
     */
    public AgencyIdList(String alias) {
        this(DSL.name(alias), AGENCY_ID_LIST);
    }

    /**
     * Create an aliased <code>oagi.agency_id_list</code> table reference
     */
    public AgencyIdList(Name alias) {
        this(alias, AGENCY_ID_LIST);
    }

    /**
     * Create a <code>oagi.agency_id_list</code> table reference
     */
    public AgencyIdList() {
        this(DSL.name("agency_id_list"), null);
    }

    public <O extends Record> AgencyIdList(Table<O> child, ForeignKey<O, AgencyIdListRecord> key) {
        super(child, key, AGENCY_ID_LIST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<AgencyIdListRecord> getPrimaryKey() {
        return Keys.KEY_AGENCY_ID_LIST_PRIMARY;
    }

    @Override
    public List<ForeignKey<AgencyIdListRecord, ?>> getReferences() {
        return Arrays.asList(Keys.AGENCY_ID_LIST_AGENCY_ID_LIST_VALUE_ID_FK, Keys.AGENCY_ID_LIST_BASED_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_NAMESPACE_ID_FK, Keys.AGENCY_ID_LIST_CREATED_BY_FK, Keys.AGENCY_ID_LIST_LAST_UPDATED_BY_FK, Keys.AGENCY_ID_LIST_REPLACEMENT_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_OWNER_USER_ID_FK, Keys.AGENCY_ID_LIST_PREV_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_NEXT_AGENCY_ID_LIST_ID_FK);
    }

    private transient AgencyIdListValue _agencyIdListValue;
    private transient AgencyIdList _agencyIdListBasedAgencyIdListIdFk;
    private transient Namespace _namespace;
    private transient AppUser _agencyIdListCreatedByFk;
    private transient AppUser _agencyIdListLastUpdatedByFk;
    private transient AgencyIdList _agencyIdListReplacementAgencyIdListIdFk;
    private transient AppUser _agencyIdListOwnerUserIdFk;
    private transient AgencyIdList _agencyIdListPrevAgencyIdListIdFk;
    private transient AgencyIdList _agencyIdListNextAgencyIdListIdFk;

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list_value</code>
     * table.
     */
    public AgencyIdListValue agencyIdListValue() {
        if (_agencyIdListValue == null)
            _agencyIdListValue = new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_AGENCY_ID_LIST_VALUE_ID_FK);

        return _agencyIdListValue;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table,
     * via the <code>agency_id_list_based_agency_id_list_id_fk</code> key.
     */
    public AgencyIdList agencyIdListBasedAgencyIdListIdFk() {
        if (_agencyIdListBasedAgencyIdListIdFk == null)
            _agencyIdListBasedAgencyIdListIdFk = new AgencyIdList(this, Keys.AGENCY_ID_LIST_BASED_AGENCY_ID_LIST_ID_FK);

        return _agencyIdListBasedAgencyIdListIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.namespace</code> table.
     */
    public Namespace namespace() {
        if (_namespace == null)
            _namespace = new Namespace(this, Keys.AGENCY_ID_LIST_NAMESPACE_ID_FK);

        return _namespace;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>agency_id_list_created_by_fk</code> key.
     */
    public AppUser agencyIdListCreatedByFk() {
        if (_agencyIdListCreatedByFk == null)
            _agencyIdListCreatedByFk = new AppUser(this, Keys.AGENCY_ID_LIST_CREATED_BY_FK);

        return _agencyIdListCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>agency_id_list_last_updated_by_fk</code> key.
     */
    public AppUser agencyIdListLastUpdatedByFk() {
        if (_agencyIdListLastUpdatedByFk == null)
            _agencyIdListLastUpdatedByFk = new AppUser(this, Keys.AGENCY_ID_LIST_LAST_UPDATED_BY_FK);

        return _agencyIdListLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table,
     * via the <code>agency_id_list_replacement_agency_id_list_id_fk</code> key.
     */
    public AgencyIdList agencyIdListReplacementAgencyIdListIdFk() {
        if (_agencyIdListReplacementAgencyIdListIdFk == null)
            _agencyIdListReplacementAgencyIdListIdFk = new AgencyIdList(this, Keys.AGENCY_ID_LIST_REPLACEMENT_AGENCY_ID_LIST_ID_FK);

        return _agencyIdListReplacementAgencyIdListIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>agency_id_list_owner_user_id_fk</code> key.
     */
    public AppUser agencyIdListOwnerUserIdFk() {
        if (_agencyIdListOwnerUserIdFk == null)
            _agencyIdListOwnerUserIdFk = new AppUser(this, Keys.AGENCY_ID_LIST_OWNER_USER_ID_FK);

        return _agencyIdListOwnerUserIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table,
     * via the <code>agency_id_list_prev_agency_id_list_id_fk</code> key.
     */
    public AgencyIdList agencyIdListPrevAgencyIdListIdFk() {
        if (_agencyIdListPrevAgencyIdListIdFk == null)
            _agencyIdListPrevAgencyIdListIdFk = new AgencyIdList(this, Keys.AGENCY_ID_LIST_PREV_AGENCY_ID_LIST_ID_FK);

        return _agencyIdListPrevAgencyIdListIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table,
     * via the <code>agency_id_list_next_agency_id_list_id_fk</code> key.
     */
    public AgencyIdList agencyIdListNextAgencyIdListIdFk() {
        if (_agencyIdListNextAgencyIdListIdFk == null)
            _agencyIdListNextAgencyIdListIdFk = new AgencyIdList(this, Keys.AGENCY_ID_LIST_NEXT_AGENCY_ID_LIST_ID_FK);

        return _agencyIdListNextAgencyIdListIdFk;
    }

    @Override
    public AgencyIdList as(String alias) {
        return new AgencyIdList(DSL.name(alias), this);
    }

    @Override
    public AgencyIdList as(Name alias) {
        return new AgencyIdList(alias, this);
    }

    @Override
    public AgencyIdList as(Table<?> alias) {
        return new AgencyIdList(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdList rename(String name) {
        return new AgencyIdList(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdList rename(Name name) {
        return new AgencyIdList(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AgencyIdList rename(Table<?> name) {
        return new AgencyIdList(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row22 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row22<String, String, String, String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime, String, Byte, String, String, String, String> fieldsRow() {
        return (Row22) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function22<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function22<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
