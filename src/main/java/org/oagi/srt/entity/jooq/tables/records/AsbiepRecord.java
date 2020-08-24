/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.Asbiep;

import java.time.LocalDateTime;


/**
 * ASBIEP represents a role in a usage of an ABIE. It is a contextualization 
 * of an ASCCP.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsbiepRecord extends UpdatableRecordImpl<AsbiepRecord> implements Record14<ULong, String, ULong, String, String, ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime, ULong> {

    private static final long serialVersionUID = -1443220947;

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
     * Setter for <code>oagi.asbiep.based_asccp_manifest_id</code>. A foreign key pointing to the ASCCP_MANIFEST record. It is the ASCCP, on which the ASBIEP contextualizes.
     */
    public void setBasedAsccpManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.asbiep.based_asccp_manifest_id</code>. A foreign key pointing to the ASCCP_MANIFEST record. It is the ASCCP, on which the ASBIEP contextualizes.
     */
    public ULong getBasedAsccpManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.asbiep.path</code>.
     */
    public void setPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.asbiep.path</code>.
     */
    public String getPath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.asbiep.hash_path</code>. hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.
     */
    public void setHashPath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.asbiep.hash_path</code>. hash_path generated from the path of the component graph using hash function, so that it is unique in the graph.
     */
    public String getHashPath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.asbiep.role_of_abie_id</code>. A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.
     */
    public void setRoleOfAbieId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.asbiep.role_of_abie_id</code>. A foreign key pointing to the ABIE record. It is the ABIE, which the property term in the based ASCCP qualifies. Note that the ABIE has to be derived from the ACC used by the based ASCCP.
     */
    public ULong getRoleOfAbieId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.asbiep.definition</code>. A definition to override the ASCCP's definition. If NULL, it means that the definition should be derived from the based ASCCP on the UI, expression generation, and any API.
     */
    public void setDefinition(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.asbiep.definition</code>. A definition to override the ASCCP's definition. If NULL, it means that the definition should be derived from the based ASCCP on the UI, expression generation, and any API.
     */
    public String getDefinition() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.asbiep.remark</code>. This column allows the user to specify a context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ASBIEP can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ASBIEP. A remark about that ASBIEP may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public void setRemark(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.asbiep.remark</code>. This column allows the user to specify a context-specific usage of the BIE. It is different from the DEFINITION column in that the DEFINITION column is a description conveying the meaning of the associated concept. Remarks may be a very implementation specific instruction or others. For example, BOM BOD, as an ACC, is a generic BOM structure. In a particular context, a BOM ASBIEP can be a Super BOM. Explanation of the Super BOM concept should be captured in the Definition of the ASBIEP. A remark about that ASBIEP may be "Type of BOM should be recognized in the BOM/typeCode."
     */
    public String getRemark() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.asbiep.biz_term</code>. This column represents a business term to indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public void setBizTerm(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.asbiep.biz_term</code>. This column represents a business term to indicate what the BIE is called in a particular business context. With this current design, only one business term is allowed per business context.
     */
    public String getBizTerm() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.asbiep.created_by</code>. A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.
     */
    public void setCreatedBy(ULong value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.asbiep.created_by</code>. A foreign key referring to the user who creates the ASBIEP. The creator of the ASBIEP is also its owner by default. ASBIEPs created as children of another ABIE have the same CREATED_BY.
     */
    public ULong getCreatedBy() {
        return (ULong) get(9);
    }

    /**
     * Setter for <code>oagi.asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the ASBIEP record. 
     */
    public void setLastUpdatedBy(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.asbiep.last_updated_by</code>. A foreign key referring to the last user who has updated the ASBIEP record. 
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.asbiep.creation_timestamp</code>. Timestamp when the ASBIEP record was first created. ASBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.asbiep.creation_timestamp</code>. Timestamp when the ASBIEP record was first created. ASBIEPs created as children of another ABIE have the same CREATION_TIMESTAMP.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>oagi.asbiep.last_update_timestamp</code>. The timestamp when the ASBIEP was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.asbiep.last_update_timestamp</code>. The timestamp when the ASBIEP was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(12);
    }

    /**
     * Setter for <code>oagi.asbiep.owner_top_level_asbiep_id</code>. This is a foreign key to the top-level ASBIEP.
     */
    public void setOwnerTopLevelAsbiepId(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.asbiep.owner_top_level_asbiep_id</code>. This is a foreign key to the top-level ASBIEP.
     */
    public ULong getOwnerTopLevelAsbiepId() {
        return (ULong) get(13);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row14<ULong, String, ULong, String, String, ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime, ULong> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    @Override
    public Row14<ULong, String, ULong, String, String, ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime, ULong> valuesRow() {
        return (Row14) super.valuesRow();
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
        return Asbiep.ASBIEP.BASED_ASCCP_MANIFEST_ID;
    }

    @Override
    public Field<String> field4() {
        return Asbiep.ASBIEP.PATH;
    }

    @Override
    public Field<String> field5() {
        return Asbiep.ASBIEP.HASH_PATH;
    }

    @Override
    public Field<ULong> field6() {
        return Asbiep.ASBIEP.ROLE_OF_ABIE_ID;
    }

    @Override
    public Field<String> field7() {
        return Asbiep.ASBIEP.DEFINITION;
    }

    @Override
    public Field<String> field8() {
        return Asbiep.ASBIEP.REMARK;
    }

    @Override
    public Field<String> field9() {
        return Asbiep.ASBIEP.BIZ_TERM;
    }

    @Override
    public Field<ULong> field10() {
        return Asbiep.ASBIEP.CREATED_BY;
    }

    @Override
    public Field<ULong> field11() {
        return Asbiep.ASBIEP.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return Asbiep.ASBIEP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field13() {
        return Asbiep.ASBIEP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field14() {
        return Asbiep.ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID;
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
        return getBasedAsccpManifestId();
    }

    @Override
    public String component4() {
        return getPath();
    }

    @Override
    public String component5() {
        return getHashPath();
    }

    @Override
    public ULong component6() {
        return getRoleOfAbieId();
    }

    @Override
    public String component7() {
        return getDefinition();
    }

    @Override
    public String component8() {
        return getRemark();
    }

    @Override
    public String component9() {
        return getBizTerm();
    }

    @Override
    public ULong component10() {
        return getCreatedBy();
    }

    @Override
    public ULong component11() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component12() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component13() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component14() {
        return getOwnerTopLevelAsbiepId();
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
        return getBasedAsccpManifestId();
    }

    @Override
    public String value4() {
        return getPath();
    }

    @Override
    public String value5() {
        return getHashPath();
    }

    @Override
    public ULong value6() {
        return getRoleOfAbieId();
    }

    @Override
    public String value7() {
        return getDefinition();
    }

    @Override
    public String value8() {
        return getRemark();
    }

    @Override
    public String value9() {
        return getBizTerm();
    }

    @Override
    public ULong value10() {
        return getCreatedBy();
    }

    @Override
    public ULong value11() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value12() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value13() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value14() {
        return getOwnerTopLevelAsbiepId();
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
        setBasedAsccpManifestId(value);
        return this;
    }

    @Override
    public AsbiepRecord value4(String value) {
        setPath(value);
        return this;
    }

    @Override
    public AsbiepRecord value5(String value) {
        setHashPath(value);
        return this;
    }

    @Override
    public AsbiepRecord value6(ULong value) {
        setRoleOfAbieId(value);
        return this;
    }

    @Override
    public AsbiepRecord value7(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AsbiepRecord value8(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public AsbiepRecord value9(String value) {
        setBizTerm(value);
        return this;
    }

    @Override
    public AsbiepRecord value10(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AsbiepRecord value11(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AsbiepRecord value12(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AsbiepRecord value13(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AsbiepRecord value14(ULong value) {
        setOwnerTopLevelAsbiepId(value);
        return this;
    }

    @Override
    public AsbiepRecord values(ULong value1, String value2, ULong value3, String value4, String value5, ULong value6, String value7, String value8, String value9, ULong value10, ULong value11, LocalDateTime value12, LocalDateTime value13, ULong value14) {
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
        value13(value13);
        value14(value14);
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
    public AsbiepRecord(ULong asbiepId, String guid, ULong basedAsccpManifestId, String path, String hashPath, ULong roleOfAbieId, String definition, String remark, String bizTerm, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, ULong ownerTopLevelAsbiepId) {
        super(Asbiep.ASBIEP);

        set(0, asbiepId);
        set(1, guid);
        set(2, basedAsccpManifestId);
        set(3, path);
        set(4, hashPath);
        set(5, roleOfAbieId);
        set(6, definition);
        set(7, remark);
        set(8, bizTerm);
        set(9, createdBy);
        set(10, lastUpdatedBy);
        set(11, creationTimestamp);
        set(12, lastUpdateTimestamp);
        set(13, ownerTopLevelAsbiepId);
    }
}
