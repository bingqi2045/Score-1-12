/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.CdtScAwdPriRecord;


/**
 * This table capture the CDT primitives allowed for a particular SC of a 
 * CDT. It also stores the CDT primitives allowed for a SC of a BDT that extends 
 * its base (such SC is not defined in the CCTS data type catalog specification).
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CdtScAwdPri extends TableImpl<CdtScAwdPriRecord> {

    private static final long serialVersionUID = -255165059;

    /**
     * The reference instance of <code>oagi.cdt_sc_awd_pri</code>
     */
    public static final CdtScAwdPri CDT_SC_AWD_PRI = new CdtScAwdPri();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CdtScAwdPriRecord> getRecordType() {
        return CdtScAwdPriRecord.class;
    }

    /**
     * The column <code>oagi.cdt_sc_awd_pri.cdt_sc_awd_pri_id</code>. Internal, primary database key.
     */
    public final TableField<CdtScAwdPriRecord, ULong> CDT_SC_AWD_PRI_ID = createField("cdt_sc_awd_pri_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.cdt_sc_awd_pri.cdt_sc_id</code>. Foreign key pointing to the supplementary component (SC).
     */
    public final TableField<CdtScAwdPriRecord, ULong> CDT_SC_ID = createField("cdt_sc_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key pointing to the supplementary component (SC).");

    /**
     * The column <code>oagi.cdt_sc_awd_pri.cdt_pri_id</code>. A foreign key pointing to the CDT_Pri table. It represents a CDT primitive allowed for the suppliement component identified in the CDT_SC_ID column.
     */
    public final TableField<CdtScAwdPriRecord, ULong> CDT_PRI_ID = createField("cdt_pri_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key pointing to the CDT_Pri table. It represents a CDT primitive allowed for the suppliement component identified in the CDT_SC_ID column.");

    /**
     * The column <code>oagi.cdt_sc_awd_pri.is_default</code>. Indicating whether the primitive is the default primitive of the supplementary component.
     */
    public final TableField<CdtScAwdPriRecord, Byte> IS_DEFAULT = createField("is_default", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "Indicating whether the primitive is the default primitive of the supplementary component.");

    /**
     * Create a <code>oagi.cdt_sc_awd_pri</code> table reference
     */
    public CdtScAwdPri() {
        this(DSL.name("cdt_sc_awd_pri"), null);
    }

    /**
     * Create an aliased <code>oagi.cdt_sc_awd_pri</code> table reference
     */
    public CdtScAwdPri(String alias) {
        this(DSL.name(alias), CDT_SC_AWD_PRI);
    }

    /**
     * Create an aliased <code>oagi.cdt_sc_awd_pri</code> table reference
     */
    public CdtScAwdPri(Name alias) {
        this(alias, CDT_SC_AWD_PRI);
    }

    private CdtScAwdPri(Name alias, Table<CdtScAwdPriRecord> aliased) {
        this(alias, aliased, null);
    }

    private CdtScAwdPri(Name alias, Table<CdtScAwdPriRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table capture the CDT primitives allowed for a particular SC of a CDT. It also stores the CDT primitives allowed for a SC of a BDT that extends its base (such SC is not defined in the CCTS data type catalog specification)."));
    }

    public <O extends Record> CdtScAwdPri(Table<O> child, ForeignKey<O, CdtScAwdPriRecord> key) {
        super(child, key, CDT_SC_AWD_PRI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CDT_SC_AWD_PRI_CDT_SC_AWD_PRI_CDT_PRI_ID_FK, Indexes.CDT_SC_AWD_PRI_CDT_SC_AWD_PRI_CDT_SC_ID_FK, Indexes.CDT_SC_AWD_PRI_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<CdtScAwdPriRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CDT_SC_AWD_PRI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CdtScAwdPriRecord> getPrimaryKey() {
        return Keys.KEY_CDT_SC_AWD_PRI_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CdtScAwdPriRecord>> getKeys() {
        return Arrays.<UniqueKey<CdtScAwdPriRecord>>asList(Keys.KEY_CDT_SC_AWD_PRI_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CdtScAwdPriRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CdtScAwdPriRecord, ?>>asList(Keys.CDT_SC_AWD_PRI_CDT_SC_ID_FK, Keys.CDT_SC_AWD_PRI_CDT_PRI_ID_FK);
    }

    public DtSc dtSc() {
        return new DtSc(this, Keys.CDT_SC_AWD_PRI_CDT_SC_ID_FK);
    }

    public CdtPri cdtPri() {
        return new CdtPri(this, Keys.CDT_SC_AWD_PRI_CDT_PRI_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CdtScAwdPri as(String alias) {
        return new CdtScAwdPri(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CdtScAwdPri as(Name alias) {
        return new CdtScAwdPri(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtScAwdPri rename(String name) {
        return new CdtScAwdPri(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CdtScAwdPri rename(Name name) {
        return new CdtScAwdPri(name, null);
    }
}
