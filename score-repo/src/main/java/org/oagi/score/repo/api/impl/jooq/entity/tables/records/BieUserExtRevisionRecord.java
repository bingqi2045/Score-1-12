/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BieUserExtRevision;


/**
 * This table is a log of events. It keeps track of the User Extension ACC (the
 * specific revision) used by an Extension ABIE. This can be a named extension
 * (such as ApplicationAreaExtension) or the AllExtension. The REVISED_INDICATOR
 * flag is designed such that a revision of a User Extension can notify the user
 * of a top-level ABIE by setting this flag to true. The TOP_LEVEL_ABIE_ID
 * column makes it more efficient to when opening a top-level ABIE, the user can
 * be notified of any new revision of the extension. A record in this table is
 * created only when there is a user extension to the the OAGIS extension
 * component/ACC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BieUserExtRevisionRecord extends UpdatableRecordImpl<BieUserExtRevisionRecord> implements Record6<String, String, String, String, Byte, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for
     * <code>oagi.bie_user_ext_revision.bie_user_ext_revision_id</code>.
     * Primary, internal database key.
     */
    public void setBieUserExtRevisionId(String value) {
        set(0, value);
    }

    /**
     * Getter for
     * <code>oagi.bie_user_ext_revision.bie_user_ext_revision_id</code>.
     * Primary, internal database key.
     */
    public String getBieUserExtRevisionId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.bie_user_ext_revision.ext_abie_id</code>. This
     * points to an ABIE record corresponding to the EXTENSION_ACC_ID record.
     * For example, this column can point to the ApplicationAreaExtension ABIE
     * which is based on the ApplicationAreaExtension ACC (referred to by the
     * EXT_ACC_ID column). This column can be NULL only when the extension is
     * the AllExtension because there is no corresponding ABIE for the
     * AllExtension ACC.
     */
    public void setExtAbieId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.bie_user_ext_revision.ext_abie_id</code>. This
     * points to an ABIE record corresponding to the EXTENSION_ACC_ID record.
     * For example, this column can point to the ApplicationAreaExtension ABIE
     * which is based on the ApplicationAreaExtension ACC (referred to by the
     * EXT_ACC_ID column). This column can be NULL only when the extension is
     * the AllExtension because there is no corresponding ABIE for the
     * AllExtension ACC.
     */
    public String getExtAbieId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.bie_user_ext_revision.ext_acc_id</code>. This
     * points to an extension ACC on which the ABIE indicated by the EXT_ABIE_ID
     * column is based. E.g. It may point to an ApplicationAreaExtension ACC,
     * AllExtension ACC, ActualLedgerExtension ACC, etc. It should be noted that
     * an ACC record pointed to must have the OAGIS_COMPONENT_TYPE = 2
     * (Extension).
     */
    public void setExtAccId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.bie_user_ext_revision.ext_acc_id</code>. This
     * points to an extension ACC on which the ABIE indicated by the EXT_ABIE_ID
     * column is based. E.g. It may point to an ApplicationAreaExtension ACC,
     * AllExtension ACC, ActualLedgerExtension ACC, etc. It should be noted that
     * an ACC record pointed to must have the OAGIS_COMPONENT_TYPE = 2
     * (Extension).
     */
    public String getExtAccId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.bie_user_ext_revision.user_ext_acc_id</code>. This
     * column points to the specific revision of a User Extension ACC (this is
     * an ACC whose OAGIS_COMPONENT_TYPE = 4) currently used by the ABIE as
     * indicated by the EXT_ABIE_ID or the by the TOP_LEVEL_ABIE_ID (in case of
     * the AllExtension).
     */
    public void setUserExtAccId(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.bie_user_ext_revision.user_ext_acc_id</code>. This
     * column points to the specific revision of a User Extension ACC (this is
     * an ACC whose OAGIS_COMPONENT_TYPE = 4) currently used by the ABIE as
     * indicated by the EXT_ABIE_ID or the by the TOP_LEVEL_ABIE_ID (in case of
     * the AllExtension).
     */
    public String getUserExtAccId() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.bie_user_ext_revision.revised_indicator</code>.
     * This column is a flag indicating to whether the User Extension ACC (as
     * identified in the USER_EXT_ACC_ID column) has been revised, i.e., there
     * is a newer version of the user extension ACC than the one currently used
     * by the EXT_ABIE_ID. 0 means the USER_EXT_ACC_ID is current, 1 means it is
     * not current.
     */
    public void setRevisedIndicator(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.bie_user_ext_revision.revised_indicator</code>.
     * This column is a flag indicating to whether the User Extension ACC (as
     * identified in the USER_EXT_ACC_ID column) has been revised, i.e., there
     * is a newer version of the user extension ACC than the one currently used
     * by the EXT_ABIE_ID. 0 means the USER_EXT_ACC_ID is current, 1 means it is
     * not current.
     */
    public Byte getRevisedIndicator() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>oagi.bie_user_ext_revision.top_level_asbiep_id</code>.
     * This is a foreign key to the top-level ASBIEP.
     */
    public void setTopLevelAsbiepId(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.bie_user_ext_revision.top_level_asbiep_id</code>.
     * This is a foreign key to the top-level ASBIEP.
     */
    public String getTopLevelAsbiepId() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String, Byte, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<String, String, String, String, Byte, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return BieUserExtRevision.BIE_USER_EXT_REVISION.BIE_USER_EXT_REVISION_ID;
    }

    @Override
    public Field<String> field2() {
        return BieUserExtRevision.BIE_USER_EXT_REVISION.EXT_ABIE_ID;
    }

    @Override
    public Field<String> field3() {
        return BieUserExtRevision.BIE_USER_EXT_REVISION.EXT_ACC_ID;
    }

    @Override
    public Field<String> field4() {
        return BieUserExtRevision.BIE_USER_EXT_REVISION.USER_EXT_ACC_ID;
    }

    @Override
    public Field<Byte> field5() {
        return BieUserExtRevision.BIE_USER_EXT_REVISION.REVISED_INDICATOR;
    }

    @Override
    public Field<String> field6() {
        return BieUserExtRevision.BIE_USER_EXT_REVISION.TOP_LEVEL_ASBIEP_ID;
    }

    @Override
    public String component1() {
        return getBieUserExtRevisionId();
    }

    @Override
    public String component2() {
        return getExtAbieId();
    }

    @Override
    public String component3() {
        return getExtAccId();
    }

    @Override
    public String component4() {
        return getUserExtAccId();
    }

    @Override
    public Byte component5() {
        return getRevisedIndicator();
    }

    @Override
    public String component6() {
        return getTopLevelAsbiepId();
    }

    @Override
    public String value1() {
        return getBieUserExtRevisionId();
    }

    @Override
    public String value2() {
        return getExtAbieId();
    }

    @Override
    public String value3() {
        return getExtAccId();
    }

    @Override
    public String value4() {
        return getUserExtAccId();
    }

    @Override
    public Byte value5() {
        return getRevisedIndicator();
    }

    @Override
    public String value6() {
        return getTopLevelAsbiepId();
    }

    @Override
    public BieUserExtRevisionRecord value1(String value) {
        setBieUserExtRevisionId(value);
        return this;
    }

    @Override
    public BieUserExtRevisionRecord value2(String value) {
        setExtAbieId(value);
        return this;
    }

    @Override
    public BieUserExtRevisionRecord value3(String value) {
        setExtAccId(value);
        return this;
    }

    @Override
    public BieUserExtRevisionRecord value4(String value) {
        setUserExtAccId(value);
        return this;
    }

    @Override
    public BieUserExtRevisionRecord value5(Byte value) {
        setRevisedIndicator(value);
        return this;
    }

    @Override
    public BieUserExtRevisionRecord value6(String value) {
        setTopLevelAsbiepId(value);
        return this;
    }

    @Override
    public BieUserExtRevisionRecord values(String value1, String value2, String value3, String value4, Byte value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BieUserExtRevisionRecord
     */
    public BieUserExtRevisionRecord() {
        super(BieUserExtRevision.BIE_USER_EXT_REVISION);
    }

    /**
     * Create a detached, initialised BieUserExtRevisionRecord
     */
    public BieUserExtRevisionRecord(String bieUserExtRevisionId, String extAbieId, String extAccId, String userExtAccId, Byte revisedIndicator, String topLevelAsbiepId) {
        super(BieUserExtRevision.BIE_USER_EXT_REVISION);

        setBieUserExtRevisionId(bieUserExtRevisionId);
        setExtAbieId(extAbieId);
        setExtAccId(extAccId);
        setUserExtAccId(userExtAccId);
        setRevisedIndicator(revisedIndicator);
        setTopLevelAsbiepId(topLevelAsbiepId);
    }
}
