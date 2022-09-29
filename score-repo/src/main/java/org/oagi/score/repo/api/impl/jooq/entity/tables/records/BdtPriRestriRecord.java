/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BdtPriRestri;


/**
 * This table captures the allowed primitives for a BDT. The allowed primitives
 * are captured by three columns the CDT_AWD_PRI_XPS_TYPE_MAP_ID, CODE_LIST_ID,
 * and AGENCY_ID_LIST_ID. The first column specifies the primitive by the
 * built-in type of an expression language such as the XML Schema built-in type.
 * The second specifies the primitive, which is a code list, while the last one
 * specifies the primitive which is an agency identification list. Only one
 * column among the three can have a value in a particular record.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BdtPriRestriRecord extends UpdatableRecordImpl<BdtPriRestriRecord> implements Record6<String, String, String, String, String, Byte> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bdt_pri_restri.bdt_pri_restri_id</code>. Primary,
     * internal database key.
     */
    public void setBdtPriRestriId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.bdt_pri_restri_id</code>. Primary,
     * internal database key.
     */
    public String getBdtPriRestriId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.bdt_id</code>. Foreign key to the DT
     * table. It shall point to only DT that is a BDT (not a CDT).
     */
    public void setBdtId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.bdt_id</code>. Foreign key to the DT
     * table. It shall point to only DT that is a BDT (not a CDT).
     */
    public String getBdtId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.cdt_awd_pri_xps_type_map_id</code>.
     * This is a foreign key to the CDT_AWD_PRI_XPS_TYPE_MAP table.  It allows
     * for a primitive restriction based on a built-in type of schema
     * expressions.
     */
    public void setCdtAwdPriXpsTypeMapId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.cdt_awd_pri_xps_type_map_id</code>.
     * This is a foreign key to the CDT_AWD_PRI_XPS_TYPE_MAP table.  It allows
     * for a primitive restriction based on a built-in type of schema
     * expressions.
     */
    public String getCdtAwdPriXpsTypeMapId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.code_list_id</code>. Foreign key to
     * the CODE_LIST table.
     */
    public void setCodeListId(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.code_list_id</code>. Foreign key to
     * the CODE_LIST table.
     */
    public String getCodeListId() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.agency_id_list_id</code>. This is a
     * foreign key to the AGENCY_ID_LIST table. It is used in the case that the
     * BDT content can be restricted to an agency identification.
     */
    public void setAgencyIdListId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.agency_id_list_id</code>. This is a
     * foreign key to the AGENCY_ID_LIST table. It is used in the case that the
     * BDT content can be restricted to an agency identification.
     */
    public String getAgencyIdListId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.bdt_pri_restri.is_default</code>. This allows
     * overriding the default primitive assigned in the CDT_AWD_PRI_XPS_TYPE_MAP
     * table. It typically indicates the most generic primtive for the data
     * type.
     */
    public void setIsDefault(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bdt_pri_restri.is_default</code>. This allows
     * overriding the default primitive assigned in the CDT_AWD_PRI_XPS_TYPE_MAP
     * table. It typically indicates the most generic primtive for the data
     * type.
     */
    public Byte getIsDefault() {
        return (Byte) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String, String, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<String, String, String, String, String, Byte> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return BdtPriRestri.BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID;
    }

    @Override
    public Field<String> field2() {
        return BdtPriRestri.BDT_PRI_RESTRI.BDT_ID;
    }

    @Override
    public Field<String> field3() {
        return BdtPriRestri.BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID;
    }

    @Override
    public Field<String> field4() {
        return BdtPriRestri.BDT_PRI_RESTRI.CODE_LIST_ID;
    }

    @Override
    public Field<String> field5() {
        return BdtPriRestri.BDT_PRI_RESTRI.AGENCY_ID_LIST_ID;
    }

    @Override
    public Field<Byte> field6() {
        return BdtPriRestri.BDT_PRI_RESTRI.IS_DEFAULT;
    }

    @Override
    public String component1() {
        return getBdtPriRestriId();
    }

    @Override
    public String component2() {
        return getBdtId();
    }

    @Override
    public String component3() {
        return getCdtAwdPriXpsTypeMapId();
    }

    @Override
    public String component4() {
        return getCodeListId();
    }

    @Override
    public String component5() {
        return getAgencyIdListId();
    }

    @Override
    public Byte component6() {
        return getIsDefault();
    }

    @Override
    public String value1() {
        return getBdtPriRestriId();
    }

    @Override
    public String value2() {
        return getBdtId();
    }

    @Override
    public String value3() {
        return getCdtAwdPriXpsTypeMapId();
    }

    @Override
    public String value4() {
        return getCodeListId();
    }

    @Override
    public String value5() {
        return getAgencyIdListId();
    }

    @Override
    public Byte value6() {
        return getIsDefault();
    }

    @Override
    public BdtPriRestriRecord value1(String value) {
        setBdtPriRestriId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value2(String value) {
        setBdtId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value3(String value) {
        setCdtAwdPriXpsTypeMapId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value4(String value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value5(String value) {
        setAgencyIdListId(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord value6(Byte value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public BdtPriRestriRecord values(String value1, String value2, String value3, String value4, String value5, Byte value6) {
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
    public BdtPriRestriRecord(String bdtPriRestriId, String bdtId, String cdtAwdPriXpsTypeMapId, String codeListId, String agencyIdListId, Byte isDefault) {
        super(BdtPriRestri.BDT_PRI_RESTRI);

        setBdtPriRestriId(bdtPriRestriId);
        setBdtId(bdtId);
        setCdtAwdPriXpsTypeMapId(cdtAwdPriXpsTypeMapId);
        setCodeListId(codeListId);
        setAgencyIdListId(agencyIdListId);
        setIsDefault(isDefault);
    }
}
