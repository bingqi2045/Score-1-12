package org.oagi.srt.repo.component.seqkey;

import java.math.BigInteger;

public interface SeqKeySupportable {

    String getState();
    BigInteger getSeqKeyId();
    BigInteger getPrevSeqKeyId();
    BigInteger getNextSeqKeyId();

}
