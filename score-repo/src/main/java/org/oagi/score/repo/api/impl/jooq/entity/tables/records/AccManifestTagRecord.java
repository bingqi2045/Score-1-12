/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AccManifestTag;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccManifestTagRecord extends UpdatableRecordImpl<AccManifestTagRecord> implements Record2<ULong, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.acc_manifest_tag.acc_manifest_id</code>.
     */
    public void setAccManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.acc_manifest_tag.acc_manifest_id</code>.
     */
    public ULong getAccManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.acc_manifest_tag.cc_tag_id</code>.
     */
    public void setCcTagId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.acc_manifest_tag.cc_tag_id</code>.
     */
    public String getCcTagId() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<ULong, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AccManifestTag.ACC_MANIFEST_TAG.ACC_MANIFEST_ID;
    }

    @Override
    public Field<String> field2() {
        return AccManifestTag.ACC_MANIFEST_TAG.CC_TAG_ID;
    }

    @Override
    public ULong component1() {
        return getAccManifestId();
    }

    @Override
    public String component2() {
        return getCcTagId();
    }

    @Override
    public ULong value1() {
        return getAccManifestId();
    }

    @Override
    public String value2() {
        return getCcTagId();
    }

    @Override
    public AccManifestTagRecord value1(ULong value) {
        setAccManifestId(value);
        return this;
    }

    @Override
    public AccManifestTagRecord value2(String value) {
        setCcTagId(value);
        return this;
    }

    @Override
    public AccManifestTagRecord values(ULong value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AccManifestTagRecord
     */
    public AccManifestTagRecord() {
        super(AccManifestTag.ACC_MANIFEST_TAG);
    }

    /**
     * Create a detached, initialised AccManifestTagRecord
     */
    public AccManifestTagRecord(ULong accManifestId, String ccTagId) {
        super(AccManifestTag.ACC_MANIFEST_TAG);

        setAccManifestId(accManifestId);
        setCcTagId(ccTagId);
    }
}
