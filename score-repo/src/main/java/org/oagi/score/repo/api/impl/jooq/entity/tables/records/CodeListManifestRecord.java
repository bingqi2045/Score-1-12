/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeListManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListManifestRecord extends UpdatableRecordImpl<CodeListManifestRecord> implements Record10<String, String, String, String, String, Byte, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.code_list_manifest.code_list_manifest_id</code>.
     * Primary, internal database key.
     */
    public void setCodeListManifestId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.code_list_manifest_id</code>.
     * Primary, internal database key.
     */
    public String getCodeListManifestId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.release_id</code>.
     */
    public void setReleaseId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.release_id</code>.
     */
    public String getReleaseId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.code_list_id</code>.
     */
    public void setCodeListId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.code_list_id</code>.
     */
    public String getCodeListId() {
        return (String) get(2);
    }

    /**
     * Setter for
     * <code>oagi.code_list_manifest.based_code_list_manifest_id</code>.
     */
    public void setBasedCodeListManifestId(String value) {
        set(3, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_manifest.based_code_list_manifest_id</code>.
     */
    public String getBasedCodeListManifestId() {
        return (String) get(3);
    }

    /**
     * Setter for
     * <code>oagi.code_list_manifest.agency_id_list_value_manifest_id</code>.
     */
    public void setAgencyIdListValueManifestId(String value) {
        set(4, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_manifest.agency_id_list_value_manifest_id</code>.
     */
    public String getAgencyIdListValueManifestId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.conflict</code>. This indicates
     * that there is a conflict between self and relationship.
     */
    public void setConflict(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.conflict</code>. This indicates
     * that there is a conflict between self and relationship.
     */
    public Byte getConflict() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>oagi.code_list_manifest.log_id</code>. A foreign key
     * pointed to a log for the current record.
     */
    public void setLogId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.code_list_manifest.log_id</code>. A foreign key
     * pointed to a log for the current record.
     */
    public String getLogId() {
        return (String) get(6);
    }

    /**
     * Setter for
     * <code>oagi.code_list_manifest.replacement_code_list_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public void setReplacementCodeListManifestId(String value) {
        set(7, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_manifest.replacement_code_list_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public String getReplacementCodeListManifestId() {
        return (String) get(7);
    }

    /**
     * Setter for
     * <code>oagi.code_list_manifest.prev_code_list_manifest_id</code>.
     */
    public void setPrevCodeListManifestId(String value) {
        set(8, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_manifest.prev_code_list_manifest_id</code>.
     */
    public String getPrevCodeListManifestId() {
        return (String) get(8);
    }

    /**
     * Setter for
     * <code>oagi.code_list_manifest.next_code_list_manifest_id</code>.
     */
    public void setNextCodeListManifestId(String value) {
        set(9, value);
    }

    /**
     * Getter for
     * <code>oagi.code_list_manifest.next_code_list_manifest_id</code>.
     */
    public String getNextCodeListManifestId() {
        return (String) get(9);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, Byte, String, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<String, String, String, String, String, Byte, String, String, String, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return CodeListManifest.CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<String> field2() {
        return CodeListManifest.CODE_LIST_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<String> field3() {
        return CodeListManifest.CODE_LIST_MANIFEST.CODE_LIST_ID;
    }

    @Override
    public Field<String> field4() {
        return CodeListManifest.CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<String> field5() {
        return CodeListManifest.CODE_LIST_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<Byte> field6() {
        return CodeListManifest.CODE_LIST_MANIFEST.CONFLICT;
    }

    @Override
    public Field<String> field7() {
        return CodeListManifest.CODE_LIST_MANIFEST.LOG_ID;
    }

    @Override
    public Field<String> field8() {
        return CodeListManifest.CODE_LIST_MANIFEST.REPLACEMENT_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<String> field9() {
        return CodeListManifest.CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public Field<String> field10() {
        return CodeListManifest.CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID;
    }

    @Override
    public String component1() {
        return getCodeListManifestId();
    }

    @Override
    public String component2() {
        return getReleaseId();
    }

    @Override
    public String component3() {
        return getCodeListId();
    }

    @Override
    public String component4() {
        return getBasedCodeListManifestId();
    }

    @Override
    public String component5() {
        return getAgencyIdListValueManifestId();
    }

    @Override
    public Byte component6() {
        return getConflict();
    }

    @Override
    public String component7() {
        return getLogId();
    }

    @Override
    public String component8() {
        return getReplacementCodeListManifestId();
    }

    @Override
    public String component9() {
        return getPrevCodeListManifestId();
    }

    @Override
    public String component10() {
        return getNextCodeListManifestId();
    }

    @Override
    public String value1() {
        return getCodeListManifestId();
    }

    @Override
    public String value2() {
        return getReleaseId();
    }

    @Override
    public String value3() {
        return getCodeListId();
    }

    @Override
    public String value4() {
        return getBasedCodeListManifestId();
    }

    @Override
    public String value5() {
        return getAgencyIdListValueManifestId();
    }

    @Override
    public Byte value6() {
        return getConflict();
    }

    @Override
    public String value7() {
        return getLogId();
    }

    @Override
    public String value8() {
        return getReplacementCodeListManifestId();
    }

    @Override
    public String value9() {
        return getPrevCodeListManifestId();
    }

    @Override
    public String value10() {
        return getNextCodeListManifestId();
    }

    @Override
    public CodeListManifestRecord value1(String value) {
        setCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value2(String value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value3(String value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value4(String value) {
        setBasedCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value5(String value) {
        setAgencyIdListValueManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value6(Byte value) {
        setConflict(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value7(String value) {
        setLogId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value8(String value) {
        setReplacementCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value9(String value) {
        setPrevCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord value10(String value) {
        setNextCodeListManifestId(value);
        return this;
    }

    @Override
    public CodeListManifestRecord values(String value1, String value2, String value3, String value4, String value5, Byte value6, String value7, String value8, String value9, String value10) {
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
     * Create a detached CodeListManifestRecord
     */
    public CodeListManifestRecord() {
        super(CodeListManifest.CODE_LIST_MANIFEST);
    }

    /**
     * Create a detached, initialised CodeListManifestRecord
     */
    public CodeListManifestRecord(String codeListManifestId, String releaseId, String codeListId, String basedCodeListManifestId, String agencyIdListValueManifestId, Byte conflict, String logId, String replacementCodeListManifestId, String prevCodeListManifestId, String nextCodeListManifestId) {
        super(CodeListManifest.CODE_LIST_MANIFEST);

        setCodeListManifestId(codeListManifestId);
        setReleaseId(releaseId);
        setCodeListId(codeListId);
        setBasedCodeListManifestId(basedCodeListManifestId);
        setAgencyIdListValueManifestId(agencyIdListValueManifestId);
        setConflict(conflict);
        setLogId(logId);
        setReplacementCodeListManifestId(replacementCodeListManifestId);
        setPrevCodeListManifestId(prevCodeListManifestId);
        setNextCodeListManifestId(nextCodeListManifestId);
    }
}
