/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BbieScRecord;


/**
 * Because there is no single table that is a contextualized counterpart of the
 * DT table (which stores both CDT and BDT), The context specific constraints
 * associated with the DT are stored in the BBIE table, while this table stores
 * the constraints associated with the DT's SCs. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbieSc extends TableImpl<BbieScRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.bbie_sc</code>
     */
    public static final BbieSc BBIE_SC = new BbieSc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BbieScRecord> getRecordType() {
        return BbieScRecord.class;
    }

    /**
     * The column <code>oagi.bbie_sc.bbie_sc_id</code>. A internal, primary
     * database key of a BBIE_SC.
     */
    public final TableField<BbieScRecord, ULong> BBIE_SC_ID = createField(DSL.name("bbie_sc_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of a BBIE_SC.");

    /**
     * The column <code>oagi.bbie_sc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public final TableField<BbieScRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.bbie_sc.based_dt_sc_manifest_id</code>. Foreign key
     * to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the
     * BDT of the based BCC and BCCP.
     */
    public final TableField<BbieScRecord, ULong> BASED_DT_SC_MANIFEST_ID = createField(DSL.name("based_dt_sc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the BDT of the based BCC and BCCP.");

    /**
     * The column <code>oagi.bbie_sc.path</code>.
     */
    public final TableField<BbieScRecord, String> PATH = createField(DSL.name("path"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.bbie_sc.hash_path</code>. hash_path generated from
     * the path of the component graph using hash function, so that it is unique
     * in the graph.
     */
    public final TableField<BbieScRecord, String> HASH_PATH = createField(DSL.name("hash_path"), SQLDataType.VARCHAR(64).nullable(false), this, "hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.");

    /**
     * The column <code>oagi.bbie_sc.bbie_id</code>. The BBIE this BBIE_SC
     * applies to.
     */
    public final TableField<BbieScRecord, ULong> BBIE_ID = createField(DSL.name("bbie_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "The BBIE this BBIE_SC applies to.");

    /**
     * The column <code>oagi.bbie_sc.dt_sc_pri_restri_id</code>. This must be
     * one of the allowed primitive/code list as specified in the corresponding
     * SC of the based BCC of the BBIE (referred to by the BBIE_ID column).
     * 
     * It is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the
     * primitive assigned to the BBIE (or also can be viewed as assigned to the
     * BBIEP for this specific association). This is assigned by the user who
     * authors the BIE. The assignment would override the default from the CC
     * side.
     * 
     * This column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot
     * have a value at the same time.
     */
    public final TableField<BbieScRecord, ULong> DT_SC_PRI_RESTRI_ID = createField(DSL.name("dt_sc_pri_restri_id"), SQLDataType.BIGINTUNSIGNED, this, "This must be one of the allowed primitive/code list as specified in the corresponding SC of the based BCC of the BBIE (referred to by the BBIE_ID column).\n\nIt is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the primitive assigned to the BBIE (or also can be viewed as assigned to the BBIEP for this specific association). This is assigned by the user who authors the BIE. The assignment would override the default from the CC side.\n\nThis column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.");

    /**
     * The column <code>oagi.bbie_sc.code_list_id</code>. This is a foreign key
     * to the CODE_LIST table. If a code list is assigned to the BBIE SC (or
     * also can be viewed as assigned to the BBIEP SC for this association),
     * then this column stores the assigned code list. It should be noted that
     * one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID
     * column may also be a code list. So this column is typically used when the
     * user wants to assign another code list different from the one permissible
     * by the CC model.
     * 
     * This column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID
     * column cannot have a value at the same time.
     */
    public final TableField<BbieScRecord, ULong> CODE_LIST_ID = createField(DSL.name("code_list_id"), SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the CODE_LIST table. If a code list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned code list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be a code list. So this column is typically used when the user wants to assign another code list different from the one permissible by the CC model.\n\nThis column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID column cannot have a value at the same time.");

    /**
     * The column <code>oagi.bbie_sc.agency_id_list_id</code>. This is a foreign
     * key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the
     * BBIE SC (or also can be viewed as assigned to the BBIEP SC for this
     * association), then this column stores the assigned Agency ID list. It
     * should be noted that one of the possible primitives assignable to the
     * DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column
     * is typically used only when the user wants to assign another Agency ID
     * list different from the one permissible by the CC model.
     * 
     * This column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column
     * cannot have a value at the same time.
     */
    public final TableField<BbieScRecord, ULong> AGENCY_ID_LIST_ID = createField(DSL.name("agency_id_list_id"), SQLDataType.BIGINTUNSIGNED, this, "This is a foreign key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the BBIE SC (or also can be viewed as assigned to the BBIEP SC for this association), then this column stores the assigned Agency ID list. It should be noted that one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column is typically used only when the user wants to assign another Agency ID list different from the one permissible by the CC model.\n\nThis column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column cannot have a value at the same time.");

    /**
     * The column <code>oagi.bbie_sc.cardinality_min</code>. The minimum
     * occurrence constraint for the BBIE SC. A valid value is 0 or 1.
     */
    public final TableField<BbieScRecord, Integer> CARDINALITY_MIN = createField(DSL.name("cardinality_min"), SQLDataType.INTEGER.nullable(false), this, "The minimum occurrence constraint for the BBIE SC. A valid value is 0 or 1.");

    /**
     * The column <code>oagi.bbie_sc.cardinality_max</code>. Maximum occurence
     * constraint of the BBIE SC. A valid value is 0 or 1.
     */
    public final TableField<BbieScRecord, Integer> CARDINALITY_MAX = createField(DSL.name("cardinality_max"), SQLDataType.INTEGER.nullable(false), this, "Maximum occurence constraint of the BBIE SC. A valid value is 0 or 1.");

    /**
     * The column <code>oagi.bbie_sc.default_value</code>. This column specifies
     * the default value constraint. Default and fixed value constraints cannot
     * be used at the same time.
     */
    public final TableField<BbieScRecord, String> DEFAULT_VALUE = createField(DSL.name("default_value"), SQLDataType.CLOB, this, "This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bbie_sc.fixed_value</code>. This column captures
     * the fixed value constraint. Default and fixed value constraints cannot be
     * used at the same time.
     */
    public final TableField<BbieScRecord, String> FIXED_VALUE = createField(DSL.name("fixed_value"), SQLDataType.CLOB, this, "This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bbie_sc.definition</code>. Description to override
     * the BDT SC definition. If NULL, it means that the definition should be
     * inherited from the based BDT SC.
     */
    public final TableField<BbieScRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Description to override the BDT SC definition. If NULL, it means that the definition should be inherited from the based BDT SC.");

    /**
     * The column <code>oagi.bbie_sc.example</code>.
     */
    public final TableField<BbieScRecord, String> EXAMPLE = createField(DSL.name("example"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.bbie_sc.remark</code>. This column allows the user
     * to specify a very context-specific usage of the BBIE SC. It is different
     * from the Definition column in that the Definition column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. 
     */
    public final TableField<BbieScRecord, String> REMARK = createField(DSL.name("remark"), SQLDataType.VARCHAR(225), this, "This column allows the user to specify a very context-specific usage of the BBIE SC. It is different from the Definition column in that the Definition column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. ");

    /**
     * The column <code>oagi.bbie_sc.biz_term</code>. Business term to indicate
     * what the BBIE SC is called in a particular business context. With this
     * current design, only one business term is allowed per business context.
     */
    public final TableField<BbieScRecord, String> BIZ_TERM = createField(DSL.name("biz_term"), SQLDataType.VARCHAR(225), this, "Business term to indicate what the BBIE SC is called in a particular business context. With this current design, only one business term is allowed per business context.");

    /**
     * The column <code>oagi.bbie_sc.is_used</code>. Flag to indicate whether
     * the field/component is used in the content model. It indicates whether
     * the field/component should be generated.
     */
    public final TableField<BbieScRecord, Byte> IS_USED = createField(DSL.name("is_used"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Flag to indicate whether the field/component is used in the content model. It indicates whether the field/component should be generated.");

    /**
     * The column <code>oagi.bbie_sc.created_by</code>. A foreign key referring
     * to the user who creates the BBIE_SC. The creator of the BBIE_SC is also
     * its owner by default.
     */
    public final TableField<BbieScRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the user who creates the BBIE_SC. The creator of the BBIE_SC is also its owner by default.");

    /**
     * The column <code>oagi.bbie_sc.last_updated_by</code>. A foreign key
     * referring to the user who has last updated the BBIE_SC record.
     */
    public final TableField<BbieScRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key referring to the user who has last updated the BBIE_SC record.");

    /**
     * The column <code>oagi.bbie_sc.creation_timestamp</code>. Timestamp when
     * the BBIE_SC record was first created.
     */
    public final TableField<BbieScRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the BBIE_SC record was first created.");

    /**
     * The column <code>oagi.bbie_sc.last_update_timestamp</code>. The timestamp
     * when the BBIE_SC was last updated.
     */
    public final TableField<BbieScRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the BBIE_SC was last updated.");

    /**
     * The column <code>oagi.bbie_sc.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public final TableField<BbieScRecord, ULong> OWNER_TOP_LEVEL_ASBIEP_ID = createField(DSL.name("owner_top_level_asbiep_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This is a foreign key to the top-level ASBIEP.");

    private BbieSc(Name alias, Table<BbieScRecord> aliased) {
        this(alias, aliased, null);
    }

    private BbieSc(Name alias, Table<BbieScRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Because there is no single table that is a contextualized counterpart of the DT table (which stores both CDT and BDT), The context specific constraints associated with the DT are stored in the BBIE table, while this table stores the constraints associated with the DT's SCs. "), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.bbie_sc</code> table reference
     */
    public BbieSc(String alias) {
        this(DSL.name(alias), BBIE_SC);
    }

    /**
     * Create an aliased <code>oagi.bbie_sc</code> table reference
     */
    public BbieSc(Name alias) {
        this(alias, BBIE_SC);
    }

    /**
     * Create a <code>oagi.bbie_sc</code> table reference
     */
    public BbieSc() {
        this(DSL.name("bbie_sc"), null);
    }

    public <O extends Record> BbieSc(Table<O> child, ForeignKey<O, BbieScRecord> key) {
        super(child, key, BBIE_SC);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.BBIE_SC_BBIE_SC_HASH_PATH_K, Indexes.BBIE_SC_BBIE_SC_PATH_K);
    }

    @Override
    public Identity<BbieScRecord, ULong> getIdentity() {
        return (Identity<BbieScRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<BbieScRecord> getPrimaryKey() {
        return Keys.KEY_BBIE_SC_PRIMARY;
    }

    @Override
    public List<ForeignKey<BbieScRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BBIE_SC_BASED_DT_SC_MANIFEST_ID_FK, Keys.BBIE_SC_BBIE_ID_FK, Keys.BBIE_SC_DT_SC_PRI_RESTRI_ID_FK, Keys.BBIE_SC_CODE_LIST_ID_FK, Keys.BBIE_SC_AGENCY_ID_LIST_ID_FK, Keys.BBIE_SC_CREATED_BY_FK, Keys.BBIE_SC_LAST_UPDATED_BY_FK, Keys.BBIE_SC_OWNER_TOP_LEVEL_ASBIEP_ID_FK);
    }

    private transient DtScManifest _dtScManifest;
    private transient Bbie _bbie;
    private transient BdtScPriRestri _bdtScPriRestri;
    private transient CodeList _codeList;
    private transient AgencyIdList _agencyIdList;
    private transient AppUser _bbieScCreatedByFk;
    private transient AppUser _bbieScLastUpdatedByFk;
    private transient TopLevelAsbiep _topLevelAsbiep;

    /**
     * Get the implicit join path to the <code>oagi.dt_sc_manifest</code> table.
     */
    public DtScManifest dtScManifest() {
        if (_dtScManifest == null)
            _dtScManifest = new DtScManifest(this, Keys.BBIE_SC_BASED_DT_SC_MANIFEST_ID_FK);

        return _dtScManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.bbie</code> table.
     */
    public Bbie bbie() {
        if (_bbie == null)
            _bbie = new Bbie(this, Keys.BBIE_SC_BBIE_ID_FK);

        return _bbie;
    }

    /**
     * Get the implicit join path to the <code>oagi.bdt_sc_pri_restri</code>
     * table.
     */
    public BdtScPriRestri bdtScPriRestri() {
        if (_bdtScPriRestri == null)
            _bdtScPriRestri = new BdtScPriRestri(this, Keys.BBIE_SC_DT_SC_PRI_RESTRI_ID_FK);

        return _bdtScPriRestri;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list</code> table.
     */
    public CodeList codeList() {
        if (_codeList == null)
            _codeList = new CodeList(this, Keys.BBIE_SC_CODE_LIST_ID_FK);

        return _codeList;
    }

    /**
     * Get the implicit join path to the <code>oagi.agency_id_list</code> table.
     */
    public AgencyIdList agencyIdList() {
        if (_agencyIdList == null)
            _agencyIdList = new AgencyIdList(this, Keys.BBIE_SC_AGENCY_ID_LIST_ID_FK);

        return _agencyIdList;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>bbie_sc_created_by_fk</code> key.
     */
    public AppUser bbieScCreatedByFk() {
        if (_bbieScCreatedByFk == null)
            _bbieScCreatedByFk = new AppUser(this, Keys.BBIE_SC_CREATED_BY_FK);

        return _bbieScCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>bbie_sc_last_updated_by_fk</code> key.
     */
    public AppUser bbieScLastUpdatedByFk() {
        if (_bbieScLastUpdatedByFk == null)
            _bbieScLastUpdatedByFk = new AppUser(this, Keys.BBIE_SC_LAST_UPDATED_BY_FK);

        return _bbieScLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.top_level_asbiep</code>
     * table.
     */
    public TopLevelAsbiep topLevelAsbiep() {
        if (_topLevelAsbiep == null)
            _topLevelAsbiep = new TopLevelAsbiep(this, Keys.BBIE_SC_OWNER_TOP_LEVEL_ASBIEP_ID_FK);

        return _topLevelAsbiep;
    }

    @Override
    public BbieSc as(String alias) {
        return new BbieSc(DSL.name(alias), this);
    }

    @Override
    public BbieSc as(Name alias) {
        return new BbieSc(alias, this);
    }

    @Override
    public BbieSc as(Table<?> alias) {
        return new BbieSc(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public BbieSc rename(String name) {
        return new BbieSc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BbieSc rename(Name name) {
        return new BbieSc(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public BbieSc rename(Table<?> name) {
        return new BbieSc(name.getQualifiedName(), null);
    }
}
