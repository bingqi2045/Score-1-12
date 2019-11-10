/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.DtSc;


/**
 * This table represents the supplementary component (SC) of a DT. Revision 
 * is not tracked at the supplementary component. It is considered intrinsic 
 * part of the DT. In other words, when a new revision of a DT is created 
 * a new set of supplementary components is created along with it. 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtScRecord extends UpdatableRecordImpl<DtScRecord> implements Record12<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String> {

    private static final long serialVersionUID = -183056835;

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
     * Setter for <code>oagi.dt_sc.guid</code>. A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.dt_sc.guid</code>. A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.
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

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<ULong, String, String, String, String, String, ULong, Integer, Integer, ULong, String, String> valuesRow() {
        return (Row12) super.valuesRow();
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
    public DtScRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, ULong value7, Integer value8, Integer value9, ULong value10, String value11, String value12) {
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
    public DtScRecord(ULong dtScId, String guid, String propertyTerm, String representationTerm, String definition, String definitionSource, ULong ownerDtId, Integer cardinalityMin, Integer cardinalityMax, ULong basedDtScId, String defaultValue, String fixedValue) {
        super(DtSc.DT_SC);

        set(0, dtScId);
        set(1, guid);
        set(2, propertyTerm);
        set(3, representationTerm);
        set(4, definition);
        set(5, definitionSource);
        set(6, ownerDtId);
        set(7, cardinalityMin);
        set(8, cardinalityMax);
        set(9, basedDtScId);
        set(10, defaultValue);
        set(11, fixedValue);
    }
}
