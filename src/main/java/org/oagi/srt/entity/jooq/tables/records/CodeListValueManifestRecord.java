/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CodeListValueManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class CodeListValueManifestRecord extends UpdatableRecordImpl<CodeListValueManifestRecord> implements Record4<ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = -453375575;

    /**
     * Setter for <code>oagi.code_list_value_manifest.code_list_value_manifest_id</code>.
     */
    public void setCodeListValueManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.code_list_value_manifest_id</code>.
     */
    public ULong getCodeListValueManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.code_list_value_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.code_list_value_manifest.code_list_value_id</code>.
     */
    public void setCodeListValueId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.code_list_value_id</code>.
     */
    public ULong getCodeListValueId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.code_list_value_manifest.code_list_manifest_id</code>.
     */
    public void setCodeListManifestId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.code_list_manifest_id</code>.
     */
    public ULong getCodeListManifestId() {
        return (ULong) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, ULong, ULong, ULong> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getCodeListValueManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getCodeListValueId();
    }

    @Override
    public ULong component4() {
        return getCodeListManifestId();
    }

    @Override
    public ULong value1() {
        return getCodeListValueManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getCodeListValueId();
    }

    @Override
    public ULong value4() {
        return getCodeListManifestId();
    }

    @Override
    public CodeListValueManifestRecord value1(ULong value) {
        setCodeListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value3(ULong value) {
        setCodeListValueId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value4(ULong value) {
        setCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CodeListValueManifestRecord
     */
    public CodeListValueManifestRecord() {
        super(CodeListValueManifest.CODE_LIST_VALUE_MANIFEST);
    }

    /**
     * Create a detached, initialised CodeListValueManifestRecord
     */
    public CodeListValueManifestRecord(ULong codeListValueManifestId, ULong releaseId, ULong codeListValueId, ULong codeListManifestId) {
        super(CodeListValueManifest.CODE_LIST_VALUE_MANIFEST);

        set(0, codeListValueManifestId);
        set(1, releaseId);
        set(2, codeListValueId);
        set(3, codeListManifestId);
    }
}
