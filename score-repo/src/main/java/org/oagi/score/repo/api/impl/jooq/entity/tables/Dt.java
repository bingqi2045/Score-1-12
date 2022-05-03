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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.DtRecord;


/**
 * The DT table stores both CDT and BDT. The two types of DTs are differentiated
 * by the TYPE column.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Dt extends TableImpl<DtRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.dt</code>
     */
    public static final Dt DT = new Dt();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtRecord> getRecordType() {
        return DtRecord.class;
    }

    /**
     * The column <code>oagi.dt.dt_id</code>. Internal, primary database key.
     */
    public final TableField<DtRecord, ULong> DT_ID = createField(DSL.name("dt_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.dt.guid</code>. A globally unique identifier
     * (GUID).
     */
    public final TableField<DtRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.dt.data_type_term</code>. This is the data type
     * term assigned to the DT. The allowed set of data type terms are defined
     * in the DTC specification. This column is derived from the Based_DT_ID
     * when the column is not blank. 
     */
    public final TableField<DtRecord, String> DATA_TYPE_TERM = createField(DSL.name("data_type_term"), SQLDataType.VARCHAR(45), this, "This is the data type term assigned to the DT. The allowed set of data type terms are defined in the DTC specification. This column is derived from the Based_DT_ID when the column is not blank. ");

    /**
     * The column <code>oagi.dt.qualifier</code>. This column shall be blank
     * when the DT_TYPE is CDT. When the DT_TYPE is BDT, this is optional. If
     * the column is not blank it is a qualified BDT. If blank then the row may
     * be a default BDT or an unqualified BDT. Default BDT is OAGIS concrete
     * implementation of the CDT, these are the DT with numbers in the name,
     * e.g., CodeType_1E7368 (DEN is 'Code_1E7368. Type'). Default BDTs are
     * almost like permutation of the CDT options into concrete data types.
     * Unqualified BDT is a BDT that OAGIS model schema generally used for its
     * canonical. A handful of default BDTs were selected; and each of them is
     * wrapped with another type definition that has a simpler name such as
     * CodeType and NormalizedString type - we call these "unqualified BDTs". 
     */
    public final TableField<DtRecord, String> QUALIFIER = createField(DSL.name("qualifier"), SQLDataType.VARCHAR(100), this, "This column shall be blank when the DT_TYPE is CDT. When the DT_TYPE is BDT, this is optional. If the column is not blank it is a qualified BDT. If blank then the row may be a default BDT or an unqualified BDT. Default BDT is OAGIS concrete implementation of the CDT, these are the DT with numbers in the name, e.g., CodeType_1E7368 (DEN is 'Code_1E7368. Type'). Default BDTs are almost like permutation of the CDT options into concrete data types. Unqualified BDT is a BDT that OAGIS model schema generally used for its canonical. A handful of default BDTs were selected; and each of them is wrapped with another type definition that has a simpler name such as CodeType and NormalizedString type - we call these \"unqualified BDTs\". ");

    /**
     * The column <code>oagi.dt.representation_term</code>.
     */
    public final TableField<DtRecord, String> REPRESENTATION_TERM = createField(DSL.name("representation_term"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>oagi.dt.six_digit_id</code>. The six number suffix comes
     * from the UN/CEFACT XML Schema NDR.
     */
    public final TableField<DtRecord, String> SIX_DIGIT_ID = createField(DSL.name("six_digit_id"), SQLDataType.VARCHAR(45), this, "The six number suffix comes from the UN/CEFACT XML Schema NDR.");

    /**
     * The column <code>oagi.dt.based_dt_id</code>. Foreign key pointing to the
     * DT table itself. This column must be blank when the DT_TYPE is CDT. This
     * column must not be blank when the DT_TYPE is BDT.
     */
    public final TableField<DtRecord, ULong> BASED_DT_ID = createField(DSL.name("based_dt_id"), SQLDataType.BIGINTUNSIGNED, this, "Foreign key pointing to the DT table itself. This column must be blank when the DT_TYPE is CDT. This column must not be blank when the DT_TYPE is BDT.");

    /**
     * The column <code>oagi.dt.den</code>. Dictionary Entry Name of the data
     * type. 
     */
    public final TableField<DtRecord, String> DEN = createField(DSL.name("den"), SQLDataType.VARCHAR(200).nullable(false), this, "Dictionary Entry Name of the data type. ");

    /**
     * The column <code>oagi.dt.definition</code>. Description of the data type.
     */
    public final TableField<DtRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Description of the data type.");

    /**
     * The column <code>oagi.dt.definition_source</code>. This is typically a
     * URL identifying the source of the DEFINITION column.
     */
    public final TableField<DtRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), SQLDataType.VARCHAR(200), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.dt.namespace_id</code>. Foreign key to the
     * NAMESPACE table. This is the namespace to which the entity belongs. This
     * namespace column is primarily used in the case the component is a user's
     * component because there is also a namespace assigned at the release
     * level.
     */
    public final TableField<DtRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.");

    /**
     * The column <code>oagi.dt.content_component_definition</code>. Description
     * of the content component of the data type.
     */
    public final TableField<DtRecord, String> CONTENT_COMPONENT_DEFINITION = createField(DSL.name("content_component_definition"), SQLDataType.CLOB, this, "Description of the content component of the data type.");

    /**
     * The column <code>oagi.dt.state</code>. Deleted, WIP, Draft, QA,
     * Candidate, Production, Release Draft, Published. This the revision life
     * cycle state of the DT.
     * 
     * State change can't be undone. But the history record can still keep the
     * records of when the state was changed.
     */
    public final TableField<DtRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(20), this, "Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the DT.\n\nState change can't be undone. But the history record can still keep the records of when the state was changed.");

    /**
     * The column <code>oagi.dt.commonly_used</code>. This is a flag to indicate
     * commonly used DT(s) by BCCPs.
     */
    public final TableField<DtRecord, Byte> COMMONLY_USED = createField(DSL.name("commonly_used"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This is a flag to indicate commonly used DT(s) by BCCPs.");

    /**
     * The column <code>oagi.dt.created_by</code>. Foreign key to the APP_USER
     * table. It indicates the user who created this DT.
     */
    public final TableField<DtRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this DT.");

    /**
     * The column <code>oagi.dt.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<DtRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.dt.owner_user_id</code>. Foreign key to the
     * APP_USER table. This is the user who owns the entity, is allowed to edit
     * the entity, and who can transfer the ownership to another user.\n\nThe
     * ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership. 
     */
    public final TableField<DtRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\\n\\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. ");

    /**
     * The column <code>oagi.dt.creation_timestamp</code>. Timestamp when the
     * revision of the DT was created. 
     * 
     * This never change for a revision.
     */
    public final TableField<DtRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the revision of the DT was created. \n\nThis never change for a revision.");

    /**
     * The column <code>oagi.dt.last_update_timestamp</code>. Timestamp when the
     * record was last updated.
     * 
     * The value of this column in the latest history record should be the same
     * as that of the current record. This column keeps the record of when the
     * revision has occurred.
     */
    public final TableField<DtRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.");

    /**
     * The column <code>oagi.dt.is_deprecated</code>. Indicates whether the CC
     * is deprecated and should not be reused (i.e., no new reference to this
     * record should be created).
     */
    public final TableField<DtRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).");

    /**
     * The column <code>oagi.dt.replacement_dt_id</code>. This refers to a
     * replacement if the record is deprecated.
     */
    public final TableField<DtRecord, ULong> REPLACEMENT_DT_ID = createField(DSL.name("replacement_dt_id"), SQLDataType.BIGINTUNSIGNED, this, "This refers to a replacement if the record is deprecated.");

    /**
     * The column <code>oagi.dt.prev_dt_id</code>. A self-foreign key to
     * indicate the previous history record.
     */
    public final TableField<DtRecord, ULong> PREV_DT_ID = createField(DSL.name("prev_dt_id"), SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.dt.next_dt_id</code>. A self-foreign key to
     * indicate the next history record.
     */
    public final TableField<DtRecord, ULong> NEXT_DT_ID = createField(DSL.name("next_dt_id"), SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the next history record.");

    private Dt(Name alias, Table<DtRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dt(Name alias, Table<DtRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The DT table stores both CDT and BDT. The two types of DTs are differentiated by the TYPE column."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.dt</code> table reference
     */
    public Dt(String alias) {
        this(DSL.name(alias), DT);
    }

    /**
     * Create an aliased <code>oagi.dt</code> table reference
     */
    public Dt(Name alias) {
        this(alias, DT);
    }

    /**
     * Create a <code>oagi.dt</code> table reference
     */
    public Dt() {
        this(DSL.name("dt"), null);
    }

    public <O extends Record> Dt(Table<O> child, ForeignKey<O, DtRecord> key) {
        super(child, key, DT);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.DT_DT_GUID_IDX, Indexes.DT_DT_LAST_UPDATE_TIMESTAMP_DESC_IDX);
    }

    @Override
    public Identity<DtRecord, ULong> getIdentity() {
        return (Identity<DtRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<DtRecord> getPrimaryKey() {
        return Keys.KEY_DT_PRIMARY;
    }

    @Override
    public List<ForeignKey<DtRecord, ?>> getReferences() {
        return Arrays.asList(Keys.DT_BASED_DT_ID_FK, Keys.DT_NAMESPACE_ID_FK, Keys.DT_CREATED_BY_FK, Keys.DT_LAST_UPDATED_BY_FK, Keys.DT_OWNER_USER_ID_FK, Keys.DT_REPLACEMENT_DT_ID_FK, Keys.DT_PREV_DT_ID_FK, Keys.DT_NEXT_DT_ID_FK);
    }

    private transient Dt _dtBasedDtIdFk;
    private transient Namespace _namespace;
    private transient AppUser _dtCreatedByFk;
    private transient AppUser _dtLastUpdatedByFk;
    private transient AppUser _dtOwnerUserIdFk;
    private transient Dt _dtReplacementDtIdFk;
    private transient Dt _dtPrevDtIdFk;
    private transient Dt _dtNextDtIdFk;

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table, via the
     * <code>dt_based_dt_id_fk</code> key.
     */
    public Dt dtBasedDtIdFk() {
        if (_dtBasedDtIdFk == null)
            _dtBasedDtIdFk = new Dt(this, Keys.DT_BASED_DT_ID_FK);

        return _dtBasedDtIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.namespace</code> table.
     */
    public Namespace namespace() {
        if (_namespace == null)
            _namespace = new Namespace(this, Keys.DT_NAMESPACE_ID_FK);

        return _namespace;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>dt_created_by_fk</code> key.
     */
    public AppUser dtCreatedByFk() {
        if (_dtCreatedByFk == null)
            _dtCreatedByFk = new AppUser(this, Keys.DT_CREATED_BY_FK);

        return _dtCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>dt_last_updated_by_fk</code> key.
     */
    public AppUser dtLastUpdatedByFk() {
        if (_dtLastUpdatedByFk == null)
            _dtLastUpdatedByFk = new AppUser(this, Keys.DT_LAST_UPDATED_BY_FK);

        return _dtLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>dt_owner_user_id_fk</code> key.
     */
    public AppUser dtOwnerUserIdFk() {
        if (_dtOwnerUserIdFk == null)
            _dtOwnerUserIdFk = new AppUser(this, Keys.DT_OWNER_USER_ID_FK);

        return _dtOwnerUserIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table, via the
     * <code>dt_replacement_dt_id_fk</code> key.
     */
    public Dt dtReplacementDtIdFk() {
        if (_dtReplacementDtIdFk == null)
            _dtReplacementDtIdFk = new Dt(this, Keys.DT_REPLACEMENT_DT_ID_FK);

        return _dtReplacementDtIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table, via the
     * <code>dt_prev_dt_id_fk</code> key.
     */
    public Dt dtPrevDtIdFk() {
        if (_dtPrevDtIdFk == null)
            _dtPrevDtIdFk = new Dt(this, Keys.DT_PREV_DT_ID_FK);

        return _dtPrevDtIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table, via the
     * <code>dt_next_dt_id_fk</code> key.
     */
    public Dt dtNextDtIdFk() {
        if (_dtNextDtIdFk == null)
            _dtNextDtIdFk = new Dt(this, Keys.DT_NEXT_DT_ID_FK);

        return _dtNextDtIdFk;
    }

    @Override
    public Dt as(String alias) {
        return new Dt(DSL.name(alias), this);
    }

    @Override
    public Dt as(Name alias) {
        return new Dt(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dt rename(String name) {
        return new Dt(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dt rename(Name name) {
        return new Dt(name, null);
    }
}
