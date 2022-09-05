/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CtxScheme;


/**
 * This table represents a context scheme (a classification scheme) for a
 * context category.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxSchemeRecord extends UpdatableRecordImpl<CtxSchemeRecord> implements Record13<String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.ctx_scheme.ctx_scheme_id</code>. Primary, internal
     * database key.
     */
    public void setCtxSchemeId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.ctx_scheme_id</code>. Primary, internal
     * database key.
     */
    public String getCtxSchemeId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.guid</code>. A globally unique
     * identifier (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.guid</code>. A globally unique
     * identifier (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_id</code>. External
     * identification of the scheme. 
     */
    public void setSchemeId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_id</code>. External
     * identification of the scheme. 
     */
    public String getSchemeId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_name</code>. Pretty print name of
     * the context scheme.
     */
    public void setSchemeName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_name</code>. Pretty print name of
     * the context scheme.
     */
    public String getSchemeName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.description</code>. Description of the
     * context scheme.
     */
    public void setDescription(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.description</code>. Description of the
     * context scheme.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_agency_id</code>. Identification
     * of the agency maintaining the scheme. This column currently does not use
     * the AGENCY_ID_LIST table. It is just a free form text at this point.
     */
    public void setSchemeAgencyId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_agency_id</code>. Identification
     * of the agency maintaining the scheme. This column currently does not use
     * the AGENCY_ID_LIST table. It is just a free form text at this point.
     */
    public String getSchemeAgencyId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_version_id</code>. Version number
     * of the context scheme.
     */
    public void setSchemeVersionId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_version_id</code>. Version number
     * of the context scheme.
     */
    public String getSchemeVersionId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.ctx_category_id</code>. This the foreign
     * key to the CTX_CATEGORY table. It identifies the context category
     * associated with this context scheme.
     */
    public void setCtxCategoryId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.ctx_category_id</code>. This the foreign
     * key to the CTX_CATEGORY table. It identifies the context category
     * associated with this context scheme.
     */
    public String getCtxCategoryId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.code_list_id</code>. This is the foreign
     * key to the CODE_LIST table. It identifies the code list associated with
     * this context scheme.
     */
    public void setCodeListId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.code_list_id</code>. This is the foreign
     * key to the CODE_LIST table. It identifies the code list associated with
     * this context scheme.
     */
    public String getCodeListId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this context scheme.
     */
    public void setCreatedBy(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this context scheme.
     */
    public String getCreatedBy() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.last_updated_by</code>. Foreign key to
     * the APP_USER table. It identifies the user who last updated the context
     * scheme.
     */
    public void setLastUpdatedBy(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.last_updated_by</code>. Foreign key to
     * the APP_USER table. It identifies the user who last updated the context
     * scheme.
     */
    public String getLastUpdatedBy() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.creation_timestamp</code>. Timestamp
     * when the scheme was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.creation_timestamp</code>. Timestamp
     * when the scheme was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.last_update_timestamp</code>. Timestamp
     * when the scheme was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.last_update_timestamp</code>. Timestamp
     * when the scheme was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return CtxScheme.CTX_SCHEME.CTX_SCHEME_ID;
    }

    @Override
    public Field<String> field2() {
        return CtxScheme.CTX_SCHEME.GUID;
    }

    @Override
    public Field<String> field3() {
        return CtxScheme.CTX_SCHEME.SCHEME_ID;
    }

    @Override
    public Field<String> field4() {
        return CtxScheme.CTX_SCHEME.SCHEME_NAME;
    }

    @Override
    public Field<String> field5() {
        return CtxScheme.CTX_SCHEME.DESCRIPTION;
    }

    @Override
    public Field<String> field6() {
        return CtxScheme.CTX_SCHEME.SCHEME_AGENCY_ID;
    }

    @Override
    public Field<String> field7() {
        return CtxScheme.CTX_SCHEME.SCHEME_VERSION_ID;
    }

    @Override
    public Field<String> field8() {
        return CtxScheme.CTX_SCHEME.CTX_CATEGORY_ID;
    }

    @Override
    public Field<String> field9() {
        return CtxScheme.CTX_SCHEME.CODE_LIST_ID;
    }

    @Override
    public Field<String> field10() {
        return CtxScheme.CTX_SCHEME.CREATED_BY;
    }

    @Override
    public Field<String> field11() {
        return CtxScheme.CTX_SCHEME.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return CtxScheme.CTX_SCHEME.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field13() {
        return CtxScheme.CTX_SCHEME.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public String component1() {
        return getCtxSchemeId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getSchemeId();
    }

    @Override
    public String component4() {
        return getSchemeName();
    }

    @Override
    public String component5() {
        return getDescription();
    }

    @Override
    public String component6() {
        return getSchemeAgencyId();
    }

    @Override
    public String component7() {
        return getSchemeVersionId();
    }

    @Override
    public String component8() {
        return getCtxCategoryId();
    }

    @Override
    public String component9() {
        return getCodeListId();
    }

    @Override
    public String component10() {
        return getCreatedBy();
    }

    @Override
    public String component11() {
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
    public String value1() {
        return getCtxSchemeId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getSchemeId();
    }

    @Override
    public String value4() {
        return getSchemeName();
    }

    @Override
    public String value5() {
        return getDescription();
    }

    @Override
    public String value6() {
        return getSchemeAgencyId();
    }

    @Override
    public String value7() {
        return getSchemeVersionId();
    }

    @Override
    public String value8() {
        return getCtxCategoryId();
    }

    @Override
    public String value9() {
        return getCodeListId();
    }

    @Override
    public String value10() {
        return getCreatedBy();
    }

    @Override
    public String value11() {
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
    public CtxSchemeRecord value1(String value) {
        setCtxSchemeId(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value3(String value) {
        setSchemeId(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value4(String value) {
        setSchemeName(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value5(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value6(String value) {
        setSchemeAgencyId(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value7(String value) {
        setSchemeVersionId(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value8(String value) {
        setCtxCategoryId(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value9(String value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value10(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value11(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value12(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public CtxSchemeRecord value13(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public CtxSchemeRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, String value11, LocalDateTime value12, LocalDateTime value13) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CtxSchemeRecord
     */
    public CtxSchemeRecord() {
        super(CtxScheme.CTX_SCHEME);
    }

    /**
     * Create a detached, initialised CtxSchemeRecord
     */
    public CtxSchemeRecord(String ctxSchemeId, String guid, String schemeId, String schemeName, String description, String schemeAgencyId, String schemeVersionId, String ctxCategoryId, String codeListId, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(CtxScheme.CTX_SCHEME);

        setCtxSchemeId(ctxSchemeId);
        setGuid(guid);
        setSchemeId(schemeId);
        setSchemeName(schemeName);
        setDescription(description);
        setSchemeAgencyId(schemeAgencyId);
        setSchemeVersionId(schemeVersionId);
        setCtxCategoryId(ctxCategoryId);
        setCodeListId(codeListId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
