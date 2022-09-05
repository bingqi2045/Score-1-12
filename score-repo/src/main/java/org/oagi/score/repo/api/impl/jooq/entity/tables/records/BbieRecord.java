/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bbie;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * A BBIE represents a relationship/association between an ABIE and a BBIEP. It
 * is a contextualization of a BCC. The BBIE table also stores some information
 * about the specific constraints related to the BDT associated with the BBIEP.
 * In particular, the three columns including the BDT_PRI_RESTRI_ID,
 * CODE_LIST_ID, and AGENCY_ID_LIST_ID allows for capturing of the specific
 * primitive to be used in the context. Only one column among the three can have
 * a value in a particular record.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbieRecord extends UpdatableRecordImpl<BbieRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.bbie.bbie_id</code>. Primary, internal database
     * key.
     */
    public void setBbieId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.bbie.bbie_id</code>. Primary, internal database
     * key.
     */
    public String getBbieId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.bbie.guid</code>. A globally unique identifier
     * (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bbie.guid</code>. A globally unique identifier
     * (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bbie.based_bcc_manifest_id</code>. The
     * BASED_BCC_MANIFEST_ID column refers to the BCC_MANIFEST record, which
     * this BBIE contextualizes.
     */
    public void setBasedBccManifestId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bbie.based_bcc_manifest_id</code>. The
     * BASED_BCC_MANIFEST_ID column refers to the BCC_MANIFEST record, which
     * this BBIE contextualizes.
     */
    public String getBasedBccManifestId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.bbie.path</code>.
     */
    public void setPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bbie.path</code>.
     */
    public String getPath() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bbie.hash_path</code>. hash_path generated from the
     * path of the component graph using hash function, so that it is unique in
     * the graph.
     */
    public void setHashPath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bbie.hash_path</code>. hash_path generated from the
     * path of the component graph using hash function, so that it is unique in
     * the graph.
     */
    public String getHashPath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.bbie.from_abie_id</code>. A foreign key pointing to
     * the ABIE table. FROM_ABIE_ID is basically  a parent data element (type)
     * of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the
     * BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC
     * or USER_EXTENSION_GROUP ACC.FROM_ABIE_ID must be based on the FROM_ACC_ID
     * in the BASED_BCC_ID.
     */
    public void setFromAbieId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bbie.from_abie_id</code>. A foreign key pointing to
     * the ABIE table. FROM_ABIE_ID is basically  a parent data element (type)
     * of the TO_ASBIEP_ID. FROM_ABIE_ID must be based on the FROM_ACC_ID in the
     * BASED_ASCC_ID except when the FROM_ACC_ID refers to an SEMANTIC_GROUP ACC
     * or USER_EXTENSION_GROUP ACC.FROM_ABIE_ID must be based on the FROM_ACC_ID
     * in the BASED_BCC_ID.
     */
    public String getFromAbieId() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.bbie.to_bbiep_id</code>. TO_BBIEP_ID is a foreign
     * key to the BBIEP table. TO_BBIEP_ID basically refers to a child data
     * element of the FROM_ABIE_ID. TO_BBIEP_ID must be based on the TO_BCCP_ID
     * in the based BCC.
     */
    public void setToBbiepId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.bbie.to_bbiep_id</code>. TO_BBIEP_ID is a foreign
     * key to the BBIEP table. TO_BBIEP_ID basically refers to a child data
     * element of the FROM_ABIE_ID. TO_BBIEP_ID must be based on the TO_BCCP_ID
     * in the based BCC.
     */
    public String getToBbiepId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.bbie.bdt_pri_restri_id</code>. This is the foreign
     * key to the BDT_PRI_RESTRI table. It indicates the primitive assigned to
     * the BBIE (or also can be viewed as assigned to the BBIEP for this
     * specific association). This is assigned by the user who authors the BIE.
     * The assignment would override the default from the CC side.
     */
    public void setBdtPriRestriId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.bbie.bdt_pri_restri_id</code>. This is the foreign
     * key to the BDT_PRI_RESTRI table. It indicates the primitive assigned to
     * the BBIE (or also can be viewed as assigned to the BBIEP for this
     * specific association). This is assigned by the user who authors the BIE.
     * The assignment would override the default from the CC side.
     */
    public String getBdtPriRestriId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.bbie.code_list_id</code>. This is a foreign key to
     * the CODE_LIST table. If a code list is assigned to the BBIE (or also can
     * be viewed as assigned to the BBIEP for this association), then this
     * column stores the assigned code list. It should be noted that one of the
     * possible primitives assignable to the BDT_PRI_RESTRI_ID column may also
     * be a code list. So this column is typically used when the user wants to
     * assign another code list different from the one permissible by the CC
     * model.
     */
    public void setCodeListId(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.bbie.code_list_id</code>. This is a foreign key to
     * the CODE_LIST table. If a code list is assigned to the BBIE (or also can
     * be viewed as assigned to the BBIEP for this association), then this
     * column stores the assigned code list. It should be noted that one of the
     * possible primitives assignable to the BDT_PRI_RESTRI_ID column may also
     * be a code list. So this column is typically used when the user wants to
     * assign another code list different from the one permissible by the CC
     * model.
     */
    public String getCodeListId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.bbie.agency_id_list_id</code>. This is a foreign
     * key to the AGENCY_ID_LIST table. It is used in the case that the BDT
     * content can be restricted to an agency identification.
     */
    public void setAgencyIdListId(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.bbie.agency_id_list_id</code>. This is a foreign
     * key to the AGENCY_ID_LIST table. It is used in the case that the BDT
     * content can be restricted to an agency identification.
     */
    public String getAgencyIdListId() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.bbie.cardinality_min</code>. The minimum occurrence
     * constraint for the BBIE. A valid value is a non-negative integer.
     */
    public void setCardinalityMin(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.bbie.cardinality_min</code>. The minimum occurrence
     * constraint for the BBIE. A valid value is a non-negative integer.
     */
    public Integer getCardinalityMin() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>oagi.bbie.cardinality_max</code>. Maximum occurence
     * constraint of the TO_BBIEP_ID. A valid value is an integer from -1 and
     * up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public void setCardinalityMax(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.bbie.cardinality_max</code>. Maximum occurence
     * constraint of the TO_BBIEP_ID. A valid value is an integer from -1 and
     * up. Specifically, -1 means unbounded. 0 means prohibited or not to use.
     */
    public Integer getCardinalityMax() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>oagi.bbie.facet_min_length</code>. Defines the minimum
     * number of units of length.
     */
    public void setFacetMinLength(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.bbie.facet_min_length</code>. Defines the minimum
     * number of units of length.
     */
    public ULong getFacetMinLength() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.bbie.facet_max_length</code>. Defines the minimum
     * number of units of length.
     */
    public void setFacetMaxLength(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.bbie.facet_max_length</code>. Defines the minimum
     * number of units of length.
     */
    public ULong getFacetMaxLength() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.bbie.facet_pattern</code>. Defines a constraint on
     * the lexical space of a datatype to literals in a specific pattern.
     */
    public void setFacetPattern(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.bbie.facet_pattern</code>. Defines a constraint on
     * the lexical space of a datatype to literals in a specific pattern.
     */
    public String getFacetPattern() {
        return (String) get(14);
    }

    /**
     * Setter for <code>oagi.bbie.default_value</code>. This column specifies
     * the default value constraint. Default and fixed value constraints cannot
     * be used at the same time.
     */
    public void setDefaultValue(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.bbie.default_value</code>. This column specifies
     * the default value constraint. Default and fixed value constraints cannot
     * be used at the same time.
     */
    public String getDefaultValue() {
        return (String) get(15);
    }

    /**
     * Setter for <code>oagi.bbie.is_nillable</code>. Indicate whether the field
     * can have a null  This is corresponding to the nillable flag in the XML
     * schema.
     */
    public void setIsNillable(Byte value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.bbie.is_nillable</code>. Indicate whether the field
     * can have a null  This is corresponding to the nillable flag in the XML
     * schema.
     */
    public Byte getIsNillable() {
        return (Byte) get(16);
    }

    /**
     * Setter for <code>oagi.bbie.fixed_value</code>. This column captures the
     * fixed value constraint. Default and fixed value constraints cannot be
     * used at the same time.
     */
    public void setFixedValue(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.bbie.fixed_value</code>. This column captures the
     * fixed value constraint. Default and fixed value constraints cannot be
     * used at the same time.
     */
    public String getFixedValue() {
        return (String) get(17);
    }

    /**
     * Setter for <code>oagi.bbie.is_null</code>. This column indicates whether
     * the field is fixed to NULL. IS_NULLl can be true only if the IS_NILLABLE
     * is true. If IS_NULL is true then the FIX_VALUE and DEFAULT_VALUE columns
     * cannot have a value.
     */
    public void setIsNull(Byte value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.bbie.is_null</code>. This column indicates whether
     * the field is fixed to NULL. IS_NULLl can be true only if the IS_NILLABLE
     * is true. If IS_NULL is true then the FIX_VALUE and DEFAULT_VALUE columns
     * cannot have a value.
     */
    public Byte getIsNull() {
        return (Byte) get(18);
    }

    /**
     * Setter for <code>oagi.bbie.definition</code>. Description to override the
     * BCC definition. If NULLl, it means that the definition should be
     * inherited from the based BCC.
     */
    public void setDefinition(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.bbie.definition</code>. Description to override the
     * BCC definition. If NULLl, it means that the definition should be
     * inherited from the based BCC.
     */
    public String getDefinition() {
        return (String) get(19);
    }

    /**
     * Setter for <code>oagi.bbie.example</code>.
     */
    public void setExample(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.bbie.example</code>.
     */
    public String getExample() {
        return (String) get(20);
    }

    /**
     * Setter for <code>oagi.bbie.remark</code>. This column allows the user to
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
        set(21, value);
    }

    /**
     * Getter for <code>oagi.bbie.remark</code>. This column allows the user to
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
        return (String) get(21);
    }

    /**
     * Setter for <code>oagi.bbie.created_by</code>. A foreign key referring to
     * the user who creates the BBIE. The creator of the BBIE is also its owner
     * by default. BBIEs created as children of another ABIE have the same
     * CREATED_BY.
     */
    public void setCreatedBy(String value) {
        set(22, value);
    }

    /**
     * Getter for <code>oagi.bbie.created_by</code>. A foreign key referring to
     * the user who creates the BBIE. The creator of the BBIE is also its owner
     * by default. BBIEs created as children of another ABIE have the same
     * CREATED_BY.
     */
    public String getCreatedBy() {
        return (String) get(22);
    }

    /**
     * Setter for <code>oagi.bbie.last_updated_by</code>. A foreign key
     * referring to the user who has last updated the BBIE record.
     */
    public void setLastUpdatedBy(String value) {
        set(23, value);
    }

    /**
     * Getter for <code>oagi.bbie.last_updated_by</code>. A foreign key
     * referring to the user who has last updated the BBIE record.
     */
    public String getLastUpdatedBy() {
        return (String) get(23);
    }

    /**
     * Setter for <code>oagi.bbie.creation_timestamp</code>. Timestamp when the
     * BBIE record was first created. BBIEs created as children of another ABIE
     * have the same CREATION_TIMESTAMP.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(24, value);
    }

    /**
     * Getter for <code>oagi.bbie.creation_timestamp</code>. Timestamp when the
     * BBIE record was first created. BBIEs created as children of another ABIE
     * have the same CREATION_TIMESTAMP.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(24);
    }

    /**
     * Setter for <code>oagi.bbie.last_update_timestamp</code>. The timestamp
     * when the ASBIE was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(25, value);
    }

    /**
     * Getter for <code>oagi.bbie.last_update_timestamp</code>. The timestamp
     * when the ASBIE was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(25);
    }

    /**
     * Setter for <code>oagi.bbie.seq_key</code>. This indicates the order of
     * the associations among other siblings. The SEQ_KEY for BIEs is decimal in
     * order to accomodate the removal of inheritance hierarchy and group. For
     * example, children of the most abstract ACC will have SEQ_KEY = 1.1, 1.2,
     * 1.3, and so on; and SEQ_KEY of the next abstraction level ACC will have
     * SEQ_KEY = 2.1, 2.2, 2.3 and so on so forth.
     */
    public void setSeqKey(BigDecimal value) {
        set(26, value);
    }

    /**
     * Getter for <code>oagi.bbie.seq_key</code>. This indicates the order of
     * the associations among other siblings. The SEQ_KEY for BIEs is decimal in
     * order to accomodate the removal of inheritance hierarchy and group. For
     * example, children of the most abstract ACC will have SEQ_KEY = 1.1, 1.2,
     * 1.3, and so on; and SEQ_KEY of the next abstraction level ACC will have
     * SEQ_KEY = 2.1, 2.2, 2.3 and so on so forth.
     */
    public BigDecimal getSeqKey() {
        return (BigDecimal) get(26);
    }

    /**
     * Setter for <code>oagi.bbie.is_used</code>. Flag to indicate whether the
     * field/component is used in the content model. It indicates whether the
     * field/component should be generated in the expression generation.
     */
    public void setIsUsed(Byte value) {
        set(27, value);
    }

    /**
     * Getter for <code>oagi.bbie.is_used</code>. Flag to indicate whether the
     * field/component is used in the content model. It indicates whether the
     * field/component should be generated in the expression generation.
     */
    public Byte getIsUsed() {
        return (Byte) get(27);
    }

    /**
     * Setter for <code>oagi.bbie.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public void setOwnerTopLevelAsbiepId(String value) {
        set(28, value);
    }

    /**
     * Getter for <code>oagi.bbie.owner_top_level_asbiep_id</code>. This is a
     * foreign key to the top-level ASBIEP.
     */
    public String getOwnerTopLevelAsbiepId() {
        return (String) get(28);
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
     * Create a detached BbieRecord
     */
    public BbieRecord() {
        super(Bbie.BBIE);
    }

    /**
     * Create a detached, initialised BbieRecord
     */
    public BbieRecord(String bbieId, String guid, String basedBccManifestId, String path, String hashPath, String fromAbieId, String toBbiepId, String bdtPriRestriId, String codeListId, String agencyIdListId, Integer cardinalityMin, Integer cardinalityMax, ULong facetMinLength, ULong facetMaxLength, String facetPattern, String defaultValue, Byte isNillable, String fixedValue, Byte isNull, String definition, String example, String remark, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, BigDecimal seqKey, Byte isUsed, String ownerTopLevelAsbiepId) {
        super(Bbie.BBIE);

        setBbieId(bbieId);
        setGuid(guid);
        setBasedBccManifestId(basedBccManifestId);
        setPath(path);
        setHashPath(hashPath);
        setFromAbieId(fromAbieId);
        setToBbiepId(toBbiepId);
        setBdtPriRestriId(bdtPriRestriId);
        setCodeListId(codeListId);
        setAgencyIdListId(agencyIdListId);
        setCardinalityMin(cardinalityMin);
        setCardinalityMax(cardinalityMax);
        setFacetMinLength(facetMinLength);
        setFacetMaxLength(facetMaxLength);
        setFacetPattern(facetPattern);
        setDefaultValue(defaultValue);
        setIsNillable(isNillable);
        setFixedValue(fixedValue);
        setIsNull(isNull);
        setDefinition(definition);
        setExample(example);
        setRemark(remark);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setSeqKey(seqKey);
        setIsUsed(isUsed);
        setOwnerTopLevelAsbiepId(ownerTopLevelAsbiepId);
    }
}
