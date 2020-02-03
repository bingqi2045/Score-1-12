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
import org.jooq.Row21;
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
import org.oagi.srt.entity.jooq.tables.records.AccRecord;


/**
 * The ACC table holds information about complex data structured concepts. 
 * For example, OAGIS's Components, Nouns, and BODs are captured in the ACC 
 * table.
 * 
 * Note that only Extension is supported when deriving ACC from another ACC. 
 * (So if there is a restriction needed, maybe that concept should placed 
 * higher in the derivation hierarchy rather than lower.)
 * 
 * In OAGIS, all XSD extensions will be treated as a qualification of an ACC.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Acc extends TableImpl<AccRecord> {

    private static final long serialVersionUID = -1732064006;

    /**
     * The reference instance of <code>oagi.acc</code>
     */
    public static final Acc ACC = new Acc();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AccRecord> getRecordType() {
        return AccRecord.class;
    }

    /**
     * The column <code>oagi.acc.acc_id</code>. A internal, primary database key of an ACC.
     */
    public final TableField<AccRecord, ULong> ACC_ID = createField(DSL.name("acc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "A internal, primary database key of an ACC.");

    /**
     * The column <code>oagi.acc.guid</code>. A globally unique identifier (GUID) of an ACC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<AccRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "A globally unique identifier (GUID) of an ACC. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.acc.object_class_term</code>. Object class name of the ACC concept. For OAGIS, this is generally name of a type with the "Type" truncated from the end. Per CCS the name is space separated. "ID" is expanded to "Identifier".
     */
    public final TableField<AccRecord, String> OBJECT_CLASS_TERM = createField(DSL.name("object_class_term"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "Object class name of the ACC concept. For OAGIS, this is generally name of a type with the \"Type\" truncated from the end. Per CCS the name is space separated. \"ID\" is expanded to \"Identifier\".");

    /**
     * The column <code>oagi.acc.den</code>. DEN (dictionary entry name) of the ACC. It can be derived as OBJECT_CLASS_QUALIFIER + "_ " + OBJECT_CLASS_TERM + ". Details".
     */
    public final TableField<AccRecord, String> DEN = createField(DSL.name("den"), org.jooq.impl.SQLDataType.VARCHAR(200).nullable(false), this, "DEN (dictionary entry name) of the ACC. It can be derived as OBJECT_CLASS_QUALIFIER + \"_ \" + OBJECT_CLASS_TERM + \". Details\".");

    /**
     * The column <code>oagi.acc.definition</code>. This is a documentation or description of the ACC. Since ACC is business context independent, this is a business context independent description of the ACC concept.
     */
    public final TableField<AccRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "This is a documentation or description of the ACC. Since ACC is business context independent, this is a business context independent description of the ACC concept.");

    /**
     * The column <code>oagi.acc.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<AccRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.acc.based_acc_id</code>. BASED_ACC_ID is a foreign key to the ACC table itself. It represents the ACC that is qualified by this ACC. In general CCS sense, a qualification can be a content extension or restriction, but the current scope supports only extension.
     */
    public final TableField<AccRecord, ULong> BASED_ACC_ID = createField(DSL.name("based_acc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "BASED_ACC_ID is a foreign key to the ACC table itself. It represents the ACC that is qualified by this ACC. In general CCS sense, a qualification can be a content extension or restriction, but the current scope supports only extension.");

    /**
     * The column <code>oagi.acc.object_class_qualifier</code>. This column stores the qualifier of an ACC, particularly when it has a based ACC. 
     */
    public final TableField<AccRecord, String> OBJECT_CLASS_QUALIFIER = createField(DSL.name("object_class_qualifier"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "This column stores the qualifier of an ACC, particularly when it has a based ACC. ");

    /**
     * The column <code>oagi.acc.oagis_component_type</code>. The value can be 0 = BASE, 1 = SEMANTICS, 2 = EXTENSION, 3 = SEMANTIC_GROUP, 4 = USER_EXTENSION_GROUP, 5 = EMBEDDED. Generally, BASE is assigned when the OBJECT_CLASS_TERM contains "Base" at the end. EXTENSION is assigned with the OBJECT_CLASS_TERM contains "Extension" at the end. SEMANTIC_GROUP is assigned when an ACC is imported from an XSD Group. USER_EXTENSION_GROUP is a wrapper ACC (a virtual ACC) for segregating user's extension content. EMBEDDED is used for an ACC whose content is not explicitly defined in the database, for example, the Any Structured Content ACC that corresponds to the xsd:any.  Other cases are assigned SEMANTICS. 
     */
    public final TableField<AccRecord, Integer> OAGIS_COMPONENT_TYPE = createField(DSL.name("oagis_component_type"), org.jooq.impl.SQLDataType.INTEGER, this, "The value can be 0 = BASE, 1 = SEMANTICS, 2 = EXTENSION, 3 = SEMANTIC_GROUP, 4 = USER_EXTENSION_GROUP, 5 = EMBEDDED. Generally, BASE is assigned when the OBJECT_CLASS_TERM contains \"Base\" at the end. EXTENSION is assigned with the OBJECT_CLASS_TERM contains \"Extension\" at the end. SEMANTIC_GROUP is assigned when an ACC is imported from an XSD Group. USER_EXTENSION_GROUP is a wrapper ACC (a virtual ACC) for segregating user's extension content. EMBEDDED is used for an ACC whose content is not explicitly defined in the database, for example, the Any Structured Content ACC that corresponds to the xsd:any.  Other cases are assigned SEMANTICS. ");

    /**
     * The column <code>oagi.acc.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public final TableField<AccRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.");

    /**
     * The column <code>oagi.acc.created_by</code>. Foreign key to the APP_USER table referring to the user who creates the entity.\n\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.
     */
    public final TableField<AccRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the user who creates the entity.\\n\\nThis column never change between the history and the current record for a given revision. The history record should have the same value as that of its current record.");

    /**
     * The column <code>oagi.acc.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. 
     */
    public final TableField<AccRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\\n\\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership. ");

    /**
     * The column <code>oagi.acc.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<AccRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record. \\n\\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.acc.creation_timestamp</code>. Timestamp when the revision of the ACC was created. \n\nThis never change for a revision.
     */
    public final TableField<AccRecord, Timestamp> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "Timestamp when the revision of the ACC was created. \\n\\nThis never change for a revision.");

    /**
     * The column <code>oagi.acc.last_update_timestamp</code>. The timestamp when the record was last updated.\n\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.
     */
    public final TableField<AccRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was last updated.\\n\\nThe value of this column in the latest history record should be the same as that of the current record. This column keeps the record of when the revision has occurred.");

    /**
     * The column <code>oagi.acc.state</code>. 1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This the revision life cycle state of the ACC.

State change can't be undone. But the history record can still keep the records of when the state was changed.
     */
    public final TableField<AccRecord, Integer> STATE = createField(DSL.name("state"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "1 = EDITING, 2 = CANDIDATE, 3 = PUBLISHED. This the revision life cycle state of the ACC.\n\nState change can't be undone. But the history record can still keep the records of when the state was changed.");

    /**
     * The column <code>oagi.acc.revision_num</code>. REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).
     */
    public final TableField<AccRecord, Integer> REVISION_NUM = createField(DSL.name("revision_num"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "REVISION_NUM is an incremental integer. It tracks changes in each component. If a change is made to a component after it has been published, the component receives a new revision number. Revision number can be 0, 1, 2, and so on. A record with zero revision number reflects the current record of the component (the identity of a component in this case is its GUID or the primary key).");

    /**
     * The column <code>oagi.acc.revision_tracking_num</code>. REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUMB can be 0, 1, 2, and so on. The zero value is assigned to the record with REVISION_NUM = 0 as a default.
     */
    public final TableField<AccRecord, Integer> REVISION_TRACKING_NUM = createField(DSL.name("revision_tracking_num"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "REVISION_TRACKING_NUM supports the ability to undo changes during a revision (life cycle of a revision is from the component's EDITING state to PUBLISHED state). Once the component has transitioned into the PUBLISHED state for its particular revision, all revision tracking records are deleted except the latest one. REVISION_TRACKING_NUMB can be 0, 1, 2, and so on. The zero value is assigned to the record with REVISION_NUM = 0 as a default.");

    /**
     * The column <code>oagi.acc.revision_action</code>. This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.
     */
    public final TableField<AccRecord, Byte> REVISION_ACTION = createField(DSL.name("revision_action"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates the action associated with the record. The action can be 1 = INSERT, 2 = UPDATE, and 3 = DELETE. This column is null for the current record.");

    /**
     * The column <code>oagi.acc.is_deprecated</code>. Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public final TableField<AccRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Indicates whether the CC is deprecated and should not be reused (i.e., no new reference to this record should be allowed).");

    /**
     * The column <code>oagi.acc.is_abstract</code>. This is the XML Schema abstract flag. Default is false. If it is true, the abstract flag will be set to true when generating a corresponding xsd:complexType. So although this flag may not apply to some ACCs such as those that are xsd:group. It is still have a false value.
     */
    public final TableField<AccRecord, Byte> IS_ABSTRACT = createField(DSL.name("is_abstract"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This is the XML Schema abstract flag. Default is false. If it is true, the abstract flag will be set to true when generating a corresponding xsd:complexType. So although this flag may not apply to some ACCs such as those that are xsd:group. It is still have a false value.");

    /**
     * Create a <code>oagi.acc</code> table reference
     */
    public Acc() {
        this(DSL.name("acc"), null);
    }

    /**
     * Create an aliased <code>oagi.acc</code> table reference
     */
    public Acc(String alias) {
        this(DSL.name(alias), ACC);
    }

    /**
     * Create an aliased <code>oagi.acc</code> table reference
     */
    public Acc(Name alias) {
        this(alias, ACC);
    }

    private Acc(Name alias, Table<AccRecord> aliased) {
        this(alias, aliased, null);
    }

    private Acc(Name alias, Table<AccRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The ACC table holds information about complex data structured concepts. For example, OAGIS's Components, Nouns, and BODs are captured in the ACC table.\n\nNote that only Extension is supported when deriving ACC from another ACC. (So if there is a restriction needed, maybe that concept should placed higher in the derivation hierarchy rather than lower.)\n\nIn OAGIS, all XSD extensions will be treated as a qualification of an ACC."));
    }

    public <O extends Record> Acc(Table<O> child, ForeignKey<O, AccRecord> key) {
        super(child, key, ACC);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ACC_ACC_BASED_ACC_ID_FK, Indexes.ACC_ACC_CREATED_BY_FK, Indexes.ACC_ACC_GUID_IDX, Indexes.ACC_ACC_LAST_UPDATED_BY_FK, Indexes.ACC_ACC_LAST_UPDATE_TIMESTAMP_DESC_IDX, Indexes.ACC_ACC_NAMESPACE_ID_FK, Indexes.ACC_ACC_OWNER_USER_ID_FK, Indexes.ACC_ACC_REVISION_IDX, Indexes.ACC_PRIMARY);
    }

    @Override
    public Identity<AccRecord, ULong> getIdentity() {
        return Keys.IDENTITY_ACC;
    }

    @Override
    public UniqueKey<AccRecord> getPrimaryKey() {
        return Keys.KEY_ACC_PRIMARY;
    }

    @Override
    public List<UniqueKey<AccRecord>> getKeys() {
        return Arrays.<UniqueKey<AccRecord>>asList(Keys.KEY_ACC_PRIMARY);
    }

    @Override
    public List<ForeignKey<AccRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AccRecord, ?>>asList(Keys.ACC_BASED_ACC_ID_FK, Keys.ACC_NAMESPACE_ID_FK, Keys.ACC_CREATED_BY_FK, Keys.ACC_OWNER_USER_ID_FK, Keys.ACC_LAST_UPDATED_BY_FK);
    }

    public org.oagi.srt.entity.jooq.tables.Acc acc() {
        return new org.oagi.srt.entity.jooq.tables.Acc(this, Keys.ACC_BASED_ACC_ID_FK);
    }

    public Namespace namespace() {
        return new Namespace(this, Keys.ACC_NAMESPACE_ID_FK);
    }

    public AppUser accCreatedByFk() {
        return new AppUser(this, Keys.ACC_CREATED_BY_FK);
    }

    public AppUser accOwnerUserIdFk() {
        return new AppUser(this, Keys.ACC_OWNER_USER_ID_FK);
    }

    public AppUser accLastUpdatedByFk() {
        return new AppUser(this, Keys.ACC_LAST_UPDATED_BY_FK);
    }

    @Override
    public Acc as(String alias) {
        return new Acc(DSL.name(alias), this);
    }

    @Override
    public Acc as(Name alias) {
        return new Acc(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Acc rename(String name) {
        return new Acc(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Acc rename(Name name) {
        return new Acc(name, null);
    }

    // -------------------------------------------------------------------------
    // Row21 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row21<ULong, String, String, String, String, String, ULong, String, Integer, ULong, ULong, ULong, ULong, Timestamp, Timestamp, Integer, Integer, Integer, Byte, Byte, Byte> fieldsRow() {
        return (Row21) super.fieldsRow();
    }
}
