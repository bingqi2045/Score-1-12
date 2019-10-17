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
import org.oagi.srt.entity.jooq.tables.AccReleaseManifest;


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
public class AccReleaseManifestRecord extends UpdatableRecordImpl<AccReleaseManifestRecord> implements Record4<ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = 864076990;

    /**
     * Setter for <code>oagi.acc_release_manifest.acc_release_manifest_id</code>.
     */
    public void setAccReleaseManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.acc_release_manifest.acc_release_manifest_id</code>.
     */
    public ULong getAccReleaseManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.acc_release_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.acc_release_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.acc_release_manifest.acc_id</code>.
     */
    public void setAccId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.acc_release_manifest.acc_id</code>.
     */
    public ULong getAccId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.acc_release_manifest.based_acc_id</code>.
     */
    public void setBasedAccId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.acc_release_manifest.based_acc_id</code>.
     */
    public ULong getBasedAccId() {
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
        return AccReleaseManifest.ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return AccReleaseManifest.ACC_RELEASE_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return AccReleaseManifest.ACC_RELEASE_MANIFEST.ACC_ID;
    }

    @Override
    public Field<ULong> field4() {
        return AccReleaseManifest.ACC_RELEASE_MANIFEST.BASED_ACC_ID;
    }

    @Override
    public ULong component1() {
        return getAccReleaseManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getAccId();
    }

    @Override
    public ULong component4() {
        return getBasedAccId();
    }

    @Override
    public ULong value1() {
        return getAccReleaseManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getAccId();
    }

    @Override
    public ULong value4() {
        return getBasedAccId();
    }

    @Override
    public AccReleaseManifestRecord value1(ULong value) {
        setAccReleaseManifestId(value);
        return this;
    }

    @Override
    public AccReleaseManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public AccReleaseManifestRecord value3(ULong value) {
        setAccId(value);
        return this;
    }

    @Override
    public AccReleaseManifestRecord value4(ULong value) {
        setBasedAccId(value);
        return this;
    }

    @Override
    public AccReleaseManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4) {
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
     * Create a detached AccReleaseManifestRecord
     */
    public AccReleaseManifestRecord() {
        super(AccReleaseManifest.ACC_RELEASE_MANIFEST);
    }

    /**
     * Create a detached, initialised AccReleaseManifestRecord
     */
    public AccReleaseManifestRecord(ULong accReleaseManifestId, ULong releaseId, ULong accId, ULong basedAccId) {
        super(AccReleaseManifest.ACC_RELEASE_MANIFEST);

        set(0, accReleaseManifestId);
        set(1, releaseId);
        set(2, accId);
        set(3, basedAccId);
    }
}
