/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Asccp;


/**
 * An ASCCP specifies a role (or property) an ACC may play under another ACC.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccpRecord extends UpdatableRecordImpl<AsccpRecord> implements Record20<ULong, String, String, String, String, ULong, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer, ULong, Byte, Byte, Integer, Integer, Byte, Byte> {

    private static final long serialVersionUID = -1283415920;

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
     * Setter for <code>oagi.asccp.property_term</code>. The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. \n\nThere must be only one ASCCP without a Property_Term for a particular ACC.
     */
    public void setPropertyTerm(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.asccp.property_term</code>. The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. \n\nThere must be only one ASCCP without a Property_Term for a particular ACC.
     */
    public String getPropertyTerm() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.asccp.definition</code>. Description of the ASCCP.
     */
    public void setDefinition(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.asccp.definition</code>. Description of the ASCCP.
     */
    public String getDefinition() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.asccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.asccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.asccp.role_of_acc_id</code>. The ACC from which this ASCCP is created (ASCCP applies role to the ACC).
     */
    public void setRoleOfAccId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.asccp.role_of_acc_id</code>. The ACC from which this ASCCP is created (ASCCP applies role to the ACC).
     */
    public ULong getRoleOfAccId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.asccp.den</code>. The dictionary entry name of the ASCCP.
     */
    public void setDen(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.asccp.den</code>. The dictionary entry name of the ASCCP.
     */
    public String getDen() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.asccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public void setCreatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.asccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.asccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public void setOwnerUserId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.asccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public ULong getOwnerUserId() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.asccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.asccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.asccp.creation_timestamp</code>. Timestamp when the revision of the ASCCP was created. 

This never change for a revision.
     */
    public void setCreationTimestamp(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.asccp.creation_timestamp</code>. Timestamp when the revision of the ASCCP was created. 

This never change for a revision.
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>oagi.asccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.asccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(11);
    }

    /**
     * Setter for <code>oagi.asccp.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This the revision life cycle state of the ACC.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public void setState(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.asccp.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This the revision life cycle state of the ACC.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public Integer getState() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>oagi.asccp.namespace_id</code>. Foreign key to the Namespace table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public void setNamespaceId(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.asccp.namespace_id</code>. Foreign key to the Namespace table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public ULong getNamespaceId() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.asccp.reusable_indicator</code>. This indicates whether the ASCCP can be used by more than one ASCC. This maps directly to the XML schema local element declaration.
     */
    public void setReusableIndicator(Byte value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.asccp.reusable_indicator</code>. This indicates whether the ASCCP can be used by more than one ASCC. This maps directly to the XML schema local element declaration.
     */
    public Byte getReusableIndicator() {
        return (Byte) get(14);
    }

    /**
     * Setter for <code>oagi.asccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.asccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>oagi.asccp.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public void setRevisionNum(Integer value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.asccp.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public Integer getRevisionNum() {
        return (Integer) get(16);
    }

    /**
     * Setter for <code>oagi.asccp.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUMB can be 0, 1, 2, and so on. The zero value is assigned to the record with REVISION_NUM = 0 as a default.
     */
    public void setRevisionTrackingNum(Integer value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.asccp.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUMB can be 0, 1, 2, and so on. The zero value is assigned to the record with REVISION_NUM = 0 as a default.
     */
    public Integer getRevisionTrackingNum() {
        return (Integer) get(17);
    }

    /**
     * Setter for <code>oagi.asccp.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public void setRevisionAction(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.asccp.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public Byte getRevisionAction() {
        return (Byte) get(18);
    }

    /**
     * Setter for <code>oagi.asccp.is_nillable</code>. This is corresponding to the XML schema nillable flag. Although the nillable may not apply in certain cases of the ASCCP (e.g., when it corresponds to an XSD group), the value is default to false for simplification.
     */
    public void setIsNillable(Byte value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.asccp.is_nillable</code>. This is corresponding to the XML schema nillable flag. Although the nillable may not apply in certain cases of the ASCCP (e.g., when it corresponds to an XSD group), the value is default to false for simplification.
     */
    public Byte getIsNillable() {
        return (Byte) get(19);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record20 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row20<ULong, String, String, String, String, ULong, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer, ULong, Byte, Byte, Integer, Integer, Byte, Byte> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    @Override
    public Row20<ULong, String, String, String, String, ULong, String, ULong, ULong, ULong, Timestamp, Timestamp, Integer, ULong, Byte, Byte, Integer, Integer, Byte, Byte> valuesRow() {
        return (Row20) super.valuesRow();
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
        return Asccp.ASCCP.PROPERTY_TERM;
    }

    @Override
    public Field<String> field4() {
        return Asccp.ASCCP.DEFINITION;
    }

    @Override
    public Field<String> field5() {
        return Asccp.ASCCP.DEFINITION_SOURCE;
    }

    @Override
    public Field<ULong> field6() {
        return Asccp.ASCCP.ROLE_OF_ACC_ID;
    }

    @Override
    public Field<String> field7() {
        return Asccp.ASCCP.DEN;
    }

    @Override
    public Field<ULong> field8() {
        return Asccp.ASCCP.CREATED_BY;
    }

    @Override
    public Field<ULong> field9() {
        return Asccp.ASCCP.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field10() {
        return Asccp.ASCCP.LAST_UPDATED_BY;
    }

    @Override
    public Field<Timestamp> field11() {
        return Asccp.ASCCP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<Timestamp> field12() {
        return Asccp.ASCCP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<Integer> field13() {
        return Asccp.ASCCP.STATE;
    }

    @Override
    public Field<ULong> field14() {
        return Asccp.ASCCP.NAMESPACE_ID;
    }

    @Override
    public Field<Byte> field15() {
        return Asccp.ASCCP.REUSABLE_INDICATOR;
    }

    @Override
    public Field<Byte> field16() {
        return Asccp.ASCCP.IS_DEPRECATED;
    }

    @Override
    public Field<Integer> field17() {
        return Asccp.ASCCP.REVISION_NUM;
    }

    @Override
    public Field<Integer> field18() {
        return Asccp.ASCCP.REVISION_TRACKING_NUM;
    }

    @Override
    public Field<Byte> field19() {
        return Asccp.ASCCP.REVISION_ACTION;
    }

    @Override
    public Field<Byte> field20() {
        return Asccp.ASCCP.IS_NILLABLE;
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
        return getPropertyTerm();
    }

    @Override
    public String component4() {
        return getDefinition();
    }

    @Override
    public String component5() {
        return getDefinitionSource();
    }

    @Override
    public ULong component6() {
        return getRoleOfAccId();
    }

    @Override
    public String component7() {
        return getDen();
    }

    @Override
    public ULong component8() {
        return getCreatedBy();
    }

    @Override
    public ULong component9() {
        return getOwnerUserId();
    }

    @Override
    public ULong component10() {
        return getLastUpdatedBy();
    }

    @Override
    public Timestamp component11() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp component12() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Integer component13() {
        return getState();
    }

    @Override
    public ULong component14() {
        return getNamespaceId();
    }

    @Override
    public Byte component15() {
        return getReusableIndicator();
    }

    @Override
    public Byte component16() {
        return getIsDeprecated();
    }

    @Override
    public Integer component17() {
        return getRevisionNum();
    }

    @Override
    public Integer component18() {
        return getRevisionTrackingNum();
    }

    @Override
    public Byte component19() {
        return getRevisionAction();
    }

    @Override
    public Byte component20() {
        return getIsNillable();
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
        return getPropertyTerm();
    }

    @Override
    public String value4() {
        return getDefinition();
    }

    @Override
    public String value5() {
        return getDefinitionSource();
    }

    @Override
    public ULong value6() {
        return getRoleOfAccId();
    }

    @Override
    public String value7() {
        return getDen();
    }

    @Override
    public ULong value8() {
        return getCreatedBy();
    }

    @Override
    public ULong value9() {
        return getOwnerUserId();
    }

    @Override
    public ULong value10() {
        return getLastUpdatedBy();
    }

    @Override
    public Timestamp value11() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp value12() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Integer value13() {
        return getState();
    }

    @Override
    public ULong value14() {
        return getNamespaceId();
    }

    @Override
    public Byte value15() {
        return getReusableIndicator();
    }

    @Override
    public Byte value16() {
        return getIsDeprecated();
    }

    @Override
    public Integer value17() {
        return getRevisionNum();
    }

    @Override
    public Integer value18() {
        return getRevisionTrackingNum();
    }

    @Override
    public Byte value19() {
        return getRevisionAction();
    }

    @Override
    public Byte value20() {
        return getIsNillable();
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
        setPropertyTerm(value);
        return this;
    }

    @Override
    public AsccpRecord value4(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AsccpRecord value5(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public AsccpRecord value6(ULong value) {
        setRoleOfAccId(value);
        return this;
    }

    @Override
    public AsccpRecord value7(String value) {
        setDen(value);
        return this;
    }

    @Override
    public AsccpRecord value8(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsccpRecord value9(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public AsccpRecord value10(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsccpRecord value11(Timestamp value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsccpRecord value12(Timestamp value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsccpRecord value13(Integer value) {
        setState(value);
        return this;
    }

    @Override
    public AsccpRecord value14(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public AsccpRecord value15(Byte value) {
        setReusableIndicator(value);
        return this;
    }

    @Override
    public AsccpRecord value16(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public AsccpRecord value17(Integer value) {
        setRevisionNum(value);
        return this;
    }

    @Override
    public AsccpRecord value18(Integer value) {
        setRevisionTrackingNum(value);
        return this;
    }

    @Override
    public AsccpRecord value19(Byte value) {
        setRevisionAction(value);
        return this;
    }

    @Override
    public AsccpRecord value20(Byte value) {
        setIsNillable(value);
        return this;
    }

    @Override
    public AsccpRecord values(ULong value1, String value2, String value3, String value4, String value5, ULong value6, String value7, ULong value8, ULong value9, ULong value10, Timestamp value11, Timestamp value12, Integer value13, ULong value14, Byte value15, Byte value16, Integer value17, Integer value18, Byte value19, Byte value20) {
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
     * Create a detached AsccpRecord
     */
    public AsccpRecord() {
        super(Asccp.ASCCP);
    }

    /**
     * Create a detached, initialised AsccpRecord
     */
    public AsccpRecord(ULong asccpId, String guid, String propertyTerm, String definition, String definitionSource, ULong roleOfAccId, String den, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp, Integer state, ULong namespaceId, Byte reusableIndicator, Byte isDeprecated, Integer revisionNum, Integer revisionTrackingNum, Byte revisionAction, Byte isNillable) {
        super(Asccp.ASCCP);

        set(0, asccpId);
        set(1, guid);
        set(2, propertyTerm);
        set(3, definition);
        set(4, definitionSource);
        set(5, roleOfAccId);
        set(6, den);
        set(7, createdBy);
        set(8, ownerUserId);
        set(9, lastUpdatedBy);
        set(10, creationTimestamp);
        set(11, lastUpdateTimestamp);
        set(12, state);
        set(13, namespaceId);
        set(14, reusableIndicator);
        set(15, isDeprecated);
        set(16, revisionNum);
        set(17, revisionTrackingNum);
        set(18, revisionAction);
        set(19, isNillable);
    }
}
