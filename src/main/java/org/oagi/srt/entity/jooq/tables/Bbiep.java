/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.sql.Timestamp;
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
import org.oagi.srt.entity.jooq.tables.records.BbiepRecord;


/**
 * BBIEP represents the usage of basic property in a specific business context. 
 * It is a contextualization of a BCCP.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bbiep extends TableImpl<BbiepRecord> {

    private static final long serialVersionUID = 1804670433;

    /**
     * The reference instance of <code>oagi.bbiep</code>
     */
    public static final Bbiep BBIEP = new Bbiep();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BbiepRecord> getRecordType() {
        return BbiepRecord.class;
    }

    /**
     * The column <code>oagi.bbiep.bbiep_id</code>. A internal, primary database key of an BBIEP.
     */
    public final TableField<BbiepRecord, ULong> BBIEP_ID = createField("bbiep_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of an BBIEP.");

    /**
     * The column <code>oagi.bbiep.guid</code>. A globally unique identifier (GUID) of an BBIEP. GUID of an BBIEP is different from its based BCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<BbiepRecord, String> GUID = createField("guid", org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an BBIEP. GUID of an BBIEP is different from its based BCCP. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.bbiep.based_bccp_id</code>. A foreign key pointing to the BCCP record. It is the BCCP, which the BBIEP contextualizes.
     */
    public final TableField<BbiepRecord, ULong> BASED_BCCP_ID = createField("based_bccp_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key pointing to the BCCP record. It is the BCCP, which the BBIEP contextualizes.");

    /**
     * The column <code>oagi.bbiep.definition</code>. Definition to override the BCCP's Definition. If NULLl, it means that the definition should be inherited from the based CC.
     */
    public final TableField<BbiepRecord, String> DEFINITION = createField("definition", org.jooq.impl.SQLDataType.CLOB, this, "Definition to override the BCCP's Definition. If NULLl, it means that the definition should be inherited from the based CC.");

    /**
     * The column <code>oagi.bbiep.remark</code>. This column allows the user to specify very context-specific usage of the BIE. It is different from the Definition column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be "Type of BOM should be recognized in the BOM/typeCode.
     */
    public final TableField<BbiepRecord, String> REMARK = createField("remark", org.jooq.impl.SQLDataType.VARCHAR(225), this, "This column allows the user to specify very context-specific usage of the BIE. It is different from the Definition column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be \"Type of BOM should be recognized in the BOM/typeCode.");

    /**
     * The column <code>oagi.bbiep.biz_term</code>. Business term to indicate what the BIE is called in a particular business context such as in an industry.
     */
    public final TableField<BbiepRecord, String> BIZ_TERM = createField("biz_term", org.jooq.impl.SQLDataType.VARCHAR(225), this, "Business term to indicate what the BIE is called in a particular business context such as in an industry.");

    /**
     * The column <code>oagi.bbiep.created_by</code>. A foreign key referring to the user who creates the BBIEP. The creator of the BBIEP is also its owner by default. BBIEPs created as children of another ABIE have the same CREATED_BY',
     */
    public final TableField<BbiepRecord, ULong> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the user who creates the BBIEP. The creator of the BBIEP is also its owner by default. BBIEPs created as children of another ABIE have the same CREATED_BY',");

    /**
     * The column <code>oagi.bbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the BBIEP record. 
     */
    public final TableField<BbiepRecord, ULong> LAST_UPDATED_BY = createField("last_updated_by", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the last user who has updated the BBIEP record. ");

    /**
     * The column <code>oagi.bbiep.creation_timestamp</code>. Timestamp when the BBIEP record was first created. BBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP,
     */
    public final TableField<BbiepRecord, Timestamp> CREATION_TIMESTAMP = createField("creation_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the BBIEP record was first created. BBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP,");

    /**
     * The column <code>oagi.bbiep.last_update_timestamp</code>. The timestamp when the BBIEP was last updated.
     */
    public final TableField<BbiepRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField("last_update_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the BBIEP was last updated.");

    /**
     * The column <code>oagi.bbiep.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE which owns this BBIEP record.
     */
    public final TableField<BbiepRecord, ULong> OWNER_TOP_LEVEL_ABIE_ID = createField("owner_top_level_abie_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This is a foriegn key to the ABIE table. It specifies the top-level ABIE which owns this BBIEP record.");

    /**
     * Create a <code>oagi.bbiep</code> table reference
     */
    public Bbiep() {
        this(DSL.name("bbiep"), null);
    }

    /**
     * Create an aliased <code>oagi.bbiep</code> table reference
     */
    public Bbiep(String alias) {
        this(DSL.name(alias), BBIEP);
    }

    /**
     * Create an aliased <code>oagi.bbiep</code> table reference
     */
    public Bbiep(Name alias) {
        this(alias, BBIEP);
    }

    private Bbiep(Name alias, Table<BbiepRecord> aliased) {
        this(alias, aliased, null);
    }

    private Bbiep(Name alias, Table<BbiepRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("BBIEP represents the usage of basic property in a specific business context. It is a contextualization of a BCCP."));
    }

    public <O extends Record> Bbiep(Table<O> child, ForeignKey<O, BbiepRecord> key) {
        super(child, key, BBIEP);
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
        return Arrays.<Index>asList(Indexes.BBIEP_BBIEP_BASED_BCCP_ID_FK, Indexes.BBIEP_BBIEP_CREATED_BY_FK, Indexes.BBIEP_BBIEP_LAST_UPDATED_BY_FK, Indexes.BBIEP_BBIEP_OWNER_TOP_LEVEL_ABIE_ID_FK, Indexes.BBIEP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<BbiepRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BBIEP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BbiepRecord> getPrimaryKey() {
        return Keys.KEY_BBIEP_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BbiepRecord>> getKeys() {
        return Arrays.<UniqueKey<BbiepRecord>>asList(Keys.KEY_BBIEP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BbiepRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BbiepRecord, ?>>asList(Keys.BBIEP_BASED_BCCP_ID_FK, Keys.BBIEP_CREATED_BY_FK, Keys.BBIEP_LAST_UPDATED_BY_FK, Keys.BBIEP_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    public Bccp bccp() {
        return new Bccp(this, Keys.BBIEP_BASED_BCCP_ID_FK);
    }

    public AppUser bbiepCreatedByFk() {
        return new AppUser(this, Keys.BBIEP_CREATED_BY_FK);
    }

    public AppUser bbiepLastUpdatedByFk() {
        return new AppUser(this, Keys.BBIEP_LAST_UPDATED_BY_FK);
    }

    public TopLevelAbie topLevelAbie() {
        return new TopLevelAbie(this, Keys.BBIEP_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bbiep as(String alias) {
        return new Bbiep(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bbiep as(Name alias) {
        return new Bbiep(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Bbiep rename(String name) {
        return new Bbiep(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Bbiep rename(Name name) {
        return new Bbiep(name, null);
    }
}
