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
import org.oagi.srt.entity.jooq.tables.BdtScPriRestri;


/**
 * This table is similar to the BDT_PRI_RESTRI table but it is for the BDT 
 * SC. The allowed primitives are captured by three columns the CDT_SC_AWD_PRI_XPS_TYPE_MAP, 
 * CODE_LIST_ID, and AGENCY_ID_LIST_ID. The first column specifies the primitive 
 * by the built-in type of an expression language such as the XML Schema built-in 
 * type. The second specifies the primitive, which is a code list, while the 
 * last one specifies the primitive which is an agency identification list. 
 * Only one column among the three can have a value in a particular record.
 * 
 * It should be noted that the table does not store the fact about primitive 
 * restriction hierarchical relationships. In other words, if a BDT SC is 
 * derived from another BDT SC and the derivative BDT SC applies some primitive 
 * restrictions, that relationship will not be explicitly stored. The derivative 
 * BDT SC points directly to the CDT_AWD_PRI_XPS_TYPE_MAP key rather than 
 * the BDT_SC_PRI_RESTRI key.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BdtScPriRestriRecord extends UpdatableRecordImpl<BdtScPriRestriRecord> implements Record6<ULong, ULong, ULong, ULong, ULong, Byte> {

    private static final long serialVersionUID = -839467456;

    /**
     * Setter for <code>oagi.bdt_sc_pri_restri.bdt_sc_pri_restri_id</code>. Primary, internal database key.
     */
    public void setBdtScPriRestriId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bdt_sc_pri_restri.bdt_sc_pri_restri_id</code>. Primary, internal database key.
     */
    public ULong getBdtScPriRestriId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bdt_sc_pri_restri.bdt_sc_id</code>. Foreign key to the DT_SC table. This column should only refers to a DT_SC that belongs to a BDT (not CDT).
     */
    public void setBdtScId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bdt_sc_pri_restri.bdt_sc_id</code>. Foreign key to the DT_SC table. This column should only refers to a DT_SC that belongs to a BDT (not CDT).
     */
    public ULong getBdtScId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.bdt_sc_pri_restri.cdt_sc_awd_pri_xps_type_map_id</code>. This column is a forieng key to the CDT_SC_AWD_PRI_XPS_TYPE_MAP table. It allows for a primitive restriction based on a built-in type of schema expressions.
     */
    public void setCdtScAwdPriXpsTypeMapId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bdt_sc_pri_restri.cdt_sc_awd_pri_xps_type_map_id</code>. This column is a forieng key to the CDT_SC_AWD_PRI_XPS_TYPE_MAP table. It allows for a primitive restriction based on a built-in type of schema expressions.
     */
    public ULong getCdtScAwdPriXpsTypeMapId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bdt_sc_pri_restri.code_list_id</code>. Foreign key to identify a code list. It allows for a primitive restriction based on a code list.
     */
    public void setCodeListId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bdt_sc_pri_restri.code_list_id</code>. Foreign key to identify a code list. It allows for a primitive restriction based on a code list.
     */
    public ULong getCodeListId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.bdt_sc_pri_restri.agency_id_list_id</code>. Foreign key to identify an agency identification list. It allows for a primitive restriction based on such list of values.
     */
    public void setAgencyIdListId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bdt_sc_pri_restri.agency_id_list_id</code>. Foreign key to identify an agency identification list. It allows for a primitive restriction based on such list of values.
     */
    public ULong getAgencyIdListId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bdt_sc_pri_restri.is_default</code>. This column specifies the default primitive for a BDT. It is typically the most generic primitive allowed for the BDT.
     */
    public void setIsDefault(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bdt_sc_pri_restri.is_default</code>. This column specifies the default primitive for a BDT. It is typically the most generic primitive allowed for the BDT.
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
        return BdtScPriRestri.BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID;
    }

    @Override
    public Field<ULong> field2() {
        return BdtScPriRestri.BDT_SC_PRI_RESTRI.BDT_SC_ID;
    }

    @Override
    public Field<ULong> field3() {
        return BdtScPriRestri.BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID;
    }

    @Override
    public Field<ULong> field4() {
        return BdtScPriRestri.BDT_SC_PRI_RESTRI.CODE_LIST_ID;
    }

    @Override
    public Field<ULong> field5() {
        return BdtScPriRestri.BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID;
    }

    @Override
    public Field<Byte> field6() {
        return BdtScPriRestri.BDT_SC_PRI_RESTRI.IS_DEFAULT;
    }

    @Override
    public ULong component1() {
        return getBdtScPriRestriId();
    }

    @Override
    public ULong component2() {
        return getBdtScId();
    }

    @Override
    public ULong component3() {
        return getCdtScAwdPriXpsTypeMapId();
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
        return getBdtScPriRestriId();
    }

    @Override
    public ULong value2() {
        return getBdtScId();
    }

    @Override
    public ULong value3() {
        return getCdtScAwdPriXpsTypeMapId();
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
    public BdtScPriRestriRecord value1(ULong value) {
        setBdtScPriRestriId(value);
        return this;
    }

    @Override
    public BdtScPriRestriRecord value2(ULong value) {
        setBdtScId(value);
        return this;
    }

    @Override
    public BdtScPriRestriRecord value3(ULong value) {
        setCdtScAwdPriXpsTypeMapId(value);
        return this;
    }

    @Override
    public BdtScPriRestriRecord value4(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public BdtScPriRestriRecord value5(ULong value) {
        setAgencyIdListId(value);
        return this;
    }

    @Override
    public BdtScPriRestriRecord value6(Byte value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public BdtScPriRestriRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, Byte value6) {
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
     * Create a detached BdtScPriRestriRecord
     */
    public BdtScPriRestriRecord() {
        super(BdtScPriRestri.BDT_SC_PRI_RESTRI);
    }

    /**
     * Create a detached, initialised BdtScPriRestriRecord
     */
    public BdtScPriRestriRecord(ULong bdtScPriRestriId, ULong bdtScId, ULong cdtScAwdPriXpsTypeMapId, ULong codeListId, ULong agencyIdListId, Byte isDefault) {
        super(BdtScPriRestri.BDT_SC_PRI_RESTRI);

        set(0, bdtScPriRestriId);
        set(1, bdtScId);
        set(2, cdtScAwdPriXpsTypeMapId);
        set(3, codeListId);
        set(4, agencyIdListId);
        set(5, isDefault);
    }
}
