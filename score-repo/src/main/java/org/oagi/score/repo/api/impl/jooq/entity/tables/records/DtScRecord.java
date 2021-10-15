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
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtSc;


/**
 * This table represents the supplementary component (    SC) of a DT. Revision 
 * is not tracked at the supplementary component. It is considered intrinsic 
 * part of the DT. In other words, when a new revision of a DT is created 
 * a new set of supplementary components is created along with it. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtScRecord extends UpdatableRecordImpl<DtScRecord> implements Record21<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.dt_sc.dt_sc_id</code>. Internal, primary database key.
     */
    public void setDtScId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.dt_sc_id</code>. Internal, primary database key.
     */
    public ULong getDtScId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.dt_sc.guid</code>. A globally unique identifier (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.guid</code>. A globally unique identifier (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.dt_sc.property_term</code>. Property term of the SC.
     */
    public void setPropertyTerm(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.property_term</code>. Property term of the SC.
     */
    public String getPropertyTerm() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.dt_sc.representation_term</code>. Representation of the supplementary component.
     */
    public void setRepresentationTerm(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.representation_term</code>. Representation of the supplementary component.
     */
    public String getRepresentationTerm() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.dt_sc.definition</code>. Description of the supplementary component.
     */
    public void setDefinition(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.definition</code>. Description of the supplementary component.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.dt_sc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.dt_sc.owner_dt_id</code>. Foreigned key to the DT table indicating the data type, to which this supplementary component belongs.
     */
    public void setOwnerDtId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.owner_dt_id</code>. Foreigned key to the DT table indicating the data type, to which this supplementary component belongs.
     */
    public ULong getOwnerDtId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.dt_sc.cardinality_min</code>. The minimum occurrence constraint associated with the supplementary component. The valid values zero or one.
     */
    public void setCardinalityMin(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.cardinality_min</code>. The minimum occurrence constraint associated with the supplementary component. The valid values zero or one.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>oagi.dt_sc.cardinality_max</code>. The maximum occurrence constraint associated with the supplementary component. The valid values are zero or one. Zero is used when the SC is restricted from an instantiation in the data type.
     */
    public void setCardinalityMax(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.cardinality_max</code>. The maximum occurrence constraint associated with the supplementary component. The valid values are zero or one. Zero is used when the SC is restricted from an instantiation in the data type.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>oagi.dt_sc.based_dt_sc_id</code>. Foreign key to the DT_SC table itself. This column is used when the SC is derived from the based DT.
     */
    public void setBasedDtScId(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.based_dt_sc_id</code>. Foreign key to the DT_SC table itself. This column is used when the SC is derived from the based DT.
     */
    public ULong getBasedDtScId() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.dt_sc.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public void setDefaultValue(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public String getDefaultValue() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.dt_sc.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public void setFixedValue(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public String getFixedValue() {
        return (String) get(11);
    }

    /**
     * Setter for <code>oagi.dt_sc.is_deprecated</code>. Indicates whether this is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public void setIsDeprecated(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.is_deprecated</code>. Indicates whether this is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>oagi.dt_sc.replacement_dt_sc_id</code>. This refers to a replacement if the record is deprecated.
     */
    public void setReplacementDtScId(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.replacement_dt_sc_id</code>. This refers to a replacement if the record is deprecated.
     */
    public ULong getReplacementDtScId() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.dt_sc.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public void setCreatedBy(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public ULong getCreatedBy() {
        return (ULong) get(14);
    }

    /**
     * Setter for <code>oagi.dt_sc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(15);
    }

    /**
     * Setter for <code>oagi.dt_sc.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public void setLastUpdatedBy(ULong value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(16);
    }

    /**
     * Setter for <code>oagi.dt_sc.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(17);
    }

    /**
     * Setter for <code>oagi.dt_sc.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(18);
    }

    /**
     * Setter for <code>oagi.dt_sc.prev_dt_sc_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevDtScId(ULong value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.prev_dt_sc_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevDtScId() {
        return (ULong) get(19);
    }

    /**
     * Setter for <code>oagi.dt_sc.next_dt_sc_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextDtScId(ULong value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.next_dt_sc_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextDtScId() {
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
    public Row21<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    @Override
    public Row21<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String, Byte, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> valuesRow() {
        return (Row21) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return DtSc.DT_SC.DT_SC_ID;
    }

    @Override
    public Field<String> field2() {
        return DtSc.DT_SC.GUID;
    }

    @Override
    public Field<String> field3() {
        return DtSc.DT_SC.PROPERTY_TERM;
    }

    @Override
    public Field<String> field4() {
        return DtSc.DT_SC.REPRESENTATION_TERM;
    }

    @Override
    public Field<String> field5() {
        return DtSc.DT_SC.DEFINITION;
    }

    @Override
    public Field<String> field6() {
        return DtSc.DT_SC.DEFINITION_SOURCE;
    }

    @Override
    public Field<ULong> field7() {
        return DtSc.DT_SC.OWNER_DT_ID;
    }

    @Override
    public Field<Integer> field8() {
        return DtSc.DT_SC.CARDINALITY_MIN;
    }

    @Override
    public Field<Integer> field9() {
        return DtSc.DT_SC.CARDINALITY_MAX;
    }

    @Override
    public Field<ULong> field10() {
        return DtSc.DT_SC.BASED_DT_SC_ID;
    }

    @Override
    public Field<String> field11() {
        return DtSc.DT_SC.DEFAULT_VALUE;
    }

    @Override
    public Field<String> field12() {
        return DtSc.DT_SC.FIXED_VALUE;
    }

    @Override
    public Field<Byte> field13() {
        return DtSc.DT_SC.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field14() {
        return DtSc.DT_SC.REPLACEMENT_DT_SC_ID;
    }

    @Override
    public Field<ULong> field15() {
        return DtSc.DT_SC.CREATED_BY;
    }

    @Override
    public Field<ULong> field16() {
        return DtSc.DT_SC.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field17() {
        return DtSc.DT_SC.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field18() {
        return DtSc.DT_SC.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field19() {
        return DtSc.DT_SC.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field20() {
        return DtSc.DT_SC.PREV_DT_SC_ID;
    }

    @Override
    public Field<ULong> field21() {
        return DtSc.DT_SC.NEXT_DT_SC_ID;
    }

    @Override
    public ULong component1() {
        return getDtScId();
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
    public String component5() {
        return getDefinition();
    }

    @Override
    public String component6() {
        return getDefinitionSource();
    }

    @Override
    public ULong component7() {
        return getOwnerDtId();
    }

    @Override
    public Integer component8() {
        return getCardinalityMin();
    }

    @Override
    public Integer component9() {
        return getCardinalityMax();
    }

    @Override
    public ULong component10() {
        return getBasedDtScId();
    }

    @Override
    public String component11() {
        return getDefaultValue();
    }

    @Override
    public String component12() {
        return getFixedValue();
    }

    @Override
    public Byte component13() {
        return getIsDeprecated();
    }

    @Override
    public ULong component14() {
        return getReplacementDtScId();
    }

    @Override
    public ULong component15() {
        return getCreatedBy();
    }

    @Override
    public ULong component16() {
        return getOwnerUserId();
    }

    @Override
    public ULong component17() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component18() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component19() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component20() {
        return getPrevDtScId();
    }

    @Override
    public ULong component21() {
        return getNextDtScId();
    }

    @Override
    public ULong value1() {
        return getDtScId();
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
    public String value5() {
        return getDefinition();
    }

    @Override
    public String value6() {
        return getDefinitionSource();
    }

    @Override
    public ULong value7() {
        return getOwnerDtId();
    }

    @Override
    public Integer value8() {
        return getCardinalityMin();
    }

    @Override
    public Integer value9() {
        return getCardinalityMax();
    }

    @Override
    public ULong value10() {
        return getBasedDtScId();
    }

    @Override
    public String value11() {
        return getDefaultValue();
    }

    @Override
    public String value12() {
        return getFixedValue();
    }

    @Override
    public Byte value13() {
        return getIsDeprecated();
    }

    @Override
    public ULong value14() {
        return getReplacementDtScId();
    }

    @Override
    public ULong value15() {
        return getCreatedBy();
    }

    @Override
    public ULong value16() {
        return getOwnerUserId();
    }

    @Override
    public ULong value17() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value18() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value19() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value20() {
        return getPrevDtScId();
    }

    @Override
    public ULong value21() {
        return getNextDtScId();
    }

    @Override
    public DtScRecord value1(ULong value) {
        setDtScId(value);
        return this;
    }

    @Override
    public DtScRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public DtScRecord value3(String value) {
        setPropertyTerm(value);
        return this;
    }

    @Override
    public DtScRecord value4(String value) {
        setRepresentationTerm(value);
        return this;
    }

    @Override
    public DtScRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public DtScRecord value6(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public DtScRecord value7(ULong value) {
        setOwnerDtId(value);
        return this;
    }

    @Override
    public DtScRecord value8(Integer value) {
        setCardinalityMin(value);
        return this;
    }

    @Override
    public DtScRecord value9(Integer value) {
        setCardinalityMax(value);
        return this;
    }

    @Override
    public DtScRecord value10(ULong value) {
        setBasedDtScId(value);
        return this;
    }

    @Override
    public DtScRecord value11(String value) {
        setDefaultValue(value);
        return this;
    }

    @Override
    public DtScRecord value12(String value) {
        setFixedValue(value);
        return this;
    }

    @Override
    public DtScRecord value13(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public DtScRecord value14(ULong value) {
        setReplacementDtScId(value);
        return this;
    }

    @Override
    public DtScRecord value15(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public DtScRecord value16(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public DtScRecord value17(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public DtScRecord value18(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public DtScRecord value19(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public DtScRecord value20(ULong value) {
        setPrevDtScId(value);
        return this;
    }

    @Override
    public DtScRecord value21(ULong value) {
        setNextDtScId(value);
        return this;
    }

    @Override
    public DtScRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, ULong value7, Integer value8, Integer value9, ULong value10, String value11, String value12, Byte value13, ULong value14, ULong value15, ULong value16, ULong value17, LocalDateTime value18, LocalDateTime value19, ULong value20, ULong value21) {
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
     * Create a detached DtScRecord
     */
    public DtScRecord() {
        super(DtSc.DT_SC);
    }

    /**
     * Create a detached, initialised DtScRecord
     */
    public DtScRecord(ULong dtScId, String guid, String propertyTerm, String representationTerm, String definition, String definitionSource, ULong ownerDtId, Integer cardinalityMin, Integer cardinalityMax, ULong basedDtScId, String defaultValue, String fixedValue, Byte isDeprecated, ULong replacementDtScId, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, ULong prevDtScId, ULong nextDtScId) {
        super(DtSc.DT_SC);

        setDtScId(dtScId);
        setGuid(guid);
        setPropertyTerm(propertyTerm);
        setRepresentationTerm(representationTerm);
        setDefinition(definition);
        setDefinitionSource(definitionSource);
        setOwnerDtId(ownerDtId);
        setCardinalityMin(cardinalityMin);
        setCardinalityMax(cardinalityMax);
        setBasedDtScId(basedDtScId);
        setDefaultValue(defaultValue);
        setFixedValue(fixedValue);
        setIsDeprecated(isDeprecated);
        setReplacementDtScId(replacementDtScId);
        setCreatedBy(createdBy);
        setOwnerUserId(ownerUserId);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setPrevDtScId(prevDtScId);
        setNextDtScId(nextDtScId);
    }
}
