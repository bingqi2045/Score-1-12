/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BbieSc;


/**
 * Because there is no single table that is a contextualized counterpart of the
 * DT table (which stores both CDT and BDT), The context specific constraints
 * associated with the DT are stored in the BBIE table, while this table stores
 * the constraints associated with the DT's SCs. 
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbieScRecord extends UpdatableRecordImpl<BbieScRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bbie_sc.bbie_sc_id</code>. Primary, internal
     * database key.
     */
    public void setBbieScId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.bbie_sc_id</code>. Primary, internal
     * database key.
     */
    public String getBbieScId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.bbie_sc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.guid</code>. A globally unique identifier
     * (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bbie_sc.based_dt_sc_manifest_id</code>. Foreign key
     * to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the
     * BDT of the based BCC and BCCP.
     */
    public void setBasedDtScManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.based_dt_sc_manifest_id</code>. Foreign key
     * to the DT_SC_MANIFEST table. This should correspond to the DT_SC of the
     * BDT of the based BCC and BCCP.
     */
    public ULong getBasedDtScManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.bbie_sc.path</code>.
     */
    public void setPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.path</code>.
     */
    public String getPath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bbie_sc.hash_path</code>. hash_path generated from
     * the path of the component graph using hash function, so that it is unique
     * in the graph.
     */
    public void setHashPath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.hash_path</code>. hash_path generated from
     * the path of the component graph using hash function, so that it is unique
     * in the graph.
     */
    public String getHashPath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.bbie_sc.bbie_id</code>. The BBIE this BBIE_SC
     * applies to.
     */
    public void setBbieId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.bbie_id</code>. The BBIE this BBIE_SC
     * applies to.
     */
    public String getBbieId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.bbie_sc.dt_sc_pri_restri_id</code>. This must be
     * one of the allowed primitive/code list as specified in the corresponding
     * SC of the based BCC of the BBIE (referred to by the BBIE_ID column).
     * 
     * It is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the
     * primitive assigned to the BBIE (or also can be viewed as assigned to the
     * BBIEP for this specific association). This is assigned by the user who
     * authors the BIE. The assignment would override the default from the CC
     * side.
     * 
     * This column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot
     * have a value at the same time.
     */
    public void setDtScPriRestriId(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.dt_sc_pri_restri_id</code>. This must be
     * one of the allowed primitive/code list as specified in the corresponding
     * SC of the based BCC of the BBIE (referred to by the BBIE_ID column).
     * 
     * It is the foreign key to the BDT_SC_PRI_RESTRI table. It indicates the
     * primitive assigned to the BBIE (or also can be viewed as assigned to the
     * BBIEP for this specific association). This is assigned by the user who
     * authors the BIE. The assignment would override the default from the CC
     * side.
     * 
     * This column, the CODE_LIST_ID column, and AGENCY_ID_LIST_ID column cannot
     * have a value at the same time.
     */
    public ULong getDtScPriRestriId() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.bbie_sc.code_list_id</code>. This is a foreign key
     * to the CODE_LIST table. If a code list is assigned to the BBIE SC (or
     * also can be viewed as assigned to the BBIEP SC for this association),
     * then this column stores the assigned code list. It should be noted that
     * one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID
     * column may also be a code list. So this column is typically used when the
     * user wants to assign another code list different from the one permissible
     * by the CC model.
     * 
     * This column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID
     * column cannot have a value at the same time.
     */
    public void setCodeListId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.code_list_id</code>. This is a foreign key
     * to the CODE_LIST table. If a code list is assigned to the BBIE SC (or
     * also can be viewed as assigned to the BBIEP SC for this association),
     * then this column stores the assigned code list. It should be noted that
     * one of the possible primitives assignable to the DT_SC_PRI_RESTRI_ID
     * column may also be a code list. So this column is typically used when the
     * user wants to assign another code list different from the one permissible
     * by the CC model.
     * 
     * This column is, the DT_SC_PRI_RESTRI_ID column, and AGENCY_ID_LIST_ID
     * column cannot have a value at the same time.
     */
    public String getCodeListId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.bbie_sc.agency_id_list_id</code>. This is a foreign
     * key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the
     * BBIE SC (or also can be viewed as assigned to the BBIEP SC for this
     * association), then this column stores the assigned Agency ID list. It
     * should be noted that one of the possible primitives assignable to the
     * DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column
     * is typically used only when the user wants to assign another Agency ID
     * list different from the one permissible by the CC model.
     * 
     * This column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column
     * cannot have a value at the same time.
     */
    public void setAgencyIdListId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.agency_id_list_id</code>. This is a foreign
     * key to the AGENCY_ID_LIST table. If a agency ID list is assigned to the
     * BBIE SC (or also can be viewed as assigned to the BBIEP SC for this
     * association), then this column stores the assigned Agency ID list. It
     * should be noted that one of the possible primitives assignable to the
     * DT_SC_PRI_RESTRI_ID column may also be an Agency ID list. So this column
     * is typically used only when the user wants to assign another Agency ID
     * list different from the one permissible by the CC model.
     * 
     * This column, the DT_SC_PRI_RESTRI_ID column, and CODE_LIST_ID column
     * cannot have a value at the same time.
     */
    public String getAgencyIdListId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.bbie_sc.cardinality_min</code>. The minimum
     * occurrence constraint for the BBIE SC. A valid value is 0 or 1.
     */
    public void setCardinalityMin(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.cardinality_min</code>. The minimum
     * occurrence constraint for the BBIE SC. A valid value is 0 or 1.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>oagi.bbie_sc.cardinality_max</code>. Maximum occurence
     * constraint of the BBIE SC. A valid value is 0 or 1.
     */
    public void setCardinalityMax(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.cardinality_max</code>. Maximum occurence
     * constraint of the BBIE SC. A valid value is 0 or 1.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>oagi.bbie_sc.facet_min_length</code>. Defines the
     * minimum number of units of length.
     */
    public void setFacetMinLength(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.facet_min_length</code>. Defines the
     * minimum number of units of length.
     */
    public ULong getFacetMinLength() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.bbie_sc.facet_max_length</code>. Defines the
     * minimum number of units of length.
     */
    public void setFacetMaxLength(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.facet_max_length</code>. Defines the
     * minimum number of units of length.
     */
    public ULong getFacetMaxLength() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.bbie_sc.facet_pattern</code>. Defines a constraint
     * on the lexical space of a datatype to literals in a specific pattern.
     */
    public void setFacetPattern(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.facet_pattern</code>. Defines a constraint
     * on the lexical space of a datatype to literals in a specific pattern.
     */
    public String getFacetPattern() {
        return (String) get(13);
    }

    /**
     * Setter for <code>oagi.bbie_sc.default_value</code>. This column specifies
     * the default value constraint. Default and fixed value constraints cannot
     * be used at the same time.
     */
    public void setDefaultValue(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.default_value</code>. This column specifies
     * the default value constraint. Default and fixed value constraints cannot
     * be used at the same time.
     */
    public String getDefaultValue() {
        return (String) get(14);
    }

    /**
     * Setter for <code>oagi.bbie_sc.fixed_value</code>. This column captures
     * the fixed value constraint. Default and fixed value constraints cannot be
     * used at the same time.
     */
    public void setFixedValue(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.fixed_value</code>. This column captures
     * the fixed value constraint. Default and fixed value constraints cannot be
     * used at the same time.
     */
    public String getFixedValue() {
        return (String) get(15);
    }

    /**
     * Setter for <code>oagi.bbie_sc.definition</code>. Description to override
     * the BDT SC definition. If NULL, it means that the definition should be
     * inherited from the based BDT SC.
     */
    public void setDefinition(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.definition</code>. Description to override
     * the BDT SC definition. If NULL, it means that the definition should be
     * inherited from the based BDT SC.
     */
    public String getDefinition() {
        return (String) get(16);
    }

    /**
     * Setter for <code>oagi.bbie_sc.example</code>.
     */
    public void setExample(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.example</code>.
     */
    public String getExample() {
        return (String) get(17);
    }

    /**
     * Setter for <code>oagi.bbie_sc.remark</code>. This column allows the user
     * to specify a very context-specific usage of the BBIE SC. It is different
     * from the Definition column in that the Definition column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. 
     */
    public void setRemark(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.remark</code>. This column allows the user
     * to specify a very context-specific usage of the BBIE SC. It is different
     * from the Definition column in that the Definition column is a description
     * conveying the meaning of the associated concept. Remarks may be a very
     * implementation specific instruction or others. 
     */
    public String getRemark() {
        return (String) get(18);
    }

    /**
     * Setter for <code>oagi.bbie_sc.biz_term</code>. Business term to indicate
     * what the BBIE SC is called in a particular business context. With this
     * current design, only one business term is allowed per business context.
     */
    public void setBizTerm(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.biz_term</code>. Business term to indicate
     * what the BBIE SC is called in a particular business context. With this
     * current design, only one business term is allowed per business context.
     */
    public String getBizTerm() {
        return (String) get(19);
    }

    /**
     * Setter for <code>oagi.bbie_sc.is_used</code>. Flag to indicate whether
     * the field/component is used in the content model. It indicates whether
     * the field/component should be generated.
     */
    public void setIsUsed(Byte value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.is_used</code>. Flag to indicate whether
     * the field/component is used in the content model. It indicates whether
     * the field/component should be generated.
     */
    public Byte getIsUsed() {
        return (Byte) get(20);
    }

    /**
     * Setter for <code>oagi.bbie_sc.created_by</code>. A foreign key referring
     * to the user who creates the BBIE_SC. The creator of the BBIE_SC is also
     * its owner by default.
     */
    public void setCreatedBy(String value) {
        set(21, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.created_by</code>. A foreign key referring
     * to the user who creates the BBIE_SC. The creator of the BBIE_SC is also
     * its owner by default.
     */
    public String getCreatedBy() {
        return (String) get(21);
    }

    /**
     * Setter for <code>oagi.bbie_sc.last_updated_by</code>. A foreign key
     * referring to the user who has last updated the BBIE_SC record.
     */
    public void setLastUpdatedBy(String value) {
        set(22, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.last_updated_by</code>. A foreign key
     * referring to the user who has last updated the BBIE_SC record.
     */
    public String getLastUpdatedBy() {
        return (String) get(22);
    }

    /**
     * Setter for <code>oagi.bbie_sc.creation_timestamp</code>. Timestamp when
     * the BBIE_SC record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(23, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.creation_timestamp</code>. Timestamp when
     * the BBIE_SC record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(23);
    }

    /**
     * Setter for <code>oagi.bbie_sc.last_update_timestamp</code>. The timestamp
     * when the BBIE_SC was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(24, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.last_update_timestamp</code>. The timestamp
     * when the BBIE_SC was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(24);
    }

    /**
     * Setter for <code>oagi.bbie_sc.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public void setOwnerTopLevelAsbiepId(String value) {
        set(25, value);
    }

    /**
     * Getter for <code>oagi.bbie_sc.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public String getOwnerTopLevelAsbiepId() {
        return (String) get(25);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BbieScRecord
     */
    public BbieScRecord() {
        super(BbieSc.BBIE_SC);
    }

    /**
     * Create a detached, initialised BbieScRecord
     */
    public BbieScRecord(String bbieScId, String guid, ULong basedDtScManifestId, String path, String hashPath, String bbieId, ULong dtScPriRestriId, String codeListId, String agencyIdListId, Integer cardinalityMin, Integer cardinalityMax, ULong facetMinLength, ULong facetMaxLength, String facetPattern, String defaultValue, String fixedValue, String definition, String example, String remark, String bizTerm, Byte isUsed, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String ownerTopLevelAsbiepId) {
        super(BbieSc.BBIE_SC);

        setBbieScId(bbieScId);
        setGuid(guid);
        setBasedDtScManifestId(basedDtScManifestId);
        setPath(path);
        setHashPath(hashPath);
        setBbieId(bbieId);
        setDtScPriRestriId(dtScPriRestriId);
        setCodeListId(codeListId);
        setAgencyIdListId(agencyIdListId);
        setCardinalityMin(cardinalityMin);
        setCardinalityMax(cardinalityMax);
        setFacetMinLength(facetMinLength);
        setFacetMaxLength(facetMaxLength);
        setFacetPattern(facetPattern);
        setDefaultValue(defaultValue);
        setFixedValue(fixedValue);
        setDefinition(definition);
        setExample(example);
        setRemark(remark);
        setBizTerm(bizTerm);
        setIsUsed(isUsed);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setOwnerTopLevelAsbiepId(ownerTopLevelAsbiepId);
    }
}
