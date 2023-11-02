/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.e2e.impl.api.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.e2e.impl.api.jooq.entity.tables.BccpManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccpManifestRecord extends UpdatableRecordImpl<BccpManifestRecord> implements Record10<ULong, ULong, ULong, ULong, String, Byte, ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bccp_manifest.bccp_manifest_id</code>.
     */
    public void setBccpManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.bccp_manifest_id</code>.
     */
    public ULong getBccpManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.bccp_id</code>.
     */
    public void setBccpId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.bccp_id</code>.
     */
    public ULong getBccpId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.bdt_manifest_id</code>.
     */
    public void setBdtManifestId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.bdt_manifest_id</code>.
     */
    public ULong getBdtManifestId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.den</code>. The dictionary entry name
     * of the BCCP. It is derived by PROPERTY_TERM + ". " + REPRESENTATION_TERM.
     */
    public void setDen(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.den</code>. The dictionary entry name
     * of the BCCP. It is derived by PROPERTY_TERM + ". " + REPRESENTATION_TERM.
     */
    public String getDen() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public void setConflict(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public Byte getConflict() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.log_id</code>. A foreign key pointed
     * to a log for the current record.
     */
    public void setLogId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.log_id</code>. A foreign key pointed
     * to a log for the current record.
     */
    public ULong getLogId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.replacement_bccp_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public void setReplacementBccpManifestId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.replacement_bccp_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public ULong getReplacementBccpManifestId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.prev_bccp_manifest_id</code>.
     */
    public void setPrevBccpManifestId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.prev_bccp_manifest_id</code>.
     */
    public ULong getPrevBccpManifestId() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.bccp_manifest.next_bccp_manifest_id</code>.
     */
    public void setNextBccpManifestId(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bccp_manifest.next_bccp_manifest_id</code>.
     */
    public ULong getNextBccpManifestId() {
        return (ULong) get(9);
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
    public Row10<ULong, ULong, ULong, ULong, String, Byte, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<ULong, ULong, ULong, ULong, String, Byte, ULong, ULong, ULong, ULong> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BccpManifest.BCCP_MANIFEST.BCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return BccpManifest.BCCP_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return BccpManifest.BCCP_MANIFEST.BCCP_ID;
    }

    @Override
    public Field<ULong> field4() {
        return BccpManifest.BCCP_MANIFEST.BDT_MANIFEST_ID;
    }

    @Override
    public Field<String> field5() {
        return BccpManifest.BCCP_MANIFEST.DEN;
    }

    @Override
    public Field<Byte> field6() {
        return BccpManifest.BCCP_MANIFEST.CONFLICT;
    }

    @Override
    public Field<ULong> field7() {
        return BccpManifest.BCCP_MANIFEST.LOG_ID;
    }

    @Override
    public Field<ULong> field8() {
        return BccpManifest.BCCP_MANIFEST.REPLACEMENT_BCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field9() {
        return BccpManifest.BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field10() {
        return BccpManifest.BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getBccpManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getBccpId();
    }

    @Override
    public ULong component4() {
        return getBdtManifestId();
    }

    @Override
    public String component5() {
        return getDen();
    }

    @Override
    public Byte component6() {
        return getConflict();
    }

    @Override
    public ULong component7() {
        return getLogId();
    }

    @Override
    public ULong component8() {
        return getReplacementBccpManifestId();
    }

    @Override
    public ULong component9() {
        return getPrevBccpManifestId();
    }

    @Override
    public ULong component10() {
        return getNextBccpManifestId();
    }

    @Override
    public ULong value1() {
        return getBccpManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getBccpId();
    }

    @Override
    public ULong value4() {
        return getBdtManifestId();
    }

    @Override
    public String value5() {
        return getDen();
    }

    @Override
    public Byte value6() {
        return getConflict();
    }

    @Override
    public ULong value7() {
        return getLogId();
    }

    @Override
    public ULong value8() {
        return getReplacementBccpManifestId();
    }

    @Override
    public ULong value9() {
        return getPrevBccpManifestId();
    }

    @Override
    public ULong value10() {
        return getNextBccpManifestId();
    }

    @Override
    public BccpManifestRecord value1(ULong value) {
        setBccpManifestId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value3(ULong value) {
        setBccpId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value4(ULong value) {
        setBdtManifestId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value5(String value) {
        setDen(value);
        return this;
    }

    @Override
    public BccpManifestRecord value6(Byte value) {
        setConflict(value);
        return this;
    }

    @Override
    public BccpManifestRecord value7(ULong value) {
        setLogId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value8(ULong value) {
        setReplacementBccpManifestId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value9(ULong value) {
        setPrevBccpManifestId(value);
        return this;
    }

    @Override
    public BccpManifestRecord value10(ULong value) {
        setNextBccpManifestId(value);
        return this;
    }

    @Override
    public BccpManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, String value5, Byte value6, ULong value7, ULong value8, ULong value9, ULong value10) {
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
     * Create a detached BccpManifestRecord
     */
    public BccpManifestRecord() {
        super(BccpManifest.BCCP_MANIFEST);
    }

    /**
     * Create a detached, initialised BccpManifestRecord
     */
    public BccpManifestRecord(ULong bccpManifestId, ULong releaseId, ULong bccpId, ULong bdtManifestId, String den, Byte conflict, ULong logId, ULong replacementBccpManifestId, ULong prevBccpManifestId, ULong nextBccpManifestId) {
        super(BccpManifest.BCCP_MANIFEST);

        setBccpManifestId(bccpManifestId);
        setReleaseId(releaseId);
        setBccpId(bccpId);
        setBdtManifestId(bdtManifestId);
        setDen(den);
        setConflict(conflict);
        setLogId(logId);
        setReplacementBccpManifestId(replacementBccpManifestId);
        setPrevBccpManifestId(prevBccpManifestId);
        setNextBccpManifestId(nextBccpManifestId);
        resetChangedOnNotNull();
    }
}
