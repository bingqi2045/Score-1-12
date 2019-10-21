/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.DtReleaseManifest;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtReleaseManifestRecord extends UpdatableRecordImpl<DtReleaseManifestRecord> implements Record3<ULong, ULong, ULong> {

    private static final long serialVersionUID = -591000214;

    /**
     * Setter for <code>oagi.dt_release_manifest.dt_release_manifest_id</code>.
     */
    public void setDtReleaseManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.dt_release_manifest.dt_release_manifest_id</code>.
     */
    public ULong getDtReleaseManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.dt_release_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.dt_release_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.dt_release_manifest.dt_id</code>.
     */
    public void setDtId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.dt_release_manifest.dt_id</code>.
     */
    public ULong getDtId() {
        return (ULong) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, ULong> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<ULong, ULong, ULong> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return DtReleaseManifest.DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return DtReleaseManifest.DT_RELEASE_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return DtReleaseManifest.DT_RELEASE_MANIFEST.DT_ID;
    }

    @Override
    public ULong component1() {
        return getDtReleaseManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getDtId();
    }

    @Override
    public ULong value1() {
        return getDtReleaseManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getDtId();
    }

    @Override
    public DtReleaseManifestRecord value1(ULong value) {
        setDtReleaseManifestId(value);
        return this;
    }

    @Override
    public DtReleaseManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public DtReleaseManifestRecord value3(ULong value) {
        setDtId(value);
        return this;
    }

    @Override
    public DtReleaseManifestRecord values(ULong value1, ULong value2, ULong value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DtReleaseManifestRecord
     */
    public DtReleaseManifestRecord() {
        super(DtReleaseManifest.DT_RELEASE_MANIFEST);
    }

    /**
     * Create a detached, initialised DtReleaseManifestRecord
     */
    public DtReleaseManifestRecord(ULong dtReleaseManifestId, ULong releaseId, ULong dtId) {
        super(DtReleaseManifest.DT_RELEASE_MANIFEST);

        set(0, dtReleaseManifestId);
        set(1, releaseId);
        set(2, dtId);
    }
}
