/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Bbiep;


/**
 * BBIEP represents the usage of basic property in a specific business context. 
 * It is a contextualization of a BCCP.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbiepRecord extends UpdatableRecordImpl<BbiepRecord> implements Record11<ULong, String, ULong, String, String, String, ULong, ULong, Timestamp, Timestamp, ULong> {

    private static final long serialVersionUID = -767515589;

    /**
     * Setter for <code>oagi.bbiep.bbiep_id</code>. A internal, primary database key of an BBIEP.
     */
    public void setBbiepId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bbiep.bbiep_id</code>. A internal, primary database key of an BBIEP.
     */
    public ULong getBbiepId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.bbiep.guid</code>. A globally unique identifier (GUID) of an BBIEP. GUID of an BBIEP is different from its based BCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bbiep.guid</code>. A globally unique identifier (GUID) of an BBIEP. GUID of an BBIEP is different from its based BCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bbiep.based_bccp_manifest_id</code>. A foreign key pointing to the BCCP_MANIFEST record. It is the BCCP, which the BBIEP contextualizes.
     */
    public void setBasedBccpManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bbiep.based_bccp_manifest_id</code>. A foreign key pointing to the BCCP_MANIFEST record. It is the BCCP, which the BBIEP contextualizes.
     */
    public ULong getBasedBccpManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bbiep.definition</code>. Definition to override the BCCP's Definition. If NULLl, it means that the definition should be inherited from the based CC.
     */
    public void setDefinition(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bbiep.definition</code>. Definition to override the BCCP's Definition. If NULLl, it means that the definition should be inherited from the based CC.
     */
    public String getDefinition() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bbiep.remark</code>. This column allows the user to specify very context-specific usage of the BIE. It is different from the Definition column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be "Type of BOM should be recognized in the BOM/typeCode.
     */
    public void setRemark(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bbiep.remark</code>. This column allows the user to specify very context-specific usage of the BIE. It is different from the Definition column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ABIE can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ABIE. A remark about that ABIE may be "Type of BOM should be recognized in the BOM/typeCode.
     */
    public String getRemark() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.bbiep.biz_term</code>. Business term to indicate what the BIE is called in a particular business context such as in an industry.
     */
    public void setBizTerm(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bbiep.biz_term</code>. Business term to indicate what the BIE is called in a particular business context such as in an industry.
     */
    public String getBizTerm() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.bbiep.created_by</code>. A foreign key referring to the user who creates the BBIEP. The creator of the BBIEP is also its owner by default. BBIEPs created as children of another ABIE have the same CREATED_BY',
     */
    public void setCreatedBy(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bbiep.created_by</code>. A foreign key referring to the user who creates the BBIEP. The creator of the BBIEP is also its owner by default. BBIEPs created as children of another ABIE have the same CREATED_BY',
     */
    public ULong getCreatedBy() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.bbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the BBIEP record. 
     */
    public void setLastUpdatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the BBIEP record. 
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.bbiep.creation_timestamp</code>. Timestamp when the BBIEP record was first created. BBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP,
     */
    public void setCreationTimestamp(Timestamp value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bbiep.creation_timestamp</code>. Timestamp when the BBIEP record was first created. BBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP,
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(8);
    }

    /**
     * Setter for <code>oagi.bbiep.last_update_timestamp</code>. The timestamp when the BBIEP was last updated.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bbiep.last_update_timestamp</code>. The timestamp when the BBIEP was last updated.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>oagi.bbiep.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE which owns this BBIEP record.
     */
    public void setOwnerTopLevelAbieId(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bbiep.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE which owns this BBIEP record.
     */
    public ULong getOwnerTopLevelAbieId() {
        return (ULong) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<ULong, String, ULong, String, String, String, ULong, ULong, Timestamp, Timestamp, ULong> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<ULong, String, ULong, String, String, String, ULong, ULong, Timestamp, Timestamp, ULong> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Bbiep.BBIEP.BBIEP_ID;
    }

    @Override
    public Field<String> field2() {
        return Bbiep.BBIEP.GUID;
    }

    @Override
    public Field<ULong> field3() {
        return Bbiep.BBIEP.BASED_BCCP_MANIFEST_ID;
    }

    @Override
    public Field<String> field4() {
        return Bbiep.BBIEP.DEFINITION;
    }

    @Override
    public Field<String> field5() {
        return Bbiep.BBIEP.REMARK;
    }

    @Override
    public Field<String> field6() {
        return Bbiep.BBIEP.BIZ_TERM;
    }

    @Override
    public Field<ULong> field7() {
        return Bbiep.BBIEP.CREATED_BY;
    }

    @Override
    public Field<ULong> field8() {
        return Bbiep.BBIEP.LAST_UPDATED_BY;
    }

    @Override
    public Field<Timestamp> field9() {
        return Bbiep.BBIEP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<Timestamp> field10() {
        return Bbiep.BBIEP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field11() {
        return Bbiep.BBIEP.OWNER_TOP_LEVEL_ABIE_ID;
    }

    @Override
    public ULong component1() {
        return getBbiepId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public ULong component3() {
        return getBasedBccpManifestId();
    }

    @Override
    public String component4() {
        return getDefinition();
    }

    @Override
    public String component5() {
        return getRemark();
    }

    @Override
    public String component6() {
        return getBizTerm();
    }

    @Override
    public ULong component7() {
        return getCreatedBy();
    }

    @Override
    public ULong component8() {
        return getLastUpdatedBy();
    }

    @Override
    public Timestamp component9() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp component10() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component11() {
        return getOwnerTopLevelAbieId();
    }

    @Override
    public ULong value1() {
        return getBbiepId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public ULong value3() {
        return getBasedBccpManifestId();
    }

    @Override
    public String value4() {
        return getDefinition();
    }

    @Override
    public String value5() {
        return getRemark();
    }

    @Override
    public String value6() {
        return getBizTerm();
    }

    @Override
    public ULong value7() {
        return getCreatedBy();
    }

    @Override
    public ULong value8() {
        return getLastUpdatedBy();
    }

    @Override
    public Timestamp value9() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp value10() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value11() {
        return getOwnerTopLevelAbieId();
    }

    @Override
    public BbiepRecord value1(ULong value) {
        setBbiepId(value);
        return this;
    }

    @Override
    public BbiepRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public BbiepRecord value3(ULong value) {
        setBasedBccpManifestId(value);
        return this;
    }

    @Override
    public BbiepRecord value4(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public BbiepRecord value5(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public BbiepRecord value6(String value) {
        setBizTerm(value);
        return this;
    }

    @Override
    public BbiepRecord value7(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public BbiepRecord value8(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public BbiepRecord value9(Timestamp value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public BbiepRecord value10(Timestamp value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public BbiepRecord value11(ULong value) {
        setOwnerTopLevelAbieId(value);
        return this;
    }

    @Override
    public BbiepRecord values(ULong value1, String value2, ULong value3, String value4, String value5, String value6, ULong value7, ULong value8, Timestamp value9, Timestamp value10, ULong value11) {
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
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BbiepRecord
     */
    public BbiepRecord() {
        super(Bbiep.BBIEP);
    }

    /**
     * Create a detached, initialised BbiepRecord
     */
    public BbiepRecord(ULong bbiepId, String guid, ULong basedBccpManifestId, String definition, String remark, String bizTerm, ULong createdBy, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp, ULong ownerTopLevelAbieId) {
        super(Bbiep.BBIEP);

        set(0, bbiepId);
        set(1, guid);
        set(2, basedBccpManifestId);
        set(3, definition);
        set(4, remark);
        set(5, bizTerm);
        set(6, createdBy);
        set(7, lastUpdatedBy);
        set(8, creationTimestamp);
        set(9, lastUpdateTimestamp);
        set(10, ownerTopLevelAbieId);
    }
}
