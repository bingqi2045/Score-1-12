/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdListValue;


/**
 * This table captures the values within an agency identification list.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListValueRecord extends UpdatableRecordImpl<AgencyIdListValueRecord> implements Record15<ULong, String, String, String, String, ULong, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> {

    private static final long serialVersionUID = 1581536633;

    /**
     * Setter for <code>oagi.agency_id_list_value.agency_id_list_value_id</code>. Primary key column.
     */
    public void setAgencyIdListValueId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.agency_id_list_value_id</code>. Primary key column.
     */
    public ULong getAgencyIdListValueId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.value</code>. A value in the agency identification list.
     */
    public void setValue(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.value</code>. A value in the agency identification list.
     */
    public String getValue() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.name</code>. Descriptive or short name of the value.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.name</code>. Descriptive or short name of the value.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.definition</code>. The meaning of the value.
     */
    public void setDefinition(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.definition</code>. The meaning of the value.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.owner_list_id</code>. Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.
     */
    public void setOwnerListId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.owner_list_id</code>. Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.
     */
    public ULong getOwnerListId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.is_deprecated</code>. Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public void setIsDeprecated(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.is_deprecated</code>. Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.replaced_by</code>. This alternative refers to a replacement if the record is deprecated.
     */
    public void setReplacedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.replaced_by</code>. This alternative refers to a replacement if the record is deprecated.
     */
    public ULong getReplacedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public void setCreatedBy(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public ULong getCreatedBy() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public void setLastUpdatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(12);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.prev_agency_id_list_value_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevAgencyIdListValueId(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.prev_agency_id_list_value_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevAgencyIdListValueId() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.next_agency_id_list_value_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextAgencyIdListValueId(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.next_agency_id_list_value_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextAgencyIdListValueId() {
        return (ULong) get(14);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row15<ULong, String, String, String, String, ULong, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<ULong, String, String, String, String, ULong, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public Field<String> field2() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.GUID;
    }

    @Override
    public Field<String> field3() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.VALUE;
    }

    @Override
    public Field<String> field4() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.NAME;
    }

    @Override
    public Field<String> field5() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.DEFINITION;
    }

    @Override
    public Field<ULong> field6() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.OWNER_LIST_ID;
    }

    @Override
    public Field<Byte> field7() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field8() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.REPLACED_BY;
    }

    @Override
    public Field<ULong> field9() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.CREATED_BY;
    }

    @Override
    public Field<ULong> field10() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field11() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field13() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field14() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.PREV_AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public Field<ULong> field15() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.NEXT_AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public ULong component1() {
        return getAgencyIdListValueId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getValue();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public String component5() {
        return getDefinition();
    }

    @Override
    public ULong component6() {
        return getOwnerListId();
    }

    @Override
    public Byte component7() {
        return getIsDeprecated();
    }

    @Override
    public ULong component8() {
        return getReplacedBy();
    }

    @Override
    public ULong component9() {
        return getCreatedBy();
    }

    @Override
    public ULong component10() {
        return getOwnerUserId();
    }

    @Override
    public ULong component11() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component12() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component13() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component14() {
        return getPrevAgencyIdListValueId();
    }

    @Override
    public ULong component15() {
        return getNextAgencyIdListValueId();
    }

    @Override
    public ULong value1() {
        return getAgencyIdListValueId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getValue();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public String value5() {
        return getDefinition();
    }

    @Override
    public ULong value6() {
        return getOwnerListId();
    }

    @Override
    public Byte value7() {
        return getIsDeprecated();
    }

    @Override
    public ULong value8() {
        return getReplacedBy();
    }

    @Override
    public ULong value9() {
        return getCreatedBy();
    }

    @Override
    public ULong value10() {
        return getOwnerUserId();
    }

    @Override
    public ULong value11() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value12() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value13() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value14() {
        return getPrevAgencyIdListValueId();
    }

    @Override
    public ULong value15() {
        return getNextAgencyIdListValueId();
    }

    @Override
    public AgencyIdListValueRecord value1(ULong value) {
        setAgencyIdListValueId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value3(String value) {
        setValue(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value6(ULong value) {
        setOwnerListId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value7(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value8(ULong value) {
        setReplacedBy(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value9(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value10(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value11(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value12(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value13(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value14(ULong value) {
        setPrevAgencyIdListValueId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value15(ULong value) {
        setNextAgencyIdListValueId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord values(ULong value1, String value2, String value3, String value4, String value5, ULong value6, Byte value7, ULong value8, ULong value9, ULong value10, ULong value11, LocalDateTime value12, LocalDateTime value13, ULong value14, ULong value15) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AgencyIdListValueRecord
     */
    public AgencyIdListValueRecord() {
        super(AgencyIdListValue.AGENCY_ID_LIST_VALUE);
    }

    /**
     * Create a detached, initialised AgencyIdListValueRecord
     */
    public AgencyIdListValueRecord(ULong agencyIdListValueId, String guid, String value, String name, String definition, ULong ownerListId, Byte isDeprecated, ULong replacedBy, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, ULong prevAgencyIdListValueId, ULong nextAgencyIdListValueId) {
        super(AgencyIdListValue.AGENCY_ID_LIST_VALUE);

        set(0, agencyIdListValueId);
        set(1, guid);
        set(2, value);
        set(3, name);
        set(4, definition);
        set(5, ownerListId);
        set(6, isDeprecated);
        set(7, replacedBy);
        set(8, createdBy);
        set(9, ownerUserId);
        set(10, lastUpdatedBy);
        set(11, creationTimestamp);
        set(12, lastUpdateTimestamp);
        set(13, prevAgencyIdListValueId);
        set(14, nextAgencyIdListValueId);
    }
}
