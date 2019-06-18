package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.Record1;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    public CcAccNode getAccNode(User user, long accId, Long releaseId) {
        return repository.getAccNodeByAccId(accId, releaseId);
    }

    public CcAsccpNode getAsccpNode(User user, long asccpId, Long releaseId) {
        return repository.getAsccpNodeByAsccpId(asccpId, releaseId);
    }

    public CcBccpNode getBccpNode(User user, long bccpId, Long releaseId) {
        return repository.getBccpNodeByBccpId(bccpId, releaseId);
    }

    public List<? extends CcNode> getDescendants(User user, CcAccNode accNode) {
        return repository.getDescendants(user, accNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        return repository.getDescendants(user, asccpNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        return repository.getDescendants(user, bccpNode);
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        return repository.getAccNodeDetail(user, accNode);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        return repository.getAsccpNodeDetail(user, asccpNode);
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        return repository.getBccpNodeDetail(user, bccpNode);
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        return repository.getBdtScNodeDetail(user, bdtScNode);
    }

    @Transactional
    public long createAcc(User user, CcAccNode ccAccNode) {
        return repository.createAcc(user, ccAccNode);
    }

    @Transactional
    public Record1<ULong> getLastAcc() {
        return repository.getLastAccId();
    }

    @Transactional
    public void updateAcc(User user, CcAccNode ccAccNode) {
        repository.updateAcc(user, ccAccNode);
    }

}

