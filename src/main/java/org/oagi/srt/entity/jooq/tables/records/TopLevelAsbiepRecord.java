/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.TopLevelAsbiep;

import java.time.LocalDateTime;


/**
 * This table indexes the ASBIEP which is a top-level ASBIEP. This table and 
 * the owner_top_level_asbiep_id column in all BIE tables allow all related 
 * BIEs to be retrieved all at once speeding up the profile BOD transactions.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TopLevelAsbiepRecord extends UpdatableRecordImpl<TopLevelAsbiepRecord> implements Record9<ULong, ULong, ULong, LocalDateTime, ULong, ULong, String, String, String> {

    private static final long serialVersionUID = -1562641705;

    /**
     * Setter for <code>oagi.top_level_asbiep.top_level_asbiep_id</code>. A internal, primary database key of an top-level ASBIEP.
     */
    public void setTopLevelAsbiepId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.top_level_asbiep_id</code>. A internal, primary database key of an top-level ASBIEP.
     */
    public ULong getTopLevelAsbiepId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.asbiep_id</code>. Foreign key to the ASBIEP table pointing to a record which is a top-level ASBIEP.
     */
    public void setAsbiepId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.asbiep_id</code>. Foreign key to the ASBIEP table pointing to a record which is a top-level ASBIEP.
     */
    public ULong getAsbiepId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.owner_user_id</code>.
     */
    public void setOwnerUserId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.owner_user_id</code>.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.last_update_timestamp</code>. The timestamp when among all related bie records was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.last_update_timestamp</code>. The timestamp when among all related bie records was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated any related bie records.
     */
    public void setLastUpdatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated any related bie records.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.release_id</code>. Foreign key to the RELEASE table. It identifies the release, for which this module is associated.
     */
    public void setReleaseId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.release_id</code>. Foreign key to the RELEASE table. It identifies the release, for which this module is associated.
     */
    public ULong getReleaseId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.version</code>. This column hold a version number assigned by the user. This column is only used by the top-level ASBIEP. No format of version is enforced.
     */
    public void setVersion(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.version</code>. This column hold a version number assigned by the user. This column is only used by the top-level ASBIEP. No format of version is enforced.
     */
    public String getVersion() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.status</code>. This is different from the STATE column which is CRUD life cycle of an entity. The use case for this is to allow the user to indicate the usage status of a top-level ASBIEP (a profile BOD). An integration architect can use this column. Example values are ?Prototype?, ?Test?, and ?Production?. Only the top-level ASBIEP can use this field.
     */
    public void setStatus(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.status</code>. This is different from the STATE column which is CRUD life cycle of an entity. The use case for this is to allow the user to indicate the usage status of a top-level ASBIEP (a profile BOD). An integration architect can use this column. Example values are ?Prototype?, ?Test?, and ?Production?. Only the top-level ASBIEP can use this field.
     */
    public String getStatus() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.top_level_asbiep.state</code>.
     */
    public void setState(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.top_level_asbiep.state</code>.
     */
    public String getState() {
        return (String) get(8);
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
    public Row9<ULong, ULong, ULong, LocalDateTime, ULong, ULong, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, ULong, ULong, LocalDateTime, ULong, ULong, String, String, String> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID;
    }

    @Override
    public Field<ULong> field2() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.ASBIEP_ID;
    }

    @Override
    public Field<ULong> field3() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.OWNER_USER_ID;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field5() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.LAST_UPDATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.RELEASE_ID;
    }

    @Override
    public Field<String> field7() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.VERSION;
    }

    @Override
    public Field<String> field8() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.STATUS;
    }

    @Override
    public Field<String> field9() {
        return TopLevelAsbiep.TOP_LEVEL_ASBIEP.STATE;
    }

    @Override
    public ULong component1() {
        return getTopLevelAsbiepId();
    }

    @Override
    public ULong component2() {
        return getAsbiepId();
    }

    @Override
    public ULong component3() {
        return getOwnerUserId();
    }

    @Override
    public LocalDateTime component4() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component5() {
        return getLastUpdatedBy();
    }

    @Override
    public ULong component6() {
        return getReleaseId();
    }

    @Override
    public String component7() {
        return getVersion();
    }

    @Override
    public String component8() {
        return getStatus();
    }

    @Override
    public String component9() {
        return getState();
    }

    @Override
    public ULong value1() {
        return getTopLevelAsbiepId();
    }

    @Override
    public ULong value2() {
        return getAsbiepId();
    }

    @Override
    public ULong value3() {
        return getOwnerUserId();
    }

    @Override
    public LocalDateTime value4() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value5() {
        return getLastUpdatedBy();
    }

    @Override
    public ULong value6() {
        return getReleaseId();
    }

    @Override
    public String value7() {
        return getVersion();
    }

    @Override
    public String value8() {
        return getStatus();
    }

    @Override
    public String value9() {
        return getState();
    }

    @Override
    public TopLevelAsbiepRecord value1(ULong value) {
        setTopLevelAsbiepId(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value2(ULong value) {
        setAsbiepId(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value3(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value4(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value5(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value6(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value7(String value) {
        setVersion(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value8(String value) {
        setStatus(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord value9(String value) {
        setState(value);
        return this;
    }

    @Override
    public TopLevelAsbiepRecord values(ULong value1, ULong value2, ULong value3, LocalDateTime value4, ULong value5, ULong value6, String value7, String value8, String value9) {
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
     * Create a detached TopLevelAsbiepRecord
     */
    public TopLevelAsbiepRecord() {
        super(TopLevelAsbiep.TOP_LEVEL_ASBIEP);
    }

    /**
     * Create a detached, initialised TopLevelAsbiepRecord
     */
    public TopLevelAsbiepRecord(ULong topLevelAsbiepId, ULong asbiepId, ULong ownerUserId, LocalDateTime lastUpdateTimestamp, ULong lastUpdatedBy, ULong releaseId, String version, String status, String state) {
        super(TopLevelAsbiep.TOP_LEVEL_ASBIEP);

        set(0, topLevelAsbiepId);
        set(1, asbiepId);
        set(2, ownerUserId);
        set(3, lastUpdateTimestamp);
        set(4, lastUpdatedBy);
        set(5, releaseId);
        set(6, version);
        set(7, status);
        set(8, state);
    }
}