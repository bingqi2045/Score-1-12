package org.oagi.score.repo.component.seqkey;

import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.types.ULong;
import org.oagi.score.data.BCCEntityType;
import org.oagi.score.repo.entity.jooq.enums.SeqKeyType;
import org.oagi.score.repo.entity.jooq.tables.records.AsccRecord;
import org.oagi.score.repo.entity.jooq.tables.records.BccRecord;
import org.oagi.score.repo.entity.jooq.tables.records.SeqKeyRecord;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.entity.jooq.Tables.*;
import static org.oagi.score.repo.component.seqkey.MoveTo.FIRST;
import static org.oagi.score.repo.component.seqkey.MoveTo.LAST;

public class SeqKeyHandler {

    private DSLContext dslContext;

    private Map<ULong, SeqKeyRecord> seqKeyRecordMap;
    private SeqKeyRecord head;
    private SeqKeyRecord tail;
    private SeqKeyRecord current;

    public SeqKeyHandler(DSLContext dslContext, AsccRecord asccRecord) {
        this.dslContext = dslContext;
        init(asccRecord.getFromAccId(), asccRecord.getSeqKeyId(), SeqKeyType.ascc, asccRecord.getAsccId());
    }

    public SeqKeyHandler(DSLContext dslContext, BccRecord bccRecord) {
        this.dslContext = dslContext;
        init(bccRecord.getFromAccId(), bccRecord.getSeqKeyId(), SeqKeyType.bcc, bccRecord.getBccId());
    }

    private void init(ULong fromAccId, ULong seqKeyId, SeqKeyType type, ULong associationId) {
        seqKeyRecordMap = dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_ID.eq(fromAccId))
                .fetch().stream().collect(
                        Collectors.toMap(SeqKeyRecord::getSeqKeyId, Function.identity()));

        for (SeqKeyRecord seqKeyRecord : seqKeyRecordMap.values()) {
            if (seqKeyRecord.getPrevSeqKeyId() == null) {
                this.head = seqKeyRecord;
            }
            if (seqKeyRecord.getNextSeqKeyId() == null) {
                this.tail = seqKeyRecord;
            }
            if (seqKeyRecord.getSeqKeyId().equals(seqKeyId)) {
                this.current = seqKeyRecord;
            }
        }

