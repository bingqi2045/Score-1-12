/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Release;


/**
 * The is table store the release information.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ReleaseRecord extends UpdatableRecordImpl<ReleaseRecord> implements Record9<ULong, String, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer> {

    private static final long serialVersionUID = -507363041;

    /**
     * Setter for <code>oagi.release.release_id</code>. RELEASE_ID must be an incremental integer. RELEASE_ID that is more than another RELEASE_ID is interpreted to be released later than the other.
     */
    public void setReleaseId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.release.release_id</code>. RELEASE_ID must be an incremental integer. RELEASE_ID that is more than another RELEASE_ID is interpreted to be released later than the other.
     */
    public ULong getReleaseId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.release.release_num</code>. Release number such has 10.0, 10.1, etc. 
     */
    public void setReleaseNum(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.release.release_num</code>. Release number such has 10.0, 10.1, etc. 
     */
    public String getReleaseNum() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.release.release_note</code>. Description or note associated with the release.
     */
    public void setReleaseNote(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.release.release_note</code>. Description or note associated with the release.
     */
    public String getReleaseNote() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.release.namespace_id</code>. Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.
     */
    public void setNamespaceId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.release.namespace_id</code>. Foreign key to the NAMESPACE table. It identifies the namespace used with the release. It is particularly useful for a library that uses a single namespace such like the OAGIS 10.x. A library that uses multiple namespace but has a main namespace may also use this column as a specific namespace can be override at the module level.
     */
    public ULong getNamespaceId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.release.created_by</code>. Foreign key to the APP_USER table identifying user who created the namespace.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.release.created_by</code>. Foreign key to the APP_USER table identifying user who created the namespace.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.release.last_updated_by</code>. Foreign key to the APP_USER table identifying the user who last updated the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.release.last_updated_by</code>. Foreign key to the APP_USER table identifying the user who last updated the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.release.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public void setCreationTimestamp(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.release.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>oagi.release.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.release.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(7);
    }

    /**
     * Setter for <code>oagi.release.state</code>. 1 = DRAFT, 2 = FINAL. This the revision life cycle state of the Release.
     */
    public void setState(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.release.state</code>. 1 = DRAFT, 2 = FINAL. This the revision life cycle state of the Release.
     */
    public Integer getState() {
        return (Integer) get(8);
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
    public Row9<ULong, String, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, String, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Release.RELEASE.RELEASE_ID;
    }

    @Override
    public Field<String> field2() {
        return Release.RELEASE.RELEASE_NUM;
    }

    @Override
    public Field<String> field3() {
        return Release.RELEASE.RELEASE_NOTE;
    }

    @Override
    public Field<ULong> field4() {
        return Release.RELEASE.NAMESPACE_ID;
    }

    @Override
    public Field<ULong> field5() {
        return Release.RELEASE.CREATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return Release.RELEASE.LAST_UPDATED_BY;
    }

    @Override
    public Field<Timestamp> field7() {
        return Release.RELEASE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<Timestamp> field8() {
        return Release.RELEASE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<Integer> field9() {
        return Release.RELEASE.STATE;
    }

    @Override
    public ULong component1() {
        return getReleaseId();
    }

    @Override
    public String component2() {
        return getReleaseNum();
    }

    @Override
    public String component3() {
        return getReleaseNote();
    }

    @Override
    public ULong component4() {
        return getNamespaceId();
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
    public Timestamp component7() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp component8() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Integer component9() {
        return getState();
    }

    @Override
    public ULong value1() {
        return getReleaseId();
    }

    @Override
    public String value2() {
        return getReleaseNum();
    }

    @Override
    public String value3() {
        return getReleaseNote();
    }

    @Override
    public ULong value4() {
        return getNamespaceId();
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
    public Timestamp value7() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp value8() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Integer value9() {
        return getState();
    }

    @Override
    public ReleaseRecord value1(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public ReleaseRecord value2(String value) {
        setReleaseNum(value);
        return this;
    }

    @Override
    public ReleaseRecord value3(String value) {
        setReleaseNote(value);
        return this;
    }

    @Override
    public ReleaseRecord value4(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public ReleaseRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ReleaseRecord value6(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ReleaseRecord value7(Timestamp value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ReleaseRecord value8(Timestamp value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ReleaseRecord value9(Integer value) {
        setState(value);
        return this;
    }

    @Override
    public ReleaseRecord values(ULong value1, String value2, String value3, ULong value4, ULong value5, ULong value6, Timestamp value7, Timestamp value8, Integer value9) {
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
     * Create a detached ReleaseRecord
     */
    public ReleaseRecord() {
        super(Release.RELEASE);
    }

    /**
     * Create a detached, initialised ReleaseRecord
     */
    public ReleaseRecord(ULong releaseId, String releaseNum, String releaseNote, ULong namespaceId, ULong createdBy, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp, Integer state) {
        super(Release.RELEASE);

        set(0, releaseId);
        set(1, releaseNum);
        set(2, releaseNote);
        set(3, namespaceId);
        set(4, createdBy);
        set(5, lastUpdatedBy);
        set(6, creationTimestamp);
        set(7, lastUpdateTimestamp);
        set(8, state);
    }
}
