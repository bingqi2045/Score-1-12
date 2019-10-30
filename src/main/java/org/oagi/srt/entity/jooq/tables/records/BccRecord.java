/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Bcc;


/**
 * A BCC represents a relationship/association between an ACC and a BCCP. 
 * It creates a data element for an ACC. 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccRecord extends UpdatableRecordImpl<BccRecord> {

    private static final long serialVersionUID = 995785473;

    /**
     * Setter for <code>oagi.bcc.bcc_id</code>. A internal, primary database key of an BCC.
     */
    public void setBccId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bcc.bcc_id</code>. A internal, primary database key of an BCC.
     */
    public ULong getBccId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bcc.guid</code>. A globally unique identifier (GUID) of BCC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.',
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bcc.guid</code>. A globally unique identifier (GUID) of BCC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.',
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bcc.cardinality_min</code>. Minimum cardinality of the TO_BCCP_ID. The valid values are non-negative integer.
     */
    public void setCardinalityMin(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bcc.cardinality_min</code>. Minimum cardinality of the TO_BCCP_ID. The valid values are non-negative integer.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>oagi.bcc.cardinality_max</code>. Maximum cardinality of the TO_BCCP_ID. The valid values are integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.',
     */
    public void setCardinalityMax(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bcc.cardinality_max</code>. Maximum cardinality of the TO_BCCP_ID. The valid values are integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.',
     */
    public Integer getCardinalityMax() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>oagi.bcc.to_bccp_id</code>. TO_BCCP_ID is a foreign key to an BCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. 

Note that for the BCC history records, this column always points to the BCCP_ID of the current record of a BCCP.',
     */
    public void setToBccpId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bcc.to_bccp_id</code>. TO_BCCP_ID is a foreign key to an BCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. 

Note that for the BCC history records, this column always points to the BCCP_ID of the current record of a BCCP.',
     */
    public ULong getToBccpId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bcc.from_acc_id</code>. FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_BCCP_ID. 

Note that for the BCC history records, this column always points to the ACC_ID of the current record of an ACC.
     */
    public void setFromAccId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bcc.from_acc_id</code>. FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_BCCP_ID. 

Note that for the BCC history records, this column always points to the ACC_ID of the current record of an ACC.
     */
    public ULong getFromAccId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.bcc.seq_key</code>. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again. 
     */
    public void setSeqKey(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bcc.seq_key</code>. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again. 
     */
    public Integer getSeqKey() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>oagi.bcc.entity_type</code>. This is a code list: 0 = ATTRIBUTE and 1 = ELEMENT. An expression generator may or may not use this information. This column is necessary because some of the BCCs are xsd:attribute and some are xsd:element in the OAGIS 10.x. 
     */
    public void setEntityType(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bcc.entity_type</code>. This is a code list: 0 = ATTRIBUTE and 1 = ELEMENT. An expression generator may or may not use this information. This column is necessary because some of the BCCs are xsd:attribute and some are xsd:element in the OAGIS 10.x. 
     */
    public Integer getEntityType() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>oagi.bcc.den</code>. DEN (dictionary entry name) of the BCC. This column can be derived from QUALIFIER and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_BCCP_ID as QUALIFIER + "_ " + OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public void setDen(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bcc.den</code>. DEN (dictionary entry name) of the BCC. This column can be derived from QUALIFIER and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_BCCP_ID as QUALIFIER + "_ " + OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public String getDen() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.bcc.definition</code>. This is a documentation or description of the BCC. Since BCC is business context independent, this is a business context independent description of the BCC. Since there are definitions also in the BCCP (as referenced by TO_BCCP_ID column) and the BDT under that BCCP, the definition in the BCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the BCCP.
     */
    public void setDefinition(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bcc.definition</code>. This is a documentation or description of the BCC. Since BCC is business context independent, this is a business context independent description of the BCC. Since there are definitions also in the BCCP (as referenced by TO_BCCP_ID column) and the BDT under that BCCP, the definition in the BCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the BCCP.
     */
    public String getDefinition() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.bcc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bcc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.bcc.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity.

This column never change between the history and the current record. The history record should have the same value as that of its current record.
     */
    public void setCreatedBy(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.bcc.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity.

This column never change between the history and the current record. The history record should have the same value as that of its current record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.bcc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.bcc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.bcc.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.bcc.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.bcc.creation_timestamp</code>. Timestamp when the revision of the BCC was created. 

This never change for a revision.
     */
    public void setCreationTimestamp(Timestamp value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.bcc.creation_timestamp</code>. Timestamp when the revision of the BCC was created. 

This never change for a revision.
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(14);
    }

    /**
     * Setter for <code>oagi.bcc.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.bcc.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(15);
    }

    /**
     * Setter for <code>oagi.bcc.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This is the revision life cycle state of the entity.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public void setState(Integer value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.bcc.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This is the revision life cycle state of the entity.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public Integer getState() {
        return (Integer) get(16);
    }

    /**
     * Setter for <code>oagi.bcc.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public void setRevisionNum(Integer value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.bcc.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public Integer getRevisionNum() {
        return (Integer) get(17);
    }

    /**
     * Setter for <code>oagi.bcc.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUM can be 0, 1, 2, and so on. The zero value is assign to the record with REVISION_NUM = 0 as a default.
     */
    public void setRevisionTrackingNum(Integer value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.bcc.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUM can be 0, 1, 2, and so on. The zero value is assign to the record with REVISION_NUM = 0 as a default.
     */
    public Integer getRevisionTrackingNum() {
        return (Integer) get(18);
    }

    /**
     * Setter for <code>oagi.bcc.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public void setRevisionAction(Byte value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.bcc.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public Byte getRevisionAction() {
        return (Byte) get(19);
    }

    /**
     * Setter for <code>oagi.bcc.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.bcc.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(20);
    }

    /**
     * Setter for <code>oagi.bcc.is_nillable</code>. Indicate whether the field can have a NULL This is corresponding to the nillable flag in the XML schema.
     */
    public void setIsNillable(Byte value) {
        set(21, value);
    }

    /**
     * Getter for <code>oagi.bcc.is_nillable</code>. Indicate whether the field can have a NULL This is corresponding to the nillable flag in the XML schema.
     */
    public Byte getIsNillable() {
        return (Byte) get(21);
    }

    /**
     * Setter for <code>oagi.bcc.default_value</code>. This set the default value at the association level. 
     */
    public void setDefaultValue(String value) {
        set(22, value);
    }

    /**
     * Getter for <code>oagi.bcc.default_value</code>. This set the default value at the association level. 
     */
    public String getDefaultValue() {
        return (String) get(22);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BccRecord
     */
    public BccRecord() {
        super(Bcc.BCC);
    }

    /**
     * Create a detached, initialised BccRecord
     */
    public BccRecord(ULong bccId, String guid, Integer cardinalityMin, Integer cardinalityMax, ULong toBccpId, ULong fromAccId, Integer seqKey, Integer entityType, String den, String definition, String definitionSource, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp, Integer state, Integer revisionNum, Integer revisionTrackingNum, Byte revisionAction, Byte isDeprecated, Byte isNillable, String defaultValue) {
        super(Bcc.BCC);

        set(0, bccId);
        set(1, guid);
        set(2, cardinalityMin);
        set(3, cardinalityMax);
        set(4, toBccpId);
        set(5, fromAccId);
        set(6, seqKey);
        set(7, entityType);
        set(8, den);
        set(9, definition);
        set(10, definitionSource);
        set(11, createdBy);
        set(12, ownerUserId);
        set(13, lastUpdatedBy);
        set(14, creationTimestamp);
        set(15, lastUpdateTimestamp);
        set(16, state);
        set(17, revisionNum);
        set(18, revisionTrackingNum);
        set(19, revisionAction);
        set(20, isDeprecated);
        set(21, isNillable);
        set(22, defaultValue);
    }
}
