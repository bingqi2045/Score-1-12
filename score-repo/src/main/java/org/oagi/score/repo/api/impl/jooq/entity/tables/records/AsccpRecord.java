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
import org.oagi.score.repo.api.impl.jooq.entity.tables.Asccp;


/**
 * An ASCCP specifies a role (or property) an ACC may play under another ACC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccpRecord extends UpdatableRecordImpl<AsccpRecord> implements Record21<ULong, String, String, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, Byte, Byte, ULong, Byte, ULong, ULong> {

    private static final long serialVersionUID = -1050227986;

    /**
     * Setter for <code>oagi.asccp.asccp_id</code>. An internal, primary database key of an ASCCP.
     */
    public void setAsccpId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.asccp.asccp_id</code>. An internal, primary database key of an ASCCP.
     */
    public ULong getAsccpId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.asccp.guid</code>. A globally unique identifier (GUID) of an ASCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.asccp.guid</code>. A globally unique identifier (GUID) of an ASCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.asccp.type</code>. The Type of the ASCCP. List: Default, Extension 
     */
    public void setType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.asccp.type</code>. The Type of the ASCCP. List: Default, Extension 
     */
    public String getType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.asccp.property_term</code>. The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. There must be only one ASCCP without a Property_Term for a particular ACC.
     */
    public void setPropertyTerm(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.asccp.property_term</code>. The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. There must be only one ASCCP without a Property_Term for a particular ACC.
     */
    public String getPropertyTerm() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.asccp.definition</code>. Description of the ASCCP.
     */
    public void setDefinition(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.asccp.definition</code>. Description of the ASCCP.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.asccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.asccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.asccp.role_of_acc_id</code>. The ACC from which this ASCCP is created (ASCCP applies role to the ACC).
     */
    public void setRoleOfAccId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.asccp.role_of_acc_id</code>. The ACC from which this ASCCP is created (ASCCP applies role to the ACC).
     */
    public ULong getRoleOfAccId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.asccp.den</code>. The dictionary entry name of the ASCCP.
     */
    public void setDen(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.asccp.den</code>. The dictionary entry name of the ASCCP.
     */
    public String getDen() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.asccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public void setCreatedBy(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.asccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.asccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public void setOwnerUserId(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.asccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public ULong getOwnerUserId() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.asccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.asccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.asccp.creation_timestamp</code>. Timestamp when the revision of the ASCCP was created. 

This never change for a revision.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.asccp.creation_timestamp</code>. Timestamp when the revision of the ASCCP was created. 

This never change for a revision.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>oagi.asccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.asccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(12);
    }

    /**
     * Setter for <code>oagi.asccp.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the ASCCP.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public void setState(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.asccp.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the ASCCP.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public String getState() {
        return (String) get(13);
    }

    /**
     * Setter for <code>oagi.asccp.namespace_id</code>. Foreign key to the Namespace table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public void setNamespaceId(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.asccp.namespace_id</code>. Foreign key to the Namespace table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public ULong getNamespaceId() {
        return (ULong) get(14);
    }

    /**
     * Setter for <code>oagi.asccp.reusable_indicator</code>. This indicates whether the ASCCP can be used by more than one ASCC. This maps directly to the XML schema local element declaration.
     */
    public void setReusableIndicator(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.asccp.reusable_indicator</code>. This indicates whether the ASCCP can be used by more than one ASCC. This maps directly to the XML schema local element declaration.
     */
    public Byte getReusableIndicator() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>oagi.asccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.asccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(16);
    }

    /**
     * Setter for <code>oagi.asccp.replacement_asccp_id</code>. This refers to a replacement if the record is deprecated.
     */
    public void setReplacementAsccpId(ULong value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.asccp.replacement_asccp_id</code>. This refers to a replacement if the record is deprecated.
     */
    public ULong getReplacementAsccpId() {
        return (ULong) get(17);
    }

    /**
     * Setter for <code>oagi.asccp.is_nillable</code>. This is corresponding to the XML schema nillable flag. Although the nillable may not apply in certain cases of the ASCCP (e.g., when it corresponds to an XSD group), the value is default to false for simplification.
     */
    public void setIsNillable(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.asccp.is_nillable</code>. This is corresponding to the XML schema nillable flag. Although the nillable may not apply in certain cases of the ASCCP (e.g., when it corresponds to an XSD group), the value is default to false for simplification.
     */
    public Byte getIsNillable() {
        return (Byte) get(18);
    }

    /**
     * Setter for <code>oagi.asccp.prev_asccp_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevAsccpId(ULong value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.asccp.prev_asccp_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevAsccpId() {
        return (ULong) get(19);
    }

    /**
     * Setter for <code>oagi.asccp.next_asccp_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextAsccpId(ULong value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.asccp.next_asccp_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextAsccpId() {
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
    public Row21<ULong, String, String, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, Byte, Byte, ULong, Byte, ULong, ULong> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    @Override
    public Row21<ULong, String, String, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, Byte, Byte, ULong, Byte, ULong, ULong> valuesRow() {
        return (Row21) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Asccp.ASCCP.ASCCP_ID;
    }

    @Override
    public Field<String> field2() {
        return Asccp.ASCCP.GUID;
    }

    @Override
    public Field<String> field3() {
        return Asccp.ASCCP.TYPE;
    }

    @Override
    public Field<String> field4() {
        return Asccp.ASCCP.PROPERTY_TERM;
    }

    @Override
    public Field<String> field5() {
        return Asccp.ASCCP.DEFINITION;
    }

    @Override
    public Field<String> field6() {
        return Asccp.ASCCP.DEFINITION_SOURCE;
    }

    @Override
    public Field<ULong> field7() {
        return Asccp.ASCCP.ROLE_OF_ACC_ID;
    }

    @Override
    public Field<String> field8() {
        return Asccp.ASCCP.DEN;
    }

    @Override
    public Field<ULong> field9() {
        return Asccp.ASCCP.CREATED_BY;
    }

    @Override
    public Field<ULong> field10() {
        return Asccp.ASCCP.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field11() {
        return Asccp.ASCCP.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return Asccp.ASCCP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field13() {
        return Asccp.ASCCP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field14() {
        return Asccp.ASCCP.STATE;
    }

    @Override
    public Field<ULong> field15() {
        return Asccp.ASCCP.NAMESPACE_ID;
    }

    @Override
    public Field<Byte> field16() {
        return Asccp.ASCCP.REUSABLE_INDICATOR;
    }

    @Override
    public Field<Byte> field17() {
        return Asccp.ASCCP.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field18() {
        return Asccp.ASCCP.REPLACEMENT_ASCCP_ID;
    }

    @Override
    public Field<Byte> field19() {
        return Asccp.ASCCP.IS_NILLABLE;
    }

    @Override
    public Field<ULong> field20() {
        return Asccp.ASCCP.PREV_ASCCP_ID;
    }

    @Override
    public Field<ULong> field21() {
        return Asccp.ASCCP.NEXT_ASCCP_ID;
    }

    @Override
    public ULong component1() {
        return getAsccpId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getType();
    }

    @Override
    public String component4() {
        return getPropertyTerm();
    }

    @Override
    public String component5() {
        return getDefinition();
    }

    @Override
    public String component6() {
        return getDefinitionSource();
    }

    @Override
    public ULong component7() {
        return getRoleOfAccId();
    }

    @Override
    public String component8() {
        return getDen();
    }

    @Override
    public ULong component9() {
        return getCreatedBy();
    }

    @Override
    public ULong component10() {
        return getOwnerUserId();
    }

    @Override
    public ULong component11() {
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
    public String component14() {
        return getState();
    }

    @Override
    public ULong component15() {
        return getNamespaceId();
    }

    @Override
    public Byte component16() {
        return getReusableIndicator();
    }

    @Override
    public Byte component17() {
        return getIsDeprecated();
    }

    @Override
    public ULong component18() {
        return getReplacementAsccpId();
    }

    @Override
    public Byte component19() {
        return getIsNillable();
    }

    @Override
    public ULong component20() {
        return getPrevAsccpId();
    }

    @Override
    public ULong component21() {
        return getNextAsccpId();
    }

    @Override
    public ULong value1() {
        return getAsccpId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getType();
    }

    @Override
    public String value4() {
        return getPropertyTerm();
    }

    @Override
    public String value5() {
        return getDefinition();
    }

    @Override
    public String value6() {
        return getDefinitionSource();
    }

    @Override
    public ULong value7() {
        return getRoleOfAccId();
    }

    @Override
    public String value8() {
        return getDen();
    }

    @Override
    public ULong value9() {
        return getCreatedBy();
    }

    @Override
    public ULong value10() {
        return getOwnerUserId();
    }

    @Override
    public ULong value11() {
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
    public String value14() {
        return getState();
    }

    @Override
    public ULong value15() {
        return getNamespaceId();
    }

    @Override
    public Byte value16() {
        return getReusableIndicator();
    }

    @Override
    public Byte value17() {
        return getIsDeprecated();
    }

    @Override
    public ULong value18() {
        return getReplacementAsccpId();
    }

    @Override
    public Byte value19() {
        return getIsNillable();
    }

    @Override
    public ULong value20() {
        return getPrevAsccpId();
    }

    @Override
    public ULong value21() {
        return getNextAsccpId();
    }

    @Override
    public AsccpRecord value1(ULong value) {
        setAsccpId(value);
        return this;
    }

    @Override
    public AsccpRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AsccpRecord value3(String value) {
        setType(value);
        return this;
    }

    @Override
    public AsccpRecord value4(String value) {
        setPropertyTerm(value);
        return this;
    }

    @Override
    public AsccpRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AsccpRecord value6(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public AsccpRecord value7(ULong value) {
        setRoleOfAccId(value);
        return this;
    }

    @Override
    public AsccpRecord value8(String value) {
        setDen(value);
        return this;
    }

    @Override
    public AsccpRecord value9(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsccpRecord value10(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public AsccpRecord value11(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsccpRecord value12(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsccpRecord value13(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsccpRecord value14(String value) {
        setState(value);
        return this;
    }

    @Override
    public AsccpRecord value15(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public AsccpRecord value16(Byte value) {
        setReusableIndicator(value);
        return this;
    }

    @Override
    public AsccpRecord value17(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public AsccpRecord value18(ULong value) {
        setReplacementAsccpId(value);
        return this;
    }

    @Override
    public AsccpRecord value19(Byte value) {
        setIsNillable(value);
        return this;
    }

    @Override
    public AsccpRecord value20(ULong value) {
        setPrevAsccpId(value);
        return this;
    }

    @Override
    public AsccpRecord value21(ULong value) {
        setNextAsccpId(value);
        return this;
    }

    @Override
    public AsccpRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, ULong value7, String value8, ULong value9, ULong value10, ULong value11, LocalDateTime value12, LocalDateTime value13, String value14, ULong value15, Byte value16, Byte value17, ULong value18, Byte value19, ULong value20, ULong value21) {
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
     * Create a detached AsccpRecord
     */
    public AsccpRecord() {
        super(Asccp.ASCCP);
    }

    /**
     * Create a detached, initialised AsccpRecord
     */
    public AsccpRecord(ULong asccpId, String guid, String type, String propertyTerm, String definition, String definitionSource, ULong roleOfAccId, String den, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, ULong namespaceId, Byte reusableIndicator, Byte isDeprecated, ULong replacementAsccpId, Byte isNillable, ULong prevAsccpId, ULong nextAsccpId) {
        super(Asccp.ASCCP);

        set(0, asccpId);
        set(1, guid);
        set(2, type);
        set(3, propertyTerm);
        set(4, definition);
        set(5, definitionSource);
        set(6, roleOfAccId);
        set(7, den);
        set(8, createdBy);
        set(9, ownerUserId);
        set(10, lastUpdatedBy);
        set(11, creationTimestamp);
        set(12, lastUpdateTimestamp);
        set(13, state);
        set(14, namespaceId);
        set(15, reusableIndicator);
        set(16, isDeprecated);
        set(17, replacementAsccpId);
        set(18, isNillable);
        set(19, prevAsccpId);
        set(20, nextAsccpId);
    }
}
