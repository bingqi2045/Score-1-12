/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CodeListManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListManifestRecord extends UpdatableRecordImpl<CodeListManifestRecord> implements Record8<ULong, ULong, ULong, ULong, ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = -476443398;

    /**
     * Setter for <code>oagi.code_list_manifest.code_list_manifest_id</code>.
     */
    public void setCodeListManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.code_list_manifest_id</code>.
     */
    public ULong getCodeListManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.module_id</code>.
     */
    public void setModuleId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.module_id</code>.
     */
    public ULong getModuleId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.code_list_id</code>.
     */
    public void setCodeListId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.code_list_id</code>.
     */
    public ULong getCodeListId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.based_code_list_manifest_id</code>.
     */
    public void setBasedCodeListManifestId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.based_code_list_manifest_id</code>.
     */
    public ULong getBasedCodeListManifestId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public void setRevisionId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public ULong getRevisionId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.prev_code_list_manifest_id</code>.
     */
    public void setPrevCodeListManifestId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.prev_code_list_manifest_id</code>.
     */
    public ULong getPrevCodeListManifestId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.next_code_list_manifest_id</code>.
     */
    public void setNextCodeListManifestId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.next_code_list_manifest_id</code>.
     */
    public ULong getNextCodeListManifestId() {
        return (ULong) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, ULong, ULong> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CodeListManifest.CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return CodeListManifest.CODE_LIST_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return CodeListManifest.CODE_LIST_MANIFEST.MODULE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return CodeListManifest.CODE_LIST_MANIFEST.CODE_LIST_ID;
    }

    @Override
    public Field<ULong> field5() {
        return CodeListManifest.CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field6() {
        return CodeListManifest.CODE_LIST_MANIFEST.REVISION_ID;
    }

    @Override
    public Field<ULong> field7() {
        return CodeListManifest.CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field8() {
        return CodeListManifest.CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getCodeListManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getModuleId();
    }

    @Override
    public ULong component4() {
        return getCodeListId();
    }

    @Override
    public ULong component5() {
        return getBasedCodeListManifestId();
    }

    @Override
    public ULong component6() {
        return getRevisionId();
    }

    @Override
    public ULong component7() {
        return getPrevCodeListManifestId();
    }

    @Override
    public ULong component8() {
        return getNextCodeListManifestId();
    }

    @Override
    public ULong value1() {
        return getCodeListManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getModuleId();
    }

    @Override
    public ULong value4() {
        return getCodeListId();
    }

    @Override
    public ULong value5() {
        return getBasedCodeListManifestId();
    }

    @Override
    public ULong value6() {
        return getRevisionId();
    }

    @Override
    public ULong value7() {
        return getPrevCodeListManifestId();
    }

    @Override
    public ULong value8() {
        return getNextCodeListManifestId();
    }

    @Override
    public CodeListManifestRecord value1(ULong value) {
        setCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value3(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value4(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value5(ULong value) {
        setBasedCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value6(ULong value) {
        setRevisionId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value7(ULong value) {
        setPrevCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value8(ULong value) {
        setNextCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, ULong value6, ULong value7, ULong value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CodeListManifestRecord
     */
    public CodeListManifestRecord() {
        super(CodeListManifest.CODE_LIST_MANIFEST);
    }

    /**
     * Create a detached, initialised CodeListManifestRecord
     */
    public CodeListManifestRecord(ULong codeListManifestId, ULong releaseId, ULong moduleId, ULong codeListId, ULong basedCodeListManifestId, ULong revisionId, ULong prevCodeListManifestId, ULong nextCodeListManifestId) {
        super(CodeListManifest.CODE_LIST_MANIFEST);

        set(0, codeListManifestId);
        set(1, releaseId);
        set(2, moduleId);
        set(3, codeListId);
        set(4, basedCodeListManifestId);
        set(5, revisionId);
        set(6, prevCodeListManifestId);
        set(7, nextCodeListManifestId);
    }
}
