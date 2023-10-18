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
import org.oagi.score.repo.api.impl.jooq.entity.tables.Acc;


/**
 * The ACC table holds information about complex data structured concepts. For
 * example, OAGIS's Components, Nouns, and BODs are captured in the ACC table.
 * 
 * Note that only Extension is supported when deriving ACC from another ACC. (So
 * if there is a restriction needed, maybe that concept should placed higher in
 * the derivation hierarchy rather than lower.)
 * 
 * In OAGIS, all XSD extensions will be treated as a qualification of an ACC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccRecord extends UpdatableRecordImpl<AccRecord> implements Record21<ULong, String, String, String, String, String, ULong, String, Integer, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, ULong, Byte, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.acc.acc_id</code>. A internal, primary database key
     * of an ACC.
     */
    public void setAccId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.acc.acc_id</code>. A internal, primary database key
     * of an ACC.
     */
    public ULong getAccId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.acc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.acc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.acc.type</code>. The Type of the ACC. List:
     * Default, Extension, AllExtension.
     */
    public void setType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.acc.type</code>. The Type of the ACC. List:
     * Default, Extension, AllExtension.
     */
    public String getType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.acc.object_class_term</code>. Object class name of
     * the ACC concept. For OAGIS, this is generally name of a type with the
     * "Type" truncated from the end. Per CCS the name is space separated. "ID"
     * is expanded to "Identifier".
     */
    public void setObjectClassTerm(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.acc.object_class_term</code>. Object class name of
     * the ACC concept. For OAGIS, this is generally name of a type with the
     * "Type" truncated from the end. Per CCS the name is space separated. "ID"
     * is expanded to "Identifier".
     */
    public String getObjectClassTerm() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.acc.definition</code>. This is a documentation or
     * description of the ACC. Since ACC is business context independent, this
     * is a business context independent description of the ACC concept.
     */
    public void setDefinition(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.acc.definition</code>. This is a documentation or
     * description of the ACC. Since ACC is business context independent, this
     * is a business context independent description of the ACC concept.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.acc.definition_source</code>. This is typically a
     * URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.acc.definition_source</code>. This is typically a
     * URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.acc.based_acc_id</code>. BASED_ACC_ID is a foreign
     * key to the ACC table itself. It represents the ACC that is qualified by
     * this ACC. In general CCS sense, a qualification can be a content
     * extension or restriction, but the current scope supports only extension.
     */
    public void setBasedAccId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.acc.based_acc_id</code>. BASED_ACC_ID is a foreign
     * key to the ACC table itself. It represents the ACC that is qualified by
     * this ACC. In general CCS sense, a qualification can be a content
     * extension or restriction, but the current scope supports only extension.
     */
    public ULong getBasedAccId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.acc.object_class_qualifier</code>. This column
     * stores the qualifier of an ACC, particularly when it has a based ACC. 
     */
    public void setObjectClassQualifier(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.acc.object_class_qualifier</code>. This column
     * stores the qualifier of an ACC, particularly when it has a based ACC. 
     */
    public String getObjectClassQualifier() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.acc.oagis_component_type</code>. The value can be 0
     * = BASE, 1 = SEMANTICS, 2 = EXTENSION, 3 = SEMANTIC_GROUP, 4 =
     * USER_EXTENSION_GROUP, 5 = EMBEDDED. Generally, BASE is assigned when the
     * OBJECT_CLASS_TERM contains "Base" at the end. EXTENSION is assigned with
     * the OBJECT_CLASS_TERM contains "Extension" at the end. SEMANTIC_GROUP is
     * assigned when an ACC is imported from an XSD Group. USER_EXTENSION_GROUP
     * is a wrapper ACC (a virtual ACC) for segregating user's extension
     * content. EMBEDDED is used for an ACC whose content is not explicitly
     * defined in the database, for example, the Any Structured Content ACC that
     * corresponds to the xsd:any.  Other cases are assigned SEMANTICS. 
     */
    public void setOagisComponentType(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.acc.oagis_component_type</code>. The value can be 0
     * = BASE, 1 = SEMANTICS, 2 = EXTENSION, 3 = SEMANTIC_GROUP, 4 =
     * USER_EXTENSION_GROUP, 5 = EMBEDDED. Generally, BASE is assigned when the
     * OBJECT_CLASS_TERM contains "Base" at the end. EXTENSION is assigned with
     * the OBJECT_CLASS_TERM contains "Extension" at the end. SEMANTIC_GROUP is
     * assigned when an ACC is imported from an XSD Group. USER_EXTENSION_GROUP
     * is a wrapper ACC (a virtual ACC) for segregating user's extension
     * content. EMBEDDED is used for an ACC whose content is not explicitly
     * defined in the database, for example, the Any Structured Content ACC that
     * corresponds to the xsd:any.  Other cases are assigned SEMANTICS. 
     */
    public Integer getOagisComponentType() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>oagi.acc.namespace_id</code>. Foreign key to the
     * NAMESPACE table. This is the namespace to which the entity belongs. This
     * namespace column is primarily used in the case the component is a user's
     * component because there is also a namespace assigned at the release
     * level.
     */
    public void setNamespaceId(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.acc.namespace_id</code>. Foreign key to the
     * NAMESPACE table. This is the namespace to which the entity belongs. This
     * namespace column is primarily used in the case the component is a user's
     * component because there is also a namespace assigned at the release
     * level.
     */
    public ULong getNamespaceId() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.acc.created_by</code>. Foreign key to the APP_USER
     * table referring to the user who creates the entity.\n\nThis column never
     * change between the history and the current record for a given revision.
     * The history record should have the same value as that of its current
     * record.
     */
    public void setCreatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.acc.created_by</code>. Foreign key to the APP_USER
     * table referring to the user who creates the entity.\n\nThis column never
     * change between the history and the current record for a given revision.
     * The history record should have the same value as that of its current
     * record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.acc.owner_user_id</code>. Foreign key to the
     * APP_USER table. This is the user who owns the entity, is allowed to edit
     * the entity, and who can transfer the ownership to another user.\n\nThe
     * ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership. 
     */
    public void setOwnerUserId(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.acc.owner_user_id</code>. Foreign key to the
     * APP_USER table. This is the user who owns the entity, is allowed to edit
     * the entity, and who can transfer the ownership to another user.\n\nThe
     * ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership. 
     */
    public ULong getOwnerUserId() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.acc.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who updated the record. \n\nIn
     * the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.acc.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who updated the record. \n\nIn
     * the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.acc.creation_timestamp</code>. Timestamp when the
     * revision of the ACC was created. \n\nThis never change for a revision.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.acc.creation_timestamp</code>. Timestamp when the
     * revision of the ACC was created. \n\nThis never change for a revision.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>oagi.acc.last_update_timestamp</code>. The timestamp
     * when the record was last updated.\n\nThe value of this column in the
     * latest history record should be the same as that of the current record.
     * This column keeps the record of when the revision has occurred.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.acc.last_update_timestamp</code>. The timestamp
     * when the record was last updated.\n\nThe value of this column in the
     * latest history record should be the same as that of the current record.
     * This column keeps the record of when the revision has occurred.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>oagi.acc.state</code>. Deleted, WIP, Draft, QA,
     * Candidate, Production, Release Draft, Published. This the revision life
     * cycle state of the ACC.
     * 
     * State change can't be undone. But the history record can still keep the
     * records of when the state was changed.
     */
    public void setState(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.acc.state</code>. Deleted, WIP, Draft, QA,
     * Candidate, Production, Release Draft, Published. This the revision life
     * cycle state of the ACC.
     * 
     * State change can't be undone. But the history record can still keep the
     * records of when the state was changed.
     */
    public String getState() {
        return (String) get(15);
    }

    /**
     * Setter for <code>oagi.acc.is_deprecated</code>. Indicates whether the CC
     * is deprecated and should not be reused (i.e., no new reference to this
     * record should be allowed).
     */
    public void setIsDeprecated(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.acc.is_deprecated</code>. Indicates whether the CC
     * is deprecated and should not be reused (i.e., no new reference to this
     * record should be allowed).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(16);
    }

    /**
     * Setter for <code>oagi.acc.replacement_acc_id</code>. This refers to a
     * replacement if the record is deprecated.
     */
    public void setReplacementAccId(ULong value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.acc.replacement_acc_id</code>. This refers to a
     * replacement if the record is deprecated.
     */
    public ULong getReplacementAccId() {
        return (ULong) get(17);
    }

    /**
     * Setter for <code>oagi.acc.is_abstract</code>. This is the XML Schema
     * abstract flag. Default is false. If it is true, the abstract flag will be
     * set to true when generating a corresponding xsd:complexType. So although
     * this flag may not apply to some ACCs such as those that are xsd:group. It
     * is still have a false value.
     */
    public void setIsAbstract(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.acc.is_abstract</code>. This is the XML Schema
     * abstract flag. Default is false. If it is true, the abstract flag will be
     * set to true when generating a corresponding xsd:complexType. So although
     * this flag may not apply to some ACCs such as those that are xsd:group. It
     * is still have a false value.
     */
    public Byte getIsAbstract() {
        return (Byte) get(18);
    }

    /**
     * Setter for <code>oagi.acc.prev_acc_id</code>. A self-foreign key to
     * indicate the previous history record.
     */
    public void setPrevAccId(ULong value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.acc.prev_acc_id</code>. A self-foreign key to
     * indicate the previous history record.
     */
    public ULong getPrevAccId() {
        return (ULong) get(19);
    }

    /**
     * Setter for <code>oagi.acc.next_acc_id</code>. A self-foreign key to
     * indicate the next history record.
     */
    public void setNextAccId(ULong value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.acc.next_acc_id</code>. A self-foreign key to
     * indicate the next history record.
     */
    public ULong getNextAccId() {
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
    public Row21<ULong, String, String, String, String, String, ULong, String, Integer, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, ULong, Byte, ULong, ULong> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    @Override
    public Row21<ULong, String, String, String, String, String, ULong, String, Integer, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, Byte, ULong, Byte, ULong, ULong> valuesRow() {
        return (Row21) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Acc.ACC.ACC_ID;
    }

    @Override
    public Field<String> field2() {
        return Acc.ACC.GUID;
    }

    @Override
    public Field<String> field3() {
        return Acc.ACC.TYPE;
    }

    @Override
    public Field<String> field4() {
        return Acc.ACC.OBJECT_CLASS_TERM;
    }

    @Override
    public Field<String> field5() {
        return Acc.ACC.DEFINITION;
    }

    @Override
    public Field<String> field6() {
        return Acc.ACC.DEFINITION_SOURCE;
    }

    @Override
    public Field<ULong> field7() {
        return Acc.ACC.BASED_ACC_ID;
    }

    @Override
    public Field<String> field8() {
        return Acc.ACC.OBJECT_CLASS_QUALIFIER;
    }

    @Override
    public Field<Integer> field9() {
        return Acc.ACC.OAGIS_COMPONENT_TYPE;
    }

    @Override
    public Field<ULong> field10() {
        return Acc.ACC.NAMESPACE_ID;
    }

    @Override
    public Field<ULong> field11() {
        return Acc.ACC.CREATED_BY;
    }

    @Override
    public Field<ULong> field12() {
        return Acc.ACC.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field13() {
        return Acc.ACC.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field14() {
        return Acc.ACC.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return Acc.ACC.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field16() {
        return Acc.ACC.STATE;
    }

    @Override
    public Field<Byte> field17() {
        return Acc.ACC.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field18() {
        return Acc.ACC.REPLACEMENT_ACC_ID;
    }

    @Override
    public Field<Byte> field19() {
        return Acc.ACC.IS_ABSTRACT;
    }

    @Override
    public Field<ULong> field20() {
        return Acc.ACC.PREV_ACC_ID;
    }

    @Override
    public Field<ULong> field21() {
        return Acc.ACC.NEXT_ACC_ID;
    }

    @Override
    public ULong component1() {
        return getAccId();
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
        return getObjectClassTerm();
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
        return getBasedAccId();
    }

    @Override
    public String component8() {
        return getObjectClassQualifier();
    }

    @Override
    public Integer component9() {
        return getOagisComponentType();
    }

    @Override
    public ULong component10() {
        return getNamespaceId();
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
        return getIsDeprecated();
    }

    @Override
    public ULong component18() {
        return getReplacementAccId();
    }

    @Override
    public Byte component19() {
        return getIsAbstract();
    }

    @Override
    public ULong component20() {
        return getPrevAccId();
    }

    @Override
    public ULong component21() {
        return getNextAccId();
    }

    @Override
    public ULong value1() {
        return getAccId();
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
        return getObjectClassTerm();
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
        return getBasedAccId();
    }

    @Override
    public String value8() {
        return getObjectClassQualifier();
    }

    @Override
    public Integer value9() {
        return getOagisComponentType();
    }

    @Override
    public ULong value10() {
        return getNamespaceId();
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
        return getIsDeprecated();
    }

    @Override
    public ULong value18() {
        return getReplacementAccId();
    }

    @Override
    public Byte value19() {
        return getIsAbstract();
    }

    @Override
    public ULong value20() {
        return getPrevAccId();
    }

    @Override
    public ULong value21() {
        return getNextAccId();
    }

    @Override
    public AccRecord value1(ULong value) {
        setAccId(value);
        return this;
    }

    @Override
    public AccRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AccRecord value3(String value) {
        setType(value);
        return this;
    }

    @Override
    public AccRecord value4(String value) {
        setObjectClassTerm(value);
        return this;
    }

    @Override
    public AccRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AccRecord value6(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public AccRecord value7(ULong value) {
        setBasedAccId(value);
        return this;
    }

    @Override
    public AccRecord value8(String value) {
        setObjectClassQualifier(value);
        return this;
    }

    @Override
    public AccRecord value9(Integer value) {
        setOagisComponentType(value);
        return this;
    }

    @Override
    public AccRecord value10(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public AccRecord value11(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AccRecord value12(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public AccRecord value13(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AccRecord value14(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AccRecord value15(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AccRecord value16(String value) {
        setState(value);
        return this;
    }

    @Override
    public AccRecord value17(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public AccRecord value18(ULong value) {
        setReplacementAccId(value);
        return this;
    }

    @Override
    public AccRecord value19(Byte value) {
        setIsAbstract(value);
        return this;
    }

    @Override
    public AccRecord value20(ULong value) {
        setPrevAccId(value);
        return this;
    }

    @Override
    public AccRecord value21(ULong value) {
        setNextAccId(value);
        return this;
    }

    @Override
    public AccRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, ULong value7, String value8, Integer value9, ULong value10, ULong value11, ULong value12, ULong value13, LocalDateTime value14, LocalDateTime value15, String value16, Byte value17, ULong value18, Byte value19, ULong value20, ULong value21) {
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
     * Create a detached AccRecord
     */
    public AccRecord() {
        super(Acc.ACC);
    }

    /**
     * Create a detached, initialised AccRecord
     */
    public AccRecord(ULong accId, String guid, String type, String objectClassTerm, String definition, String definitionSource, ULong basedAccId, String objectClassQualifier, Integer oagisComponentType, ULong namespaceId, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, Byte isDeprecated, ULong replacementAccId, Byte isAbstract, ULong prevAccId, ULong nextAccId) {
        super(Acc.ACC);

        setAccId(accId);
        setGuid(guid);
        setType(type);
        setObjectClassTerm(objectClassTerm);
        setDefinition(definition);
        setDefinitionSource(definitionSource);
        setBasedAccId(basedAccId);
        setObjectClassQualifier(objectClassQualifier);
        setOagisComponentType(oagisComponentType);
        setNamespaceId(namespaceId);
        setCreatedBy(createdBy);
        setOwnerUserId(ownerUserId);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setState(state);
        setIsDeprecated(isDeprecated);
        setReplacementAccId(replacementAccId);
        setIsAbstract(isAbstract);
        setPrevAccId(prevAccId);
        setNextAccId(nextAccId);
        resetChangedOnNotNull();
    }
}
