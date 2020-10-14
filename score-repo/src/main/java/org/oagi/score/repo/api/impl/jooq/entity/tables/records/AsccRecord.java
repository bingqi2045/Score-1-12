/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record21;
import org.jooq.Row21;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Ascc;


/**
 * An ASCC represents a relationship/association between two ACCs through 
 * an ASCCP. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccRecord extends UpdatableRecordImpl<AsccRecord> implements Record21<ULong, String, Integer, Integer, Integer, ULong, ULong, ULong, String, String, String, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong> {

    private static final long serialVersionUID = 1252646608;

    /**
     * Setter for <code>oagi.ascc.ascc_id</code>. An internal, primary database key of an ASCC.
     */
    public void setAsccId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ascc.ascc_id</code>. An internal, primary database key of an ASCC.
     */
    public ULong getAsccId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.ascc.guid</code>. A globally unique identifier (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ascc.guid</code>. A globally unique identifier (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ascc.cardinality_min</code>. Minimum occurrence of the TO_ASCCP_ID. The valid values are non-negative integer.
     */
    public void setCardinalityMin(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ascc.cardinality_min</code>. Minimum occurrence of the TO_ASCCP_ID. The valid values are non-negative integer.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>oagi.ascc.cardinality_max</code>. Maximum cardinality of the TO_ASCCP_ID. A valid value is integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public void setCardinalityMax(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ascc.cardinality_max</code>. Maximum cardinality of the TO_ASCCP_ID. A valid value is integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>oagi.ascc.seq_key</code>. @deprecated since 2.0.0. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again.
     */
    public void setSeqKey(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ascc.seq_key</code>. @deprecated since 2.0.0. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again.
     */
    public Integer getSeqKey() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>oagi.ascc.seq_key_id</code>.
     */
    public void setSeqKeyId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.ascc.seq_key_id</code>.
     */
    public ULong getSeqKeyId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.ascc.from_acc_id</code>. FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_ASCCP_ID.
     */
    public void setFromAccId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.ascc.from_acc_id</code>. FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_ASCCP_ID.
     */
    public ULong getFromAccId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.ascc.to_asccp_id</code>. TO_ASCCP_ID is a foreign key to an ASCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. 
     */
    public void setToAsccpId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.ascc.to_asccp_id</code>. TO_ASCCP_ID is a foreign key to an ASCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. 
     */
    public ULong getToAsccpId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.ascc.den</code>. DEN (dictionary entry name) of the ASCC. This column can be derived from Qualifier and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_ASCCP_ID as Qualifier + "_ " + OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public void setDen(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.ascc.den</code>. DEN (dictionary entry name) of the ASCC. This column can be derived from Qualifier and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_ASCCP_ID as Qualifier + "_ " + OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public String getDen() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.ascc.definition</code>. This is a documentation or description of the ASCC. Since ASCC is business context independent, this is a business context independent description of the ASCC. Since there are definitions also in the ASCCP (as referenced by the TO_ASCCP_ID column) and the ACC under that ASCCP, definition in the ASCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the ASCCP.
     */
    public void setDefinition(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.ascc.definition</code>. This is a documentation or description of the ASCC. Since ASCC is business context independent, this is a business context independent description of the ASCC. Since there are definitions also in the ASCCP (as referenced by the TO_ASCCP_ID column) and the ACC under that ASCCP, definition in the ASCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the ASCCP.
     */
    public String getDefinition() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.ascc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.ascc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.ascc.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.ascc.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>oagi.ascc.replacement_ascc_id</code>. This refers to a replacement if the record is deprecated.
     */
    public void setReplacementAsccId(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.ascc.replacement_ascc_id</code>. This refers to a replacement if the record is deprecated.
     */
    public ULong getReplacementAsccId() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.ascc.created_by</code>. A foreign key to the APP_USER table referring to the user who creates the entity.

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public void setCreatedBy(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.ascc.created_by</code>. A foreign key to the APP_USER table referring to the user who creates the entity.

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.ascc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public void setOwnerUserId(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.ascc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public ULong getOwnerUserId() {
        return (ULong) get(14);
    }

    /**
     * Setter for <code>oagi.ascc.last_updated_by</code>. A foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.ascc.last_updated_by</code>. A foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(15);
    }

    /**
     * Setter for <code>oagi.ascc.creation_timestamp</code>. Timestamp when the revision of the ASCC was created. 

This never change for a revision.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.ascc.creation_timestamp</code>. Timestamp when the revision of the ASCC was created. 

This never change for a revision.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(16);
    }

    /**
     * Setter for <code>oagi.ascc.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.ascc.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(17);
    }

    /**
     * Setter for <code>oagi.ascc.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCC.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public void setState(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.ascc.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCC.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public String getState() {
        return (String) get(18);
    }

    /**
     * Setter for <code>oagi.ascc.prev_ascc_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevAsccId(ULong value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.ascc.prev_ascc_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevAsccId() {
        return (ULong) get(19);
    }

    /**
     * Setter for <code>oagi.ascc.next_ascc_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextAsccId(ULong value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.ascc.next_ascc_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextAsccId() {
        return (ULong) get(20);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record21 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row21<ULong, String, Integer, Integer, Integer, ULong, ULong, ULong, String, String, String, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    @Override
    public Row21<ULong, String, Integer, Integer, Integer, ULong, ULong, ULong, String, String, String, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong> valuesRow() {
        return (Row21) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Ascc.ASCC.ASCC_ID;
    }

    @Override
    public Field<String> field2() {
        return Ascc.ASCC.GUID;
    }

    @Override
    public Field<Integer> field3() {
        return Ascc.ASCC.CARDINALITY_MIN;
    }

    @Override
    public Field<Integer> field4() {
        return Ascc.ASCC.CARDINALITY_MAX;
    }

    @Override
    public Field<Integer> field5() {
        return Ascc.ASCC.SEQ_KEY;
    }

    @Override
    public Field<ULong> field6() {
        return Ascc.ASCC.SEQ_KEY_ID;
    }

    @Override
    public Field<ULong> field7() {
        return Ascc.ASCC.FROM_ACC_ID;
    }

    @Override
    public Field<ULong> field8() {
        return Ascc.ASCC.TO_ASCCP_ID;
    }

    @Override
    public Field<String> field9() {
        return Ascc.ASCC.DEN;
    }

    @Override
    public Field<String> field10() {
        return Ascc.ASCC.DEFINITION;
    }

    @Override
    public Field<String> field11() {
        return Ascc.ASCC.DEFINITION_SOURCE;
    }

    @Override
    public Field<Byte> field12() {
        return Ascc.ASCC.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field13() {
        return Ascc.ASCC.REPLACEMENT_ASCC_ID;
    }

    @Override
    public Field<ULong> field14() {
        return Ascc.ASCC.CREATED_BY;
    }

    @Override
    public Field<ULong> field15() {
        return Ascc.ASCC.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field16() {
        return Ascc.ASCC.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field17() {
        return Ascc.ASCC.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field18() {
        return Ascc.ASCC.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field19() {
        return Ascc.ASCC.STATE;
    }

    @Override
    public Field<ULong> field20() {
        return Ascc.ASCC.PREV_ASCC_ID;
    }

    @Override
    public Field<ULong> field21() {
        return Ascc.ASCC.NEXT_ASCC_ID;
    }

    @Override
    public ULong component1() {
        return getAsccId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public Integer component3() {
        return getCardinalityMin();
    }

    @Override
    public Integer component4() {
        return getCardinalityMax();
    }

    @Override
    public Integer component5() {
        return getSeqKey();
    }

    @Override
    public ULong component6() {
        return getSeqKeyId();
    }

    @Override
    public ULong component7() {
        return getFromAccId();
    }

    @Override
    public ULong component8() {
        return getToAsccpId();
    }

    @Override
    public String component9() {
        return getDen();
    }

    @Override
    public String component10() {
        return getDefinition();
    }

    @Override
    public String component11() {
        return getDefinitionSource();
    }

    @Override
    public Byte component12() {
        return getIsDeprecated();
    }

    @Override
    public ULong component13() {
        return getReplacementAsccId();
    }

    @Override
    public ULong component14() {
        return getCreatedBy();
    }

    @Override
    public ULong component15() {
        return getOwnerUserId();
    }

    @Override
    public ULong component16() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component17() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component18() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String component19() {
        return getState();
    }

    @Override
    public ULong component20() {
        return getPrevAsccId();
    }

    @Override
    public ULong component21() {
        return getNextAsccId();
    }

    @Override
    public ULong value1() {
        return getAsccId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public Integer value3() {
        return getCardinalityMin();
    }

    @Override
    public Integer value4() {
        return getCardinalityMax();
    }

    @Override
    public Integer value5() {
        return getSeqKey();
    }

    @Override
    public ULong value6() {
        return getSeqKeyId();
    }

    @Override
    public ULong value7() {
        return getFromAccId();
    }

    @Override
    public ULong value8() {
        return getToAsccpId();
    }

    @Override
    public String value9() {
        return getDen();
    }

    @Override
    public String value10() {
        return getDefinition();
    }

    @Override
    public String value11() {
        return getDefinitionSource();
    }

    @Override
    public Byte value12() {
        return getIsDeprecated();
    }

    @Override
    public ULong value13() {
        return getReplacementAsccId();
    }

    @Override
    public ULong value14() {
        return getCreatedBy();
    }

    @Override
    public ULong value15() {
        return getOwnerUserId();
    }

    @Override
    public ULong value16() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value17() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value18() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value19() {
        return getState();
    }

    @Override
    public ULong value20() {
        return getPrevAsccId();
    }

    @Override
    public ULong value21() {
        return getNextAsccId();
    }

    @Override
    public AsccRecord value1(ULong value) {
        setAsccId(value);
        return this;
    }

    @Override
    public AsccRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AsccRecord value3(Integer value) {
        setCardinalityMin(value);
        return this;
    }

    @Override
    public AsccRecord value4(Integer value) {
        setCardinalityMax(value);
        return this;
    }

    @Override
    public AsccRecord value5(Integer value) {
        setSeqKey(value);
        return this;
    }

    @Override
    public AsccRecord value6(ULong value) {
        setSeqKeyId(value);
        return this;
    }

    @Override
    public AsccRecord value7(ULong value) {
        setFromAccId(value);
        return this;
    }

    @Override
    public AsccRecord value8(ULong value) {
        setToAsccpId(value);
        return this;
    }

    @Override
    public AsccRecord value9(String value) {
        setDen(value);
        return this;
    }

    @Override
    public AsccRecord value10(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AsccRecord value11(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public AsccRecord value12(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public AsccRecord value13(ULong value) {
        setReplacementAsccId(value);
        return this;
    }

    @Override
    public AsccRecord value14(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsccRecord value15(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public AsccRecord value16(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsccRecord value17(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsccRecord value18(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsccRecord value19(String value) {
        setState(value);
        return this;
    }

    @Override
    public AsccRecord value20(ULong value) {
        setPrevAsccId(value);
        return this;
    }

    @Override
    public AsccRecord value21(ULong value) {
        setNextAsccId(value);
        return this;
    }

    @Override
    public AsccRecord values(ULong value1, String value2, Integer value3, Integer value4, Integer value5, ULong value6, ULong value7, ULong value8, String value9, String value10, String value11, Byte value12, ULong value13, ULong value14, ULong value15, ULong value16, LocalDateTime value17, LocalDateTime value18, String value19, ULong value20, ULong value21) {
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
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        value20(value20);
        value21(value21);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AsccRecord
     */
    public AsccRecord() {
        super(Ascc.ASCC);
    }

    /**
     * Create a detached, initialised AsccRecord
     */
    public AsccRecord(ULong asccId, String guid, Integer cardinalityMin, Integer cardinalityMax, Integer seqKey, ULong seqKeyId, ULong fromAccId, ULong toAsccpId, String den, String definition, String definitionSource, Byte isDeprecated, ULong replacementAsccId, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, ULong prevAsccId, ULong nextAsccId) {
        super(Ascc.ASCC);

        set(0, asccId);
        set(1, guid);
        set(2, cardinalityMin);
        set(3, cardinalityMax);
        set(4, seqKey);
        set(5, seqKeyId);
        set(6, fromAccId);
        set(7, toAsccpId);
        set(8, den);
        set(9, definition);
        set(10, definitionSource);
        set(11, isDeprecated);
        set(12, replacementAsccId);
        set(13, createdBy);
        set(14, ownerUserId);
        set(15, lastUpdatedBy);
        set(16, creationTimestamp);
        set(17, lastUpdateTimestamp);
        set(18, state);
        set(19, prevAsccId);
        set(20, nextAsccId);
    }
}
