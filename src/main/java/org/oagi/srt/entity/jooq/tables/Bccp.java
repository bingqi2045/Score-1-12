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
import org.oagi.srt.entity.jooq.tables.records.BccpRecord;


/**
 * An BCCP specifies a property concept and data type associated with it. 
 * A BCCP can be then added as a property of an ACC.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Bccp extends TableImpl<BccpRecord> {

    private static final long serialVersionUID = 331874983;

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
     * The column <code>oagi.bccp.bccp_id</code>. An internal, primary database key.
     */
    public final TableField<BccpRecord, ULong> BCCP_ID = createField("bccp_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "An internal, primary database key.");

    /**
     * The column <code>oagi.bccp.guid</code>. A globally unique identifier (GUID). Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.',
     */
    public final TableField<BccpRecord, String> GUID = createField("guid", org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID). Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.',");

    /**
     * The column <code>oagi.bccp.property_term</code>. The property concept that the BCCP models. 
     */
    public final TableField<BccpRecord, String> PROPERTY_TERM = createField("property_term", org.jooq.impl.SQLDataType.VARCHAR(60).nullable(false), this, "The property concept that the BCCP models. ");

    /**
     * The column <code>oagi.bccp.representation_term</code>. The representation term convey the format of the data the BCCP can take. The value is derived from the DT.DATA_TYPE_TERM of the associated BDT as referred to by the BDT_ID column.
     */
    public final TableField<BccpRecord, String> REPRESENTATION_TERM = createField("representation_term", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "The representation term convey the format of the data the BCCP can take. The value is derived from the DT.DATA_TYPE_TERM of the associated BDT as referred to by the BDT_ID column.");

    /**
     * The column <code>oagi.bccp.bdt_id</code>. Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.
     */
    public final TableField<BccpRecord, ULong> BDT_ID = createField("bdt_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key pointing to the DT table indicating the data typye or data format of the BCCP. Only DT_ID which DT_Type is BDT can be used.");

    /**
     * The column <code>oagi.bccp.den</code>. The dictionary entry name of the BCCP. It is derived by PROPERTY_TERM + ". " + REPRESENTATION_TERM.
     */
    public final TableField<BccpRecord, String> DEN = createField("den", org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "The dictionary entry name of the BCCP. It is derived by PROPERTY_TERM + \". \" + REPRESENTATION_TERM.");

    /**
     * The column <code>oagi.bccp.definition</code>. Description of the BCCP.
     */
    public final TableField<BccpRecord, String> DEFINITION = createField("definition", org.jooq.impl.SQLDataType.CLOB, this, "Description of the BCCP.");

    /**
     * The column <code>oagi.bccp.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<BccpRecord, String> DEFINITION_SOURCE = createField("definition_source", org.jooq.impl.SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.bccp.module_id</code>. Foreign key to the module table indicating physical schema module the BCCP belongs to.
     */
    public final TableField<BccpRecord, ULong> MODULE_ID = createField("module_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the module table indicating physical schema module the BCCP belongs to.");

    /**
     * The column <code>oagi.bccp.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public final TableField<BccpRecord, ULong> NAMESPACE_ID = createField("namespace_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.");

    /**
     * The column <code>oagi.bccp.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).
     */
    public final TableField<BccpRecord, Byte> IS_DEPRECATED = createField("is_deprecated", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be created).");

    /**
     * The column <code>oagi.bccp.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity. 

This column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public final TableField<BccpRecord, ULong> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the user who creates the entity. \n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.");

    /**
     * The column <code>oagi.bccp.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public final TableField<BccpRecord, ULong> OWNER_USER_ID = createField("owner_user_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.bccp.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who has updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<BccpRecord, ULong> LAST_UPDATED_BY = createField("last_updated_by", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who has updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.bccp.creation_timestamp</code>. Timestamp when the revision of the BCCP was created. 

This never change for a revision.
     */
    public final TableField<BccpRecord, Timestamp> CREATION_TIMESTAMP = createField("creation_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the revision of the BCCP was created. \n\nThis never change for a revision.");

    /**
     * The column <code>oagi.bccp.last_update_timestamp</code>. The timestamp when the record was last updated.

The value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public final TableField<BccpRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField("last_update_timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.");

    /**
     * The column <code>oagi.bccp.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This the revision life cycle state of the ACC.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public final TableField<BccpRecord, Integer> STATE = createField("state", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This the revision life cycle state of the ACC.\n\nState change can't be undone. But the history record can still keep the records of when the state was changed.");

    /**
     * The column <code>oagi.bccp.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public final TableField<BccpRecord, Integer> REVISION_NUM = createField("revision_num", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).");

    /**
     * The column <code>oagi.bccp.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUMB can be 0, 1, 2, and so on. The zero value is assigned to the record with REVISION_NUM = 0 as a default.
     */
    public final TableField<BccpRecord, Integer> REVISION_TRACKING_NUM = createField("revision_tracking_num", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUMB can be 0, 1, 2, and so on. The zero value is assigned to the record with REVISION_NUM = 0 as a default.");

    /**
     * The column <code>oagi.bccp.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public final TableField<BccpRecord, Integer> REVISION_ACTION = createField("revision_action", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.INTEGER)), this, "This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.");

    /**
     * The column <code>oagi.bccp.release_id</code>. RELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).

Not all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.

Unpublished components cannot be released.

This column is NULLl for the current record.
     */
    public final TableField<BccpRecord, ULong> RELEASE_ID = createField("release_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "RELEASE_ID is an incremental integer. It is an unformatted counter part of the RELEASE_NUMBER in the RELEASE table. RELEASE_ID can be 1, 2, 3, and so on. A release ID indicates the release point when a particular component revision is released. A component revision is only released once and assumed to be included in the subsequent releases unless it has been deleted (as indicated by the REVISION_ACTION column).\n\nNot all component revisions have an associated RELEASE_ID because some revisions may never be released. USER_EXTENSION_GROUP component type is never part of a release.\n\nUnpublished components cannot be released.\n\nThis column is NULLl for the current record.");

    /**
     * The column <code>oagi.bccp.current_bccp_id</code>. This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.

It is noted that although this is a foreign key by definition, we don't specify a foreign key in the data model. This is because when an entity is deleted the current record won't exist anymore.

The value of this column for the current record should be left NULL.
     */
    public final TableField<BccpRecord, ULong> CURRENT_BCCP_ID = createField("current_bccp_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This is a self-foreign-key. It points from a revised record to the current record. The current record is denoted by the the record whose REVISION_NUM is 0. Revised records (a.k.a. history records) and their current record must have the same GUID.\n\nIt is noted that although this is a foreign key by definition, we don't specify a foreign key in the data model. This is because when an entity is deleted the current record won't exist anymore.\n\nThe value of this column for the current record should be left NULL.");

    /**
     * The column <code>oagi.bccp.is_nillable</code>. This is corresponding to the XML Schema nillable flag. Although the nillable may not apply to certain cases of the BCCP (e.g., when it is only used as XSD attribute), the value is default to false for simplification. 
     */
    public final TableField<BccpRecord, Byte> IS_NILLABLE = createField("is_nillable", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "This is corresponding to the XML Schema nillable flag. Although the nillable may not apply to certain cases of the BCCP (e.g., when it is only used as XSD attribute), the value is default to false for simplification. ");

    /**
     * The column <code>oagi.bccp.default_value</code>. This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.
     */
    public final TableField<BccpRecord, String> DEFAULT_VALUE = createField("default_value", org.jooq.impl.SQLDataType.CLOB, this, "This column specifies the default value constraint. Default and fixed value constraints cannot be used at the same time.");

    /**
     * Create a <code>oagi.bccp</code> table reference
     */
    public Bccp() {
        this(DSL.name("bccp"), null);
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

    private Bccp(Name alias, Table<BccpRecord> aliased) {
        this(alias, aliased, null);
    }

    private Bccp(Name alias, Table<BccpRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("An BCCP specifies a property concept and data type associated with it. A BCCP can be then added as a property of an ACC."));
    }

    public <O extends Record> Bccp(Table<O> child, ForeignKey<O, BccpRecord> key) {
        super(child, key, BCCP);
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
        return Arrays.<Index>asList(Indexes.BCCP_BCCP_BDT_ID_FK, Indexes.BCCP_BCCP_CREATED_BY_FK, Indexes.BCCP_BCCP_CURRENT_BCCP_ID_FK, Indexes.BCCP_BCCP_LAST_UPDATED_BY_FK, Indexes.BCCP_BCCP_MODULE_ID_FK, Indexes.BCCP_BCCP_NAMESPACE_ID_FK, Indexes.BCCP_BCCP_OWNER_USER_ID_FK, Indexes.BCCP_BCCP_RELEASE_ID_FK, Indexes.BCCP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<BccpRecord, ULong> getIdentity() {
        return Keys.IDENTITY_BCCP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BccpRecord> getPrimaryKey() {
        return Keys.KEY_BCCP_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BccpRecord>> getKeys() {
        return Arrays.<UniqueKey<BccpRecord>>asList(Keys.KEY_BCCP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BccpRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BccpRecord, ?>>asList(Keys.BCCP_BDT_ID_FK, Keys.BCCP_MODULE_ID_FK, Keys.BCCP_NAMESPACE_ID_FK, Keys.BCCP_CREATED_BY_FK, Keys.BCCP_OWNER_USER_ID_FK, Keys.BCCP_LAST_UPDATED_BY_FK, Keys.BCCP_RELEASE_ID_FK, Keys.BCCP_CURRENT_BCCP_ID_FK);
    }

    public Dt dt() {
        return new Dt(this, Keys.BCCP_BDT_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.BCCP_MODULE_ID_FK);
    }

    public Namespace namespace() {
        return new Namespace(this, Keys.BCCP_NAMESPACE_ID_FK);
    }

    public AppUser bccpCreatedByFk() {
        return new AppUser(this, Keys.BCCP_CREATED_BY_FK);
    }

    public AppUser bccpOwnerUserIdFk() {
        return new AppUser(this, Keys.BCCP_OWNER_USER_ID_FK);
    }

    public AppUser bccpLastUpdatedByFk() {
        return new AppUser(this, Keys.BCCP_LAST_UPDATED_BY_FK);
    }

    public Release release() {
        return new Release(this, Keys.BCCP_RELEASE_ID_FK);
    }

    public org.oagi.srt.entity.jooq.tables.Bccp bccp() {
        return new org.oagi.srt.entity.jooq.tables.Bccp(this, Keys.BCCP_CURRENT_BCCP_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bccp as(String alias) {
        return new Bccp(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bccp as(Name alias) {
        return new Bccp(alias, this);
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
}
