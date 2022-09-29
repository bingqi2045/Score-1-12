/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bbiep;


/**
 * BBIEP represents the usage of basic property in a specific business context.
 * It is a contextualization of a BCCP.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbiepRecord extends UpdatableRecordImpl<BbiepRecord> implements Record13<String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bbiep.bbiep_id</code>. Primary, internal database
     * key.
     */
    public void setBbiepId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bbiep.bbiep_id</code>. Primary, internal database
     * key.
     */
    public String getBbiepId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.bbiep.guid</code>. A globally unique identifier
     * (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bbiep.guid</code>. A globally unique identifier
     * (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bbiep.based_bccp_manifest_id</code>. A foreign key
     * pointing to the BCCP_MANIFEST record. It is the BCCP, which the BBIEP
     * contextualizes.
     */
    public void setBasedBccpManifestId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bbiep.based_bccp_manifest_id</code>. A foreign key
     * pointing to the BCCP_MANIFEST record. It is the BCCP, which the BBIEP
     * contextualizes.
     */
    public String getBasedBccpManifestId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.bbiep.path</code>.
     */
    public void setPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bbiep.path</code>.
     */
    public String getPath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bbiep.hash_path</code>. hash_path generated from
     * the path of the component graph using hash function, so that it is unique
     * in the graph.
     */
    public void setHashPath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bbiep.hash_path</code>. hash_path generated from
     * the path of the component graph using hash function, so that it is unique
     * in the graph.
     */
    public String getHashPath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.bbiep.definition</code>. Definition to override the
     * BCCP's Definition. If NULLl, it means that the definition should be
     * inherited from the based CC.
     */
    public void setDefinition(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bbiep.definition</code>. Definition to override the
     * BCCP's Definition. If NULLl, it means that the definition should be
     * inherited from the based CC.
     */
    public String getDefinition() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.bbiep.remark</code>. This column allows the user to
     * specify very context-specific usage of the BIE. It is different from the
     * Definition column in that the DEFINITION column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. For example, BOM BOD, as
     * an ACC, is a generic BOM structure. In a particular context, a BOM ABIE
     * can be a Super BOM. Explanation of the Super BOM concept should be
     * captured in the Definition of the ABIE. A remark about that ABIE may be
     * "Type of BOM should be recognized in the BOM/typeCode.
     */
    public void setRemark(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bbiep.remark</code>. This column allows the user to
     * specify very context-specific usage of the BIE. It is different from the
     * Definition column in that the DEFINITION column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. For example, BOM BOD, as
     * an ACC, is a generic BOM structure. In a particular context, a BOM ABIE
     * can be a Super BOM. Explanation of the Super BOM concept should be
     * captured in the Definition of the ABIE. A remark about that ABIE may be
     * "Type of BOM should be recognized in the BOM/typeCode.
     */
    public String getRemark() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.bbiep.biz_term</code>. Business term to indicate
     * what the BIE is called in a particular business context such as in an
     * industry.
     */
    public void setBizTerm(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bbiep.biz_term</code>. Business term to indicate
     * what the BIE is called in a particular business context such as in an
     * industry.
     */
    public String getBizTerm() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.bbiep.created_by</code>. A foreign key referring to
     * the user who creates the BBIEP. The creator of the BBIEP is also its
     * owner by default. BBIEPs created as children of another ABIE have the
     * same CREATED_BY',
     */
    public void setCreatedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bbiep.created_by</code>. A foreign key referring to
     * the user who creates the BBIEP. The creator of the BBIEP is also its
     * owner by default. BBIEPs created as children of another ABIE have the
     * same CREATED_BY',
     */
    public String getCreatedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.bbiep.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the BBIEP record. 
     */
    public void setLastUpdatedBy(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bbiep.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the BBIEP record. 
     */
    public String getLastUpdatedBy() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.bbiep.creation_timestamp</code>. Timestamp when the
     * BBIEP record was first created. BBIEPs created as children of another
     * ABIE have the same CREATION_TIMESTAMP,
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bbiep.creation_timestamp</code>. Timestamp when the
     * BBIEP record was first created. BBIEPs created as children of another
     * ABIE have the same CREATION_TIMESTAMP,
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(10);
    }

    /**
     * Setter for <code>oagi.bbiep.last_update_timestamp</code>. The timestamp
     * when the BBIEP was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.bbiep.last_update_timestamp</code>. The timestamp
     * when the BBIEP was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>oagi.bbiep.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public void setOwnerTopLevelAsbiepId(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.bbiep.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public String getOwnerTopLevelAsbiepId() {
        return (String) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime, String> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Bbiep.BBIEP.BBIEP_ID;
    }

    @Override
    public Field<String> field2() {
        return Bbiep.BBIEP.GUID;
    }

    @Override
    public Field<String> field3() {
        return Bbiep.BBIEP.BASED_BCCP_MANIFEST_ID;
    }

    @Override
    public Field<String> field4() {
        return Bbiep.BBIEP.PATH;
    }

    @Override
    public Field<String> field5() {
        return Bbiep.BBIEP.HASH_PATH;
    }

    @Override
    public Field<String> field6() {
        return Bbiep.BBIEP.DEFINITION;
    }

    @Override
    public Field<String> field7() {
        return Bbiep.BBIEP.REMARK;
    }

    @Override
    public Field<String> field8() {
        return Bbiep.BBIEP.BIZ_TERM;
    }

    @Override
    public Field<String> field9() {
        return Bbiep.BBIEP.CREATED_BY;
    }

    @Override
    public Field<String> field10() {
        return Bbiep.BBIEP.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return Bbiep.BBIEP.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return Bbiep.BBIEP.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field13() {
        return Bbiep.BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID;
    }

    @Override
    public String component1() {
        return getBbiepId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getBasedBccpManifestId();
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
    public String component6() {
        return getDefinition();
    }

    @Override
    public String component7() {
        return getRemark();
    }

    @Override
    public String component8() {
        return getBizTerm();
    }

    @Override
    public String component9() {
        return getCreatedBy();
    }

    @Override
    public String component10() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component11() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component12() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String component13() {
        return getOwnerTopLevelAsbiepId();
    }

    @Override
    public String value1() {
        return getBbiepId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getBasedBccpManifestId();
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
    public String value6() {
        return getDefinition();
    }

    @Override
    public String value7() {
        return getRemark();
    }

    @Override
    public String value8() {
        return getBizTerm();
    }

    @Override
    public String value9() {
        return getCreatedBy();
    }

    @Override
    public String value10() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value11() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value12() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value13() {
        return getOwnerTopLevelAsbiepId();
    }

    @Override
    public BbiepRecord value1(String value) {
        setBbiepId(value);
        return this;
    }

    @Override
    public BbiepRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public BbiepRecord value3(String value) {
        setBasedBccpManifestId(value);
        return this;
    }

    @Override
    public BbiepRecord value4(String value) {
        setPath(value);
        return this;
    }

    @Override
    public BbiepRecord value5(String value) {
        setHashPath(value);
        return this;
    }

    @Override
    public BbiepRecord value6(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public BbiepRecord value7(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public BbiepRecord value8(String value) {
        setBizTerm(value);
        return this;
    }

    @Override
    public BbiepRecord value9(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public BbiepRecord value10(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public BbiepRecord value11(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public BbiepRecord value12(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public BbiepRecord value13(String value) {
        setOwnerTopLevelAsbiepId(value);
        return this;
    }

    @Override
    public BbiepRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, LocalDateTime value11, LocalDateTime value12, String value13) {
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
    public BbiepRecord(String bbiepId, String guid, String basedBccpManifestId, String path, String hashPath, String definition, String remark, String bizTerm, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String ownerTopLevelAsbiepId) {
        super(Bbiep.BBIEP);

        setBbiepId(bbiepId);
        setGuid(guid);
        setBasedBccpManifestId(basedBccpManifestId);
        setPath(path);
        setHashPath(hashPath);
        setDefinition(definition);
        setRemark(remark);
        setBizTerm(bizTerm);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setOwnerTopLevelAsbiepId(ownerTopLevelAsbiepId);
    }
}
