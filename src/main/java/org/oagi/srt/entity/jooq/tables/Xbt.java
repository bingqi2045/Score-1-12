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
import org.oagi.srt.entity.jooq.tables.records.XbtRecord;


/**
 * This table stores XML schema built-in types and OAGIS built-in types. OAGIS 
 * built-in types are those types defined in the XMLSchemaBuiltinType and 
 * the XMLSchemaBuiltinType Patterns schemas.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Xbt extends TableImpl<XbtRecord> {

    private static final long serialVersionUID = 1554427218;

    /**
     * The reference instance of <code>oagi.xbt</code>
     */
    public static final Xbt XBT = new Xbt();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<XbtRecord> getRecordType() {
        return XbtRecord.class;
    }

    /**
     * The column <code>oagi.xbt.xbt_id</code>. Primary, internal database key.
     */
    public final TableField<XbtRecord, ULong> XBT_ID = createField(DSL.name("xbt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.xbt.name</code>. Human understandable name of the built-in type.
     */
    public final TableField<XbtRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(45), this, "Human understandable name of the built-in type.");

    /**
     * The column <code>oagi.xbt.builtIn_type</code>. Built-in type as it should appear in the XML schema including the namespace prefix. Namespace prefix for the XML schema namespace is assumed to be 'xsd' and a default prefix for the OAGIS built-int type.
     */
    public final TableField<XbtRecord, String> BUILTIN_TYPE = createField(DSL.name("builtIn_type"), org.jooq.impl.SQLDataType.VARCHAR(45), this, "Built-in type as it should appear in the XML schema including the namespace prefix. Namespace prefix for the XML schema namespace is assumed to be 'xsd' and a default prefix for the OAGIS built-int type.");

    /**
     * The column <code>oagi.xbt.jbt_draft05_map</code>.
     */
    public final TableField<XbtRecord, String> JBT_DRAFT05_MAP = createField(DSL.name("jbt_draft05_map"), org.jooq.impl.SQLDataType.VARCHAR(500), this, "");

    /**
     * The column <code>oagi.xbt.openapi30_map</code>.
     */
    public final TableField<XbtRecord, String> OPENAPI30_MAP = createField(DSL.name("openapi30_map"), org.jooq.impl.SQLDataType.VARCHAR(500), this, "");

    /**
     * The column <code>oagi.xbt.subtype_of_xbt_id</code>. Foreign key to the XBT table itself. It indicates a super type of this XSD built-in type.
     */
    public final TableField<XbtRecord, ULong> SUBTYPE_OF_XBT_ID = createField(DSL.name("subtype_of_xbt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the XBT table itself. It indicates a super type of this XSD built-in type.");

    /**
     * The column <code>oagi.xbt.schema_definition</code>.
     */
    public final TableField<XbtRecord, String> SCHEMA_DEFINITION = createField(DSL.name("schema_definition"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.xbt.module_id</code>.
     */
    public final TableField<XbtRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.xbt.release_id</code>.
     */
    public final TableField<XbtRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.xbt.revision_doc</code>.
     */
    public final TableField<XbtRecord, String> REVISION_DOC = createField(DSL.name("revision_doc"), org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>oagi.xbt.state</code>.
     */
    public final TableField<XbtRecord, Integer> STATE = createField(DSL.name("state"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>oagi.xbt.created_by</code>.
     */
    public final TableField<XbtRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.xbt.owner_user_id</code>.
     */
    public final TableField<XbtRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.xbt.last_updated_by</code>.
     */
    public final TableField<XbtRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.xbt.creation_timestamp</code>.
     */
    public final TableField<XbtRecord, Timestamp> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>oagi.xbt.last_update_timestamp</code>.
     */
    public final TableField<XbtRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>oagi.xbt.revision_num</code>.
     */
    public final TableField<XbtRecord, Integer> REVISION_NUM = createField(DSL.name("revision_num"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>oagi.xbt.revision_tracking_num</code>.
     */
    public final TableField<XbtRecord, Integer> REVISION_TRACKING_NUM = createField(DSL.name("revision_tracking_num"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>oagi.xbt.revision_action</code>.
     */
    public final TableField<XbtRecord, Byte> REVISION_ACTION = createField(DSL.name("revision_action"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * The column <code>oagi.xbt.current_xbt_id</code>.
     */
    public final TableField<XbtRecord, ULong> CURRENT_XBT_ID = createField(DSL.name("current_xbt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.xbt.is_deprecated</code>.
     */
    public final TableField<XbtRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "");

    /**
     * Create a <code>oagi.xbt</code> table reference
     */
    public Xbt() {
        this(DSL.name("xbt"), null);
    }

    /**
     * Create an aliased <code>oagi.xbt</code> table reference
     */
    public Xbt(String alias) {
        this(DSL.name(alias), XBT);
    }

    /**
     * Create an aliased <code>oagi.xbt</code> table reference
     */
    public Xbt(Name alias) {
        this(alias, XBT);
    }

    private Xbt(Name alias, Table<XbtRecord> aliased) {
        this(alias, aliased, null);
    }

    private Xbt(Name alias, Table<XbtRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table stores XML schema built-in types and OAGIS built-in types. OAGIS built-in types are those types defined in the XMLSchemaBuiltinType and the XMLSchemaBuiltinType Patterns schemas."));
    }

    public <O extends Record> Xbt(Table<O> child, ForeignKey<O, XbtRecord> key) {
        super(child, key, XBT);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.XBT_PRIMARY, Indexes.XBT_XBT_CREATED_BY_FK, Indexes.XBT_XBT_CURRENT_XBT_ID_FK, Indexes.XBT_XBT_LAST_UPDATED_BY_FK, Indexes.XBT_XBT_MODULE_ID_FK, Indexes.XBT_XBT_OWNER_USER_ID_FK, Indexes.XBT_XBT_RELEASE_ID_FK, Indexes.XBT_XBT_SUBTYPE_OF_XBT_ID_FK);
    }

    @Override
    public Identity<XbtRecord, ULong> getIdentity() {
        return Keys.IDENTITY_XBT;
    }

    @Override
    public UniqueKey<XbtRecord> getPrimaryKey() {
        return Keys.KEY_XBT_PRIMARY;
    }

    @Override
    public List<UniqueKey<XbtRecord>> getKeys() {
        return Arrays.<UniqueKey<XbtRecord>>asList(Keys.KEY_XBT_PRIMARY);
    }

    @Override
    public List<ForeignKey<XbtRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<XbtRecord, ?>>asList(Keys.XBT_SUBTYPE_OF_XBT_ID_FK, Keys.XBT_MODULE_ID_FK, Keys.XBT_RELEASE_ID_FK, Keys.XBT_CREATED_BY_FK, Keys.XBT_OWNER_USER_ID_FK, Keys.XBT_LAST_UPDATED_BY_FK, Keys.XBT_CURRENT_XBT_ID_FK);
    }

    public org.oagi.srt.entity.jooq.tables.Xbt xbtSubtypeOfXbtIdFk() {
        return new org.oagi.srt.entity.jooq.tables.Xbt(this, Keys.XBT_SUBTYPE_OF_XBT_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.XBT_MODULE_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.XBT_RELEASE_ID_FK);
    }

    public AppUser xbtCreatedByFk() {
        return new AppUser(this, Keys.XBT_CREATED_BY_FK);
    }

    public AppUser xbtOwnerUserIdFk() {
        return new AppUser(this, Keys.XBT_OWNER_USER_ID_FK);
    }

    public AppUser xbtLastUpdatedByFk() {
        return new AppUser(this, Keys.XBT_LAST_UPDATED_BY_FK);
    }

    public org.oagi.srt.entity.jooq.tables.Xbt xbtCurrentXbtIdFk() {
        return new org.oagi.srt.entity.jooq.tables.Xbt(this, Keys.XBT_CURRENT_XBT_ID_FK);
    }

    @Override
    public Xbt as(String alias) {
        return new Xbt(DSL.name(alias), this);
    }

    @Override
    public Xbt as(Name alias) {
        return new Xbt(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Xbt rename(String name) {
        return new Xbt(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Xbt rename(Name name) {
        return new Xbt(name, null);
    }

    // -------------------------------------------------------------------------
    // Row21 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row21<ULong, String, String, String, String, ULong, String, ULong, ULong, String, Integer, ULong, ULong, ULong, Timestamp, Timestamp, Integer, Integer, Byte, ULong, Byte> fieldsRow() {
        return (Row21) super.fieldsRow();
    }
}
