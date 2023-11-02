/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.OasHttpHeader;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OasHttpHeaderRecord extends UpdatableRecordImpl<OasHttpHeaderRecord> implements Record11<ULong, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.oas_http_header.oas_http_header_id</code>. The
     * primary key of the record.
     */
    public void setOasHttpHeaderId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.oas_http_header_id</code>. The
     * primary key of the record.
     */
    public ULong getOasHttpHeaderId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.oas_http_header.guid</code>. The GUID of the
     * record.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.guid</code>. The GUID of the
     * record.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.oas_http_header.header</code>. REQUIRED. The name
     * of the header. Header names are case sensitive. 
     */
    public void setHeader(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.header</code>. REQUIRED. The name
     * of the header. Header names are case sensitive. 
     */
    public String getHeader() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.oas_http_header.description</code>. A brief
     * description of the header. This could contain examples of use. CommonMark
     * syntax MAY be used for rich text representation.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.description</code>. A brief
     * description of the header. This could contain examples of use. CommonMark
     * syntax MAY be used for rich text representation.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.oas_http_header.agency_id_list_value_id</code>. A
     * reference of the agency id list value
     */
    public void setAgencyIdListValueId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.agency_id_list_value_id</code>. A
     * reference of the agency id list value
     */
    public ULong getAgencyIdListValueId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.oas_http_header.schema_type_reference</code>.
     * REQUIRED. The schema defining the type used for the header using the
     * reference string, $ref.
     */
    public void setSchemaTypeReference(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.schema_type_reference</code>.
     * REQUIRED. The schema defining the type used for the header using the
     * reference string, $ref.
     */
    public String getSchemaTypeReference() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.oas_http_header.owner_user_id</code>. The user who
     * owns the record.
     */
    public void setOwnerUserId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.owner_user_id</code>. The user who
     * owns the record.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.oas_http_header.created_by</code>. The user who
     * creates the record.
     */
    public void setCreatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.created_by</code>. The user who
     * creates the record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.oas_http_header.last_updated_by</code>. The user
     * who last updates the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.last_updated_by</code>. The user
     * who last updates the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.oas_http_header.creation_timestamp</code>. The
     * timestamp when the record is created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.creation_timestamp</code>. The
     * timestamp when the record is created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>oagi.oas_http_header.last_update_timestamp</code>. The
     * timestamp when the record is last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.oas_http_header.last_update_timestamp</code>. The
     * timestamp when the record is last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<ULong, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<ULong, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return OasHttpHeader.OAS_HTTP_HEADER.OAS_HTTP_HEADER_ID;
    }

    @Override
    public Field<String> field2() {
        return OasHttpHeader.OAS_HTTP_HEADER.GUID;
    }

    @Override
    public Field<String> field3() {
        return OasHttpHeader.OAS_HTTP_HEADER.HEADER;
    }

    @Override
    public Field<String> field4() {
        return OasHttpHeader.OAS_HTTP_HEADER.DESCRIPTION;
    }

    @Override
    public Field<ULong> field5() {
        return OasHttpHeader.OAS_HTTP_HEADER.AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public Field<String> field6() {
        return OasHttpHeader.OAS_HTTP_HEADER.SCHEMA_TYPE_REFERENCE;
    }

    @Override
    public Field<ULong> field7() {
        return OasHttpHeader.OAS_HTTP_HEADER.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field8() {
        return OasHttpHeader.OAS_HTTP_HEADER.CREATED_BY;
    }

    @Override
    public Field<ULong> field9() {
        return OasHttpHeader.OAS_HTTP_HEADER.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return OasHttpHeader.OAS_HTTP_HEADER.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return OasHttpHeader.OAS_HTTP_HEADER.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getOasHttpHeaderId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getHeader();
    }

    @Override
    public String component4() {
        return getDescription();
    }

    @Override
    public ULong component5() {
        return getAgencyIdListValueId();
    }

    @Override
    public String component6() {
        return getSchemaTypeReference();
    }

    @Override
    public ULong component7() {
        return getOwnerUserId();
    }

    @Override
    public ULong component8() {
        return getCreatedBy();
    }

    @Override
    public ULong component9() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component10() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value1() {
        return getOasHttpHeaderId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getHeader();
    }

    @Override
    public String value4() {
        return getDescription();
    }

    @Override
    public ULong value5() {
        return getAgencyIdListValueId();
    }

    @Override
    public String value6() {
        return getSchemaTypeReference();
    }

    @Override
    public ULong value7() {
        return getOwnerUserId();
    }

    @Override
    public ULong value8() {
        return getCreatedBy();
    }

    @Override
    public ULong value9() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value10() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public OasHttpHeaderRecord value1(ULong value) {
        setOasHttpHeaderId(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value3(String value) {
        setHeader(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value4(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value5(ULong value) {
        setAgencyIdListValueId(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value6(String value) {
        setSchemaTypeReference(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value7(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value8(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value9(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value10(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord value11(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public OasHttpHeaderRecord values(ULong value1, String value2, String value3, String value4, ULong value5, String value6, ULong value7, ULong value8, ULong value9, LocalDateTime value10, LocalDateTime value11) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OasHttpHeaderRecord
     */
    public OasHttpHeaderRecord() {
        super(OasHttpHeader.OAS_HTTP_HEADER);
    }

    /**
     * Create a detached, initialised OasHttpHeaderRecord
     */
    public OasHttpHeaderRecord(ULong oasHttpHeaderId, String guid, String header, String description, ULong agencyIdListValueId, String schemaTypeReference, ULong ownerUserId, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(OasHttpHeader.OAS_HTTP_HEADER);

        setOasHttpHeaderId(oasHttpHeaderId);
        setGuid(guid);
        setHeader(header);
        setDescription(description);
        setAgencyIdListValueId(agencyIdListValueId);
        setSchemaTypeReference(schemaTypeReference);
        setOwnerUserId(ownerUserId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        resetChangedOnNotNull();
    }
}
