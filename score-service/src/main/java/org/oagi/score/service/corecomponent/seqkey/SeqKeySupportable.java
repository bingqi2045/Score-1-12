package org.oagi.score.service.corecomponent.seqkey;

public interface SeqKeySupportable {

    String getState();
    String getSeqKeyId();
    String getPrevSeqKeyId();
    String getNextSeqKeyId();

}
