package org.oagi.srt.gateway.http.api.cc_management.service;

import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAccNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAsccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcNode;
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
        return repository.getAccNode(accId, releaseId);
    }

    public CcAsccpNode getAsccpNode(User user, long asccpId, Long releaseId) {
        return repository.getAsccpNode(asccpId, releaseId);
    }

    public CcBccpNode getBccpNode(User user, long bccpId, Long releaseId) {
        return repository.getBccpNode(bccpId, releaseId);
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

}
