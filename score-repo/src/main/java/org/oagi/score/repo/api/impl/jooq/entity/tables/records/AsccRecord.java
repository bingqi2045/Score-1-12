/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Ascc;

import java.time.LocalDateTime;


/**
 * An ASCC represents a relationship/association between two ACCs through an
 * ASCCP. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccRecord extends UpdatableRecordImpl<AsccRecord> implements Record20<String, String, Integer, Integer, Integer, String, String, String, String, String, Byte, String, String, String, String, LocalDateTime, LocalDateTime, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.ascc.ascc_id</code>. Primary, internal database
     * key.
     */
    public void setAsccId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.ascc.ascc_id</code>. Primary, internal database
     * key.
     */
    public String getAsccId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.ascc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.ascc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.ascc.cardinality_min</code>. Minimum occurrence of
     * the TO_ASCCP_ID. The valid values are non-negative integer.
     */
    public void setCardinalityMin(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.ascc.cardinality_min</code>. Minimum occurrence of
     * the TO_ASCCP_ID. The valid values are non-negative integer.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>oagi.ascc.cardinality_max</code>. Maximum cardinality of
     * the TO_ASCCP_ID. A valid value is integer -1 and up. Specifically, -1
     * means unbounded. 0 means prohibited or not to use.
     */
    public void setCardinalityMax(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.ascc.cardinality_max</code>. Maximum cardinality of
     * the TO_ASCCP_ID. A valid value is integer -1 and up. Specifically, -1
     * means unbounded. 0 means prohibited or not to use.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>oagi.ascc.seq_key</code>. @deprecated since 2.0.0. This
     * indicates the order of the associations among other siblings. A valid
     * value is positive integer. The SEQ_KEY at the CC side is localized. In
     * other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs
     * of the former ACC starts at 1 again.
     */
    public void setSeqKey(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.ascc.seq_key</code>. @deprecated since 2.0.0. This
     * indicates the order of the associations among other siblings. A valid
     * value is positive integer. The SEQ_KEY at the CC side is localized. In
     * other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs
     * of the former ACC starts at 1 again.
     */
    public Integer getSeqKey() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>oagi.ascc.from_acc_id</code>. FROM_ACC_ID is a foreign
     * key pointing to an ACC record. It is basically pointing to a parent data
     * element (type) of the TO_ASCCP_ID.
     */
    public void setFromAccId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.ascc.from_acc_id</code>. FROM_ACC_ID is a foreign
     * key pointing to an ACC record. It is basically pointing to a parent data
     * element (type) of the TO_ASCCP_ID.
     */
    public String getFromAccId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.ascc.to_asccp_id</code>. TO_ASCCP_ID is a foreign
     * key to an ASCCP table record. It is basically pointing to a child data
     * element of the FROM_ACC_ID.
     */
    public void setToAsccpId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.ascc.to_asccp_id</code>. TO_ASCCP_ID is a foreign
     * key to an ASCCP table record. It is basically pointing to a child data
     * element of the FROM_ACC_ID.
     */
    public String getToAsccpId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.ascc.den</code>. DEN (dictionary entry name) of the
     * ASCC. This column can be derived from Qualifier and OBJECT_CLASS_TERM of
     * the FROM_ACC_ID and DEN of the TO_ASCCP_ID as Qualifier + "_ " +
     * OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public void setDen(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.ascc.den</code>. DEN (dictionary entry name) of the
     * ASCC. This column can be derived from Qualifier and OBJECT_CLASS_TERM of
     * the FROM_ACC_ID and DEN of the TO_ASCCP_ID as Qualifier + "_ " +
     * OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public String getDen() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.ascc.definition</code>. This is a documentation or
     * description of the ASCC. Since ASCC is business context independent, this
     * is a business context independent description of the ASCC. Since there
     * are definitions also in the ASCCP (as referenced by the TO_ASCCP_ID
     * column) and the ACC under that ASCCP, definition in the ASCC is a
     * specific description about the relationship between the ACC (as in
     * FROM_ACC_ID) and the ASCCP.
     */
    public void setDefinition(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.ascc.definition</code>. This is a documentation or
     * description of the ASCC. Since ASCC is business context independent, this
     * is a business context independent description of the ASCC. Since there
     * are definitions also in the ASCCP (as referenced by the TO_ASCCP_ID
     * column) and the ACC under that ASCCP, definition in the ASCC is a
     * specific description about the relationship between the ACC (as in
     * FROM_ACC_ID) and the ASCCP.
     */
    public String getDefinition() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.ascc.definition_source</code>. This is typically a
     * URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.ascc.definition_source</code>. This is typically a
     * URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.ascc.is_deprecated</code>. Indicates whether the CC
     * is deprecated and should not be reused (i.e., no new reference to this
     * record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.ascc.is_deprecated</code>. Indicates whether the CC
     * is deprecated and should not be reused (i.e., no new reference to this
     * record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(10);
    }

    /**
     * Setter for <code>oagi.ascc.replacement_ascc_id</code>. This refers to a
     * replacement if the record is deprecated.
     */
    public void setReplacementAsccId(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.ascc.replacement_ascc_id</code>. This refers to a
     * replacement if the record is deprecated.
     */
    public String getReplacementAsccId() {
        return (String) get(11);
    }

    /**
     * Setter for <code>oagi.ascc.created_by</code>. A foreign key to the
     * APP_USER table referring to the user who creates the entity.
     * 
     * This column never change between the history and the current record for a
     * given revision. The history record should have the same value as that of
     * its current record.
     */
    public void setCreatedBy(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.ascc.created_by</code>. A foreign key to the
     * APP_USER table referring to the user who creates the entity.
     * 
     * This column never change between the history and the current record for a
     * given revision. The history record should have the same value as that of
     * its current record.
     */
    public String getCreatedBy() {
        return (String) get(12);
    }

    /**
     * Setter for <code>oagi.ascc.owner_user_id</code>. Foreign key to the
     * APP_USER table. This is the user who owns the entity, is allowed to edit
     * the entity, and who can transfer the ownership to another user.
     * 
     * The ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership.
     */
    public void setOwnerUserId(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.ascc.owner_user_id</code>. Foreign key to the
     * APP_USER table. This is the user who owns the entity, is allowed to edit
     * the entity, and who can transfer the ownership to another user.
     * 
     * The ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership.
     */
    public String getOwnerUserId() {
        return (String) get(13);
    }

    /**
     * Setter for <code>oagi.ascc.last_updated_by</code>. A foreign key to the
     * APP_USER table referring to the last user who has updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.ascc.last_updated_by</code>. A foreign key to the
     * APP_USER table referring to the last user who has updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public String getLastUpdatedBy() {
        return (String) get(14);
    }

    /**
     * Setter for <code>oagi.ascc.creation_timestamp</code>. Timestamp when the
     * revision of the ASCC was created. 
     * 
     * This never change for a revision.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.ascc.creation_timestamp</code>. Timestamp when the
     * revision of the ASCC was created. 
     * 
     * This never change for a revision.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>oagi.ascc.last_update_timestamp</code>. The timestamp
     * when the record was last updated.
     * 
     * The value of this column in the latest history record should be the same
     * as that of the current record. This column keeps the record of when the
     * change has occurred.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.ascc.last_update_timestamp</code>. The timestamp
     * when the record was last updated.
     * 
     * The value of this column in the latest history record should be the same
     * as that of the current record. This column keeps the record of when the
     * change has occurred.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(16);
    }

    /**
     * Setter for <code>oagi.ascc.state</code>. Deleted, WIP, Draft, QA,
     * Candidate, Production, Release Draft, Published. This the revision life
     * cycle state of the BCC.
     * 
     * State change can't be undone. But the history record can still keep the
     * records of when the state was changed.
     */
    public void setState(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.ascc.state</code>. Deleted, WIP, Draft, QA,
     * Candidate, Production, Release Draft, Published. This the revision life
     * cycle state of the BCC.
     * 
     * State change can't be undone. But the history record can still keep the
     * records of when the state was changed.
     */
    public String getState() {
        return (String) get(17);
    }

    /**
     * Setter for <code>oagi.ascc.prev_ascc_id</code>. A self-foreign key to
     * indicate the previous history record.
     */
    public void setPrevAsccId(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.ascc.prev_ascc_id</code>. A self-foreign key to
     * indicate the previous history record.
     */
    public String getPrevAsccId() {
        return (String) get(18);
    }

    /**
     * Setter for <code>oagi.ascc.next_ascc_id</code>. A self-foreign key to
     * indicate the next history record.
     */
    public void setNextAsccId(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.ascc.next_ascc_id</code>. A self-foreign key to
     * indicate the next history record.
     */
    public String getNextAsccId() {
        return (String) get(19);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record20 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row20<String, String, Integer, Integer, Integer, String, String, String, String, String, Byte, String, String, String, String, LocalDateTime, LocalDateTime, String, String, String> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    @Override
    public Row20<String, String, Integer, Integer, Integer, String, String, String, String, String, Byte, String, String, String, String, LocalDateTime, LocalDateTime, String, String, String> valuesRow() {
        return (Row20) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
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
    public Field<String> field6() {
        return Ascc.ASCC.FROM_ACC_ID;
    }

    @Override
    public Field<String> field7() {
        return Ascc.ASCC.TO_ASCCP_ID;
    }

    @Override
    public Field<String> field8() {
        return Ascc.ASCC.DEN;
    }

    @Override
    public Field<String> field9() {
        return Ascc.ASCC.DEFINITION;
    }

    @Override
    public Field<String> field10() {
        return Ascc.ASCC.DEFINITION_SOURCE;
    }

    @Override
    public Field<Byte> field11() {
        return Ascc.ASCC.IS_DEPRECATED;
    }

    @Override
    public Field<String> field12() {
        return Ascc.ASCC.REPLACEMENT_ASCC_ID;
    }

    @Override
    public Field<String> field13() {
        return Ascc.ASCC.CREATED_BY;
    }

    @Override
    public Field<String> field14() {
        return Ascc.ASCC.OWNER_USER_ID;
    }

    @Override
    public Field<String> field15() {
        return Ascc.ASCC.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return Ascc.ASCC.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field17() {
        return Ascc.ASCC.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field18() {
        return Ascc.ASCC.STATE;
    }

    @Override
    public Field<String> field19() {
        return Ascc.ASCC.PREV_ASCC_ID;
    }

    @Override
    public Field<String> field20() {
        return Ascc.ASCC.NEXT_ASCC_ID;
    }

    @Override
    public String component1() {
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
    public String component6() {
        return getFromAccId();
    }

    @Override
    public String component7() {
        return getToAsccpId();
    }

    @Override
    public String component8() {
        return getDen();
    }

    @Override
    public String component9() {
        return getDefinition();
    }

    @Override
    public String component10() {
        return getDefinitionSource();
    }

    @Override
    public Byte component11() {
        return getIsDeprecated();
    }

    @Override
    public String component12() {
        return getReplacementAsccId();
    }

    @Override
    public String component13() {
        return getCreatedBy();
    }

    @Override
    public String component14() {
        return getOwnerUserId();
    }

    @Override
    public String component15() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component16() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component17() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String component18() {
        return getState();
    }

    @Override
    public String component19() {
        return getPrevAsccId();
    }

    @Override
    public String component20() {
        return getNextAsccId();
    }

    @Override
    public String value1() {
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
    public String value6() {
        return getFromAccId();
    }

    @Override
    public String value7() {
        return getToAsccpId();
    }

    @Override
    public String value8() {
        return getDen();
    }

    @Override
    public String value9() {
        return getDefinition();
    }

    @Override
    public String value10() {
        return getDefinitionSource();
    }

    @Override
    public Byte value11() {
        return getIsDeprecated();
    }

    @Override
    public String value12() {
        return getReplacementAsccId();
    }

    @Override
    public String value13() {
        return getCreatedBy();
    }

    @Override
    public String value14() {
        return getOwnerUserId();
    }

    @Override
    public String value15() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value16() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value17() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value18() {
        return getState();
    }

    @Override
    public String value19() {
        return getPrevAsccId();
    }

    @Override
    public String value20() {
        return getNextAsccId();
    }

    @Override
    public AsccRecord value1(String value) {
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
    public AsccRecord value6(String value) {
        setFromAccId(value);
        return this;
    }

    @Override
    public AsccRecord value7(String value) {
        setToAsccpId(value);
        return this;
    }

    @Override
    public AsccRecord value8(String value) {
        setDen(value);
        return this;
    }

    @Override
    public AsccRecord value9(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AsccRecord value10(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public AsccRecord value11(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public AsccRecord value12(String value) {
        setReplacementAsccId(value);
        return this;
    }

    @Override
    public AsccRecord value13(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsccRecord value14(String value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public AsccRecord value15(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsccRecord value16(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsccRecord value17(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsccRecord value18(String value) {
        setState(value);
        return this;
    }

    @Override
    public AsccRecord value19(String value) {
        setPrevAsccId(value);
        return this;
    }

    @Override
    public AsccRecord value20(String value) {
        setNextAsccId(value);
        return this;
    }

    @Override
    public AsccRecord values(String value1, String value2, Integer value3, Integer value4, Integer value5, String value6, String value7, String value8, String value9, String value10, Byte value11, String value12, String value13, String value14, String value15, LocalDateTime value16, LocalDateTime value17, String value18, String value19, String value20) {
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
    public AsccRecord(String asccId, String guid, Integer cardinalityMin, Integer cardinalityMax, Integer seqKey, String fromAccId, String toAsccpId, String den, String definition, String definitionSource, Byte isDeprecated, String replacementAsccId, String createdBy, String ownerUserId, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, String prevAsccId, String nextAsccId) {
        super(Ascc.ASCC);

        setAsccId(asccId);
        setGuid(guid);
        setCardinalityMin(cardinalityMin);
        setCardinalityMax(cardinalityMax);
        setSeqKey(seqKey);
        setFromAccId(fromAccId);
        setToAsccpId(toAsccpId);
        setDen(den);
        setDefinition(definition);
        setDefinitionSource(definitionSource);
        setIsDeprecated(isDeprecated);
        setReplacementAsccId(replacementAsccId);
        setCreatedBy(createdBy);
        setOwnerUserId(ownerUserId);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setState(state);
        setPrevAsccId(prevAsccId);
        setNextAsccId(nextAsccId);
    }
}