        if (this.current == null) {
            this.current = new SeqKeyRecord();
            this.current.setFromAccId(fromAccId);
            this.current.setType(type);
            this.current.setCcId(associationId);
            this.current.setSeqKeyId(
                    dslContext.insertInto(SEQ_KEY)
                            .set(this.current)
                            .returning(SEQ_KEY.SEQ_KEY_ID)
                            .fetchOne().getSeqKeyId()
            );

            if (type.equals(SeqKeyType.ascc)) {
                dslContext.update(ASCC)
                        .set(ASCC.SEQ_KEY_ID, this.current.getSeqKeyId())
                        .where(ASCC.ASCC_ID.eq(associationId))
                        .execute();
            } else {
                dslContext.update(BCC)
                        .set(BCC.SEQ_KEY_ID, this.current.getSeqKeyId())
                        .where(BCC.BCC_ID.eq(associationId))
                        .execute();
            }
        }
    }

    public void moveTo(int pos) {
        if (pos < -1) {
            throw new IllegalArgumentException();
        }

        if (pos == 0) {
            this.moveTo(FIRST);
        } else if (pos == -1) {
            this.moveTo(LAST);
        } else {
            SeqKeyRecord target = this.head;
            pos--;
            while (pos > 0) {
                target = this.seqKeyRecordMap.get(target.getNextSeqKeyId());
                pos--;
            }
            moveAfter(target);
        }
    }

    public void moveTo(MoveTo to) {
        switch (to) {
            case FIRST:
                if (this.head != null) {
                    brokeLinks(this.current);

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
                    brokeLinks(this.current);

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

    private void brokeLinks(SeqKeyRecord record) {
        if (record.getPrevSeqKeyId() != null) {
            SeqKeyRecord currentPrevSeqKeyRecord = getSeqKeyRecordById(record.getPrevSeqKeyId());

            currentPrevSeqKeyRecord.setNextSeqKeyId(record.getNextSeqKeyId());
            UpdateSetFirstStep<SeqKeyRecord> step = dslContext.update(SEQ_KEY);
            if (currentPrevSeqKeyRecord.getNextSeqKeyId() == null) {
                step.setNull(SEQ_KEY.NEXT_SEQ_KEY_ID)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(currentPrevSeqKeyRecord.getSeqKeyId()))
                        .execute();
            } else {
                step.set(SEQ_KEY.NEXT_SEQ_KEY_ID, currentPrevSeqKeyRecord.getNextSeqKeyId())
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(currentPrevSeqKeyRecord.getSeqKeyId()))
                        .execute();
            }
        }

        if (record.getNextSeqKeyId() != null) {
            SeqKeyRecord currentNextSeqKeyRecord = getSeqKeyRecordById(record.getNextSeqKeyId());

            currentNextSeqKeyRecord.setPrevSeqKeyId(record.getPrevSeqKeyId());
            UpdateSetFirstStep<SeqKeyRecord> step = dslContext.update(SEQ_KEY);
            if (currentNextSeqKeyRecord.getPrevSeqKeyId() == null) {
                step.setNull(SEQ_KEY.PREV_SEQ_KEY_ID)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(currentNextSeqKeyRecord.getSeqKeyId()))
                        .execute();
            } else {
                step.set(SEQ_KEY.PREV_SEQ_KEY_ID, currentNextSeqKeyRecord.getPrevSeqKeyId())
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(currentNextSeqKeyRecord.getSeqKeyId()))
                        .execute();
            }
        }
    }

    public void moveAfter(SeqKeyRecord after) {
        if (after == null) {
            return;
        }

        if (after.getNextSeqKeyId() != null && after.getNextSeqKeyId().equals(this.current.getSeqKeyId())) {
            return;
        }

        brokeLinks(this.current);

        // DO NOT change orders of executions.

        current.setPrevSeqKeyId(after.getSeqKeyId());
        current.setNextSeqKeyId(after.getNextSeqKeyId());

        UpdateSetMoreStep<SeqKeyRecord> step = dslContext.update(SEQ_KEY)
                .set(SEQ_KEY.PREV_SEQ_KEY_ID, current.getPrevSeqKeyId());
        if (current.getNextSeqKeyId() == null) {
            step = step.setNull(SEQ_KEY.NEXT_SEQ_KEY_ID);
        } else {
            step = step.set(SEQ_KEY.NEXT_SEQ_KEY_ID, current.getNextSeqKeyId());
        }
        step.where(SEQ_KEY.SEQ_KEY_ID.eq(current.getSeqKeyId()))
                .execute();

        SeqKeyRecord afterNextSeqKeyRecord = getSeqKeyRecordById(after.getNextSeqKeyId());
        if (afterNextSeqKeyRecord != null) {
            afterNextSeqKeyRecord.setPrevSeqKeyId(this.current.getSeqKeyId());
            afterNextSeqKeyRecord.update(SEQ_KEY.PREV_SEQ_KEY_ID);
        }

        after.setNextSeqKeyId(this.current.getSeqKeyId());
        dslContext.update(SEQ_KEY)
                .set(SEQ_KEY.NEXT_SEQ_KEY_ID, after.getNextSeqKeyId())
                .where(SEQ_KEY.SEQ_KEY_ID.eq(after.getSeqKeyId()))
                .execute();
    }

    private SeqKeyRecord getSeqKeyRecordById(ULong seqKeyId) {
        return dslContext.selectFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKeyId))
                .fetchOne();
    }

    public static List<SeqKeySupportable> sort(List<SeqKeySupportable> seqKeyList) {
        if (seqKeyList == null || seqKeyList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<BigInteger, SeqKeySupportable> seqKeyMap = seqKeyList.stream()
                .collect(Collectors.toMap(
                        SeqKeySupportable::getSeqKeyId, Function.identity()));

        /*
         * To ensure that every CC has different next seq_key id.
         * If not, it would cause a circular reference.
         */
        if (seqKeyMap.size() != seqKeyMap.values().stream()
                .map(e -> e.getNextSeqKeyId())
                .collect(Collectors.toSet()).size()) {
            throw new IllegalStateException();
        }

        SeqKeySupportable head = seqKeyMap.values().stream()
                .filter(e -> e.getPrevSeqKeyId() == null)
                .findFirst().orElse(null);

        List<SeqKeySupportable> sorted = new ArrayList();
        SeqKeySupportable current = head;
        while (current != null) {
            if (!current.getState().equals(CcState.Deleted.name())) {
                sorted.add(current);
            }
            current = seqKeyMap.get(current.getNextSeqKeyId());
        }
        return sorted;
    }

}
