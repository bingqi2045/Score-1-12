/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.XbtReleaseManifest;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class XbtReleaseManifestRecord extends UpdatableRecordImpl<XbtReleaseManifestRecord> implements Record4<ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = -65007409;

    /**
     * Setter for <code>oagi.xbt_release_manifest.xbt_release_manifest_id</code>.
     */
    public void setXbtReleaseManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.xbt_release_manifest.xbt_release_manifest_id</code>.
     */
    public ULong getXbtReleaseManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.xbt_release_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.xbt_release_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.xbt_release_manifest.module_id</code>.
     */
    public void setModuleId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.xbt_release_manifest.module_id</code>.
     */
    public ULong getModuleId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.xbt_release_manifest.xbt_id</code>.
     */
    public void setXbtId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.xbt_release_manifest.xbt_id</code>.
     */
    public ULong getXbtId() {
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
        return XbtReleaseManifest.XBT_RELEASE_MANIFEST.XBT_RELEASE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return XbtReleaseManifest.XBT_RELEASE_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return XbtReleaseManifest.XBT_RELEASE_MANIFEST.MODULE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return XbtReleaseManifest.XBT_RELEASE_MANIFEST.XBT_ID;
    }

    @Override
    public ULong component1() {
        return getXbtReleaseManifestId();
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
        return getXbtId();
    }

    @Override
    public ULong value1() {
        return getXbtReleaseManifestId();
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
        return getXbtId();
    }

    @Override
    public XbtReleaseManifestRecord value1(ULong value) {
        setXbtReleaseManifestId(value);
        return this;
    }

    @Override
    public XbtReleaseManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public XbtReleaseManifestRecord value3(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public XbtReleaseManifestRecord value4(ULong value) {
        setXbtId(value);
        return this;
    }

    @Override
    public XbtReleaseManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4) {
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
     * Create a detached XbtReleaseManifestRecord
     */
    public XbtReleaseManifestRecord() {
        super(XbtReleaseManifest.XBT_RELEASE_MANIFEST);
    }

    /**
     * Create a detached, initialised XbtReleaseManifestRecord
     */
    public XbtReleaseManifestRecord(ULong xbtReleaseManifestId, ULong releaseId, ULong moduleId, ULong xbtId) {
        super(XbtReleaseManifest.XBT_RELEASE_MANIFEST);

        set(0, xbtReleaseManifestId);
        set(1, releaseId);
        set(2, moduleId);
        set(3, xbtId);
    }
}
