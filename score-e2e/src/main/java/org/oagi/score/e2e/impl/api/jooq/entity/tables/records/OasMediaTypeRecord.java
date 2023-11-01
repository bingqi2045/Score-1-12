/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.e2e.impl.api.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.e2e.impl.api.jooq.entity.tables.OasMediaType;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OasMediaTypeRecord extends UpdatableRecordImpl<OasMediaTypeRecord> implements Record8<ULong, String, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.oas_media_type.oas_media_type_id</code>. The
     * primary key of the record.
     */
    public void setOasMediaTypeId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.oas_media_type_id</code>. The
     * primary key of the record.
     */
    public ULong getOasMediaTypeId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.oas_media_type.guid</code>. The GUID of the record.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.guid</code>. The GUID of the record.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.oas_media_type.description</code>. On POST, PUT,
     * and PATCH, $ref is present
     */
    public void setDescription(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.description</code>. On POST, PUT,
     * and PATCH, $ref is present
     */
    public String getDescription() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.oas_media_type.owner_user_id</code>. The user who
     * owns the record.
     */
    public void setOwnerUserId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.owner_user_id</code>. The user who
     * owns the record.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.oas_media_type.created_by</code>. The user who
     * creates the record.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.created_by</code>. The user who
     * creates the record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.oas_media_type.last_updated_by</code>. The user who
     * last updates the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.last_updated_by</code>. The user who
     * last updates the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.oas_media_type.creation_timestamp</code>. The
     * timestamp when the record is created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.creation_timestamp</code>. The
     * timestamp when the record is created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.oas_media_type.last_update_timestamp</code>. The
     * timestamp when the record is last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.oas_media_type.last_update_timestamp</code>. The
     * timestamp when the record is last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, String, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<ULong, String, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return OasMediaType.OAS_MEDIA_TYPE.OAS_MEDIA_TYPE_ID;
    }

    @Override
    public Field<String> field2() {
        return OasMediaType.OAS_MEDIA_TYPE.GUID;
    }

    @Override
    public Field<String> field3() {
        return OasMediaType.OAS_MEDIA_TYPE.DESCRIPTION;
    }

    @Override
    public Field<ULong> field4() {
        return OasMediaType.OAS_MEDIA_TYPE.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field5() {
        return OasMediaType.OAS_MEDIA_TYPE.CREATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return OasMediaType.OAS_MEDIA_TYPE.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return OasMediaType.OAS_MEDIA_TYPE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return OasMediaType.OAS_MEDIA_TYPE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getOasMediaTypeId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getDescription();
    }

    @Override
    public ULong component4() {
        return getOwnerUserId();
    }

    @Override
    public ULong component5() {
        return getCreatedBy();
    }

    @Override
    public ULong component6() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component7() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component8() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value1() {
        return getOasMediaTypeId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getDescription();
    }

    @Override
    public ULong value4() {
        return getOwnerUserId();
    }

    @Override
    public ULong value5() {
        return getCreatedBy();
    }

    @Override
    public ULong value6() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value7() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value8() {
        return getLastUpdateTimestamp();
    }

    @Override
    public OasMediaTypeRecord value1(ULong value) {
        setOasMediaTypeId(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value3(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value4(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value6(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public OasMediaTypeRecord values(ULong value1, String value2, String value3, ULong value4, ULong value5, ULong value6, LocalDateTime value7, LocalDateTime value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OasMediaTypeRecord
     */
    public OasMediaTypeRecord() {
        super(OasMediaType.OAS_MEDIA_TYPE);
    }

    /**
     * Create a detached, initialised OasMediaTypeRecord
     */
    public OasMediaTypeRecord(ULong oasMediaTypeId, String guid, String description, ULong ownerUserId, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(OasMediaType.OAS_MEDIA_TYPE);

        setOasMediaTypeId(oasMediaTypeId);
        setGuid(guid);
        setDescription(description);
        setOwnerUserId(ownerUserId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        resetChangedOnNotNull();
    }
}
