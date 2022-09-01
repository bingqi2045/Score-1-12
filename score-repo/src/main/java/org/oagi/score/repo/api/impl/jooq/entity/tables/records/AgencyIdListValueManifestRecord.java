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
import org.oagi.score.repo.api.impl.jooq.entity.tables.AgencyIdListValueManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AgencyIdListValueManifestRecord extends UpdatableRecordImpl<AgencyIdListValueManifestRecord> implements Record9<ULong, String, ULong, ULong, ULong, Byte, ULong, ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.agency_id_list_value_manifest_id</code>.
     */
    public void setAgencyIdListValueManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.agency_id_list_value_manifest_id</code>.
     */
    public ULong getAgencyIdListValueManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value_manifest.release_id</code>.
     * Foreign key to the RELEASE table.
     */
    public void setReleaseId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value_manifest.release_id</code>.
     * Foreign key to the RELEASE table.
     */
    public String getReleaseId() {
        return (String) get(1);
    }

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.agency_id_list_value_id</code>.
     */
    public void setAgencyIdListValueId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.agency_id_list_value_id</code>.
     */
    public ULong getAgencyIdListValueId() {
        return (ULong) get(2);
    }

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.agency_id_list_manifest_id</code>.
     */
    public void setAgencyIdListManifestId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.agency_id_list_manifest_id</code>.
     */
    public ULong getAgencyIdListManifestId() {
        return (ULong) get(3);
    }

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.based_agency_id_list_value_manifest_id</code>.
     */
    public void setBasedAgencyIdListValueManifestId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.based_agency_id_list_value_manifest_id</code>.
     */
    public ULong getBasedAgencyIdListValueManifestId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.agency_id_list_value_manifest.conflict</code>. This
     * indicates that there is a conflict between self and relationship.
     */
    public void setConflict(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.agency_id_list_value_manifest.conflict</code>. This
     * indicates that there is a conflict between self and relationship.
     */
    public Byte getConflict() {
        return (Byte) get(5);
    }

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.replacement_agency_id_list_value_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public void setReplacementAgencyIdListValueManifestId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.replacement_agency_id_list_value_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public ULong getReplacementAgencyIdListValueManifestId() {
        return (ULong) get(6);
    }

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.prev_agency_id_list_value_manifest_id</code>.
     */
    public void setPrevAgencyIdListValueManifestId(ULong value) {
        set(7, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.prev_agency_id_list_value_manifest_id</code>.
     */
    public ULong getPrevAgencyIdListValueManifestId() {
        return (ULong) get(7);
    }

    /**
     * Setter for
     * <code>oagi.agency_id_list_value_manifest.next_agency_id_list_value_manifest_id</code>.
     */
    public void setNextAgencyIdListValueManifestId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for
     * <code>oagi.agency_id_list_value_manifest.next_agency_id_list_value_manifest_id</code>.
     */
    public ULong getNextAgencyIdListValueManifestId() {
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
    public Row9<ULong, String, ULong, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, String, ULong, ULong, ULong, Byte, ULong, ULong, ULong> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<String> field2() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_VALUE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field5() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.BASED_AGENCY_ID_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<Byte> field6() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.CONFLICT;
    }

    @Override
    public Field<ULong> field7() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.REPLACEMENT_AGENCY_ID_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field8() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.PREV_AGENCY_ID_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field9() {
        return AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST.NEXT_AGENCY_ID_LIST_VALUE_MANIFEST_ID;
    }

    @Override
    public ULong component1() {
        return getAgencyIdListValueManifestId();
    }

    @Override
    public String component2() {
        return getReleaseId();
    }

    @Override
    public ULong component3() {
        return getAgencyIdListValueId();
    }

    @Override
    public ULong component4() {
        return getAgencyIdListManifestId();
    }

    @Override
    public ULong component5() {
        return getBasedAgencyIdListValueManifestId();
    }

    @Override
    public Byte component6() {
        return getConflict();
    }

    @Override
    public ULong component7() {
        return getReplacementAgencyIdListValueManifestId();
    }

    @Override
    public ULong component8() {
        return getPrevAgencyIdListValueManifestId();
    }

    @Override
    public ULong component9() {
        return getNextAgencyIdListValueManifestId();
    }

    @Override
    public ULong value1() {
        return getAgencyIdListValueManifestId();
    }

    @Override
    public String value2() {
        return getReleaseId();
    }

    @Override
    public ULong value3() {
        return getAgencyIdListValueId();
    }

    @Override
    public ULong value4() {
        return getAgencyIdListManifestId();
    }

    @Override
    public ULong value5() {
        return getBasedAgencyIdListValueManifestId();
    }

    @Override
    public Byte value6() {
        return getConflict();
    }

    @Override
    public ULong value7() {
        return getReplacementAgencyIdListValueManifestId();
    }

    @Override
    public ULong value8() {
        return getPrevAgencyIdListValueManifestId();
    }

    @Override
    public ULong value9() {
        return getNextAgencyIdListValueManifestId();
    }

    @Override
    public AgencyIdListValueManifestRecord value1(ULong value) {
        setAgencyIdListValueManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value2(String value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value3(ULong value) {
        setAgencyIdListValueId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value4(ULong value) {
        setAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value5(ULong value) {
        setBasedAgencyIdListValueManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value6(Byte value) {
        setConflict(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value7(ULong value) {
        setReplacementAgencyIdListValueManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value8(ULong value) {
        setPrevAgencyIdListValueManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord value9(ULong value) {
        setNextAgencyIdListValueManifestId(value);
        return this;
    }

    @Override
    public AgencyIdListValueManifestRecord values(ULong value1, String value2, ULong value3, ULong value4, ULong value5, Byte value6, ULong value7, ULong value8, ULong value9) {
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
     * Create a detached AgencyIdListValueManifestRecord
     */
    public AgencyIdListValueManifestRecord() {
        super(AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST);
    }

    /**
     * Create a detached, initialised AgencyIdListValueManifestRecord
     */
    public AgencyIdListValueManifestRecord(ULong agencyIdListValueManifestId, String releaseId, ULong agencyIdListValueId, ULong agencyIdListManifestId, ULong basedAgencyIdListValueManifestId, Byte conflict, ULong replacementAgencyIdListValueManifestId, ULong prevAgencyIdListValueManifestId, ULong nextAgencyIdListValueManifestId) {
        super(AgencyIdListValueManifest.AGENCY_ID_LIST_VALUE_MANIFEST);

        setAgencyIdListValueManifestId(agencyIdListValueManifestId);
        setReleaseId(releaseId);
        setAgencyIdListValueId(agencyIdListValueId);
        setAgencyIdListManifestId(agencyIdListManifestId);
        setBasedAgencyIdListValueManifestId(basedAgencyIdListValueManifestId);
        setConflict(conflict);
        setReplacementAgencyIdListValueManifestId(replacementAgencyIdListValueManifestId);
        setPrevAgencyIdListValueManifestId(prevAgencyIdListValueManifestId);
        setNextAgencyIdListValueManifestId(nextAgencyIdListValueManifestId);
    }
}
