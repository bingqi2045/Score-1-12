/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.BdtPriRestri;


/**
 * This table captures the allowed primitives for a BDT. The allowed primitives 
 * are captured by three columns the CDT_AWD_PRI_XPS_TYPE_MAP_ID, CODE_LIST_ID, 
 * and AGENCY_ID_LIST_ID. The first column specifies the primitive by the 
 * built-in type of an expression language such as the XML Schema built-in 
 * type. The second specifies the primitive, which is a code list, while the 
 * last one specifies the primitive which is an agency identification list. 
 * Only one column among the three can have a value in a particular record.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BdtPriRestriRecord extends UpdatableRecordImpl<BdtPriRestriRecord> implements Record6<ULong, ULong, ULong, ULong, ULong, Byte> {

    private static final long serialVersionUID = -555213428;

    /**
     * Setter for <code>oagi.bdt_pri_restri.bdt_pri_restri_id</code>. Primary, internal database key.
     */
    public void setBdtPriRestriId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.bdt_pri_restri_id</code>. Primary, internal database key.
     */
    public ULong getBdtPriRestriId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.bdt_id</code>. Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).
     */
    public void setBdtId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.bdt_id</code>. Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).
     */
    public ULong getBdtId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.cdt_awd_pri_xps_type_map_id</code>. This is a foreign key to the CDT_AWD_PRI_XPS_TYPE_MAP table.  It allows for a primitive restriction based on a built-in type of schema expressions.
     */
    public void setCdtAwdPriXpsTypeMapId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.cdt_awd_pri_xps_type_map_id</code>. This is a foreign key to the CDT_AWD_PRI_XPS_TYPE_MAP table.  It allows for a primitive restriction based on a built-in type of schema expressions.
     */
    public ULong getCdtAwdPriXpsTypeMapId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.code_list_id</code>. Foreign key to the CODE_LIST table.
     */
    public void setCodeListId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.code_list_id</code>. Foreign key to the CODE_LIST table.
     */
    public ULong getCodeListId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.
     */
    public void setAgencyIdListId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.agency_id_list_id</code>. This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.
     */
    public ULong getAgencyIdListId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.is_default</code>. This allows overriding the default primitive assigned in the CDT_AWD_PRI_XPS_TYPE_MAP table. It typically indicates the most generic primtive for the data type.
     */
    public void setIsDefault(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.is_default</code>. This allows overriding the default primitive assigned in the CDT_AWD_PRI_XPS_TYPE_MAP table. It typically indicates the most generic primtive for the data type.
     */
    public Byte getIsDefault() {
        return (Byte) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<ULong, ULong, ULong, ULong, ULong, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<ULong, ULong, ULong, ULong, ULong, Byte> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BdtPriRestri.BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID;
    }

    @Override
    public Field<ULong> field2() {
        return BdtPriRestri.BDT_PRI_RESTRI.BDT_ID;
    }

    @Override
    public Field<ULong> field3() {
        return BdtPriRestri.BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID;
    }

    @Override
    public Field<ULong> field4() {
        return BdtPriRestri.BDT_PRI_RESTRI.CODE_LIST_ID;
    }

    @Override
    public Field<ULong> field5() {
        return BdtPriRestri.BDT_PRI_RESTRI.AGENCY_ID_LIST_ID;
    }

    @Override
    public Field<Byte> field6() {
        return BdtPriRestri.BDT_PRI_RESTRI.IS_DEFAULT;
    }

    @Override
    public ULong component1() {
        return getBdtPriRestriId();
    }

    @Override
    public ULong component2() {
        return getBdtId();
    }

    @Override
    public ULong component3() {
        return getCdtAwdPriXpsTypeMapId();
    }

    @Override
    public ULong component4() {
        return getCodeListId();
    }

    @Override
    public ULong component5() {
        return getAgencyIdListId();
    }

    @Override
    public Byte component6() {
        return getIsDefault();
    }

    @Override
    public ULong value1() {
        return getBdtPriRestriId();
    }

    @Override
    public ULong value2() {
        return getBdtId();
    }

    @Override
    public ULong value3() {
        return getCdtAwdPriXpsTypeMapId();
    }

    @Override
    public ULong value4() {
        return getCodeListId();
    }

    @Override
    public ULong value5() {
        return getAgencyIdListId();
    }

    @Override
    public Byte value6() {
        return getIsDefault();
    }

    @Override
    public BdtPriRestriRecord value1(ULong value) {
        setBdtPriRestriId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value2(ULong value) {
        setBdtId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value3(ULong value) {
        setCdtAwdPriXpsTypeMapId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value4(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value5(ULong value) {
        setAgencyIdListId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value6(Byte value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, Byte value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BdtPriRestriRecord
     */
    public BdtPriRestriRecord() {
        super(BdtPriRestri.BDT_PRI_RESTRI);
    }

    /**
     * Create a detached, initialised BdtPriRestriRecord
     */
    public BdtPriRestriRecord(ULong bdtPriRestriId, ULong bdtId, ULong cdtAwdPriXpsTypeMapId, ULong codeListId, ULong agencyIdListId, Byte isDefault) {
        super(BdtPriRestri.BDT_PRI_RESTRI);

        set(0, bdtPriRestriId);
        set(1, bdtId);
        set(2, cdtAwdPriXpsTypeMapId);
        set(3, codeListId);
        set(4, agencyIdListId);
        set(5, isDefault);
    }
}
