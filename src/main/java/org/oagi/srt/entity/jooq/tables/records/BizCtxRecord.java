/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.BizCtx;

import java.time.LocalDateTime;


/**
 * This table represents a business context. A business context is a combination
 * of one or more business context values.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class BizCtxRecord extends UpdatableRecordImpl<BizCtxRecord> implements Record7<ULong, String, String, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1645936985;

    /**
     * Setter for <code>oagi.biz_ctx.biz_ctx_id</code>. Primary, internal database key.
     */
    public void setBizCtxId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.biz_ctx_id</code>. Primary, internal database key.
     */
    public ULong getBizCtxId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.biz_ctx.guid</code>. A globally unique identifier (GUID). Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.guid</code>. A globally unique identifier (GUID). Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.biz_ctx.name</code>. Short, descriptive name of the business context.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.name</code>. Short, descriptive name of the business context.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.biz_ctx.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity.
     */
    public void setCreatedBy(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity.
     */
    public ULong getCreatedBy() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.biz_ctx.last_updated_by</code>. Foreign key to the APP_USER table  referring to the last user who has updated the business context.
     */
    public void setLastUpdatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.last_updated_by</code>. Foreign key to the APP_USER table  referring to the last user who has updated the business context.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.biz_ctx.creation_timestamp</code>. Timestamp when the business context record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.creation_timestamp</code>. Timestamp when the business context record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>oagi.biz_ctx.last_update_timestamp</code>. The timestamp when the business context was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.biz_ctx.last_update_timestamp</code>. The timestamp when the business context was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, String, String, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<ULong, String, String, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BizCtx.BIZ_CTX.BIZ_CTX_ID;
    }

    @Override
    public Field<String> field2() {
        return BizCtx.BIZ_CTX.GUID;
    }

    @Override
    public Field<String> field3() {
        return BizCtx.BIZ_CTX.NAME;
    }

    @Override
    public Field<ULong> field4() {
        return BizCtx.BIZ_CTX.CREATED_BY;
    }

    @Override
    public Field<ULong> field5() {
        return BizCtx.BIZ_CTX.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return BizCtx.BIZ_CTX.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return BizCtx.BIZ_CTX.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getBizCtxId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public ULong component4() {
        return getCreatedBy();
    }

    @Override
    public ULong component5() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component6() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component7() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value1() {
        return getBizCtxId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public ULong value4() {
        return getCreatedBy();
    }

    @Override
    public ULong value5() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value6() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value7() {
        return getLastUpdateTimestamp();
    }

    @Override
    public BizCtxRecord value1(ULong value) {
        setBizCtxId(value);
        return this;
    }

    @Override
    public BizCtxRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public BizCtxRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public BizCtxRecord value4(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public BizCtxRecord value5(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public BizCtxRecord value6(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public BizCtxRecord value7(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public BizCtxRecord values(ULong value1, String value2, String value3, ULong value4, ULong value5, LocalDateTime value6, LocalDateTime value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BizCtxRecord
     */
    public BizCtxRecord() {
        super(BizCtx.BIZ_CTX);
    }

    /**
     * Create a detached, initialised BizCtxRecord
     */
    public BizCtxRecord(ULong bizCtxId, String guid, String name, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(BizCtx.BIZ_CTX);

        set(0, bizCtxId);
        set(1, guid);
        set(2, name);
        set(3, createdBy);
        set(4, lastUpdatedBy);
        set(5, creationTimestamp);
        set(6, lastUpdateTimestamp);
    }
}
