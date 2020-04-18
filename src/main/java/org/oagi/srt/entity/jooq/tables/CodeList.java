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
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row21;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.CodeListRecord;


/**
 * This table stores information about a code list. When a code list is derived 
 * from another code list, the whole set of code values belonging to the based 
 * code list will be copied.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeList extends TableImpl<CodeListRecord> {

    private static final long serialVersionUID = 820716012;

    /**
     * The reference instance of <code>oagi.code_list</code>
     */
    public static final CodeList CODE_LIST = new CodeList();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CodeListRecord> getRecordType() {
        return CodeListRecord.class;
    }

    /**
     * The column <code>oagi.code_list.code_list_id</code>. Internal, primary database key.
     */
    public final TableField<CodeListRecord, ULong> CODE_LIST_ID = createField(DSL.name("code_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.code_list.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public final TableField<CodeListRecord, String> GUID = createField(DSL.name("guid"), org.jooq.impl.SQLDataType.VARCHAR(41).nullable(false), this, "GUID of the code list. Per OAGIS, a GUID is of the form \"oagis-id-\" followed by a 32 Hex character sequence.");

    /**
     * The column <code>oagi.code_list.enum_type_guid</code>. In the OAGIS Model XML schema, a type, which keeps all the enumerated values, is  defined separately from the type that represents a code list. This only applies to some code lists. When that is the case, this column stores the GUID of that enumeration type.
     */
    public final TableField<CodeListRecord, String> ENUM_TYPE_GUID = createField(DSL.name("enum_type_guid"), org.jooq.impl.SQLDataType.VARCHAR(41), this, "In the OAGIS Model XML schema, a type, which keeps all the enumerated values, is  defined separately from the type that represents a code list. This only applies to some code lists. When that is the case, this column stores the GUID of that enumeration type.");

    /**
     * The column <code>oagi.code_list.name</code>. Name of the code list.
     */
    public final TableField<CodeListRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "Name of the code list.");

    /**
     * The column <code>oagi.code_list.list_id</code>. External identifier.
     */
    public final TableField<CodeListRecord, String> LIST_ID = createField(DSL.name("list_id"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "External identifier.");

    /**
     * The column <code>oagi.code_list.agency_id</code>. Foreign key to the AGENCY_ID_LIST_VALUE table. It indicates the organization which maintains the code list.
     */
    public final TableField<CodeListRecord, ULong> AGENCY_ID = createField(DSL.name("agency_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "Foreign key to the AGENCY_ID_LIST_VALUE table. It indicates the organization which maintains the code list.");

    /**
     * The column <code>oagi.code_list.version_id</code>. Code list version number.
     */
    public final TableField<CodeListRecord, String> VERSION_ID = createField(DSL.name("version_id"), org.jooq.impl.SQLDataType.VARCHAR(10).nullable(false), this, "Code list version number.");

    /**
     * The column <code>oagi.code_list.definition</code>. Description of the code list.
     */
    public final TableField<CodeListRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "Description of the code list.");

    /**
     * The column <code>oagi.code_list.remark</code>. Usage information about the code list.
     */
    public final TableField<CodeListRecord, String> REMARK = createField(DSL.name("remark"), org.jooq.impl.SQLDataType.VARCHAR(225), this, "Usage information about the code list.");

    /**
     * The column <code>oagi.code_list.definition_source</code>. This is typically a URL which indicates the source of the code list's DEFINITION.
     */
    public final TableField<CodeListRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "This is typically a URL which indicates the source of the code list's DEFINITION.");

    /**
     * The column <code>oagi.code_list.extensible_indicator</code>. This is a flag to indicate whether the code list is final and shall not be further derived.
     */
    public final TableField<CodeListRecord, Byte> EXTENSIBLE_INDICATOR = createField(DSL.name("extensible_indicator"), org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "This is a flag to indicate whether the code list is final and shall not be further derived.");

    /**
     * The column <code>oagi.code_list.is_deprecated</code>. Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public final TableField<CodeListRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).");

    /**
     * The column <code>oagi.code_list.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public final TableField<CodeListRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created the code list.");

    /**
     * The column <code>oagi.code_list.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public final TableField<CodeListRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.code_list.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public final TableField<CodeListRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the code list.");

    /**
     * The column <code>oagi.code_list.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public final TableField<CodeListRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "Timestamp when the code list was created.");

    /**
     * The column <code>oagi.code_list.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public final TableField<CodeListRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "Timestamp when the code list was last updated.");

    /**
     * The column <code>oagi.code_list.state</code>. Life cycle state of the code list. Possible values are Editing, Published, or Deleted. Only a code list in published state is available for derivation and for used by the CC and BIE. Once the code list is published, it cannot go back to Editing. A new version would have to be created.
     */
    public final TableField<CodeListRecord, String> STATE = createField(DSL.name("state"), org.jooq.impl.SQLDataType.VARCHAR(10), this, "Life cycle state of the code list. Possible values are Editing, Published, or Deleted. Only a code list in published state is available for derivation and for used by the CC and BIE. Once the code list is published, it cannot go back to Editing. A new version would have to be created.");

    /**
     * The column <code>oagi.code_list.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public final TableField<CodeListRecord, ULong> REVISION_ID = createField(DSL.name("revision_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A foreign key pointed to revision for the current record.");

    /**
     * The column <code>oagi.code_list.prev_code_list_id</code>. A self-foreign key to indicate the previous history record.
     */
    public final TableField<CodeListRecord, ULong> PREV_CODE_LIST_ID = createField(DSL.name("prev_code_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.code_list.next_code_list_id</code>. A self-foreign key to indicate the next history record.
     */
    public final TableField<CodeListRecord, ULong> NEXT_CODE_LIST_ID = createField(DSL.name("next_code_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "A self-foreign key to indicate the next history record.");

    /**
     * Create a <code>oagi.code_list</code> table reference
     */
    public CodeList() {
        this(DSL.name("code_list"), null);
    }

    /**
     * Create an aliased <code>oagi.code_list</code> table reference
     */
    public CodeList(String alias) {
        this(DSL.name(alias), CODE_LIST);
    }

    /**
     * Create an aliased <code>oagi.code_list</code> table reference
     */
    public CodeList(Name alias) {
        this(alias, CODE_LIST);
    }

    private CodeList(Name alias, Table<CodeListRecord> aliased) {
        this(alias, aliased, null);
    }

    private CodeList(Name alias, Table<CodeListRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table stores information about a code list. When a code list is derived from another code list, the whole set of code values belonging to the based code list will be copied."), TableOptions.table());
    }

    public <O extends Record> CodeList(Table<O> child, ForeignKey<O, CodeListRecord> key) {
        super(child, key, CODE_LIST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<CodeListRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CODE_LIST;
    }

    @Override
    public UniqueKey<CodeListRecord> getPrimaryKey() {
        return Keys.KEY_CODE_LIST_PRIMARY;
    }

    @Override
    public List<UniqueKey<CodeListRecord>> getKeys() {
        return Arrays.<UniqueKey<CodeListRecord>>asList(Keys.KEY_CODE_LIST_PRIMARY);
    }

    @Override
    public List<ForeignKey<CodeListRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CodeListRecord, ?>>asList(Keys.CODE_LIST_AGENCY_ID_FK, Keys.CODE_LIST_CREATED_BY_FK, Keys.CODE_LIST_OWNER_USER_ID_FK, Keys.CODE_LIST_LAST_UPDATED_BY_FK, Keys.CODE_LIST_REVISION_ID_FK, Keys.CODE_LIST_PREV_CODE_LIST_ID_FK, Keys.CODE_LIST_NEXT_CODE_LIST_ID_FK);
    }

    public AgencyIdListValue agencyIdListValue() {
        return new AgencyIdListValue(this, Keys.CODE_LIST_AGENCY_ID_FK);
    }

    public AppUser codeListCreatedByFk() {
        return new AppUser(this, Keys.CODE_LIST_CREATED_BY_FK);
    }

    public AppUser codeListOwnerUserIdFk() {
        return new AppUser(this, Keys.CODE_LIST_OWNER_USER_ID_FK);
    }

    public AppUser codeListLastUpdatedByFk() {
        return new AppUser(this, Keys.CODE_LIST_LAST_UPDATED_BY_FK);
    }

    public Revision revision() {
        return new Revision(this, Keys.CODE_LIST_REVISION_ID_FK);
    }

    public CodeList codeListPrevCodeListIdFk() {
        return new CodeList(this, Keys.CODE_LIST_PREV_CODE_LIST_ID_FK);
    }

    public CodeList codeListNextCodeListIdFk() {
        return new CodeList(this, Keys.CODE_LIST_NEXT_CODE_LIST_ID_FK);
    }

    @Override
    public CodeList as(String alias) {
        return new CodeList(DSL.name(alias), this);
    }

    @Override
    public CodeList as(Name alias) {
        return new CodeList(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeList rename(String name) {
        return new CodeList(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeList rename(Name name) {
        return new CodeList(name, null);
    }

    // -------------------------------------------------------------------------
    // Row21 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row21<ULong, String, String, String, String, ULong, String, String, String, String, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong, ULong> fieldsRow() {
        return (Row21) super.fieldsRow();
    }
}
