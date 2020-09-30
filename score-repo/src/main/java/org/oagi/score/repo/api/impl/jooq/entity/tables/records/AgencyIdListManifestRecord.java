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
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdListManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListManifestRecord extends UpdatableRecordImpl<AgencyIdListManifestRecord> implements Record9<ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong, ULong> {

    private static final long serialVersionUID = -948261583;

    /**
     * Setter for <code>oagi.agency_id_list_manifest.agency_id_list_manifest_id</code>.
     */
    public void setAgencyIdListManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.agency_id_list_manifest_id</code>.
     */
    public ULong getAgencyIdListManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.release_id</code>.
     */
    public void setReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.release_id</code>.
     */
    public ULong getReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.agency_id_list_id</code>.
     */
    public void setAgencyIdListId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.agency_id_list_id</code>.
     */
    public ULong getAgencyIdListId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.based_agency_id_list_manifest_id</code>.
     */
    public void setBasedAgencyIdListManifestId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.based_agency_id_list_manifest_id</code>.
     */
    public ULong getBasedAgencyIdListManifestId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.conflict</code>. This indicates that there is a conflict between self and relationship.
     */
    public void setConflict(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.conflict</code>. This indicates that there is a conflict between self and relationship.
     */
    public Byte getConflict() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public void setRevisionId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.revision_id</code>. A foreign key pointed to revision for the current record.
     */
    public ULong getRevisionId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.replaced_manifest_by</code>. This alternative refers to a replacement manifest if the record is deprecated.
     */
    public void setReplacedManifestBy(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.replaced_manifest_by</code>. This alternative refers to a replacement manifest if the record is deprecated.
     */
    public ULong getReplacedManifestBy() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.prev_agency_id_list_manifest_id</code>.
     */
    public void setPrevAgencyIdListManifestId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.prev_agency_id_list_manifest_id</code>.
     */
    public ULong getPrevAgencyIdListManifestId() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.agency_id_list_manifest.next_agency_id_list_manifest_id</code>.
     */
    public void setNextAgencyIdListManifestId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_manifest.next_agency_id_list_manifest_id</code>.
     */
    public ULong getNextAgencyIdListManifestId() {
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
    public Row9<ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong, ULong> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID;
    }

    @Override
    public Field<ULong> field4() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public Field<Byte> field5() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.CONFLICT;
    }

    @Override
    public Field<ULong> field6() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.REVISION_ID;
    }

    @Override
    public Field<ULong> field7() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.REPLACED_MANIFEST_BY;
    }

    @Override
    public Field<ULong> field8() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.PREV_AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field9() {
        return AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST.NEXT_AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getAgencyIdListManifestId();
    }

    @Override
    public ULong component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getAgencyIdListId();
    }

    @Override
    public ULong component4() {
        return getBasedAgencyIdListManifestId();
    }

    @Override
    public Byte component5() {
        return getConflict();
    }

    @Override
    public ULong component6() {
        return getRevisionId();
    }

    @Override
    public ULong component7() {
        return getReplacedManifestBy();
    }

    @Override
    public ULong component8() {
        return getPrevAgencyIdListManifestId();
    }

    @Override
    public ULong component9() {
        return getNextAgencyIdListManifestId();
    }

    @Override
    public ULong value1() {
        return getAgencyIdListManifestId();
    }

    @Override
    public ULong value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getAgencyIdListId();
    }

    @Override
    public ULong value4() {
        return getBasedAgencyIdListManifestId();
    }

    @Override
    public Byte value5() {
        return getConflict();
    }

    @Override
    public ULong value6() {
        return getRevisionId();
    }

    @Override
    public ULong value7() {
        return getReplacedManifestBy();
    }

    @Override
    public ULong value8() {
        return getPrevAgencyIdListManifestId();
    }

    @Override
    public ULong value9() {
        return getNextAgencyIdListManifestId();
    }

    @Override
    public AgencyIdListManifestRecord value1(ULong value) {
        setAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value2(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value3(ULong value) {
        setAgencyIdListId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value4(ULong value) {
        setBasedAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value5(Byte value) {
        setConflict(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value6(ULong value) {
        setRevisionId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value7(ULong value) {
        setReplacedManifestBy(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value8(ULong value) {
        setPrevAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord value9(ULong value) {
        setNextAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, Byte value5, ULong value6, ULong value7, ULong value8, ULong value9) {
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
     * Create a detached AgencyIdListManifestRecord
     */
    public AgencyIdListManifestRecord() {
        super(AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST);
    }

    /**
     * Create a detached, initialised AgencyIdListManifestRecord
     */
    public AgencyIdListManifestRecord(ULong agencyIdListManifestId, ULong releaseId, ULong agencyIdListId, ULong basedAgencyIdListManifestId, Byte conflict, ULong revisionId, ULong replacedManifestBy, ULong prevAgencyIdListManifestId, ULong nextAgencyIdListManifestId) {
        super(AgencyIdListManifest.AGENCY_ID_LIST_MANIFEST);

        set(0, agencyIdListManifestId);
        set(1, releaseId);
        set(2, agencyIdListId);
        set(3, basedAgencyIdListManifestId);
        set(4, conflict);
        set(5, revisionId);
        set(6, replacedManifestBy);
        set(7, prevAgencyIdListManifestId);
        set(8, nextAgencyIdListManifestId);
    }
}
