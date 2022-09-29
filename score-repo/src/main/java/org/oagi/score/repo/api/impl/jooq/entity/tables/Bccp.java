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
import org.jooq.Function22;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row22;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccpRecord;


/**
 * An BCCP specifies a property concept and data type associated with it. A BCCP
 * can be then added as a property of an ACC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bccp extends TableImpl<BccpRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.bccp</code>
     */
    public static final Bccp BCCP = new Bccp();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BccpRecord> getRecordType() {
        return BccpRecord.class;
    }

    /**
     * The column <code>oagi.bccp.bccp_id</code>. Primary, internal database
     * key.
     */
    public final TableField<BccpRecord, String> BCCP_ID = createField(DSL.name("bccp_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.bccp.guid</code>. A globally unique identifier
     * (GUID).
     */
    public final TableField<BccpRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.bccp.property_term</code>. The property concept
     * that the BCCP models.
     */
    public final TableField<BccpRecord, String> PROPERTY_TERM = createField(DSL.name("property_term"), SQLDataType.VARCHAR(100).nullable(false), this, "The property concept that the BCCP models.");

    /**
     * The column <code>oagi.bccp.representation_term</code>. The representation
     * term convey the format of the data the BCCP can take. The value is
     * derived from the DT.DATA_TYPE_TERM of the associated BDT as referred to
     * by the BDT_ID column.
     */
    public final TableField<BccpRecord, String> REPRESENTATION_TERM = createField(DSL.name("representation_term"), SQLDataType.VARCHAR(20).nullable(false), this, "The representation term convey the format of the data the BCCP can take. The value is derived from the DT.DATA_TYPE_TERM of the associated BDT as referred to by the BDT_ID column.");

    /**
     * The column <code>oagi.bccp.bdt_id</code>. Foreign key pointing to the DT
     * table indicating the data typye or data format of the BCCP. Only DT_ID
     * which DT_Type is BDT can be used.
     */
    public final TableField<BccpRecord, String> BDT_ID = createField(DSL.name("bdt_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.");

    /**
     * The column <code>oagi.bccp.den</code>. The dictionary entry name of the
     * BCCP. It is derived by PROPERTY_TERM + ". " + REPRESENTATION_TERM.
     */
    public final TableField<BccpRecord, String> DEN = createField(DSL.name("den"), SQLDataType.VARCHAR(200).nullable(false), this, "The dictionary entry name of the BCCP. It is derived by PROPERTY_TERM + \". \" + REPRESENTATION_TERM.");

    /**
     * The column <code>oagi.bccp.definition</code>. Description of the BCCP.
     */
    public final TableField<BccpRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Description of the BCCP.");

    /**
     * The column <code>oagi.bccp.definition_source</code>. This is typically a
     * URL identifying the source of the DEFINITION column.
     */
    public final TableField<BccpRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.bccp.namespace_id</code>. Foreign key to the
     * NAMESPACE table. This is the namespace to which the entity belongs. This
     * namespace column is primarily used in the case the component is a user's
     * component because there is also a namespace assigned at the release
     * level.
     */
    public final TableField<BccpRecord, String> NAMESPACE_ID = createField(DSL.name("namespace_id"), SQLDataType.CHAR(36), this, "Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.");

    /**
     * The column <code>oagi.bccp.is_deprecated</code>. Indicates whether the CC
     * is deprecated and should not be reused (i.e., no new reference to this
     * record should be created).
     */
    public final TableField<BccpRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).");

    /**
     * The column <code>oagi.bccp.replacement_bccp_id</code>. This refers to a
     * replacement if the record is deprecated.
     */
    public final TableField<BccpRecord, String> REPLACEMENT_BCCP_ID = createField(DSL.name("replacement_bccp_id"), SQLDataType.CHAR(36), this, "This refers to a replacement if the record is deprecated.");

    /**
     * The column <code>oagi.bccp.created_by</code>. Foreign key to the APP_USER
     * table referring to the user who creates the entity. 
     * 
     * This column never change between the history and the current record for a
     * given revision. The history record should have the same value as that of
     * its current record.
     */
    public final TableField<BccpRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table referring to the user who creates the entity. \n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.");

    /**
     * The column <code>oagi.bccp.owner_user_id</code>. Foreign key to the
     * APP_USER table. This is the user who owns the entity, is allowed to edit
     * the entity, and who can transfer the ownership to another user.
     * 
     * The ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership.
     */
    public final TableField<BccpRecord, String> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.bccp.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who has updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<BccpRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.bccp.creation_timestamp</code>. Timestamp when the
     * revision of the BCCP was created. 
     * 
     * This never change for a revision.
     */
    public final TableField<BccpRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "Timestamp when the revision of the BCCP was created. \n\nThis never change for a revision.");

    /**
     * The column <code>oagi.bccp.last_update_timestamp</code>. The timestamp
     * when the record was last updated.
     * 
     * The value of this column in the latest history record should be the same
     * as that of the current record. This column keeps the record of when the
     * revision has occurred.
     */
    public final TableField<BccpRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.");

    /**
     * The column <code>oagi.bccp.state</code>. Deleted, WIP, Draft, QA,
     * Candidate, Production, Release Draft, Published. This the revision life
     * cycle state of the BCCP.
     * 
     * State change can't be undone. But the history record can still keep the
     * records of when the state was changed.
     */
    public final TableField<BccpRecord, String> STATE = createField(DSL.name("state"), SQLDataType.VARCHAR(20), this, "Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the BCCP.\n\nState change can't be undone. But the history record can still keep the records of when the state was changed.");

    /**
     * The column <code>oagi.bccp.is_nillable</code>. This is corresponding to
     * the XML Schema nillable flag. Although the nillable may not apply to
     * certain cases of the BCCP (e.g., when it is only used as XSD attribute),
     * the value is default to false for simplification. 
     */
    public final TableField<BccpRecord, Byte> IS_NILLABLE = createField(DSL.name("is_nillable"), SQLDataType.TINYINT.nullable(false), this, "This is corresponding to the XML Schema nillable flag. Although the nillable may not apply to certain cases of the BCCP (e.g., when it is only used as XSD attribute), the value is default to false for simplification. ");

    /**
     * The column <code>oagi.bccp.default_value</code>. This column specifies
     * the default value constraint. Default and fixed value constraints cannot
     * be used at the same time.
     */
    public final TableField<BccpRecord, String> DEFAULT_VALUE = createField(DSL.name("default_value"), SQLDataType.CLOB, this, "This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bccp.fixed_value</code>. This column captures the
     * fixed value constraint. Default and fixed value constraints cannot be
     * used at the same time.
     */
    public final TableField<BccpRecord, String> FIXED_VALUE = createField(DSL.name("fixed_value"), SQLDataType.CLOB, this, "This column captures the fixed value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * The column <code>oagi.bccp.prev_bccp_id</code>. A self-foreign key to
     * indicate the previous history record.
     */
    public final TableField<BccpRecord, String> PREV_BCCP_ID = createField(DSL.name("prev_bccp_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.bccp.next_bccp_id</code>. A self-foreign key to
     * indicate the next history record.
     */
    public final TableField<BccpRecord, String> NEXT_BCCP_ID = createField(DSL.name("next_bccp_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the next history record.");

    private Bccp(Name alias, Table<BccpRecord> aliased) {
        this(alias, aliased, null);
    }

    private Bccp(Name alias, Table<BccpRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("An BCCP specifies a property concept and data type associated with it. A BCCP can be then added as a property of an ACC."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.bccp</code> table reference
     */
    public Bccp(String alias) {
        this(DSL.name(alias), BCCP);
    }

    /**
     * Create an aliased <code>oagi.bccp</code> table reference
     */
    public Bccp(Name alias) {
        this(alias, BCCP);
    }

    /**
     * Create a <code>oagi.bccp</code> table reference
     */
    public Bccp() {
        this(DSL.name("bccp"), null);
    }

    public <O extends Record> Bccp(Table<O> child, ForeignKey<O, BccpRecord> key) {
        super(child, key, BCCP);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.BCCP_BCCP_GUID_IDX, Indexes.BCCP_BCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX);
    }

    @Override
    public UniqueKey<BccpRecord> getPrimaryKey() {
        return Keys.KEY_BCCP_PRIMARY;
    }

    @Override
    public List<ForeignKey<BccpRecord, ?>> getReferences() {
        return Arrays.asList(Keys.BCCP_BDT_ID_FK, Keys.BCCP_NAMESPACE_ID_FK, Keys.BCCP_REPLACEMENT_BCCP_ID_FK, Keys.BCCP_CREATED_BY_FK, Keys.BCCP_OWNER_USER_ID_FK, Keys.BCCP_LAST_UPDATED_BY_FK, Keys.BCCP_PREV_BCCP_ID_FK, Keys.BCCP_NEXT_BCCP_ID_FK);
    }

    private transient Dt _dt;
    private transient Namespace _namespace;
    private transient Bccp _bccpReplacementBccpIdFk;
    private transient AppUser _bccpCreatedByFk;
    private transient AppUser _bccpOwnerUserIdFk;
    private transient AppUser _bccpLastUpdatedByFk;
    private transient Bccp _bccpPrevBccpIdFk;
    private transient Bccp _bccpNextBccpIdFk;

    /**
     * Get the implicit join path to the <code>oagi.dt</code> table.
     */
    public Dt dt() {
        if (_dt == null)
            _dt = new Dt(this, Keys.BCCP_BDT_ID_FK);

        return _dt;
    }

    /**
     * Get the implicit join path to the <code>oagi.namespace</code> table.
     */
    public Namespace namespace() {
        if (_namespace == null)
            _namespace = new Namespace(this, Keys.BCCP_NAMESPACE_ID_FK);

        return _namespace;
    }

    /**
     * Get the implicit join path to the <code>oagi.bccp</code> table, via the
     * <code>bccp_replacement_bccp_id_fk</code> key.
     */
    public Bccp bccpReplacementBccpIdFk() {
        if (_bccpReplacementBccpIdFk == null)
            _bccpReplacementBccpIdFk = new Bccp(this, Keys.BCCP_REPLACEMENT_BCCP_ID_FK);

        return _bccpReplacementBccpIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>bccp_created_by_fk</code> key.
     */
    public AppUser bccpCreatedByFk() {
        if (_bccpCreatedByFk == null)
            _bccpCreatedByFk = new AppUser(this, Keys.BCCP_CREATED_BY_FK);

        return _bccpCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>bccp_owner_user_id_fk</code> key.
     */
    public AppUser bccpOwnerUserIdFk() {
        if (_bccpOwnerUserIdFk == null)
            _bccpOwnerUserIdFk = new AppUser(this, Keys.BCCP_OWNER_USER_ID_FK);

        return _bccpOwnerUserIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>bccp_last_updated_by_fk</code> key.
     */
    public AppUser bccpLastUpdatedByFk() {
        if (_bccpLastUpdatedByFk == null)
            _bccpLastUpdatedByFk = new AppUser(this, Keys.BCCP_LAST_UPDATED_BY_FK);

        return _bccpLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.bccp</code> table, via the
     * <code>bccp_prev_bccp_id_fk</code> key.
     */
    public Bccp bccpPrevBccpIdFk() {
        if (_bccpPrevBccpIdFk == null)
            _bccpPrevBccpIdFk = new Bccp(this, Keys.BCCP_PREV_BCCP_ID_FK);

        return _bccpPrevBccpIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.bccp</code> table, via the
     * <code>bccp_next_bccp_id_fk</code> key.
     */
    public Bccp bccpNextBccpIdFk() {
        if (_bccpNextBccpIdFk == null)
            _bccpNextBccpIdFk = new Bccp(this, Keys.BCCP_NEXT_BCCP_ID_FK);

        return _bccpNextBccpIdFk;
    }

    @Override
    public Bccp as(String alias) {
        return new Bccp(DSL.name(alias), this);
    }

    @Override
    public Bccp as(Name alias) {
        return new Bccp(alias, this);
    }

    @Override
    public Bccp as(Table<?> alias) {
        return new Bccp(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Bccp rename(String name) {
        return new Bccp(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Bccp rename(Name name) {
        return new Bccp(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Bccp rename(Table<?> name) {
        return new Bccp(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row22 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row22<String, String, String, String, String, String, String, String, String, Byte, String, String, String, String, LocalDateTime, LocalDateTime, String, Byte, String, String, String, String> fieldsRow() {
        return (Row22) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function22<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function22<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
