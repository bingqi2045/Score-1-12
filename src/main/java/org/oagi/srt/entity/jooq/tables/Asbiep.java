/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row15;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.AsbiepRecord;


/**
 * ASBIEP represents a role in a usage of an ABIE. It is a contextualization 
 * of an ASCCP.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Asbiep extends TableImpl<AsbiepRecord> {

    private static final long serialVersionUID = -1828087560;

    /**
     * The reference instance of <code>oagi.asbiep</code>
     */
    public static final Asbiep ASBIEP = new Asbiep();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsbiepRecord> getRecordType() {
        return AsbiepRecord.class;
    }

    /**
     * The column <code>oagi.asbiep.asbiep_id</code>. A internal, primary database key of an ASBIEP.
     */
    public final TableField<AsbiepRecord, ULong> ASBIEP_ID = createField(DSL.name("asbiep_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of an ASBIEP.");

    /**
     * The column <code>oagi.asbiep.guid</code>. A globally unique identifier (GUID) of an ASBIEP. GUID of an ASBIEP is different from its based ASCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<AsbiepRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an ASBIEP. GUID of an ASBIEP is different from its based ASCCP. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.asbiep.based_asccp_manifest_id</code>. A foreign key pointing to the ASCCP_MANIFEST record. It is the ASCCP, on which the ASBIEP contextualizes.
     */
    public final TableField<AsbiepRecord, ULong> BASED_ASCCP_MANIFEST_ID = createField(DSL.name("based_asccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key pointing to the ASCCP_MANIFEST record. It is the ASCCP, on which the ASBIEP contextualizes.");

    /**
     * The column <code>oagi.asbiep.path</code>.
     */
    public final TableField<AsbiepRecord, String> PATH = createField(DSL.name("path"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.asbiep.hash_path</code>. hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.
     */
    public final TableField<AsbiepRecord, String> HASH_PATH = createField(DSL.name("hash_path"), org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.");

    /**
     * The column <code>oagi.asbiep.role_of_abie_id</code>. A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.
     */
    public final TableField<AsbiepRecord, ULong> ROLE_OF_ABIE_ID = createField(DSL.name("role_of_abie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.");

    /**
     * The column <code>oagi.asbiep.definition</code>. A definition to override the ASCCP's definition. If NULL, it means that the definition should be derived from the based ASCCP on the UI, expression generation, and any API.
     */
    public final TableField<AsbiepRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "A definition to override the ASCCP's definition. If NULL, it means that the definition should be derived from the based ASCCP on the UI, expression generation, and any API.");

    /**
     * The column <code>oagi.asbiep.remark</code>. This column allows the user to specify a context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ASBIEP can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ASBIEP. A remark about that ASBIEP may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public final TableField<AsbiepRecord, String> REMARK = createField(DSL.name("remark"), org.jooq.impl.SQLDataType.VARCHAR(225), this, "This column allows the user to specify a context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ASBIEP can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ASBIEP. A remark about that ASBIEP may be \"Type of BOM should be recognized in the BOM/typeCode.\"");

    /**
     * The column <code>oagi.asbiep.biz_term</code>. This column represents a business term to indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public final TableField<AsbiepRecord, String> BIZ_TERM = createField(DSL.name("biz_term"), org.jooq.impl.SQLDataType.VARCHAR(225), this, "This column represents a business term to indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.");

    /**
     * The column <code>oagi.asbiep.created_by</code>. A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.
     */
    public final TableField<AsbiepRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.");

    /**
     * The column <code>oagi.asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the ASBIEP record. 
     */
    public final TableField<AsbiepRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the last user who has updated the ASBIEP record. ");

    /**
     * The column <code>oagi.asbiep.creation_timestamp</code>. Timestamp when the ASBIEP record was first created. ASBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public final TableField<AsbiepRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "Timestamp when the ASBIEP record was first created. ASBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP.");

    /**
     * The column <code>oagi.asbiep.last_update_timestamp</code>. The timestamp when the ASBIEP was last updated.
     */
    public final TableField<AsbiepRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the ASBIEP was last updated.");

    /**
     * The column <code>oagi.asbiep.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE, which owns this ASBIEP record.
     */
    public final TableField<AsbiepRecord, ULong> OWNER_TOP_LEVEL_ABIE_ID = createField(DSL.name("owner_top_level_abie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This is a foriegn key to the ABIE table. It specifies the top-level ABIE, which owns this ASBIEP record.");

    /**
     * The column <code>oagi.asbiep.ref_top_level_abie_id</code>. A foreign key of the reference TOP_LEVEL_ABIE to re-use a profiled BIE.
     */
    public final TableField<AsbiepRecord, ULong> REF_TOP_LEVEL_ABIE_ID = createField(DSL.name("ref_top_level_abie_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A foreign key of the reference TOP_LEVEL_ABIE to re-use a profiled BIE.");

    /**
     * Create a <code>oagi.asbiep</code> table reference
     */
    public Asbiep() {
        this(DSL.name("asbiep"), null);
    }

    /**
     * Create an aliased <code>oagi.asbiep</code> table reference
     */
    public Asbiep(String alias) {
        this(DSL.name(alias), ASBIEP);
    }

    /**
     * Create an aliased <code>oagi.asbiep</code> table reference
     */
    public Asbiep(Name alias) {
        this(alias, ASBIEP);
    }

    private Asbiep(Name alias, Table<AsbiepRecord> aliased) {
        this(alias, aliased, null);
    }

    private Asbiep(Name alias, Table<AsbiepRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("ASBIEP represents a role in a usage of an ABIE. It is a contextualization of an ASCCP."), TableOptions.table());
    }

    public <O extends Record> Asbiep(Table<O> child, ForeignKey<O, AsbiepRecord> key) {
        super(child, key, ASBIEP);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ASBIEP_ASBIEP_HASH_PATH_K, Indexes.ASBIEP_ASBIEP_PATH_K);
    }

    @Override
    public Identity<AsbiepRecord, ULong> getIdentity() {
        return Keys.IDENTITY_ASBIEP;
    }

    @Override
    public UniqueKey<AsbiepRecord> getPrimaryKey() {
        return Keys.KEY_ASBIEP_PRIMARY;
    }

    @Override
    public List<UniqueKey<AsbiepRecord>> getKeys() {
        return Arrays.<UniqueKey<AsbiepRecord>>asList(Keys.KEY_ASBIEP_PRIMARY);
    }

    @Override
    public List<ForeignKey<AsbiepRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AsbiepRecord, ?>>asList(Keys.ASBIEP_BASED_ASCCP_MANIFEST_ID_FK, Keys.ASBIEP_ROLE_OF_ABIE_ID_FK, Keys.ASBIEP_CREATED_BY_FK, Keys.ASBIEP_LAST_UPDATED_BY_FK, Keys.ASBIEP_OWNER_TOP_LEVEL_ABIE_ID_FK, Keys.ASBIEP_REF_TOP_LEVEL_ABIE_ID_FK);
    }

    public AsccpManifest asccpManifest() {
        return new AsccpManifest(this, Keys.ASBIEP_BASED_ASCCP_MANIFEST_ID_FK);
    }

    public Abie abie() {
        return new Abie(this, Keys.ASBIEP_ROLE_OF_ABIE_ID_FK);
    }

    public AppUser asbiepCreatedByFk() {
        return new AppUser(this, Keys.ASBIEP_CREATED_BY_FK);
    }

    public AppUser asbiepLastUpdatedByFk() {
        return new AppUser(this, Keys.ASBIEP_LAST_UPDATED_BY_FK);
    }

    public TopLevelAbie asbiepOwnerTopLevelAbieIdFk() {
        return new TopLevelAbie(this, Keys.ASBIEP_OWNER_TOP_LEVEL_ABIE_ID_FK);
    }

    public TopLevelAbie asbiepRefTopLevelAbieIdFk() {
        return new TopLevelAbie(this, Keys.ASBIEP_REF_TOP_LEVEL_ABIE_ID_FK);
    }

    @Override
    public Asbiep as(String alias) {
        return new Asbiep(DSL.name(alias), this);
    }

    @Override
    public Asbiep as(Name alias) {
        return new Asbiep(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Asbiep rename(String name) {
        return new Asbiep(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Asbiep rename(Name name) {
        return new Asbiep(name, null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<ULong, String, ULong, String, String, ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> fieldsRow() {
        return (Row15) super.fieldsRow();
    }
}
