package org.oagi.srt.repo.component.seqkey;

import org.jooq.types.ULong;

public interface SeqKeySupportable {

    ULong getSeqKeyId();
    ULong getPrevSeqKeyId();
    ULong getNextSeqKeyId();

}
