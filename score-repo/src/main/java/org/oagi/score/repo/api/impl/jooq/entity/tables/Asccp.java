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
import org.jooq.Row19;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Indexes;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccpRecord;


/**
 * An ASCCP specifies a role (or property) an ACC may play under another ACC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Asccp extends TableImpl<AsccpRecord> {

    private static final long serialVersionUID = -946249797;

    /**
     * The reference instance of <code>oagi.asccp</code>
     */
    public static final Asccp ASCCP = new Asccp();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsccpRecord> getRecordType() {
        return AsccpRecord.class;
    }

    /**
     * The column <code>oagi.asccp.asccp_id</code>. An internal, primary database key of an ASCCP.
     */
    public final TableField<AsccpRecord, ULong> ASCCP_ID = createField(DSL.name("asccp_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "An internal, primary database key of an ASCCP.");

    /**
     * The column <code>oagi.asccp.guid</code>. A globally unique identifier (GUID) of an ASCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<AsccpRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an ASCCP. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.asccp.property_term</code>. The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. There must be only one ASCCP without a Property_Term for a particular ACC.
     */
    public final TableField<AsccpRecord, String> PROPERTY_TERM = createField(DSL.name("property_term"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "The role (or property) the ACC as referred to by the Role_Of_ACC_ID play when the ASCCP is used by another ACC. There must be only one ASCCP without a Property_Term for a particular ACC.");

    /**
     * The column <code>oagi.asccp.definition</code>. Description of the ASCCP.
     */
    public final TableField<AsccpRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "Description of the ASCCP.");

    /**
     * The column <code>oagi.asccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<AsccpRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.asccp.role_of_acc_id</code>. The ACC from which this ASCCP is created (ASCCP applies role to the ACC).
     */
    public final TableField<AsccpRecord, ULong> ROLE_OF_ACC_ID = createField(DSL.name("role_of_acc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "The ACC from which this ASCCP is created (ASCCP applies role to the ACC).");

    /**
     * The column <code>oagi.asccp.den</code>. The dictionary entry name of the ASCCP.
     */
    public final TableField<AsccpRecord, String> DEN = createField(DSL.name("den"), org.jooq.impl.SQLDataType.VARCHAR(200), this, "The dictionary entry name of the ASCCP.");

    /**
     * The column <code>oagi.asccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public final TableField<AsccpRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the user who creates the entity. \n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.");

    /**
     * The column <code>oagi.asccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public final TableField<AsccpRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. ");

    /**
     * The column <code>oagi.asccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<AsccpRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.asccp.creation_timestamp</code>. Timestamp when the revision of the ASCCP was created. 

This never change for a revision.
     */
    public final TableField<AsccpRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "Timestamp when the revision of the ASCCP was created. \n\nThis never change for a revision.");

    /**
     * The column <code>oagi.asccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public final TableField<AsccpRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.");

    /**
     * The column <code>oagi.asccp.state</code>. Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the ASCCP.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public final TableField<AsccpRecord, String> STATE = createField(DSL.name("state"), org.jooq.impl.SQLDataType.VARCHAR(20), this, "Deleted, WIP, Draft, QA, Candidate, Production, Release Draft, Published. This the revision life cycle state of the ASCCP.\n\nState change can't be undone. But the history record can still keep the records of when the state was changed.");

    /**
     * The column <code>oagi.asccp.namespace_id</code>. Foreign key to the Namespace table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public final TableField<AsccpRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the Namespace table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.");

    /**
     * The column <code>oagi.asccp.reusable_indicator</code>. This indicates whether the ASCCP can be used by more than one ASCC. This maps directly to the XML schema local element declaration.
     */
    public final TableField<AsccpRecord, Byte> REUSABLE_INDICATOR = createField(DSL.name("reusable_indicator"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates whether the ASCCP can be used by more than one ASCC. This maps directly to the XML schema local element declaration.");

    /**
     * The column <code>oagi.asccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public final TableField<AsccpRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).");

    /**
     * The column <code>oagi.asccp.is_nillable</code>. This is corresponding to the XML schema nillable flag. Although the nillable may not apply in certain cases of the ASCCP (e.g., when it corresponds to an XSD group), the value is default to false for simplification.
     */
    public final TableField<AsccpRecord, Byte> IS_NILLABLE = createField(DSL.name("is_nillable"), org.jooq.impl.SQLDataType.TINYINT, this, "This is corresponding to the XML schema nillable flag. Although the nillable may not apply in certain cases of the ASCCP (e.g., when it corresponds to an XSD group), the value is default to false for simplification.");

    /**
     * The column <code>oagi.asccp.prev_asccp_id</code>. A self-foreign key to indicate the previous history record.
     */
    public final TableField<AsccpRecord, ULong> PREV_ASCCP_ID = createField(DSL.name("prev_asccp_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.asccp.next_asccp_id</code>. A self-foreign key to indicate the next history record.
     */
    public final TableField<AsccpRecord, ULong> NEXT_ASCCP_ID = createField(DSL.name("next_asccp_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the next history record.");

    /**
     * Create a <code>oagi.asccp</code> table reference
     */
    public Asccp() {
        this(DSL.name("asccp"), null);
    }

    /**
     * Create an aliased <code>oagi.asccp</code> table reference
     */
    public Asccp(String alias) {
        this(DSL.name(alias), ASCCP);
    }

    /**
     * Create an aliased <code>oagi.asccp</code> table reference
     */
    public Asccp(Name alias) {
        this(alias, ASCCP);
    }

    private Asccp(Name alias, Table<AsccpRecord> aliased) {
        this(alias, aliased, null);
    }

    private Asccp(Name alias, Table<AsccpRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("An ASCCP specifies a role (or property) an ACC may play under another ACC."), TableOptions.table());
    }

    public <O extends Record> Asccp(Table<O> child, ForeignKey<O, AsccpRecord> key) {
        super(child, key, ASCCP);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ASCCP_ASCCP_GUID_IDX, Indexes.ASCCP_ASCCP_LAST_UPDATE_TIMESTAMP_DESC_IDX);
    }

    @Override
    public Identity<AsccpRecord, ULong> getIdentity() {
        return Keys.IDENTITY_ASCCP;
    }

    @Override
    public UniqueKey<AsccpRecord> getPrimaryKey() {
        return Keys.KEY_ASCCP_PRIMARY;
    }

    @Override
    public List<UniqueKey<AsccpRecord>> getKeys() {
        return Arrays.<UniqueKey<AsccpRecord>>asList(Keys.KEY_ASCCP_PRIMARY);
    }

    @Override
    public List<ForeignKey<AsccpRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AsccpRecord, ?>>asList(Keys.ASCCP_ROLE_OF_ACC_ID_FK, Keys.ASCCP_CREATED_BY_FK, Keys.ASCCP_OWNER_USER_ID_FK, Keys.ASCCP_LAST_UPDATED_BY_FK, Keys.ASCCP_NAMESPACE_ID_FK, Keys.ASCCP_PREV_ASCCP_ID_FK, Keys.ASCCP_NEXT_ASCCP_ID_FK);
    }

    public Acc acc() {
        return new Acc(this, Keys.ASCCP_ROLE_OF_ACC_ID_FK);
    }

    public AppUser asccpCreatedByFk() {
        return new AppUser(this, Keys.ASCCP_CREATED_BY_FK);
    }

    public AppUser asccpOwnerUserIdFk() {
        return new AppUser(this, Keys.ASCCP_OWNER_USER_ID_FK);
    }

    public AppUser asccpLastUpdatedByFk() {
        return new AppUser(this, Keys.ASCCP_LAST_UPDATED_BY_FK);
    }

    public Namespace namespace() {
        return new Namespace(this, Keys.ASCCP_NAMESPACE_ID_FK);
    }

    public Asccp asccpPrevAsccpIdFk() {
        return new Asccp(this, Keys.ASCCP_PREV_ASCCP_ID_FK);
    }

    public Asccp asccpNextAsccpIdFk() {
        return new Asccp(this, Keys.ASCCP_NEXT_ASCCP_ID_FK);
    }

    @Override
    public Asccp as(String alias) {
        return new Asccp(DSL.name(alias), this);
    }

    @Override
    public Asccp as(Name alias) {
        return new Asccp(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Asccp rename(String name) {
        return new Asccp(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Asccp rename(Name name) {
        return new Asccp(name, null);
    }

    // -------------------------------------------------------------------------
    // Row19 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row19<ULong, String, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, Byte, Byte, Byte, ULong, ULong> fieldsRow() {
        return (Row19) super.fieldsRow();
    }
}