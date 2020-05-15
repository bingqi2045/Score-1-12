package org.oagi.srt.repo.component.seqkey;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.entity.jooq.enums.SeqKeyType;
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;
import org.oagi.srt.entity.jooq.tables.records.BccRecord;
import org.oagi.srt.entity.jooq.tables.records.SeqKeyRecord;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

public class SeqKeyHandler {

    private DSLContext dslContext;

    private Map<ULong, SeqKeyRecord> seqKeyRecordMap;
    private SeqKeyRecord head;
    private SeqKeyRecord tail;
    private SeqKeyRecord current;

    public SeqKeyHandler(DSLContext dslContext, AsccRecord asccRecord) {
        this.dslContext = dslContext;
        init(asccRecord);
    }

    public SeqKeyHandler(DSLContext dslContext, BccRecord bccRecord) {
        this.dslContext = dslContext;
        init(bccRecord);
    }

    private void init(AsccRecord asccRecord) {
        seqKeyRecordMap = dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_ID.eq(asccRecord.getFromAccId()))
                .fetch().stream().collect(
                        Collectors.toMap(SeqKeyRecord::getSeqKeyId, Function.identity()));

        for (SeqKeyRecord seqKeyRecord : seqKeyRecordMap.values()) {
            if (seqKeyRecord.getPrevSeqKeyId() == null) {
                this.head = seqKeyRecord;
            }
            if (seqKeyRecord.getNextSeqKeyId() == null) {
                this.tail = seqKeyRecord;
            }
            if (seqKeyRecord.getSeqKeyId() == asccRecord.getSeqKeyId()) {
                this.current = seqKeyRecord;
            }
        }

