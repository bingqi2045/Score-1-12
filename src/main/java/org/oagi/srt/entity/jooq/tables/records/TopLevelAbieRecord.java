/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.TopLevelAbie;


/**
 * This table indexes the ABIE which is a top-level ABIE. This table and the 
 * owner_top_level_abie_id column in all BIE tables allow all related BIEs 
 * to be retrieved all at once speeding up the profile BOD transactions.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TopLevelAbieRecord extends UpdatableRecordImpl<TopLevelAbieRecord> implements Record7<ULong, ULong, ULong, LocalDateTime, ULong, ULong, String> {

    private static final long serialVersionUID = 1477381681;

    /**
     * Setter for <code>oagi.top_level_abie.top_level_abie_id</code>. A internal, primary database key of an ACC.
     */
    public void setTopLevelAbieId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.top_level_abie_id</code>. A internal, primary database key of an ACC.
     */
    public ULong getTopLevelAbieId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.top_level_abie.abie_id</code>. Foreign key to the ABIE table pointing to a record which is a top-level ABIE.
     */
    public void setAbieId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.abie_id</code>. Foreign key to the ABIE table pointing to a record which is a top-level ABIE.
     */
    public ULong getAbieId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.top_level_abie.owner_user_id</code>.
     */
    public void setOwnerUserId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.owner_user_id</code>.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.top_level_abie.last_update_timestamp</code>. The timestamp when among all related bie records was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.last_update_timestamp</code>. The timestamp when among all related bie records was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>oagi.top_level_abie.last_updated_by</code>. A foreign key referring to the last user who has updated any related bie records.
     */
    public void setLastUpdatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.last_updated_by</code>. A foreign key referring to the last user who has updated any related bie records.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.top_level_abie.release_id</code>. Foreign key to the RELEASE table. It identifies the release, for which this module is associated.
     */
    public void setReleaseId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.release_id</code>. Foreign key to the RELEASE table. It identifies the release, for which this module is associated.
     */
    public ULong getReleaseId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.top_level_abie.state</code>.
     */
    public void setState(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.top_level_abie.state</code>.
     */
    public String getState() {
        return (String) get(6);
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
    public Row7<ULong, ULong, ULong, LocalDateTime, ULong, ULong, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<ULong, ULong, ULong, LocalDateTime, ULong, ULong, String> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return TopLevelAbie.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return TopLevelAbie.TOP_LEVEL_ABIE.ABIE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return TopLevelAbie.TOP_LEVEL_ABIE.OWNER_USER_ID;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return TopLevelAbie.TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field5() {
        return TopLevelAbie.TOP_LEVEL_ABIE.LAST_UPDATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return TopLevelAbie.TOP_LEVEL_ABIE.RELEASE_ID;
    }

    @Override
    public Field<String> field7() {
        return TopLevelAbie.TOP_LEVEL_ABIE.STATE;
    }

    @Override
    public ULong component1() {
        return getTopLevelAbieId();
    }

    @Override
    public ULong component2() {
        return getAbieId();
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
        return getState();
    }

    @Override
    public ULong value1() {
        return getTopLevelAbieId();
    }

    @Override
    public ULong value2() {
        return getAbieId();
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
        return getState();
    }

    @Override
    public TopLevelAbieRecord value1(ULong value) {
        setTopLevelAbieId(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord value2(ULong value) {
        setAbieId(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord value3(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord value4(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord value5(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord value6(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord value7(String value) {
        setState(value);
        return this;
    }

    @Override
    public TopLevelAbieRecord values(ULong value1, ULong value2, ULong value3, LocalDateTime value4, ULong value5, ULong value6, String value7) {
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
     * Create a detached TopLevelAbieRecord
     */
    public TopLevelAbieRecord() {
        super(TopLevelAbie.TOP_LEVEL_ABIE);
    }

    /**
     * Create a detached, initialised TopLevelAbieRecord
     */
    public TopLevelAbieRecord(ULong topLevelAbieId, ULong abieId, ULong ownerUserId, LocalDateTime lastUpdateTimestamp, ULong lastUpdatedBy, ULong releaseId, String state) {
        super(TopLevelAbie.TOP_LEVEL_ABIE);

        set(0, topLevelAbieId);
        set(1, abieId);
        set(2, ownerUserId);
        set(3, lastUpdateTimestamp);
        set(4, lastUpdatedBy);
        set(5, releaseId);
        set(6, state);
    }
}
