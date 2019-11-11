/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Asbiep;


/**
 * ASBIEP represents a role in a usage of an ABIE. It is a contextualization 
 * of an ASCCP.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsbiepRecord extends UpdatableRecordImpl<AsbiepRecord> implements Record12<ULong, String, ULong, ULong, String, String, String, ULong, ULong, Timestamp, Timestamp, ULong> {

    private static final long serialVersionUID = -1418175482;

    /**
     * Setter for <code>oagi.asbiep.asbiep_id</code>. A internal, primary database key of an ASBIEP.
     */
    public void setAsbiepId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.asbiep.asbiep_id</code>. A internal, primary database key of an ASBIEP.
     */
    public ULong getAsbiepId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.asbiep.guid</code>. A globally unique identifier (GUID) of an ASBIEP. GUID of an ASBIEP is different from its based ASCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.asbiep.guid</code>. A globally unique identifier (GUID) of an ASBIEP. GUID of an ASBIEP is different from its based ASCCP. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.asbiep.based_asccp_id</code>. A foreign key pointing to the ASCCP record. It is the ASCCP, on which the ASBIEP contextualizes.
     */
    public void setBasedAsccpId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.asbiep.based_asccp_id</code>. A foreign key pointing to the ASCCP record. It is the ASCCP, on which the ASBIEP contextualizes.
     */
    public ULong getBasedAsccpId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.asbiep.role_of_abie_id</code>. A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.
     */
    public void setRoleOfAbieId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.asbiep.role_of_abie_id</code>. A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.
     */
    public ULong getRoleOfAbieId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.asbiep.definition</code>. A definition to override the ASCCP's definition. If NULL, it means that the definition should be derived from the based ASCCP on the UI, expression generation, and any API.
     */
    public void setDefinition(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.asbiep.definition</code>. A definition to override the ASCCP's definition. If NULL, it means that the definition should be derived from the based ASCCP on the UI, expression generation, and any API.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.asbiep.remark</code>. This column allows the user to specify a context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ASBIEP can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ASBIEP. A remark about that ASBIEP may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public void setRemark(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.asbiep.remark</code>. This column allows the user to specify a context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ASBIEP can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ASBIEP. A remark about that ASBIEP may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public String getRemark() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.asbiep.biz_term</code>. This column represents a business term to indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public void setBizTerm(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.asbiep.biz_term</code>. This column represents a business term to indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public String getBizTerm() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.asbiep.created_by</code>. A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.
     */
    public void setCreatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.asbiep.created_by</code>. A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.
     */
    public ULong getCreatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the ASBIEP record. 
     */
    public void setLastUpdatedBy(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the ASBIEP record. 
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.asbiep.creation_timestamp</code>. Timestamp when the ASBIEP record was first created. ASBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public void setCreationTimestamp(Timestamp value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.asbiep.creation_timestamp</code>. Timestamp when the ASBIEP record was first created. ASBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public Timestamp getCreationTimestamp() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>oagi.asbiep.last_update_timestamp</code>. The timestamp when the ASBIEP was last updated.
     */
    public void setLastUpdateTimestamp(Timestamp value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.asbiep.last_update_timestamp</code>. The timestamp when the ASBIEP was last updated.
     */
    public Timestamp getLastUpdateTimestamp() {
        return (Timestamp) get(10);
    }

    /**
     * Setter for <code>oagi.asbiep.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE, which owns this ASBIEP record.
     */
    public void setOwnerTopLevelAbieId(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.asbiep.owner_top_level_abie_id</code>. This is a foriegn key to the ABIE table. It specifies the top-level ABIE, which owns this ASBIEP record.
     */
    public ULong getOwnerTopLevelAbieId() {
        return (ULong) get(11);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<ULong, String, ULong, ULong, String, String, String, ULong, ULong, Timestamp, Timestamp, ULong> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<ULong, String, ULong, ULong, String, String, String, ULong, ULong, Timestamp, Timestamp, ULong> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Asbiep.ASBIEP.ASBIEP_ID;
    }

    @Override
    public Field<String> field2() {
        return Asbiep.ASBIEP.GUID;
    }

    @Override
    public Field<ULong> field3() {
        return Asbiep.ASBIEP.BASED_ASCCP_ID;
    }

    @Override
    public Field<ULong> field4() {
        return Asbiep.ASBIEP.ROLE_OF_ABIE_ID;
    }

    @Override
    public Field<String> field5() {
        return Asbiep.ASBIEP.DEFINITION;
    }

    @Override
    public Field<String> field6() {
        return Asbiep.ASBIEP.REMARK;
    }

    @Override
    public Field<String> field7() {
        return Asbiep.ASBIEP.BIZ_TERM;
    }

    @Override
    public Field<ULong> field8() {
        return Asbiep.ASBIEP.CREATED_BY;
    }

    @Override
    public Field<ULong> field9() {
        return Asbiep.ASBIEP.LAST_UPDATED_BY;
    }

    @Override
    public Field<Timestamp> field10() {
        return Asbiep.ASBIEP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<Timestamp> field11() {
        return Asbiep.ASBIEP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field12() {
        return Asbiep.ASBIEP.OWNER_TOP_LEVEL_ABIE_ID;
    }

    @Override
    public ULong component1() {
        return getAsbiepId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public ULong component3() {
        return getBasedAsccpId();
    }

    @Override
    public ULong component4() {
        return getRoleOfAbieId();
    }

    @Override
    public String component5() {
        return getDefinition();
    }

    @Override
    public String component6() {
        return getRemark();
    }

    @Override
    public String component7() {
        return getBizTerm();
    }

    @Override
    public ULong component8() {
        return getCreatedBy();
    }

    @Override
    public ULong component9() {
        return getLastUpdatedBy();
    }

    @Override
    public Timestamp component10() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp component11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component12() {
        return getOwnerTopLevelAbieId();
    }

    @Override
    public ULong value1() {
        return getAsbiepId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public ULong value3() {
        return getBasedAsccpId();
    }

    @Override
    public ULong value4() {
        return getRoleOfAbieId();
    }

    @Override
    public String value5() {
        return getDefinition();
    }

    @Override
    public String value6() {
        return getRemark();
    }

    @Override
    public String value7() {
        return getBizTerm();
    }

    @Override
    public ULong value8() {
        return getCreatedBy();
    }

    @Override
    public ULong value9() {
        return getLastUpdatedBy();
    }

    @Override
    public Timestamp value10() {
        return getCreationTimestamp();
    }

    @Override
    public Timestamp value11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value12() {
        return getOwnerTopLevelAbieId();
    }

    @Override
    public AsbiepRecord value1(ULong value) {
        setAsbiepId(value);
        return this;
    }

    @Override
    public AsbiepRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AsbiepRecord value3(ULong value) {
        setBasedAsccpId(value);
        return this;
    }

    @Override
    public AsbiepRecord value4(ULong value) {
        setRoleOfAbieId(value);
        return this;
    }

    @Override
    public AsbiepRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AsbiepRecord value6(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public AsbiepRecord value7(String value) {
        setBizTerm(value);
        return this;
    }

    @Override
    public AsbiepRecord value8(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsbiepRecord value9(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsbiepRecord value10(Timestamp value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsbiepRecord value11(Timestamp value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsbiepRecord value12(ULong value) {
        setOwnerTopLevelAbieId(value);
        return this;
    }

    @Override
    public AsbiepRecord values(ULong value1, String value2, ULong value3, ULong value4, String value5, String value6, String value7, ULong value8, ULong value9, Timestamp value10, Timestamp value11, ULong value12) {
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
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AsbiepRecord
     */
    public AsbiepRecord() {
        super(Asbiep.ASBIEP);
    }

    /**
     * Create a detached, initialised AsbiepRecord
     */
    public AsbiepRecord(ULong asbiepId, String guid, ULong basedAsccpId, ULong roleOfAbieId, String definition, String remark, String bizTerm, ULong createdBy, ULong lastUpdatedBy, Timestamp creationTimestamp, Timestamp lastUpdateTimestamp, ULong ownerTopLevelAbieId) {
        super(Asbiep.ASBIEP);

        set(0, asbiepId);
        set(1, guid);
        set(2, basedAsccpId);
        set(3, roleOfAbieId);
        set(4, definition);
        set(5, remark);
        set(6, bizTerm);
        set(7, createdBy);
        set(8, lastUpdatedBy);
        set(9, creationTimestamp);
        set(10, lastUpdateTimestamp);
        set(11, ownerTopLevelAbieId);
    }
}