        if (this.current == null) {
            this.current = new SeqKeyRecord();
            this.current.setFromAccId(asccRecord.getFromAccId());
            this.current.setType(SeqKeyType.ascc);
            this.current.setCcId(asccRecord.getAsccId());
            this.current.setSeqKeyId(
                    dslContext.insertInto(SEQ_KEY)
                            .set(this.current)
                            .returning(SEQ_KEY.SEQ_KEY_ID)
                            .fetchOne().getSeqKeyId()
            );

            asccRecord.setSeqKeyId(this.current.getSeqKeyId());
            dslContext.update(ASCC)
                    .set(ASCC.SEQ_KEY_ID, asccRecord.getSeqKeyId())
                    .where(ASCC.ASCC_ID.eq(asccRecord.getAsccId()))
                    .execute();
        }
    }

    private void init(BccRecord bccRecord) {
        seqKeyRecordMap = dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_ID.eq(bccRecord.getFromAccId()))
                .fetch().stream().collect(
                        Collectors.toMap(SeqKeyRecord::getSeqKeyId, Function.identity()));

        for (SeqKeyRecord seqKeyRecord : seqKeyRecordMap.values()) {
            if (seqKeyRecord.getPrevSeqKeyId() == null) {
                this.head = seqKeyRecord;
            }
            if (seqKeyRecord.getNextSeqKeyId() == null) {
                this.tail = seqKeyRecord;
            }
            if (seqKeyRecord.getSeqKeyId() == bccRecord.getSeqKeyId()) {
                this.current = seqKeyRecord;
            }
        }

        if (this.current == null) {
            this.current = new SeqKeyRecord();
            this.current.setFromAccId(bccRecord.getFromAccId());
            this.current.setType(SeqKeyType.bcc);
            this.current.setCcId(bccRecord.getBccId());
            this.current.setSeqKeyId(
                    dslContext.insertInto(SEQ_KEY)
                            .set(this.current)
                            .returning(SEQ_KEY.SEQ_KEY_ID)
                            .fetchOne().getSeqKeyId()
            );

            bccRecord.setSeqKeyId(this.current.getSeqKeyId());
            dslContext.update(BCC)
                    .set(BCC.SEQ_KEY_ID, bccRecord.getSeqKeyId())
                    .where(BCC.BCC_ID.eq(bccRecord.getBccId()))
                    .execute();
        }
    }

    public void moveTo(MoveTo to) {
        switch (to) {
            case FIRST:
                if (this.head != null) {
                    this.current.setPrevSeqKeyId(null);
                    this.current.setNextSeqKeyId(this.head.getSeqKeyId());
                    dslContext.update(SEQ_KEY)
                            .setNull(SEQ_KEY.PREV_SEQ_KEY_ID)
                            .set(SEQ_KEY.NEXT_SEQ_KEY_ID, this.head.getSeqKeyId())
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(this.current.getSeqKeyId()))
                            .execute();

                    this.head.setPrevSeqKeyId(this.current.getSeqKeyId());
                    dslContext.update(SEQ_KEY)
                            .set(SEQ_KEY.PREV_SEQ_KEY_ID, this.current.getSeqKeyId())
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(this.head.getSeqKeyId()))
                            .execute();
                }
                break;

            case LAST_OF_ATTR:
                List<ULong> attrSeqKeys =
                        dslContext.select(BCC.SEQ_KEY_ID)
                                .from(BCC)
                                .where(and(
                                        BCC.ENTITY_TYPE.eq(BCCEntityType.Attribute.getValue()),
                                        BCC.SEQ_KEY_ID.in(
                                                this.seqKeyRecordMap.values().stream()
                                                        .filter(e -> e.getType() == SeqKeyType.bcc)
                                                        .map(e -> e.getSeqKeyId())
                                                        .collect(Collectors.toList()))
                                ))
                                .fetchInto(ULong.class);

                SeqKeyRecord lastAttrSeqKeyRecord = null;
                for (ULong attrSeqKey : attrSeqKeys) {
                    SeqKeyRecord seqKeyRecord = seqKeyRecordMap.get(attrSeqKey);
                    if (seqKeyRecord.getNextSeqKeyId() == null ||
                            !attrSeqKeys.contains(seqKeyRecord.getNextSeqKeyId())) {
                        lastAttrSeqKeyRecord = seqKeyRecord;
                        break;
                    }
                }

                moveAfter(lastAttrSeqKeyRecord);

                break;

            case LAST:
                if (this.tail != null) {
                    this.current.setPrevSeqKeyId(this.tail.getSeqKeyId());
                    this.current.setNextSeqKeyId(null);
                    dslContext.update(SEQ_KEY)
                            .set(SEQ_KEY.PREV_SEQ_KEY_ID, this.tail.getSeqKeyId())
                            .setNull(SEQ_KEY.NEXT_SEQ_KEY_ID)
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(this.current.getSeqKeyId()))
                            .execute();

                    this.tail.setNextSeqKeyId(this.current.getSeqKeyId());
                    dslContext.update(SEQ_KEY)
                            .set(SEQ_KEY.NEXT_SEQ_KEY_ID, this.current.getSeqKeyId())
                            .where(SEQ_KEY.SEQ_KEY_ID.eq(this.tail.getSeqKeyId()))
                            .execute();
                }
                break;
        }
    }

    public void moveAfter(SeqKeyRecord after) {
        if (after == null) {
            return;
        }

        current.setPrevSeqKeyId(after.getSeqKeyId());
        current.setNextSeqKeyId(after.getNextSeqKeyId());
        dslContext.update(SEQ_KEY)
                .set(SEQ_KEY.PREV_SEQ_KEY_ID, current.getPrevSeqKeyId())
                .set(SEQ_KEY.NEXT_SEQ_KEY_ID, current.getNextSeqKeyId())
                .where(SEQ_KEY.SEQ_KEY_ID.eq(current.getSeqKeyId()))
                .execute();

        SeqKeyRecord afterNext = seqKeyRecordMap.get(after.getNextSeqKeyId());
        if (afterNext != null) {
            afterNext.setPrevSeqKeyId(current.getSeqKeyId());
            dslContext.update(SEQ_KEY)
                    .set(SEQ_KEY.PREV_SEQ_KEY_ID, afterNext.getPrevSeqKeyId())
                    .where(SEQ_KEY.SEQ_KEY_ID.eq(afterNext.getSeqKeyId()))
                    .execute();

            after.setNextSeqKeyId(current.getSeqKeyId());
            dslContext.update(SEQ_KEY)
                    .set(SEQ_KEY.NEXT_SEQ_KEY_ID, after.getNextSeqKeyId())
                    .where(SEQ_KEY.SEQ_KEY_ID.eq(after.getSeqKeyId()))
                    .execute();
        }
    }

    public static List<SeqKeySupportable> sort(List<SeqKeySupportable> seqKeyList) {
        if (seqKeyList == null || seqKeyList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<BigInteger, SeqKeySupportable> seqKeyMap = seqKeyList.stream()
                .collect(Collectors.toMap(
                        SeqKeySupportable::getSeqKeyId, Function.identity()));

        SeqKeySupportable head = seqKeyMap.values().stream()
                .filter(e -> e.getPrevSeqKeyId() == null)
                .findFirst().orElse(null);

        List<SeqKeySupportable> sorted = new ArrayList();
        SeqKeySupportable current = head;
        while (current != null) {
            sorted.add(current);
            current = seqKeyMap.get(current.getNextSeqKeyId());
        }
        return sorted;
    }

}
