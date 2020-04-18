/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Xbt;

import java.time.LocalDateTime;


/**
 * This table stores XML schema built-in types and OAGIS built-in types. OAGIS
 * built-in types are those types defined in the XMLSchemaBuiltinType and
 * the XMLSchemaBuiltinType Patterns schemas.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class XbtRecord extends UpdatableRecordImpl<XbtRecord> implements Record17<ULong, String, String, String, String, String, ULong, String, String, Integer, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, Byte> {

    private static final long serialVersionUID = 341833768;

    /**
     * Setter for <code>oagi.xbt.xbt_id</code>. Primary, internal database key.
     */
    public void setXbtId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.xbt.xbt_id</code>. Primary, internal database key.
     */
    public ULong getXbtId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.xbt.guid</code>. A globally unique identifier (GUID) of an XBT. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.xbt.guid</code>. A globally unique identifier (GUID) of an XBT. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.xbt.name</code>. Human understandable name of the built-in type.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.xbt.name</code>. Human understandable name of the built-in type.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.xbt.builtIn_type</code>. Built-in type as it should appear in the XML schema including the namespace prefix. Namespace prefix for the XML schema namespace is assumed to be 'xsd' and a default prefix for the OAGIS built-int type.
     */
    public void setBuiltinType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.xbt.builtIn_type</code>. Built-in type as it should appear in the XML schema including the namespace prefix. Namespace prefix for the XML schema namespace is assumed to be 'xsd' and a default prefix for the OAGIS built-int type.
     */
    public String getBuiltinType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.xbt.jbt_draft05_map</code>.
     */
    public void setJbtDraft05Map(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.xbt.jbt_draft05_map</code>.
     */
    public String getJbtDraft05Map() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.xbt.openapi30_map</code>.
     */
    public void setOpenapi30Map(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.xbt.openapi30_map</code>.
     */
    public String getOpenapi30Map() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.xbt.subtype_of_xbt_id</code>. Foreign key to the XBT table itself. It indicates a super type of this XSD built-in type.
     */
    public void setSubtypeOfXbtId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.xbt.subtype_of_xbt_id</code>. Foreign key to the XBT table itself. It indicates a super type of this XSD built-in type.
     */
    public ULong getSubtypeOfXbtId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.xbt.schema_definition</code>.
     */
    public void setSchemaDefinition(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.xbt.schema_definition</code>.
     */
    public String getSchemaDefinition() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.xbt.revision_doc</code>.
     */
    public void setRevisionDoc(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.xbt.revision_doc</code>.
     */
    public String getRevisionDoc() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.xbt.state</code>.
     */
    public void setState(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.xbt.state</code>.
     */
    public Integer getState() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>oagi.xbt.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public void setRevisionId(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.xbt.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public ULong getRevisionId() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.xbt.created_by</code>.
     */
    public void setCreatedBy(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.xbt.created_by</code>.
     */
    public ULong getCreatedBy() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.xbt.owner_user_id</code>.
     */
    public void setOwnerUserId(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.xbt.owner_user_id</code>.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.xbt.last_updated_by</code>.
     */
    public void setLastUpdatedBy(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.xbt.last_updated_by</code>.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.xbt.creation_timestamp</code>.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.xbt.creation_timestamp</code>.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>oagi.xbt.last_update_timestamp</code>.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.xbt.last_update_timestamp</code>.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>oagi.xbt.is_deprecated</code>.
     */
    public void setIsDeprecated(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.xbt.is_deprecated</code>.
     */
    public Byte getIsDeprecated() {
        return (Byte) get(16);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record17 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row17<ULong, String, String, String, String, String, ULong, String, String, Integer, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, Byte> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    @Override
    public Row17<ULong, String, String, String, String, String, ULong, String, String, Integer, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime, Byte> valuesRow() {
        return (Row17) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Xbt.XBT.XBT_ID;
    }

    @Override
    public Field<String> field2() {
        return Xbt.XBT.GUID;
    }

    @Override
    public Field<String> field3() {
        return Xbt.XBT.NAME;
    }

    @Override
    public Field<String> field4() {
        return Xbt.XBT.BUILTIN_TYPE;
    }

    @Override
    public Field<String> field5() {
        return Xbt.XBT.JBT_DRAFT05_MAP;
    }

    @Override
    public Field<String> field6() {
        return Xbt.XBT.OPENAPI30_MAP;
    }

    @Override
    public Field<ULong> field7() {
        return Xbt.XBT.SUBTYPE_OF_XBT_ID;
    }

    @Override
    public Field<String> field8() {
        return Xbt.XBT.SCHEMA_DEFINITION;
    }

    @Override
    public Field<String> field9() {
        return Xbt.XBT.REVISION_DOC;
    }

    @Override
    public Field<Integer> field10() {
        return Xbt.XBT.STATE;
    }

    @Override
    public Field<ULong> field11() {
        return Xbt.XBT.REVISION_ID;
    }

    @Override
    public Field<ULong> field12() {
        return Xbt.XBT.CREATED_BY;
    }

    @Override
    public Field<ULong> field13() {
        return Xbt.XBT.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field14() {
        return Xbt.XBT.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return Xbt.XBT.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return Xbt.XBT.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<Byte> field17() {
        return Xbt.XBT.IS_DEPRECATED;
    }

    @Override
    public ULong component1() {
        return getXbtId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getBuiltinType();
    }

    @Override
    public String component5() {
        return getJbtDraft05Map();
    }

    @Override
    public String component6() {
        return getOpenapi30Map();
    }

    @Override
    public ULong component7() {
        return getSubtypeOfXbtId();
    }

    @Override
    public String component8() {
        return getSchemaDefinition();
    }

    @Override
    public String component9() {
        return getRevisionDoc();
    }

    @Override
    public Integer component10() {
        return getState();
    }

    @Override
    public ULong component11() {
        return getRevisionId();
    }

    @Override
    public ULong component12() {
        return getCreatedBy();
    }

    @Override
    public ULong component13() {
        return getOwnerUserId();
    }

    @Override
    public ULong component14() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component15() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component16() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Byte component17() {
        return getIsDeprecated();
    }

    @Override
    public ULong value1() {
        return getXbtId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getBuiltinType();
    }

    @Override
    public String value5() {
        return getJbtDraft05Map();
    }

    @Override
    public String value6() {
        return getOpenapi30Map();
    }

    @Override
    public ULong value7() {
        return getSubtypeOfXbtId();
    }

    @Override
    public String value8() {
        return getSchemaDefinition();
    }

    @Override
    public String value9() {
        return getRevisionDoc();
    }

    @Override
    public Integer value10() {
        return getState();
    }

    @Override
    public ULong value11() {
        return getRevisionId();
    }

    @Override
    public ULong value12() {
        return getCreatedBy();
    }

    @Override
    public ULong value13() {
        return getOwnerUserId();
    }

    @Override
    public ULong value14() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value15() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value16() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Byte value17() {
        return getIsDeprecated();
    }

    @Override
    public XbtRecord value1(ULong value) {
        setXbtId(value);
        return this;
    }

    @Override
    public XbtRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public XbtRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public XbtRecord value4(String value) {
        setBuiltinType(value);
        return this;
    }

    @Override
    public XbtRecord value5(String value) {
        setJbtDraft05Map(value);
        return this;
    }

    @Override
    public XbtRecord value6(String value) {
        setOpenapi30Map(value);
        return this;
    }

    @Override
    public XbtRecord value7(ULong value) {
        setSubtypeOfXbtId(value);
        return this;
    }

    @Override
    public XbtRecord value8(String value) {
        setSchemaDefinition(value);
        return this;
    }

    @Override
    public XbtRecord value9(String value) {
        setRevisionDoc(value);
        return this;
    }

    @Override
    public XbtRecord value10(Integer value) {
        setState(value);
        return this;
    }

    @Override
    public XbtRecord value11(ULong value) {
        setRevisionId(value);
        return this;
    }

    @Override
    public XbtRecord value12(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public XbtRecord value13(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public XbtRecord value14(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public XbtRecord value15(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public XbtRecord value16(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public XbtRecord value17(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public XbtRecord values(ULong value1, String value2, String value3, String value4, String value5, String value6, ULong value7, String value8, String value9, Integer value10, ULong value11, ULong value12, ULong value13, ULong value14, LocalDateTime value15, LocalDateTime value16, Byte value17) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached XbtRecord
     */
    public XbtRecord() {
        super(Xbt.XBT);
    }

    /**
     * Create a detached, initialised XbtRecord
     */
    public XbtRecord(ULong xbtId, String guid, String name, String builtinType, String jbtDraft05Map, String openapi30Map, ULong subtypeOfXbtId, String schemaDefinition, String revisionDoc, Integer state, ULong revisionId, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, Byte isDeprecated) {
        super(Xbt.XBT);

        set(0, xbtId);
        set(1, guid);
        set(2, name);
        set(3, builtinType);
        set(4, jbtDraft05Map);
        set(5, openapi30Map);
        set(6, subtypeOfXbtId);
        set(7, schemaDefinition);
        set(8, revisionDoc);
        set(9, state);
        set(10, revisionId);
        set(11, createdBy);
        set(12, ownerUserId);
        set(13, lastUpdatedBy);
        set(14, creationTimestamp);
        set(15, lastUpdateTimestamp);
        set(16, isDeprecated);
    }
}
