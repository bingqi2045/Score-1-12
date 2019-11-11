/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row22;
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
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;


/**
 * An ASCC represents a relationship/association between two ACCs through 
 * an ASCCP. 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Ascc extends TableImpl<AsccRecord> {

    private static final long serialVersionUID = -1232020202;

    /**
     * The reference instance of <code>oagi.ascc</code>
     */
    public static final Ascc ASCC = new Ascc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsccRecord> getRecordType() {
        return AsccRecord.class;
    }

    /**
     * The column <code>oagi.ascc.ascc_id</code>. An internal, primary database key of an ASCC.
     */
    public final TableField<AsccRecord, ULong> ASCC_ID = createField(DSL.name("ascc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "An internal, primary database key of an ASCC.");

    /**
     * The column <code>oagi.ascc.guid</code>. A globally unique identifier (GUID) of an ASCC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<AsccRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an ASCC. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.ascc.cardinality_min</code>. Minimum occurrence of the TO_ASCCP_ID. The valid values are non-negative integer.
     */
    public final TableField<AsccRecord, Integer> CARDINALITY_MIN = createField(DSL.name("cardinality_min"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Minimum occurrence of the TO_ASCCP_ID. The valid values are non-negative integer.");

    /**
     * The column <code>oagi.ascc.cardinality_max</code>. Maximum cardinality of the TO_ASCCP_ID. A valid value is integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public final TableField<AsccRecord, Integer> CARDINALITY_MAX = createField(DSL.name("cardinality_max"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Maximum cardinality of the TO_ASCCP_ID. A valid value is integer -1 and up. Specifically, -1 means unbounded. 0 means prohibited or not to use.");

    /**
     * The column <code>oagi.ascc.seq_key</code>. This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again. 
     */
    public final TableField<AsccRecord, Integer> SEQ_KEY = createField(DSL.name("seq_key"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "This indicates the order of the associations among other siblings. A valid value is positive integer. The SEQ_KEY at the CC side is localized. In other words, if an ACC is based on another ACC, SEQ_KEY of ASCCs or BCCs of the former ACC starts at 1 again. ");

    /**
     * The column <code>oagi.ascc.from_acc_id</code>. FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_ASCCP_ID.
     */
    public final TableField<AsccRecord, ULong> FROM_ACC_ID = createField(DSL.name("from_acc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "FROM_ACC_ID is a foreign key pointing to an ACC record. It is basically pointing to a parent data element (type) of the TO_ASCCP_ID.");

    /**
     * The column <code>oagi.ascc.to_asccp_id</code>. TO_ASCCP_ID is a foreign key to an ASCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. 
     */
    public final TableField<AsccRecord, ULong> TO_ASCCP_ID = createField(DSL.name("to_asccp_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "TO_ASCCP_ID is a foreign key to an ASCCP table record. It is basically pointing to a child data element of the FROM_ACC_ID. ");

    /**
     * The column <code>oagi.ascc.den</code>. DEN (dictionary entry name) of the ASCC. This column can be derived from Qualifier and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_ASCCP_ID as Qualifier + "_ " + OBJECT_CLASS_TERM + ". " + DEN. 
     */
    public final TableField<AsccRecord, String> DEN = createField(DSL.name("den"), org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "DEN (dictionary entry name) of the ASCC. This column can be derived from Qualifier and OBJECT_CLASS_TERM of the FROM_ACC_ID and DEN of the TO_ASCCP_ID as Qualifier + \"_ \" + OBJECT_CLASS_TERM + \". \" + DEN. ");

    /**
     * The column <code>oagi.ascc.definition</code>. This is a documentation or description of the ASCC. Since ASCC is business context independent, this is a business context independent description of the ASCC. Since there are definitions also in the ASCCP (as referenced by the TO_ASCCP_ID column) and the ACC under that ASCCP, definition in the ASCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the ASCCP.
     */
    public final TableField<AsccRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "This is a documentation or description of the ASCC. Since ASCC is business context independent, this is a business context independent description of the ASCC. Since there are definitions also in the ASCCP (as referenced by the TO_ASCCP_ID column) and the ACC under that ASCCP, definition in the ASCC is a specific description about the relationship between the ACC (as in FROM_ACC_ID) and the ASCCP.");

    /**
     * The column <code>oagi.ascc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<AsccRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.ascc.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public final TableField<AsccRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).");

    /**
     * The column <code>oagi.ascc.created_by</code>. A foreign key to the APP_USER table referring to the user who creates the entity.

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public final TableField<AsccRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.");

    /**
     * The column <code>oagi.ascc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public final TableField<AsccRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. ");

    /**
     * The column <code>oagi.ascc.last_updated_by</code>. A foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<AsccRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.ascc.creation_timestamp</code>. Timestamp when the revision of the ASCC was created. 

This never change for a revision.
     */
    public final TableField<AsccRecord, Timestamp> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the revision of the ASCC was created. \n\nThis never change for a revision.");

    /**
     * The column <code>oagi.ascc.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.
     */
    public final TableField<AsccRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the change has occurred.");

    /**
     * The column <code>oagi.ascc.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This is the revision life cycle state of the entity.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public final TableField<AsccRecord, Integer> STATE = createField(DSL.name("state"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This is the revision life cycle state of the entity.\n\nState change can't be undone. But the history record can still keep the records of when the state was changed.");

    /**
     * The column <code>oagi.ascc.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public final TableField<AsccRecord, Integer> REVISION_NUM = createField(DSL.name("revision_num"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).");

    /**
     * The column <code>oagi.ascc.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUM can be 0, 1, 2, and so on. The zero value is assign to the record with REVISION_NUM = 0 as a default.
     */
    public final TableField<AsccRecord, Integer> REVISION_TRACKING_NUM = createField(DSL.name("revision_tracking_num"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUM can be 0, 1, 2, and so on. The zero value is assign to the record with REVISION_NUM = 0 as a default.");

    /**
     * The column <code>oagi.ascc.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public final TableField<AsccRecord, Byte> REVISION_ACTION = createField(DSL.name("revision_action"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.");

    /**
     * The column <code>oagi.ascc.release_id</code>. RELEASE_ID is an incremental integer. It is an unformatted counterpart of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. RELEASE_ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).

Not all component revisions have an associated RELEASE_ID because some revisions may never be released.

Unpublished components cannot be released.

This column is NULL for the current record.
     */
    public final TableField<AsccRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "RELEASE_ID is an incremental integer. It is an unformatted counterpart of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. RELEASE_ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released.\n\nUnpublished components cannot be released.\n\nThis column is NULL for the current record.");

    /**
     * The column <code>oagi.ascc.current_ascc_id</code>. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.

It is noted that although this is a foreign key by definition, we don't specify a foreign key in the data model. This is because when an entity is deleted the current record won't exist anymore.

The value of this column for the current record should be left NULL.
     */
    public final TableField<AsccRecord, ULong> CURRENT_ASCC_ID = createField(DSL.name("current_ascc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don't specify a foreign key in the data model. This is because when an entity is deleted the current record won't exist anymore.\n\nThe value of this column for the current record should be left NULL.");

    /**
     * Create a <code>oagi.ascc</code> table reference
     */
    public Ascc() {
        this(DSL.name("ascc"), null);
    }

    /**
     * Create an aliased <code>oagi.ascc</code> table reference
     */
    public Ascc(String alias) {
        this(DSL.name(alias), ASCC);
    }

    /**
     * Create an aliased <code>oagi.ascc</code> table reference
     */
    public Ascc(Name alias) {
        this(alias, ASCC);
    }

    private Ascc(Name alias, Table<AsccRecord> aliased) {
        this(alias, aliased, null);
    }

    private Ascc(Name alias, Table<AsccRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("An ASCC represents a relationship/association between two ACCs through an ASCCP. "));
    }

    public <O extends Record> Ascc(Table<O> child, ForeignKey<O, AsccRecord> key) {
        super(child, key, ASCC);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ASCC_ASCC_CREATED_BY_FK, Indexes.ASCC_ASCC_CURRENT_ASCC_ID_FK, Indexes.ASCC_ASCC_FROM_ACC_ID_FK, Indexes.ASCC_ASCC_LAST_UPDATED_BY_FK, Indexes.ASCC_ASCC_OWNER_USER_ID_FK, Indexes.ASCC_ASCC_RELEASE_ID_FK, Indexes.ASCC_ASCC_TO_ASCCP_ID_FK, Indexes.ASCC_PRIMARY);
    }

    @Override
    public Identity<AsccRecord, ULong> getIdentity() {
        return Keys.IDENTITY_ASCC;
    }

    @Override
    public UniqueKey<AsccRecord> getPrimaryKey() {
        return Keys.KEY_ASCC_PRIMARY;
    }

    @Override
    public List<UniqueKey<AsccRecord>> getKeys() {
        return Arrays.<UniqueKey<AsccRecord>>asList(Keys.KEY_ASCC_PRIMARY);
    }

    @Override
    public List<ForeignKey<AsccRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AsccRecord, ?>>asList(Keys.ASCC_FROM_ACC_ID_FK, Keys.ASCC_TO_ASCCP_ID_FK, Keys.ASCC_CREATED_BY_FK, Keys.ASCC_OWNER_USER_ID_FK, Keys.ASCC_LAST_UPDATED_BY_FK, Keys.ASCC_RELEASE_ID_FK, Keys.ASCC_CURRENT_ASCC_ID_FK);
    }

    public Acc acc() {
        return new Acc(this, Keys.ASCC_FROM_ACC_ID_FK);
    }

    public Asccp asccp() {
        return new Asccp(this, Keys.ASCC_TO_ASCCP_ID_FK);
    }

    public AppUser asccCreatedByFk() {
        return new AppUser(this, Keys.ASCC_CREATED_BY_FK);
    }

    public AppUser asccOwnerUserIdFk() {
        return new AppUser(this, Keys.ASCC_OWNER_USER_ID_FK);
    }

    public AppUser asccLastUpdatedByFk() {
        return new AppUser(this, Keys.ASCC_LAST_UPDATED_BY_FK);
    }

    public Release release() {
        return new Release(this, Keys.ASCC_RELEASE_ID_FK);
    }

    public org.oagi.srt.entity.jooq.tables.Ascc ascc() {
        return new org.oagi.srt.entity.jooq.tables.Ascc(this, Keys.ASCC_CURRENT_ASCC_ID_FK);
    }

    @Override
    public Ascc as(String alias) {
        return new Ascc(DSL.name(alias), this);
    }

    @Override
    public Ascc as(Name alias) {
        return new Ascc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Ascc rename(String name) {
        return new Ascc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Ascc rename(Name name) {
        return new Ascc(name, null);
    }

    // -------------------------------------------------------------------------
    // Row22 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row22<ULong, String, Integer, Integer, Integer, ULong, ULong, String, String, String, Byte, ULong, ULong, ULong, Timestamp, Timestamp, Integer, Integer, Integer, Byte, ULong, ULong> fieldsRow() {
        return (Row22) super.fieldsRow();
    }
}
