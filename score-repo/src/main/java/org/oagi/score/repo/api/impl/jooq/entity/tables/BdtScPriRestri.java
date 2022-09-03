/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function6;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BdtScPriRestriRecord;


/**
 * This table is similar to the BDT_PRI_RESTRI table but it is for the BDT SC.
 * The allowed primitives are captured by three columns the
 * CDT_SC_AWD_PRI_XPS_TYPE_MAP, CODE_LIST_ID, and AGENCY_ID_LIST_ID. The first
 * column specifies the primitive by the built-in type of an expression language
 * such as the XML Schema built-in type. The second specifies the primitive,
 * which is a code list, while the last one specifies the primitive which is an
 * agency identification list. Only one column among the three can have a value
 * in a particular record.
 * 
 * It should be noted that the table does not store the fact about primitive
 * restriction hierarchical relationships. In other words, if a BDT SC is
 * derived from another BDT SC and the derivative BDT SC applies some primitive
 * restrictions, that relationship will not be explicitly stored. The derivative
 * BDT SC points directly to the CDT_AWD_PRI_XPS_TYPE_MAP key rather than the
 * BDT_SC_PRI_RESTRI key.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BdtScPriRestri extends TableImpl<BdtScPriRestriRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.bdt_sc_pri_restri</code>
     */
    public static final BdtScPriRestri BDT_SC_PRI_RESTRI = new BdtScPriRestri();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BdtScPriRestriRecord> getRecordType() {
        return BdtScPriRestriRecord.class;
    }

    /**
     * The column <code>oagi.bdt_sc_pri_restri.bdt_sc_pri_restri_id</code>.
     * Primary, internal database key.
     */
    public final TableField<BdtScPriRestriRecord, ULong> BDT_SC_PRI_RESTRI_ID = createField(DSL.name("bdt_sc_pri_restri_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.bdt_sc_pri_restri.bdt_sc_id</code>. Foreign key to
     * the DT table. It shall point to only DT that is a BDT (not a CDT).
     */
    public final TableField<BdtScPriRestriRecord, String> BDT_SC_ID = createField(DSL.name("bdt_sc_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the DT table. It shall point to only DT that is a BDT (not a CDT).");

    /**
     * The column
     * <code>oagi.bdt_sc_pri_restri.cdt_sc_awd_pri_xps_type_map_id</code>.
     * Foreign key to the CDT_SC_AWD_PRI_XPS_TYPE_MAP table.
     */
    public final TableField<BdtScPriRestriRecord, String> CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID = createField(DSL.name("cdt_sc_awd_pri_xps_type_map_id"), SQLDataType.CHAR(36), this, "Foreign key to the CDT_SC_AWD_PRI_XPS_TYPE_MAP table.");

    /**
     * The column <code>oagi.bdt_sc_pri_restri.code_list_id</code>. Foreign key
     * to the CODE_LIST table.
     */
    public final TableField<BdtScPriRestriRecord, String> CODE_LIST_ID = createField(DSL.name("code_list_id"), SQLDataType.CHAR(36), this, "Foreign key to the CODE_LIST table.");

    /**
     * The column <code>oagi.bdt_sc_pri_restri.agency_id_list_id</code>. This is
     * a foreign key to the AGENCY_ID_LIST table. It is used in the case that
     * the BDT content can be restricted to an agency identification.
     */
    public final TableField<BdtScPriRestriRecord, String> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), SQLDataType.CHAR(36), this, "This is a foreign key to the AGENCY_ID_LIST table. It is used in the case that the BDT content can be restricted to an agency identification.");

    /**
     * The column <code>oagi.bdt_sc_pri_restri.is_default</code>. This column
     * specifies the default primitive for a BDT. It is typically the most
     * generic primitive allowed for the BDT.
     */
    public final TableField<BdtScPriRestriRecord, Byte> IS_DEFAULT = createField(DSL.name("is_default"), SQLDataType.TINYINT.nullable(false), this, "This column specifies the default primitive for a BDT. It is typically the most generic primitive allowed for the BDT.");

    private BdtScPriRestri(Name alias, Table<BdtScPriRestriRecord> aliased) {
        this(alias, aliased, null);
    }

    private BdtScPriRestri(Name alias, Table<BdtScPriRestriRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table is similar to the BDT_PRI_RESTRI table but it is for the BDT SC. The allowed primitives are captured by three columns the CDT_SC_AWD_PRI_XPS_TYPE_MAP, CODE_LIST_ID, and AGENCY_ID_LIST_ID. The first column specifies the primitive by the built-in type of an expression language such as the XML Schema built-in type. The second specifies the primitive, which is a code list, while the last one specifies the primitive which is an agency identification list. Only one column among the three can have a value in a particular record.\n\nIt should be noted that the table does not store the fact about primitive restriction hierarchical relationships. In other words, if a BDT SC is derived from another BDT SC and the derivative BDT SC applies some primitive restrictions, that relationship will not be explicitly stored. The derivative BDT SC points directly to the CDT_AWD_PRI_XPS_TYPE_MAP key rather than the BDT_SC_PRI_RESTRI key."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.bdt_sc_pri_restri</code> table reference
     */
    public BdtScPriRestri(String alias) {
        this(DSL.name(alias), BDT_SC_PRI_RESTRI);
    }

    /**
     * Create an aliased <code>oagi.bdt_sc_pri_restri</code> table reference
     */
    public BdtScPriRestri(Name alias) {
        this(alias, BDT_SC_PRI_RESTRI);
    }

    /**
     * Create a <code>oagi.bdt_sc_pri_restri</code> table reference
     */
    public BdtScPriRestri() {
        this(DSL.name("bdt_sc_pri_restri"), null);
    }

    public <O extends Record> BdtScPriRestri(Table<O> child, ForeignKey<O, BdtScPriRestriRecord> key) {
        super(child, key, BDT_SC_PRI_RESTRI);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<BdtScPriRestriRecord, ULong> getIdentity() {
        return (Identity<BdtScPriRestriRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<BdtScPriRestriRecord> getPrimaryKey() {
        return Keys.KEY_BDT_SC_PRI_RESTRI_PRIMARY;
    }

    @Override
    public List<ForeignKey<BdtScPriRestriRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BDT_SC_PRI_RESTRI_BDT_SC_ID_FK, Keys.BDT_SC_PRI_RESTRI_CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID_FK, Keys.BDT_SC_PRI_RESTRI_CODE_LIST_ID_FK, Keys.BDT_SC_PRI_RESTRI_AGENCY_ID_LIST_ID_FK);
    }

    private transient DtSc _dtSc;
    private transient CdtScAwdPriXpsTypeMap _cdtScAwdPriXpsTypeMap;
    private transient CodeList _codeList;
    private transient AgencyIdList _agencyIdList;

    /**
     * Get the implicit join path to the <code>oagi.dt_sc</code> table.
     */
    public DtSc dtSc() {
        if (_dtSc == null)
            _dtSc = new DtSc(this, Keys.BDT_SC_PRI_RESTRI_BDT_SC_ID_FK);

        return _dtSc;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.cdt_sc_awd_pri_xps_type_map</code> table.
     */
    public CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap() {
        if (_cdtScAwdPriXpsTypeMap == null)
            _cdtScAwdPriXpsTypeMap = new CdtScAwdPriXpsTypeMap(this, Keys.BDT_SC_PRI_RESTRI_CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID_FK);

        return _cdtScAwdPriXpsTypeMap;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list</code> table.
     */
    public CodeList codeList() {
        if (_codeList == null)
            _codeList = new CodeList(this, Keys.BDT_SC_PRI_RESTRI_CODE_LIST_ID_FK);

        return _codeList;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table.
     */
    public AgencyIdList agencyIdList() {
        if (_agencyIdList == null)
            _agencyIdList = new AgencyIdList(this, Keys.BDT_SC_PRI_RESTRI_AGENCY_ID_LIST_ID_FK);

        return _agencyIdList;
    }

    @Override
    public BdtScPriRestri as(String alias) {
        return new BdtScPriRestri(DSL.name(alias), this);
    }

    @Override
    public BdtScPriRestri as(Name alias) {
        return new BdtScPriRestri(alias, this);
    }

    @Override
    public BdtScPriRestri as(Table<?> alias) {
        return new BdtScPriRestri(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BdtScPriRestri rename(String name) {
        return new BdtScPriRestri(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BdtScPriRestri rename(Name name) {
        return new BdtScPriRestri(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BdtScPriRestri rename(Table<?> name) {
        return new BdtScPriRestri(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<ULong, String, String, String, String, Byte> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function6<? super ULong, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function6<? super ULong, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
