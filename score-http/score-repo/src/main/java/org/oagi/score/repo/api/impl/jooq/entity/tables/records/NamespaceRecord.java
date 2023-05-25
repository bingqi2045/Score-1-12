/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Namespace;


/**
 * This table stores information about a namespace. Namespace is the namespace
 * as in the XML schema specification.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NamespaceRecord extends UpdatableRecordImpl<NamespaceRecord> implements Record10<ULong, String, String, String, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.namespace.namespace_id</code>. Primary, internal
     * database key.
     */
    public void setNamespaceId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.namespace.namespace_id</code>. Primary, internal
     * database key.
     */
    public ULong getNamespaceId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.namespace.uri</code>. This is the URI of the
     * namespace.
     */
    public void setUri(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.namespace.uri</code>. This is the URI of the
     * namespace.
     */
    public String getUri() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.namespace.prefix</code>. This is a default short
     * name to represent the URI. It may be overridden during the expression
     * generation. Null or empty means the same thing like the default prefix in
     * an XML schema.
     */
    public void setPrefix(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.namespace.prefix</code>. This is a default short
     * name to represent the URI. It may be overridden during the expression
     * generation. Null or empty means the same thing like the default prefix in
     * an XML schema.
     */
    public String getPrefix() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.namespace.description</code>. Description or
     * explanation about the namespace or use of the namespace.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.namespace.description</code>. Description or
     * explanation about the namespace or use of the namespace.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.namespace.is_std_nmsp</code>. This indicates
     * whether the namespace is reserved for standard used (i.e., whether it is
     * an OAGIS namespace). If it is true, then end users cannot user the
     * namespace for the end user CCs.
     */
    public void setIsStdNmsp(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.namespace.is_std_nmsp</code>. This indicates
     * whether the namespace is reserved for standard used (i.e., whether it is
     * an OAGIS namespace). If it is true, then end users cannot user the
     * namespace for the end user CCs.
     */
    public Byte getIsStdNmsp() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>oagi.namespace.owner_user_id</code>. Foreign key to the
     * APP_USER table identifying the user who can update or delete the record.
     */
    public void setOwnerUserId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.namespace.owner_user_id</code>. Foreign key to the
     * APP_USER table identifying the user who can update or delete the record.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.namespace.created_by</code>. Foreign key to the
     * APP_USER table identifying user who created the namespace.
     */
    public void setCreatedBy(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.namespace.created_by</code>. Foreign key to the
     * APP_USER table identifying user who created the namespace.
     */
    public ULong getCreatedBy() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.namespace.last_updated_by</code>. Foreign key to
     * the APP_USER table identifying the user who last updated the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.namespace.last_updated_by</code>. Foreign key to
     * the APP_USER table identifying the user who last updated the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.namespace.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.namespace.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>oagi.namespace.last_update_timestamp</code>. The
     * timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.namespace.last_update_timestamp</code>. The
     * timestamp when the record was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<ULong, String, String, String, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<ULong, String, String, String, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Namespace.NAMESPACE.NAMESPACE_ID;
    }

    @Override
    public Field<String> field2() {
        return Namespace.NAMESPACE.URI;
    }

    @Override
    public Field<String> field3() {
        return Namespace.NAMESPACE.PREFIX;
    }

    @Override
    public Field<String> field4() {
        return Namespace.NAMESPACE.DESCRIPTION;
    }

    @Override
    public Field<Byte> field5() {
        return Namespace.NAMESPACE.IS_STD_NMSP;
    }

    @Override
    public Field<ULong> field6() {
        return Namespace.NAMESPACE.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field7() {
        return Namespace.NAMESPACE.CREATED_BY;
    }

    @Override
    public Field<ULong> field8() {
        return Namespace.NAMESPACE.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return Namespace.NAMESPACE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return Namespace.NAMESPACE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getNamespaceId();
    }

    @Override
    public String component2() {
        return getUri();
    }

    @Override
    public String component3() {
        return getPrefix();
    }

    @Override
    public String component4() {
        return getDescription();
    }

    @Override
    public Byte component5() {
        return getIsStdNmsp();
    }

    @Override
    public ULong component6() {
        return getOwnerUserId();
    }

    @Override
    public ULong component7() {
        return getCreatedBy();
    }

    @Override
    public ULong component8() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component9() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component10() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value1() {
        return getNamespaceId();
    }

    @Override
    public String value2() {
        return getUri();
    }

    @Override
    public String value3() {
        return getPrefix();
    }

    @Override
    public String value4() {
        return getDescription();
    }

    @Override
    public Byte value5() {
        return getIsStdNmsp();
    }

    @Override
    public ULong value6() {
        return getOwnerUserId();
    }

    @Override
    public ULong value7() {
        return getCreatedBy();
    }

    @Override
    public ULong value8() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value9() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value10() {
        return getLastUpdateTimestamp();
    }

    @Override
    public NamespaceRecord value1(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public NamespaceRecord value2(String value) {
        setUri(value);
        return this;
    }

    @Override
    public NamespaceRecord value3(String value) {
        setPrefix(value);
        return this;
    }

    @Override
    public NamespaceRecord value4(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public NamespaceRecord value5(Byte value) {
        setIsStdNmsp(value);
        return this;
    }

    @Override
    public NamespaceRecord value6(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public NamespaceRecord value7(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public NamespaceRecord value8(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public NamespaceRecord value9(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public NamespaceRecord value10(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public NamespaceRecord values(ULong value1, String value2, String value3, String value4, Byte value5, ULong value6, ULong value7, ULong value8, LocalDateTime value9, LocalDateTime value10) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NamespaceRecord
     */
    public NamespaceRecord() {
        super(Namespace.NAMESPACE);
    }

    /**
     * Create a detached, initialised NamespaceRecord
     */
    public NamespaceRecord(ULong namespaceId, String uri, String prefix, String description, Byte isStdNmsp, ULong ownerUserId, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(Namespace.NAMESPACE);

        setNamespaceId(namespaceId);
        setUri(uri);
        setPrefix(prefix);
        setDescription(description);
        setIsStdNmsp(isStdNmsp);
        setOwnerUserId(ownerUserId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        resetChangedOnNotNull();
    }
}
