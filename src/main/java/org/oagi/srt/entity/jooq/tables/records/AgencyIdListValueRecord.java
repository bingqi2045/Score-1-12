/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.AgencyIdListValue;


/**
 * This table captures the values within an agency identification list.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListValueRecord extends UpdatableRecordImpl<AgencyIdListValueRecord> implements Record5<ULong, String, String, String, ULong> {

    private static final long serialVersionUID = 810272317;

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
     * Setter for <code>oagi.agency_id_list_value.value</code>. A value in the agency identification list.
     */
    public void setValue(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.value</code>. A value in the agency identification list.
     */
    public String getValue() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.name</code>. Descriptive or short name of the value.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.name</code>. Descriptive or short name of the value.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.definition</code>. The meaning of the value.
     */
    public void setDefinition(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.definition</code>. The meaning of the value.
     */
    public String getDefinition() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value.owner_list_id</code>. Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.
     */
    public void setOwnerListId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value.owner_list_id</code>. Foreign key to the agency identification list in the AGENCY_ID_LIST table this value belongs to.
     */
    public ULong getOwnerListId() {
        return (ULong) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<ULong, String, String, String, ULong> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<ULong, String, String, String, ULong> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public Field<String> field2() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.VALUE;
    }

    @Override
    public Field<String> field3() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.NAME;
    }

    @Override
    public Field<String> field4() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.DEFINITION;
    }

    @Override
    public Field<ULong> field5() {
        return AgencyIdListValue.AGENCY_ID_LIST_VALUE.OWNER_LIST_ID;
    }

    @Override
    public ULong component1() {
        return getAgencyIdListValueId();
    }

    @Override
    public String component2() {
        return getValue();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getDefinition();
    }

    @Override
    public ULong component5() {
        return getOwnerListId();
    }

    @Override
    public ULong value1() {
        return getAgencyIdListValueId();
    }

    @Override
    public String value2() {
        return getValue();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getDefinition();
    }

    @Override
    public ULong value5() {
        return getOwnerListId();
    }

    @Override
    public AgencyIdListValueRecord value1(ULong value) {
        setAgencyIdListValueId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value2(String value) {
        setValue(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value4(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord value5(ULong value) {
        setOwnerListId(value);
        return this;
    }

    @Override
    public AgencyIdListValueRecord values(ULong value1, String value2, String value3, String value4, ULong value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
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
    public AgencyIdListValueRecord(ULong agencyIdListValueId, String value, String name, String definition, ULong ownerListId) {
        super(AgencyIdListValue.AGENCY_ID_LIST_VALUE);

        set(0, agencyIdListValueId);
        set(1, value);
        set(2, name);
        set(3, definition);
        set(4, ownerListId);
    }
}
