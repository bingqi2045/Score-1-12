/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CtxCategory;


/**
 * This table captures the context category. Examples of context categories as
 * described in the CCTS are business process, industry, etc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxCategoryRecord extends UpdatableRecordImpl<CtxCategoryRecord> implements Record8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.ctx_category.ctx_category_id</code>. Primary,
     * internal database key.
     */
    public void setCtxCategoryId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.ctx_category_id</code>. Primary,
     * internal database key.
     */
    public String getCtxCategoryId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.ctx_category.guid</code>. A globally unique
     * identifier (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.guid</code>. A globally unique
     * identifier (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ctx_category.name</code>. Short name of the context
     * category.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.name</code>. Short name of the context
     * category.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.ctx_category.description</code>. Explanation of
     * what the context category is.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.description</code>. Explanation of
     * what the context category is.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.ctx_category.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created the context category.
     */
    public void setCreatedBy(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created the context category.
     */
    public String getCreatedBy() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.ctx_category.last_updated_by</code>. Foreign key to
     * the APP_USER table. It identifies the user who last updated the context
     * category.
     */
    public void setLastUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.last_updated_by</code>. Foreign key to
     * the APP_USER table. It identifies the user who last updated the context
     * category.
     */
    public String getLastUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.ctx_category.creation_timestamp</code>. Timestamp
     * when the context category was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.creation_timestamp</code>. Timestamp
     * when the context category was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.ctx_category.last_update_timestamp</code>.
     * Timestamp when the context category was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.ctx_category.last_update_timestamp</code>.
     * Timestamp when the context category was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return CtxCategory.CTX_CATEGORY.CTX_CATEGORY_ID;
    }

    @Override
    public Field<String> field2() {
        return CtxCategory.CTX_CATEGORY.GUID;
    }

    @Override
    public Field<String> field3() {
        return CtxCategory.CTX_CATEGORY.NAME;
    }

    @Override
    public Field<String> field4() {
        return CtxCategory.CTX_CATEGORY.DESCRIPTION;
    }

    @Override
    public Field<String> field5() {
        return CtxCategory.CTX_CATEGORY.CREATED_BY;
    }

    @Override
    public Field<String> field6() {
        return CtxCategory.CTX_CATEGORY.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return CtxCategory.CTX_CATEGORY.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return CtxCategory.CTX_CATEGORY.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public String component1() {
        return getCtxCategoryId();
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
    public String component4() {
        return getDescription();
    }

    @Override
    public String component5() {
        return getCreatedBy();
    }

    @Override
    public String component6() {
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
    public String value1() {
        return getCtxCategoryId();
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
    public String value4() {
        return getDescription();
    }

    @Override
    public String value5() {
        return getCreatedBy();
    }

    @Override
    public String value6() {
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
    public CtxCategoryRecord value1(String value) {
        setCtxCategoryId(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value4(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value5(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value6(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public CtxCategoryRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public CtxCategoryRecord values(String value1, String value2, String value3, String value4, String value5, String value6, LocalDateTime value7, LocalDateTime value8) {
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
     * Create a detached CtxCategoryRecord
     */
    public CtxCategoryRecord() {
        super(CtxCategory.CTX_CATEGORY);
    }

    /**
     * Create a detached, initialised CtxCategoryRecord
     */
    public CtxCategoryRecord(String ctxCategoryId, String guid, String name, String description, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(CtxCategory.CTX_CATEGORY);

        setCtxCategoryId(ctxCategoryId);
        setGuid(guid);
        setName(name);
        setDescription(description);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
