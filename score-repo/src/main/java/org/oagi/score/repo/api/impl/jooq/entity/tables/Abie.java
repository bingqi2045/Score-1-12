/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function15;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row15;
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
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AbieRecord;


/**
 * The ABIE table stores information about an ABIE, which is a contextualized
 * ACC. The context is represented by the BUSINESS_CTX_ID column that refers to
 * a business context. Each ABIE must have a business context and a based ACC.
 * 
 * It should be noted that, per design document, there is no corresponding ABIE
 * created for an ACC which will not show up in the instance document such as
 * ACCs of OAGIS_COMPONENT_TYPE "SEMANTIC_GROUP", "USER_EXTENSION_GROUP", etc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Abie extends TableImpl<AbieRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.abie</code>
     */
    public static final Abie ABIE = new Abie();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AbieRecord> getRecordType() {
        return AbieRecord.class;
    }

    /**
     * The column <code>oagi.abie.abie_id</code>. Primary, internal database
     * key.
     */
    public final TableField<AbieRecord, String> ABIE_ID = createField(DSL.name("abie_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.abie.guid</code>. A globally unique identifier
     * (GUID).
     */
    public final TableField<AbieRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.abie.based_acc_manifest_id</code>. A foreign key to
     * the ACC_MANIFEST table refering to the ACC, on which the business context
     * has been applied to derive this ABIE.
     */
    public final TableField<AbieRecord, ULong> BASED_ACC_MANIFEST_ID = createField(DSL.name("based_acc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key to the ACC_MANIFEST table refering to the ACC, on which the business context has been applied to derive this ABIE.");

    /**
     * The column <code>oagi.abie.path</code>.
     */
    public final TableField<AbieRecord, String> PATH = createField(DSL.name("path"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.abie.hash_path</code>. hash_path generated from the
     * path of the component graph using hash function, so that it is unique in
     * the graph.
     */
    public final TableField<AbieRecord, String> HASH_PATH = createField(DSL.name("hash_path"), SQLDataType.VARCHAR(64).nullable(false), this, "hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.");

    /**
     * The column <code>oagi.abie.biz_ctx_id</code>. (Deprecated) A foreign key
     * to the BIZ_CTX table. This column stores the business context assigned to
     * the ABIE.
     */
    public final TableField<AbieRecord, String> BIZ_CTX_ID = createField(DSL.name("biz_ctx_id"), SQLDataType.CHAR(36), this, "(Deprecated) A foreign key to the BIZ_CTX table. This column stores the business context assigned to the ABIE.");

    /**
     * The column <code>oagi.abie.definition</code>. Definition to override the
     * ACC's definition. If NULL, it means that the definition should be
     * inherited from the based CC.
     */
    public final TableField<AbieRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Definition to override the ACC's definition. If NULL, it means that the definition should be inherited from the based CC.");

    /**
     * The column <code>oagi.abie.created_by</code>. A foreign key referring to
     * the user who creates the ABIE. The creator of the ABIE is also its owner
     * by default. ABIEs created as children of another ABIE have the same
     * CREATED_BY as its parent.
     */
    public final TableField<AbieRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key referring to the user who creates the ABIE. The creator of the ABIE is also its owner by default. ABIEs created as children of another ABIE have the same CREATED_BY as its parent.");

    /**
     * The column <code>oagi.abie.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the ABIE record. This may be
     * the user who is in the same group as the creator.
     */
    public final TableField<AbieRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key referring to the last user who has updated the ABIE record. This may be the user who is in the same group as the creator.");

    /**
     * The column <code>oagi.abie.creation_timestamp</code>. Timestamp when the
     * ABIE record was first created. ABIEs created as children of another ABIE
     * have the same CREATION_TIMESTAMP.
     */
    public final TableField<AbieRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the ABIE record was first created. ABIEs created as children of another ABIE have the same CREATION_TIMESTAMP.");

    /**
     * The column <code>oagi.abie.last_update_timestamp</code>. The timestamp
     * when the ABIE was last updated.
     */
    public final TableField<AbieRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the ABIE was last updated.");

    /**
     * The column <code>oagi.abie.state</code>. 2 = EDITING, 4 = PUBLISHED. This
     * column is only used with a top-level ABIE, because that is the only entry
     * point for editing. The state value indicates the visibility of the
     * top-level ABIE to users other than the owner. In the user group
     * environment, a logic can apply that other users in the group can see the
     * top-level ABIE only when it is in the 'Published' state.
     */
    public final TableField<AbieRecord, Integer> STATE = createField(DSL.name("state"), SQLDataType.INTEGER, this, "2 = EDITING, 4 = PUBLISHED. This column is only used with a top-level ABIE, because that is the only entry point for editing. The state value indicates the visibility of the top-level ABIE to users other than the owner. In the user group environment, a logic can apply that other users in the group can see the top-level ABIE only when it is in the 'Published' state.");

    /**
     * The column <code>oagi.abie.remark</code>. This column allows the user to
     * specify very context-specific usage of the BIE. It is different from the
     * DEFINITION column in that the DEFINITION column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. For example, BOM BOD, as
     * an ACC, is a generic BOM structure. In a particular context, a BOM ABIE
     * can be a Super BOM. Explanation of the Super BOM concept should be
     * captured in the Definition of the ABIE. A remark about that ABIE may be
     * "Type of BOM should be recognized in the BOM/typeCode."
     */
    public final TableField<AbieRecord, String> REMARK = createField(DSL.name("remark"), SQLDataType.VARCHAR(225), this, "This column allows the user to specify very context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be \"Type of BOM should be recognized in the BOM/typeCode.\"");

    /**
     * The column <code>oagi.abie.biz_term</code>. To indicate what the BIE is
     * called in a particular business context. With this current design, only
     * one business term is allowed per business context.
     */
    public final TableField<AbieRecord, String> BIZ_TERM = createField(DSL.name("biz_term"), SQLDataType.VARCHAR(225), this, "To indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.");

    /**
     * The column <code>oagi.abie.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public final TableField<AbieRecord, String> OWNER_TOP_LEVEL_ASBIEP_ID = createField(DSL.name("owner_top_level_asbiep_id"), SQLDataType.CHAR(36).nullable(false), this, "This is a foreign key to the top-level ASBIEP.");

    private Abie(Name alias, Table<AbieRecord> aliased) {
        this(alias, aliased, null);
    }

    private Abie(Name alias, Table<AbieRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The ABIE table stores information about an ABIE, which is a contextualized ACC. The context is represented by the BUSINESS_CTX_ID column that refers to a business context. Each ABIE must have a business context and a based ACC.\n\nIt should be noted that, per design document, there is no corresponding ABIE created for an ACC which will not show up in the instance document such as ACCs of OAGIS_COMPONENT_TYPE \"SEMANTIC_GROUP\", \"USER_EXTENSION_GROUP\", etc."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.abie</code> table reference
     */
    public Abie(String alias) {
        this(DSL.name(alias), ABIE);
    }

    /**
     * Create an aliased <code>oagi.abie</code> table reference
     */
    public Abie(Name alias) {
        this(alias, ABIE);
    }

    /**
     * Create a <code>oagi.abie</code> table reference
     */
    public Abie() {
        this(DSL.name("abie"), null);
    }

    public <O extends Record> Abie(Table<O> child, ForeignKey<O, AbieRecord> key) {
        super(child, key, ABIE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.ABIE_ABIE_HASH_PATH_K, Indexes.ABIE_ABIE_PATH_K);
    }

    @Override
    public UniqueKey<AbieRecord> getPrimaryKey() {
        return Keys.KEY_ABIE_PRIMARY;
    }

    @Override
    public List<ForeignKey<AbieRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ABIE_BASED_ACC_MANIFEST_ID_FK, Keys.ABIE_BIZ_CTX_ID_FK, Keys.ABIE_CREATED_BY_FK, Keys.ABIE_LAST_UPDATED_BY_FK, Keys.ABIE_OWNER_TOP_LEVEL_ASBIEP_ID_FK);
    }

    private transient AccManifest _accManifest;
    private transient BizCtx _bizCtx;
    private transient AppUser _abieCreatedByFk;
    private transient AppUser _abieLastUpdatedByFk;
    private transient TopLevelAsbiep _topLevelAsbiep;

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table.
     */
    public AccManifest accManifest() {
        if (_accManifest == null)
            _accManifest = new AccManifest(this, Keys.ABIE_BASED_ACC_MANIFEST_ID_FK);

        return _accManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.biz_ctx</code> table.
     */
    public BizCtx bizCtx() {
        if (_bizCtx == null)
            _bizCtx = new BizCtx(this, Keys.ABIE_BIZ_CTX_ID_FK);

        return _bizCtx;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>abie_created_by_fk</code> key.
     */
    public AppUser abieCreatedByFk() {
        if (_abieCreatedByFk == null)
            _abieCreatedByFk = new AppUser(this, Keys.ABIE_CREATED_BY_FK);

        return _abieCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>abie_last_updated_by_fk</code> key.
     */
    public AppUser abieLastUpdatedByFk() {
        if (_abieLastUpdatedByFk == null)
            _abieLastUpdatedByFk = new AppUser(this, Keys.ABIE_LAST_UPDATED_BY_FK);

        return _abieLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.top_level_asbiep</code>
     * table.
     */
    public TopLevelAsbiep topLevelAsbiep() {
        if (_topLevelAsbiep == null)
            _topLevelAsbiep = new TopLevelAsbiep(this, Keys.ABIE_OWNER_TOP_LEVEL_ASBIEP_ID_FK);

        return _topLevelAsbiep;
    }

    @Override
    public Abie as(String alias) {
        return new Abie(DSL.name(alias), this);
    }

    @Override
    public Abie as(Name alias) {
        return new Abie(alias, this);
    }

    @Override
    public Abie as(Table<?> alias) {
        return new Abie(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Abie rename(String name) {
        return new Abie(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Abie rename(Name name) {
        return new Abie(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Abie rename(Table<?> name) {
        return new Abie(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<String, String, ULong, String, String, String, String, String, String, LocalDateTime, LocalDateTime, Integer, String, String, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function15<? super String, ? super String, ? super ULong, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super Integer, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function15<? super String, ? super String, ? super ULong, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super Integer, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
