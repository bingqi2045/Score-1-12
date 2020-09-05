package org.oagi.score.service.corecomponent.seqkey;

import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.corecomponent.BccEntityType;
import org.oagi.score.repo.api.corecomponent.seqkey.model.*;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.score.service.corecomponent.seqkey.MoveTo.FIRST;
import static org.oagi.score.service.corecomponent.seqkey.MoveTo.LAST;

public class SeqKeyHandler {

    private ScoreRepositoryFactory scoreRepositoryFactory;
    private ScoreUser requester;

    private SeqKey head;
    private SeqKey tail;
    private SeqKey current;

    public SeqKeyHandler(ScoreRepositoryFactory scoreRepositoryFactory, ScoreUser requester) {
        this.scoreRepositoryFactory = scoreRepositoryFactory;
        this.requester = requester;
    }

    public void initAscc(BigInteger fromAccId, BigInteger seqKeyId, BigInteger associationId) {
        init(fromAccId, seqKeyId, SeqKeyType.ASCC, associationId);
    }

    public void initBcc(BigInteger fromAccId, BigInteger seqKeyId, BigInteger associationId) {
        init(fromAccId, seqKeyId, SeqKeyType.BCC, associationId);
    }

    private void init(BigInteger fromAccId, BigInteger seqKeyId, SeqKeyType type, BigInteger associationId) {
        for (SeqKey seqKey : scoreRepositoryFactory.createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(this.requester)
                        .withFromAccId(fromAccId)).getSeqKey()) {
            if (seqKey.getPrevSeqKey() == null) {
                this.head = seqKey;
            }
            if (seqKey.getNextSeqKey() == null) {
                this.tail = seqKey;
            }
            if (seqKey.getSeqKeyId().equals(seqKeyId)) {
                this.current = seqKey;
            }
        }

        if (this.current == null) {
            this.current = scoreRepositoryFactory.createSeqKeyWriteRepository()
                    .createSeqKey(new CreateSeqKeyRequest(this.requester)
                            .withFromAccId(fromAccId)
                            .withType(type)
                            .withCcId(associationId))
                    .getSeqKey();
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
            SeqKey target = this.head;
            pos--;
            while (pos > 0) {
                target = target.getNextSeqKey();
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

                    this.current.setPrevSeqKey(null);
                    this.current.setNextSeqKey(this.head);
                    update(this.current);

                    this.head.setPrevSeqKey(this.current);
                    update(this.head);

                    this.head = this.current;
                }

                break;

            case LAST_OF_ATTR:
                SeqKey target = this.head;

                while (target != null &&
                        target.getSeqKeyType() == SeqKeyType.BCC &&
                        target.getEntityType() == BccEntityType.Attribute) {
                    target = target.getNextSeqKey();
                }

                moveAfter((target != null) ? target.getPrevSeqKey() : this.head);

                break;

            case LAST:
                if (this.tail != null) {
                    brokeLinks(this.current);

                    this.current.setPrevSeqKey(this.tail);
                    this.current.setNextSeqKey(null);
                    update(this.current);

                    this.tail.setNextSeqKey(this.current);
                    update(this.tail);

                    this.tail = this.current;
                }
                break;
        }
    }

    public void deleteCurrent() {
        brokeLinks(this.current);

        scoreRepositoryFactory.createSeqKeyWriteRepository()
                .deleteSeqKey(new DeleteSeqKeyRequest(this.requester)
                        .withSeqKeyId(this.current.getSeqKeyId()));

        this.current = null;
    }

    private void brokeLinks(SeqKey record) {
        SeqKey currentPrevSeqKey = record.getPrevSeqKey();
        SeqKey currentNextSeqKey = record.getNextSeqKey();

        if (currentPrevSeqKey != null) {
            currentPrevSeqKey.setNextSeqKey(record.getNextSeqKey());
            update(currentPrevSeqKey);
        }

        if (currentNextSeqKey != null) {
            currentNextSeqKey.setPrevSeqKey(record.getPrevSeqKey());
            update(currentNextSeqKey);
        }
    }

    public void moveAfter(SeqKey after) {
        if (after == null) {
            return;
        }

        if (after.getNextSeqKey() != null && after.getNextSeqKey().equals(this.current.getSeqKeyId())) {
            return;
        }

        brokeLinks(this.current);

        // DO NOT change orders of executions.

        current.setPrevSeqKey(after);
        current.setNextSeqKey(after.getNextSeqKey());
        update(current);

        SeqKey afterNextSeqKey = after.getNextSeqKey();
        if (afterNextSeqKey != null) {
            afterNextSeqKey.setPrevSeqKey(this.current);
            update(afterNextSeqKey);
        }

        after.setNextSeqKey(this.current);
        update(after);
    }

    private void update(SeqKey seqKey) {
        scoreRepositoryFactory.createSeqKeyWriteRepository()
                .updateSeqKey(new UpdateSeqKeyRequest(this.requester)
                        .withSeqKey(seqKey));
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
            sorted.add(current);
            current = seqKeyMap.get(current.getNextSeqKeyId());
        }
        return sorted;
    }

}
