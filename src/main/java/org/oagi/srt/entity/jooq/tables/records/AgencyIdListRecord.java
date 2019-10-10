/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.AgencyIdList;


/**
 * The AGENCY_ID_LIST table stores information about agency identification 
 * lists. The list's values are however kept in the AGENCY_ID_LIST_VALUE.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListRecord extends UpdatableRecordImpl<AgencyIdListRecord> implements Record9<ULong, String, String, String, String, ULong, String, ULong, String> {

    private static final long serialVersionUID = -1925883609;

    /**
     * Setter for <code>oagi.agency_id_list.agency_id_list_id</code>. A internal, primary database key.
     */
    public void setAgencyIdListId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.agency_id_list_id</code>. A internal, primary database key.
     */
    public ULong getAgencyIdListId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.agency_id_list.guid</code>. A globally unique identifier (GUID) of an agency identifier scheme. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.guid</code>. A globally unique identifier (GUID) of an agency identifier scheme. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.agency_id_list.enum_type_guid</code>. This column stores the GUID of the type containing the enumerated values. In OAGIS, most code lists and agnecy ID lists are defined by an XyzCodeContentType (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or XyzAgencyIdentificationEnumerationContentType). However, some don't have the enumeration type. When that is the case, this column is null.
     */
    public void setEnumTypeGuid(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.enum_type_guid</code>. This column stores the GUID of the type containing the enumerated values. In OAGIS, most code lists and agnecy ID lists are defined by an XyzCodeContentType (or XyzAgencyIdentificationContentType) and XyzCodeEnumerationType (or XyzAgencyIdentificationEnumerationContentType). However, some don't have the enumeration type. When that is the case, this column is null.
     */
    public String getEnumTypeGuid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.agency_id_list.name</code>. Name of the agency identification list.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.name</code>. Name of the agency identification list.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.agency_id_list.list_id</code>. This is a business or standard identification assigned to the agency identification list.
     */
    public void setListId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.list_id</code>. This is a business or standard identification assigned to the agency identification list.
     */
    public String getListId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.agency_id_list.agency_id_list_value_id</code>. This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.
     */
    public void setAgencyIdListValueId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.agency_id_list_value_id</code>. This is the identification of the agency or organization which developed and/or maintains the list. Theoretically, this can be modeled as a self-reference foreign key, but it is not implemented at this point.
     */
    public ULong getAgencyIdListValueId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.agency_id_list.version_id</code>. Version number of the agency identification list (assigned by the agency).
     */
    public void setVersionId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.version_id</code>. Version number of the agency identification list (assigned by the agency).
     */
    public String getVersionId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.agency_id_list.module_id</code>. Foreign key to the module table indicating the physical schema the MODULE belongs to.
     */
    public void setModuleId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.module_id</code>. Foreign key to the module table indicating the physical schema the MODULE belongs to.
     */
    public ULong getModuleId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.agency_id_list.definition</code>. Description of the agency identification list.
     */
    public void setDefinition(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list.definition</code>. Description of the agency identification list.
     */
    public String getDefinition() {
        return (String) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, String, String, String, String, ULong, String, ULong, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, String, String, String, String, ULong, String, ULong, String> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AgencyIdList.AGENCY_ID_LIST.AGENCY_ID_LIST_ID;
    }

    @Override
    public Field<String> field2() {
        return AgencyIdList.AGENCY_ID_LIST.GUID;
    }

    @Override
    public Field<String> field3() {
        return AgencyIdList.AGENCY_ID_LIST.ENUM_TYPE_GUID;
    }

    @Override
    public Field<String> field4() {
        return AgencyIdList.AGENCY_ID_LIST.NAME;
    }

    @Override
    public Field<String> field5() {
        return AgencyIdList.AGENCY_ID_LIST.LIST_ID;
    }

    @Override
    public Field<ULong> field6() {
        return AgencyIdList.AGENCY_ID_LIST.AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public Field<String> field7() {
        return AgencyIdList.AGENCY_ID_LIST.VERSION_ID;
    }

    @Override
    public Field<ULong> field8() {
        return AgencyIdList.AGENCY_ID_LIST.MODULE_ID;
    }

    @Override
    public Field<String> field9() {
        return AgencyIdList.AGENCY_ID_LIST.DEFINITION;
    }

    @Override
    public ULong component1() {
        return getAgencyIdListId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getEnumTypeGuid();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public String component5() {
        return getListId();
    }

    @Override
    public ULong component6() {
        return getAgencyIdListValueId();
    }

    @Override
    public String component7() {
        return getVersionId();
    }

    @Override
    public ULong component8() {
        return getModuleId();
    }

    @Override
    public String component9() {
        return getDefinition();
    }

    @Override
    public ULong value1() {
        return getAgencyIdListId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getEnumTypeGuid();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public String value5() {
        return getListId();
    }

    @Override
    public ULong value6() {
        return getAgencyIdListValueId();
    }

    @Override
    public String value7() {
        return getVersionId();
    }

    @Override
    public ULong value8() {
        return getModuleId();
    }

    @Override
    public String value9() {
        return getDefinition();
    }

    @Override
    public AgencyIdListRecord value1(ULong value) {
        setAgencyIdListId(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value3(String value) {
        setEnumTypeGuid(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value5(String value) {
        setListId(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value6(ULong value) {
        setAgencyIdListValueId(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value7(String value) {
        setVersionId(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value8(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public AgencyIdListRecord value9(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AgencyIdListRecord values(ULong value1, String value2, String value3, String value4, String value5, ULong value6, String value7, ULong value8, String value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AgencyIdListRecord
     */
    public AgencyIdListRecord() {
        super(AgencyIdList.AGENCY_ID_LIST);
    }

    /**
     * Create a detached, initialised AgencyIdListRecord
     */
    public AgencyIdListRecord(ULong agencyIdListId, String guid, String enumTypeGuid, String name, String listId, ULong agencyIdListValueId, String versionId, ULong moduleId, String definition) {
        super(AgencyIdList.AGENCY_ID_LIST);

        set(0, agencyIdListId);
        set(1, guid);
        set(2, enumTypeGuid);
        set(3, name);
        set(4, listId);
        set(5, agencyIdListValueId);
        set(6, versionId);
        set(7, moduleId);
        set(8, definition);
    }
}
