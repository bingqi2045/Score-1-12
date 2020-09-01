/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Dt;


/**
 * The DT table stores both CDT and BDT. The two types of DTs are differentiated 
 * by the TYPE column.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtRecord extends UpdatableRecordImpl<DtRecord> {

    private static final long serialVersionUID = 1079512204;

    /**
     * Setter for <code>oagi.dt.dt_id</code>. Internal, primary database key.
     */
    public void setDtId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.dt.dt_id</code>. Internal, primary database key.
     */
    public ULong getDtId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.dt.guid</code>. GUID of the data type. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.dt.guid</code>. GUID of the data type. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.dt.type</code>. List value: 0 = CDT, 1 = BDT.
     */
    public void setType(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.dt.type</code>. List value: 0 = CDT, 1 = BDT.
     */
    public Integer getType() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>oagi.dt.version_num</code>. Format X.Y.Z where all of them are integer with no leading zero allowed. X means major version number, Y means minor version number and Z means patch version number. This column is different from the REVISION_NUM column in that the new version is only assigned to the release component while the REVISION_NUM is assigned every time editing life cycle.
     */
    public void setVersionNum(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.dt.version_num</code>. Format X.Y.Z where all of them are integer with no leading zero allowed. X means major version number, Y means minor version number and Z means patch version number. This column is different from the REVISION_NUM column in that the new version is only assigned to the release component while the REVISION_NUM is assigned every time editing life cycle.
     */
    public String getVersionNum() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.dt.previous_version_dt_id</code>. Foregin key to the DT table itself. It identifies the previous version.
     */
    public void setPreviousVersionDtId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.dt.previous_version_dt_id</code>. Foregin key to the DT table itself. It identifies the previous version.
     */
    public ULong getPreviousVersionDtId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.dt.data_type_term</code>. This is the data type term assigned to the DT. The allowed set of data type terms are defined in the DTC specification. This column is derived from the Based_DT_ID when the column is not blank. 
     */
    public void setDataTypeTerm(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.dt.data_type_term</code>. This is the data type term assigned to the DT. The allowed set of data type terms are defined in the DTC specification. This column is derived from the Based_DT_ID when the column is not blank. 
     */
    public String getDataTypeTerm() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.dt.qualifier</code>. This column shall be blank when the DT_TYPE is CDT. When the DT_TYPE is BDT, this is optional. If the column is not blank it is a qualified BDT. If blank then the row may be a default BDT or an unqualified BDT. Default BDT is OAGIS concrete implementation of the CDT, these are the DT with numbers in the name, e.g., CodeType_1E7368 (DEN is 'Code_1E7368. Type'). Default BDTs are almost like permutation of the CDT options into concrete data types. Unqualified BDT is a BDT that OAGIS model schema generally used for its canonical. A handful of default BDTs were selected; and each of them is wrapped with another type definition that has a simpler name such as CodeType and NormalizedString type - we call these "unqualified BDTs". 
     */
    public void setQualifier(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.dt.qualifier</code>. This column shall be blank when the DT_TYPE is CDT. When the DT_TYPE is BDT, this is optional. If the column is not blank it is a qualified BDT. If blank then the row may be a default BDT or an unqualified BDT. Default BDT is OAGIS concrete implementation of the CDT, these are the DT with numbers in the name, e.g., CodeType_1E7368 (DEN is 'Code_1E7368. Type'). Default BDTs are almost like permutation of the CDT options into concrete data types. Unqualified BDT is a BDT that OAGIS model schema generally used for its canonical. A handful of default BDTs were selected; and each of them is wrapped with another type definition that has a simpler name such as CodeType and NormalizedString type - we call these "unqualified BDTs". 
     */
    public String getQualifier() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.dt.based_dt_id</code>. Foreign key pointing to the DT table itself. This column must be blank when the DT_TYPE is CDT. This column must not be blank when the DT_TYPE is BDT.
     */
    public void setBasedDtId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.dt.based_dt_id</code>. Foreign key pointing to the DT table itself. This column must be blank when the DT_TYPE is CDT. This column must not be blank when the DT_TYPE is BDT.
     */
    public ULong getBasedDtId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.dt.den</code>. Dictionary Entry Name of the data type. 
     */
    public void setDen(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.dt.den</code>. Dictionary Entry Name of the data type. 
     */
    public String getDen() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.dt.content_component_den</code>. When the DT_TYPE is CDT this column is automatically derived from DATA_TYPE_TERM as "&lt;DATA_TYPE_TYPE&gt;. Content", where 'Content' is called property term of the content component according to CCTS. When the DT_TYPE is BDT this column has the same value as its BASED_DT_ID.
     */
    public void setContentComponentDen(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.dt.content_component_den</code>. When the DT_TYPE is CDT this column is automatically derived from DATA_TYPE_TERM as "&lt;DATA_TYPE_TYPE&gt;. Content", where 'Content' is called property term of the content component according to CCTS. When the DT_TYPE is BDT this column has the same value as its BASED_DT_ID.
     */
    public String getContentComponentDen() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.dt.definition</code>. Description of the data type.
     */
    public void setDefinition(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.dt.definition</code>. Description of the data type.
     */
    public String getDefinition() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.dt.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.dt.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(11);
    }

    /**
     * Setter for <code>oagi.dt.content_component_definition</code>. Description of the content component of the data type.
     */
    public void setContentComponentDefinition(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.dt.content_component_definition</code>. Description of the content component of the data type.
     */
    public String getContentComponentDefinition() {
        return (String) get(12);
    }

    /**
     * Setter for <code>oagi.dt.revision_doc</code>. This is for documenting about the revision, e.g., how the newer version of the DT is different from the previous version.
     */
    public void setRevisionDoc(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.dt.revision_doc</code>. This is for documenting about the revision, e.g., how the newer version of the DT is different from the previous version.
     */
    public String getRevisionDoc() {
        return (String) get(13);
    }

    /**
     * Setter for <code>oagi.dt.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the DT.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public void setState(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.dt.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the DT.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public String getState() {
        return (String) get(14);
    }

    /**
     * Setter for <code>oagi.dt.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this DT.
     */
    public void setCreatedBy(ULong value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.dt.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this DT.
     */
    public ULong getCreatedBy() {
        return (ULong) get(15);
    }

    /**
     * Setter for <code>oagi.dt.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.dt.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(16);
    }

    /**
     * Setter for <code>oagi.dt.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public void setOwnerUserId(ULong value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.dt.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public ULong getOwnerUserId() {
        return (ULong) get(17);
    }

    /**
     * Setter for <code>oagi.dt.creation_timestamp</code>. Timestamp when the revision of the DT was created. 

This never change for a revision.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.dt.creation_timestamp</code>. Timestamp when the revision of the DT was created. 

This never change for a revision.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(18);
    }

    /**
     * Setter for <code>oagi.dt.last_update_timestamp</code>. Timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.dt.last_update_timestamp</code>. Timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(19);
    }

    /**
     * Setter for <code>oagi.dt.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.dt.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(20);
    }

    /**
     * Setter for <code>oagi.dt.prev_dt_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevDtId(ULong value) {
        set(21, value);
    }

    /**
     * Getter for <code>oagi.dt.prev_dt_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevDtId() {
        return (ULong) get(21);
    }

    /**
     * Setter for <code>oagi.dt.next_dt_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextDtId(ULong value) {
        set(22, value);
    }

    /**
     * Getter for <code>oagi.dt.next_dt_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextDtId() {
        return (ULong) get(22);
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
     * Create a detached DtRecord
     */
    public DtRecord() {
        super(Dt.DT);
    }

    /**
     * Create a detached, initialised DtRecord
     */
    public DtRecord(ULong dtId, String guid, Integer type, String versionNum, ULong previousVersionDtId, String dataTypeTerm, String qualifier, ULong basedDtId, String den, String contentComponentDen, String definition, String definitionSource, String contentComponentDefinition, String revisionDoc, String state, ULong createdBy, ULong lastUpdatedBy, ULong ownerUserId, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, Byte isDeprecated, ULong prevDtId, ULong nextDtId) {
        super(Dt.DT);

        set(0, dtId);
        set(1, guid);
        set(2, type);
        set(3, versionNum);
        set(4, previousVersionDtId);
        set(5, dataTypeTerm);
        set(6, qualifier);
        set(7, basedDtId);
        set(8, den);
        set(9, contentComponentDen);
        set(10, definition);
        set(11, definitionSource);
        set(12, contentComponentDefinition);
        set(13, revisionDoc);
        set(14, state);
        set(15, createdBy);
        set(16, lastUpdatedBy);
        set(17, ownerUserId);
        set(18, creationTimestamp);
        set(19, lastUpdateTimestamp);
        set(20, isDeprecated);
        set(21, prevDtId);
        set(22, nextDtId);
    }
}