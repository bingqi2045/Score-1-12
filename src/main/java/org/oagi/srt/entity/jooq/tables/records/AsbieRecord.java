/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Asbie;


/**
 * An ASBIE represents a relationship/association between two ABIEs through 
 * an ASBIEP. It is a contextualization of an ASCC.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsbieRecord extends UpdatableRecordImpl<AsbieRecord> implements Record17<ULong, String, ULong, ULong, ULong, String, Integer, Integer, Byte, String, ULong, ULong, Timestamp, Timestamp, BigDecimal, Byte, ULong> {

    private static final long serialVersionUID = 2050533447;

    /**
     * Setter for <code>oagi.asbie.asbie_id</code>. A internal, primary database key of an ASBIE.
     */
    public void setAsbieId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.asbie.asbie_id</code>. A internal, primary database key of an ASBIE.
     */
    public ULong getAsbieId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.asbie.guid</code>. A globally unique identifier (GUID) of an ASBIE. GUID of an ASBIE is different from its based ASCC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.asbie.guid</code>. A globally unique identifier (GUID) of an ASBIE. GUID of an ASBIE is different from its based ASCC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.asbie.from_abie_id</code>. A foreign key pointing to the ABIE table. FROM_ABIE_ID is basically  a parent data element (type) of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC or USER_EXTENSION_GROUP ACC.
     */
    public void setFromAbieId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.asbie.from_abie_id</code>. A foreign key pointing to the ABIE table. FROM_ABIE_ID is basically  a parent data element (type) of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC or USER_EXTENSION_GROUP ACC.
     */
    public ULong getFromAbieId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.asbie.to_asbiep_id</code>. A foreign key to the ASBIEP table. TO_ASBIEP_ID is basically a child data element of the FROM_ABIE_ID. The TO_ASBIEP_ID must be based on the TO_ASCCP_ID in the BASED_ASCC_ID.
     */
    public void setToAsbiepId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.asbie.to_asbiep_id</code>. A foreign key to the ASBIEP table. TO_ASBIEP_ID is basically a child data element of the FROM_ABIE_ID. The TO_ASBIEP_ID must be based on the TO_ASCCP_ID in the BASED_ASCC_ID.
     */
    public ULong getToAsbiepId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.asbie.based_ascc_id</code>. The BASED_ASCC_ID column refers to the ASCC record, which this ASBIE contextualizes.
     */
    public void setBasedAsccId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.asbie.based_ascc_id</code>. The BASED_ASCC_ID column refers to the ASCC record, which this ASBIE contextualizes.
     */
    public ULong getBasedAsccId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.asbie.definition</code>. Definition to override the ASCC definition. If NULL, it means that the definition should be derived from the based CC on the UI, expression generation, and any API.
     */
    public void setDefinition(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.asbie.definition</code>. Definition to override the ASCC definition. If NULL, it means that the definition should be derived from the based CC on the UI, expression generation, and any API.
     */
    public String getDefinition() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.asbie.cardinality_min</code>. Minimum occurence constraint of the TO_ASBIEP_ID. A valid value is a non-negative integer.
     */
    public void setCardinalityMin(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.asbie.cardinality_min</code>. Minimum occurence constraint of the TO_ASBIEP_ID. A valid value is a non-negative integer.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>oagi.asbie.cardinality_max</code>. Maximum occurrence constraint of the TO_ASBIEP_ID. A valid value is an integer from -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public void setCardinalityMax(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.asbie.cardinality_max</code>. Maximum occurrence constraint of the TO_ASBIEP_ID. A valid value is an integer from -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>oagi.asbie.is_nillable</code>. Indicate whether the TO_ASBIEP_ID is allowed to be null.
     */
    public void setIsNillable(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.asbie.is_nillable</code>. Indicate whether the TO_ASBIEP_ID is allowed to be null.
     */
    public Byte getIsNillable() {
        return (Byte) get(8);
    }

    /**
     * Setter for <code>oagi.asbie.remark</code>. This column allows the user to specify very context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public void setRemark(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.asbie.remark</code>. This column allows the user to specify very context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public String getRemark() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.asbie.created_by</code>. A foreign key referring to the user who creates the ASBIE. The creator of the ASBIE is also its owner by default. ASBIEs created as children of another ABIE have the same CREATED_BY.
     */
    public void setCreatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.asbie.created_by</code>. A foreign key referring to the user who creates the ASBIE. The creator of the ASBIE is also its owner by default. ASBIEs created as children of another ABIE have the same CREATED_BY.
     */
    public ULong getCreatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.asbie.last_updated_by</code>. A foreign key referring to the user who has last updated the ASBIE record. 
     */
    public void setLastUpdatedBy(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.asbie.last_updated_by</code>. A foreign key referring to the user who has last updated the ASBIE record. 
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.asbie.creation_timestamp</code>. Timestamp when the ASBIE record was first created. ASBIEs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public void setCreationTimestamp(Timestamp value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.asbie.creation_timestamp</code>. Timestamp when the ASBIE record was first created. ASBIEs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(12);
    }

    /**
     * Setter for <code>oagi.asbie.last_update_timestamp</code>. The timestamp when the ASBIE was last updated.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.asbie.last_update_timestamp</code>. The timestamp when the ASBIE was last updated.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(13);
    }

    /**
     * Setter for <code>oagi.asbie.seq_key</code>. This indicates the order of the associations among other siblings. The SEQ_KEY for BIEs is decimal in order to accomodate the removal of inheritance hierarchy and group. For example, children of the most abstract ACC will have SEQ_KEY = 1.1, 1.2, 1.3, and so on; and SEQ_KEY of the next abstraction level ACC will have SEQ_KEY = 2.1, 2.2, 2.3 and so on so forth.
     */
    public void setSeqKey(BigDecimal value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.asbie.seq_key</code>. This indicates the order of the associations among other siblings. The SEQ_KEY for BIEs is decimal in order to accomodate the removal of inheritance hierarchy and group. For example, children of the most abstract ACC will have SEQ_KEY = 1.1, 1.2, 1.3, and so on; and SEQ_KEY of the next abstraction level ACC will have SEQ_KEY = 2.1, 2.2, 2.3 and so on so forth.
     */
    public BigDecimal getSeqKey() {
        return (BigDecimal) get(14);
    }

    /**
     * Setter for <code>oagi.asbie.is_used</code>. Flag to indicate whether the field/component is used in the content model. It signifies whether the field/component should be generated.
     */
    public void setIsUsed(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.asbie.is_used</code>. Flag to indicate whether the field/component is used in the content model. It signifies whether the field/component should be generated.
     */
    public Byte getIsUsed() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>oagi.asbie.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE which owns this ASBIE record.
     */
    public void setOwnerTopLevelAbieId(ULong value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.asbie.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE which owns this ASBIE record.
     */
    public ULong getOwnerTopLevelAbieId() {
        return (ULong) get(16);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record17 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<ULong, String, ULong, ULong, ULong, String, Integer, Integer, Byte, String, ULong, ULong, Timestamp, Timestamp, BigDecimal, Byte, ULong> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<ULong, String, ULong, ULong, ULong, String, Integer, Integer, Byte, String, ULong, ULong, Timestamp, Timestamp, BigDecimal, Byte, ULong> valuesRow() {
        return (Row17) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field1() {
        return Asbie.ASBIE.ASBIE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Asbie.ASBIE.GUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field3() {
        return Asbie.ASBIE.FROM_ABIE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field4() {
        return Asbie.ASBIE.TO_ASBIEP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field5() {
        return Asbie.ASBIE.BASED_ASCC_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Asbie.ASBIE.DEFINITION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return Asbie.ASBIE.CARDINALITY_MIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return Asbie.ASBIE.CARDINALITY_MAX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field9() {
        return Asbie.ASBIE.IS_NILLABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Asbie.ASBIE.REMARK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field11() {
        return Asbie.ASBIE.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field12() {
        return Asbie.ASBIE.LAST_UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field13() {
        return Asbie.ASBIE.CREATION_TIMESTAMP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field14() {
        return Asbie.ASBIE.LAST_UPDATE_TIMESTAMP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field15() {
        return Asbie.ASBIE.SEQ_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field16() {
        return Asbie.ASBIE.IS_USED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ULong> field17() {
        return Asbie.ASBIE.OWNER_TOP_LEVEL_ABIE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component1() {
        return getAsbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getGuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component3() {
        return getFromAbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component4() {
        return getToAsbiepId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component5() {
        return getBasedAsccId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getDefinition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getCardinalityMin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getCardinalityMax();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component9() {
        return getIsNillable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getRemark();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component11() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component12() {
        return getLastUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component13() {
        return getCreationTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component14() {
        return getLastUpdateTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component15() {
        return getSeqKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component16() {
        return getIsUsed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong component17() {
        return getOwnerTopLevelAbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value1() {
        return getAsbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGuid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value3() {
        return getFromAbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value4() {
        return getToAsbiepId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value5() {
        return getBasedAsccId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getDefinition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getCardinalityMin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getCardinalityMax();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value9() {
        return getIsNillable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getRemark();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value11() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value12() {
        return getLastUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value13() {
        return getCreationTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value14() {
        return getLastUpdateTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value15() {
        return getSeqKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value16() {
        return getIsUsed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ULong value17() {
        return getOwnerTopLevelAbieId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value1(ULong value) {
        setAsbieId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value2(String value) {
        setGuid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value3(ULong value) {
        setFromAbieId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value4(ULong value) {
        setToAsbiepId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value5(ULong value) {
        setBasedAsccId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value6(String value) {
        setDefinition(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value7(Integer value) {
        setCardinalityMin(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value8(Integer value) {
        setCardinalityMax(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value9(Byte value) {
        setIsNillable(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value10(String value) {
        setRemark(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value11(ULong value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value12(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value13(Timestamp value) {
        setCreationTimestamp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value14(Timestamp value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value15(BigDecimal value) {
        setSeqKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value16(Byte value) {
        setIsUsed(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord value17(ULong value) {
        setOwnerTopLevelAbieId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsbieRecord values(ULong value1, String value2, ULong value3, ULong value4, ULong value5, String value6, Integer value7, Integer value8, Byte value9, String value10, ULong value11, ULong value12, Timestamp value13, Timestamp value14, BigDecimal value15, Byte value16, ULong value17) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AsbieRecord
     */
    public AsbieRecord() {
        super(Asbie.ASBIE);
    }

    /**
     * Create a detached, initialised AsbieRecord
     */
    public AsbieRecord(ULong asbieId, String guid, ULong fromAbieId, ULong toAsbiepId, ULong basedAsccId, String definition, Integer cardinalityMin, Integer cardinalityMax, Byte isNillable, String remark, ULong createdBy, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp, BigDecimal seqKey, Byte isUsed, ULong ownerTopLevelAbieId) {
        super(Asbie.ASBIE);

        set(0, asbieId);
        set(1, guid);
        set(2, fromAbieId);
        set(3, toAsbiepId);
        set(4, basedAsccId);
        set(5, definition);
        set(6, cardinalityMin);
        set(7, cardinalityMax);
        set(8, isNillable);
        set(9, remark);
        set(10, createdBy);
        set(11, lastUpdatedBy);
        set(12, creationTimestamp);
        set(13, lastUpdateTimestamp);
        set(14, seqKey);
        set(15, isUsed);
        set(16, ownerTopLevelAbieId);
    }
}
