/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AsccBizterm;


/**
 * The ascc_bizterm table stores information about the aggregation between the
 * business term and ASCC. TODO: Placeholder, definition is missing.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccBiztermRecord extends UpdatableRecordImpl<AsccBiztermRecord> implements Record7<String, String, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.ascc_bizterm.ascc_bizterm_id</code>. An internal,
     * primary database key of an Business term.
     */
    public void setAsccBiztermId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.ascc_bizterm_id</code>. An internal,
     * primary database key of an Business term.
     */
    public String getAsccBiztermId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.ascc_bizterm.business_term_id</code>. An internal
     * ID of the associated business term
     */
    public void setBusinessTermId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.business_term_id</code>. An internal
     * ID of the associated business term
     */
    public String getBusinessTermId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ascc_bizterm.ascc_id</code>. An internal ID of the
     * associated ASCC
     */
    public void setAsccId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.ascc_id</code>. An internal ID of the
     * associated ASCC
     */
    public String getAsccId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.ascc_bizterm.created_by</code>. A foreign key
     * referring to the user who creates the ascc_bizterm record. The creator of
     * the ascc_bizterm is also its owner by default.
     */
    public void setCreatedBy(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.created_by</code>. A foreign key
     * referring to the user who creates the ascc_bizterm record. The creator of
     * the ascc_bizterm is also its owner by default.
     */
    public String getCreatedBy() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.ascc_bizterm.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the ascc_bizterm record. This
     * may be the user who is in the same group as the creator.
     */
    public void setLastUpdatedBy(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the ascc_bizterm record. This
     * may be the user who is in the same group as the creator.
     */
    public String getLastUpdatedBy() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.ascc_bizterm.creation_timestamp</code>. Timestamp
     * when the ascc_bizterm record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.creation_timestamp</code>. Timestamp
     * when the ascc_bizterm record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>oagi.ascc_bizterm.last_update_timestamp</code>. The
     * timestamp when the ascc_bizterm was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.ascc_bizterm.last_update_timestamp</code>. The
     * timestamp when the ascc_bizterm was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<String, String, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return AsccBizterm.ASCC_BIZTERM.ASCC_BIZTERM_ID;
    }

    @Override
    public Field<String> field2() {
        return AsccBizterm.ASCC_BIZTERM.BUSINESS_TERM_ID;
    }

    @Override
    public Field<String> field3() {
        return AsccBizterm.ASCC_BIZTERM.ASCC_ID;
    }

    @Override
    public Field<String> field4() {
        return AsccBizterm.ASCC_BIZTERM.CREATED_BY;
    }

    @Override
    public Field<String> field5() {
        return AsccBizterm.ASCC_BIZTERM.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field6() {
        return AsccBizterm.ASCC_BIZTERM.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return AsccBizterm.ASCC_BIZTERM.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public String component1() {
        return getAsccBiztermId();
    }

    @Override
    public String component2() {
        return getBusinessTermId();
    }

    @Override
    public String component3() {
        return getAsccId();
    }

    @Override
    public String component4() {
        return getCreatedBy();
    }

    @Override
    public String component5() {
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
    public String value1() {
        return getAsccBiztermId();
    }

    @Override
    public String value2() {
        return getBusinessTermId();
    }

    @Override
    public String value3() {
        return getAsccId();
    }

    @Override
    public String value4() {
        return getCreatedBy();
    }

    @Override
    public String value5() {
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
    public AsccBiztermRecord value1(String value) {
        setAsccBiztermId(value);
        return this;
    }

    @Override
    public AsccBiztermRecord value2(String value) {
        setBusinessTermId(value);
        return this;
    }

    @Override
    public AsccBiztermRecord value3(String value) {
        setAsccId(value);
        return this;
    }

    @Override
    public AsccBiztermRecord value4(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsccBiztermRecord value5(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsccBiztermRecord value6(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsccBiztermRecord value7(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsccBiztermRecord values(String value1, String value2, String value3, String value4, String value5, LocalDateTime value6, LocalDateTime value7) {
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
     * Create a detached AsccBiztermRecord
     */
    public AsccBiztermRecord() {
        super(AsccBizterm.ASCC_BIZTERM);
    }

    /**
     * Create a detached, initialised AsccBiztermRecord
     */
    public AsccBiztermRecord(String asccBiztermId, String businessTermId, String asccId, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(AsccBizterm.ASCC_BIZTERM);

        setAsccBiztermId(asccBiztermId);
        setBusinessTermId(businessTermId);
        setAsccId(asccId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
