/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record21;
import org.jooq.Row21;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Bccp;

import java.time.LocalDateTime;


/**
 * An BCCP specifies a property concept and data type associated with it. 
 * A BCCP can be then added as a property of an ACC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccpRecord extends UpdatableRecordImpl<BccpRecord> implements Record21<ULong, String, String, String, ULong, String, String, String, ULong, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, String, String, ULong, ULong> {

    private static final long serialVersionUID = -1269807039;

    /**
     * Setter for <code>oagi.bccp.bccp_id</code>. An internal, primary database key.
     */
    public void setBccpId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bccp.bccp_id</code>. An internal, primary database key.
     */
    public ULong getBccpId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bccp.guid</code>. A globally unique identifier (GUID). Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.',
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bccp.guid</code>. A globally unique identifier (GUID). Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.',
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bccp.property_term</code>. The property concept that the BCCP models.
     */
    public void setPropertyTerm(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bccp.property_term</code>. The property concept that the BCCP models.
     */
    public String getPropertyTerm() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.bccp.representation_term</code>. The representation term convey the format of the data the BCCP can take. The value is derived from the DT.DATA_TYPE_TERM of the associated BDT as referred to by the BDT_ID column.
     */
    public void setRepresentationTerm(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bccp.representation_term</code>. The representation term convey the format of the data the BCCP can take. The value is derived from the DT.DATA_TYPE_TERM of the associated BDT as referred to by the BDT_ID column.
     */
    public String getRepresentationTerm() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bccp.bdt_id</code>. Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.
     */
    public void setBdtId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bccp.bdt_id</code>. Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.
     */
    public ULong getBdtId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bccp.den</code>. The dictionary entry name of the BCCP. It is derived by PROPERTY_TERM + ". " + REPRESENTATION_TERM.
     */
    public void setDen(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bccp.den</code>. The dictionary entry name of the BCCP. It is derived by PROPERTY_TERM + ". " + REPRESENTATION_TERM.
     */
    public String getDen() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.bccp.definition</code>. Description of the BCCP.
     */
    public void setDefinition(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bccp.definition</code>. Description of the BCCP.
     */
    public String getDefinition() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.bccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.bccp.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public void setNamespaceId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bccp.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public ULong getNamespaceId() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.bccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>oagi.bccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public void setCreatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.bccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.bccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.bccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.bccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.bccp.creation_timestamp</code>. Timestamp when the revision of the BCCP was created. 

This never change for a revision.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.bccp.creation_timestamp</code>. Timestamp when the revision of the BCCP was created. 

This never change for a revision.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>oagi.bccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.bccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>oagi.bccp.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCCP.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public void setState(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.bccp.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCCP.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public String getState() {
        return (String) get(15);
    }

    /**
     * Setter for <code>oagi.bccp.is_nillable</code>. This is corresponding to the XML Schema nillable flag. Although the nillable may not apply to certain cases of the BCCP (e.g., when it is only used as XSD attribute), the value is default to false for simplification. 
     */
    public void setIsNillable(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.bccp.is_nillable</code>. This is corresponding to the XML Schema nillable flag. Although the nillable may not apply to certain cases of the BCCP (e.g., when it is only used as XSD attribute), the value is default to false for simplification. 
     */
    public Byte getIsNillable() {
        return (Byte) get(16);
    }

    /**
     * Setter for <code>oagi.bccp.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public void setDefaultValue(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.bccp.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public String getDefaultValue() {
        return (String) get(17);
    }

    /**
     * Setter for <code>oagi.bccp.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public void setFixedValue(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.bccp.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public String getFixedValue() {
        return (String) get(18);
    }

    /**
     * Setter for <code>oagi.bccp.prev_bccp_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevBccpId(ULong value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.bccp.prev_bccp_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevBccpId() {
        return (ULong) get(19);
    }

    /**
     * Setter for <code>oagi.bccp.next_bccp_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextBccpId(ULong value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.bccp.next_bccp_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextBccpId() {
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
    public Row21<ULong, String, String, String, ULong, String, String, String, ULong, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, String, String, ULong, ULong> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    @Override
    public Row21<ULong, String, String, String, ULong, String, String, String, ULong, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, String, String, ULong, ULong> valuesRow() {
        return (Row21) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Bccp.BCCP.BCCP_ID;
    }

    @Override
    public Field<String> field2() {
        return Bccp.BCCP.GUID;
    }

    @Override
    public Field<String> field3() {
        return Bccp.BCCP.PROPERTY_TERM;
    }

    @Override
    public Field<String> field4() {
        return Bccp.BCCP.REPRESENTATION_TERM;
    }

    @Override
    public Field<ULong> field5() {
        return Bccp.BCCP.BDT_ID;
    }

    @Override
    public Field<String> field6() {
        return Bccp.BCCP.DEN;
    }

    @Override
    public Field<String> field7() {
        return Bccp.BCCP.DEFINITION;
    }

    @Override
    public Field<String> field8() {
        return Bccp.BCCP.DEFINITION_SOURCE;
    }

    @Override
    public Field<ULong> field9() {
        return Bccp.BCCP.NAMESPACE_ID;
    }

    @Override
    public Field<Byte> field10() {
        return Bccp.BCCP.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field11() {
        return Bccp.BCCP.CREATED_BY;
    }

    @Override
    public Field<ULong> field12() {
        return Bccp.BCCP.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field13() {
        return Bccp.BCCP.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field14() {
        return Bccp.BCCP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return Bccp.BCCP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field16() {
        return Bccp.BCCP.STATE;
    }

    @Override
    public Field<Byte> field17() {
        return Bccp.BCCP.IS_NILLABLE;
    }

    @Override
    public Field<String> field18() {
        return Bccp.BCCP.DEFAULT_VALUE;
    }

    @Override
    public Field<String> field19() {
        return Bccp.BCCP.FIXED_VALUE;
    }

    @Override
    public Field<ULong> field20() {
        return Bccp.BCCP.PREV_BCCP_ID;
    }

    @Override
    public Field<ULong> field21() {
        return Bccp.BCCP.NEXT_BCCP_ID;
    }

    @Override
    public ULong component1() {
        return getBccpId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getPropertyTerm();
    }

    @Override
    public String component4() {
        return getRepresentationTerm();
    }

    @Override
    public ULong component5() {
        return getBdtId();
    }

    @Override
    public String component6() {
        return getDen();
    }

    @Override
    public String component7() {
        return getDefinition();
    }

    @Override
    public String component8() {
        return getDefinitionSource();
    }

    @Override
    public ULong component9() {
        return getNamespaceId();
    }

    @Override
    public Byte component10() {
        return getIsDeprecated();
    }

    @Override
    public ULong component11() {
        return getCreatedBy();
    }

    @Override
    public ULong component12() {
        return getOwnerUserId();
    }

    @Override
    public ULong component13() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component14() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component15() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String component16() {
        return getState();
    }

    @Override
    public Byte component17() {
        return getIsNillable();
    }

    @Override
    public String component18() {
        return getDefaultValue();
    }

    @Override
    public String component19() {
        return getFixedValue();
    }

    @Override
    public ULong component20() {
        return getPrevBccpId();
    }

    @Override
    public ULong component21() {
        return getNextBccpId();
    }

    @Override
    public ULong value1() {
        return getBccpId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getPropertyTerm();
    }

    @Override
    public String value4() {
        return getRepresentationTerm();
    }

    @Override
    public ULong value5() {
        return getBdtId();
    }

    @Override
    public String value6() {
        return getDen();
    }

    @Override
    public String value7() {
        return getDefinition();
    }

    @Override
    public String value8() {
        return getDefinitionSource();
    }

    @Override
    public ULong value9() {
        return getNamespaceId();
    }

    @Override
    public Byte value10() {
        return getIsDeprecated();
    }

    @Override
    public ULong value11() {
        return getCreatedBy();
    }

    @Override
    public ULong value12() {
        return getOwnerUserId();
    }

    @Override
    public ULong value13() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value14() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value15() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value16() {
        return getState();
    }

    @Override
    public Byte value17() {
        return getIsNillable();
    }

    @Override
    public String value18() {
        return getDefaultValue();
    }

    @Override
    public String value19() {
        return getFixedValue();
    }

    @Override
    public ULong value20() {
        return getPrevBccpId();
    }

    @Override
    public ULong value21() {
        return getNextBccpId();
    }

    @Override
    public BccpRecord value1(ULong value) {
        setBccpId(value);
        return this;
    }

    @Override
    public BccpRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public BccpRecord value3(String value) {
        setPropertyTerm(value);
        return this;
    }

    @Override
    public BccpRecord value4(String value) {
        setRepresentationTerm(value);
        return this;
    }

    @Override
    public BccpRecord value5(ULong value) {
        setBdtId(value);
        return this;
    }

    @Override
    public BccpRecord value6(String value) {
        setDen(value);
        return this;
    }

    @Override
    public BccpRecord value7(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public BccpRecord value8(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public BccpRecord value9(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public BccpRecord value10(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public BccpRecord value11(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public BccpRecord value12(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public BccpRecord value13(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public BccpRecord value14(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public BccpRecord value15(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public BccpRecord value16(String value) {
        setState(value);
        return this;
    }

    @Override
    public BccpRecord value17(Byte value) {
        setIsNillable(value);
        return this;
    }

    @Override
    public BccpRecord value18(String value) {
        setDefaultValue(value);
        return this;
    }

    @Override
    public BccpRecord value19(String value) {
        setFixedValue(value);
        return this;
    }

    @Override
    public BccpRecord value20(ULong value) {
        setPrevBccpId(value);
        return this;
    }

    @Override
    public BccpRecord value21(ULong value) {
        setNextBccpId(value);
        return this;
    }

    @Override
    public BccpRecord values(ULong value1, String value2, String value3, String value4, ULong value5, String value6, String value7, String value8, ULong value9, Byte value10, ULong value11, ULong value12, ULong value13, LocalDateTime value14, LocalDateTime value15, String value16, Byte value17, String value18, String value19, ULong value20, ULong value21) {
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
     * Create a detached BccpRecord
     */
    public BccpRecord() {
        super(Bccp.BCCP);
    }

    /**
     * Create a detached, initialised BccpRecord
     */
    public BccpRecord(ULong bccpId, String guid, String propertyTerm, String representationTerm, ULong bdtId, String den, String definition, String definitionSource, ULong namespaceId, Byte isDeprecated, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, Byte isNillable, String defaultValue, String fixedValue, ULong prevBccpId, ULong nextBccpId) {
        super(Bccp.BCCP);

        set(0, bccpId);
        set(1, guid);
        set(2, propertyTerm);
        set(3, representationTerm);
        set(4, bdtId);
        set(5, den);
        set(6, definition);
        set(7, definitionSource);
        set(8, namespaceId);
        set(9, isDeprecated);
        set(10, createdBy);
        set(11, ownerUserId);
        set(12, lastUpdatedBy);
        set(13, creationTimestamp);
        set(14, lastUpdateTimestamp);
        set(15, state);
        set(16, isNillable);
        set(17, defaultValue);
        set(18, fixedValue);
        set(19, prevBccpId);
        set(20, nextBccpId);
    }
}
