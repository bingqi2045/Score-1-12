/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row20;
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
     * The column <code>oagi.agency_id_list.agency_id_list_id</code>. A internal, primary database key.
     */
    public final TableField<AgencyIdListRecord, ULong> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key.");

    /**
     * The column <code>oagi.agency_id_list.guid</code>. A globally unique identifier (GUID).
     */
    public final TableField<AgencyIdListRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.agency_id_list.enum_type_guid</code>. This column stores the GUID of the type containing the enumerated values. In OAGIS, most code lists and agnecy ID lists are defined by an XyzCodeContentType (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or XyzAgencyIdentificationEnumerationContentType). However, some don't have the enumeration type. When that is the case, this column is null.
     */
    public final TableField<AgencyIdListRecord, String> ENUM_TYPE_GUID = createField(DSL.name("enum_type_guid"), SQLDataType.VARCHAR(41).nullable(false), this, "This column stores the GUID of the type containing the enumerated values. In OAGIS, most code lists and agnecy ID lists are defined by an XyzCodeContentType (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or XyzAgencyIdentificationEnumerationContentType). However, some don't have the enumeration type. When that is the case, this column is null.");

    /**
     * The column <code>oagi.agency_id_list.name</code>. Name of the agency identification list.
     */
    public final TableField<AgencyIdListRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100), this, "Name of the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list.list_id</code>. This is a business or standard identification assigned to the agency identification list.
     */
    public final TableField<AgencyIdListRecord, String> LIST_ID = createField(DSL.name("list_id"), SQLDataType.VARCHAR(10), this, "This is a business or standard identification assigned to the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list.agency_id_list_value_id</code>. This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.
     */
    public final TableField<AgencyIdListRecord, ULong> AGENCY_ID_LIST_VALUE_ID = createField(DSL.name("agency_id_list_value_id"), SQLDataType.BIGINTUNSIGNED, this, "This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.");

    /**
     * The column <code>oagi.agency_id_list.version_id</code>. Version number of the agency identification list (assigned by the agency).
     */
    public final TableField<AgencyIdListRecord, String> VERSION_ID = createField(DSL.name("version_id"), SQLDataType.VARCHAR(10), this, "Version number of the agency identification list (assigned by the agency).");

    /**
     * The column <code>oagi.agency_id_list.based_agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table itself. This identifies the agency id list on which this agency id list is based, if any. The derivation may be restriction and/or extension.
     */
    public final TableField<AgencyIdListRecord, ULong> BASED_AGENCY_ID_LIST_ID = createField(DSL.name("based_agency_id_list_id"), SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the AGENCY_ID_LIST table itself. This identifies the agency id list on which this agency id list is based, if any. The derivation may be restriction and/or extension.");

    /**
     * The column <code>oagi.agency_id_list.definition</code>. Description of the agency identification list.
     */
    public final TableField<AgencyIdListRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Description of the agency identification list.");

    /**
     * The column <code>oagi.agency_id_list.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public final TableField<AgencyIdListRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.");

    /**
     * The column <code>oagi.agency_id_list.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the agency ID list.
     */
    public final TableField<AgencyIdListRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created the agency ID list.");

    /**
     * The column <code>oagi.agency_id_list.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.
     */
    public final TableField<AgencyIdListRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the agency ID list.");

    /**
     * The column <code>oagi.agency_id_list.creation_timestamp</code>. Timestamp when the agency ID list was created.
     */
    public final TableField<AgencyIdListRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the agency ID list was created.");

    /**
     * The column <code>oagi.agency_id_list.last_update_timestamp</code>. Timestamp when the agency ID list was last updated.
     */
    public final TableField<AgencyIdListRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the agency ID list was last updated.");

    /**
     * The column <code>oagi.agency_id_list.state</code>. Life cycle state of the agency ID list. Possible values are Editing, Published, or Deleted. Only the agency ID list in published state is available for derivation and for used by the CC and BIE. Once the agency ID list is published, it cannot go back to Editing. A new version would have to be created.
     */
    public final TableField<AgencyIdListRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(20), this, "Life cycle state of the agency ID list. Possible values are Editing, Published, or Deleted. Only the agency ID list in published state is available for derivation and for used by the CC and BIE. Once the agency ID list is published, it cannot go back to Editing. A new version would have to be created.");

    /**
     * The column <code>oagi.agency_id_list.is_deprecated</code>. Indicates whether the agency id list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public final TableField<AgencyIdListRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), SQLDataType.TINYINT.defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicates whether the agency id list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).");

    /**
     * The column <code>oagi.agency_id_list.replacement_agency_id_list_id</code>. This refers to a replacement if the record is deprecated.
     */
    public final TableField<AgencyIdListRecord, ULong> REPLACEMENT_AGENCY_ID_LIST_ID = createField(DSL.name("replacement_agency_id_list_id"), SQLDataType.BIGINTUNSIGNED, this, "This refers to a replacement if the record is deprecated.");

    /**
     * The column <code>oagi.agency_id_list.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public final TableField<AgencyIdListRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.agency_id_list.prev_agency_id_list_id</code>. A self-foreign key to indicate the previous history record.
     */
    public final TableField<AgencyIdListRecord, ULong> PREV_AGENCY_ID_LIST_ID = createField(DSL.name("prev_agency_id_list_id"), SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.agency_id_list.next_agency_id_list_id</code>. A self-foreign key to indicate the next history record.
     */
    public final TableField<AgencyIdListRecord, ULong> NEXT_AGENCY_ID_LIST_ID = createField(DSL.name("next_agency_id_list_id"), SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the next history record.");

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
        return Oagi.OAGI;
    }

    @Override
    public Identity<AgencyIdListRecord, ULong> getIdentity() {
        return (Identity<AgencyIdListRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<AgencyIdListRecord> getPrimaryKey() {
        return Keys.KEY_AGENCY_ID_LIST_PRIMARY;
    }

    @Override
    public List<UniqueKey<AgencyIdListRecord>> getKeys() {
        return Arrays.<UniqueKey<AgencyIdListRecord>>asList(Keys.KEY_AGENCY_ID_LIST_PRIMARY);
    }

    @Override
    public List<ForeignKey<AgencyIdListRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AgencyIdListRecord, ?>>asList(Keys.AGENCY_ID_LIST_AGENCY_ID_LIST_VALUE_ID_FK, Keys.AGENCY_ID_LIST_BASED_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_NAMESPACE_ID_FK, Keys.AGENCY_ID_LIST_CREATED_BY_FK, Keys.AGENCY_ID_LIST_LAST_UPDATED_BY_FK, Keys.AGENCY_ID_LIST_REPLACEMENT_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_OWNER_USER_ID_FK, Keys.AGENCY_ID_LIST_PREV_AGENCY_ID_LIST_ID_FK, Keys.AGENCY_ID_LIST_NEXT_AGENCY_ID_LIST_ID_FK);
    }

    public AgencyIdListValue agencyIdListValue() {
        return new AgencyIdListValue(this, Keys.AGENCY_ID_LIST_AGENCY_ID_LIST_VALUE_ID_FK);
    }

    public AgencyIdList agencyIdListBasedAgencyIdListIdFk() {
        return new AgencyIdList(this, Keys.AGENCY_ID_LIST_BASED_AGENCY_ID_LIST_ID_FK);
    }

    public Namespace namespace() {
        return new Namespace(this, Keys.AGENCY_ID_LIST_NAMESPACE_ID_FK);
    }

    public AppUser agencyIdListCreatedByFk() {
        return new AppUser(this, Keys.AGENCY_ID_LIST_CREATED_BY_FK);
    }

    public AppUser agencyIdListLastUpdatedByFk() {
        return new AppUser(this, Keys.AGENCY_ID_LIST_LAST_UPDATED_BY_FK);
    }

    public AgencyIdList agencyIdListReplacementAgencyIdListIdFk() {
        return new AgencyIdList(this, Keys.AGENCY_ID_LIST_REPLACEMENT_AGENCY_ID_LIST_ID_FK);
    }

    public AppUser agencyIdListOwnerUserIdFk() {
        return new AppUser(this, Keys.AGENCY_ID_LIST_OWNER_USER_ID_FK);
    }

    public AgencyIdList agencyIdListPrevAgencyIdListIdFk() {
        return new AgencyIdList(this, Keys.AGENCY_ID_LIST_PREV_AGENCY_ID_LIST_ID_FK);
    }

    public AgencyIdList agencyIdListNextAgencyIdListIdFk() {
        return new AgencyIdList(this, Keys.AGENCY_ID_LIST_NEXT_AGENCY_ID_LIST_ID_FK);
    }

    @Override
    public AgencyIdList as(String alias) {
        return new AgencyIdList(DSL.name(alias), this);
    }

    @Override
    public AgencyIdList as(Name alias) {
        return new AgencyIdList(alias, this);
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

    // -------------------------------------------------------------------------
    // Row20 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row20<ULong, String, String, String, String, ULong, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row20) super.fieldsRow();
    }
}
