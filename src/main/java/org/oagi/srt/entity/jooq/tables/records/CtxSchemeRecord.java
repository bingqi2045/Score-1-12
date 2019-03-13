/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CtxScheme;


/**
 * This table represents a context scheme (a classification scheme) for a 
 * context category.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CtxSchemeRecord extends UpdatableRecordImpl<CtxSchemeRecord> implements Record13<ULong, String, String, String, String, String, String, ULong, ULong, ULong, ULong, Timestamp, Timestamp> {

    private static final long serialVersionUID = -974100283;

    /**
     * Setter for <code>oagi.ctx_scheme.ctx_scheme_id</code>. Internal, primary, database key.
     */
    public void setCtxSchemeId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.ctx_scheme_id</code>. Internal, primary, database key.
     */
    public ULong getCtxSchemeId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.guid</code>. GUID of the classification scheme. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.guid</code>. GUID of the classification scheme. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_id</code>. External identification of the scheme. 
     */
    public void setSchemeId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_id</code>. External identification of the scheme. 
     */
    public String getSchemeId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_name</code>. Pretty print name of the context scheme.
     */
    public void setSchemeName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_name</code>. Pretty print name of the context scheme.
     */
    public String getSchemeName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.description</code>. Description of the context scheme.
     */
    public void setDescription(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.description</code>. Description of the context scheme.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_agency_id</code>. Identification of the agency maintaining the scheme. This column currently does not use the AGENCY_ID_LIST table. It is just a free form text at this point.
     */
    public void setSchemeAgencyId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_agency_id</code>. Identification of the agency maintaining the scheme. This column currently does not use the AGENCY_ID_LIST table. It is just a free form text at this point.
     */
    public String getSchemeAgencyId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.scheme_version_id</code>. Version number of the context scheme.
     */
    public void setSchemeVersionId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.scheme_version_id</code>. Version number of the context scheme.
     */
    public String getSchemeVersionId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.ctx_category_id</code>. This the foreign key to the CTX_CATEGORY table. It identifies the context category associated with this context scheme.
     */
    public void setCtxCategoryId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.ctx_category_id</code>. This the foreign key to the CTX_CATEGORY table. It identifies the context category associated with this context scheme.
     */
    public ULong getCtxCategoryId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.code_list_id</code>. This is a foreign key to the CODE_LIST table.
     */
    public void setCodeListId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.code_list_id</code>. This is a foreign key to the CODE_LIST table.
     */
    public ULong getCodeListId() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this context scheme.
     */
    public void setCreatedBy(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this context scheme.
     */
    public ULong getCreatedBy() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the context scheme.
     */
    public void setLastUpdatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the context scheme.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.creation_timestamp</code>. Timestamp when the scheme was created.
     */
    public void setCreationTimestamp(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.creation_timestamp</code>. Timestamp when the scheme was created.
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>oagi.ctx_scheme.last_update_timestamp</code>. Timestamp when the scheme was last updated.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.ctx_scheme.last_update_timestamp</code>. Timestamp when the scheme was last updated.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<ULong, String, String, String, String, String, String, ULong, ULong, ULong, ULong, Timestamp, Timestamp> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row13<ULong, String, String, String, String, String, String, ULong, ULong, ULong, ULong, Timestamp, Timestamp> valuesRow() {
        return (Row13) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field1() {
        return CtxScheme.CTX_SCHEME.CTX_SCHEME_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return CtxScheme.CTX_SCHEME.GUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return CtxScheme.CTX_SCHEME.SCHEME_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return CtxScheme.CTX_SCHEME.SCHEME_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return CtxScheme.CTX_SCHEME.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return CtxScheme.CTX_SCHEME.SCHEME_AGENCY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return CtxScheme.CTX_SCHEME.SCHEME_VERSION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field8() {
        return CtxScheme.CTX_SCHEME.CTX_CATEGORY_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field9() {
        return CtxScheme.CTX_SCHEME.CODE_LIST_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field10() {
        return CtxScheme.CTX_SCHEME.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field11() {
        return CtxScheme.CTX_SCHEME.LAST_UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field12() {
        return CtxScheme.CTX_SCHEME.CREATION_TIMESTAMP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return CtxScheme.CTX_SCHEME.LAST_UPDATE_TIMESTAMP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getCtxSchemeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getGuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getSchemeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getSchemeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getSchemeAgencyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getSchemeVersionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component8() {
        return getCtxCategoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component9() {
        return getCodeListId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component10() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component11() {
        return getLastUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component12() {
        return getCreationTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component13() {
        return getLastUpdateTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getCtxSchemeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getSchemeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getSchemeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getSchemeAgencyId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getSchemeVersionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value8() {
        return getCtxCategoryId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value9() {
        return getCodeListId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value10() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value11() {
        return getLastUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value12() {
        return getCreationTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value13() {
        return getLastUpdateTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value1(ULong value) {
        setCtxSchemeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value2(String value) {
        setGuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value3(String value) {
        setSchemeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value4(String value) {
        setSchemeName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value5(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value6(String value) {
        setSchemeAgencyId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value7(String value) {
        setSchemeVersionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value8(ULong value) {
        setCtxCategoryId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value9(ULong value) {
        setCodeListId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value10(ULong value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value11(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value12(Timestamp value) {
        setCreationTimestamp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord value13(Timestamp value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CtxSchemeRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, String value7, ULong value8, ULong value9, ULong value10, ULong value11, Timestamp value12, Timestamp value13) {
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
    public CtxSchemeRecord(ULong ctxSchemeId, String guid, String schemeId, String schemeName, String description, String schemeAgencyId, String schemeVersionId, ULong ctxCategoryId, ULong codeListId, ULong createdBy, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp) {
        super(CtxScheme.CTX_SCHEME);

        set(0, ctxSchemeId);
        set(1, guid);
        set(2, schemeId);
        set(3, schemeName);
        set(4, description);
        set(5, schemeAgencyId);
        set(6, schemeVersionId);
        set(7, ctxCategoryId);
        set(8, codeListId);
        set(9, createdBy);
        set(10, lastUpdatedBy);
        set(11, creationTimestamp);
        set(12, lastUpdateTimestamp);
    }
}
