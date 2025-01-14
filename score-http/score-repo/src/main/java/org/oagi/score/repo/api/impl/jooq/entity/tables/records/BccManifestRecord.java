/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BccManifestRecord extends UpdatableRecordImpl<BccManifestRecord> implements Record10<ULong, ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bcc_manifest.bcc_manifest_id</code>.
     */
    public void setBccManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.bcc_manifest_id</code>.
     */
    public ULong getBccManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.bcc_id</code>.
     */
    public void setBccId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.bcc_id</code>.
     */
    public ULong getBccId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.seq_key_id</code>.
     */
    public void setSeqKeyId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.seq_key_id</code>.
     */
    public ULong getSeqKeyId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.from_acc_manifest_id</code>.
     */
    public void setFromAccManifestId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.from_acc_manifest_id</code>.
     */
    public ULong getFromAccManifestId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.to_bccp_manifest_id</code>.
     */
    public void setToBccpManifestId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.to_bccp_manifest_id</code>.
     */
    public ULong getToBccpManifestId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public void setConflict(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public Byte getConflict() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.replacement_bcc_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public void setReplacementBccManifestId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.replacement_bcc_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public ULong getReplacementBccManifestId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.prev_bcc_manifest_id</code>.
     */
    public void setPrevBccManifestId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.prev_bcc_manifest_id</code>.
     */
    public ULong getPrevBccManifestId() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.bcc_manifest.next_bcc_manifest_id</code>.
     */
    public void setNextBccManifestId(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bcc_manifest.next_bcc_manifest_id</code>.
     */
    public ULong getNextBccManifestId() {
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
    public Row10<ULong, ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Row10<ULong, ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return BccManifest.BCC_MANIFEST.BCC_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return BccManifest.BCC_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return BccManifest.BCC_MANIFEST.BCC_ID;
    }

    @Override
    public Field<ULong> field4() {
        return BccManifest.BCC_MANIFEST.SEQ_KEY_ID;
    }

    @Override
    public Field<ULong> field5() {
        return BccManifest.BCC_MANIFEST.FROM_ACC_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field6() {
        return BccManifest.BCC_MANIFEST.TO_BCCP_MANIFEST_ID;
    }

    @Override
    public Field<Byte> field7() {
        return BccManifest.BCC_MANIFEST.CONFLICT;
    }

    @Override
    public Field<ULong> field8() {
        return BccManifest.BCC_MANIFEST.REPLACEMENT_BCC_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field9() {
        return BccManifest.BCC_MANIFEST.PREV_BCC_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field10() {
        return BccManifest.BCC_MANIFEST.NEXT_BCC_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getBccManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getBccId();
    }

    @Override
    public ULong component4() {
        return getSeqKeyId();
    }

    @Override
    public ULong component5() {
        return getFromAccManifestId();
    }

    @Override
    public ULong component6() {
        return getToBccpManifestId();
    }

    @Override
    public Byte component7() {
        return getConflict();
    }

    @Override
    public ULong component8() {
        return getReplacementBccManifestId();
    }

    @Override
    public ULong component9() {
        return getPrevBccManifestId();
    }

    @Override
    public ULong component10() {
        return getNextBccManifestId();
    }

    @Override
    public ULong value1() {
        return getBccManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getBccId();
    }

    @Override
    public ULong value4() {
        return getSeqKeyId();
    }

    @Override
    public ULong value5() {
        return getFromAccManifestId();
    }

    @Override
    public ULong value6() {
        return getToBccpManifestId();
    }

    @Override
    public Byte value7() {
        return getConflict();
    }

    @Override
    public ULong value8() {
        return getReplacementBccManifestId();
    }

    @Override
    public ULong value9() {
        return getPrevBccManifestId();
    }

    @Override
    public ULong value10() {
        return getNextBccManifestId();
    }

    @Override
    public BccManifestRecord value1(ULong value) {
        setBccManifestId(value);
        return this;
    }

    @Override
    public BccManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public BccManifestRecord value3(ULong value) {
        setBccId(value);
        return this;
    }

    @Override
    public BccManifestRecord value4(ULong value) {
        setSeqKeyId(value);
        return this;
    }

    @Override
    public BccManifestRecord value5(ULong value) {
        setFromAccManifestId(value);
        return this;
    }

    @Override
    public BccManifestRecord value6(ULong value) {
        setToBccpManifestId(value);
        return this;
    }

    @Override
    public BccManifestRecord value7(Byte value) {
        setConflict(value);
        return this;
    }

    @Override
    public BccManifestRecord value8(ULong value) {
        setReplacementBccManifestId(value);
        return this;
    }

    @Override
    public BccManifestRecord value9(ULong value) {
        setPrevBccManifestId(value);
        return this;
    }

    @Override
    public BccManifestRecord value10(ULong value) {
        setNextBccManifestId(value);
        return this;
    }

    @Override
    public BccManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, ULong value6, Byte value7, ULong value8, ULong value9, ULong value10) {
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
     * Create a detached BccManifestRecord
     */
    public BccManifestRecord() {
        super(BccManifest.BCC_MANIFEST);
    }

    /**
     * Create a detached, initialised BccManifestRecord
     */
    public BccManifestRecord(ULong bccManifestId, ULong releaseId, ULong bccId, ULong seqKeyId, ULong fromAccManifestId, ULong toBccpManifestId, Byte conflict, ULong replacementBccManifestId, ULong prevBccManifestId, ULong nextBccManifestId) {
        super(BccManifest.BCC_MANIFEST);

        setBccManifestId(bccManifestId);
        setReleaseId(releaseId);
        setBccId(bccId);
        setSeqKeyId(seqKeyId);
        setFromAccManifestId(fromAccManifestId);
        setToBccpManifestId(toBccpManifestId);
        setConflict(conflict);
        setReplacementBccManifestId(replacementBccManifestId);
        setPrevBccManifestId(prevBccManifestId);
        setNextBccManifestId(nextBccManifestId);
    }
}
