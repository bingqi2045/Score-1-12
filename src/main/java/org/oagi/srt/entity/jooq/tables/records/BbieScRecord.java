/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.BbieSc;


/**
 * Because there is no single table that is a contextualized counterpart of
 * the DT table (which stores both CDT and BDT), The context specific constraints
 * associated with the DT are stored in the BBIE table, while this table stores
 * the constraints associated with the DT's SCs.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class BbieScRecord extends UpdatableRecordImpl<BbieScRecord> implements Record17<ULong, String, ULong, ULong, ULong, ULong, ULong, Integer, Integer, String, String, String, String, String, String, Byte, ULong> {

    private static final long serialVersionUID = 1711743364;

    /**
     * Setter for <code>oagi.bbie_sc.bbie_sc_id</code>. A internal, primary database key of a BBIE_SC.
     */
    public void setBbieScId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.bbie_sc_id</code>. A internal, primary database key of a BBIE_SC.
     */
    public ULong getBbieScId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bbie_sc.guid</code>. A globally unique identifier (GUID). It is different from the GUID fo the SC on the CC side. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.guid</code>. A globally unique identifier (GUID). It is different from the GUID fo the SC on the CC side. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bbie_sc.based_dt_sc_manifest_id</code>. Foreign key to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.
     */
    public void setBasedDtScManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.based_dt_sc_manifest_id</code>. Foreign key to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.
     */
    public ULong getBasedDtScManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bbie_sc.bbie_id</code>. The BBIE this BBIE_SC applies to.
     */
    public void setBbieId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.bbie_id</code>. The BBIE this BBIE_SC applies to.
     */
    public ULong getBbieId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.bbie_sc.dt_sc_pri_restri_id</code>. This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).
     * <p>
     * It is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.
     * <p>
     * This column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.
     */
    public void setDtScPriRestriId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.dt_sc_pri_restri_id</code>. This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).
     * <p>
     * It is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.
     * <p>
     * This column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.
     */
    public ULong getDtScPriRestriId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bbie_sc.code_list_id</code>. This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.
     * <p>
     * This column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.
     */
    public void setCodeListId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.code_list_id</code>. This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.
     * <p>
     * This column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.
     */
    public ULong getCodeListId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.bbie_sc.agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.
     * <p>
     * This column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.
     */
    public void setAgencyIdListId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.
     * <p>
     * This column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.
     */
    public ULong getAgencyIdListId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.bbie_sc.cardinality_min</code>. The minimum occurrence constraint for the BBIE SC. A valid value is 0 or 1.
     */
    public void setCardinalityMin(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.cardinality_min</code>. The minimum occurrence constraint for the BBIE SC. A valid value is 0 or 1.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>oagi.bbie_sc.cardinality_max</code>. Maximum occurence constraint of the BBIE SC. A valid value is 0 or 1.
     */
    public void setCardinalityMax(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.cardinality_max</code>. Maximum occurence constraint of the BBIE SC. A valid value is 0 or 1.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>oagi.bbie_sc.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public void setDefaultValue(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public String getDefaultValue() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.bbie_sc.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public void setFixedValue(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.fixed_value</code>. This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public String getFixedValue() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.bbie_sc.definition</code>. Description to override the BDT SC definition. If NULL, it means that the definition should be inherited from the based BDT SC.
     */
    public void setDefinition(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.definition</code>. Description to override the BDT SC definition. If NULL, it means that the definition should be inherited from the based BDT SC.
     */
    public String getDefinition() {
        return (String) get(11);
    }

    /**
     * Setter for <code>oagi.bbie_sc.example</code>.
     */
    public void setExample(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.example</code>.
     */
    public String getExample() {
        return (String) get(12);
    }

    /**
     * Setter for <code>oagi.bbie_sc.remark</code>. This column allows the user to specify a very context-specific usage of the BBIE SC. It is different from the Definition column in that the Definition column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others.
     */
    public void setRemark(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.remark</code>. This column allows the user to specify a very context-specific usage of the BBIE SC. It is different from the Definition column in that the Definition column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others.
     */
    public String getRemark() {
        return (String) get(13);
    }

    /**
     * Setter for <code>oagi.bbie_sc.biz_term</code>. Business term to indicate what the BBIE SC is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public void setBizTerm(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.biz_term</code>. Business term to indicate what the BBIE SC is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public String getBizTerm() {
        return (String) get(14);
    }

    /**
     * Setter for <code>oagi.bbie_sc.is_used</code>. Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated.
     */
    public void setIsUsed(Byte value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.is_used</code>. Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated.
     */
    public Byte getIsUsed() {
        return (Byte) get(15);
    }

    /**
     * Setter for <code>oagi.bbie_sc.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE. It specifies the top-level ABIE, which owns this BBIE_SC record.
     */
    public void setOwnerTopLevelAbieId(ULong value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE. It specifies the top-level ABIE, which owns this BBIE_SC record.
     */
    public ULong getOwnerTopLevelAbieId() {
        return (ULong) get(16);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record17 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row17<ULong, String, ULong, ULong, ULong, ULong, ULong, Integer, Integer, String, String, String, String, String, String, Byte, ULong> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    @Override
    public Row17<ULong, String, ULong, ULong, ULong, ULong, ULong, Integer, Integer, String, String, String, String, String, String, Byte, ULong> valuesRow() {
        return (Row17) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BbieSc.BBIE_SC.BBIE_SC_ID;
    }

    @Override
    public Field<String> field2() {
        return BbieSc.BBIE_SC.GUID;
    }

    @Override
    public Field<ULong> field3() {
        return BbieSc.BBIE_SC.BASED_DT_SC_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field4() {
        return BbieSc.BBIE_SC.BBIE_ID;
    }

    @Override
    public Field<ULong> field5() {
        return BbieSc.BBIE_SC.DT_SC_PRI_RESTRI_ID;
    }

    @Override
    public Field<ULong> field6() {
        return BbieSc.BBIE_SC.CODE_LIST_ID;
    }

    @Override
    public Field<ULong> field7() {
        return BbieSc.BBIE_SC.AGENCY_ID_LIST_ID;
    }

    @Override
    public Field<Integer> field8() {
        return BbieSc.BBIE_SC.CARDINALITY_MIN;
    }

    @Override
    public Field<Integer> field9() {
        return BbieSc.BBIE_SC.CARDINALITY_MAX;
    }

    @Override
    public Field<String> field10() {
        return BbieSc.BBIE_SC.DEFAULT_VALUE;
    }

    @Override
    public Field<String> field11() {
        return BbieSc.BBIE_SC.FIXED_VALUE;
    }

    @Override
    public Field<String> field12() {
        return BbieSc.BBIE_SC.DEFINITION;
    }

    @Override
    public Field<String> field13() {
        return BbieSc.BBIE_SC.EXAMPLE;
    }

    @Override
    public Field<String> field14() {
        return BbieSc.BBIE_SC.REMARK;
    }

    @Override
    public Field<String> field15() {
        return BbieSc.BBIE_SC.BIZ_TERM;
    }

    @Override
    public Field<Byte> field16() {
        return BbieSc.BBIE_SC.IS_USED;
    }

    @Override
    public Field<ULong> field17() {
        return BbieSc.BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID;
    }

    @Override
    public ULong component1() {
        return getBbieScId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public ULong component3() {
        return getBasedDtScManifestId();
    }

    @Override
    public ULong component4() {
        return getBbieId();
    }

    @Override
    public ULong component5() {
        return getDtScPriRestriId();
    }

    @Override
    public ULong component6() {
        return getCodeListId();
    }

    @Override
    public ULong component7() {
        return getAgencyIdListId();
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
    public String component10() {
        return getDefaultValue();
    }

    @Override
    public String component11() {
        return getFixedValue();
    }

    @Override
    public String component12() {
        return getDefinition();
    }

    @Override
    public String component13() {
        return getExample();
    }

    @Override
    public String component14() {
        return getRemark();
    }

    @Override
    public String component15() {
        return getBizTerm();
    }

    @Override
    public Byte component16() {
        return getIsUsed();
    }

    @Override
    public ULong component17() {
        return getOwnerTopLevelAbieId();
    }

    @Override
    public ULong value1() {
        return getBbieScId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public ULong value3() {
        return getBasedDtScManifestId();
    }

    @Override
    public ULong value4() {
        return getBbieId();
    }

    @Override
    public ULong value5() {
        return getDtScPriRestriId();
    }

    @Override
    public ULong value6() {
        return getCodeListId();
    }

    @Override
    public ULong value7() {
        return getAgencyIdListId();
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
    public String value10() {
        return getDefaultValue();
    }

    @Override
    public String value11() {
        return getFixedValue();
    }

    @Override
    public String value12() {
        return getDefinition();
    }

    @Override
    public String value13() {
        return getExample();
    }

    @Override
    public String value14() {
        return getRemark();
    }

    @Override
    public String value15() {
        return getBizTerm();
    }

    @Override
    public Byte value16() {
        return getIsUsed();
    }

    @Override
    public ULong value17() {
        return getOwnerTopLevelAbieId();
    }

    @Override
    public BbieScRecord value1(ULong value) {
        setBbieScId(value);
        return this;
    }

    @Override
    public BbieScRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public BbieScRecord value3(ULong value) {
        setBasedDtScManifestId(value);
        return this;
    }

    @Override
    public BbieScRecord value4(ULong value) {
        setBbieId(value);
        return this;
    }

    @Override
    public BbieScRecord value5(ULong value) {
        setDtScPriRestriId(value);
        return this;
    }

    @Override
    public BbieScRecord value6(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public BbieScRecord value7(ULong value) {
        setAgencyIdListId(value);
        return this;
    }

    @Override
    public BbieScRecord value8(Integer value) {
        setCardinalityMin(value);
        return this;
    }

    @Override
    public BbieScRecord value9(Integer value) {
        setCardinalityMax(value);
        return this;
    }

    @Override
    public BbieScRecord value10(String value) {
        setDefaultValue(value);
        return this;
    }

    @Override
    public BbieScRecord value11(String value) {
        setFixedValue(value);
        return this;
    }

    @Override
    public BbieScRecord value12(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public BbieScRecord value13(String value) {
        setExample(value);
        return this;
    }

    @Override
    public BbieScRecord value14(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public BbieScRecord value15(String value) {
        setBizTerm(value);
        return this;
    }

    @Override
    public BbieScRecord value16(Byte value) {
        setIsUsed(value);
        return this;
    }

    @Override
    public BbieScRecord value17(ULong value) {
        setOwnerTopLevelAbieId(value);
        return this;
    }

    @Override
    public BbieScRecord values(ULong value1, String value2, ULong value3, ULong value4, ULong value5, ULong value6, ULong value7, Integer value8, Integer value9, String value10, String value11, String value12, String value13, String value14, String value15, Byte value16, ULong value17) {
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
     * Create a detached BbieScRecord
     */
    public BbieScRecord() {
        super(BbieSc.BBIE_SC);
    }

    /**
     * Create a detached, initialised BbieScRecord
     */
    public BbieScRecord(ULong bbieScId, String guid, ULong basedDtScManifestId, ULong bbieId, ULong dtScPriRestriId, ULong codeListId, ULong agencyIdListId, Integer cardinalityMin, Integer cardinalityMax, String defaultValue, String fixedValue, String definition, String example, String remark, String bizTerm, Byte isUsed, ULong ownerTopLevelAbieId) {
        super(BbieSc.BBIE_SC);

        set(0, bbieScId);
        set(1, guid);
        set(2, basedDtScManifestId);
        set(3, bbieId);
        set(4, dtScPriRestriId);
        set(5, codeListId);
        set(6, agencyIdListId);
        set(7, cardinalityMin);
        set(8, cardinalityMax);
        set(9, defaultValue);
        set(10, fixedValue);
        set(11, definition);
        set(12, example);
        set(13, remark);
        set(14, bizTerm);
        set(15, isUsed);
        set(16, ownerTopLevelAbieId);
    }
}
