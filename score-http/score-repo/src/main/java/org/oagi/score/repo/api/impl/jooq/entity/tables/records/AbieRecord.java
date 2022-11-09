/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Abie;


/**
 * The ABIE table stores information about an ABIE, which is a contextualized
 * ACC. The context is represented by the BUSINESS_CTX_ID column that refers to
 * a business context. Each ABIE must have a business context and a based ACC.
 * 
 * It should be noted that, per design document, there is no corresponding ABIE
 * created for an ACC which will not show up in the instance document such as
 * ACCs of OAGIS_COMPONENT_TYPE "SEMANTIC_GROUP", "USER_EXTENSION_GROUP", etc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AbieRecord extends UpdatableRecordImpl<AbieRecord> implements Record15<ULong, String, ULong, String, String, ULong, String, ULong, ULong, LocalDateTime, LocalDateTime, Integer, String, String, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.abie.abie_id</code>. A internal, primary database
     * key of an ABIE.
     */
    public void setAbieId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.abie.abie_id</code>. A internal, primary database
     * key of an ABIE.
     */
    public ULong getAbieId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.abie.guid</code>. A globally unique identifier
     * (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.abie.guid</code>. A globally unique identifier
     * (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.abie.based_acc_manifest_id</code>. A foreign key to
     * the ACC_MANIFEST table refering to the ACC, on which the business context
     * has been applied to derive this ABIE.
     */
    public void setBasedAccManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.abie.based_acc_manifest_id</code>. A foreign key to
     * the ACC_MANIFEST table refering to the ACC, on which the business context
     * has been applied to derive this ABIE.
     */
    public ULong getBasedAccManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.abie.path</code>.
     */
    public void setPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.abie.path</code>.
     */
    public String getPath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.abie.hash_path</code>. hash_path generated from the
     * path of the component graph using hash function, so that it is unique in
     * the graph.
     */
    public void setHashPath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.abie.hash_path</code>. hash_path generated from the
     * path of the component graph using hash function, so that it is unique in
     * the graph.
     */
    public String getHashPath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.abie.biz_ctx_id</code>. (Deprecated) A foreign key
     * to the BIZ_CTX table. This column stores the business context assigned to
     * the ABIE.
     */
    public void setBizCtxId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.abie.biz_ctx_id</code>. (Deprecated) A foreign key
     * to the BIZ_CTX table. This column stores the business context assigned to
     * the ABIE.
     */
    public ULong getBizCtxId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.abie.definition</code>. Definition to override the
     * ACC's definition. If NULL, it means that the definition should be
     * inherited from the based CC.
     */
    public void setDefinition(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.abie.definition</code>. Definition to override the
     * ACC's definition. If NULL, it means that the definition should be
     * inherited from the based CC.
     */
    public String getDefinition() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.abie.created_by</code>. A foreign key referring to
     * the user who creates the ABIE. The creator of the ABIE is also its owner
     * by default. ABIEs created as children of another ABIE have the same
     * CREATED_BY as its parent.
     */
    public void setCreatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.abie.created_by</code>. A foreign key referring to
     * the user who creates the ABIE. The creator of the ABIE is also its owner
     * by default. ABIEs created as children of another ABIE have the same
     * CREATED_BY as its parent.
     */
    public ULong getCreatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.abie.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the ABIE record. This may be
     * the user who is in the same group as the creator.
     */
    public void setLastUpdatedBy(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.abie.last_updated_by</code>. A foreign key
     * referring to the last user who has updated the ABIE record. This may be
     * the user who is in the same group as the creator.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.abie.creation_timestamp</code>. Timestamp when the
     * ABIE record was first created. ABIEs created as children of another ABIE
     * have the same CREATION_TIMESTAMP.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.abie.creation_timestamp</code>. Timestamp when the
     * ABIE record was first created. ABIEs created as children of another ABIE
     * have the same CREATION_TIMESTAMP.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>oagi.abie.last_update_timestamp</code>. The timestamp
     * when the ABIE was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.abie.last_update_timestamp</code>. The timestamp
     * when the ABIE was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(10);
    }

    /**
     * Setter for <code>oagi.abie.state</code>. 2 = EDITING, 4 = PUBLISHED. This
     * column is only used with a top-level ABIE, because that is the only entry
     * point for editing. The state value indicates the visibility of the
     * top-level ABIE to users other than the owner. In the user group
     * environment, a logic can apply that other users in the group can see the
     * top-level ABIE only when it is in the 'Published' state.
     */
    public void setState(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.abie.state</code>. 2 = EDITING, 4 = PUBLISHED. This
     * column is only used with a top-level ABIE, because that is the only entry
     * point for editing. The state value indicates the visibility of the
     * top-level ABIE to users other than the owner. In the user group
     * environment, a logic can apply that other users in the group can see the
     * top-level ABIE only when it is in the 'Published' state.
     */
    public Integer getState() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>oagi.abie.remark</code>. This column allows the user to
     * specify very context-specific usage of the BIE. It is different from the
     * DEFINITION column in that the DEFINITION column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. For example, BOM BOD, as
     * an ACC, is a generic BOM structure. In a particular context, a BOM ABIE
     * can be a Super BOM. Explanation of the Super BOM concept should be
     * captured in the Definition of the ABIE. A remark about that ABIE may be
     * "Type of BOM should be recognized in the BOM/typeCode."
     */
    public void setRemark(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.abie.remark</code>. This column allows the user to
     * specify very context-specific usage of the BIE. It is different from the
     * DEFINITION column in that the DEFINITION column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. For example, BOM BOD, as
     * an ACC, is a generic BOM structure. In a particular context, a BOM ABIE
     * can be a Super BOM. Explanation of the Super BOM concept should be
     * captured in the Definition of the ABIE. A remark about that ABIE may be
     * "Type of BOM should be recognized in the BOM/typeCode."
     */
    public String getRemark() {
        return (String) get(12);
    }

    /**
     * Setter for <code>oagi.abie.biz_term</code>. To indicate what the BIE is
     * called in a particular business context. With this current design, only
     * one business term is allowed per business context.
     */
    public void setBizTerm(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.abie.biz_term</code>. To indicate what the BIE is
     * called in a particular business context. With this current design, only
     * one business term is allowed per business context.
     */
    public String getBizTerm() {
        return (String) get(13);
    }

    /**
     * Setter for <code>oagi.abie.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public void setOwnerTopLevelAsbiepId(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.abie.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public ULong getOwnerTopLevelAsbiepId() {
        return (ULong) get(14);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row15<ULong, String, ULong, String, String, ULong, String, ULong, ULong, LocalDateTime, LocalDateTime, Integer, String, String, ULong> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    @Override
    public Row15<ULong, String, ULong, String, String, ULong, String, ULong, ULong, LocalDateTime, LocalDateTime, Integer, String, String, ULong> valuesRow() {
        return (Row15) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Abie.ABIE.ABIE_ID;
    }

    @Override
    public Field<String> field2() {
        return Abie.ABIE.GUID;
    }

    @Override
    public Field<ULong> field3() {
        return Abie.ABIE.BASED_ACC_MANIFEST_ID;
    }

    @Override
    public Field<String> field4() {
        return Abie.ABIE.PATH;
    }

    @Override
    public Field<String> field5() {
        return Abie.ABIE.HASH_PATH;
    }

    @Override
    public Field<ULong> field6() {
        return Abie.ABIE.BIZ_CTX_ID;
    }

    @Override
    public Field<String> field7() {
        return Abie.ABIE.DEFINITION;
    }

    @Override
    public Field<ULong> field8() {
        return Abie.ABIE.CREATED_BY;
    }

    @Override
    public Field<ULong> field9() {
        return Abie.ABIE.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return Abie.ABIE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return Abie.ABIE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<Integer> field12() {
        return Abie.ABIE.STATE;
    }

    @Override
    public Field<String> field13() {
        return Abie.ABIE.REMARK;
    }

    @Override
    public Field<String> field14() {
        return Abie.ABIE.BIZ_TERM;
    }

    @Override
    public Field<ULong> field15() {
        return Abie.ABIE.OWNER_TOP_LEVEL_ASBIEP_ID;
    }

    @Override
    public ULong component1() {
        return getAbieId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public ULong component3() {
        return getBasedAccManifestId();
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
        return getBizCtxId();
    }

    @Override
    public String component7() {
        return getDefinition();
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
    public LocalDateTime component10() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Integer component12() {
        return getState();
    }

    @Override
    public String component13() {
        return getRemark();
    }

    @Override
    public String component14() {
        return getBizTerm();
    }

    @Override
    public ULong component15() {
        return getOwnerTopLevelAsbiepId();
    }

    @Override
    public ULong value1() {
        return getAbieId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public ULong value3() {
        return getBasedAccManifestId();
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
        return getBizCtxId();
    }

    @Override
    public String value7() {
        return getDefinition();
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
    public LocalDateTime value10() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public Integer value12() {
        return getState();
    }

    @Override
    public String value13() {
        return getRemark();
    }

    @Override
    public String value14() {
        return getBizTerm();
    }

    @Override
    public ULong value15() {
        return getOwnerTopLevelAsbiepId();
    }

    @Override
    public AbieRecord value1(ULong value) {
        setAbieId(value);
        return this;
    }

    @Override
    public AbieRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public AbieRecord value3(ULong value) {
        setBasedAccManifestId(value);
        return this;
    }

    @Override
    public AbieRecord value4(String value) {
        setPath(value);
        return this;
    }

    @Override
    public AbieRecord value5(String value) {
        setHashPath(value);
        return this;
    }

    @Override
    public AbieRecord value6(ULong value) {
        setBizCtxId(value);
        return this;
    }

    @Override
    public AbieRecord value7(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public AbieRecord value8(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public AbieRecord value9(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public AbieRecord value10(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public AbieRecord value11(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public AbieRecord value12(Integer value) {
        setState(value);
        return this;
    }

    @Override
    public AbieRecord value13(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public AbieRecord value14(String value) {
        setBizTerm(value);
        return this;
    }

    @Override
    public AbieRecord value15(ULong value) {
        setOwnerTopLevelAsbiepId(value);
        return this;
    }

    @Override
    public AbieRecord values(ULong value1, String value2, ULong value3, String value4, String value5, ULong value6, String value7, ULong value8, ULong value9, LocalDateTime value10, LocalDateTime value11, Integer value12, String value13, String value14, ULong value15) {
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
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AbieRecord
     */
    public AbieRecord() {
        super(Abie.ABIE);
    }

    /**
     * Create a detached, initialised AbieRecord
     */
    public AbieRecord(ULong abieId, String guid, ULong basedAccManifestId, String path, String hashPath, ULong bizCtxId, String definition, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, Integer state, String remark, String bizTerm, ULong ownerTopLevelAsbiepId) {
        super(Abie.ABIE);

        setAbieId(abieId);
        setGuid(guid);
        setBasedAccManifestId(basedAccManifestId);
        setPath(path);
        setHashPath(hashPath);
        setBizCtxId(bizCtxId);
        setDefinition(definition);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setState(state);
        setRemark(remark);
        setBizTerm(bizTerm);
        setOwnerTopLevelAsbiepId(ownerTopLevelAsbiepId);
    }
}