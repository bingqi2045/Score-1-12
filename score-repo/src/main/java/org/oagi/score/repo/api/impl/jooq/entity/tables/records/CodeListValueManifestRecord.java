/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeListValueManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListValueManifestRecord extends UpdatableRecordImpl<CodeListValueManifestRecord> implements Record9<ULong, String, String, ULong, ULong, Byte, ULong, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for
     * <code>oagi.code_list_value_manifest.code_list_value_manifest_id</code>.
     */
    public void setCodeListValueManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_value_manifest.code_list_value_manifest_id</code>.
     */
    public ULong getCodeListValueManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.code_list_value_manifest.release_id</code>. Foreign
     * key to the RELEASE table.
     */
    public void setReleaseId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.release_id</code>. Foreign
     * key to the RELEASE table.
     */
    public String getReleaseId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.code_list_value_manifest.code_list_value_id</code>.
     */
    public void setCodeListValueId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.code_list_value_id</code>.
     */
    public String getCodeListValueId() {
        return (String) get(2);
    }

    /**
     * Setter for
     * <code>oagi.code_list_value_manifest.code_list_manifest_id</code>.
     */
    public void setCodeListManifestId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_value_manifest.code_list_manifest_id</code>.
     */
    public ULong getCodeListManifestId() {
        return (ULong) get(3);
    }

    /**
     * Setter for
     * <code>oagi.code_list_value_manifest.based_code_list_value_manifest_id</code>.
     */
    public void setBasedCodeListValueManifestId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_value_manifest.based_code_list_value_manifest_id</code>.
     */
    public ULong getBasedCodeListValueManifestId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.code_list_value_manifest.conflict</code>. This
     * indicates that there is a conflict between self and relationship.
     */
    public void setConflict(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.code_list_value_manifest.conflict</code>. This
     * indicates that there is a conflict between self and relationship.
     */
    public Byte getConflict() {
        return (Byte) get(5);
    }

    /**
     * Setter for
     * <code>oagi.code_list_value_manifest.replacement_code_list_value_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public void setReplacementCodeListValueManifestId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_value_manifest.replacement_code_list_value_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public ULong getReplacementCodeListValueManifestId() {
        return (ULong) get(6);
    }

    /**
     * Setter for
     * <code>oagi.code_list_value_manifest.prev_code_list_value_manifest_id</code>.
     */
    public void setPrevCodeListValueManifestId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_value_manifest.prev_code_list_value_manifest_id</code>.
     */
    public ULong getPrevCodeListValueManifestId() {
        return (ULong) get(7);
    }

    /**
     * Setter for
     * <code>oagi.code_list_value_manifest.next_code_list_value_manifest_id</code>.
     */
    public void setNextCodeListValueManifestId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_value_manifest.next_code_list_value_manifest_id</code>.
     */
    public ULong getNextCodeListValueManifestId() {
        return (ULong) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, String, String, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, String, String, ULong, ULong, Byte, ULong, ULong, ULong> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<String> field2() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<String> field3() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field5() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.BASED_CODE_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<Byte> field6() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.CONFLICT;
    }

    @Override
    public Field<ULong> field7() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.REPLACEMENT_CODE_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field8() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.PREV_CODE_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field9() {
        return CodeListValueManifest.CODE_LIST_VALUE_MANIFEST.NEXT_CODE_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getCodeListValueManifestId();
    }

    @Override
    public String component2() {
        return getReleaseId();
    }

    @Override
    public String component3() {
        return getCodeListValueId();
    }

    @Override
    public ULong component4() {
        return getCodeListManifestId();
    }

    @Override
    public ULong component5() {
        return getBasedCodeListValueManifestId();
    }

    @Override
    public Byte component6() {
        return getConflict();
    }

    @Override
    public ULong component7() {
        return getReplacementCodeListValueManifestId();
    }

    @Override
    public ULong component8() {
        return getPrevCodeListValueManifestId();
    }

    @Override
    public ULong component9() {
        return getNextCodeListValueManifestId();
    }

    @Override
    public ULong value1() {
        return getCodeListValueManifestId();
    }

    @Override
    public String value2() {
        return getReleaseId();
    }

    @Override
    public String value3() {
        return getCodeListValueId();
    }

    @Override
    public ULong value4() {
        return getCodeListManifestId();
    }

    @Override
    public ULong value5() {
        return getBasedCodeListValueManifestId();
    }

    @Override
    public Byte value6() {
        return getConflict();
    }

    @Override
    public ULong value7() {
        return getReplacementCodeListValueManifestId();
    }

    @Override
    public ULong value8() {
        return getPrevCodeListValueManifestId();
    }

    @Override
    public ULong value9() {
        return getNextCodeListValueManifestId();
    }

    @Override
    public CodeListValueManifestRecord value1(ULong value) {
        setCodeListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value2(String value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value3(String value) {
        setCodeListValueId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value4(ULong value) {
        setCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value5(ULong value) {
        setBasedCodeListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value6(Byte value) {
        setConflict(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value7(ULong value) {
        setReplacementCodeListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value8(ULong value) {
        setPrevCodeListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord value9(ULong value) {
        setNextCodeListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListValueManifestRecord values(ULong value1, String value2, String value3, ULong value4, ULong value5, Byte value6, ULong value7, ULong value8, ULong value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
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
    public CodeListValueManifestRecord(ULong codeListValueManifestId, String releaseId, String codeListValueId, ULong codeListManifestId, ULong basedCodeListValueManifestId, Byte conflict, ULong replacementCodeListValueManifestId, ULong prevCodeListValueManifestId, ULong nextCodeListValueManifestId) {
        super(CodeListValueManifest.CODE_LIST_VALUE_MANIFEST);

        setCodeListValueManifestId(codeListValueManifestId);
        setReleaseId(releaseId);
        setCodeListValueId(codeListValueId);
        setCodeListManifestId(codeListManifestId);
        setBasedCodeListValueManifestId(basedCodeListValueManifestId);
        setConflict(conflict);
        setReplacementCodeListValueManifestId(replacementCodeListValueManifestId);
        setPrevCodeListValueManifestId(prevCodeListValueManifestId);
        setNextCodeListValueManifestId(nextCodeListValueManifestId);
    }
}
