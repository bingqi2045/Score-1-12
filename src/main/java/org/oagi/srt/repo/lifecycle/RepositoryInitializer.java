package org.oagi.srt.repo.lifecycle;

import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.repo.RevisionRepository;
import org.oagi.srt.repo.domain.RevisionSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Component
public class RepositoryInitializer implements InitializingBean {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private RevisionRepository revisionRepository;
    
    @Autowired
    private RevisionSerializer serializer;

    @Override
    public void afterPropertiesSet() throws Exception {
        initAccRevision();
        initAsccRevision();
        initBccRevision();

        initAsccpRevision();
        initBccpRevision();

        initCodeListRevision();
        initCodeListValueRevision();

        initDtRevision();
        initDtScRevision();

        initXbtRevision();
    }

    private void initAccRevision() {
        // For 'Non-Working' releases.
        List<AccManifestRecord> accManifestRecordList = dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST).join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        ACC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(AccManifestRecord.class);

        for (AccManifestRecord accManifestRecord : accManifestRecordList) {
            AccRecord accRecord = dslContext.selectFrom(ACC)
                    .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                    .fetchOne();

            List<AsccRecord> asccRecords = dslContext.selectFrom(ASCC)
                    .where(ASCC.FROM_ACC_ID.eq(accRecord.getAccId()))
                    .fetch();

            List<BccRecord> bccRecords = dslContext.selectFrom(BCC)
                    .where(BCC.FROM_ACC_ID.eq(accRecord.getAccId()))
                    .fetch();

            RevisionRecord revisionRecord = new RevisionRecord();
            revisionRecord.setRevisionNum(UInteger.valueOf(1));
            revisionRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            revisionRecord.setRevisionAction(RevisionAction.Added.name());
            revisionRecord.setSnapshot(JSON.valueOf(
                    serializer.serialize(accRecord, asccRecords, bccRecords)
            ));
            revisionRecord.setCreatedBy(accRecord.getCreatedBy());
            revisionRecord.setCreationTimestamp(accRecord.getCreationTimestamp());

            ULong revisionId = dslContext.insertInto(REVISION)
                    .set(revisionRecord)
                    .returning(REVISION.REVISION_ID).fetchOne().getRevisionId();

            accManifestRecord.setRevisionId(revisionId);
            accManifestRecord.update(ACC_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        accManifestRecordList = dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST).join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        ACC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(AccManifestRecord.class);

        for (AccManifestRecord nextAccManifestRecord : accManifestRecordList) {
            AccManifestRecord prevAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                    .where(and(
                            ACC_MANIFEST.ACC_ID.equal(nextAccManifestRecord.getAccId()),
                            ACC_MANIFEST.RELEASE_ID.notEqual(nextAccManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevAccManifestRecord.setNextAccManifestId(nextAccManifestRecord.getAccManifestId());
            prevAccManifestRecord.update(ACC_MANIFEST.NEXT_ACC_MANIFEST_ID);

            nextAccManifestRecord.setPrevAccManifestId(prevAccManifestRecord.getAccManifestId());
            nextAccManifestRecord.setRevisionId(prevAccManifestRecord.getRevisionId());
            nextAccManifestRecord.update(ACC_MANIFEST.PREV_ACC_MANIFEST_ID, ACC_MANIFEST.REVISION_ID);
        }
    }

    private void initAsccRevision() {
        // For 'Non-Working' releases.
        List<AsccManifestRecord> asccManifestRecordList = dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST).join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        ASCC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(AsccManifestRecord.class);

        for (AsccManifestRecord asccManifestRecord : asccManifestRecordList) {
            ULong revisionId = dslContext.select(REVISION.REVISION_ID)
                    .from(REVISION)
                    .join(ACC_MANIFEST).on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asccManifestRecord.getFromAccManifestId()))
                    .fetchOne().get(REVISION.REVISION_ID);

            asccManifestRecord.setRevisionId(revisionId);
            asccManifestRecord.update(ASCC_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        asccManifestRecordList = dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST).join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        ASCC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(AsccManifestRecord.class);

        for (AsccManifestRecord nextAsccManifestRecord : asccManifestRecordList) {
            AsccManifestRecord prevAsccManifestRecord = dslContext.selectFrom(ASCC_MANIFEST)
                    .where(and(
                            ASCC_MANIFEST.ASCC_ID.equal(nextAsccManifestRecord.getAsccId()),
                            ASCC_MANIFEST.RELEASE_ID.notEqual(nextAsccManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevAsccManifestRecord.setNextAsccManifestId(nextAsccManifestRecord.getAsccManifestId());
            prevAsccManifestRecord.update(ASCC_MANIFEST.NEXT_ASCC_MANIFEST_ID);

            nextAsccManifestRecord.setPrevAsccManifestId(prevAsccManifestRecord.getAsccManifestId());
            nextAsccManifestRecord.setRevisionId(prevAsccManifestRecord.getRevisionId());
            nextAsccManifestRecord.update(ASCC_MANIFEST.PREV_ASCC_MANIFEST_ID, ASCC_MANIFEST.REVISION_ID);
        }
    }

    private void initBccRevision() {
        // For 'Non-Working' releases.
        List<BccManifestRecord> bccManifestRecordList = dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST).join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        BCC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(BccManifestRecord.class);

        for (BccManifestRecord bccManifestRecord : bccManifestRecordList) {
            ULong revisionId = dslContext.select(REVISION.REVISION_ID)
                    .from(REVISION)
                    .join(ACC_MANIFEST).on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                    .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(bccManifestRecord.getFromAccManifestId()))
                    .fetchOne().get(REVISION.REVISION_ID);

            bccManifestRecord.setRevisionId(revisionId);
            bccManifestRecord.update(BCC_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        bccManifestRecordList = dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST).join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        BCC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(BccManifestRecord.class);

        for (BccManifestRecord nextBccManifestRecord : bccManifestRecordList) {
            BccManifestRecord prevBccManifestRecord = dslContext.selectFrom(BCC_MANIFEST)
                    .where(and(
                            BCC_MANIFEST.BCC_ID.equal(nextBccManifestRecord.getBccId()),
                            BCC_MANIFEST.RELEASE_ID.notEqual(nextBccManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevBccManifestRecord.setNextBccManifestId(nextBccManifestRecord.getBccManifestId());
            prevBccManifestRecord.update(BCC_MANIFEST.NEXT_BCC_MANIFEST_ID);

            nextBccManifestRecord.setPrevBccManifestId(prevBccManifestRecord.getBccManifestId());
            nextBccManifestRecord.setRevisionId(prevBccManifestRecord.getRevisionId());
            nextBccManifestRecord.update(BCC_MANIFEST.PREV_BCC_MANIFEST_ID, BCC_MANIFEST.REVISION_ID);
        }
    }

    private void initAsccpRevision() {
        // For 'Non-Working' releases.
        List<AsccpManifestRecord> asccpManifestRecordList = dslContext.select(ASCCP_MANIFEST.fields())
                .from(ASCCP_MANIFEST).join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        ASCCP_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(AsccpManifestRecord.class);

        for (AsccpManifestRecord asccpManifestRecord : asccpManifestRecordList) {
            AsccpRecord asccpRecord = dslContext.selectFrom(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(asccpManifestRecord.getAsccpId()))
                    .fetchOne();

            RevisionRecord revisionRecord = new RevisionRecord();
            revisionRecord.setRevisionNum(UInteger.valueOf(1));
            revisionRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            revisionRecord.setRevisionAction(RevisionAction.Added.name());
            revisionRecord.setSnapshot(JSON.valueOf(serializer.serialize(asccpRecord)));
            revisionRecord.setCreatedBy(asccpRecord.getCreatedBy());
            revisionRecord.setCreationTimestamp(asccpRecord.getCreationTimestamp());

            ULong revisionId = dslContext.insertInto(REVISION)
                    .set(revisionRecord)
                    .returning(REVISION.REVISION_ID).fetchOne().getRevisionId();

            asccpManifestRecord.setRevisionId(revisionId);
            asccpManifestRecord.update(ASCCP_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        asccpManifestRecordList = dslContext.select(ASCCP_MANIFEST.fields())
                .from(ASCCP_MANIFEST).join(RELEASE).on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        ASCCP_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(AsccpManifestRecord.class);

        for (AsccpManifestRecord nextAsccpManifestRecord : asccpManifestRecordList) {
            AsccpManifestRecord prevAsccpManifestRecord = dslContext.selectFrom(ASCCP_MANIFEST)
                    .where(and(
                            ASCCP_MANIFEST.ASCCP_ID.equal(nextAsccpManifestRecord.getAsccpId()),
                            ASCCP_MANIFEST.RELEASE_ID.notEqual(nextAsccpManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevAsccpManifestRecord.setNextAsccpManifestId(nextAsccpManifestRecord.getAsccpManifestId());
            prevAsccpManifestRecord.update(ASCCP_MANIFEST.NEXT_ASCCP_MANIFEST_ID);

            nextAsccpManifestRecord.setPrevAsccpManifestId(prevAsccpManifestRecord.getAsccpManifestId());
            nextAsccpManifestRecord.setRevisionId(prevAsccpManifestRecord.getRevisionId());
            nextAsccpManifestRecord.update(ASCCP_MANIFEST.PREV_ASCCP_MANIFEST_ID, ASCCP_MANIFEST.REVISION_ID);
        }
    }

    private void initBccpRevision() {
        // For 'Non-Working' releases.
        List<BccpManifestRecord> bccpManifestRecordList = dslContext.select(BCCP_MANIFEST.fields())
                .from(BCCP_MANIFEST).join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        BCCP_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(BccpManifestRecord.class);

        for (BccpManifestRecord bccpManifestRecord : bccpManifestRecordList) {
            BccpRecord bccpRecord = dslContext.selectFrom(BCCP)
                    .where(BCCP.BCCP_ID.eq(bccpManifestRecord.getBccpId()))
                    .fetchOne();

            RevisionRecord revisionRecord = new RevisionRecord();
            revisionRecord.setRevisionNum(UInteger.valueOf(1));
            revisionRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            revisionRecord.setRevisionAction(RevisionAction.Added.name());
            revisionRecord.setSnapshot(JSON.valueOf(serializer.serialize(bccpRecord)));
            revisionRecord.setCreatedBy(bccpRecord.getCreatedBy());
            revisionRecord.setCreationTimestamp(bccpRecord.getCreationTimestamp());

            ULong revisionId = dslContext.insertInto(REVISION)
                    .set(revisionRecord)
                    .returning(REVISION.REVISION_ID).fetchOne().getRevisionId();

            bccpManifestRecord.setRevisionId(revisionId);
            bccpManifestRecord.update(BCCP_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        bccpManifestRecordList = dslContext.select(BCCP_MANIFEST.fields())
                .from(BCCP_MANIFEST).join(RELEASE).on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        BCCP_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(BccpManifestRecord.class);

        for (BccpManifestRecord nextBccpManifestRecord : bccpManifestRecordList) {
            BccpManifestRecord prevBccpManifestRecord = dslContext.selectFrom(BCCP_MANIFEST)
                    .where(and(
                            BCCP_MANIFEST.BCCP_ID.equal(nextBccpManifestRecord.getBccpId()),
                            BCCP_MANIFEST.RELEASE_ID.notEqual(nextBccpManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevBccpManifestRecord.setNextBccpManifestId(nextBccpManifestRecord.getBccpManifestId());
            prevBccpManifestRecord.update(BCCP_MANIFEST.NEXT_BCCP_MANIFEST_ID);

            nextBccpManifestRecord.setPrevBccpManifestId(prevBccpManifestRecord.getBccpManifestId());
            nextBccpManifestRecord.setRevisionId(prevBccpManifestRecord.getRevisionId());
            nextBccpManifestRecord.update(BCCP_MANIFEST.PREV_BCCP_MANIFEST_ID, BCCP_MANIFEST.REVISION_ID);
        }
    }

    private void initCodeListRevision() {
        // For 'Non-Working' releases.
        List<CodeListManifestRecord> codeListManifestRecordList = dslContext.select(CODE_LIST_MANIFEST.fields())
                .from(CODE_LIST_MANIFEST).join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        CODE_LIST_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(CodeListManifestRecord.class);

        for (CodeListManifestRecord codeListManifestRecord : codeListManifestRecordList) {
            CodeListRecord codeListRecord = dslContext.selectFrom(CODE_LIST)
                    .where(CODE_LIST.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                    .fetchOne();

            List<CodeListValueRecord> codeListValueRecordList = dslContext.selectFrom(CODE_LIST_VALUE)
                    .where(CODE_LIST_VALUE.CODE_LIST_ID.eq(codeListManifestRecord.getCodeListId()))
                    .orderBy(CODE_LIST_VALUE.CODE_LIST_VALUE_ID.asc())
                    .fetch();

            RevisionRecord revisionRecord = new RevisionRecord();
            revisionRecord.setRevisionNum(UInteger.valueOf(1));
            revisionRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            revisionRecord.setRevisionAction(RevisionAction.Added.name());
            revisionRecord.setSnapshot(JSON.valueOf(
                    serializer.serialize(codeListRecord, codeListValueRecordList)
            ));
            revisionRecord.setCreatedBy(codeListRecord.getCreatedBy());
            revisionRecord.setCreationTimestamp(codeListRecord.getCreationTimestamp());

            ULong revisionId = dslContext.insertInto(REVISION)
                    .set(revisionRecord)
                    .returning(REVISION.REVISION_ID).fetchOne().getRevisionId();

            codeListManifestRecord.setRevisionId(revisionId);
            codeListManifestRecord.update(CODE_LIST_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        codeListManifestRecordList = dslContext.select(CODE_LIST_MANIFEST.fields())
                .from(CODE_LIST_MANIFEST).join(RELEASE).on(CODE_LIST_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        CODE_LIST_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(CodeListManifestRecord.class);

        for (CodeListManifestRecord nextCodeListManifestRecord : codeListManifestRecordList) {
            CodeListManifestRecord prevCodeListManifestRecord = dslContext.selectFrom(CODE_LIST_MANIFEST)
                    .where(and(
                            CODE_LIST_MANIFEST.CODE_LIST_ID.equal(nextCodeListManifestRecord.getCodeListId()),
                            CODE_LIST_MANIFEST.RELEASE_ID.notEqual(nextCodeListManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevCodeListManifestRecord.setNextCodeListManifestId(nextCodeListManifestRecord.getCodeListManifestId());
            prevCodeListManifestRecord.update(CODE_LIST_MANIFEST.NEXT_CODE_LIST_MANIFEST_ID);

            nextCodeListManifestRecord.setPrevCodeListManifestId(prevCodeListManifestRecord.getCodeListManifestId());
            nextCodeListManifestRecord.setRevisionId(prevCodeListManifestRecord.getRevisionId());
            nextCodeListManifestRecord.update(CODE_LIST_MANIFEST.PREV_CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.REVISION_ID);
        }
    }

    private void initCodeListValueRevision() {
        // For 'Non-Working' releases.
        List<CodeListValueManifestRecord> codeListValueManifestRecordList = dslContext.select(CODE_LIST_VALUE_MANIFEST.fields())
                .from(CODE_LIST_VALUE_MANIFEST).join(RELEASE).on(CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        CODE_LIST_VALUE_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(CodeListValueManifestRecord.class);

        for (CodeListValueManifestRecord codeListValueManifestRecord : codeListValueManifestRecordList) {
            ULong revisionId = dslContext.select(REVISION.REVISION_ID)
                    .from(REVISION)
                    .join(CODE_LIST_MANIFEST).on(CODE_LIST_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                    .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(codeListValueManifestRecord.getCodeListManifestId()))
                    .fetchOne().get(REVISION.REVISION_ID);

            codeListValueManifestRecord.setRevisionId(revisionId);
            codeListValueManifestRecord.update(CODE_LIST_VALUE_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        codeListValueManifestRecordList = dslContext.select(CODE_LIST_VALUE_MANIFEST.fields())
                .from(CODE_LIST_VALUE_MANIFEST).join(RELEASE).on(CODE_LIST_VALUE_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        CODE_LIST_VALUE_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(CodeListValueManifestRecord.class);

        for (CodeListValueManifestRecord nextCodeListValueManifestRecord : codeListValueManifestRecordList) {
            CodeListValueManifestRecord prevCodeListValueManifestRecord = dslContext.selectFrom(CODE_LIST_VALUE_MANIFEST)
                    .where(and(
                            CODE_LIST_VALUE_MANIFEST.CODE_LIST_VALUE_ID.equal(nextCodeListValueManifestRecord.getCodeListValueId()),
                            CODE_LIST_VALUE_MANIFEST.RELEASE_ID.notEqual(nextCodeListValueManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevCodeListValueManifestRecord.setNextCodeListValueManifestId(nextCodeListValueManifestRecord.getCodeListValueManifestId());
            prevCodeListValueManifestRecord.update(CODE_LIST_VALUE_MANIFEST.NEXT_CODE_LIST_VALUE_MANIFEST_ID);

            nextCodeListValueManifestRecord.setPrevCodeListValueManifestId(prevCodeListValueManifestRecord.getCodeListValueManifestId());
            nextCodeListValueManifestRecord.setRevisionId(prevCodeListValueManifestRecord.getRevisionId());
            nextCodeListValueManifestRecord.update(CODE_LIST_VALUE_MANIFEST.PREV_CODE_LIST_VALUE_MANIFEST_ID, CODE_LIST_VALUE_MANIFEST.REVISION_ID);
        }
    }

    private void initDtRevision() {
        // For 'Non-Working' releases.
        List<DtManifestRecord> dtManifestRecordList = dslContext.select(DT_MANIFEST.fields())
                .from(DT_MANIFEST).join(RELEASE).on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        DT_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(DtManifestRecord.class);

        for (DtManifestRecord dtManifestRecord : dtManifestRecordList) {
            DtRecord dtRecord = dslContext.selectFrom(DT)
                    .where(DT.DT_ID.eq(dtManifestRecord.getDtId()))
                    .fetchOne();

            List<DtScRecord> dtScRecordList = dslContext.selectFrom(DT_SC)
                    .where(DT_SC.OWNER_DT_ID.eq(dtRecord.getDtId()))
                    .orderBy(DT_SC.DT_SC_ID.asc())
                    .fetch();

            RevisionRecord revisionRecord = new RevisionRecord();
            revisionRecord.setRevisionNum(UInteger.valueOf(1));
            revisionRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            revisionRecord.setRevisionAction(RevisionAction.Added.name());
            revisionRecord.setSnapshot(
                    JSON.valueOf(serializer.serialize(dtRecord, dtScRecordList))
            );
            revisionRecord.setCreatedBy(dtRecord.getCreatedBy());
            revisionRecord.setCreationTimestamp(dtRecord.getCreationTimestamp());

            ULong revisionId = dslContext.insertInto(REVISION)
                    .set(revisionRecord)
                    .returning(REVISION.REVISION_ID).fetchOne().getRevisionId();

            dtManifestRecord.setRevisionId(revisionId);
            dtManifestRecord.update(DT_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        dtManifestRecordList = dslContext.select(DT_MANIFEST.fields())
                .from(DT_MANIFEST).join(RELEASE).on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        DT_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(DtManifestRecord.class);

        for (DtManifestRecord nextDtManifestRecord : dtManifestRecordList) {
            DtManifestRecord prevDtManifestRecord = dslContext.selectFrom(DT_MANIFEST)
                    .where(and(
                            DT_MANIFEST.DT_ID.equal(nextDtManifestRecord.getDtId()),
                            DT_MANIFEST.RELEASE_ID.notEqual(nextDtManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevDtManifestRecord.setNextDtManifestId(nextDtManifestRecord.getDtManifestId());
            prevDtManifestRecord.update(DT_MANIFEST.NEXT_DT_MANIFEST_ID);

            nextDtManifestRecord.setPrevDtManifestId(prevDtManifestRecord.getDtManifestId());
            nextDtManifestRecord.setRevisionId(prevDtManifestRecord.getRevisionId());
            nextDtManifestRecord.update(DT_MANIFEST.PREV_DT_MANIFEST_ID, DT_MANIFEST.REVISION_ID);
        }
    }

    private void initDtScRevision() {
        // For 'Non-Working' releases.
        List<DtScManifestRecord> dtScValueManifestRecordList = dslContext.select(DT_SC_MANIFEST.fields())
                .from(DT_SC_MANIFEST).join(RELEASE).on(DT_SC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        DT_SC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(DtScManifestRecord.class);

        for (DtScManifestRecord dtScValueManifestRecord : dtScValueManifestRecordList) {
            ULong revisionId = dslContext.select(REVISION.REVISION_ID)
                    .from(REVISION)
                    .join(DT_MANIFEST).on(DT_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                    .where(DT_MANIFEST.DT_MANIFEST_ID.eq(dtScValueManifestRecord.getOwnerDtManifestId()))
                    .fetchOne().get(REVISION.REVISION_ID);

            dtScValueManifestRecord.setRevisionId(revisionId);
            dtScValueManifestRecord.update(DT_SC_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        dtScValueManifestRecordList = dslContext.select(DT_SC_MANIFEST.fields())
                .from(DT_SC_MANIFEST).join(RELEASE).on(DT_SC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        DT_SC_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(DtScManifestRecord.class);

        for (DtScManifestRecord nextDtScManifestRecord : dtScValueManifestRecordList) {
            DtScManifestRecord prevDtScManifestRecord = dslContext.selectFrom(DT_SC_MANIFEST)
                    .where(and(
                            DT_SC_MANIFEST.DT_SC_ID.equal(nextDtScManifestRecord.getDtScId()),
                            DT_SC_MANIFEST.RELEASE_ID.notEqual(nextDtScManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevDtScManifestRecord.setNextDtScManifestId(nextDtScManifestRecord.getDtScManifestId());
            prevDtScManifestRecord.update(DT_SC_MANIFEST.NEXT_DT_SC_MANIFEST_ID);

            nextDtScManifestRecord.setPrevDtScManifestId(prevDtScManifestRecord.getDtScManifestId());
            nextDtScManifestRecord.setRevisionId(prevDtScManifestRecord.getRevisionId());
            nextDtScManifestRecord.update(DT_SC_MANIFEST.PREV_DT_SC_MANIFEST_ID, DT_SC_MANIFEST.REVISION_ID);
        }
    }

    private void initXbtRevision() {
        // For 'Non-Working' releases.
        List<XbtManifestRecord> xbtManifestRecordList = dslContext.select(XBT_MANIFEST.fields())
                .from(XBT_MANIFEST).join(RELEASE).on(XBT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.notEqual("Working"),
                        XBT_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(XbtManifestRecord.class);

        for (XbtManifestRecord xbtManifestRecord : xbtManifestRecordList) {
            XbtRecord xbtRecord = dslContext.selectFrom(XBT)
                    .where(XBT.XBT_ID.eq(xbtManifestRecord.getXbtId()))
                    .fetchOne();

            RevisionRecord revisionRecord = new RevisionRecord();
            revisionRecord.setRevisionNum(UInteger.valueOf(1));
            revisionRecord.setRevisionTrackingNum(UInteger.valueOf(1));
            revisionRecord.setRevisionAction(RevisionAction.Added.name());
            revisionRecord.setSnapshot(JSON.valueOf(serializer.serialize(xbtRecord)));
            revisionRecord.setCreatedBy(xbtRecord.getCreatedBy());
            revisionRecord.setCreationTimestamp(xbtRecord.getCreationTimestamp());

            ULong revisionId = dslContext.insertInto(REVISION)
                    .set(revisionRecord)
                    .returning(REVISION.REVISION_ID).fetchOne().getRevisionId();

            xbtManifestRecord.setRevisionId(revisionId);
            xbtManifestRecord.update(XBT_MANIFEST.REVISION_ID);
        }

        // For 'Working' release.
        xbtManifestRecordList = dslContext.select(XBT_MANIFEST.fields())
                .from(XBT_MANIFEST).join(RELEASE).on(XBT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        RELEASE.RELEASE_NUM.equal("Working"),
                        XBT_MANIFEST.REVISION_ID.isNull()
                ))
                .fetchInto(XbtManifestRecord.class);

        for (XbtManifestRecord nextXbtManifestRecord : xbtManifestRecordList) {
            XbtManifestRecord prevXbtManifestRecord = dslContext.selectFrom(XBT_MANIFEST)
                    .where(and(
                            XBT_MANIFEST.XBT_ID.equal(nextXbtManifestRecord.getXbtId()),
                            XBT_MANIFEST.RELEASE_ID.notEqual(nextXbtManifestRecord.getReleaseId())
                    ))
                    .fetchOne();

            prevXbtManifestRecord.setNextXbtManifestId(nextXbtManifestRecord.getXbtManifestId());
            prevXbtManifestRecord.update(XBT_MANIFEST.NEXT_XBT_MANIFEST_ID);

            nextXbtManifestRecord.setPrevXbtManifestId(prevXbtManifestRecord.getXbtManifestId());
            nextXbtManifestRecord.setRevisionId(prevXbtManifestRecord.getRevisionId());
            nextXbtManifestRecord.update(XBT_MANIFEST.PREV_XBT_MANIFEST_ID, XBT_MANIFEST.REVISION_ID);
        }
    }
}
